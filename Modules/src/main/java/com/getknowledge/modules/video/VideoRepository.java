package com.getknowledge.modules.video;

import com.coremedia.iso.IsoFile;
import com.getknowledge.modules.courses.Course;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.video.comment.VideoComment;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.exceptions.DeleteException;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.exceptions.SystemError;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import com.googlecode.mp4parser.FileDataSourceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@Repository("VideoRepository")
public class VideoRepository extends BaseRepository<Video> {

    @Autowired
    private TraceService trace;

    @Override
    protected Class<Video> getClassEntity() {
        return Video.class;
    }

    @Value("${video.path}")
    public String pathToVideo;

    @Override
    public void remove(Video video) {
        String separator = File.separator;
        File file = new File(pathToVideo + File.separator + video.getLink());
        if (file.exists()) {
            boolean result = file.delete();
        }

        super.remove(video);

        //Удаляем кооментарии
        entityManager.createQuery("delete from  VideoComment vc where vc.video.id = :id")
                .setParameter("id",video.getId())
                .executeUpdate();
    }

    public Course findCourseByVideo(Video video) {
        List<Course> courseList = entityManager.createQuery(
                "select c from Course c where exists(select t from Tutorial t where t.video.id = :videoId and t.course.id = c.id)")
                .setParameter("videoId",video.getId())
                .getResultList();
        return courseList.isEmpty() ? null : courseList.get(0);
    }

    @Override
    public void create(Video object) {
        if (object.getLink() == null) {
            object.setLink(UUID.randomUUID().toString());
        }
        if (object.getUploadTime() == null) {
            object.setUploadTime(Calendar.getInstance());
        }
        super.create(object);
    }

    @Override
    public void update(Video object){
        Video video = read(object.getId());
        //Не разрешаем редактировать link, если по данному пути уже записано видео
        if (video.getLink() != object.getLink()) {
            if (new File(getVideoPath(video.getId())).exists()) {
                throw new RuntimeException("Error update video file link, because link reference to video");
            }
        }
        super.update(object);
    }

    public Video create(String name, String link, byte [] cover, boolean allowEveryOne) {
        Video video = new Video();
        video.setLink(link);
        video.setVideoName(name);
        video.setAllowEveryOne(allowEveryOne);
        video.setUploadTime(Calendar.getInstance());
        video.setCover(cover);
        create(video);
        return video;
    }

    public Video create(String name,String link) {
        return create(name,link,null,true);
    }


    public Video create(String name, byte[] cover) {
        return create(name,null,cover,true);
    }

    public Video update(String name, byte[] cover) {
        Video video = new Video();
        if (name != null) {
            video.setVideoName(name);
        }
        video.setCover(cover);
        merge(video);
        return video;
    }

    //Загрузка видео в каталог
    // {paramPath}/{userId}/{videoId}_{fileName}
    public Result uploadVideo(Video video, UserInfo userInfo, MultipartFile multipartFile) {

        String separator = File.separator;

        File file = new File(pathToVideo + separator + userInfo.getId());

        if (!file.exists()) {
            trace.log("Create dir for author videos : " + file.getAbsolutePath(), TraceLevel.Event,true);
            if (file.mkdir()) {
                trace.log("Can't create path for user : " + file.getAbsolutePath(), TraceLevel.Error, true);
                return Result.Failed("Не возможно создать папку для пользователя : " + file.getAbsolutePath());
            }
        }

        //Если мы перезаливаем видео необходимо удалить старое
        if (video.getLink() != null) {
            try {
                if (Files.exists(Paths.get(getVideoPath(video.getId())))) {
                    Files.delete(Paths.get(getVideoPath(video.getId())));
                }
            } catch (IOException e) {
                trace.logException("Can't remove video file : " + video.getLink(), e, TraceLevel.Error, true);
                return Result.Failed("Не возможно удалить файл: " + video.getLink());
            }
        }

        String link =  userInfo.getId() + separator +
                       video.getId() + "_" +
                       multipartFile.getOriginalFilename();

        File videoFile = new File( pathToVideo + separator + link);

        //Если такого файла нету созадем
        if (!videoFile.exists()) {
            try {
                if (!videoFile.createNewFile()) {
                    return Result.Failed("Не возможно создать файл");
                }
            } catch (IOException e) {
                trace.logException("Error upload video file" , e,TraceLevel.Error,true);
                return Result.Failed("Не возможно создать файл");
            }
        }

        //Записываем данные из request
        try (FileOutputStream fos = new FileOutputStream(videoFile)) {
            fos.write(multipartFile.getBytes());
        } catch (IOException e) {
            trace.logException("Error upload video file" , e,TraceLevel.Error,true);
        }

        video.setLink(link);
        video.setUploadTime(Calendar.getInstance());
        video.setSize(multipartFile.getSize());

        IsoFile  isoFile = null;
        try(FileDataSourceImpl dataSource = new FileDataSourceImpl(getVideoPath(video.getId())))  {

            isoFile = new IsoFile(dataSource);

            if (isoFile.getMovieBox() != null && isoFile.getMovieBox().getMovieHeaderBox() != null) {
                video.setDuration(isoFile.getMovieBox().getMovieHeaderBox().getDuration());
            }

        } catch (Exception e) {
            trace.logException("Error calculate video duration" , e,TraceLevel.Error,true);
        } finally {
            if (isoFile != null) {
                try {
                    isoFile.close();
                }  catch (IOException e) {
                    trace.logException("Error close ISO File" , e,TraceLevel.Error,true);
                }
            }
        }

        merge(video);
        trace.log("Video file successfully upload + " + videoFile.getAbsolutePath() , TraceLevel.Event,true);
        return Result.Complete();
    }

    public String getVideoPath(Long id){
        Video video = read(id);
        return video == null ? null : pathToVideo + File.separator + video.getLink();
    }
}

package com.getknowledge.modules.video;

import com.getknowledge.modules.courses.Course;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.video.comment.VideoComment;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.exceptions.DeleteException;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

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
    }

    public Course findCourseByVideo(Video video) {
        List<Course> courseList = entityManager.createQuery(
                "select c from Course c where exists(select t from Tutorial t where t.video.id = :videoId and t.course.id = c.id)")
                .setParameter("videoId",video.getId())
                .getResultList();
        return courseList.isEmpty() ? null : courseList.get(0);
    }

    public Video create(String name,String link,byte [] cover,boolean allowEveryOne) {
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
        video.setVideoName(name);
        video.setCover(cover);
        merge(video);
        return video;
    }

    //Загрузка видео в каталог
    // {paramPath}/{userId}/{videoId}_{fileName}
    public void uploadVideo(Video video,UserInfo userInfo,MultipartFile multipartFile) {

        String separator = File.separator;

        File file = new File(pathToVideo + separator + userInfo.getId());

        if (!file.exists()) {
            trace.log("Create dir for author videos : " + file.getAbsolutePath(), TraceLevel.Event,true);
            file.mkdir();
        }

        //Если мы перезаливаем видео необходимо удалить старое
        if (video.getLink() != null) {
            File oldVideo = new File(pathToVideo + separator + video.getLink());
            oldVideo.delete();
        }

        String link =  userInfo.getId() + separator +
                       video.getId() + "_" +
                       multipartFile.getOriginalFilename();

        File videoFile = new File( pathToVideo + separator + link);

        //Если такого файла нету созадем
        if (!videoFile.exists()) {
            try {
                videoFile.createNewFile();
            } catch (IOException e) {
                trace.logException("Error upload video file" , e,TraceLevel.Error,true);
            }
        }

        //Записываем данные из request
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(videoFile);
            fos.write(multipartFile.getBytes());
        } catch (IOException e) {
            trace.logException("Error upload video file" , e,TraceLevel.Error,true);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    trace.logException("Error upload video file" , e,TraceLevel.Error,true);
                }
            }
        }

        video.setLink(link);
        video.setUploadTime(Calendar.getInstance());
        merge(video);
        trace.log("Video file successfully upload + " + videoFile.getAbsolutePath() , TraceLevel.Event,true);
    }

    public String getVideoPath(Long id){
        Video video = read(id);
        return video == null ? null : pathToVideo + File.separator + video.getLink();
    }
}

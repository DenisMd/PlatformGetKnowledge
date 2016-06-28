package com.getknowledge.modules.books;

import com.getknowledge.modules.books.group.GroupBooks;
import com.getknowledge.modules.books.group.GroupBooksRepository;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.dictionaries.language.LanguageRepository;
import com.getknowledge.modules.dictionaries.language.names.Languages;
import com.getknowledge.modules.attachements.FileAttachment;
import com.getknowledge.modules.attachements.FileAttachmentRepository;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoService;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.serializers.FileResponse;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.FileService;
import com.getknowledge.platform.base.services.ImageService;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.List;

@Service("BookService")
public class BookService extends AbstractService implements ImageService,FileService {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private GroupBooksRepository groupBooksRepository;

    @Autowired
    private TraceService trace;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private FileAttachmentRepository fileAttachmentRepository;

    private Result checkBookRight(HashMap<String,Object> data) {
        Long bookId = longFromField("bookId",data);
        Book book = bookRepository.read(bookId);
        if (book == null) {
            return Result.NotFound();
        }

        UserInfo userInfo = userInfoService.getAuthorizedUser(data);

        if (!book.getAuthorizationList().isAccessEdit(userInfo.getUser())) {
            return Result.AccessDenied();
        }

        Result result = Result.Complete();
        result.setObject(book);
        return result;
    }


    @Action(name = "createBook" , mandatoryFields = {"name","groupBookUrl","authorName","description","language"})
    @Transactional
    public Result createBook(HashMap<String,Object> data) {
        if (!data.containsKey("principalName"))
            return Result.NotAuthorized();

        UserInfo userInfo = userInfoService.getAuthorizedUser(data);

        Book book = new Book();
        if (!book.getAuthorizationList().isAccessCreate(userInfo.getUser())) {
            return Result.AccessDenied();
        }

        GroupBooks groupBooks =  groupBooksRepository.getSingleEntityByFieldAndValue("url", data.get("groupBookUrl"));
        if (groupBooks == null) {
            trace.log("Group book id is incorrect" , TraceLevel.Warning,true);
            return Result.Failed();
        }

        String name = (String) data.get("name");
        String authorName = (String) data.get("authorName");
        String description = (String) data.get("description");
        Language language = null;
        List<String> links = null;
        List<String> tags = null;

        if (data.containsKey("links")){
            links = (List<String>) data.get("links");
        }

        if (data.containsKey("tags")){
            tags = (List<String>) data.get("tags");
        }


        language = languageRepository.getLanguage(Languages.valueOf((String) data.get("language")));
        if (language == null) {
            Result failed = Result.Failed("language_not_found");
            return failed;
        }

        byte [] cover = null;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("com.getknowledge.modules/image/nocover.jpg")) {
            cover = org.apache.commons.io.IOUtils.toByteArray(is);
        } catch (IOException e) {
            trace.logException("Error load file: " + e.getMessage(), e, TraceLevel.Error,true);
        }

        book = bookRepository.createBook(groupBooks,userInfo,name,authorName,description,language,links,tags,cover);

        Result result = Result.Complete();
        result.setObject(book.getId());
        return result;
    }

    @Action(name = "updateBookInformation" , mandatoryFields = {"bookId"})
    @Transactional
    public Result updateBookInformation(HashMap<String,Object> data) {

        Result result = checkBookRight(data);
        Book book;
        if (result.getObject() != null)  {
            book = (Book) result.getObject();
        } else {
            return result;
        }

        String name = null;
        if (data.containsKey("name")) {
           name =(String) data.get("name");
        }

        String description = null;
        Language language = null;
        if (data.containsKey("description")) {
            description = (String) data.get("description");
        }
        if (data.containsKey("language")) {
            language = languageRepository.getLanguage(Languages.valueOf((String) data.get("language")));
        }

        String authorName = null;
        if (data.containsKey("authorName")) {
            authorName = (String) data.get("authorName");
        }
        List<String> links = null;
        List<String> tags = null;

        if (data.containsKey("links")){
            links = (List<String>) data.get("links");
        }

        if (data.containsKey("tags")){
            tags = (List<String>) data.get("tags");
        }

        bookRepository.updateBook(book, name,language, authorName, description, links, tags);
        return Result.Complete();
    }

    @ActionWithFile(name = "uploadCover" , mandatoryFields = {"bookId"})
    @Transactional
    public Result uploadCover(HashMap<String,Object> data, List<MultipartFile> files) {
        Result result = checkBookRight(data);
        Book book;
        if (result.getObject() != null)  {
            book = (Book) result.getObject();
        } else {
            return result;
        }

        try {
            book.setCover(files.get(0).getBytes());
        } catch (IOException e) {
            trace.logException("Error read cover for book" , e, TraceLevel.Warning,true);
            return Result.Failed();
        }

        bookRepository.merge(book);
        return Result.Complete();
    }

    @ActionWithFile(name = "uploadData" , mandatoryFields = {"bookId"}, maxSize = 50_104)
    @Transactional
    public Result uploadData(HashMap<String,Object> data, List<MultipartFile> files) {
        Result result = checkBookRight(data);
        Book book;
        if (result.getObject() != null)  {
            book = (Book) result.getObject();
        } else {
            return result;
        }

        try {
            FileAttachment fileAttachment = new FileAttachment();
            fileAttachment.setFileName(files.get(0).getOriginalFilename());
            fileAttachment.setData(files.get(0).getBytes());
            fileAttachmentRepository.create(fileAttachment);
            book.setFileAttachment(fileAttachment);
            book.setFileName(files.get(0).getOriginalFilename());
        } catch (IOException e) {
            trace.logException("Error read data for book" , e, TraceLevel.Warning,true);
            return Result.Failed();
        }

        bookRepository.merge(book);
        return Result.Complete();
    }

    @Override
    @Transactional
    public byte[] getImageById(long id) {
        Book book = bookRepository.read(id);
        return book == null ? null : book.getCover();
    }

    @Override
    @Transactional
    public FileResponse getFile(long id, Object key) {
        FileResponse fileResponse = new FileResponse();
        Book book = bookRepository.read(id);
        if (book != null && book.getFileAttachment() != null)  {
            fileResponse.setData(book.getFileAttachment().getData());
            fileResponse.setFileName(book.getFileName());
        }
        return fileResponse;
    }
}

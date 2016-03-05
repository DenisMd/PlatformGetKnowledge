package com.getknowledge.modules.books;

import com.getknowledge.modules.books.group.GroupBooks;
import com.getknowledge.modules.books.group.GroupBooksRepository;
import com.getknowledge.modules.books.tags.BooksTag;
import com.getknowledge.modules.books.tags.BooksTagRepository;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.dictionaries.language.LanguageRepository;
import com.getknowledge.modules.dictionaries.language.names.Languages;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoService;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.serializers.FileResponse;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.FileService;
import com.getknowledge.platform.base.services.ImageService;
import com.getknowledge.platform.exceptions.NotAuthorized;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import org.apache.commons.fileupload.FileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.awt.print.Book;
import java.io.*;
import java.util.HashMap;
import java.util.List;

@Service("BooksService")
public class BooksService extends AbstractService implements ImageService,FileService {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private BooksTagRepository booksTagRepository;

    @Autowired
    private GroupBooksRepository groupBooksRepository;

    @Autowired
    private TraceService trace;

    @Autowired
    private BooksRepository booksRepository = new BooksRepository();

    private void prepareTag(HashMap<String,Object> data , Books book) {
        if (data.containsKey("tags")) {
            List<String> tags = (List<String>) data.get("tags");
            for (String tag : tags) {
                BooksTag booksTag = booksTagRepository.createIfNotExist(tag);
                book.getTags().add(booksTag);
            }
        }
    }

    @Transactional
    private void removeTagsFromBook(Books book) {

        for (BooksTag booksTag : book.getTags()) {
            booksTag.getBooks().remove(book);
            booksTagRepository.merge(booksTag);
        }

        book.getTags().clear();
    }

    private void prepareLinks(HashMap<String,Object> data , Books book) {
        if (data.containsKey("links")) {
            List<String> list = (List<String>) data.get("links");
            book.setLinks(list);
        }
    }

    private Result checkBookRight(HashMap<String,Object> data) {
        Long bookId = new Long((Integer)data.get("bookId"));
        Books book = booksRepository.read(bookId);
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


    @Action(name = "createBooks" , mandatoryFields = {"name","groupBookId","description","language"})
    public Result createBook(HashMap<String,Object> data) {
        if (!data.containsKey("principalName"))
            return Result.NotAuthorized();

        UserInfo userInfo = userInfoService.getAuthorizedUser(data);

        Books book = new Books();
        book.setOwner(userInfo);

        if (!book.getAuthorizationList().isAccessCreate(userInfo.getUser())) {
            return Result.AccessDenied();
        }

        Long groupBookId = new Long((Integer)data.get("groupBookId"));

        GroupBooks groupBooks =  groupBooksRepository.read(groupBookId);
        if (groupBookId == null) {
            trace.log("Group book id is incorrect" , TraceLevel.Warning);
            return Result.Failed();
        }

        book.setGroupBooks(groupBooks);

        book.setName((String) data.get("name"));
        book.setDescription((String) data.get("description"));

        try {
            Language language = languageRepository.getLanguage(Languages.valueOf((String) data.get("language")));
            book.setLanguage(language);
        } catch (Exception exception) {
            Result result = Result.Failed();
            result.setObject("Language not found");
            return result;
        }

        prepareLinks(data,book);

        prepareTag(data,book);

        booksRepository.create(book);

        for (BooksTag booksTag : book.getTags()) {
            booksTag.getBooks().add(book);
            booksTagRepository.merge(booksTag);
        }
        Result result = Result.Complete();
        result.setObject(book.getId());
        return result;
    }

    @Action(name = "updateBookInformation" , mandatoryFields = {"bookId"})
    public Result updateBookInformation(HashMap<String,Object> data) {

        Result result = checkBookRight(data);
        Books book;
        if (result.getObject() != null)  {
            book = (Books) result.getObject();
        } else {
            return result;
        }


        if (data.containsKey("name")) {
            String name = (String) data.get("name");
            book.setName(name);
        }

        if (data.containsKey("description")) {
            String description = (String) data.get("description");
            book.setDescription(description);
        }

        prepareLinks(data,book);

        removeTagsFromBook(book);
        prepareTag(data,book);

        booksRepository.merge(book);
        for (BooksTag booksTag : book.getTags()) {
            booksTag.getBooks().add(book);
            booksTagRepository.merge(booksTag);
        }

        booksTagRepository.removeUnusedTags();

        return Result.Complete();
    }

    @ActionWithFile(name = "uploadCover" , mandatoryFields = {"bookId"})
    public Result updataCover(HashMap<String,Object> data, List<MultipartFile> files) {
        Result result = checkBookRight(data);
        Books book;
        if (result.getObject() != null)  {
            book = (Books) result.getObject();
        } else {
            return result;
        }

        try {
            book.setCover(files.get(0).getBytes());
        } catch (IOException e) {
            trace.logException("Error read cover for book" , e, TraceLevel.Warning);
            return Result.Failed();
        }

        booksRepository.merge(book);
        return Result.Complete();
    }

    @ActionWithFile(name = "uploadData" , mandatoryFields = {"bookId"})
    public Result updataData(HashMap<String,Object> data, List<MultipartFile> files) {
        Result result = checkBookRight(data);
        Books book;
        if (result.getObject() != null)  {
            book = (Books) result.getObject();
        } else {
            return result;
        }

        try {
            book.setBookData(files.get(0).getBytes());
            book.setFileName(files.get(0).getOriginalFilename());
        } catch (IOException e) {
            trace.logException("Error read data for book" , e, TraceLevel.Warning);
            return Result.Failed();
        }

        booksRepository.merge(book);
        return Result.Complete();
    }

    @Override
    public byte[] getImageById(long id) {
        Books books = booksRepository.read(id);
        return books == null ? null : books.getCover();
    }

    @Override
    public FileResponse getFile(long id, Object key) {
        FileResponse fileResponse = new FileResponse();
        Books books = booksRepository.read(id);
        if (books != null)  {
            fileResponse.setData(books.getBookData());
            fileResponse.setFileName(books.getFileName());
        }
        return fileResponse;
    }
}

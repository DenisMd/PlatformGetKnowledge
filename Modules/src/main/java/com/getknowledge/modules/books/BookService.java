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
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    private BooksTagRepository booksTagRepository;

    @Autowired
    private GroupBooksRepository groupBooksRepository;

    @Autowired
    private TraceService trace;

    @Autowired
    private BookRepository bookRepository;

    private void prepareLinks(HashMap<String,Object> data , Book book) {
        if (data.containsKey("links")) {
            List<String> list = (List<String>) data.get("links");
            book.setLinks(list);
        }
    }

    private Result checkBookRight(HashMap<String,Object> data) {
        Long bookId = new Long((Integer)data.get("bookId"));
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


    @Action(name = "createBooks" , mandatoryFields = {"name","groupBookId","description","language"})
    public Result createBook(HashMap<String,Object> data) {
        if (!data.containsKey("principalName"))
            return Result.NotAuthorized();

        UserInfo userInfo = userInfoService.getAuthorizedUser(data);

        Book book = new Book();
        book.setOwner(userInfo);

        if (!book.getAuthorizationList().isAccessCreate(userInfo.getUser())) {
            return Result.AccessDenied();
        }

        Long groupBookId = new Long((Integer)data.get("groupBookId"));

        GroupBooks groupBooks =  groupBooksRepository.read(groupBookId);
        if (groupBooks == null) {
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

        if (data.containsKey("tags")) {
            List<String> tags = (List<String>) data.get("tags");
            booksTagRepository.createTags(tags,book);
        }

        bookRepository.create(book);

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
        Book book;
        if (result.getObject() != null)  {
            book = (Book) result.getObject();
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

        if (data.containsKey("tags")) {
            List<String> tags = (List<String>) data.get("tags");
            booksTagRepository.removeTagsFromEntity(book);
            booksTagRepository.createTags(tags,book);
        }

        bookRepository.merge(book);
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
        Book book;
        if (result.getObject() != null)  {
            book = (Book) result.getObject();
        } else {
            return result;
        }

        try {
            book.setCover(files.get(0).getBytes());
        } catch (IOException e) {
            trace.logException("Error read cover for book" , e, TraceLevel.Warning);
            return Result.Failed();
        }

        bookRepository.merge(book);
        return Result.Complete();
    }

    @ActionWithFile(name = "uploadData" , mandatoryFields = {"bookId"})
    public Result updataData(HashMap<String,Object> data, List<MultipartFile> files) {
        Result result = checkBookRight(data);
        Book book;
        if (result.getObject() != null)  {
            book = (Book) result.getObject();
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

        bookRepository.merge(book);
        return Result.Complete();
    }

    @Override
    public byte[] getImageById(long id) {
        Book book = bookRepository.read(id);
        return book == null ? null : book.getCover();
    }

    @Override
    public FileResponse getFile(long id, Object key) {
        FileResponse fileResponse = new FileResponse();
        Book book = bookRepository.read(id);
        if (book != null)  {
            fileResponse.setData(book.getBookData());
            fileResponse.setFileName(book.getFileName());
        }
        return fileResponse;
    }
}

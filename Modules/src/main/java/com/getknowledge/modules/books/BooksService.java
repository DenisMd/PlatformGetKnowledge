package com.getknowledge.modules.books;

import com.getknowledge.modules.Result;
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
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.FileService;
import com.getknowledge.platform.base.services.ImageService;
import com.getknowledge.platform.exceptions.NotAuthorized;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Action(name = "createBooks" , mandatoryFields = {"name","groupBookId","description","language"})
    public Result createBook(HashMap<String,Object> data) throws NotAuthorized {
        if (!data.containsKey("principalName"))
            return Result.NotAuthorized;

        UserInfo userInfo = userInfoService.getAuthorizedUser(data);

        Books book = new Books();
        book.setUser(userInfo);

        if (!book.getAuthorizationList().isAccessCreate(userInfo.getUser())) {
            return Result.AccessDenied;
        }

        Long groupBookId = new Long((Integer)data.get("groupBookId"));

        GroupBooks groupBooks =  groupBooksRepository.read(groupBookId);
        if (groupBookId == null) {
            trace.log("Group book id is incorrect" , TraceLevel.Warning);
            return Result.Failed;
        }

        book.setGroupBooks(groupBooks);

        book.setName((String) data.get("name"));
        book.setDescription((String) data.get("description"));

        try {
            Language language = languageRepository.getLanguage(Languages.valueOf((String) data.get("language")));
            book.setLanguage(language);
        } catch (Exception exception) {
            Result result = Result.Failed;
            result.setObject("Language not found");
            return result;
        }

        if (data.containsKey("links")) {
            List<String> list = (List<String>) data.get("links");
            book.setLinks(list);
        }

        if (data.containsKey("tags")) {
            List<String> tags = (List<String>) data.get("tags");
            for (String tag : tags) {
                BooksTag booksTag = booksTagRepository.createIfNotExist(tag);
                book.getTags().add(booksTag);
            }
        }

        booksRepository.create(book);

        for (BooksTag booksTag : book.getTags()) {
            booksTag.getBooks().add(book);
            booksTagRepository.merge(booksTag);
        }
        Result result = Result.Complete;
        result.setObject(book.getId());
        return result;
    }

    @Override
    public byte[] getImageById(long id) {
        Books books = booksRepository.read(id);
        return books == null ? null : books.getCover();
    }

    @Override
    public byte[] getFile(long id, Object key) {
        Books books = booksRepository.read(id);
        return books == null ? null : books.getBookData();
    }
}

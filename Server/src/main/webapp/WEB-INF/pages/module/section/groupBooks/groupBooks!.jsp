<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/courses/group.css">

<h1 class="title" ng-if="!groupBook">{{translate("books_title")}}</h1>
<h1 class="title" ng-if="groupBook">{{groupBook}}</h1>

<module-template name="cards/folderCards" data="folderData" ng-if="!groupBook"></module-template>
<module-template name="cards/books" data="booksData"  ng-if="groupBook"></module-template>
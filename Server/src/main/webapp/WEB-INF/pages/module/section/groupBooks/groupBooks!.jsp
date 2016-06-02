<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/components/folders.css">

<module-template name="cards/folders" data="folderData" ng-if="!groupBook"></module-template>
<module-template name="cards/books" data="booksData"  ng-if="groupBook"></module-template>
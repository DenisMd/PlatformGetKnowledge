<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<h1 class="title" ng-if="groupCourses">{{groupCourses}}</h1>

<module-template name="cards/folders" data="folderData" ng-if="!groupCourses"></module-template>
<module-template name="cards/courses" data="coursesData" ng-if="groupCourses"></module-template>
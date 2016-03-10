<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/courses/group.css">

<h1 class="title" ng-if="!groupCourses">{{translate("courses_title")}}</h1>
<h1 class="title" ng-if="groupCourses">{{groupCourses}}</h1>

<module-template name="cards/folderCards" data="folderData" ng-if="!groupCourses"></module-template>
<module-template name="cards/courses" data="coursesData" ng-if="groupCourses"></module-template>
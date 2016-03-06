<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/courses/group.css">

<h1 class="title" ng-if="!groupProgram">{{translate("programs_title")}}</h1>
<h1 class="title" ng-if="groupProgram">{{groupProgram}}</h1>

<module-template name="cards/folderCards" data="folderData" ng-if="!groupProgram"></module-template>
<module-template name="cards/program" data="programData"  ng-if="groupProgram"></module-template>
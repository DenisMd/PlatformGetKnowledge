<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<h1 class="title" ng-if="groupProgram">{{groupProgram}}</h1>

<module-template name="cards/folders" data="folderData" ng-if="!groupProgram"></module-template>
<module-template name="cards/programs" data="programData"  ng-if="groupProgram"></module-template>
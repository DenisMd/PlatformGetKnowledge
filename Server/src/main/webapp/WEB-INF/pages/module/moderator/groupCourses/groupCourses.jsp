<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/admin.css">

<div class="panel panel-default">
    <div class="panel-body">
        <md-input-container>
            <label>{{translate("sections")}}</label>
            <md-select ng-model="sectionId">
                <md-option ng-repeat="section in sections" value="{{section.id}}">
                    {{translate(section.name)}}
                </md-option>
            </md-select>
        </md-input-container>
        <md-button ng-click="setSection(sectionId)" class="md-raised">{{translate("search")}}</md-button>
    </div>
</div>

<div class="table-selector">
    <table class="table table-hover ">
        <caption>{{translate("courses")}} : {{countGroupCourses + ' ' + translate("ofRecords")}}</caption>
        <thead>
        <tr>
            <th ng-click="setGroupOrder('id')">
                {{translate("id")}}
            </th>
            <th ng-click="setGroupOrder('title')">
                {{translate("name")}}
            </th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="course in coursesGroup" ng-click="setCurrentItem(course)">
            <td>{{course.id}}</td>
            <td>{{course.title}}</td>
        </tr>
        <tr>
            <td colspan="4" ng-click="loadMore()" class="loadMore">
                {{translate("groupCourses_loadMore")}}
            </td>
        </tr>
        </tbody>
    </table>
</div>

<md-content>
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab ng-if="currentGroup != null">
            <md-tab-label>
                {{translate('groupCourses_info')}}
            </md-tab-label>
            <md-tab-body>
                <p>
                    {{translate('id')}} : {{currentGroup.id}} <br/>
                    {{translate('name')}} : {{currentGroup.title}}<br/>
                    <module-template name="inputs/multilanguage" data="multiLanguageData"></module-template>
                    <md-button class="md-raised md-primary" ng-click="updateSections()" ng-disabled="!currentGroup">{{translate("update")}}</md-button>
                </p>
            </md-tab-body>
        </md-tab>
        <md-tab ng-if="currentGroup != null">
            <md-tab-label>
                {{translate('section_image')}}
            </md-tab-label>
            <md-tab-body>
                <md-content class="md-padding">
                    <module-template name="inputs/selectImage" data="getCropImageData(currentSection)"></module-template>
                </md-content>
            </md-tab-body>
        </md-tab>
    </md-tabs>
</md-content>


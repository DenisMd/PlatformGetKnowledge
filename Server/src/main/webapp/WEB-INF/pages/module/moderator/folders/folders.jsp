<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/admin.css">

<div class="panel panel-default">
    <div class="panel-body">
        <md-input-container>
            <label>{{translate("folder_type")}}</label>
            <md-select ng-model="folderType">
                <md-option ng-repeat="folder in typesFolder"  ng-value="folder">
                    {{translate(folder.name)}}
                </md-option>
            </md-select>
        </md-input-container>
        <span class="panel-item fa fa-3x fa-plus create" tooltip-placement="bottom"
              uib-tooltip="{{translate('folder_create')}}" ng-click="showAdvanced($event)">
        </span>
        <span class="panel-item fa fa-3x fa-minus delete" tooltip-placement="bottom"
              uib-tooltip="{{translate('folder_delete')}}" ng-click="showDeleteDialog($event)" ng-if="currentGroup != null">
        </span>
        <md-input-container>
            <label>{{translate("section")}}</label>
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
                {{translate("folder_loadMore")}}
            </td>
        </tr>
        </tbody>
    </table>
</div>

<md-content>
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab ng-if="currentGroup != null">
            <md-tab-label>
                {{translate('folder_info')}}
            </md-tab-label>
            <md-tab-body>
                <p>
                    {{translate('id')}} : {{currentGroup.id}} <br/>
                    <div>
                        <md-input-container>
                            <label>{{translate("name")}}</label>
                            <input ng-model="currentGroup.title">
                        </md-input-container>
                    </div>
                <div>
                    <md-input-container>
                        <label>{{translate("url")}}</label>
                        <input ng-model="currentGroup.url">
                    </md-input-container>
                </div>
                    <module-template name="inputs/multilanguage" data="multiLanguageData"></module-template>
                    <md-button class="md-raised md-primary" ng-click="updateGroup()" ng-disabled="!currentGroup">{{translate("update")}}</md-button>
                </p>
            </md-tab-body>
        </md-tab>
        <md-tab ng-if="currentGroup != null">
            <md-tab-label>
                {{translate('folder_image')}}
            </md-tab-label>
            <md-tab-body>
                <md-content class="md-padding">
                    <module-template name="inputs/selectImage" data="getCropImageData()"></module-template>
                </md-content>
            </md-tab-body>
        </md-tab>n
    </md-tabs>
</md-content>


<script type="text/ng-template" id="createGroupCourses.html">
    <md-dialog  ng-cloak aria-label="options dialog">
            <md-toolbar>
                <div class="md-toolbar-tools" >
                    <h2>{{parentScope.translate("folder_create")}}</h2>
                    <span flex></span>
                    <md-button class="md-icon-button" ng-click="cancel()">
                        <md-icon md-svg-src="resources/image/svg/close.svg" aria-label="Close dialog"></md-icon>
                    </md-button>
                </div>
            </md-toolbar>
            <md-dialog-content  layout-padding>
                <div class="md-dialog-content">
                    <div>
                        <md-input-container>
                            <label>{{parentScope.translate("name")}}</label>
                            <input ng-model="group.title">
                        </md-input-container>
                    </div>
                    <div>
                        <md-input-container>
                            <label>{{parentScope.translate("Url")}}</label>
                            <input ng-model="group.url">
                        </md-input-container>
                    </div>
                    <md-input-container>
                        <label>{{parentScope.translate("section")}}</label>
                        <md-select ng-model="group.sectionId">
                            <md-option ng-repeat="section in parentScope.sections" value="{{section.id}}">
                                {{parentScope.translate(section.name)}}
                            </md-option>
                        </md-select>
                    </md-input-container>
                </div>
            </md-dialog-content>
            <md-dialog-actions layout="row">
                <md-button class="md-raised md-primary" ng-click="answer(group)">
                    {{parentScope.translate("create")}}
                </md-button>
            </md-dialog-actions>
    </md-dialog>
</script>

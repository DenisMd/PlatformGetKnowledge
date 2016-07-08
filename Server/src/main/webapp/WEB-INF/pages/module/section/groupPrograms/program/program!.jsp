<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/program-page/program.css">


<div ng-show="showEditableContent">
    <md-content>
        <md-tabs md-dynamic-height md-border-bottom>
            <md-tab label="{{translate('program_info')}}">
                <md-content class="md-padding" layout="column">
                    <div layout-gt-sm="row" flex>
                        <md-input-container flex-gt-sm>
                            <label>{{translate("name")}}</label>
                            <input ng-model="program.name">
                        </md-input-container>
                    </div>
                    <div flex layout="row">
                        <md-input-container class="md-block" flex-gt-sm>
                            <label>{{translate("choose_language")}}</label>
                            <md-select ng-model="program.language">
                                <md-option ng-value="lang" ng-repeat="lang in langs">{{ translate(lang) }}</md-option>
                            </md-select>
                        </md-input-container>
                    </div>
                    <div flex>
                        <label for="description" class="md-title">{{translate("description")}}</label>
                        <textarea class="form-control program-description" rows="5" id="description" ng-model="program.description"></textarea>
                    </div>
                    <div flex>
                        <p class="program-add-url" ng-click="addUrl()">{{translate("program_add_url")}}</p>
                        <div ng-repeat="url in program.urls" layout="row">
                            <md-input-container flex>
                                <label>{{translate("url") + " " + ($index+1)}}</label>
                                <md-icon class="fa fa-times remove-url" ng-click="removeUrl($index)"></md-icon>
                                <input ng-model="url.name">
                            </md-input-container>
                        </div>
                    </div>
                    <div layout="column" class="program-tags" flex ng-init="newProgram.tags = []" flex>
                        <div class="md-title" flex>{{translate("tags")}}</div>
                        <md-chips ng-model="program.tagsName" flex></md-chips>
                    </div>
                    <div layout="row" class="update-btn-container" layout-align="center" flex>
                        <button flex="none" class="btn btn-default blue-btn" ng-click="updateProgram(program)">{{translate("update")}}</button>
                    </div>
                </md-content>
            </md-tab>
            <md-tab label="{{translate('program_cover')}}">
                <md-content class="md-padding">
                    <md-tab-body>
                        <md-content class="md-padding">
                            <module-template name="inputs/selectImage" data="croppedImg"></module-template>
                        </md-content>
                    </md-tab-body>
                </md-content>
            </md-tab>
            <md-tab label="{{translate('program_data')}}">
                <md-content class="md-padding">
                    <module-template name="inputs/uploadFiles" data="uploadData"></module-template>
                    <%--<input type="file" nv-file-select uploader="uploader"/><br/>--%>
                    <%--<ul>--%>
                    <%--<li ng-repeat="item in uploader.queue">--%>
                    <%--Name: <span ng-bind="item.file.name"></span><br/>--%>
                    <%--<button ng-click="item.upload()">{{translate("upload")}}</button>--%>
                    <%--</li>--%>
                    <%--</ul>--%>
                </md-content>
            </md-tab>
            <md-tab label="{{translate('program_delete')}}">
                <md-content class="md-padding">
                    <div>
                        <button type="button" class="btn btn-danger" ng-click="showDeleteDialog(event)">{{translate("program_delete")}}</button>
                    </div>
                </md-content>
            </md-tab>
        </md-tabs>
    </md-content>
</div>


<div layout="row" class="program-change-row" layout-align-gt-sm="start start" layout-align="center center">
    <button class="btn btn-default blue-btn" ng-click="showEditableContent = !showEditableContent" ng-show="program.editable" flex="none">
        {{translate("program_change")}}
    </button>
</div>

<div layout-gt-sm="row"  layout-align-gt-sm="start start" layout-align="start center" layout="column">
    <div flex="none" >
        <img ng-src="{{program.coverUrl}}" class="program-cover">
    </div>
    <div flex="65" class="program-title">
        <h1 class="text-center">{{program.name}}</h1>
        <h4 class="text-center">{{program.authorName}}</h4>
        <p class="description text-justify">
            {{program.description}}
        </p>
        <p class="program-date">{{translate("program_create_date")}} : {{program.createDate|date:"medium"}}</p>
        <md-chips ng-model="program.tagsName" readonly="true"></md-chips>
    </div>
    <div flex="35" layout="column" layout-align="start center">
        <p class="text-center program-author-title">{{translate("program_uploader")}}<p>
        <a ng-href="{{program.owner.userUrl}}" class="program-author-name">
            <img ng-src="{{program.owner.imageSrc}}" class="main-image"/>
        <p class="main-label">{{program.owner.firstName}} {{program.owner.lastName}}</p>
        </a>
        <p>{{program.owner.speciality}}</p>
    </div>
</div>

<div layout="column" layout-align="start center" class="program-links">

    <div class="program-links-title">
        {{translate("program_links")}}
    </div>

    <div ng-repeat="url in program.links track by $index" class="program-link-margin">
        <i class="fa fa-external-link" aria-hidden="true"></i>
        <a ng-href="{{url}}" class="program-link" target="_blank">{{url}}</a>
    </div>

    <div ng-if="program.fileName">
        <i class="fa fa-cloud-download" aria-hidden="true"></i>
        <a ng-href="{{book.downloadUrl}}" download>{{translate("program_download_link")}} ---> '{{program.fileName}}'</a>
    </div>
</div>

<div>
    <module-template name="comments/simpleComment" data="commentData"></module-template>
</div>
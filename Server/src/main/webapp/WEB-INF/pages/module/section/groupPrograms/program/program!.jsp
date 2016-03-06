<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/section.css">

<h1 class="text-center">{{program.name}}</h1>

<div>
    <md-button class="md-raised md-primary" ng-click="showEditableContent = !showEditableContent" ng-show="program.editable">{{translate("change")}}</md-button>
</div>

<div ng-show="showEditableContent">
    <md-content>
        <md-tabs md-dynamic-height md-border-bottom>
            <md-tab label="{{translate('program_info')}}">
                <md-content class="md-padding">
                    <div>
                        <md-input-container>
                            <label>{{translate("name")}}</label>
                            <input ng-model="program.name">
                        </md-input-container>
                    </div>
                    <h2 class="md-title">Тэги</h2>
                    <div>
                        <md-chips ng-model="program.tagsName" readonly="false"></md-chips>
                    </div>
                    <div>
                        <label for="description">{{translate("description")}}</label>
                        <textarea class="form-control" rows="5" id="description" ng-model="program.description"></textarea>
                    </div>
                    <div>
                        <a href="" ng-click="addUrl()">{{translate("program_add_url")}}</a>
                        <div>
                            <div ng-repeat="url in program.urls">
                                <md-input-container>
                                    <label>{{translate("url") + ($index+1)}}</label>
                                    <input ng-model="url.name"> (<a href="" ng-click="removeUrl($index)">X</a>)
                                </md-input-container>
                            </div>
                        </div>
                    </div>
                    <div>
                        <md-button class="md-raised md-primary" ng-click="updateProgram(program)">{{translate("update")}}</md-button>
                    </div>
                </md-content>
            </md-tab>
            <md-tab label="{{translate('program_cover')}}">
                <md-content class="md-padding">
                    <md-tab-body>
                        <md-content class="md-padding">
                            <module-template name="inputs/selectImage" data="getCropImageData()"></module-template>
                        </md-content>
                    </md-tab-body>
                </md-content>
            </md-tab>
            <md-tab label="{{translate('program_data')}}">
                <md-content class="md-padding">
                    <input type="file" nv-file-select uploader="uploader"/><br/>
                    <ul>
                        <li ng-repeat="item in uploader.queue">
                            Name: <span ng-bind="item.file.name"></span><br/>
                            <button ng-click="item.upload()">{{translate("upload")}}</button>
                        </li>
                    </ul>
                </md-content>
            </md-tab>
        </md-tabs>
    </md-content>
</div>

<img ng-src="{{programImg()}}"
     class="cover-img">

<p class="description">
    {{program.description}}
</p>

<div>
    {{translate("tags")}} :
    <div ng-repeat="tag in  program.tags" style="display:inline">
        {{tag.tagName}}
    </div>
    <br>
    {{translate("links")}}
    <ul>
        <li ng-repeat="url in program.links">
            <a ng-href="{{url}}">{{url}}</a>
        </li>
    </ul>
    <div>
        <a ng-href="{{programData()}}" download>{{"data_link"}}</a>
    </div>
</div>

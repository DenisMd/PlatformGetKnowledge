<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/section.css">

<h1 class="text-center">{{course.name}}</h1>

<div>
    <md-button class="md-raised md-primary" ng-click="showEditableContent = !showEditableContent" ng-show="course.editable">{{translate("change")}}</md-button>
</div>

<div ng-show="showEditableContent">
    <md-content>
        <md-tabs md-dynamic-height md-border-bottom>
            <md-tab label="{{translate('course_info')}}">
                <md-content class="md-padding">
                    <div>
                        <md-input-container>
                            <label>{{translate("name")}}</label>
                            <input ng-model="course.name">
                        </md-input-container>
                    </div>
                    <h2 class="md-title">Тэги</h2>
                    <div>
                        <md-chips ng-model="course.tagsName" readonly="false"></md-chips>
                    </div>
                    <div>
                        <label for="description">{{translate("description")}}</label>
                        <textarea class="form-control" rows="5" id="description" ng-model="course.description"></textarea>
                    </div>
                    <div>
                        <md-button class="md-raised md-primary" ng-click="updateProgram(course)">{{translate("update")}}</md-button>
                    </div>
                </md-content>
            </md-tab>
            <md-tab label="{{translate('cours_cover')}}">
                <md-content class="md-padding">
                    <md-tab-body>
                        <md-content class="md-padding">
                            <module-template name="inputs/selectImage" data="getCropImageData()"></module-template>
                        </md-content>
                    </md-tab-body>
                </md-content>
            </md-tab>
        </md-tabs>
    </md-content>
</div>

<img ng-src="{{courseImg()}}"
     class="cover-img">

<p class="description">
    {{course.description}}
</p>

<div>
    {{translate("tags")}} :
    <div ng-repeat="tag in course.tags" style="display:inline">
        {{tag.tagName}}
    </div>
    <br>
</div>

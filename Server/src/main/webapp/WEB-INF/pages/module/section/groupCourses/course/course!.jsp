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
                        <h2 class="md-title">Знания, полученные за курс</h2>
                        <md-contact-chips
                                ng-model="course.sourceKnowledge"
                                md-contacts="querySearch($query)"
                                md-contact-name="name"
                                md-contact-image="image"
                                md-require-match="true"
                                md-highlight-flags="i"
                                placeholder="To">
                        </md-contact-chips>
                    </div>
                    <div ng-if="!course.base">
                        <h2 class="md-title">Знания, требуемые для освоения курса</h2>
                        <md-contact-chips
                                ng-model="course.requiredKnowledge"
                                md-contacts="querySearch($query)"
                                md-contact-name="name"
                                md-contact-image="image"
                                md-require-match="true"
                                md-highlight-flags="i"
                                placeholder="To">
                        </md-contact-chips>
                    </div>
                    <div>
                        <label for="description">{{translate("description")}}</label>
                        <textarea class="form-control" rows="5" id="description" ng-model="course.description"></textarea>
                    </div>
                    <div>
                        <md-button class="md-raised md-primary" ng-click="updateCourse(course)">{{translate("update")}}</md-button>
                    </div>
                </md-content>
            </md-tab>
            <md-tab label="{{translate('course_cover')}}">
                <md-content class="md-padding">
                    <md-tab-body>
                        <md-content class="md-padding">
                            <module-template name="inputs/selectImage" data="getCropImageData()"></module-template>
                        </md-content>
                    </md-tab-body>
                </md-content>
            </md-tab>
            <md-tab label="{{translate('course_video_intro')}}">
                <md-content class="md-padding">
                    <md-tab-body>
                        <md-content class="md-padding">
                            <md-input-container>
                                <label>{{translate("name")}}</label>
                                <input ng-model="course.intro.videoName">
                            </md-input-container>
                            <div ng-show="course.intro.videoName">
                                <module-template name="inputs/selectImage" data="getVideoImage()"></module-template>
                            </div>
                            <div>
                                <input type="file" accept="video/mp4,video/x-m4v,video/*" nv-file-select uploader="uploader"/><br/>
                                <ul>
                                    <li ng-repeat="item in uploader.queue">
                                        Name: <span ng-bind="item.file.name"></span><br/>
                                        <button ng-click="item.upload()">{{translate("upload")}}</button>
                                    </li>
                                </ul>
                            </div>
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


{{course}}
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/course-page/course.css">

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

<%--Course Info--%>
<div layout="row" class="course-info">
    <div flex="60" class="course-intro-video">
        <module-template name="components/video" data="indexVideo1"></module-template>
    </div>
    <div flex class="course-info-left-block">
        <%--Name--%>
        <div>
            {{course.name}}
        </div>
        <%--Dates--%>
        <did>
            {{translate("course_create_time")}} : {{course.createDate | date:'meduim'}}
        </did>
        <div>
            {{translate("course_last_released_time")}} : {{course.lastReleasedDate | date:'meduim'}}
        </div>
        <%--Knowledge--%>
        <div>

        </div>
        <%--Rating--%>
        <div>

        </div>
        <%--Price--%>
        <div>

        </div>
    </div>
</div>

<%--Tutorials list--%>
<div layout="row">

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
<div ng-controller="videoCtrl">
    <!-- Modal -->
    <div class="modal fade" id="videoModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <div class="modal-dialog" role="document">
            <div class="modal-content modal-center">
                <!--<div class="modal-body">-->
                <video id="main-video" class="video-js vjs-default-skin vjs-big-play-centered">
                    <!--poster="/resources/image/index/slider/programming.jpg" data-setup="{{videoSetup}}">-->
                </video>
                <!--</div>-->
            </div>
        </div>
    </div>
</div>

<div ng-if="course.editable && !course.release">
    <md-button class="md-raised md-primary" ng-click="makeRelease()">
        Make release
    </md-button>
</div>

<div ng-if="course.editable">
    <md-button class="md-raised md-primary" ng-click="showAdvanced($event)">
        Create tutorial
    </md-button>
</div>

<ul>
    <li ng-repeat="(key,name) in tutorials">
        <a ng-href="{{addUrlToPath('/tutorial/'+key)}}">Урок {{key + ') ' + name}}</a>
    </li>
</ul>

{{course}}

<script type="text/ng-template" id="createTutorial.html">
    <md-dialog  ng-cloak>
        <form>
            <md-toolbar>
                <div class="md-toolbar-tools">
                    <h2>{{parentScope.translate("course_create_tutorial")}}</h2>
                    <span flex></span>
                    <md-button class="md-icon-button" ng-click="cancel()">
                        <md-icon md-svg-src="resources/image/svg/close.svg" aria-label="Close dialog"></md-icon>
                    </md-button>
                </div>
            </md-toolbar>
            <md-dialog-content>
                <div class="md-dialog-content">
                    <div>
                        <md-input-container>
                            <label>{{parentScope.translate("name")}}</label>
                            <input ng-model="tutorial.name">
                        </md-input-container>
                    </div>
                </div>
            </md-dialog-content>
            <md-dialog-actions layout="row">
                <md-button class="md-raised md-primary" ng-click="answer(tutorial)">
                    {{parentScope.translate("create")}}
                </md-button>
            </md-dialog-actions>
        </form>
    </md-dialog>
</script>
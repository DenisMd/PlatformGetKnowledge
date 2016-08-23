<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/course-page/course.css">


<div layout="column" layout-gt-sm="row" layout-margin class="course-author-row" layout-align-gt-sm="start start" layout-align="center center">
    <button class="btn btn-default black-btn" ng-click="showEditableContent = !showEditableContent" ng-if="course.editable" flex="none">
        {{translate("course_change")}}
    </button>

    <button class="btn btn-default black-btn" ng-click="showAdvanced($event)" ng-if="course.editable" flex="none">
        {{translate("course_create_tutorial")}}
    </button>

    <button class="btn btn-default black-btn" ng-click="" ng-if="course.editable" flex="none">
        {{translate("course_change_tutorial_order")}}
    </button>

    <button class="btn btn-default black-btn" ng-click="makeRelease()" ng-if="course.editable && !course.release" flex="none">
        {{translate("course_make_release")}}
    </button>

</div>

<div ng-if="showEditableContent">
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
<div layout="column" layout-gt-sm="row" class="course-info">
    <div flex="60" class="course-intro-video">
        <module-template name="components/video" data="introVideo"></module-template>
        <%--Knowledge--%>
        <div ng-if="!course.base">
            <div class="course-knowledge-title">{{translate("course_required_knowledge")}} : </div>
            <md-chips flex class="md-contact-chips"
                      ng-model="course.requiredKnowledge"
                      md-require-match="true"
                      readonly="true">
                <md-chip-template>
                    <a ng-href="{{$chip.knowldgeHref}}" class="link-without-style">
                        <div class="md-contact-avatar">
                            <img ng-src="{{$chip.image}}" alt="{{$chip.name}}" ng-if="$chip.image" class="chip-image"/>
                        </div>
                        <div class="md-contact-name">
                            {{$chip.name}}
                        </div>
                    </a>
                </md-chip-template>
            </md-chips>
        </div>
        <div>
            <div class="course-knowledge-title">{{translate("course_source_knowledge")}} :</div>
            <md-chips flex class="md-contact-chips"
                      ng-model="course.sourceKnowledge"
                      md-require-match="true"
                      readonly="true">
                <md-chip-template>
                    <a ng-href="{{$chip.knowldgeHref}}" class="link-without-style">
                        <div class="md-contact-avatar">
                            <img ng-src="{{$chip.image}}" alt="{{$chip.name}}" ng-if="$chip.image" class="chip-image"/>
                        </div>
                        <div class="md-contact-name">
                            {{$chip.name}}
                        </div>
                    </a>
                </md-chip-template>
            </md-chips>
        </div>
    </div>
    <div flex class="course-info-left-block">
        <%--Name--%>
        <div layout="row" layout-align="center" class="course-title">
            {{course.name}}
        </div>
        <div ng-if="course.base">
            {{translate("course_base")}}
        </div>
        <%--Version--%>
        <div>
            {{translate("course_version")}} : {{course.version | version}}
        </div>
        <%--Dates--%>
        <did>
            {{translate("course_create_date")}} : {{course.createDate | date:'medium'}}
        </did>
        <div ng-if="course.lastReleasedDate">
            {{translate("course_last_released_date")}} : {{course.lastReleasedDate | date:'medium'}}
        </div>
        <%--Rating--%>
        <hr>
        <div>
            <%--{{translate("course_avg_rating")}}--%>
            <%--<div star-rating ng-model="course.rating.avgRating" max="5"--%>
                 <%--readonly="true">--%>
            <%--</div>--%>
            {{translate("course_information_rating")}}
            <div star-rating ng-model="course.rating.qualityInformation" max="5"
                 readonly="true">
            </div>
            {{translate("course_exercises_rating")}}
            <div star-rating ng-model="course.rating.qualityExercises" max="5"
                 readonly="true">
            </div>
            {{translate("course_test_rating")}}
            <div star-rating ng-model="course.rating.qualityTest" max="5"
                 readonly="true">
            </div>
            {{translate("course_relevance_information")}}
            <div star-rating ng-model="course.rating.relevanceInformation" max="5"
                 readonly="true">
            </div>
        </div>
        <%--Price--%>
        <hr>
        <div>
            <module-template name="components/price" data="course.item.price"></module-template>
        </div>
        <hr>
        <div class="course-tags">
            {{translate("tags")}} :
            <div ng-repeat="tag in course.tags" style="display:inline">
                <span class="course-tag-name">{{tag.tagName}}</span><span ng-if="!$last">,</span>
            </div>
            <br>
        </div>
    </div>
</div>

<%--Tutorials list and Description with Author info--%>
<div layout="row">
    <div flex="60">
        <ul>
            <li ng-repeat="(key,name) in tutorials">
                <a ng-href="{{addUrlToPath('/tutorial/'+key)}}">Урок {{key + ') ' + name}}</a>
            </li>
        </ul>
    </div>
    <div flex>
        <p class="text-center book-author-title">{{translate("course_author")}}<p>
        <a ng-href="{{course.author.userUrl}}" class="course-author-name">
            <img ng-src="{{course.author.imageSrc}}" class="main-image"/>
            <p class="main-label">{{course.author.firstName}} {{course.author.lastName}}</p>
        </a>
        <p>{{course.author.speciality}}</p>
        <img ng-src="{{course.imageSrc}}"
             class="cover-img">

        <p class="description">
            {{course.description}}
        </p>
    </div>
</div>

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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/section.css">

<h1 class="text-center">Урок {{tutorial.orderNumber + ") " + tutorial.name}}</h1>
<div>
    <md-button class="md-raised md-primary" ng-click="showEditableContent = !showEditableContent" ng-show="course.editable">{{translate("change")}}</md-button>
</div>

<div ng-show="showEditableContent">
    <md-content>
        <md-tabs md-dynamic-height md-border-bottom>
            <md-tab label="{{translate('tutorial_info')}}">
                <md-content class="md-padding">
                    <div>
                        <md-input-container>
                            <label>{{translate("name")}}</label>
                            <input ng-model="tutorial.name">
                        </md-input-container>
                    </div>
                    <div>
                        <md-input-container class="md-block">
                            <label>{{translate("order")}}</label>
                            <input required type="number" step="any" name="rate" ng-model="tutorial.orderNumber" />
                        </md-input-container>
                    </div>
                    <div>
                        <label for="description">{{translate("data")}}</label>
                        <textarea class="form-control" rows="5" id="description" ng-model="tutorial.data"></textarea>
                    </div>
                    <div>
                        <md-button class="md-raised md-primary" ng-click="updateTutorial(tutorial)">{{translate("update")}}</md-button>
                    </div>
                </md-content>
            </md-tab>
            <md-tab label="{{translate('tutorial_video')}}">
                <md-content class="md-padding">
                    <md-tab-body>
                        <md-content class="md-padding">
                            <md-input-container>
                                <label>{{translate("name")}}</label>
                                <input ng-model="tutorial.video.videoName">
                            </md-input-container>
                            <div ng-show="tutorial.video.videoName">
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

<p class="description">
    {{tutorial.data}}
</p>

<div ng-controller="videoCtrl">
    <div class="video-image" ng-if="tutorial.video">
        <img ng-src="{{videoImg(tutorial.video.id)}}"
             class="img-thumbnail video-poster">
        <a ng-click="open(tutorial.video.id)" class="video-play"><span class="glyphicon glyphicon-play-circle" aria-hidden="true"></span></a>
    </div>
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


{{tutorial}}


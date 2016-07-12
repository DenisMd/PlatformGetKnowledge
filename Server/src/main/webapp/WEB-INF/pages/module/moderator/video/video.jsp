<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/workflow/workflow.css">

<div class="selector-zone">
  <module-template name="selectors/serverSelector" data="selectorData"></module-template>
</div>

<md-content md-theme="darkTheme">
  <md-tabs md-dynamic-height md-border-bottom>
    <md-tab label="{{translate('video_info')}}" ng-if="currentVideo != null">
      <md-content flex layout-padding>
        <div layout="row">
          <div flex="55" flex-gt-sm="20">{{translate('id')}}</div>
          <div flex>{{currentVideo.id}}</div>
        </div>
        <div layout="row">
          <div flex="55" flex-gt-sm="20">{{translate('video_link')}}</div>
          <div flex>{{currentVideo.link}}</div>
        </div>
        <div layout="row">
          <md-input-container flex>
            <label>{{translate("name")}}</label>
            <input ng-model="currentVideo.videoName">
          </md-input-container>
        </div>
        <div layout="row">
          <md-checkbox ng-model="currentVideo.allowEveryOne" flex>
            {{translate("video_allow")}}
          </md-checkbox>
        </div>
        <md-button class="md-raised md-primary md-btn" ng-click="updateVideo()" ng-disabled="!currentVideo">{{translate("update")}}</md-button>
      </md-content>
    </md-tab>
    <md-tab ng-if="currentVideo != null">
      <md-tab-label>
        {{translate('video_cover')}}
      </md-tab-label>
      <md-tab-body>
        <md-content class="md-padding">
          <module-template name="inputs/selectImage" data="croppedImg"></module-template>
        </md-content>
      </md-tab-body>
    </md-tab>
  </md-tabs>
</md-content>


<script type="text/ng-template" id="createKnowledge.html">
  <md-dialog  ng-cloak aria-label="options dialog">
    <md-toolbar>
      <div class="md-toolbar-tools" >
        <h2>{{parentScope.translate("knowledge_create")}}</h2>
        <span flex></span>
        <md-button class="md-icon-button" ng-click="cancel()">
          <md-icon md-svg-src="resources/image/svg/close.svg" aria-label="Close dialog"></md-icon>
        </md-button>
      </div>
    </md-toolbar>
    <md-dialog-content  layout-padding>
      <div class="md-dialog-content">
        <div layout="row">
          <md-input-container flex>
            <label>{{parentScope.translate("name")}}</label>
            <input ng-model="knowledge.name">
          </md-input-container>
        </div>
        <div layout="row">
          <md-input-container flex>
            <label>{{parentScope.translate("type")}}</label>
            <md-select ng-model="knowledge.knowledgeType">
              <md-option ng-repeat="type in parentScope.knowledgeType" value="{{type}}">
                {{parentScope.translate(type.toLowerCase())}}
              </md-option>
            </md-select>
          </md-input-container>
        </div>
      </div>
    </md-dialog-content>
    <md-dialog-actions layout="row">
      <md-button class="md-raised md-primary" ng-click="answer(knowledge)">
        {{parentScope.translate("create")}}
      </md-button>
    </md-dialog-actions>
  </md-dialog>
</script>



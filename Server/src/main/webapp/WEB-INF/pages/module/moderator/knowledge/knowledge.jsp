<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/admin.css">

<div class="panel panel-default">
    <span class="panel-item fa fa-3x fa-plus create" tooltip-placement="bottom"
          uib-tooltip="{{translate('knowledge_create')}}" ng-click="showAdvanced($event)">
        </span>
        <span class="panel-item fa fa-3x fa-minus delete" tooltip-placement="bottom"
              uib-tooltip="{{translate('knowledge_delete')}}" ng-click="showDeleteDialog($event)" ng-if="currentKnowledge != null">
        </span>
    <div class="panel-body">
        <md-input-container>
            <label>{{translate("knowledge_type")}}</label>
            <md-select ng-model="knType">
                <md-option ng-repeat="type in knowledgeType" value="{{type}}">
                    {{translate(type.toLowerCase())}}
                </md-option>
            </md-select>
        </md-input-container>
        <md-button ng-click="searchByType(knType)" class="md-raised">{{translate("search")}}</md-button>
    </div>
</div>


<div class="table-selector">
    <table class="table table-hover ">
        <caption>{{translate("knowledge")}}</caption>
        <thead>
        <tr>
            <th ng-click="setLogOrder('id')">
                {{translate("id")}}
            </th>
            <th ng-click="setLogOrder('name')">
                {{translate("name")}}
            </th>
            <th ng-click="setLogOrder('knowledgeType')">
                {{translate("type")}}
            </th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="knowledge in knowledges" ng-click="setCurrentItem(knowledge)">
            <td>{{knowledge.id}}</td>
            <td>{{knowledge.name}}</td>
            <td>{{translate(knowledge.knowledgeType.toLowerCase())}}</td>
        </tr>
        <tr>
            <td colspan="4" ng-click="loadMore()" class="loadMore">
                {{translate("knowledge_loadMore")}}
            </td>
        </tr>
        </tbody>
    </table>
</div>

<md-content>
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab label="{{translate('knowledge_info')}}" ng-if="currentKnowledge != null">
            <md-content flex layout-padding>
                {{translate('id')}} : {{currentKnowledge.id}} <br/>
                <div>
                    <md-input-container>
                        <label>{{translate("name")}}</label>
                        <input ng-model="currentKnowledge.name">
                    </md-input-container>
                </div>
                <div>
                    <md-input-container>
                        <label>{{parentScope.translate("type")}}</label>
                        <md-select ng-model="currentKnowledge.knowledgeType">
                            <md-option ng-repeat="type in knowledgeType" value="{{type}}">
                                {{translate(type.toLowerCase())}}
                            </md-option>
                        </md-select>
                    </md-input-container>
                </div>
                <div class="form-group">
                    <label for="note">{{translate("knowledge_note")}}:</label>
                    <textarea class="form-control" rows="5" id="note" ng-model="currentKnowledge.note"></textarea>
                </div>
                <md-button class="md-raised md-primary" ng-click="updateKnowledge()" ng-disabled="!currentKnowledge">{{translate("update")}}</md-button>
            </md-content>
        </md-tab>
        <md-tab ng-if="currentKnowledge != null">
            <md-tab-label>
                {{translate('knowledge_image')}}
            </md-tab-label>
            <md-tab-body>
                <md-content class="md-padding">
                    <module-template name="inputs/selectImage" data="getCropImageData()"></module-template>
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
                <div>
                    <md-input-container>
                        <label>{{parentScope.translate("name")}}</label>
                        <input ng-model="knowledge.name">
                    </md-input-container>
                </div>
                <md-input-container>
                    <label>{{parentScope.translate("type")}}</label>
                    <md-select ng-model="knowledge.knowledgeType">
                        <md-option ng-repeat="type in parentScope.knowledgeType" value="{{type}}">
                            {{parentScope.translate(type.toLowerCase())}}
                        </md-option>
                    </md-select>
                </md-input-container>
            </div>
        </md-dialog-content>
        <md-dialog-actions layout="row">
            <md-button class="md-raised md-primary" ng-click="answer(knowledge)">
                {{parentScope.translate("create")}}
            </md-button>
        </md-dialog-actions>
    </md-dialog>
</script>



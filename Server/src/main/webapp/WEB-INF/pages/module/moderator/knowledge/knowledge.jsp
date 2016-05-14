<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/workflow/workflow.css">

<div class="selector-zone">
    <module-template name="selectors/serverSelector" data="selectorData"></module-template>
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
                        <md-select ng-model="currentKnowledge.knowledgeType" aria-label="knowledgeType">
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



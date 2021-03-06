<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/workflow/workflow.css">

<div class="selector-zone">
    <module-template name="selectors/clientSelector" data="selectorData"></module-template>
</div>

<md-content md-theme="darkTheme">
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab label="{{translate('ps_title')}}" ng-if="currentEStyle != null">
            <md-content class="md-padding">
                <div layout="row">
                    <div flex="55" flex-gt-sm="20">{{translate('id')}}</div>
                    <div flex>{{currentEStyle.id}}</div>
                </div>
                <div layout="row">
                    <md-input-container flex>
                        <label>{{translate('name')}}</label>
                        <input ng-model="currentEStyle.name">
                    </md-input-container>
                </div>
                <md-button class="md-raised md-primary md-btn" ng-click="updatePLanguage()" ng-disabled="!currentEStyle">{{translate("update")}}</md-button>
            </md-content>
        </md-tab>
    </md-tabs>
</md-content>

<script type="text/ng-template" id="createPS.html">
    <md-dialog  ng-cloak>
        <form>
            <md-toolbar>
                <div class="md-toolbar-tools">
                    <h2>{{parentScope.translate("ps_create")}}</h2>
                    <span flex></span>
                    <md-button class="md-icon-button" ng-click="cancel()">
                        <md-icon md-svg-src="resources/image/svg/close.svg" aria-label="Close dialog"></md-icon>
                    </md-button>
                </div>
            </md-toolbar>
            <md-dialog-content>
                <div class="md-dialog-content">
                    <div layout="row">
                        <md-input-container flex>
                            <label>{{parentScope.translate("name")}}</label>
                            <input ng-model="es.name">
                        </md-input-container>
                    </div>
                </div>
            </md-dialog-content>
            <md-dialog-actions layout="row">
                <md-button class="md-raised md-primary" ng-click="answer(es)">
                    {{parentScope.translate("create")}}
                </md-button>
            </md-dialog-actions>
        </form>
    </md-dialog>
</script>
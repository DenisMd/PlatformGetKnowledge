<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/workflow/workflow.css">

<div class="selector-zone">
    <module-template name="selectors/clientSelector" data="selectorData"></module-template>
</div>

<md-content md-theme="darkTheme">
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab label="{{translate('bootstrap_serviceInfo')}}" ng-if="currentService != null">
            <md-content class="md-padding">
                    <div layout="row">
                        <div flex="55" flex-gt-sm="20">{{translate('id')}}</div>
                        <div flex>{{currentService.id}}</div>
                    </div>
                    <div layout="row">
                        <div flex="55" flex-gt-sm="20">{{translate('name')}}</div>
                        <div flex>{{currentService.name}}</div>
                    </div>
                    <div layout="row">
                        <div flex="55" flex-gt-sm="20">{{translate('bootstrap_order')}}</div>
                        <div flex>{{currentService.order}}</div>
                    </div>
                    <div layout="row">
                        <div flex="55" flex-gt-sm="20">{{translate('bootstrap_repeat')}}</div>
                        <div flex>
                            <md-switch ng-model="currentService.repeat" class="switch-cell"></md-switch>
                        </div>
                    </div>

                    <md-button class="md-raised md-primary md-btn" ng-click="updateService()" ng-disabled="!currentService">{{translate("update")}}</md-button>
            </md-content>
        </md-tab>
        <md-tab label="{{translate('bootstrap_stackTrace')}}" ng-if="currentService != null && currentService.stackTrace != null">
            <module-template name="components/stacktrace" data="stackTraceData"></module-template>
        </md-tab>
    </md-tabs>
</md-content>

<script type="text/ng-template" id="doBootstrapModal.html">
    <md-dialog ng-cloak>
        <form>
            <md-toolbar>
                <div class="md-toolbar-tools">
                    <h2>{{parentScope.translate("bootstrap_do_bootstrap")}}</h2>
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
                            <label>{{parentScope.translate("bootstrap_domain")}}</label>
                            <input ng-model="bootstrap.domain">
                        </md-input-container>
                    </div>
                    <div layout="row">
                        <md-input-container flex>
                            <label>{{parentScope.translate("email")}}</label>
                            <input ng-model="bootstrap.email">
                        </md-input-container>
                    </div>
                    <div layout="row">
                        <md-input-container flex>
                            <label>{{parentScope.translate("password")}}</label>
                            <input ng-model="bootstrap.password">
                        </md-input-container>
                    </div>
                    <div layout="row">
                        <md-input-container flex>
                            <label>{{parentScope.translate("first_name")}}</label>
                            <input ng-model="bootstrap.firstName">
                        </md-input-container>
                    </div>
                    <div layout="row">
                        <md-input-container flex>
                            <label>{{parentScope.translate("last_name")}}</label>
                            <input ng-model="bootstrap.lastName">
                        </md-input-container>
                    </div>
                </div>
            </md-dialog-content>
            <md-dialog-actions layout="row">
                <md-button class="md-raised md-primary" ng-click="answer(bootstrap)" ng-disabled="parentScope.objectIsEmpty(bootstrap)">
                    {{parentScope.translate("do_it")}}
                </md-button>
            </md-dialog-actions>
        </form>
    </md-dialog>
</script>
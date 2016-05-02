<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/workflow/workflow.css">

<div class="selector-zone">
    <module-template name="selectors/staticSelector" data="selectorData"></module-template>
</div>

<md-content>
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab label="{{translate('bootstrap_serviceInfo')}}" ng-if="currentService != null">
            <md-content class="md-padding">
                <p>

                    {{translate('id')}}                 : {{currentService.id}}     <br/>
                    {{translate('name')}}               : {{currentService.name}}   <br/>
                    {{translate('bootstrap_order')}}    : {{currentService.order}}  <br/>

                    <md-switch ng-model="currentService.repeat">
                        {{translate('bootstrap_repeat')}}
                    </md-switch>
                    <md-button class="md-raised md-primary" ng-click="updateService()" ng-disabled="!currentService">{{translate("update")}}</md-button>
                </p>
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
                    <div>
                        <md-input-container>
                            <label>{{parentScope.translate("bootstrap_domain")}}</label>
                            <input ng-model="bootstrap.domain">
                        </md-input-container>
                    </div>
                    <div>
                        <md-input-container>
                            <label>{{parentScope.translate("email")}}</label>
                            <input ng-model="bootstrap.email">
                        </md-input-container>
                    </div>
                    <div>
                        <md-input-container>
                            <label>{{parentScope.translate("password")}}</label>
                            <input ng-model="bootstrap.password">
                        </md-input-container>
                    </div>
                    <div>
                        <md-input-container>
                            <label>{{parentScope.translate("first_name")}}</label>
                            <input ng-model="bootstrap.firstName">
                        </md-input-container>
                    </div>
                    <div>
                        <md-input-container>
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
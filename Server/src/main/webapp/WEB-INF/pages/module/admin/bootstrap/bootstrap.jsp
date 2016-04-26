<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/workflow/workflow.css">

<%--<div class="panel panel-default">--%>
    <%--<div class="panel-body">--%>
        <%--<span class="panel-item fa fa-3x fa-cogs" tooltip-placement="bottom"--%>
              <%--uib-tooltip="{{translate('bootstrap_doBootstrap')}}" ng-click="showBootstrapDialog($event)">--%>
        <%--</span>--%>
    <%--</div>--%>
<%--</div>--%>
<div class="selector-zone">
    <module-template name="selectors/staticSelector" data="selectorData"></module-template>
</div>
<md-content>
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab label="{{translate('bootstrap_serviceInfo')}}" ng-if="currentService != null">
            <md-content class="md-padding">
                <p>
                    {{translate('id')}} : {{currentService.id}} <br/>
                    {{translate('name')}} : {{currentService.name}}       <br/>
                    {{translate('bootstrap_order')}} : {{currentService.order}}     <br/>
                    <md-switch ng-model="currentService.repeat">
                        {{translate('bootstrap_repeat')}}
                    </md-switch>
                    <md-button class="md-raised md-primary" ng-click="updateService()" ng-disabled="!currentService">{{translate("update")}}</md-button>
                </p>
            </md-content>
        </md-tab>
        <md-tab label="{{translate('bootstrap_stackTrace')}}" ng-if="currentService != null && currentService.stackTrace != null">
            <md-toolbar class="md-warn">
                <div class="md-toolbar-tools">
                    <h2 class="md-flex">{{translate('bootstrap_stackTrace')}}</h2>
                </div>
            </md-toolbar>
            <md-content flex layout-padding>
                <md-button class="btn md-raised md-warn clipboard"  data-clipboard-text="{{currentService.stackTrace}}">
                    {{translate("copyToClipBoard")}}
                </md-button>
                <p id="bar">
                    {{currentService.stackTrace}}
                </p>
            </md-content>
        </md-tab>
    </md-tabs>
</md-content>

<%--<script type="text/ng-template" id="doBootstrapModal.html">--%>
    <%--<md-dialog ng-cloak>--%>
        <%--<form>--%>
            <%--<md-toolbar>--%>
                <%--<div class="md-toolbar-tools">--%>
                    <%--<h2>{{parentScope.translate("bootstrap_doBootstrap")}}</h2>--%>
                    <%--<span flex></span>--%>
                    <%--<md-button class="md-icon-button" ng-click="cancel()">--%>
                        <%--<md-icon md-svg-src="resources/image/svg/close.svg" aria-label="Close dialog"></md-icon>--%>
                    <%--</md-button>--%>
                <%--</div>--%>
            <%--</md-toolbar>--%>
            <%--<md-dialog-content>--%>
                <%--<div class="md-dialog-content">--%>
                    <%--<div>--%>
                        <%--<md-input-container>--%>
                            <%--<label>{{parentScope.translate("bootstrap_domain")}}</label>--%>
                            <%--<input ng-model="bootstrap.domain">--%>
                        <%--</md-input-container>--%>
                    <%--</div>--%>
                    <%--<div>--%>
                        <%--<md-input-container>--%>
                            <%--<label>{{parentScope.translate("email")}}</label>--%>
                            <%--<input ng-model="bootstrap.email">--%>
                        <%--</md-input-container>--%>
                    <%--</div>--%>
                    <%--<div>--%>
                        <%--<md-input-container>--%>
                            <%--<label>{{parentScope.translate("password")}}</label>--%>
                            <%--<input ng-model="bootstrap.password">--%>
                        <%--</md-input-container>--%>
                    <%--</div>--%>
                    <%--<div>--%>
                        <%--<md-input-container>--%>
                            <%--<label>{{parentScope.translate("firstName")}}</label>--%>
                            <%--<input ng-model="bootstrap.firstName">--%>
                        <%--</md-input-container>--%>
                    <%--</div>--%>
                    <%--<div>--%>
                        <%--<md-input-container>--%>
                            <%--<label>{{parentScope.translate("lastName")}}</label>--%>
                            <%--<input ng-model="bootstrap.lastName">--%>
                        <%--</md-input-container>--%>
                    <%--</div>--%>
                <%--</div>--%>
            <%--</md-dialog-content>--%>
            <%--<md-dialog-actions layout="row">--%>
                <%--<md-button class="md-raised md-primary" ng-click="answer(bootstrap)">--%>
                    <%--{{parentScope.translate("doIt")}}--%>
                <%--</md-button>--%>
            <%--</md-dialog-actions>--%>
        <%--</form>--%>
    <%--</md-dialog>--%>
<%--</script>--%>

<%--Модальное окно для показа пользовательских фильтров--%>
<module-template name="dialogs/customFilter"></module-template>
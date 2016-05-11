<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" type="text/css" href="/resources/css/admin.css">

<div class="panel panel-default">
    <div class="panel-body">
        <span class="panel-item fa fa-3x fa-plus create" tooltip-placement="bottom"
              uib-tooltip="{{translate('es_create')}}" ng-click="showAdvanced($event)">
        </span>
        <span class="panel-item fa fa-3x fa-minus delete" tooltip-placement="bottom"
              uib-tooltip="{{translate('es_delete')}}" ng-click="showDeleteDialog($event)" ng-if="currentEStyle != null">
        </span>
    </div>
</div>

<div class="table-selector">
    <table class="table table-hover ">
        <caption>{{translate("es_title")}}</caption>
        <thead>
        <tr>
            <th ng-click="setOrder('id')">
                {{translate("id")}}
            </th>
            <th ng-click="setOrder('name')">
                {{translate("name")}}
            </th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="style in editorStyles | orderBy:order" class="selected-row"
            ng-click="setCurrentItem(style)">
            <td>{{style.id}}</td>
            <td>{{style.name}}</td>
        </tr>
        </tbody>
    </table>
</div>

<md-content>
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab label="{{translate('es_title')}}" ng-if="currentEStyle != null">
            <md-content class="md-padding">
                <p>
                    {{translate('id')}} : {{currentEStyle.id}} <br/>
                <div>
                    <md-input-container>
                        <label>{{translate('name')}}</label>
                        <input ng-model="currentEStyle.name">
                    </md-input-container>
                </div>
                <md-button class="md-raised md-primary" ng-click="updatePLanguage()" ng-disabled="!currentEStyle">{{translate("update")}}</md-button>
                </p>
            </md-content>
        </md-tab>
    </md-tabs>
</md-content>

<script type="text/ng-template" id="createES.html">
    <md-dialog  ng-cloak>
        <form>
            <md-toolbar>
                <div class="md-toolbar-tools">
                    <h2>{{parentScope.translate("es_create")}}</h2>
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
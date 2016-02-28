<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" type="text/css" href="/resources/css/admin.css">

<div class="panel panel-default">
    <div class="panel-body">
        <span class="panel-item fa fa-3x fa-plus create" tooltip-placement="bottom"
              uib-tooltip="{{translate('pl_create')}}" ng-click="showAdvanced($event)">
        </span>
        <span class="panel-item fa fa-3x fa-minus delete" tooltip-placement="bottom"
              uib-tooltip="{{translate('pl_delete')}}" ng-click="showDeleteDialog($event)" ng-if="currentPLanguage != null">
        </span>
    </div>
</div>

<div class="table-selector">
    <table class="table table-hover ">
        <caption>{{translate("pl_title")}}</caption>
        <thead>
        <tr>
            <th ng-click="setOrder('id')">
                {{translate("id")}}
            </th>
            <th ng-click="setOrder('name')">
                {{translate("name")}}
            </th>
            <th ng-click="setOrder('mode')">
                {{translate("pl_mode")}}
            </th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="planguage in planguages | orderBy:order" class="selected-row"
            ng-click="setCurrentItem(planguage)">
            <td>{{planguage.id}}</td>
            <td>{{planguage.name}}</td>
            <td>{{planguage.mode}}</td>
        </tr>
        </tbody>
    </table>
</div>

<md-content>
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab label="{{translate('pl_title')}}" ng-if="currentPLanguage != null">
            <md-content class="md-padding">
                <p>
                    {{translate('id')}} : {{currentPLanguage.id}} <br/>
                    <div>
                        <md-input-container>
                            <label>{{translate('name')}}</label>
                            <input ng-model="currentPLanguage.name">
                        </md-input-container>
                    </div>
                    <div>
                        <md-input-container>
                            <label>{{translate("pl_mode")}}</label>
                            <input ng-model="currentPLanguage.mode">
                        </md-input-container>
                    </div>
                    <md-button class="md-raised md-primary" ng-click="updatePLanguage()" ng-disabled="!currentPLanguage">{{translate("update")}}</md-button>
                </p>
            </md-content>
        </md-tab>
    </md-tabs>
</md-content>

<script type="text/ng-template" id="createPl.html">
    <md-dialog  ng-cloak>
        <form>
            <md-toolbar>
                <div class="md-toolbar-tools">
                    <h2>{{parentScope.translate("pl_create")}}</h2>
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
                            <input ng-model="pl.name">
                        </md-input-container>
                    </div>
                    <div>
                        <md-input-container>
                            <label>{{parentScope.translate("pl_mode")}}</label>
                            <input ng-model="pl.mode">
                        </md-input-container>
                    </div>
                </div>
            </md-dialog-content>
            <md-dialog-actions layout="row">
                <md-button class="md-raised md-primary" ng-click="answer(pl)">
                    {{parentScope.translate("create")}}
                </md-button>
            </md-dialog-actions>
        </form>
    </md-dialog>
</script>
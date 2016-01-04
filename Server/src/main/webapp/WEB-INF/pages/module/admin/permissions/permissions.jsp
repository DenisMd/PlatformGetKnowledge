<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" type="text/css" href="/resources/css/admin.css">

<div class="panel panel-default">
    <div class="panel-body">
        <span class="panel-item fa fa-3x fa-plus create" tooltip-placement="bottom"
              uib-tooltip="{{translate('permission_createPermission')}}" ng-click="showAdvanced($event)">
        </span>
        <span class="panel-item fa fa-3x fa-minus delete" tooltip-placement="bottom"
              uib-tooltip="{{translate('permission_deletePermission')}}" ng-click="showDeleteDialog($event)" ng-if="currentPermission != null">
        </span>
    </div>
</div>

<div class="table-selector">
    <table class="table table-hover ">
        <caption>{{translate("permission_title")}}</caption>
        <thead>
        <tr>
            <th ng-click="setOrder('id')">
                {{translate("id")}}
            </th>
            <th ng-click="setOrder('name')">
                {{translate("name")}}
            </th>
            <th>
                {{translate("permission_note")}}
            </th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="permission in permissions | orderBy:order" class="selected-row"
            ng-click="setCurrentItem(permission)">
            <td>{{permission.id}}</td>
            <td>{{permission.permissionName}}</td>
            <td>{{permission.note}}</td>
        </tr>
        </tbody>
    </table>
</div>

<md-content>
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab label="{{translate('permission')}}" ng-if="currentPermission != null">
            <md-content class="md-padding">
                <p>
                    {{translate('id')}} : {{currentPermission.id}} <br/>
                    {{translate('name')}} : {{currentPermission.permissionName}}       <br/>
                    <div class="form-group">
                        <label for="note">{{translate("permission_note")}}:</label>
                        <textarea class="form-control" rows="5" id="note" ng-model="currentPermission.note"></textarea>
                    </div>
                    <md-button class="md-raised md-primary" ng-click="updatePermission()" ng-disabled="!currentPermission">{{translate("permission_updatePermission")}}</md-button>
                </p>
            </md-content>
        </md-tab>
        <md-tab label="{{translate('users')}}" ng-if="permissionUsers != null && permissionUsers.length > 0">
            <table  class="table table-hover ">
                <thead>
                    <tr>
                        <th>{{translate("id")}}</th>
                        <th>{{translate("email")}}</th>
                    </tr>
                </thead>
                <tbody>
                    <tr ng-repeat="user in permissionUsers">
                        <td>{{user.id}}</td>
                        <td>{{user.login}}</td>
                    </tr>
                </tbody>
            </table>
        </md-tab>
        <md-tab label="{{translate('roles')}}" ng-if="permissionRoles != null && permissionRoles.length > 0">
            <table  class="table table-hover ">
                <thead>
                <tr>
                    <th>{{translate("id")}}</th>
                    <th>{{translate("name")}}</th>
                </tr>
                </thead>
                <tbody>
                <tr ng-repeat="user in permissionRoles">
                    <td>{{user.id}}</td>
                    <td>{{user.roleName}}</td>
                </tr>
                </tbody>
            </table>
        </md-tab>
    </md-tabs>
</md-content>

<script type="text/ng-template" id="createPermission.html">
    <md-dialog  ng-cloak>
        <form>
            <md-toolbar>
                <div class="md-toolbar-tools">
                    <h2>{{parentScope.translate("permission_createPermission")}}</h2>
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
                            <input ng-model="permission.permissionName">
                        </md-input-container>
                    </div>
                    <div>
                        <md-input-container>
                            <label>{{parentScope.translate("permission_note")}}</label>
                            <input ng-model="permission.note">
                        </md-input-container>
                    </div>
                </div>
            </md-dialog-content>
            <md-dialog-actions layout="row">
                <md-button class="md-raised md-primary" ng-click="answer(permission)">
                    {{parentScope.translate("create")}}
                </md-button>
            </md-dialog-actions>
        </form>
    </md-dialog>
</script>

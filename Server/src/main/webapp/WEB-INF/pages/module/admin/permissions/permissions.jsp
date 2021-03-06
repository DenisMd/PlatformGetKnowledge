<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/workflow/workflow.css">

<div class="selector-zone">
    <module-template name="selectors/clientSelector" data="selectorData"></module-template>
</div>

<md-content md-theme="darkTheme">
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab label="{{translate('permission')}}" ng-if="currentPermission != null">
            <md-content class="md-padding">
                <div layout="row">
                    <div flex="55" flex-gt-sm="20">{{translate('id')}}</div>
                    <div flex>{{currentPermission.id}}</div>
                </div>
                <div layout="row">
                    <div flex="55" flex-gt-sm="20">{{translate('name')}}</div>
                    <div flex>{{currentPermission.permissionName}}</div>
                </div>
                <div layout="row" class="margin-top-10">
                    <md-input-container class="md-block" flex>
                        <label>{{translate("permission_note")}}</label>
                        <textarea ng-model="currentPermission.note" md-maxlength="255" rows="3" md-select-on-focus></textarea>
                    </md-input-container>
                </div>
                <md-button class="md-raised md-primary md-btn" ng-click="updatePermission()" ng-disabled="!currentPermission">{{translate("permission_update_permission")}}</md-button>
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
                    <h2>{{parentScope.translate("permission_create_permission")}}</h2>
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
                            <input ng-model="permission.permissionName">
                        </md-input-container>
                    </div>
                    <div layout="row">
                        <md-input-container class="md-block" flex>
                            <label>{{parentScope.translate("permission_note")}}</label>
                            <textarea ng-model="permission.note" md-maxlength="255" rows="3" md-select-on-focus></textarea>
                        </md-input-container>
                    </div>
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

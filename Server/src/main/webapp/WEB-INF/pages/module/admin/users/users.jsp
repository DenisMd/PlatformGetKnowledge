<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/workflow/workflow.css">

<div class="selector-zone">
    <module-template name="selectors/serverSelector" data="selectorData"></module-template>
</div>


<md-content>
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab label="{{translate('user')}}" ng-if="currentUser != null">
            <md-content class="md-padding">
                <p>
                    {{translate('id')}} : {{currentUser.id}} <br/>
                    {{translate('name')}} : {{currentUser.lastName + ' ' + currentUser.firstName}} <br/>
                    {{translate("email")}} : {{currentUser.user.login}}<br/>
                    {{translate("user_create_date")}} : {{currentUser.user.createDate | date:'medium'}}<br/>
                    {{translate("user_blocked")}} : {{currentUser.user.blocked}}<br/>
                    {{translate("user_enabled")}} : {{currentUser.user.enabled}}<br/>
                    <module-template data="roleData" name="inputs/list"></module-template>
                </p>
                <md-button class="md-raised md-primary" ng-click="updateUser()" ng-disabled="!currentUser">{{translate("user_update")}}</md-button>
            </md-content>
        </md-tab>
        <md-tab label="{{translate('permissions')}}" ng-if="currentUser != null">
            <md-content>
                <p>
                    <a href="" ng-click="addNewPermission()">{{translate("user_add_new_permission")}}</a>
                    |
                    <a href="" ng-click="showDeleteColumn = !showDeleteColumn;">{{translate("user_remove_permission")}}</a>
                </p>
                <div ng-show="showAutoCompleteForRight">
                    <module-template data="permissionsData" name="inputs/list"></module-template>
                </div>
                <table class="table table-hover">
                    <thead>
                    <tr>
                        <th ng-show="showDeleteColumn">{{translate("user_remove_permission")}}</th>
                        <th>{{translate("id")}}</th>
                        <th>{{translate("name")}}</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr ng-repeat="permission in currentUser.user.permissions">
                        <td ng-show="showDeleteColumn"><span class="fa fa-times remove-icon" ng-click="removePermission(permission.id)"></span></td>
                        <td>{{permission.id}}</td>
                        <td>{{permission.permissionName}}</td>
                    </tr>
                    </tbody>
                </table>
                <md-button class="md-raised md-primary" ng-click="updateUser()" ng-disabled="!currentUser">{{translate("user_update_permissions")}}</md-button>
            </md-content>
        </md-tab>
    </md-tabs>
</md-content>
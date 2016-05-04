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
                    {{translate("user_createDate")}} : {{currentUser.user.createDate | date:'medium'}}<br/>
                    <module-template data="roleData" name="inputs/select"></module-template>
                    <md-switch ng-model="currentUser.user.enabled">
                        {{translate('user_enabled')}}
                    </md-switch>
                </p>
                <md-button class="md-raised md-primary" ng-click="updateUser()" ng-disabled="!currentUser">{{translate("user_update")}}</md-button>
            </md-content>
        </md-tab>
        <md-tab label="{{translate('permissions')}}" ng-if="currentUser != null">
            <md-content>
                <p>
                    <a href="" ng-click="addNewPermission()">{{translate("user_addNewPermission")}}</a>
                    |
                    <a href="" ng-click="showDeleteColumn = !showDeleteColumn;">{{translate("user_removePermission")}}</a>
                </p>
                <div ng-show="showAutoCompleteForRight">
                    <module-template data="permissionsData" name="inputs/select"></module-template>
                </div>
                <table class="table table-hover">
                    <thead>
                    <tr>
                        <th ng-show="showDeleteColumn">{{translate("user_removePermission")}}</th>
                        <th>{{translate("id")}}</th>
                        <th>{{translate("name")}}</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr ng-repeat="permission in currentUser.user.permissions">
                        <td ng-show="showDeleteColumn"><span class="fa fa-minus delete" ng-click="removePermission(permission.id)"></span></td>
                        <td>{{permission.id}}</td>
                        <td>{{permission.permissionName}}</td>
                    </tr>
                    </tbody>
                </table>
                <md-button class="md-raised md-primary" ng-click="updateUser()" ng-disabled="!currentUser">{{translate("user_updatePermissions")}}</md-button>
            </md-content>
        </md-tab>
    </md-tabs>
</md-content>
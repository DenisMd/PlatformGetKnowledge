<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" type="text/css" href="/resources/css/admin.css">

<div class="panel panel-default">
    <div class="panel-body">
        <span class="panel-item fa fa-3x fa-minus delete" tooltip-placement="bottom"
              uib-tooltip="{{translate('user_remove')}}" ng-click="showDeleteDialog($event)" ng-if="currentUser != null">
        </span>
        <md-input-container>
            <label>{{translate("user_findUser")}}</label>
            <input ng-model="searchTextField">

        </md-input-container>
        <md-button ng-click="searchUsers(searchTextField)" class="md-raised">{{translate("search")}}</md-button>
    </div>
</div>

<div class="table-selector">
    <table class="table table-hover ">
        <caption>{{translate("user_title")}} : {{countUsers + ' ' + translate("ofRecords")}}</caption>
        <thead>
        <tr>
            <th ng-click="setUserOrder('id')">
                {{translate("id")}}
            </th>
            <th>
                {{translate("user_role")}}
            </th>
            <th ng-click="setUserOrder('user.login')">
                {{translate("email")}}
            </th>
            <th>
                {{translate("name")}}
            </th>
            <th ng-click="setUserOrder('user.createDate')">
                {{translate("user_createDate")}}
            </th>
            <th ng-click="setUserOrder('user.enabled')">
                {{translate("user_enabled")}}
            </th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="userInfo in users" ng-click="setCurrentItem(userInfo)">
            <td>{{userInfo.id}}</td>
            <td>{{userInfo.user.role.roleName}}</td>
            <td>{{userInfo.lastName + ' ' + userInfo.firstName}}</td>
            <td>{{userInfo.user.login}}</td>
            <td>{{userInfo.user.createDate | date:'medium'}}</td>
            <td>{{userInfo.user.enabled}}</td>
        </tr>
        <tr>
            <td colspan="6" ng-click="loadMore()" class="loadMore">
                {{translate("user_loadMore")}}
            </td>
        </tr>
        </tbody>
    </table>
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
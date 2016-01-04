<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" type="text/css" href="/resources/css/admin.css">

<div class="panel panel-default">
    <div class="panel-body">
        <span class="panel-item fa fa-3x fa-plus create" tooltip-placement="bottom"
              uib-tooltip="{{translate('role_createRole')}}" ng-click="showAdvanced($event)">
        </span>
        <span class="panel-item fa fa-3x fa-minus delete" tooltip-placement="bottom"
              uib-tooltip="{{translate('role_deleteRole')}}" ng-click="showDeleteDialog($event)" ng-if="currentRole != null">
        </span>
    </div>
</div>

<div class="table-selector">
    <table class="table table-hover ">
        <caption>{{translate("roles_title")}}</caption>
        <thead>
        <tr>
            <th ng-click="setOrder('id')">
                {{translate("id")}}
            </th>
            <th ng-click="setOrder('name')">
                {{translate("name")}}
            </th>
            <th>
                {{translate("role_note")}}
            </th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="role in roles | orderBy:order" class="selected-row"
            ng-click="setCurrentItem(role)">
            <td>{{role.id}}</td>
            <td>{{role.roleName}}</td>
            <td>{{role.note}}</td>
        </tr>
        </tbody>
    </table>
</div>

<md-content>
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab label="{{translate('role')}}" ng-if="currentRole != null">
            <md-content class="md-padding">
                <p>
                    {{translate('id')}} : {{currentRole.id}} <br/>
                    {{translate('name')}} : {{currentRole.roleName}}       <br/>
                <div class="form-group">
                    <label for="note">{{translate("role_note")}}:</label>
                    <textarea class="form-control" rows="5" id="note" ng-model="currentRole.note"></textarea>
                </div>
                <md-button class="md-raised md-primary" ng-click="updateRole()" ng-disabled="!currentRole">{{translate("role_updateRole")}}</md-button>
                </p>
            </md-content>
        </md-tab>
    </md-tabs>
</md-content>


<script type="text/ng-template" id="createRole.html">
    <md-dialog  ng-cloak>
        <form>
            <md-toolbar>
                <div class="md-toolbar-tools">
                    <h2>{{parentScope.translate("role_createRole")}}</h2>
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
                            <input ng-model="role.roleName">
                        </md-input-container>
                    </div>
                    <div>
                        <md-input-container>
                            <label>{{parentScope.translate("role_note")}}</label>
                            <input ng-model="role.note">
                        </md-input-container>
                    </div>
                </div>
            </md-dialog-content>
            <md-dialog-actions layout="row">
                <md-button class="md-raised md-primary" ng-click="answer(role)">
                    {{parentScope.translate("create")}}
                </md-button>
            </md-dialog-actions>
        </form>
    </md-dialog>
</script>

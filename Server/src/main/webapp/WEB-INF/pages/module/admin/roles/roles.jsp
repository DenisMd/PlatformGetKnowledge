<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/workflow/workflow.css">

<div class="selector-zone">
    <module-template name="selectors/clientSelector" data="selectorData"></module-template>
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
                <md-button class="md-raised md-primary" ng-click="updateRole()" ng-disabled="!currentRole">{{translate("role_update_role")}}</md-button>
                </p>
            </md-content>
        </md-tab>
        <md-tab label="{{translate('permissions')}}" ng-if="currentRole != null">
            <md-content>
                <p>
                    <a href="" ng-click="addNewPermission()">{{translate("role_add_new_permission")}}</a>
                     |
                    <a href="" ng-click="showDeleteColumn = !showDeleteColumn;">{{translate("role_remove_permission")}}</a>
                </p>
                <div ng-show="showAutoCompleteForRight">
                    <module-template data="permissionsData" name="inputs/list"></module-template>
                </div>
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th ng-show="showDeleteColumn">{{translate("role_remove_permission")}}</th>
                            <th>{{translate("id")}}</th>
                            <th>{{translate("name")}}</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr ng-repeat="permission in currentRole.permissions">
                            <td ng-show="showDeleteColumn"><span class="fa fa-times remove-icon" ng-click="removePermission(permission.id)"></span></td>
                            <td>{{permission.id}}</td>
                            <td>{{permission.permissionName}}</td>
                        </tr>
                    </tbody>
                </table>
                <md-button class="md-raised md-primary" ng-click="updateRole()" ng-disabled="!currentRole">{{translate("role_update_role")}}</md-button>
            </md-content>
        </md-tab>
    </md-tabs>
</md-content>


<script type="text/ng-template" id="createRole.html">
    <md-dialog  ng-cloak>
        <form>
            <md-toolbar>
                <div class="md-toolbar-tools">
                    <h2>{{parentScope.translate("role_create_role")}}</h2>
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

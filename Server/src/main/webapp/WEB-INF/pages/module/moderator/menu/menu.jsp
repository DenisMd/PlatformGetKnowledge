<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="row">
    <div class="col-lg-4 col-md-4">
        <module-template data="treeViewListData" name="tree-view/treeList"></module-template>
    </div>
    <div class="col-lg-8 col-md-8" ng-if="currentMenuItem">
        <div ng-if="currentMenuItem.level == 1">
            <md-input-container>
                <label>{{translate("name")}}</label>
                <input ng-model="currentMenuItem.name">
            </md-input-container>
        </div>

        <div ng-if="!currentMenuItem.level">
            <div>
                <md-input-container>
                    <label>{{translate("name")}}</label>
                    <input ng-model="currentMenuItem.title">
                </md-input-container>
            </div>
            <div>
                <md-input-container>
                    <label>{{translate("menu_url")}}</label>
                    <input ng-model="currentMenuItem.url">
                </md-input-container>
            </div>
            <div>
                <md-input-container>
                    <label>{{translate("menu_icon_url")}}</label>
                    <input ng-model="currentMenuItem.iconUrl">
                </md-input-container>
            </div>
            <div>
                <md-input-container>
                    <label>{{translate("menu_color")}}</label>
                    <input ng-model="currentMenuItem.color">
                </md-input-container>
            </div>
        </div>
        <md-button class="md-raised md-primary" ng-click="updateMenu()">
            {{translate("update")}}
        </md-button>
    </div>
</div>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/moderator/menu.css">

<div class="row">
    <div class="col-lg-4 col-md-4">
        <ul>
            <li ng-repeat="menu in listMenu" class="menu-name">
                <span ng-click="setCurrentItem(menu , 1)">{{menu.name}}</span>
                <script type="text/ng-template" id="categoryTree">
                    <span ng-click="setCurrentItem(menuItem)">{{ menuItem.title }}</span>
                    <ul ng-if="menuItem.subItems" ng-show="menuItem.isOpen">
                        <li ng-repeat="menuItem in menuItem.subItems" ng-include="'categoryTree'" class="menu-subItem">
                        </li>
                    </ul>
                </script>
                <ul>
                    <li ng-repeat="menuItem in menu.items" ng-include="'categoryTree'" ng-show="menu.isOpen" class="menu-item"></li>
                </ul>
            </li>
        </ul>
    </div>
    <div class="col-lg-8 col-md-8">
        <div ng-if="currentMenuItem.level == 1">
            <md-input-container>
                <label>{{translate("name")}}</label>
                <input ng-model="currentMenuItem.name">
            </md-input-container>
        </div>

        <div ng-if="!currentMenuItem.level">
            <md-input-container>
                <label>{{translate("name")}}</label>
                <input ng-model="currentMenuItem.title">
            </md-input-container>
            <md-input-container>
                <label>{{translate("url")}}</label>
                <input ng-model="currentMenuItem.url">
            </md-input-container>
            <md-input-container>
                <label>{{translate("icon_url")}}</label>
                <input ng-model="currentMenuItem.iconUrl">
            </md-input-container>
            <md-input-container>
                <label>{{translate("color")}}</label>
                <input ng-model="currentMenuItem.color">
            </md-input-container>
        </div>
    </div>
</div>
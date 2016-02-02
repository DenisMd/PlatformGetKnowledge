<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<ul>
    <li ng-repeat="menu in listMenu">
        {{menu.name}}
        <script type="text/ng-template" id="categoryTree">
            {{ menuItem.title }}
            <ul ng-if="menuItem.subItems">
                <li ng-repeat="menuItem in menuItem.subItems" ng-include="'categoryTree'">
                </li>
            </ul>
        </script>
        <ul>
            <li ng-repeat="menuItem in menu.items" ng-include="'categoryTree'"></li>
        </ul>
    </li>
</ul>
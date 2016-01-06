<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" type="text/css" href="/resources/css/admin.css">

<div class="table-selector">
    <table class="table table-hover ">
        <caption>{{translate("user_title")}}</caption>
        <thead>
        <tr>
            <th ng-click="setOrder('id')">
                {{translate("id")}}
            </th>
            <th ng-click="setOrder('login')">
                {{translate("email")}}
            </th>
            <th ng-click="setOrder('createDate')">
                {{translate("user_createDate")}}
            </th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="user in users | orderBy:order" class="selected-row"
            ng-click="setCurrentItem(user)">
            <td>{{user.id}}</td>
            <td>{{user.login}}</td>
            <td>{{user.createDate | date:'medium'}}</td>
        </tr>
        </tbody>
    </table>
</div>
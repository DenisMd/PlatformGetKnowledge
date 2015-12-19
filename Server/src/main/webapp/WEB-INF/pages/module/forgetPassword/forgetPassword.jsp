<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/login.css">

<div class="panel panel-default">
    <div class="panel-body">
        {{translate('forgetMessage')}}
    </div>
</div>

<div class="form-group">
    <label for="inputEmail" class="control-label">{{translate("email")}}</label>
    <input type="text" class="form-control" id="inputEmail" placeholder="{{translate('email')}}" ng-model="email">
</div>
<div class="form-group login-btns">
    <button class="btn login-btn" ng-click="forgetPassword(email)">{{translate("restorePassword")}}</button>
</div>

{{resultForgetPassword}}
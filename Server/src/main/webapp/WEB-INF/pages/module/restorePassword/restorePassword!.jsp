<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/login.css">




<form class="center-form">
    <div class="alert alert-info text-center" role="alert">
        {{resultRestorePassword}}
    </div>
    <div class="form-group form-title">
        {{translate("restorePassword")}}
    </div>

    <div class="form-group">
        <label for="inputPassword" class="control-label">{{translate("password")}}</label>
        <input type="text" class="form-control" id="inputPassword" placeholder="{{translate('password')}}" ng-model="password">
    </div>
    <div class="form-group login-btns">
        <button class="btn login-btn login-btn-margin" ng-click="restorePassword(password)">{{translate("restorePassword")}}</button>
    </div>
</form>

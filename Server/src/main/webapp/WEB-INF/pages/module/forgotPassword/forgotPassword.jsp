<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/login.css">

<form class="center-form">
    <div ng-class="resultForgotPassword?'':'div-hidden'" class="alert alert-info text-center" role="alert">
        {{translate(resultForgotPassword)}}
    </div>
    <div class="form-group form-title">
        {{translate("forgotPassword")}}
    </div>

    <div class="form-group">
        <label for="inputEmail" class="control-label">{{translate("email")}}</label>
        <input type="text" class="form-control" id="inputEmail" placeholder="{{translate('email')}}" ng-model="email">
    </div>
    <div class="form-group login-btns">
        <button class="btn login-btn login-btn-margin" ng-click="forgotPassword(email)">{{translate("restorePassword")}}</button>
    </div>
</form>


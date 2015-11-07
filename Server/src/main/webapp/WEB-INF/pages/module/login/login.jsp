<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/login.css">

<div>
    <form class="login-form">
    <div class="form-group login-title">
        {{translate("signIn")}}
    </div>

        <div class="form-group">
            <label for="inputEmail" class="control-label">{{translate("email")}}</label>
            <input type="text" class="form-control" id="inputEmail" placeholder="Email" ng-model="info.login">
        </div>

        <div class="form-group">
            <label for="inputPassword" class="control-label">{{translate("password")}}</label>
            <input type="password" class="form-control" id="inputPassword" placeholder="Password" ng-model="info.password">
        </div>

        <div class="form-group">
            <label class="control-label"><a><span class="glyphicon glyphicon-question-sign" aria-hidden="true"></span> {{translate("forgotPassword")}}</a></label>
        </div>

        <div class="form-group login-btns">
            <button class="btn login-btn" ng-click="login()">{{translate("login")}}</button>
        </div>
        <div class="form-group text-center">
            {{translate("dontHaveAccount")}} <a ng-href="{{createUrl('/register')}}">{{translate("SignUp")}}</a>
        </div>

        <div ng-show="error" style="color:maroon" role="alert">
            <div ng-show="error">{{translate("Incorrect password or login")}}</div>
        </div>
    </form>
</div>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/authorize-forms/forms.css">

<div>
    <form class="center-form">
        <module-template name="components/infoMessage" data="loginError"></module-template>
        <div class="form-group form-title">
            {{translate("login_in")}}
        </div>

        <div class="form-group">
            <label for="inputEmail" class="control-label">{{translate("email")}}</label>
            <input type="text" class="form-control" id="inputEmail" placeholder="{{translate('email')}}" ng-model="loginData.login">
        </div>

        <div class="form-group">
            <label for="inputPassword" class="control-label">{{translate("password")}}</label>
            <input type="password" class="form-control" id="inputPassword" placeholder="{{translate('password')}}" ng-model="loginData.password">
        </div>

        <div class="form-group">
            <label class="control-label"><a ng-href="{{createUrl('/forgotPassword')}}"><span class="glyphicon glyphicon-question-sign" aria-hidden="true"></span> {{translate("login_forgot_password")}}</a></label>
        </div>

        <div class="form-group login-btns">
            <button class="btn login-btn" ng-click="login()">{{translate("login")}}</button>
        </div>
        <div class="form-group text-center">
            {{translate("login_dont_have_account")}} <a ng-href="{{createUrl('/register')}}">{{translate("login_up")}}</a>
        </div>
    </form>
</div>
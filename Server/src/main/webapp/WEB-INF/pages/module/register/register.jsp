<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/login.css">

<form class="login-form" name="registerForm">
    <div ng-class="error?'':'err-hidden'" class="alert alert-danger text-center" role="alert">
        Error
    </div>
    {{registerForm.$error}}
    <div class="form-group login-title">
        {{translate("signUp")}}
    </div>

    <div class="form-group">
        <label for="inputEmail" class="control-label">{{translate("email")}}</label>
        <input type="email" class="form-control" name="inputEmail" id="inputEmail" placeholder="Email" ng-model="info.email">
    </div>

    <div class="form-group">
        <label for="inputPassword" class="control-label">{{translate("password")}}</label>
        <input type="password" class="form-control" name="inputPassword" id="inputPassword" placeholder="Password" ng-model="info.password">
    </div>

    <div class="form-group">
        <label for="inputRepeatPassword" class="control-label">{{translate("repeatPassword")}}</label>
        <input type="password" use-validation="compareTo(value,anotherInput)" options="{{registerForm.inputPassword}}" class="form-control" id="inputRepeatPassword" placeholder="Repeat you password" ng-model="password">
    </div>

    <div class="form-group">
        <label for="inputFirstName" class="control-label">{{translate("FirstName")}}</label>
        <input type="password" class="form-control" id="inputFirstName" placeholder="First name" ng-model="info.firstName">
    </div>

    <div class="form-group">
        <label for="inputLastName" class="control-label">{{translate("LastName")}}</label>
        <input type="password" class="form-control" id="inputLastName" placeholder="Last name" ng-model="info.lastName">
    </div>

    <div class="form-group login-btns">
        <button class="btn login-btn" ng-click="signUp()">Register</button>
    </div>
</form>

{{registe}}
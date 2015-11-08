<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/login.css">

<form class="center-form" name="registerForm">
    <div ng-class="error?'':'err-hidden'" class="alert alert-danger text-center" role="alert">
        Error
    </div>
    <div class="form-group form-title">
        {{translate("signUp")}}
    </div>

    <div class="form-group">
        <label for="inputEmail" class="control-label">{{translate("email")}}</label>
        <input type="email" class="form-control input-lg" required name="inputEmail" id="inputEmail" placeholder="Email" ng-model="info.email">
    </div>

    <div class="form-group">
        <label for="inputPassword" class="control-label">{{translate("password")}}</label>
        <input type="password" class="form-control input-lg" required name="inputPassword" id="inputPassword" placeholder="Password" ng-model="info.password">
    </div>

    <div class="form-group">
        <label for="inputRepeatPassword" class="control-label">{{translate("repeatPassword")}}</label>
        <input type="password" use-validation="compareTo" required options="{'value':info.password}" class="form-control input-lg" id="inputRepeatPassword" placeholder="Repeat you password" ng-model="password">
    </div>

    <div class="form-group">
        <label for="inputFirstName" class="control-label">{{translate("FirstName")}}</label>
        <input type="text" class="form-control input-lg" required id="inputFirstName" placeholder="First name" ng-model="info.firstName">
    </div>

    <div class="form-group">
        <label for="inputLastName" class="control-label">{{translate("LastName")}}</label>
        <input type="text" class="form-control input-lg" required id="inputLastName" placeholder="Last name" ng-model="info.lastName">
    </div>

    <div class="form-group login-btns">
        <button class="btn login-btn" ng-click="signUp()" ng-disabled="registerForm.$invalid">Register</button>
    </div>
</form>

{{registe}}
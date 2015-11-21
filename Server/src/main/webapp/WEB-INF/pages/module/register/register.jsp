<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/login.css">

<form class="center-form" name="registerForm">
    <div ng-class="error?'':'err-hidden'" class="alert alert-danger text-center" role="alert">
        {{translate(registerInfo)}}
    </div>
    <div ng-show="registerInfo == 'Complete'" class="alert  alert-info text-center" role="alert">
        {{translate(registerInfo)}}
    </div>
    <div class="form-group form-title">
        {{translate("signUp")}}
    </div>

    <div class="form-group">
        <label for="inputEmail" class="control-label">{{translate("email")}}</label>
        <input type="email" class="form-control input-lg" required name="inputEmail" id="inputEmail" placeholder="{{translate('email')}}" ng-model="info.email">
    </div>

    <div class="form-group">
        <label for="inputPassword" class="control-label">{{translate("password")}}</label>
        <input type="password" class="form-control input-lg" ng-minlength="6" required name="inputPassword" id="inputPassword" placeholder="{{translate('password')}}" ng-model="info.password">
    </div>

    <div class="form-group">
        <label for="inputRepeatPassword" class="control-label">{{translate("repeatPassword")}}</label>
        <input type="password" use-validation="compareTo" required options="{'value':info.password}" class="form-control input-lg" id="inputRepeatPassword" placeholder='{{translate("repeatPassword")}}' ng-model="password">
    </div>

    <div class="form-group">
        <label for="inputFirstName" class="control-label">{{translate("firstName")}}</label>
        <input type="text" class="form-control input-lg" required id="inputFirstName" placeholder='{{translate("firstName")}}' ng-model="info.firstName">
    </div>

    <div class="form-group">
        <label for="inputLastName" class="control-label">{{translate("lastName")}}</label>
        <input type="text" class="form-control input-lg" required id="inputLastName" placeholder='{{translate("lastName")}}' ng-model="info.lastName">
    </div>

    <div class="form-group">
        <module-template data="languageData" name="inputs/select"></module-template>
    </div>

    <div class="form-group">
        <div class="register-switch">
            <input type="radio" ng-model="info.sex" name="sex" ng-value="true" id="male" class="register-switch-input" checked>
            <label for="male" class="register-switch-label">{{translate("male")}}</label>
            <input type="radio" ng-model="info.sex" name="sex"  ng-value="false" id="female" class="register-switch-input">
            <label for="female" class="register-switch-label">{{translate("female")}}</label>
        </div>
    </div>

    <div class="form-group login-btns">
        <button class="btn login-btn" ng-click="signUp()" ng-disabled="registerForm.$invalid">{{translate("register")}}</button>
    </div>
</form>
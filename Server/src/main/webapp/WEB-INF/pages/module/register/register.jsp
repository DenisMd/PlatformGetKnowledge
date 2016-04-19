<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/authorize-forms/forms.css">

<form class="center-form without-margin-top" name="registerForm">
    <module-template name="components/infoMessage" data="registerInfo"></module-template>
    <div class="form-group form-title">
        {{translate("register")}}
    </div>

    <div class="form-group">
        <label for="inputEmail" class="control-label">{{translate("email")}}</label>
        <input type="email" class="form-control input-lg on-error on-success" required name="inputEmail" id="inputEmail" placeholder="{{translate('email')}}" ng-model="info.email">
    </div>

    <div class="form-group">
        <label for="inputPassword" class="control-label">{{translate("password")}}</label>
        <input type="password" class="form-control input-lg on-error on-success" ng-minlength="6" required name="inputPassword" id="inputPassword" placeholder="{{translate('password')}}" ng-model="info.password">
    </div>

    <div class="form-group">
        <label for="inputRepeatPassword" class="control-label">{{translate("register_repeat_password")}}</label>
        <input type="password" use-validation="compareTo" required options="{'value':info.password}" class="form-control input-lg on-error on-success" id="inputRepeatPassword" placeholder='{{translate("register_repeat_password")}}' ng-model="password">
    </div>

    <div class="form-group">
        <label for="inputFirstName" class="control-label">{{translate("first_name")}}</label>
        <input type="text" class="form-control input-lg  on-error on-success" required id="inputFirstName" placeholder='{{translate("first_name")}}' ng-model="info.firstName">
    </div>

    <div class="form-group">
        <label for="inputLastName" class="control-label">{{translate("last_name")}}</label>
        <input type="text" class="form-control input-lg on-error on-success" required id="inputLastName" placeholder='{{translate("last_name")}}' ng-model="info.lastName">
    </div>

    <div class="form-group">
        <label class="control-label">Выберите язык</label><br/>
        <module-template data="languageData" name="inputs/list"></module-template>
    </div>

    <div class="form-group">
        <label class="label-block">Ваш пол</label>
        <span class="radio-inline">
            <input type="radio" ng-model="info.sex" name="sex" ng-value="true" id="male" checked>
            <label for="male">{{translate("male")}}</label>
        </span>
        <span class="radio-inline">
            <input type="radio" ng-model="info.sex" name="sex"  ng-value="false" id="female">
            <label for="female">{{translate("female")}}</label>
        </span>
    </div>

    <div class="form-group login-btns">
        <button class="btn login-btn" ng-click="signUp()" ng-disabled="registerForm.$invalid">{{translate("register_do")}}</button>
    </div>
</form>
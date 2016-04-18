<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/authorize-forms/forms.css">

<form class="center-form">
    <module-template name="components/infoMessage" data="forgotError"></module-template>
    <div class="form-group form-title">
        {{translate("forgot_password")}}
    </div>

    <div class="form-group">
        <label for="inputEmail" class="control-label">{{translate("email")}}</label>
        <input type="text" class="form-control" id="inputEmail" placeholder="{{translate('email')}}" ng-model="email">
    </div>
    <div class="form-group login-btns">
        <button class="btn login-btn login-btn-margin" ng-click="forgotPassword(email)">{{translate("forgot_restore")}}</button>
    </div>
</form>


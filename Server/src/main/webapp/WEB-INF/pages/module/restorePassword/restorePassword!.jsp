<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/authorize-forms/forms.css">

<form class="center-form">
    <module-template name="components/infoMessage" data="restorePasswordInfo"></module-template>
    <div class="form-group form-title">
        {{translate("restore_password")}}
    </div>

    <div class="form-group">
        <label for="inputPassword" class="control-label">{{translate("password")}}</label>
        <input type="text" class="form-control" id="inputPassword" placeholder="{{translate('password')}}" ng-model="password">
    </div>
    <div class="form-group login-btns">
        <button class="btn login-btn login-btn-margin" ng-click="restorePassword(password)">{{translate("restore_password")}}</button>
    </div>
</form>

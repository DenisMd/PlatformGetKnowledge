<div class="form-group">
    <label for="inputPassword" class="control-label">{{translate("password")}}</label>
    <input type="password" class="form-control" id="inputPassword" placeholder="{{translate('password')}}" ng-model="password">
</div>
<div class="form-group login-btns">
    <button class="btn login-btn" ng-click="restorePassword(password)">{{translate("restorePassword")}}</button>
</div>
{{password}}
{{resultRestorePassword}}
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/login.css">

<div>
    <div ng-class="error?'':'err-hidden'" class="alert alert-danger text-center" role="alert">
        Error
    </div>
    <form class="login-form">
    <div class="form-group login-title">
        Sign in
    </div>

        <div class="form-group">
            <label for="inputEmail" class="control-label">Email</label>
            <input type="text" class="form-control" id="inputEmail" placeholder="Email" ng-model="info.login">
        </div>

        <div class="form-group">
            <label for="inputPassword" class="control-label">Password</label>
            <input type="password" class="form-control" id="inputPassword" placeholder="Password" ng-model="info.password">
        </div>

        <div class="form-group">
            <label class="control-label"><a><span class="glyphicon glyphicon-question-sign" aria-hidden="true"></span> Forgot your password?</a></label>
        </div>

        <div class="form-group login-btns">
            <button class="btn login-btn" ng-click="login()">Войти</button>
        </div>
        <div class="form-group text-center">
            Don't have an account? <a ng-href="{{createUrl('/register')}}">Sign up</a>
        </div>
    </form>
</div>
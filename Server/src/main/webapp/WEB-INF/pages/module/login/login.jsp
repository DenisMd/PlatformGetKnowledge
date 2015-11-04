<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/login.css">



<div class="login-form">
    Get knowledge : {{loginResult}}
</div>

email : <input type="text" ng-model="test.email"><br/>
password : <input type="password" ng-model="test.password"><br/>

{{test}}
<br/>
<button class="btn btn-primary" ng-click="login(test.email, test.password)">Войти</button>

<a ng-href="{{createUrl('/register')}}">Register</a>
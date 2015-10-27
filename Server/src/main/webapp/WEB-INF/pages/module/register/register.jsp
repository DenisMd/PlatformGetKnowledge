<%@ page contentType="text/html;charset=UTF-8" language="java" %>

Register page

Email : <input type="text" ng-model="r.email"/><br/>
Password: <input type="password" ng-model="r.password"/><br/>
FirstName : <input type="text" ng-model="r.name"/><br/>
LastName : <input type="text" ng-model="r.name2"/><br/>
{{r}}<br/>

<button class="btn btn-primary" ng-click="register(r.email,r.password,r.name,r.name2)">Register</button><br/>
{{registerResult}}
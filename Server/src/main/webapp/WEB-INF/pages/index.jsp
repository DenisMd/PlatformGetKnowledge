<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html ng-app="mainApp" ng-controller="mainController">
<head>
    <title>Index</title>
    <link rel="stylesheet" href="/resources/bower_components/bootstrap/dist/css/bootstrap.css">
    <link rel="stylesheet" href="/resources/css/index.css">
</head>
<body>
    <module-template name="menus/mainMenu" data="{{menu}}"></module-template>
    <div class="wrapper">
        <div ui-view></div>
        <p>text:<span ng-bind-html="application.text.language"></span></p><br/>
        Main menu : {{menu}}
    </div>

    <script src="/resources/bower_components/jquery/dist/jquery.min.js"></script>
    <script src="/resources/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
    <script src="/resources/bower_components/angular/angular.min.js"></script>
    <script src="/resources/bower_components/angular-ui-router/release/angular-ui-router.min.js"></script>
    <script src="/resources/bower_components/angular-sanitize/angular-sanitize.min.js"></script>
    <script src="/resources/application/platform.js"></script>
    <script src="/resources/application/getKnowledge.js"></script>

    <c:forEach items="${scripts}" var="src">
        <script src="<c:out value="${src}"></c:out>"></script>
    </c:forEach>

</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html ng-app="mainApp" ng-controller="mainController">
<head>
    <title>Index</title>
    <link rel="stylesheet" href="/resources/bower_components/bootstrap/dist/css/bootstrap.css" type="text/css">
    <link rel="stylesheet" href="/resources/bower_components/malihu-custom-scrollbar-plugin/jquery.mCustomScrollbar.min.css" type="text/css"/>
    <link rel="stylesheet" href="/resources/bower_components/video.js/dist/video-js.css" type="text/css"/>
    <link rel="stylesheet" href="/resources/css/index.css" type="text/css">
</head>
<body>
    <header class="header-info">
        <div class="header-item header-left">
            <button class="menu-toggle" ng-click="toggelClick()"><span class="hamburger">&#9776;</span></button>
            <div class="site-title">getKnowledge();</div>
        </div>
        <div class="header-item header-right">
            <div class="header-item language-panel"><a ng-class="application.language=='ru'?'selected':''"
                                                       ng-click="changeLanguage('ru')">Ru</a>/<a
                    ng-class="application.language=='en'?'selected':''" ng-click="changeLanguage('en')">En</a></div>
        </div>
        <div class="header-item header-center"></div>
    </header>

    <module-template name="menus/mainMenu" data="menu"></module-template>
    <div class="wrapper wrapper-left" id="wrapper">
            <div ui-view></div>
    </div>

    <script src="/resources/bower_components/jquery/dist/jquery.min.js"></script>
    <script src="/resources/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
    <script src="/resources/bower_components/angular/angular.min.js"></script>
    <script src="/resources/bower_components/angular-ui-router/release/angular-ui-router.min.js"></script>
    <script src="/resources/bower_components/angular-sanitize/angular-sanitize.min.js"></script>
    <script src="/resources/bower_components/angular-animate/angular-animate.min.js"></script>
    <script src="/resources/bower_components/angular-bootstrap/ui-bootstrap.min.js"></script>
    <script src="/resources/bower_components/angular-bootstrap/ui-bootstrap-tpls.min.js"></script>
    <script src="/resources/bower_components/malihu-custom-scrollbar-plugin/jquery.mCustomScrollbar.concat.min.js"></script>
    <script src="/resources/bower_components/ng-scrollbars/dist/scrollbars.min.js"></script>
    <script src="/resources/bower_components/video.js/dist/video.min.js"></script>
    <script src="/resources/application/platform.js"></script>
    <script src="/resources/application/getKnowledge.js"></script>

    <c:forEach items="${scripts}" var="src">
        <script src="<c:out value="${src}"></c:out>"></script>
    </c:forEach>

</body>
</html>

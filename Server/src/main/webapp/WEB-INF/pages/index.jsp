<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html ng-app="mainApp" ng-controller="mainController">
<head>
    <title>Index</title>
    <link href='https://fonts.googleapis.com/css?family=PT+Sans:400,700&subset=latin,cyrillic' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="/resources/bower_components/bootstrap/dist/css/bootstrap.css" type="text/css">
    <link rel="stylesheet" href="/resources/bower_components/malihu-custom-scrollbar-plugin/jquery.mCustomScrollbar.min.css" type="text/css"/>
    <link rel="stylesheet" href="/resources/bower_components/video.js/dist/video-js.min.css" type="text/css"/>
    <link rel="stylesheet" href="/resources/bower_components/angular-loading-bar/build/loading-bar.css" type="text/css"/>
    <link rel="stylesheet" href="/resources/css/index.css" type="text/css">
    <link rel="stylesheet" type="text/css" href="/resources/bower_components/ng-img-crop/compile/minified/ng-img-crop.css">
    <meta charset="utf-8">
</head>
<body>

    <header class="header-info">
        <div class="header-item header-left">
            <button class="menu-toggle" ng-click="toggelClick()"><span class="hamburger">&#9776;</span></button>
            <div class="site-title"><a ng-href="{{createUrl('')}}">getKnowledge();</a></div>
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
    <script src="/resources/bower_components/angular-file-upload/dist/angular-file-upload.min.js"></script>
    <script src="/resources/bower_components/angular-loading-bar/build/loading-bar.min.js"></script>
    <script src="/resources/bower_components/angular-bootstrap/ui-bootstrap.min.js"></script>
    <script src="/resources/bower_components/angular-bootstrap/ui-bootstrap-tpls.min.js"></script>
    <script src="/resources/bower_components/malihu-custom-scrollbar-plugin/jquery.mCustomScrollbar.concat.min.js"></script>
    <script src="/resources/bower_components/ng-scrollbars/dist/scrollbars.min.js"></script>
    <script src="/resources/bower_components/video.js/dist/video.min.js"></script>
    <script src="/resources/bower_components/ng-img-crop/compile/minified/ng-img-crop.js"></script>
    <%--<script src="/resources/bower_components/videojs-hotkeys/videojs.hotkeys.min.js"></script>--%>
    <script src="/resources/application/platform.js"></script>
    <script src="/resources/application/getKnowledge.js"></script>

    <c:forEach items="${scripts}" var="src">
        <script src="<c:out value="${src}"></c:out>"></script>
    </c:forEach>

</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html ng-app="mainApp" ng-controller="mainController">
<head>
    <title>GetKnwoledge</title>

    <%--Шрифт--%>
    <link href='https://fonts.googleapis.com/css?family=PT+Sans:400,700&subset=latin,cyrillic' rel='stylesheet'
          type='text/css'>

    <%--External css--%>
    <link rel="stylesheet" href="/resources/bower_components/bootstrap/dist/css/bootstrap.min.css" type="text/css">
    <link rel="stylesheet" href="/resources/bower_components/malihu-custom-scrollbar-plugin/jquery.mCustomScrollbar.min.css" type="text/css"/>
    <link rel="stylesheet" href="/resources/bower_components/video.js/dist/video-js.min.css" type="text/css"/>
    <link rel="stylesheet" href="/resources/bower_components/angular-loading-bar/build/loading-bar.min.css" type="text/css"/>
    <link rel="stylesheet" href="/resources/bower_components/font-awesome/css/font-awesome.min.css">
    <link rel="stylesheet" href="/resources/bower_components/angular-material/angular-material.min.css">
    <link rel="stylesheet" href="/resources/bower_components/angular-chart.js/dist/angular-chart.min.css">
    <link rel="stylesheet" href="/resources/bower_components/ng-img-crop/compile/minified/ng-img-crop.css" type="text/css">
    <link rel="stylesheet" href="/resources/bower_components/codemirror/lib/codemirror.css" type="text/css">
    <link rel="stylesheet" href="/resources/node_modules/angular-bootstrap-datetimepicker/src/css/datetimepicker.css" type="text/css">

    <%--Наши css--%>
    <link rel="stylesheet" type="text/css" href="/resources/css/main/index.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/main/loader-bar.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/main/menu.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/main/scroll.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/main/video.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/main/graphics.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/main/editor.css">

    <%--Inputs--%>
    <link rel="stylesheet" type="text/css" href="/resources/css/main/inputs/image/select-image.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/main/inputs/files/select-files.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/main/inputs/list/list-input.css">

    <%--Dist css for production--%>
    <%--<link rel="stylesheet" type="text/css" href="/resources/dist/css/main.min.css">--%>

    <%--Simple comments--%>
    <link rel="stylesheet" type="text/css" href="/resources/css/comments/simple-comment.css">

    <%--Beatu--%>
    <link rel="stylesheet" type="text/css" href="/resources/css/beauty/btns/btns.css">

    <link rel="shortcut icon" type="image/x-icon" href="/resources/image/favicon.ico"/>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
</head>
<body layout-fill layout="column" ng-cloak>

    <module-template name="components/header" data="headerData"></module-template>
    <module-template name="components/mainMenu" data="menuData"></module-template>

    <md-content>
        <div class="wrapper wrapper-main-content" id="wrapper">
            <div ui-view></div>
        </div>
    </md-content>

    <%--Модальное окно для показа видео--%>
    <module-template name="dialogs/videoDialog"></module-template>
    <%--Модальное окно для показа списка--%>
    <module-template name="dialogs/listDialog"></module-template>
    <!--Модальное окно для показа пользовательских фильтров-->
    <module-template name="dialogs/customFilter"></module-template>

    <script src="/resources/dist/js/ext/external-libs.min.js" type="text/javascript"></script>
    <%--<script src="/resources/dist/js/main-application.min.js" type="text/javascript"></script>--%>

    <script src="/resources/application/platform.js"></script>
    <script src="/resources/application/getKnowledge.js"></script>

    <c:forEach items="${scripts}" var="src">
        <script src="<c:out value="${src}"></c:out>"></script>
    </c:forEach>

</body>
</html>

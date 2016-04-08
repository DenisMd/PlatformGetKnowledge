<%@ page contentType="text/html;charset=UTF-8" language="java" %>

{{newsList}}

<button ng-click = "loadNews()">Загрузить еще</button>
<input type="text" ng-model="news.title">
<input type="text" ng-model="news.message">
{{news}}
<button ng-click = "createNews()">Создать</button>



<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/section.css">

<h1 class="text-center">{{translate(book.name)}}</h1>

<img ng-src="{{bookImg()}}"
     class="cover-img">

<p class="description">
  {{book.description}}
</p>


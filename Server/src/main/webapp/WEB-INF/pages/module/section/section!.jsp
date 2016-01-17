<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/section.css">

<h1 class="text-center">{{translate(section.name)}}</h1>

<img ng-src="{{sectionImg(section.id)}}"
    class="cover-img">

<p class="description">
    {{application.language == 'ru' ? section.descriptionRu : section.descriptionEn}}
</p>

<module-template name="cards/cards1" data="sectionCards"></module-template>
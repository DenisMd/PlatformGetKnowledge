<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/courses/group.css">

<h1 class="title">{{translate("courses_title")}}</h1>


<md-content class="md-padding" layout-xs="column" layout="row">
    <div flex-xs flex-gt-xs="50" layout="column">
        <md-card ng-repeat="gc in splitArray(groupCourses,true)">
            <img  ng-src="{{coursesImg(gc.id)}}" class="md-card-image" alt="Washed Out">
            <md-card-title>
                <md-card-title-text>
                    <span class="md-headline">{{gc.title}}</span>
                </md-card-title-text>
            </md-card-title>
            <md-card-content>
                <p>
                    {{gc["description"+application.language.capitalizeFirstLetter()]}}
                </p>
            </md-card-content>
            <md-card-actions layout="row" layout-align="end center">
                <md-button>Перейти</md-button>
            </md-card-actions>
        </md-card>
    </div>
    <div flex-xs flex-gt-xs="50" layout="column">
        <md-card  ng-repeat="gc in splitArray(groupCourses,false)">
            <img  ng-src="{{coursesImg(gc.id)}}" class="md-card-image" alt="Washed Out">
            <md-card-title>
                <md-card-title-text>
                    <span class="md-headline">{{gc.title}}</span>
                </md-card-title-text>
            </md-card-title>
            <md-card-content>
                <p>
                    {{gc["description"+application.language.capitalizeFirstLetter()]}}
                </p>
            </md-card-content>
            <md-card-actions layout="row" layout-align="end center">
                <md-button>Перейти</md-button>
            </md-card-actions>
        </md-card>
    </div>
</md-content>
<md-content layout="row"  layout-align="center center">
    <span ng-click="loadMore()" class="loadMore">{{translate("courses_load_more")}}</span>
</md-content>

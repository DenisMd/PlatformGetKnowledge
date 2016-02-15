<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<md-card ng-repeat="gc in groupCourses">
    <md-card-title>
        <md-card-title-text>
            <span class="md-headline">{{gc.title}}</span>
            <span class="md-subhead">{{gc["description"+application.language.capitalizeFirstLetter()]}}</span>
        </md-card-title-text>
        <md-card-title-media>
            <div class="md-media-lg card-media">
                <img ng-src="{{coursesImg(gc.id)}}">
            </div>
        </md-card-title-media>
    </md-card-title>
    <md-card-actions layout="row" layout-align="end center">
        <md-button>Перейти</md-button>
    </md-card-actions>
</md-card>
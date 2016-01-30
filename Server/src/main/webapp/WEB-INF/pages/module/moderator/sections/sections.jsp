<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" type="text/css" href="/resources/css/admin.css">

<div class="table-selector">
    <table class="table table-hover ">
        <caption>{{translate("sections")}}</caption>
        <thead>
        <tr>
            <th ng-click="setOrder('id')">
                {{translate("id")}}
            </th>
            <th ng-click="setOrder('name')">
                {{translate("name")}}
            </th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="section in sections | orderBy:order" class="selected-row"
            ng-click="setCurrentItem(section)">
            <td>{{section.id}}</td>
            <td>{{section.name}}</td>
        </tr>
        </tbody>
    </table>
</div>

<md-content>
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab label="{{translate('section_info')}}" ng-if="currentSection != null">
            <md-content class="md-padding">
                <p>
                    {{translate('id')}} : {{currentSection.id}} <br/>
                    {{translate('name')}} : {{currentSection.name}}<br/>
                    <module-template name="inputs/multilanguage" data="multiLanguageData"></module-template>
                    <md-button class="md-raised md-primary" ng-click="updateSections()" ng-disabled="!currentSection">{{translate("update")}}</md-button>
                </p>
            </md-content>
        </md-tab>
        <md-tab label="{{translate('section_image')}}" ng-if="currentSection != null">
            <md-content class="md-padding">
                <module-template name="inputs/selectImage" data="getCropImageData(currentSection)"></module-template>
            </md-content>
        </md-tab>
    </md-tabs>
</md-content>

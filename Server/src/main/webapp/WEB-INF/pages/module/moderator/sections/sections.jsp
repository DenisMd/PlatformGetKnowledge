<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/workflow/workflow.css">

<div class="selector-zone">
    <module-template name="selectors/clientSelector" data="selectorData"></module-template>
</div>

<md-content>
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab label="{{translate('section_info')}}" ng-if="currentSection != null">
            <md-content class="md-padding">
                <div layout="row">
                    <div flex-gt-sm="20" flex="auto">{{translate('id')}}</div>
                    <div flex>{{currentSection.id}}</div>
                </div>
                <div layout="row">
                    <div flex-gt-sm="20" flex="auto">{{translate('name')}}</div>
                    <div flex>{{currentSection.name}}</div>
                </div>
                <module-template name="inputs/multilanguage" data="multiLanguageData"></module-template>
                <md-button class="md-raised md-primary md-btn" ng-click="updateSections()" ng-disabled="!currentSection">{{translate("update")}}</md-button>
            </md-content>
        </md-tab>
        <md-tab label="{{translate('section_image')}}" ng-if="currentSection != null">
            <md-content class="md-padding">
                <md-content class="md-padding">
                    <module-template name="inputs/selectImage" data="croppedImg"></module-template>
                </md-content>
            </md-content>
        </md-tab>
    </md-tabs>
</md-content>

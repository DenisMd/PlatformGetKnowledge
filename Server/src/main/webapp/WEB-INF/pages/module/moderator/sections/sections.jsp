<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/workflow/workflow.css">

<div class="selector-zone">
    <module-template name="selectors/clientSelector" data="selectorData"></module-template>
</div>

<md-content>
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab ng-if="currentSection != null">
            <md-tab-label>
                {{translate('section_info')}}
            </md-tab-label>
            <md-tab-body>
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
            </md-tab-body>
        </md-tab>
        <md-tab ng-if="currentSection != null">
            <md-tab-label>
                {{translate('section_image')}}
            </md-tab-label>
            <md-tab-body>
                <md-content class="md-padding">
                    <module-template name="inputs/selectImage" data="getCropImageData()"></module-template>
                </md-content>
            </md-tab-body>
        </md-tab>
    </md-tabs>
</md-content>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/workflow/workflow.css">

<md-content>
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab>
            <md-tab-label>
                {{translate('folder_group_courses')}}
            </md-tab-label>
            <md-tab-body>
                <div class="selector-zone">
                    <module-template name="selectors/serverSelector" data="selectorData3"></module-template>
                </div>
            </md-tab-body>
        </md-tab>
        <md-tab>
            <md-tab-label>
                {{translate('folder_group_books')}}
            </md-tab-label>
            <md-tab-body>
                <div class="selector-zone">
                    <module-template name="selectors/serverSelector" data="selectorData1"></module-template>
                </div>
            </md-tab-body>
        </md-tab>
        <md-tab>
            <md-tab-label>
                {{translate('folder_group_programs')}}
            </md-tab-label>
            <md-tab-body>
                <div class="selector-zone">
                    <module-template name="selectors/serverSelector" data="selectorData2"></module-template>
                </div>
            </md-tab-body>
        </md-tab>
    </md-tabs>
</md-content>

<md-content md-theme="darkTheme">
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab label="{{translate('folder_info')}}" ng-if="currentGroup != null">
            <md-content class="md-padding">
                <div layout="row" class="field">
                    <div flex="55" flex-gt-sm="20">{{translate('id')}}</div>
                    <div flex>{{currentGroup.id}}</div>
                </div>
                <div layout="row" class="field">
                    <div flex="55" flex-gt-sm="20">{{translate('section')}}</div>
                    <div flex>
                        {{translate(currentGroup.section.name)}}
                    </div>
                </div>
                <div layout="row" class="field">
                    <div flex="55" flex-gt-sm="20">{{translate('name')}}</div>
                    <div flex>
                        <input type="text" class="form-control" ng-model="currentGroup.title">
                    </div>
                </div>
                <div layout="row" class="field">
                    <div flex="55" flex-gt-sm="20">{{translate("folder_url")}}</div>
                    <div flex>
                        <input type="text" class="form-control" ng-model="currentGroup.url">
                    </div>
                </div>
                <module-template name="inputs/multilanguage" data="multiLanguageData"></module-template>
                <md-button class="md-raised md-primary md-btn" ng-click="updateGroup()" ng-disabled="!currentGroup">{{translate("update")}}</md-button>
            </md-content>
        </md-tab>
        <md-tab label="{{translate('folder_image')}}" ng-if="currentGroup != null">
            <md-content class="md-padding">
                <md-content class="md-padding">
                    <module-template name="inputs/selectImage" data="croppedImg"></module-template>
                </md-content>
            </md-content>
        </md-tab>
    </md-tabs>
</md-content>


<script type="text/ng-template" id="createFolder.html">
    <md-dialog  ng-cloak aria-label="options dialog">
            <md-toolbar>
                <div class="md-toolbar-tools" >
                    <h2>{{parentScope.translate("folder_create")}}</h2>
                    <span flex></span>
                    <md-button class="md-icon-button" ng-click="cancel()">
                        <md-icon md-svg-src="resources/image/svg/close.svg" aria-label="Close dialog"></md-icon>
                    </md-button>
                </div>
            </md-toolbar>
            <md-dialog-content  layout-padding>
                <div class="md-dialog-content">
                    <div layout="row">
                        <md-input-container flex>
                            <label>{{parentScope.translate("name")}}</label>
                            <input ng-model="group.title">
                        </md-input-container>
                    </div>
                    <div layout="row">
                        <md-input-container flex>
                            <label>{{parentScope.translate("Url")}}</label>
                            <input ng-model="group.url">
                        </md-input-container>
                    </div>
                    <div layout="row">
                        <md-input-container flex>
                        <label>{{parentScope.translate("section")}}</label>
                        <md-select ng-model="group.sectionId">
                            <md-option ng-repeat="section in parentScope.sections" value="{{section.id}}">
                                {{parentScope.translate(section.name)}}
                            </md-option>
                        </md-select>
                        </md-input-container>
                    </div>
                </div>
            </md-dialog-content>
            <md-dialog-actions layout="row">
                <md-button class="md-raised md-primary" ng-click="answer(group)">
                    {{parentScope.translate("create")}}
                </md-button>
            </md-dialog-actions>
    </md-dialog>
</script>

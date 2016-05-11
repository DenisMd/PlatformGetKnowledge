<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/workflow/workflow.css">

<div class="selector-zone">
  <module-template name="selectors/clientSelector" data="selectorData"></module-template>
</div>


<md-content>
  <md-tabs md-dynamic-height md-border-bottom>
    <md-tab label="{{translate('socialLinks')}}" ng-if="currentLink != null">
      <md-content class="md-padding">
        <p>
          {{translate('id')}} : {{currentLink.id}} <br/>
          {{translate('name')}} : {{currentLink.roleName}}       <br/>
          <md-input-container class="md-block" flex-gt-sm>
            <label>{{translate("link")}}</label>
            <input ng-model="currentLink.link">
          </md-input-container>
        <md-button class="md-raised md-primary" ng-click="updateSocialLink()" ng-disabled="!currentLink">{{translate("socialLinks_update")}}</md-button>
        </p>
      </md-content>
    </md-tab>
  </md-tabs>
</md-content>

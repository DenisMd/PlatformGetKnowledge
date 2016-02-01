<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" type="text/css" href="/resources/css/admin.css">

<div class="table-selector">
  <table class="table table-hover ">
    <caption>{{translate("socialLinks")}}</caption>
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
    <tr ng-repeat="link in links | orderBy:order" class="selected-row"
        ng-click="setCurrentItem(link)">
      <td>{{link.id}}</td>
      <td>{{link.name}}</td>
    </tr>
    </tbody>
  </table>
</div>

<md-content>
  <md-tabs md-dynamic-height md-border-bottom>
    <md-tab label="{{translate('role')}}" ng-if="currentLink != null">
      <md-content class="md-padding">
        <p>
          {{translate('id')}} : {{currentLink.id}} <br/>
          {{translate('name')}} : {{currentLink.roleName}}       <br/>
          <md-input-container class="md-block" flex-gt-sm>
            <label>{{translate("link")}}</label>
            <input ng-model="currentLink.link">
          </md-input-container>
        <md-button class="md-raised md-primary" ng-click="updateRole()" ng-disabled="!currentLink">{{translate("socialLinks_update")}}</md-button>
        </p>
      </md-content>
    </md-tab>
  </md-tabs>
</md-content>

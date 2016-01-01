<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" type="text/css" href="/resources/css/selector-table.css">

<md-toolbar layout="row">
    <h1>{{translate("bootstrap_title")}}</h1>
</md-toolbar>

<div class="table-selector">
    <table class="table table-hover ">
        <caption>{{translate("bootstrap_services")}}</caption>
        <thead>
        <tr>
            <th>
                {{translate("id")}}
            </th>
            <th>
                {{translate("name")}}
            </th>
            <th>
                {{translate("bootstrapState")}}
            </th>
            <th>
                {{translate("order")}}
            </th>
            <th>
                {{translate("repeat")}}
            </th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="service in bootstrap_services" class="selected-row"
            ng-click="setCurrentItem(service)">
            <td>{{service.id}}</td>
            <td>{{service.name}}</td>
            <td>{{service.bootstrapState}}</td>
            <td>{{service.order}}</td>
            <td>{{service.repeat}}</td>
        </tr>
        </tbody>
    </table>
</div>

<md-content>
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab label="{{translate('service_info')}}">
            <md-content class="md-padding">
                <h1 class="md-display-2">{{translate('service_info')}}</h1>

                <p>
                    {{translate('id')}} : {{currentService.id}} <br/>
                    {{translate('name')}} : {{currentService.name}}       <br/>
                    {{translate('order')}} : {{currentService.order}}     <br/>
                    {{translate('repeat')}} : {{currentService.repeat}}   <br/>
                    <md-switch ng-model="currentService.repeat">
                        {{translate('repeat')}}
                    </md-switch>
                    <md-button class="md-raised md-primary" ng-click="updateService()" ng-disabled="!currentService">{{translate("update")}}</md-button>
                </p>
            </md-content>
        </md-tab>
    </md-tabs>
</md-content>


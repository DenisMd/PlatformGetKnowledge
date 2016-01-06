<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/user.css">
<div class="user-content">
    <div class="col-sm-5 user-menu-content">
        <div class="user-menu">
            <div class="user-info-card">
                <img ng-src="{{userImg(user_info.id)}}" class="img-circle" />
            </div>
            <div ng-show="user_info.online" class="text-center">
                <span class="circle-status circle-online"></span> {{translate("Online")}}
            </div>
            <div ng-hide="user_info.online" class="text-center">
                <span class="circle-status circle-offline"></span> {{translate("Offline")}}
            </div>
            <p class="user-name text-center">{{user_info.firstName}} {{user_info.lastName}}</p>

            <p class="user-speciality text-center">{{user_info.specialty}}</p>

            <div class="user-status text-center">
                <module-template data="statusText" name="inputs/editableTextarea"></module-template>
            </div>
            <p class="text-center">
                <a href="https://www.google.ru">google.ru</a>
            </p>
            <p class="link-card user-link text-center">
                <span class=" col-xs-3"><i class="fa fa-vk"></i></span>
                <span class=" col-xs-3"><i class="fa fa-facebook"></i></span>
                <span class=" col-xs-3"><i class="fa fa-twitter"></i></span>
                <span class=" col-xs-3"><i class="fa fa-git"></i></span>

            </p>

            <ul class="list-group user-nav">
                <li  class="list-group-item" ng-repeat="menuItem in user_info.userMenu.items">
                    <a ng-href="{{createUrl(menuItem.url)}}">{{translate(menuItem.title)}}</a>
                </li>
            </ul>
        </div>
    </div>
    <div class="col-sm-7">
        {{user_info}}
    </div>



</div>


<div class="modal fade" id="userModal">
    <div class="modal-dialog first-form-modal">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">{{translate("extraRegister")}}</h4>
            </div>
            <div class="modal-body first-form-body" ng-scrollbars ng-scrollbars-config="modalScrollConfig">
                <form name="firsTimeForm" style="position: relative;">
                    <div class="form-group">
                        <label class="control-label">{{translate("country_select")}}</label>
                        <module-template data="countryData" name="inputs/select"></module-template>
                    </div>
                    <div class="form-group">
                        <label class="control-label">{{translate("region_select")}}</label>
                        <module-template data="regionData" name="inputs/select"></module-template>
                    </div>
                    <div class="form-group">
                        <label class="control-label">{{translate("city_select")}}</label>
                        <module-template data="cityData" name="inputs/select"></module-template>
                    </div>
                    <div class="form-group">
                        <label class="control-label">{{translate("birthday_select")}} dd.mm.yyyy</label>
                        <module-template data="dateData" name="inputs/datepicker"></module-template>
                    </div>
                    <div class="form-group">
                        <label class="control-label">{{translate("specialty_select")}}</label>
                        <input class="form-control" type="text" ng-model="speciality" ng-maxlength="40">
                    </div>
                </form>
            </div>

            <div class="modal-footer modal-find">
                <button type="button" class="btn btn-primary" ng-disabled="firsTimeForm.$invalid" ng-click="save()">
                    {{translate("save")}}
                </button>
                <button type="button" class="btn btn-default" ng-click="closeModal()">{{translate("skip")}}</button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->



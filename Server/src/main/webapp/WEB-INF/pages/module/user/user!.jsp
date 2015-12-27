<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/user.css">
<div class="user-content">
    <div class="col-sm-3 user-menu-content">
        <div class="user-photo">
            <img ng-src="{{userImg(user_info.id)}}" class="main-image user-image"/>
        </div>
        <div class="user-menu">
            <div class="">
                <p class="main-label">{{user_info.firstName}} {{user_info.lastName}}</p>

                <p class="info-about-user">{{user_info.specialty}}</p>
            </div>
            <div ng-show="user_info.online">
                <span class="circle-status circle-online"></span>
                Online
            </div>
            <div>
                <module-template data="statusText" name="inputs/editableTextarea"></module-template>
            </div>
        </div>
    </div>
    <div class="col-sm-9">
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



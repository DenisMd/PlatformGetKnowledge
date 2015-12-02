<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/user.css">
User page
{{user_info}}

<div class="modal fade" id="userModal">
    <div class="modal-dialog first-form-modal">
        <div class="modal-content">
            <div class="modal-body first-form-body">
                страна регион город
                <form name="firsTimeForm" style="position: relative;">
                    <div class="form-group">
                        <label class="control-label">{{translate("country")}}</label>
                        <module-template data="countryData" name="inputs/select"></module-template>
                    </div>
                    <div class="form-group">
                        <label class="control-label">{{translate("region")}}</label>
                        <module-template data="regionData" name="inputs/select"></module-template>
                    </div>
                    <div class="form-group">
                        <label class="control-label">{{translate("city")}}</label>
                        <module-template data="cityData" name="inputs/select"></module-template>
                    </div>
                </form>
            </div>
            <div class="modal-footer modal-find">
                <button type="button" class="btn btn-primary" ng-disabled="true" ng-click="saveModalModel()">Save</button>
                <button type="button" class="btn btn-danger" ng-click="closeModal()">Cancel</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
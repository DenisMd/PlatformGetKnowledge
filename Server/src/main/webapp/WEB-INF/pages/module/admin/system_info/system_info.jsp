<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<md-toolbar class="md-warn">
    <div class="md-toolbar-tools">
        <h2 class="md-flex">{{translate("system_info")}}</h2>
    </div>
</md-toolbar>
<md-content flex layout-padding>
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab label="{{translate('system_settingsInfo')}}">
            <md-content class="md-padding">
                <p>
                    {{translate("id") + " : " + settings.id}}<br/>
                    {{translate("email") + " : " + settings.email}}<br/>
                    {{translate("version") + " : " + settings.version}}<br/>
                    {{translate("system_domain") + " : " + settings.domain}}<br/>
                </p>
            </md-content>
        </md-tab>
        <md-tab label="{{translate('system_services')}}">
            <md-content class="md-padding">
                <module-template data="treeViewListData" name="tree-view/treeList"></module-template>
            </md-content>
        </md-tab>
    </md-tabs>
</md-content>.
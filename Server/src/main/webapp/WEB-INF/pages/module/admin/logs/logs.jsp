<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/workflow/workflow.css">

<div class="selector-zone">
    <module-template name="selectors/serverSelector" data="selectorData"></module-template>
</div>

<md-content>
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab label="{{translate('log_stack_trace')}}" ng-if="currentLog != null && currentLog.stackTrace != null">
            <module-template name="components/stacktrace" data="stackTraceData"></module-template>
        </md-tab>
    </md-tabs>
</md-content>



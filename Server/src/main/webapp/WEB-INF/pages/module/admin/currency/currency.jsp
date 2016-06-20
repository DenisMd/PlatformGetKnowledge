<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/workflow/workflow.css">

<div class="selector-zone">
  <module-template name="selectors/clientSelector" data="selectorData"></module-template>
</div>

<md-content md-theme="darkTheme">
  <md-tabs md-dynamic-height md-border-bottom>
    <md-tab label="{{translate('currency_info')}}" ng-if="currentCurrency != null">
      <md-content class="md-padding">
        <div layout="row">
          <div flex="55" flex-gt-sm="20">{{translate('id')}}</div>
          <div flex>{{currentCurrency.id}}</div>
        </div>
        <div layout="row">
          <div flex="55" flex-gt-sm="20">{{translate('name')}}</div>
          <div flex>{{translate('name')}}</div>
        </div>
        <div layout="row">
          <div flex="55" flex-gt-sm="20">{{translate('currency_char_code')}}</div>
          <div flex>{{currentCurrency.charCode}}</div>
        </div>
        <div layout="row">
          <div flex="55" flex-gt-sm="20">{{translate('currency_is_base')}}</div>
          <div flex>{{currentCurrency.baseCurrency}}</div>
        </div>
        <div layout="row">
          <div flex="55" flex-gt-sm="20">{{translate('currency_value')}}</div>
          <div flex>{{currentCurrency.value}}</div>
        </div>
      </md-content>
    </md-tab>
  </md-tabs>
</md-content>
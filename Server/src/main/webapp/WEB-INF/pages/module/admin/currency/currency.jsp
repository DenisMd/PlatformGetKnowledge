<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/workflow/workflow.css">

<div class="selector-zone">
  <module-template name="selectors/clientSelector" data="selectorData"></module-template>
</div>

<md-content>
  <md-tabs md-dynamic-height md-border-bottom>
    <md-tab label="{{translate('currency_info')}}" ng-if="currentCurrency != null">
      <md-content class="md-padding">
        <p>
          {{translate('id')}}                     : {{currentCurrency.id}}             <br/>
          {{translate('name')}}                   : {{currentCurrency.name}}           <br/>
          {{translate('currency_char_code')}}     : {{currentCurrency.charCode}}       <br/>
          {{translate('currency_is_base')}}       : {{currentCurrency.baseCurrency}}   <br/>
          {{translate('currency_value')}}         : {{currentCurrency.value}}          <br/>
        </p>
      </md-content>
    </md-tab>
  </md-tabs>
</md-content>
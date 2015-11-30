<link href="/resources/css/accept.css" rel="stylesheet">
<div class="info accepted" ng-show="result == 'Complete'">
    <div class="glyphicon glyphicon-ok"></div><{{uuid}}><br>
    {{translate(result)}} <a ng-href="{{createUrl('/login')}}">{{translate('loginPage')}}</a>
</div>

<div class="info not-accepted" ng-hide="result == 'Complete'">
    <div class="glyphicon glyphicon-remove"></div><{{uuid}}><br>
    {{translate(result)}}
</div>

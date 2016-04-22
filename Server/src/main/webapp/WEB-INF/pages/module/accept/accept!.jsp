<link href="/resources/css/accept.css" rel="stylesheet">

<!-- Add or remove "with-glare" class. Removing the glare makes the screen interactive -->

<div class="css-mb with-glare">
    <div class="mb-display-position">
        <div class="mb-display">
            <div class="mb-screen-position">
                <div class="mb-screen">

                    <div class="info accepted"  ng-show="result == 'Complete'">
                        <div class="glyphicon glyphicon-ok"></div><{{uuid}}><br>
                        {{translate(result)}} <a ng-href="{{createUrl('/login')}}">{{translate('loginPage')}}</a>
                    </div>

                    <div class="info not-accepted" ng-hide="result == 'Complete'">
                        <div class="glyphicon glyphicon-remove"></div><{{uuid}}><br>
                        {{translate(result)}}
                    </div>

                </div>
            </div>
        </div>
    </div>
    <div class="mb-body"></div>
    <div class="mb-bottom-cover"></div>
</div>
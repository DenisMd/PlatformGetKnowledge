<link href="/resources/css/accept/accept.css" rel="stylesheet">

<!-- Add or remove "with-glare" class. Removing the glare makes the screen interactive -->

<div class="css-mb with-glare">
    <div class="mb-display-position">
        <div class="mb-display">
            <div class="mb-screen-position">
                <div class="mb-screen">

                    <div class="info accepted"  ng-show="result === 'complete'">
                        <div class="glyphicon glyphicon-ok"></div><{{uuid}}><br>
                        {{translate('accept_' + result)}} <a ng-href="{{createUrl('/login')}}">{{translate('accept_login_page')}}</a>
                    </div>

                    <div class="info not-accepted" ng-hide="result === 'complete'">
                        <div class="glyphicon glyphicon-remove"></div><{{uuid}}><br>
                        {{translate('accept_' + result)}}
                    </div>

                </div>
            </div>
        </div>
    </div>
    <div class="mb-body"></div>
    <div class="mb-bottom-cover"></div>
</div>
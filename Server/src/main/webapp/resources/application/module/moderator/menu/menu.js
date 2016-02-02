model.controller("menuCtrl", function ($scope,applicationService,className) {

    applicationService.list($scope,"listMenu",className.menu);
});
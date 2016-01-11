model.controller("system_infoCtrl", function ($scope, applicationService, className) {

    applicationService.read($scope,"settings",className.settings,1);

    applicationService.list($scope,"systemServices",className.systemServices)
});
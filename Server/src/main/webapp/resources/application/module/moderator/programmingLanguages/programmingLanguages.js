model.controller("programmingLanguagesCtrl", function ($scope,applicationService,className,$mdDialog,$mdMedia) {

    applicationService.list($scope , "planguages",className.programmingLanguages);

    $scope.setCurrentItem = function (item) {
        $scope.currentPLanguage = item;
    };

    $scope.updatePLanguage = function() {
        applicationService.update($scope,"",className.programmingLanguages,$scope.currentPLanguage,function(result){
            $scope.showToast(result);
        });
    };

    $scope.showAdvanced = function(ev) {
        $scope.showDialog(ev,$scope,"createPl.html",function(answer){
            applicationService.create($scope,"", className.programmingLanguages,answer,function(result){
                $scope.showToast(result);
                applicationService.list($scope , "planguages", className.programmingLanguages);
            });
        });
    };

    $scope.showDeleteDialog = function(ev) {
        var confirm = $mdDialog.confirm()
            .title($scope.translate("pl_delete") + " " + $scope.currentPLanguage.name)
            .textContent("")
            .targetEvent(ev)
            .ariaLabel('Delete pl')
            .ok($scope.translate("delete"))
            .cancel($scope.translate("cancel"));
        $mdDialog.show(confirm).then(function() {
            applicationService.remove($scope,"",className.programmingLanguages,$scope.currentPLanguage.id,function (result) {
                $scope.showToast(result);
                applicationService.list($scope , "planguages", className.programmingLanguages);
            });
        });
    };


});
model.controller("editorStylesCtrl", function ($scope,applicationService,className,$mdDialog,$mdMedia) {

    applicationService.list($scope , "editorStyles",className.programmingStyles);

    $scope.setCurrentItem = function (item) {
        $scope.currentEStyle = item;
    };

    $scope.updatePLanguage = function() {
        applicationService.update($scope,"",className.programmingStyles,$scope.currentEStyle,function(result){
            $scope.showToast(result);
        });
    };

    $scope.showAdvanced = function(ev) {
        $scope.showDialog(ev,$scope,"createES.html",function(answer){
            applicationService.create($scope,"", className.programmingStyles,answer,function(result){
                $scope.showToast(result);
                applicationService.list($scope , "editorStyles", className.programmingStyles);
            });
        });
    };

    $scope.showDeleteDialog = function(ev) {
        var confirm = $mdDialog.confirm()
            .title($scope.translate("es_delete") + " " + $scope.currentEStyle.name)
            .textContent("")
            .targetEvent(ev)
            .ariaLabel('Delete pl')
            .ok($scope.translate("delete"))
            .cancel($scope.translate("cancel"));
        $mdDialog.show(confirm).then(function() {
            applicationService.remove($scope,"",className.programmingStyles,$scope.currentEStyle.id,function (result) {
                $scope.showToast(result);
                applicationService.list($scope , "editorStyles", className.programmingStyles);
            });
        });
    };

});
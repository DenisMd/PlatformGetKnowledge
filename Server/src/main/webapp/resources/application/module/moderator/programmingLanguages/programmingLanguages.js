model.controller("programmingLanguagesCtrl", function ($scope,applicationService,className,$mdDialog,$mdMedia) {


    function programmingLanguagesList() {
        $scope.selectorData.list = [];
        applicationService.list($scope , "planguages",className.programmingLanguages , function(item){
            $scope.selectorData.list.push(item);
        });
    }

    $scope.selectorData = {
        list        : [],
        tableName   :   "pl_title",
        filters      : [
            {
                title : "name",
                type  : "text",
                field : "name",
                default : true
            },{
                title : "pl_mode",
                type  : "text",
                field : "mode",
                default : true
            }
        ],
        headerNames : [
            {
                name : "id",
                orderBy : true
            },
            {
                name : "name",
                title : "name",
                orderBy : true
            },
            {
                name : "mode",
                title : "pl_mode",
                orderBy : true
            }
        ],
        selectItemCallback : function (item) {
            $scope.currentPLanguage = item;
        },
        actions : [
            {
                icon : "fa-plus",
                color : "#46BE28",
                tooltip : "pl_create",
                actionCallback : function (ev){
                    $scope.showDialog(ev,$scope,"createPl.html",function(answer){
                        applicationService.create($scope,"", className.programmingLanguages,answer,function(result){
                            $scope.showToast($scope.getResultMessage(result));
                            programmingLanguagesList();
                        });
                    });
                }
            }
        ],
        deleteOptions : {
            deleteCallback : function (ev,item) {
                $scope.showConfirmDialog(
                    ev,
                    $scope.translate("pl_delete") + " " + item.name,
                    null,
                    'Delete pl',
                    $scope.translate("delete"),
                    $scope.translate("cancel"),
                    function () {
                        applicationService.remove($scope,"",className.programmingLanguages,$scope.currentPLanguage.id,function (result) {
                            $scope.showToast($scope.getResultMessage(result));
                            programmingLanguagesList();
                            $scope.currentPLanguage = null;
                        });
                    }
                )
            },
            deleteTitle : "pl_delete"
        }
    };

    programmingLanguagesList();

    $scope.updatePLanguage = function() {
        applicationService.update($scope,"",className.programmingLanguages,$scope.currentPLanguage,function(result){
            $scope.showToast($scope.getResultMessage(result));
        });
    };


});
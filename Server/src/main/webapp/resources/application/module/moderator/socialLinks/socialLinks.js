model.controller("socialLinksCtrl", function ($scope,applicationService,className) {

    function updateSocialLinks(){
        applicationService.list($scope, "links", className.socialLinks , function(item) {
            $scope.selectorData.list.push(item);
        });
    }

    updateSocialLinks();


    $scope.selectorData = {
        list        : [],
        tableName   :   "socialLinks",
        filters      : [
            {
                title : "name",
                type  : "text",
                field : "name",
                default : true
            },{
                title : "id",
                type  : "number",
                field : "id"
            }
        ],
        headerNames : [
            {
                name : "id",
                orderBy : true
            },
            {
                name : "name",
                orderBy : true
            }
        ],
        selectItemCallback : function (item) {
            $scope.currentLink = item;
        }
    };

    $scope.updateSocialLink = function(){
        applicationService.update($scope,"links",className.socialLinks,$scope.currentLink,function(result){
            $scope.showToast($scope.getResultMessage(result));
        });
    };


});
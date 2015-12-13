model.controller("usersCtrl", function ($scope, $state,$http,applicationService,pageService,className) {
    $scope.usersSelector = {
        title : "user_title",
        columns : ["id", "login", "createDate"],
        content : [],
        tabs : [{
            title : "user",
            columns : [{name : "login" , "type" : "string"},{name : "createDate" , "type" : "string"}],
            readOnly : true
        }]
    };

    applicationService.list($scope , "services",className.users , function(permission){
        $scope.usersSelector.content.push(permission);
    });
});
model.controller("usersCtrl", function ($scope, $state,$http,applicationService,pageService,className) {
    $scope.usersSelector = {
        title : "user_title",
        columns : ["id", "email", "createDate"],
        content : [],
        filters : [null,null,"date:medium"],
        callback : function (item) {
            $scope.editorData.item = item;
        }
    };

    $scope.editorData = {
        item : null,
        tabs : [{
            title : "user",
            columns : [{name : "email" , "type" : "string" , disabled : true},{name : "createDate" , "type" : "string", disabled : true}],
            readOnly : true,
            className : className.users
        }]
    };

    applicationService.list($scope ,"users",className.users , function(user){
        var tmpUser = user;
        tmpUser.email = user.login;
        delete user.login;
        $scope.usersSelector.content.push(tmpUser);
    });
});
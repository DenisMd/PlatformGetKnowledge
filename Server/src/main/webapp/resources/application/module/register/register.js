model.controller("registerCtrl", function ($scope, $http,applicationService,className) {
    $scope.info = {
        "sex" : true
    };

    // Информация о доступных языках
    $scope.languageData = {
        "count"         :  3,
        "titleField"    : "title",
        "classForInput" : "input-group-lg",
        "listName"      : "systemLanguages",
        "required"      : true,
        "callback"  : function (value){
            $scope.info.language = value.name;
        }
    };

    applicationService.list($scope,"systemLanguages",className.language, function (item) {
        item.title = $scope.translate(item.name.toLowerCase());
    });

    $scope.signUp = function() {
        if ($scope.registerForm.$invalid) {
            return;
        }
        applicationService.action($scope,"registerInfo",className.userInfo,"register",$scope.info, function(registerInfo) {
            if (registerInfo === 'Complete') {
                $scope.registerInfo = {
                    message : $scope.translate("register_complete"),
                    type : 'success'
                };
            } else if (registerInfo === 'PasswordLessThan6'){
                $scope.registerInfo = {
                    message : $scope.translate("register_password_less_than_6"),
                    type : 'danger'
                };
            }  else if (registerInfo === 'UserAlreadyCreated'){
                $scope.registerInfo = {
                    message : $scope.translate("register_already_created"),
                    type : 'danger'
                };
            } else if (registerInfo === 'LanguageNotSupported'){
                $scope.registerInfo = {
                    message : $scope.translate("register_language_not_found"),
                    type : 'danger'
                };
            } else if (registerInfo === 'EmailNotSend'){
                $scope.registerInfo = {
                    message : $scope.translate("register_email_failed"),
                    type : 'danger'
                };
            }
            //Переместить скролл в самый вверх страници для просмотра ссобщения
            $('html,body').scrollTop(0);
        });
    };
});

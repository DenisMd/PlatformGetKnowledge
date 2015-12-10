model.controller("userCtrl", function ($scope, $state,$http,applicationService,pageService,className) {
    var userId = pageService.getPathVariable("user",$state.params.path);
    if (userId) {
        applicationService.read($scope, "user_info" , className.userInfo, userId, function(item){
            if ($scope.user && $scope.user.id === parseInt(userId, 10)) {
                if (item.firstLogin) {
                    applicationService.action($scope, "countriesList", className.country, "getCountries", {language: $scope.application.language.capitalizeFirstLetter()});
                    $("#userModal").modal({
                        backdrop: 'static',
                        keyboard: false
                    });
                    $("#userModal").modal('show');
                }
            }
        });
    }
    $scope.closeModal = function(){
        $("#userModal").modal('hide');
    };


    //данные для select
    var isCountryValid = false;
    var isRegionValid = false;
    var isRegionDisable = true;
    var isCityDisable = true;


    $scope.countryData = {
        "id" : "country",
        "count" : 3,
        "filter":"countryName",
        "listName" : "countriesList",
        //"required" : true,
        "maxHeight" : 300,
        "isValid" : function(value){
            isCountryValid = value;
        },
        "callback" : function (value){
            $scope.country = value;
            $scope.$broadcast('reset' + $scope.cityData.id.capitalizeFirstLetter() + 'Event');
            $scope.$broadcast('reset' + $scope.regionData.id.capitalizeFirstLetter() + 'Event');
            applicationService.action($scope, "regionsList", className.region, "chooseRegionsByCountry", {countryId:value.id,language : $scope.application.language.capitalizeFirstLetter()});
            isRegionDisable = false;
            isCityDisable = true;
        }
    };

    $scope.regionData = {
        "id" : "region",
        "count" : 3,//
        "filter":"regionName",
        "listName" : "regionsList",
        //"required" : true,//
        "maxHeight" : 300,//
        "disable" : function(){
            return !isCountryValid || isRegionDisable;
        },
        "isValid" : function(value){
            isRegionValid = value;
        },
        "callback" : function (value){
            $scope.region = value;
            $scope.$broadcast('reset' + $scope.cityData.id.capitalizeFirstLetter() + 'Event');
            applicationService.action($scope, "citiesList", className.city, "getCitiesByRegion", {regionId:value.id,language : $scope.application.language.capitalizeFirstLetter()});
            isCityDisable = false;
        }
    };

    $scope.cityData = {
        "id" : "city",
        "count" : 3,
        "filter":"cityName",
        "listName" : "citiesList",
        //"required" : true,
        "maxHeight" : 300,
        "disable" : function(){
            return !isCountryValid || !isRegionValid || isCityDisable;
        },
        "callback" : function (value){
            $scope.city = value;
        }
    };


    $scope.dateData = {
        onChange: function(date){
            $scope.date = date;
        }
    };


    //данные для image
    $scope.imageLoad = {
        id : "image-loud",
        save : function(data){
            $scope.image = data;
        }
    };

    //
    $scope.speciality = "";
    //сохранение
    $scope.save = function(){
        if ($scope.user) {
            var data = {
                userId: $scope.user.id
            };
            if ($scope.city) data.cityId = $scope.city.id;
            if ($scope.speciality) data.speciality = $scope.speciality;
            if ($scope.date) data.date = $scope.date;

            applicationService.actionWithFile($scope, "status", className.userInfo, "updateExtraInfo", data, $scope.image);
        }
    }

});
model.controller("userCtrl", function ($scope, $state,$http,applicationService,pageService,arcService,className) {
    var userId = pageService.getPathVariable("user",$state.params.path);

    if (userId) {
        pageService.setOnLogout(function(){
            init();
        });
        init();
        function init() {
            applicationService.read($scope, "user_info", className.userInfo, userId, function (item) {
                $scope.statusText.text = item.status;
                if ($scope.user && $scope.user.id === parseInt(userId, 10)) {
                    $scope.statusText.onSave = function (text) {
                        applicationService.action($scope, "updateStatus", className.userInfo, "updateStatus", {status: text}, function (item) {
                            if (item === "Complete") {
                                applicationService.read($scope, "user_info", className.userInfo, userId, function (item) {
                                    $scope.statusText.text = item.status;
                                });
                            }
                        });
                    };
                    if (item.firstLogin) {
                        applicationService.list($scope, "countriesList", className.country);
                        $("#userModal").modal({
                            backdrop: 'static',
                            keyboard: false
                        });
                        $("#userModal").modal('show');
                    }


                } else {
                    $scope.statusText.readonly = true;
                }
            });
        }
    }
    $scope.closeModal = function(){
        applicationService.action($scope,"skipResult",className.userInfo,"skipExtraRegistration",{});
        $("#userModal").modal('hide');
    };

    //данные для status
    $scope.statusText = {};


    //основной контент
    //данные для диаграмм
    $scope.dataForArcs = [{
        id : 1,
        title : "Java",
        percent : 80,
        colour : "#FF2B2B"
    }, {
        id : 2,
        title : "Photoshop",
        percent : 70,
        colour : "#8000FF"
    },{
        id : 3,
        title : "JavaScript",
        percent : 90,
        colour : "#FFCC00"
    } ];
    $scope.arcOptions = arcService;
    //scroll для диаграмм курсов
    $scope.arcScrollConfig = angular.merge({axis:"x"}, $scope.modalScrollConfig);

    //статистика
    $scope.labels = ["January", "February", "March", "April", "May", "June", "July"];
    $scope.series = ['A'];
    $scope.data = [
        [65, 59, 80, 81, 56, 55, 40]
    ];
    $scope.dataCircle = [65, 59, 80, 81, 56, 55, 40];




    //modal
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
        "maxHeight" : 300,
        "isValid" : function(value){
            isCountryValid = value;
        },
        required: false,
        "callback": function(value){
            $scope.country = value;
            $scope.$broadcast('reset' + $scope.cityData.id.capitalizeFirstLetter() + 'Event');
            $scope.$broadcast('reset' + $scope.regionData.id.capitalizeFirstLetter() + 'Event');
            applicationService.action($scope, "regionsList", className.region, "chooseRegionsByCountry", {countryId:value.id});
            isRegionDisable = false;
            isCityDisable = true;
        }
    };

    $scope.regionData = {
        "id" : "region",
        "count" : 3,//
        "filter":"regionName",
        "listName" : "regionsList",
        "maxHeight" : 300,//
        "disable" : function(){
            return !isCountryValid || !$scope.country || isRegionDisable;
        },
        "required" : function(){
            return $scope.country;
        },
        "isValid" : function(value){
            isRegionValid = value;
        },
        "callback" : function (value){
            $scope.region = value;
            $scope.$broadcast('reset' + $scope.cityData.id.capitalizeFirstLetter() + 'Event');
            applicationService.action($scope, "citiesList", className.city, "getCitiesByRegion", {regionId:value.id});
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
            return !isCountryValid || !isRegionValid  || !$scope.country  || !$scope.region  || isCityDisable;
        },
        "required" : function(){
            return $scope.country;
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
        save : function(file){
            applicationService.actionWithFile($scope,"updateImage",className.userInfo,"updateImage",{},file);
        }
    };



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

            applicationService.action($scope, "updateExtraInfo", className.userInfo, "updateExtraInfo", data, function(item){
                if (item === "Complete"){
                    $scope.user.specialty = data.speciality;
                    $("#userModal").modal('hide');
                }
            });
        }
    }

});

//model.directive("containerScrollablle", function(){
//    return {
//        link:function(scope,element){
//            scope.$watch(function(){return element.width()}, function (newValue, oldValue) {
//                element.find("a").width(newValue / 3);
//            });
//        }
//    }
//});

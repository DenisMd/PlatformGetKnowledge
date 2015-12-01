model.controller("userCtrl", function ($scope, $state,$http,applicationService,pageService,className) {
    var userId = pageService.getPathVariable("user",$state.params.path);
    if (userId) {
        applicationService.read($scope, "user_info" , className.userInfo, userId, function(){
            applicationService.action($scope, "countriesList", className.country, "getCountries", {language : $scope.application.language.capitalizeFirstLetter()});
            $("#userModal").modal({
                backdrop: 'static',
                keyboard: false
            });
            $("#userModal").modal('show');
        });
    }
    $scope.closeModal = function(){
        $("#userModal").modal('hide');
    };


    //даннкые для select

    $scope.countryData = {
        "id" : "country",
        "count" : 3,
        "filter":"countryName",
        "listName" : "countriesList",
        "required" : true,
        "maxHeight" : 300,
        "callback" : function (value){
            $scope.country = value;
            applicationService.action($scope, "regions", className.region, "chooseRegionsByCountry", {countryId:value.id,language : $scope.application.language.capitalizeFirstLetter()});
        }
    };

    $scope.regionData = {
        "id" : "region",
        "count" : 3,//
        "filter":"regionName",
        "listName" : "regions",
        "required" : true,//
        "maxHeight" : 300,//
        "callback" : function (value){
            $scope.region = value;
            applicationService.action($scope, "cities", className.city, "getCitiesByRegion", {regionId:value.id,language : $scope.application.language.capitalizeFirstLetter()}, function(item){
                console.log(item);
            });
        }
    };

    //$scope.cityData = {
    //    "id" : "city",
    //    "count" : 3,
    //    "filter":"countryName",
    //    "listName" : "countriesList",
    //    "required" : true,
    //    "maxHeight" : 300,
    //    "callback" : function (value){
    //        $scope.country = value;
    //        applicationService.action($scope, "regions", className.region, "chooseRegionsByCountry", {countryId:value.id,language : $scope.application.language.capitalizeFirstLetter()});
    //    }
    //};
});
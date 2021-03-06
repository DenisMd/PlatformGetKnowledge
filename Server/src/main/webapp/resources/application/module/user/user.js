model.controller("userCtrl", function ($scope, $state,$timeout,$http,applicationService,pageService,arcService,className) {
    var userId = pageService.getPathVariable("user",$state.params.path);

    function init() {
        applicationService.read($scope, "user_info", className.userInfo, userId, function (item) {

            //Если пользователя не существует бросаем 404
            if (!item) {
                $state.go("404");
            }

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


    if (userId) {
        init();
    }
    $scope.closeModal = function(){
        applicationService.action($scope,"skipResult",className.userInfo,"updateExtraInfo",{});
        $("#userModal").modal('hide');
    };

    //данные для status
    $scope.statusText = {
        maxLength:140
    };


    //основной контент
    //данные для диаграмм
    $scope.dataForArcs = [{
        id : 1,
        title : "Java",
        percent : 80,
        color : "#FF2B2B"
    }, {
        id : 2,
        title : "Photoshop",
        percent : 70,
        color : "#8000FF"
    },{
        id : 3,
        title : "JavaScript",
        percent : 90,
        color : "#FFCC00"
    } ];

    //scroll для диаграмм курсов
    $scope.arcScrollConfig = angular.merge({axis:"x", advanced:{ autoExpandHorizontalScroll: true }}, $scope.modalScrollConfig);

    //статистика
    //TODO посмотреть можно ли выключть responsive настройки

    function initStatisticGraphics(firstData,secondData,thirdData){
        var statisticGraphic = angular.element("#stastistic1")[0].getContext("2d");
        var graphic = new Chart(statisticGraphic).Line(firstData, arcService.getMainOption());
        statisticGraphics.push(graphic);

        statisticGraphic = $("#stastistic2").get(0).getContext("2d");
        graphic = new Chart(statisticGraphic).Bar(secondData, arcService.getMainOption());
        statisticGraphics.push(graphic);

        statisticGraphic = $("#stastistic3").get(0).getContext("2d");
        graphic = new Chart(statisticGraphic).Pie(thirdData, arcService.getMainOption());
        statisticGraphics.push(graphic);
    };
    $scope.showStatistic = true;
    $scope.toggelStatistic = function(){
        $scope.showStatistic = !$scope.showStatistic;
    };

    var graphicData = {
        labels : ["January", "February", "March", "April", "May", "June", "July"],
        datasets: [
                {
                    fillColor: "rgba(220,220,220,0.2)",
                    pointColor: "rgba(220,220,220,1)",
                    data: [65, 59, 80, 81, 56, 55, 40]
            }
        ]
    };

    var dataCircle = [
        {
            value: 65,
            color:"#F7464A",
            label: "Red"
        },
        {
            value: 50,
            color: "#46BFBD",
            label: "Green"
        },
        {
            value: 100,
            color: "#FDB45C",
            label: "Yellow"
        }
    ];

    var statisticGraphics = [];
    var arcGraphics = [];

    var test = [
        {
            value: 300,
            color:"#F7464A",
            highlight: "#FF5A5E",
            label: "Red"
        },
        {
            value: 50,
            color: "#46BFBD",
            highlight: "#5AD3D1",
            label: "Green"
        },
        {
            value: 100,
            color: "#FDB45C",
            highlight: "#FFC870",
            label: "Yellow"
        }
    ];
    $timeout(function(){
        $scope.dataForArcs.forEach(
        function(item,i){
        var ctx = document.getElementById("arc"+i).getContext("2d");
        var myDoughnutChart = new Chart(ctx).Doughnut(arcService.getDataForArc(item.percent,item.color),arcService.arcOptions);
        arcGraphics[i] = myDoughnutChart;
    });
    },0);

    initStatisticGraphics(graphicData,graphicData,dataCircle);




    //modal
    //данные для select
    var isCountryValid = false;
    var isRegionValid = false;
    var isRegionDisable = true;
    var isCityDisable = true;


    $scope.countryData = {
        "id"            : "country",
        "count"         : 3,
        "titleField"    :"countryName",
        "listName"      : "countriesList",
        "maxHeight"     : 300,
        "valid" : function(value){
            isCountryValid = value;
        },
        required: false,
        "callback": function(value){
            $scope.country = value;
            $scope.$broadcast('reset' + $scope.cityData.id.capitalizeFirstLetter() + 'Event');
            $scope.$broadcast('reset' + $scope.regionData.id.capitalizeFirstLetter() + 'Event');
            applicationService.action($scope, "regionsList", className.region, "getRegionsByCountry", {countryId:value.id});
            isRegionDisable = false;
            isCityDisable = true;
        }
    };

    $scope.regionData = {
        "id"            : "region",
        "count"         : 3,
        "titleField"    :"regionName",
        "listName"      : "regionsList",
        "maxHeight"     : 300,
        "disable" : function(){
            return !isCountryValid || !$scope.country || isRegionDisable;
        },
        "required" : function(){
            return $scope.country;
        },
        "valid" : function(value){
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
        "id"            : "city",
        "count"         : 3,
        "titleField"    :"cityName",
        "listName"      : "citiesList",
        "maxHeight"     : 300,
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


    $scope.dateTimeOptions = {
        id : "birth-day",
        format : "DD-MMMM-YYYY",
        minView : 'day' ,
        startView : 'year',
        onChange: function(date,isValid){
            if (isValid) {
                $scope.date = date;
            } else {
                $scope.date = null;
            }
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
            var data = {};
            if ($scope.city) {
                data.cityId = $scope.city.id;
            }
            if ($scope.speciality) {
                data.speciality = $scope.speciality;
            }
            if ($scope.date) {
                data.date = $scope.date;
            }

            applicationService.action($scope, "updateExtraInfo", className.userInfo, "updateExtraInfo", data, function(item){
                if (item === "Complete"){
                    $scope.user.specialty = data.speciality;
                    $("#userModal").modal('hide');
                }
            });
        }
    };


});

model.controller("newsCtrl", function ($scope,$state,applicationService,className,pageService) {

    var filter = applicationService.createFilter(className.news,0,10);
    filter.setOrder("postDate",true);

    $scope.newsList = [];

    function doRequest(){
      applicationService.filterRequest($scope,"",filter,function(news){
              $scope.newsList.push(news);
      })
    };
    doRequest();

    $scope.loadNews = function(){
        filter.increase(10);
        doRequest();
    };
    $scope.createNews = function(){
      applicationService.create($scope,"",className.news,$scope.news,function(result){
          $scope.showToast(result);
      })
    };



});
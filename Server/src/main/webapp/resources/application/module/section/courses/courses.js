model.controller("coursesCtrl", function ($scope,applicationService,className,pageService,$state) {

    var sectionName = pageService.getPathVariable("section",$state.params.path);

    var filter = applicationService.createFilter(className.groupCourses,0,10);
    filter.equal("section.name",sectionName);
    $scope.groupCourses = [];

    var addLog = function(groupCourses){
        $scope.groupCourses.push(groupCourses);
    };

    var doAction = function(){
        applicationService.filterRequest($scope,"",filter,addLog);
    };

    doAction();

    $scope.loadMore = function () {
        filter.increase(10);
        doAction();
    };

    $scope.coursesImg = function(id){
        return applicationService.imageHref(className.groupCourses,id);
    };
});
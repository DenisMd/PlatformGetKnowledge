model.controller("groupCoursesCtrl", function ($scope,applicationService,className) {

    applicationService.list($scope,"sections",className.section);

    $scope.multiLanguageData = {
        label : $scope.translate("groupCourses_description")
    };

    var filter = applicationService.createFilter(className.groupCourses,0,10);
    $scope.coursesGroup= [];

    var addGroupCourses = function(courses){
        $scope.coursesGroup.push(courses);
    };

    var doAction = function(){
        applicationService.filterRequest($scope,"",filter,addGroupCourses);
    };

    doAction();

    var reverse = false;
    $scope.setGroupOrder = function(orderName) {
        reverse = !reverse;
        filter.clearOrder();
        filter.setOrder(orderName,reverse);


        filter.reload();
        $scope.coursesGroup = [];
        doAction();
    };

    $scope.setSection = function(sectionId) {
        if (!sectionId) {
            filter.clearEqual();
        } else {
            filter.equal("section.id" , sectionId);
        }
        filter.reload();
        $scope.coursesGroup = [];
        doAction();
    };

    $scope.currentGroup = null;
    $scope.setCurrentItem = function(item){
        $scope.currentGroup = item;
        $scope.multiLanguageData.languages = {"ru":  item.descriptionRu, "en":  item.descriptionEn};
    };

    $scope.loadMore = function () {
        filter.increase(10);
        doAction();
    };

    $scope.updateGroup = function() {
        $scope.currentGroup.descriptionRu = $scope.multiLanguageData.languages.ru;
        $scope.currentGroup.descriptionEn = $scope.multiLanguageData.languages.en;
        applicationService.update($scope,"",className.groupCourses,$scope.currentGroup,function(result){
            $scope.showToast(result);
        });
    };

    var croppedImg = {
        save: function(file){
            updateImage(file);
        },
        areaType:"square"
    };

    $scope.getCropImageData  = function(){
        croppedImg.src = applicationService.imageHref(className.groupCourses,$scope.currentGroup.id);
        croppedImg.notUseDefault = $scope.currentGroup.imageViewExist;
        return croppedImg;
    };

    var updateImage = function(file) {
        applicationService.actionWithFile($scope,"cover",className.groupCourses,"updateCover",{id:$scope.currentGroup.id},file);
    };

    applicationService.count($scope,"countGroupCourses",className.groupCourses);
});
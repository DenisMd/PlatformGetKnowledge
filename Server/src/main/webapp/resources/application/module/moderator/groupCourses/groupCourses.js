model.controller("groupCoursesCtrl", function ($scope,applicationService,className,$mdDialog) {

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


    $scope.showDeleteDialog = function(ev) {
        var confirm = $mdDialog.confirm()
            .title($scope.translate("groupCourses_delete") + " " + $scope.currentGroup.title)
            .textContent($scope.translate("groupCourses_delete_confirmation"))
            .targetEvent(ev)
            .ariaLabel('Delete role')
            .ok($scope.translate("delete"))
            .cancel($scope.translate("cancel"));
        $mdDialog.show(confirm).then(function() {
            applicationService.remove($scope,"",className.groupCourses,$scope.currentGroup.id,function (result) {
                $scope.showToast(result);
                $scope.coursesGroup = [];
                doAction();
            });
        });
    };

    $scope.showAdvanced = function(ev) {
        $scope.showDialog(ev,$scope,"createGroupCourses.html",function(answer){
            applicationService.action($scope,"", className.groupCourses,"createGroupCourses",answer,function(result){
                $scope.showToast(result);
                $scope.coursesGroup = [];
                doAction();
            });
        });
    };
});
model.controller("foldersCtrl", function ($scope,applicationService,className,$mdDialog) {

    applicationService.list($scope,"sections",className.section);

    $scope.multiLanguageData = {
        label : $scope.translate("folder_description")
    };

    $scope.typesFolder = [{
        name : "groupCourses",
        className : className.groupCourses,
        createActionName : "createGroupCourses",
        updateCover : "updateCover"
    },{
        name : "groupBooks",
        className : className.groupBooks,
        createActionName : "createGroupBooks",
        updateCover : "updateCover"
    },{
        name : "groupPrograms",
        className : className.groupPrograms,
        createActionName : "createGroupPrograms",
        updateCover : "updateCover"
    }];

    var currentFolder =  $scope.typesFolder[0];


    var filter = applicationService.createFilter(currentFolder.className,0,10);
    $scope.coursesGroup= [];

    $scope.$watch('folderType', function(folder) {
        if(folder) {
            $scope.coursesGroup = [];
            currentFolder = folder;
            filter = applicationService.createFilter(currentFolder.className,0,10);
            doAction();
        }
    });


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
        applicationService.update($scope,"",currentFolder.className,$scope.currentGroup,function(result){
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
        croppedImg.src = applicationService.imageHref(currentFolder.className,$scope.currentGroup.id);
        croppedImg.notUseDefault = $scope.currentGroup.imageViewExist;
        return croppedImg;
    };

    var updateImage = function(file) {
        applicationService.actionWithFile($scope,"cover",currentFolder.className,currentFolder.updateCover,{id:$scope.currentGroup.id},file);
    };


    $scope.showDeleteDialog = function(ev) {
        var confirm = $mdDialog.confirm()
            .title($scope.translate("folder_delete") + " " + $scope.currentGroup.title)
            .textContent($scope.translate("folder_delete_confirmation"))
            .targetEvent(ev)
            .ariaLabel('Delete role')
            .ok($scope.translate("delete"))
            .cancel($scope.translate("cancel"));
        $mdDialog.show(confirm).then(function() {
            applicationService.remove($scope,"",currentFolder.className,$scope.currentGroup.id,function (result) {
                $scope.showToast(result);
                $scope.coursesGroup = [];
                doAction();
            });
        });
    };

    $scope.showAdvanced = function(ev) {
        $scope.showDialog(ev,$scope,"createGroupCourses.html",function(answer){
            applicationService.action($scope,"", currentFolder.className,currentFolder.createActionName,answer,function(result){
                $scope.showToast(result);
                $scope.coursesGroup = [];
                doAction();
            });
        });
    };
});
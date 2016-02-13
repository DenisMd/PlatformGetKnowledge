model.controller("groupCoursesCtrl", function ($scope,applicationService,className) {

    $scope.treeViewListData = {
        dataList : [],
        fieldTitle : "name",
        fieldSubItems : "courses",
        subItemFieldTitle : "title",
        subItemFieldSubItems : "none",
        callback : function(item) {
            $scope.currentMenuItem = item;
        }
    };

    applicationService.list($scope,"sections", className.section,function(item){

        item.courses = [];
        item.name = item.name.capitalizeFirstLetter();
        $scope.treeViewListData.dataList.push(item);
        applicationService.action($scope,"",className.groupCourses,"getGroupCoursesFromSection",{
            "sectionId" : item.id
        },function(groupCourses){
            item.courses.push(groupCourses);
        });
    });

    $scope.setCurrentItem = function(item){
        $scope.currentMenuItem = item;
        item.isOpen = !item.isOpen;
    };
});
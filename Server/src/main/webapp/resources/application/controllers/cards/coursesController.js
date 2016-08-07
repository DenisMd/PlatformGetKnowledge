model.controller("coursesController" , function($scope,$state,$languages,applicationService,className){

    var maxCharactersInName = 40;
    var maxCharacterDescription = 250;

    $scope.orderDesc = true;
    $scope.showCreateArea = false;
    $scope.showFilterArea = true;

    $scope.filter = applicationService.createFilter($scope.getData().className,0,10);
    $scope.filter.createFiltersInfo();
    $scope.filter.equals("groupCourses.url","text",$scope.getData().groupCourse);
    $scope.filter.equals("groupCourses.section.name","text",$scope.getData().sectionName);
    $scope.filter.equals("draft","logical",false);
    $scope.courses = [];

     function by_date(orderDesc) {
        $scope.filter.clearOrder();
        $scope.filter.setOrder("createDate" , orderDesc);

        $scope.filter.result.first = 0;
        $scope.courses = [];

        doAction();
    }

     function by_name(orderDesc) {
        $scope.filter.clearOrder();
        $scope.filter.setOrder("name" , orderDesc);

        $scope.filter.result.first = 0;
        $scope.courses = [];

        doAction();
    }

    function by_rating(orderDesc) {
        $scope.filter.clearOrder();
        $scope.filter.setOrder("rating.avgRating" , orderDesc);
        $scope.filter.setDistinct(false);

        $scope.filter.result.first = 0;
        $scope.courses = [];

        doAction();
    }
    
    function by_price(orderDesc) {
        $scope.filter.clearOrder();
        $scope.filter.setOrder("item.price.price" , orderDesc);
        $scope.filter.setDistinct(false);

        $scope.filter.result.first = 0;
        $scope.courses = [];

        doAction();
    }
    

    $scope.currentFilter = "1";
    $scope.sortings = [
        {
            id : "1",
            title : "by_date",
            callback : function() {
                $scope.currentFilter = this.id;
                $scope.orderDesc = !$scope.orderDesc;
                by_date($scope.orderDesc);
            }
        },{
            id : "2",
            title : "by_name",
            callback : function() {
                $scope.currentFilter = this.id;
                $scope.orderDesc = !$scope.orderDesc;
                by_name($scope.orderDesc);
            }
        },{
            id : "3",
            title : "by_rating",
            callback : function () {
                $scope.currentFilter = this.id;
                $scope.orderDesc = !$scope.orderDesc;
                by_rating($scope.orderDesc);
            }
        },{
            id : "4",
            title : "by_price",
            callback : function () {
                $scope.currentFilter = this.id;
                $scope.orderDesc = !$scope.orderDesc;
                by_price($scope.orderDesc);
            }
        }
    ];

    var likeIndex;
    var equalIndex;
    var isBaseIndex;
    var isFreeIndex;
    var isisAvailableIndex;
    $scope.searchCourse = function(text,language,options) {
        if (likeIndex != undefined) {
            $scope.filter.result.customFilters.splice(likeIndex,1);
        }
        if (text) {
            likeIndex  = $scope.filter.addCustomFilter("searchCourses",{
                textValue : text
            });
        } else {
            likeIndex = undefined;
        }

        if (equalIndex !== undefined) {
            $scope.filter.result.filtersInfo.filters.splice(equalIndex, 1);
        }

        if (language && language != "any") {
            equalIndex = $scope.filter.equals("language.name", "str",language.capitalizeFirstLetter());
        } else {
            equalIndex = undefined;
        }

        if (isBaseIndex != undefined) {
            $scope.filter.result.filtersInfo.filters.splice(isBaseIndex,1);
        } else {
            isBaseIndex = undefined;
        }

        if (isFreeIndex != undefined) {
            $scope.filter.result.customFilters.splice(isFreeIndex,1);
        } else {
            isFreeIndex = undefined;
        }

        if (isisAvailableIndex != undefined) {
            $scope.filter.result.customFilters.splice(isisAvailableIndex,1);
        } else {
            isisAvailableIndex = undefined;
        }

        if (options) {
            if ("isBase" in options) {
                if (options.isBase)
                    isBaseIndex  = $scope.filter.equals("base", "logical", options.isBase);
            }

            if ("isFree" in options) {
                if (options.isFree)
                    isFreeIndex  = $scope.filter.addCustomFilter("isFreeCourses",{});
            }
            if ("isAvailable" in options) {
                if (options.isAvailable)
                    isisAvailableIndex  = $scope.filter.addCustomFilter("isAvailable",{});
            }
        }

        //$scope.filter.like("tags.tagName","text","%"+text+"%");

        $scope.filter.result.first = 0;
        $scope.courses = [];

        doAction();
    };

    var addCourse = function(course){
        if(course) {
            course.imageSrc = applicationService.imageHref($scope.getData().className, course.id);
            course.href = $scope.addUrlToPath("/course/" + course.id);
            if (course.name.length > maxCharactersInName) {
                course.name = course.name.substr(0, maxCharactersInName) + "...";
            }
            if (course.description.length > maxCharacterDescription) {
                course.description = course.description.substr(0,maxCharacterDescription) + "...";
            }
            if (course.item || course.item.price) {
                course.item.price = $scope.convertPrice(course.item.price);
            }
            
            $scope.courses.push(course);
        }
    };

    var doAction = function(){
        applicationService.filterRequest($scope,"coursesInfo", $scope.filter,addCourse);
    };

    $scope.loadMore = function () {
        $scope.filter.increase(10);
        doAction();
    };

    $scope.langs = $languages.languages;

    $scope.createCourse = function(newCourse) {
        newCourse.groupCourseUrl = $scope.getData().groupCourse;
        newCourse.language = newCourse.language.capitalizeFirstLetter();
        applicationService.action($scope,"",className.course,"createCourse",newCourse,function(result){
            $scope.showToast($scope.getResultMessage(result));
            if (result.status == "Complete") {
                $scope.goTo("course/" + result.object);
            }
        });
    };

    //Проверка на существование выбранной группы книг
    var groupCourseFilter = applicationService.createFilter(className.groupCourses,0,10);
    groupCourseFilter.createFiltersInfo();
    groupCourseFilter.equals("url","text",$scope.getData().groupCourse);
    groupCourseFilter.equals("section.name","text",$scope.getData().sectionName);
    applicationService.filterRequest($scope,"groupCourseInfo", groupCourseFilter,function(groupCourse){
        if (groupCourse == null) {
            $state.go("404");
        }

        by_date(true);
    });
});
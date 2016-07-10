model.controller("coursesController" , function($scope,$state,$languages,applicationService,className){

    var maxCharactersInName = 40;
    var maxCharacterDescription = 250;

    $scope.currentFilterByDate = true;
    $scope.showCreateArea = false;

    $scope.filter = applicationService.createFilter($scope.getData().className,0,10);
    $scope.filter.createFiltersInfo();
    $scope.filter.equals("groupCourses.url","text",$scope.getData().groupCourse);
    $scope.filter.equals("groupCourses.section.name","text",$scope.getData().sectionName);
    $scope.courses = [];

    $scope.by_date = function() {
        $scope.currentFilterByDate = true;
        $scope.filter.clearOrder();
        $scope.filter.setOrder("createDate" , false);

        $scope.filter.result.first = 0;
        $scope.courses = [];

        doAction();
    };

    $scope.by_name = function() {
        $scope.currentFilterByDate = false;
        $scope.filter.clearOrder();
        $scope.filter.setOrder("name" , false);

        $scope.filter.result.first = 0;
        $scope.courses = [];

        doAction();
    };

    var likeIndex;
    var equalIndex;
    $scope.searchCourse = function(text,language) {
        if (likeIndex != undefined) {
            $scope.filter.result.customFilters.splice(likeIndex,1);
        }
        if (text) {
            likeIndex  = $scope.filter.addCustomFilter("searchCourses",{
                textValue : text
            });
        }

        if (equalIndex !== undefined) {
            $scope.filter.result.filtersInfo.filters.splice(equalIndex, 1);
        }

        if (language && language != "any") {
            equalIndex = $scope.filter.equals("language.name", "str",language.capitalizeFirstLetter());
        } else {
            equalIndex = undefined;
        }

        //$scope.filter.like("tags.tagName","text","%"+text+"%");

        $scope.filter.result.first = 0;
        $scope.programs = [];

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

        $scope.by_date();
    });
});
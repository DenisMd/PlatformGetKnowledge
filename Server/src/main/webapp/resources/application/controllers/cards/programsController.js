model.controller("programsController" , function($scope,$state,$languages,applicationService,className){
    var maxCharactersInName = 40;
    var maxCharacterDescription = 250;

    $scope.orderDesc = true;
    $scope.showCreateArea = false;

    $scope.filter = applicationService.createFilter($scope.getData().className,0,10);
    $scope.filter.createFiltersInfo();
    $scope.filter.equals("groupPrograms.url","text",$scope.getData().groupProgram);
    $scope.filter.equals("groupPrograms.section.name","text",$scope.getData().sectionName);
    $scope.programs = [];

    $scope.by_date = function(orderDesc) {
        $scope.currentFilterByDate = true;
        $scope.filter.clearOrder();
        $scope.filter.setOrder("createDate" , orderDesc);

        $scope.filter.result.first = 0;
        $scope.programs = [];

        doAction();
    };

    $scope.by_name = function(orderDesc) {
        $scope.currentFilterByDate = false;
        $scope.filter.clearOrder();
        $scope.filter.setOrder("name" , orderDesc);

        $scope.filter.result.first = 0;
        $scope.programs = [];

        doAction();
    };

    $scope.currentFilter = "1";
    $scope.sortings = [
        {
            id : "1",
            title : "by_date",
            callback : function() {
                $scope.currentFilter = this.id;
                $scope.orderDesc = !$scope.orderDesc;
                $scope.by_date($scope.orderDesc);
            }
        },{
            id : "2",
            title : "by_name",
            callback : function() {
                $scope.currentFilter = this.id;
                $scope.orderDesc = !$scope.orderDesc;
                $scope.by_name($scope.orderDesc);
            }
        }
    ];

    var likeIndex;
    var equalIndex;
    $scope.searchPrograms = function(text,language) {
        if (likeIndex != undefined) {
            $scope.filter.result.customFilters.splice(likeIndex,1);
        }
        if (text) {
            likeIndex  = $scope.filter.addCustomFilter("searchPrograms",{
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

    var addProgram = function(program){
        if(program) {
            program.imageSrc = applicationService.imageHref($scope.getData().className, program.id);
            program.href = $scope.addUrlToPath("/program/" + program.id);
            if (program.name.length > maxCharactersInName) {
                program.name = program.name.substr(0, maxCharactersInName) + "...";
            }
            if (program.description.length > maxCharacterDescription) {
                program.description = program.description.substr(0,maxCharacterDescription) + "...";
            }
            $scope.programs.push(program);
        }
    };

    var doAction = function(){
        applicationService.filterRequest($scope,"programsInfo", $scope.filter,addProgram);
    };

    $scope.loadMore = function () {
        $scope.filter.increase(10);
        doAction();
    };

    $scope.langs = $languages.languages;

    $scope.createProgram = function(newProgram) {
        newProgram.groupProgramUrl = $scope.getData().groupProgram;
        newProgram.language = newProgram.language.capitalizeFirstLetter();
        applicationService.action($scope,"",className.program,"createProgram",newProgram,function(result){
            $scope.showToast($scope.getResultMessage(result));
            if (result.status == "Complete") {
                $scope.goTo("program/" + result.object);
            }
        });
    };

    //Проверка на существование выбранной группы книг
    var groupProgramFilter = applicationService.createFilter(className.groupPrograms,0,10);
    groupProgramFilter.createFiltersInfo();
    groupProgramFilter.equals("url","text",$scope.getData().groupProgram);
    groupProgramFilter.equals("section.name","text",$scope.getData().sectionName);
    applicationService.filterRequest($scope,"groupProgramInfo", groupProgramFilter,function(groupProgram){
        if (groupProgram == null) {
            $state.go("404");
        }

        $scope.by_date(true);
    });
});
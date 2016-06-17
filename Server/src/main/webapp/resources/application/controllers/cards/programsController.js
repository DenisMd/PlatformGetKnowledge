model.controller("programsController" , function($scope,$state,applicationService,className){
    var maxCharactersInName = 21;

    $scope.currentFilterByDate = true;
    $scope.showCreateArea = false;

    $scope.filter = applicationService.createFilter($scope.getData().className,0,10);
    $scope.filter.createFiltersInfo();
    $scope.filter.equals("groupPrograms.url","text",$scope.getData().groupProgram);
    $scope.filter.equals("groupPrograms.section.name","text",$scope.getData().sectionName);
    $scope.programs = [];

    $scope.by_date = function() {
        $scope.currentFilterByDate = true;
        $scope.filter.clearOrder();
        $scope.filter.setOrder("createDate" , true);

        $scope.filter.result.first = 0;
        $scope.programs = [];

        doAction();
    };

    $scope.by_name = function() {
        $scope.currentFilterByDate = false;
        $scope.filter.clearOrder();
        $scope.filter.setOrder("name" , false);

        $scope.filter.result.first = 0;
        $scope.programs = [];

        doAction();
    };

    var likeIndex;
    $scope.searchPrograms = function(text) {
        if (likeIndex != undefined) {
            $scope.filter.result.customFilters.splice(likeIndex,1);
        }
        if (text) {
            likeIndex  = $scope.filter.addCustomFilter("searchPrograms",{
                textValue : text
            });
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

    applicationService.list($scope,"langs",className.language, function (item) {
        item.title = $scope.translate(item.name.toLowerCase());
    });

    $scope.createProgram = function(newProgram) {
        newProgram.groupProgramUrl = $scope.getData().groupProgram;
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
    applicationService.filterRequest($scope,"groupProgramInfo", groupProgramFilter,function(groupProgram){
        if (groupProgram == null) {
            $state.go("404");
        }

        $scope.by_date();
    });
});
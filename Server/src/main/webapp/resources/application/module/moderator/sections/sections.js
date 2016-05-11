model.controller("sectionsCtrl", function ($scope, $state,$http,applicationService,pageService,className) {

    function updateSections(){
        applicationService.list($scope , "sections",className.section,function(item){
            if (!item.descriptionRu) {
                item.descriptionRu = "";
            }
            if (!item.descriptionEn) {
                item.descriptionEn = "";
            }
            $scope.selectorData.list.push(item);
        });
    }

    updateSections();

    $scope.selectorData = {
        list        : [],
        tableName   :   "section_info",
        filters      : [
            {
                title : "name",
                type  : "text",
                field : "name",
                default : true
            },{
                title : "id",
                type  : "number",
                field : "id"
            }
        ],
        headerNames : [
            {
                name : "id",
                orderBy : true
            },
            {
                name : "name",
                orderBy : true
            }
        ],
        selectItemCallback : function (item) {
            $scope.currentSection = item;
            $scope.multiLanguageData.languages = {"ru":  item.descriptionRu, "en":  item.descriptionEn};
        }
    };

    $scope.updateSections = function() {
        $scope.currentSection.descriptionRu = $scope.multiLanguageData.languages.ru;
        $scope.currentSection.descriptionEn = $scope.multiLanguageData.languages.en;
        applicationService.update($scope,"",className.section,$scope.currentSection,function(result){
            $scope.showToast($scope.getResultMessage(result));
        });
        //выполнить update image
    };

    $scope.multiLanguageData = {
        label : $scope.translate("section_description")
    };

    var croppedImg = {
        save: function(file){
            updateImage(file);
        },
        areaType:"square"
    };

    $scope.getCropImageData  = function(){
        croppedImg.src = applicationService.imageHref(className.section,$scope.currentSection.id);
        croppedImg.notUseDefault = $scope.currentSection.imageViewExist;
        return croppedImg;
    };

    var updateImage = function(file) {
        applicationService.actionWithFile($scope,"cover",className.section,"updateCover",{id:$scope.currentSection.id},file,function (result) {
            updateSections();
        });
    };
});
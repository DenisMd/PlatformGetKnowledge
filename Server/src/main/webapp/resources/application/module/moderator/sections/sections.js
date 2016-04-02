model.controller("sectionsCtrl", function ($scope, $state,$http,applicationService,pageService,className) {

    $scope.updateSections = function() {
        $scope.currentSection.descriptionRu = $scope.multiLanguageData.languages.ru;
        $scope.currentSection.descriptionEn = $scope.multiLanguageData.languages.en;
        applicationService.update($scope,"",className.section,$scope.currentSection,function(result){
            $scope.showToast(result);
        });
        //выполнить update image
    };

    $scope.multiLanguageData = {
        label : $scope.translate("section_description")
    };

    applicationService.list($scope , "sections",className.section,function(item){
        if (!item.descriptionRu) {
            item.descriptionRu = "";
        }
        if (!item.descriptionEn) {
            item.descriptionEn = "";
        }
    });


    $scope.setCurrentItem = function (item) {
        $scope.currentSection = item;
        $scope.multiLanguageData.languages = {"ru":  item.descriptionRu, "en":  item.descriptionEn};
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
        applicationService.actionWithFile($scope,"cover",className.section,"updateCover",{id:$scope.currentSection.id},file);
    };
});
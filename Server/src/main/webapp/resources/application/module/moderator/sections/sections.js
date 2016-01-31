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
        if (!item.descriptionRu)
            item.descriptionRu = "";
        if (!item.descriptionEn)
            item.descriptionEn = "";
    });


    $scope.setCurrentItem = function (item) {
        $scope.currentSection = item;
        $scope.multiLanguageData.languages = {"ru":  item.descriptionRu, "en":  item.descriptionEn};
    };

    $scope.sectionImg = function(id){
        return applicationService.imageHref(className.section,id);
    };

    var coverImage = null;

    $scope.croppedImg = {
        save: function(file){
            $scope.updateImage(file);
        },
        isInModal:true
    };

    $scope.getCropImageData  = function(currentSection){
        $scope.croppedImg.src = $scope.sectionImg(currentSection.id);
        return $scope.croppedImg;
    };

    $scope.updateImage = function(file) {
        applicationService.actionWithFile($scope,"cover",className.section,"updateCover",{id:$scope.currentSection.id},file);
    }
});
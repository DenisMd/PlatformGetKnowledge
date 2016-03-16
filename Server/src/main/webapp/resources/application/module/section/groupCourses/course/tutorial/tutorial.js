model.controller("tutorialCtrl", function ($scope,applicationService,className,pageService,$state) {
    var courseId = pageService.getPathVariable("course",$state.params.path);
    var tutorialNumber = pageService.getPathVariable("tutorial" , $state.params.path)
});

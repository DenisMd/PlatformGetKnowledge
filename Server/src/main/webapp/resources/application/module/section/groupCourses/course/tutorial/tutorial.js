model.controller("tutorialCtrl", function ($scope,applicationService,className,pageService,$state) {
    var courseId = pageService.getPathVariable("course",$state.params.path);
    var tutorialNumber = pageService.getPathVariable("tutorial" , $state.params.path);

    function readTutorialInfo() {
        applicationService.action($scope,"tutorial",className.tutorial,"getTutorial", {
            courseId : +courseId,
            orderNumber : +tutorialNumber
        },function(tut){
            applicationService.action($scope,"",className.tutorial,"getVideo",{
                tutorialId : tut.id
            },function(videoInfo){
                $scope.tutorial.video = videoInfo;
            });

            applicationService.action($scope,"",className.tutorial,"getTutorialText",{
                tutorialId : tut.id
            },function(textData){
                $scope.tutorial.data = textData;
            });
        });

        applicationService.read($scope,"course",className.course,+courseId);
    }

    readTutorialInfo();

    $scope.showEditableContent = false;

    $scope.updateTutorial = function(tutorial) {
        var result = {};
        result.tutorialId = tutorial.id;
        result.name = tutorial.name;
        result.orderNumber = tutorial.orderNumber;
        result.data = tutorial.data;
        applicationService.action($scope,"",className.tutorial,"updateTutorial",result,function(result){
            $scope.showToast(result);
            if (tutorialNumber !== result.orderNumber) {
                var n = window.location.href.lastIndexOf('/');
                $scope.goTo(window.location.href.substring(0,n+1)+result.object,true);
            }
            readTutorialInfo();
        });
    };

    var videoImg = {
        save: function(file){
            updateVideoImage(file);
        },
        areaType:"square"
    };

    $scope.getVideoImage  = function(){
        if ($scope.tutorial && $scope.tutorial.video) {
            videoImg.src = applicationService.imageHref(className.video, $scope.tutorial.video.id);
            videoImg.notUseDefault = true;
        }
        return videoImg;
    };

    var updateVideoImage = function(file) {
        applicationService.actionWithFile($scope,"",className.tutorial,"uploadVideoTutorial",{
            tutorialId : $scope.tutorial.id,
            videoName : $scope.tutorial.video.videoName
        },file);
    };

    $scope.uploader = applicationService.createUploader($scope,"",className.video,"uploadVideo",null,null,function(formData){
        formData.data = JSON.stringify({videoId:$scope.tutorial.video.id});
    });

});

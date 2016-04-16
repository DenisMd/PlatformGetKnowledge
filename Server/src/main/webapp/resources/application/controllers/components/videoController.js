var player;

function initVideoPlayer() {
    if (!player) {
        var options = {
            "controls": true,
            "preload": "metadata",
            "autoplay": false,
            "width": 720,
            "height": 480,
            aspectRatio: '16:9'
        };
        player = videojs(document.getElementById('main-video'), options, function () {
            player = this;
        });
    }
}



model.controller("videoCtrl",function($scope,videoDialogService,className,applicationService){

    initVideoPlayer();

    //Получаем url для загрузки видео
    function getVideoUrl(id) {
        return "/data/readVideo?className="+className.video+"&id="+id;
    }

    applicationService.read($scope,"video",className.video,$scope.getData().id);

    $scope.open = function() {
        var videoUrl = getVideoUrl($scope.video.id);
        videoDialogService.afterOpen($scope.video.id);
        if (!player ||player.currentSrc() !== videoUrl) {
            player.src({type: "video/mp4", src: videoUrl});
            player.play();
        } else {
            player.play();
        }
        $('#videoModal').modal('show');
    };

    $scope.close = function(){
        if (player) {
            player.pause();
        }
    };

    //Получения обложки для видео
    $scope.videoImg = function(id){
        return applicationService.imageHref(className.video,id);
    };

    $('#videoModal').on("hidden.bs.modal",function(){
        $scope.close();
    });
});
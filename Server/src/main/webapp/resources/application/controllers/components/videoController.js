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

model.controller("videoController",function($scope,videoDialogService,className,applicationService){

    initVideoPlayer();

    //Получаем url для загрузки видео
    function getVideoUrl(id) {
        return "/data/readVideo?className="+className.video+"&id="+id;
    }

    //Получения обложки для видео
    function videoImg(id){
        return applicationService.imageHref(className.video,id);
    }

    applicationService.read($scope,"video",className.video,$scope.getData().id,function(video){
        video.coverSrc = videoImg(video.id);
    });

    $scope.open = function() {
        var videoUrl = getVideoUrl($scope.video.id);
        videoDialogService.afterOpen($scope.video);
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



    $('#videoModal').on("hidden.bs.modal",function(){
        $scope.close();
    });
});
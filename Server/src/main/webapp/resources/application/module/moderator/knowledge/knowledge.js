model.controller("knowledgeCtrl", function ($scope, $state,$http,applicationService,pageService,className,$mdDialog) {

    var filter = applicationService.createFilter(className.knowledge,0,10);
    $scope.knowledges = [];

    $scope.knowledgeType = ['Programming' , 'Design' , 'Math' , 'Physic'];

    var addKnowledge = function(knowledge){
        $scope.knowledges.push(knowledge);
    };

    var doAction = function(){
        applicationService.filterRequest($scope,"",filter,addKnowledge);
    };

    doAction();

    var reverse = false;
    $scope.setLogOrder = function(orderName) {
        reverse = !reverse;
        filter.clearOrder();
        filter.setOrder(orderName,reverse);


        filter.reload();
        $scope.knowledges = [];
        doAction();
    };

    $scope.searchByType = function(type) {
        if (!type) {
            filter.clearIn();
        } else {
            filter.in("knowledgeType" , [type]);
        }
        filter.reload();
        $scope.knowledges = [];
        doAction();
    };

    $scope.currentKnowledge = null;
    $scope.setCurrentItem = function(item){
        $scope.currentKnowledge = item;
    };

    $scope.loadMore = function () {
        filter.increase(10);
        doAction();
    };

    $scope.updateKnowledge = function () {
        applicationService.update($scope,"",className.knowledge,$scope.currentKnowledge,function(result){
            $scope.showToast(result);
        });
    };

    $scope.showDeleteDialog = function(ev) {
        var confirm = $mdDialog.confirm()
            .title($scope.translate("knowledge_delete") + " " + $scope.currentKnowledge.name)
            .textContent($scope.translate("knowledge_delete_confirmation"))
            .targetEvent(ev)
            .ariaLabel('Delete knowledge')
            .ok($scope.translate("delete"))
            .cancel($scope.translate("cancel"));
        $mdDialog.show(confirm).then(function() {
            applicationService.remove($scope,"",className.knowledge,$scope.currentKnowledge.id,function (result) {
                $scope.showToast(result);
                $scope.knowledges = [];
                doAction();
            });
        });
    };

    $scope.showAdvanced = function(ev) {
        $scope.showDialog(ev,$scope,"createKnowledge.html",function(answer){
            applicationService.create($scope,"", className.knowledge,answer,function(result){
                $scope.showToast(result);
                $scope.knowledges = [];
                doAction();
            });
        });
    };

    var croppedImg = {
        save: function(file){
            updateImage(file);
        },
        areaType:"circle",
        isCrop : true
    };

    $scope.getCropImageData  = function(){
        croppedImg.src = applicationService.imageHref(className.knowledge,$scope.currentKnowledge.id);
        croppedImg.notUseDefault = $scope.currentKnowledge.imageViewExist;
        return croppedImg;
    };

    var updateImage = function(file) {
        applicationService.actionWithFile($scope,"image",className.knowledge,"uploadImage",{knowledgeId:$scope.currentKnowledge.id},file,function(result){
            $scope.showToast(result);
            $scope.knowledges = [];
            doAction();
        });
    }
});
model.controller("cardsController",function($scope){

    function fillItems(event,data) {
        $scope.cardsItem = [];
        $scope.colLg = 12 / data.cardsInRow;
        var tempArray = [];
        for (var i = 0; i < data.cards.length; i++) {
            if (i !== 0 && ((i % data.cardsInRow) === 0)) {
                $scope.cardsItem.push(tempArray);
                tempArray = [];
            }
            tempArray.push(data.cards[i]);
        }

        if (tempArray.length !== 0) {
            $scope.cardsItem.push(tempArray);
        }
    }

    $scope.$on("fillCards",fillItems);

});
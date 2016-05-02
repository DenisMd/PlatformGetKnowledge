model.controller("dateTimePickerController",function ($scope) {
    $scope.inputId = "dropdown-" + $scope.getData().id;
    $scope.inputFormat = $scope.getData().format;
    $scope.config = {
        dropdownSelector : $scope.inputId,
        minView : $scope.getData().minView,
        startView : $scope.getData().startView
    };

    $scope.onTimeSet = function (newDate) {
        $scope.getData().onChange(moment(newDate).valueOf() , moment(newDate).isValid());
    }
});
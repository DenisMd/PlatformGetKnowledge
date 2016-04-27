model.factory('customFilterService', function() {
    var modalId = "#customFilterDialogId";
    return {
        openDialog : function(){
            $(modalId).modal('show');
        }
    }
});
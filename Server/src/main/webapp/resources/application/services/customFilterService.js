model.factory('customFilterService', function() {
    var modalId = "#customFilterDialogId";
    return {
        openDialog : function(){
            $(modalId).modal('show');
        },
        closeDialog : function(filter) {
            //if (element){
            //    callbackSave(element);
            //}
            $(modalId).modal("hide");
        }
    }
});
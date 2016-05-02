model.factory('customFilterService', function() {
    var modalId = "#customFilterDialogId";
    var callbackOpen = null;
    var callbackSave = null;
    var filtersInfo = [];
    return {

        filtersInfo : function(info) {
            if (info !== undefined) filtersInfo = info;

            return filtersInfo;
        },

        setCallbackOpen : function(callback) {
            callbackOpen = callback;
        },

        setCallbackSave : function(callback) {
            callbackSave = callback;
        },

        openDialog : function(defaultValue){
            $(modalId).modal('show');
            if (angular.isFunction(callbackOpen)) {
                callbackOpen(defaultValue);
            }
        },
        closeDialog : function() {
            $(modalId).modal("hide");
        },
        saveDialog : function(filter) {
            $(modalId).modal("hide");

            if (angular.isFunction(callbackSave)) {
                callbackSave(filter);
            }
        }
    }
});
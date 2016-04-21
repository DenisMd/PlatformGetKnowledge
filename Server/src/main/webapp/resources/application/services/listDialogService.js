model.factory('listDialogService', function() {
    var listInfo    = {};
    var modalId     = "#listDialogId";
    var callbackOpen = null;
    var callbackSave = null;

    //подсчет высоты основного содержания модалки
    function getHeight(){
        var height = listInfo.maxHeight? listInfo.maxHeight: 400;
        var temp = 40 * listInfo.list.length;
        return !temp || temp > height? height : temp;
    }

    return {
        setCallbackOpen : function(func) {
            callbackOpen = func;
        },

        setCallbackSave : function (func) {
            callbackSave = func
        },

        getListInfo : function(){
            return listInfo;
        },

        setListInfo : function(data){
            listInfo = data;
        },

        openDialog : function(){
            $(modalId).modal({
                backdrop: 'static',
                keyboard: false
            });
            $(modalId).modal('show');
            $(modalId+" .table-content").height(getHeight());
            callbackOpen();
        },

        closeDialog : function(element) {
            if (element){
                callbackSave(element);
            }
            $(modalId).modal("hide");
        },

        height : function() {
            return 0;//getHeight();
        }
    };
});
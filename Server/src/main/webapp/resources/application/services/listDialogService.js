model.factory('listDialogService', function() {
    var listInfo    = {};
    var modalId     = "#listDialogId";
    var isModelOpen = false;

    //подсчет высоты основного содержания модалки
    function getHeight(){
        var height = listInfo.maxHeight? listInfo.maxHeight: 400;
        var temp = 40 * listInfo.length;
        return !temp || temp > height? height : temp;
    }

    return {
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
        },

        closeDialog : function() {
            $(modalId).modal("hide");
        },

        height : function() {
            return getHeight();
        },

        setModelOpen : function(isOpen) {
        }
    };
});
model.factory('listDialogService', function() {
    var listInfo = {};

    return {
        getListInfo : function(){
            return listInfo;
        },

        setListInfo : function(data){
            listInfo = data;
        }
    };
});
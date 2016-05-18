model.animation('.trSlide', [function() {
    var shrinkyItems=[];
    var speedUp=3;
    var speedDown=3;

    function clear(){
        var needClear = true;
        shrinkyItems.forEach(function(item, index, object) {
            if (item !== null) {
                needClear = false;
            }
        });
        if (needClear){
            shrinkyItems = [];
        }
    }

    function initShrinky(element, direction, doneFn){
        clear();
        var child=element.getElementsByTagName('div')[0];
        var targetHeight=direction=="up"?0 : child.offsetHeight;
        var currentHeight = direction=="up"?child.offsetHeight:0;
        var id=shrinkyItems.push({
            element:element,
            direction:direction,
            currentHeight: currentHeight,
            targetHeight: targetHeight,
            doneFn:doneFn
        });
        if(direction=="down")
            element.style.height=(currentHeight)+"px";

        setTimeout(function(){
            shrinky(id-1);
        });
    }

    function shrinky(id){
        var item=shrinkyItems[id];
        var heightChange=item.currentHeight<item.targetHeight?speedDown:-speedUp;
        item.currentHeight+=heightChange;
        if(item.direction=="down" && item.currentHeight>item.targetHeight)
            item.currentHeight=item.targetHeight;
        if(item.direction=="up" && item.currentHeight<item.targetHeight)
            item.currentHeight=item.targetHeight;
        item.element.style.height=(item.currentHeight)+"px";
        if (item.currentHeight==item.targetHeight){
            item.doneFn();
            shrinkyItems[id]=null;
        }
        else{
            setTimeout(function(){
                shrinky(id);
            });
        }
    }


    return {
        // make note that other events (like addClass/removeClass)
        // have different function input parameters
        enter: function(element, doneFn) {
            var div=element[0].getElementsByClassName("item-panel")[0];
            initShrinky(div,"down", doneFn);
        },


        leave: function(element, doneFn) {
            var div=element[0].getElementsByClassName("item-panel")[0];
            initShrinky(div,"up", doneFn);
        }
    }
}]);
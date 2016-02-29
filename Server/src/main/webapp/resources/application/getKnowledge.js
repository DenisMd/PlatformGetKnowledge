new Clipboard('.clipboard');

var model = angular.module("mainApp", ["BackEndService", "ui.bootstrap", "ngImgCrop" , "ngMaterial","ui.codemirror", "hljs"]);
model.constant("codemirrorURL", "/resources/bower_components/codemirror/")

var player;

function initVideoPlayer() {
    var options = {
        "controls": true,
        "preload": "matadata",
        "autoplay": false,
        "width": 720,
        "height": 480
    };
    player = videojs(document.getElementById('main-video'), options, function () {
        player = this;
    });

}

model.config(function (hljsServiceProvider,codemirrorURL) {
    hljsServiceProvider.setOptions({
        // replace tab with 4 spaces
        tabReplace: '    '
    });
    CodeMirror.modeURL = codemirrorURL+ "mode/%N/%N.js";
});

model.controller("mainController", function ($scope,$rootScope, $http, $state, applicationService,pageService, className,$mdToast,$mdDialog, $mdMedia,$parse) {

    //Toast
    $scope.showToast = function (text) {
        $mdToast.show(
            $mdToast.simple()
                .textContent($scope.translate(text))
                .position("bottom right")
                .hideDelay(3000)
        );
    };

    //hightlights
    $scope.toPrettyJSON = function (objStr, tabWidth) {
        try {
            var obj = $parse(objStr)({});
        }catch(e){
            // eat $parse error
            return _lastGoodResult;
        }

        var result = JSON.stringify(obj, null, Number(tabWidth));
        _lastGoodResult = result;

        return result;
    };

    //Dialog
    $scope.showDialog = function (ev,$scope,htmlName,callbackForOk,onRemoving,onComplete,outsideToClose) {
        var useFullScreen = ($mdMedia('sm') || $mdMedia('xs'))  && $scope.customFullscreen;
        var clickOutsideToClose = angular.isDefined(outsideToClose)? outsideToClose : false;

        $mdDialog.show({
                controller: DialogController,
                templateUrl: htmlName,
                parent: angular.element(document.body),
                targetEvent: ev,
                clickOutsideToClose:clickOutsideToClose,
                fullscreen: useFullScreen,
                locals: {
                    theScope: $scope
                },
                onComplete: onComplete,
                onRemoving:  onRemoving
        })
            .then(function(answer) {
                callbackForOk(answer);
            });
        $scope.$watch(function() {
            return $mdMedia('xs') || $mdMedia('sm');
        }, function(wantsFullScreen) {
            $scope.customFullscreen = (wantsFullScreen === true);
        });
    };

    //Устанавливает сортировку для массива
    var reverse = false;
    $scope.setOrder = function (order) {
        reverse = !reverse;
        $scope.order = reverse?"-"+order:order;
    };


    //---------------------------------------- системные методы
    //Получаем url для загрузки видео
    $scope.getVideoUrl = function (id) {
        return "/data/readVideo?className="+className.video+"&id="+id;
    };

    //перевести по ключу
    $scope.translate = function (key) {
        if (!$scope.application || !$scope.application.text || !(key in $scope.application.text)) {
            return key;
        }

        return $scope.application.text[key];

    };

    //создать ссылку на страницу с учетом языка
    $scope.createUrl = function (url) {
        if (!$scope.application) return;
        return '#/' + $scope.application.language + url;
    };

    $scope.addUrlToPath = function (url) {
        return window.location + url;
    };

    $scope.openInNewTab = function(url) {
        window.open(
            url,
            '_blank' // <- This is what makes it open in a new window.
        );
    };

    //смена языка
    $scope.changeLanguage = function (language) {
        if (!$scope.application.language || $scope.application.language === language) {
            return false;
        }
        if ($state.includes('404') || $state.includes('accessDenied')){
            $rootScope.application = pageInfo;
            return true;
        } else {
            var str = window.location.hash.split("/").splice(2).join("/");
            if (str) {
                $state.go("modules", {
                    language: language,
                    path: str
                });
            } else {
                $state.go("home", {
                    language: language
                });
            }
            return true;
        }
    };

    //создает массив для ng-repeat
    $scope.range = function(n) {
        if (!n) return 1;
        return new Array(Math.ceil(n));
    };

    //создаем массив по диапозону
    $scope.getRow = function (index, length, array) {
        var result = [];
        for (var i = index*length; i < length*(index+1); i++) {
            if (array.length <= i) return result;
            result.push(array[i]);
        }
        return result;
    };

    //---------------------------------------- методы для меню
    //Разлогиниваемся
    $scope.logout = function(){
        if (!$scope.user) return;
        $http.get("/j_spring_security_logout").success(function(){
            applicationService.action($scope, "user", className.userInfo, "getAuthorizedUser", {},function(){
                $scope.reloadMenu();
                pageService.onLogout();
            });
        });
    };

    $scope.toggelMenu = true;

    $scope.toggelClick = function () {
        $scope.toggelMenu = !$scope.toggelMenu;
        var wrapper = angular.element("#wrapper");
        wrapper.toggleClass("wrapper-left");
    };

    $scope.menuScrollConfig = {
        theme: 'light-3',
        snapOffset: 100,
        advanced: {
            updateOnContentResize: true,
            updateOnSelectorChange: "ul li"
        }
    };

    //scroll для модалок
    $scope.modalScrollConfig = {
        theme: 'dark-3',
        advanced: {
            updateOnContentResize: true,
            updateOnSelectorChange: true
        }
    };

    $scope.userImg = function(id){
        return applicationService.imageHref(className.userInfo,id);
    };

    //--------------------------------------------- опции слайдера
    $scope.carouselData = {
        interval : 5000,
        slides : [
            {
                section: "Programming",
                image: "/resources/image/index/slider/programming.jpg",
                text: "carousel_programming"
            },
            {
                section: "Math",
                image: "/resources/image/index/slider/math.jpg",
                text: "carousel_math"
            },
            {
                section: "Physic",
                image: "/resources/image/index/slider/physic.jpg",
                text: "carousel_physic"
            },
            {
                section: "Design",
                image: '/resources/image/index/slider/design.jpg',
                text: "carousel_design"
            }
        ]
    };

    $scope.reloadMenu = function(callback){
        applicationService.action($scope, "menu", className.menu, "getMenu", {}, function(menu){
            if (angular.isFunction(callback)){
                callback(menu);
            }
        });
    };
    $scope.reloadMenu(function(menu){
        $scope.cardsData = {
            title : "ourCourses",
            cardsInRow : 3,
            cards : menu.items,
            prefix : ''
        };
    });

    $scope.openSocialLink = function(name){
        var object = $.grep($scope.mainLinks, function(e){ return e.name == name; });
        if (object[0].link) {
            $scope.openInNewTab(object[0].link);
        }
    };


    applicationService.list($scope,"mainLinks" , className.socialLinks);
    applicationService.action($scope, "user", className.userInfo, "getAuthorizedUser", {});

    applicationService.list($scope , "programmingLanguages",className.programmingLanguages);
    applicationService.list($scope , "programmingStyles",className.programmingStyles);

    applicationService.action($scope , "countries" ,className.country , "getCountries",{
        language : "Ru"
    });
});

model.controller("treeListCtrl" , function ($scope) {
    $scope.setCurrentItem = function(item , level){
        item.isOpen = !item.isOpen;
        $scope.getData().callback(item);

        if (level) {
            $scope.currentMenuItem.level = level;
        }
    };
});

model.controller("videoCtrl",function($scope){
    initVideoPlayer();

    var videoUrl = $scope.getVideoUrl(1);
    $scope.url = {type: "video/mp4", src: videoUrl};

    $scope.open = function() {
        if (!player ||player.currentSrc() != $scope.url.src) {
            player.src($scope.url);
            player.play();
        } else {
            player.play();
        }
        $('#videoModal').modal('show');

    };

    $scope.close = function(){
        if (player) {
            player.pause();
        }
    };

    $('#videoModal').on("hidden.bs.modal",function(){
        $scope.close();
    });
});

//select value
model.controller("selectCtrl",function($scope,$sce,$filter,$document) {
    $scope.choose = false;
    var isModelOpen = false;
    $scope.model =  $scope.getData().defaultValue in $scope ? $scope[$scope.getData().defaultValue] : "";
    $scope.modalModel;
    $scope.selectModalValue;
    $scope.selectValue;

    $scope.filter = $scope.getData().filter;
    $scope.id = $scope.getData().id;
    $scope.count = $scope.getData().count;
    $scope.class = $scope.getData().class;
    $scope.callback = angular.isFunction($scope.getData().callback)? $scope.getData().callback : null;
    $scope.list = [];
    var selector = '#' + $scope.id;

    $scope.closeModal = function(){
        $(selector).modal("hide");
        $scope.resetActiveElementInModal();
        $scope.modalModel = "";
        $scope.selectModalValue = null;
        isModelOpen = false;
    } ;
    $scope.getItem = function (item) {
        if (!item) return "";
        if (item.$$unwrapTrustedValue) {
            return item;
        } else {
            return item[$scope.filter];
        }
    };

    $scope.getList = function(){
        if ($scope.getData().parentScope) {
            $scope.list =  $scope.getData().listName in  $scope.getData().parentScope ? $scope.getData().parentScope[$scope.getData().listName] : []
        } else {
            $scope.list = $scope.getData().listName in $scope ? $scope[$scope.getData().listName] : [];
        }
        return $scope.list;
    };

    $scope.getFilteredData = function (isModal) {
        var list = $scope.getList();

        var filter = {};
        if (!$scope.filter) {
            filter = isModal?$scope.modalModel:$scope.model;
        } else {
            filter[$scope.filter] = isModal?$scope.modalModel:$scope.model;
        }
        var filteredData = $filter('filter')(list,filter);
        if (!isModal) {
            filteredData = $filter('limitTo')(filteredData, $scope.count);

            if (filteredData) {
                var valid = true;

                if (filteredData.length === 1) {
                    if ($scope.choose && $scope.model.toString() === $scope.getItem(filteredData[0]).toString()) {
                        $scope.setModel(filteredData[0]);
                    }
                } else {
                    if (filteredData.length === 0) {
                        if ($scope.isRequired()) {
                            valid = false;
                        }
                    }
                }
            }
            if (!$scope.choose && $scope.model && !($scope.model.toString() === $scope.getItem(filteredData[0]).toString())){
                valid = false;
            }
            $scope.selectForm['main-select'].$setValidity("selectValue", valid);
            $scope.setValid();
        } else {
            if (isModelOpen) {
                if (filteredData.length === 0) {
                    $scope.selectForm['search-input'].$setValidity("searchValue", false);
                } else {
                    $scope.selectForm['search-input'].$setValidity("searchValue", true);
                }
            }
        }
        return filteredData;
    };

    $scope.empty = function(isModal){
          return $scope.getFilteredData(isModal).length === 0;
    };

    $scope.setSelect = function (value) {
        $scope.choose = value;
    };

    $scope.onEvent = function (event) {
        var elem = angular.element(event.currentTarget);
        switch (event.type) {
            case "mouseenter":
                elem.addClass("active");
                break;
            case "mouseleave":
                elem.removeClass("active");
                break;
        }
    };

    $scope.resetModel = function () {
        $scope.model = "";
        $scope.selectValue = null;
        $scope.choose = false;
    };

    $scope.setModel = function (value) {
        $scope.model = getValue(value);
        $scope.selectValue = value;
        $scope.choose = false;
        $scope.callback(value);
    };

    $scope.open = function () {
        $(selector).modal({
            backdrop: 'static',
            keyboard: false
        });
        $(selector).modal('show');
        $(selector+" .table-content").height(getHeight());
        isModelOpen = true;
    };

    var currentElement;
    $scope.resetActiveElementInModal = function(){
        if (currentElement) {
            currentElement.removeClass("info");
            currentElement = null;
        }
    };

    $scope.setModalModel = function(event,value){
        $scope.resetActiveElementInModal();
        var elem = angular.element(event.currentTarget);
        currentElement = elem;
        elem.addClass("info");
        $scope.selectModalValue = value;
    };

    $scope.saveModalModel = function(){
        $('#' + $scope.id).modal('hide');
        $scope.setModel($scope.selectModalValue);
        $scope.resetActiveElementInModal();
    };

    //закрытие подсказки, если щелчок происходдит за пределами элемнта
    $scope.hideSelect = function(){
        $scope.$apply(function () {
            if ($scope.choose) {
                $scope.getFilteredData();
                $scope.choose = false;
            }
        });
    };

    $scope.isRequired = function(){
        var val = $scope.getData().required;

        if (!val) return false;

        if (angular.isFunction(val)){
            return val();
        } else{
            return val;
        }
    };

    //есть ли запрет на редактирование
    $scope.isDisabled = function(){
        var val = $scope.getData().disable;

        if (!val) return false;

        if (angular.isFunction(val)){
            return val();
        } else{
            return val;
        }
    };

    $scope.setValid = function(){
        var val = $scope.getData().isValid;

        if (!val) return false;
        if (angular.isFunction(val)) {
            val($scope.selectForm['main-select'].$valid);
        }
    };

    //scroll для таблицы
    $scope.selectScrollConfig = angular.merge({setHeight: getHeight()}, $scope.modalScrollConfig);

    //ожидание сброса значения
    $scope.$on('reset'+$scope.id.capitalizeFirstLetter()+'Event', function(event, args) {
        $scope.resetModel();
    });

    //подсчет высоты основного содержания модалки
    function getHeight(){
        var height = $scope.getData().maxHeight? $scope.getData().maxHeight: 400;
        var temp = 40 * $scope.getList().length;
        return !temp || temp > height? height : temp;
    }

    //текст отображающийся в input
    function getValue(value){
        if (angular.isString(value) || value.$$unwrapTrustedValue) {
            return value;
        } else {
            return value[$scope.filter];
        }
    }

});

model.directive("hideOptions",function($document){
    return {
        restrict: 'A',
        scope:{
            "callback":"&hideOptions"
        },
        link: function (scope, element, attr) {
            var input = element.find("input")[0];
            var button = element.find("button")[0];
            if (scope.callback && angular.isFunction(scope.callback)){
                $document.on("click", function (event) {
                    switch (event.target){
                        case input:
                        case button:
                            return;
                    }

                    scope.$apply(scope.callback());

                });
            }
        }
    };
});

//crop image
model.controller("selectImgCtrl", function($scope){
    var getDataSrc = function(){
        return $scope.getData().src+"&" + new Date();
    };

    $scope.originalImg = "";

    var defaultImage = "/resources/image/default/camera.png";

    $scope.uploadFile = function(file,$event) {
        if (file) {
            // ng-img-crop
            var imageReader = new FileReader();
            imageReader.onload = function(image) {
                if ($scope.showModal()) {
                    initModalImage(image,$event);
                } else {
                    $scope.$apply(function($scope) {
                        oldImageSrc = getDataSrc();
                        $scope.originalImg = image.target.result;
                        $scope.onChange($scope.originalImg);
                    });

                }
            };
            imageReader.readAsDataURL(file);
        }
    };


    $scope.getAreaType = function(){
        if ($scope.getData().areaType && angular.isString($scope.getData().areaType)) {
            var type = $scope.getData().areaType.toLowerCase();
            switch (type) {
                case "square":
                case "circle":
                    return type;
            }
        }
        return "circle";
    };

    $scope.onChange = function (element) {
        if (!element) return;

        if (angular.isFunction($scope.getData().save)) {
            var file = base64ToBlob(element);
            $scope.getData().save(file);
        }
    };

    function initModalImage(image,event){
        $scope.$apply(function($scope) {
            $scope.originalImg = image.target.result;
            $scope.item = {
                original : $scope.originalImg,
                cropImage:  $scope.croppedImg
            };
            if (!dialogShown) {
                $scope.showDialog(event, $scope, "cropModal.html",$scope.onChange,function(){
                    dialogShown = false;
                    oldImageSrc = getDataSrc();
                    $scope.originalImg = $scope.item.cropImage;
                });
                dialogShown = true;
            }
        });
    }

    $scope.showModal = function(){
        if ($scope.getData()) {
            return $scope.getData().isInModal ? $scope.getData().isInModal : false;
        }
        return false;
    };

    $scope.getClass = function(){
        return $scope.getAreaType() === "circle" ? "img-circle" : "";
    };

    $scope.getImage = function(){
        var notUseDefault = $scope.getData().notUseDefault;
        if (notUseDefault || oldImageSrc) {
            var image = getDataSrc();
            if (image) {
                if (!oldImageSrc) {
                    $scope.originalImg = image;
                } else {
                    if (oldImageSrc !== image){
                        $scope.originalImg =  image;
                        oldImageSrc = "";
                    }
                }
                return $scope.originalImg;
            } else {
                $scope.originalImg = ""
            }
        }
        return defaultImage;
    };

    $scope.getResultSize = function(){
      return $scope.getData().resultSize;
    };

    $scope.getResultQuality = function(){
      return $scope.getData().resultQuality;
    };

    function base64ToBlob(base64Data) {
        var byteString;
        if (base64Data.split(',')[0].indexOf('base64') >= 0) {
            byteString = atob(base64Data.split(',')[1]);
        } else {
            byteString = decodeURI(base64Data.split(',')[1]);
        }
        var mimeString = base64Data.split(',')[0].split(':')[1].split(';')[0];
        var array = [];
        for(var i = 0; i < byteString.length; i++) {
            array.push(byteString.charCodeAt(i));
        }
        var  byteArrays = [new Uint8Array(array)];
        return new Blob(byteArrays, { type: mimeString });
    };
    var oldImageSrc = "";
    var dialogShown = false;
});

//date picker
model.controller("datepickerCtrl", function($scope){
    init();
    var defaultFormat = "dd.MM.yyyy";
    $scope.popupOptions = {
        "current" : $scope.translate("datapiker-today"),
        "clear" : $scope.translate("datapiker-clear"),
        "close" : $scope.translate("datapiker-close")
    };

    $scope.getFormat = function(){
        if ($scope.format){
            return $scope.format;
        }
        return defaultFormat;
    };

    $scope.change = function(){
        if (angular.isFunction($scope.onChange)){
            $scope.onChange($scope.date);
        }
    };

    $scope.status = {
        opened: false
    };
    $scope.date = null;

    $scope.open = function($event) {
        $scope.status.opened = true;
    };

    $scope.$watch("date", function(newVal,oldVal){
        if (newVal != oldVal){
            $scope.change();
        }
    });

    $scope.updateValues = function(){
        init();
    };

    function init() {
        var options = null;
        if ($scope.getData()){
            $scope.required = $scope.getData().required;
            $scope.onChange = $scope.getData().onChange;
            $scope.format = $scope.getData().format;
            options = $scope.getData().options;
        }
        if (options){
            $scope.dataOptions = options;
            $scope.dataOptions["show-weeks"] = $scope.dataOptions["show-weeks"]? true:false;
        } else {
            $scope.dataOptions = {
                "show-weeks": false
            };
        }
    }
});

model.directive('datepickerPopupFormat',function(dateFilter,$parse){
    return{
        restrict:'A',
        require:'?ngModel',
        link:function(scope,element,attrs,ngModel,ctrl){
            ngModel.$parsers.push(function(viewValue){
                 return dateFilter(viewValue,attrs.uibDatepickerPopup);
            });
        }
    }
});

model.controller("textareaCtrl",function($scope,$element){
    var textarea = $element.find('textarea');
    $scope.showingTextarea = false;
    $scope.showEditButton = false;
    $scope.model = {};
    var defaultText = {
        text: $scope.translate("textarea_change_text"),
        use: false
    };

    $scope.toggelEditButton = function(event){
        if ($scope.isReadonly()) return;

        var elem = angular.element(event.currentTarget);
        switch (event.type) {
            case "mouseenter":
                $scope.showEditButton = true;
                if (!$scope.showingTextarea){
                    elem.addClass("edit-text");
                } else {
                    elem.removeClass("edit-text");
                }
                break;
            case "mouseleave":
                $scope.showEditButton = false;
                elem.removeClass("edit-text");
                break;
        }
    };

    $scope.showTextarea = function(event){
        var elem = null;

        if (event){
            elem = angular.element(event.currentTarget).parent().parent();
        }

        $scope.showingTextarea = !$scope.showingTextarea;
        if ($scope.showingTextarea){
            elem.removeClass("edit-text");
            $scope.model.text = $scope.getData().text;
            textarea.focus();
        }
    };

    $scope.save = function(){
        if (!$scope.model.text) return;
        if(angular.isFunction($scope.getData().onSave)){
            $scope.getData().onSave($scope.model.text);
        }

        $scope.showTextarea();
    };

    $scope.isReadonly = function(){
        if(angular.isFunction($scope.getData().readonly)){
            return $scope.getData().readonly();
        }
        return $scope.getData().readonly?$scope.getData().readonly:false;
    };

    $scope.getText = function(){
        if (!$scope.getData().text && !$scope.isReadonly()){
                defaultText.default = true;
                return defaultText;
        }
        defaultText.default = false;
        return $scope.getData();
    };

    $scope.getTextClass = function(){
        return defaultText.default? "default-text" : "";
    };

    $scope.getMaxLength = function(){
        return $scope.getData().maxLength;
    }
});

model.controller("sectionCard",function($scope,$state,applicationService,className){
    applicationService.action($scope, "section" , className.section,"getSectionByNameAndLanguage" , {
        language : $scope.application.language.capitalizeFirstLetter(),
        name :  $scope.getData().sectionName
    } , function(section){
        //Нету секции
        if (!section){
            $state.go("404");
        }
        $scope.sectionCards = {
            title : "categories",
            cardsInRow : 3,
            cards : section.menuItem.subItems,
            prefix : section.menuItem.url
        };
    });

    $scope.sectionImg = function(id){
        return applicationService.imageHref(className.section,id);
    };
});

model.controller("postController",function($scope,codemirrorURL){
    $scope.pasteCode = function() {
        $scope.showDialog(event, $scope, "pasteCode.html", function () {
        },null,refresh);
    };

    $scope.code = "var i = \"hello world\"";
    $scope.theme = $scope.programmingStyles[0];

    $scope.refreshCode = false;
    var refresh = function(){
        $scope.refreshCode = !$scope.refreshCode;
    };

    // The ui-codemirror option
    $scope.cmOption = {
        lineNumbers: true,
        indentWithTabs: true,
        onLoad : function(_editor){
            $scope.modeChanged = function(){
                var mode = $scope.mode.jsFile;
                loadMode(mode,_editor);
            };

            $scope.themeChanged = function(){
                var css = $scope.theme.name.toLowerCase();
                if (css !== "default"){
                    if(!loadTheme(css,_editor)){
                        _editor.setOption("theme", css);
                    }
                } else {
                    _editor.setOption("theme", css);
                }
            };

            $scope.mode = $scope.programmingLanguages[0];
            $scope.modeChanged();
        }
    };

    var loadTheme = function(theme,editor){
        var href = codemirrorURL +"theme/"+theme+".css";

        if  ($("link[href='"+ href+"']").length) return false;

        var link = document.createElement('link');
        link.onload = function(){
            editor.setOption("theme", theme);
        };
        link.rel = "stylesheet";
        link.type = "text/css";
        link.href = href;

        document.getElementsByTagName('head')[0].appendChild(link);
        return true;
    };

    var loadMode = function (val,editor) {
        var  m, mode, spec;
        if (m = /.+\.([^.]+)$/.exec(val)) {
            var info = CodeMirror.findModeByExtension(m[1]);
            if (info) {
                mode = info.mode;
                spec = info.mime;
            }
        } else if (/\//.test(val)) {
            var info = CodeMirror.findModeByMIME(val);
            if (info) {
                mode = info.mode;
                spec = val;
            }
        } else {
            mode = spec = val;
        }
        if (mode) {
            editor.setOption("mode", spec);
            CodeMirror.autoLoadMode(editor, mode);
        }
    };

});

model.controller("folderCardsCtrl" , function ($scope,applicationService) {
    var filter = applicationService.createFilter($scope.getData().className,0,10);
    filter.equal("section.name",$scope.getData().sectionName);
    $scope.folders = [];

    var addLog = function(folder){
        $scope.folders.push(folder);
    };

    var doAction = function(){
        applicationService.filterRequest($scope,"",filter,addLog);
    };

    $scope.goTo = function(url) {
        window.location.href = $scope.addUrlToPath("/"+url);
    };

    doAction();

    $scope.loadMore = function () {
        filter.increase(10);
        doAction();
    };

    $scope.folderImg = function(id){
        return applicationService.imageHref($scope.getData().className,id);
    };

    $scope.splitArray = function(array,even) {
        var tempArr = [];
        for (var i = 0; i < array.length; i++) {
            if(i % 2 === 0 && even) { // index is even
                tempArr.push(array[i]);
            }
            if(i % 2 === 1 && !even) { // index is onn
                tempArr.push(array[i]);
            }
        }
        return tempArr;
    }
});

model.controller("booksCardCtrl" , function($scope,applicationService,className){
    var filter = applicationService.createFilter($scope.getData().className,0,10);
    filter.equal("groupBooks.url",$scope.getData().groupBooks);
    filter.equal("groupBooks.section.name",$scope.getData().sectionName);
    $scope.books = [];

    var addBook = function(book){
        $scope.books.push(book);
    };

    var doAction = function(){
        applicationService.filterRequest($scope,"",filter,addBook);
    };

    $scope.goTo = function(url) {
        window.location.href = $scope.addUrlToPath("/"+url);
    };

    doAction();

    $scope.loadMore = function () {
        filter.increase(10);
        doAction();
    };

    $scope.folderImg = function(id){
        return applicationService.imageHref($scope.getData().className,id);
    };

    $scope.showAdvanced = function(ev) {
        $scope.showDialog(ev,$scope,"createBook.html",function(answer){
            applicationService.action($scope,"bootstrapResult" , className.bootstrap_services,"do",answer,function(result){
                $scope.showToast(result);
                applicationService.list($scope , "bootstrap_services",className.bootstrap_services);
            });
        });
    };

    $scope.splitArray = function(array,even) {
        var tempArr = [];
        for (var i = 0; i < array.length; i++) {
            if(i % 2 === 0 && even) { // index is even
                tempArr.push(array[i]);
            }
            if(i % 2 === 1 && !even) { // index is onn
                tempArr.push(array[i]);
            }
        }
        return tempArr;
    };

    $scope.languageData = {
        "id" : "languages",
        "count" : 3,
        "filter":"title",
        "class" : "input-group-sm",
        "listName" : "lang",
        "required" : true,
        "parentScope" : $scope,
        "callback" : function (value){
            $scope.info.language = value.name;
        }
    };

    applicationService.list($scope,"lang",className.language, function (item) {
        item.title = $scope.translate(item.name.toLowerCase())
    });
});

//dialogs
function DialogController($scope, $mdDialog , theScope) {
    $scope.parentScope = theScope;
    $scope.hide = function () {
        $mdDialog.hide();
    };
    $scope.cancel = function () {
        $mdDialog.cancel();
    };
    $scope.answer = function (answer) {
        $mdDialog.hide(answer);
    };
}

//Настройки для arc
model.service('arcService', function(){

    this.getDataForArc = function (persent, color, defaultColor) {
        return [{
            value: persent,
            color: color
        },
        {
            value: 100 - persent,
            color: defaultColor? defaultColor : "#FFFFFF"
        }];
    };


    this.labels = ["percent","none"];
    this.arcOptions = {
        maintainAspectRatio: false,
        responsive: true,
        segmentShowStroke : false,
        showTooltips : false,
        percentageInnerCutout : 80
    };

    this.mainOption = {
        responsive: true,
        maintainAspectRatio:false,
        segmentShowStroke : false,
        showTooltips : false
    }

});



//инициализацтя пользовательских модулей
new Clipboard('.clipboard');

var model = angular.module("mainApp", ["backend.service", "ngImgCrop" , "ngMaterial","ui.codemirror",'ui.bootstrap.datetimepicker','ui.dateTimeInput']);
model.constant("codemirrorURL", "/resources/bower_components/codemirror/");
model.constant("maxMobileWidth" , 570);
model.constant("maxMobileHeight" , 700);
model.constant("minMobileWidth", 320);

model.config(function (codemirrorURL,$mdThemingProvider) {
    CodeMirror.modeURL = codemirrorURL+ "mode/%N/%N.js";
    $mdThemingProvider.definePalette('primaryPalette', {
        '50': '11171c',
        '100': '11171c',
        '200': '11171c',
        '300': '11171c',
        '400': '11171c',
        '500': '11171c',
        '600': '11171c',
        '700': '11171c',
        '800': '11171c',
        '900': '11171c',
        'A100': '11171c',
        'A200': '11171c',
        'A400': '11171c',
        'A700': '11171c',
        'contrastDefaultColor': 'light',
        'contrastDarkColors': ['50', '100',
            '200', '300', '400', 'A100'],
        'contrastLightColors': undefined
    });
    $mdThemingProvider.theme('default')
        .primaryPalette('primaryPalette');

    var darkPallette = {
        '50': '#11171c',
        '100': '#11171c',
        '200': '#11171c',
        '300': '#11171c',
        '400': '#11171c',
        '500': '#11171c',
        '600': '#11171c',
        '700': '#11171c',
        '800': '#11171c',
        '900': '#11171c',
        'A100': '#11171c',
        'A200': '#11171c',
        'A400': '#11171c',
        'A700': '#11171c'
    };
    $mdThemingProvider
        .definePalette('darkPallette', darkPallette);
    
    $mdThemingProvider.theme('darkTheme')
        .primaryPalette('primaryPalette')
        .accentPalette('darkPallette');
    
    
});

//Главный контроллер
model.controller("mainController", function ($scope,$http,$state,$languages,maxMobileWidth,applicationService,pageService,className,$mdToast,$mdDialog, $mdMedia,$parse) {

    //----------------------------------------------------- инициализация клиентской информации
    $scope.mainScope = $scope;

    //информация о заголовке страници
    $scope.toggelMenu = true;

    $scope.screenHeight = screen.height;
    $scope.screenWidth = screen.width;

    //Сварачивает и разварачивает меню
    $scope.toggelClick = function () {
        $scope.toggelMenu = !$scope.toggelMenu;
        var wrapper = angular.element("#wrapper");
        wrapper.toggleClass("wrapper-main-content");

        var siteFooter = angular.element("#footer-page");
        siteFooter.toggleClass("footer-margin");
    };

    if (screen.width <= maxMobileWidth) {
        $scope.toggelClick();
    }

    $scope.headerData = {
        languages : $languages.languages
    };

    //информация о главном меню на странице
    $scope.menuData = {};

    //scroll для модалок
    $scope.modalScrollConfig = {
        theme: 'dark-3',
        advanced: {
            updateOnContentResize: true,
            updateOnSelectorChange: true
        }
    };

    //--------------------------------------------------------методы по работе с ценами
    $scope.getRealPrice = function (price) {
        if (price.free) {
            return 0;
        }

        return price.price - price.price * (price.discount / 100.0)
    };

    $scope.convertPrice = function (price) {
        if (price.free) {
            return price;
        }
        if (!$scope.user || !$scope.user.currency) {
            //TODO: возможно лучше конвертировать в базовую валюту
            return price;
        }

        if ($scope.user.currency.id === price.currency.id) {
            return price;
        }

        var newPrice = angular.copy(price);
        newPrice.currency = $scope.user.currency;
        newPrice.price = (price.price * price.currency.value) / $scope.user.currency.value;

        return newPrice;
    };

    //--------------------------------------------------------- методы по работе с языком

    //перевести по ключу
    $scope.translate = function (key,isHtml) {
        if (!$scope.application || !$scope.application.text || !(key in $scope.application.text)) {
            return key;
        }

        if (isHtml) {
            return $scope.application.text[key];
        }

        return $scope.application.text[key].toString();
    };

    //Из result получить сообщение
    $scope.getResultMessage = function(result) {
        if (!result || !result.status)
            return "";
        if (result.message) {
            return $scope.translate(result.status.toLowercaseFirstLetter()) + " : " + $scope.translate(result.message.toLowercaseFirstLetter());
        } else {
            return $scope.translate(result.status.toLowercaseFirstLetter());
        }
    };

    //смена языка
    $scope.changeLanguage = function (language) {
        moment.locale(language);
        if (!$scope.application.language || $scope.application.language === language) {
            return false;
        }
        if ($state.includes('404') || $state.includes('accessDenied')){
            return true;
        } else {
            var str = window.location.hash.split("/").splice(2).join("/");
            if (str) {
                $state.go("moduleParam", {
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

    //------------------------------------------------------------------------ методы по работе с пользовтаелем

    //получаем текущего пользователя в системе
    $scope.getAuthorizedUser = function(callback){
        applicationService.action($scope, "user", className.userInfo, "getAuthorizedUser", {},callback);
    };
    $scope.getAuthorizedUser(function (user) {
        if (user != null) {
            user.imageSrc = $scope.userImg(user.id);
        }
    });

    //получения пользовательского изображения
    $scope.userImg = function(id){
        return applicationService.imageHref(className.userInfo,id);
    };

    //------------------------------------------------------------------------ методы по работе с ссылками

    //Возвращает корректный url с учетом языка
    $scope.createUrl = function (url) {
        if (!$scope.application) {
            return '#/' + 'ru' + url;
        }
        return '#/' + $scope.application.language + url;
    };

    $scope.addUrlToPath = function (url) {
        return window.location + url;
    };

    $scope.goTo = function(url,isAbsolutePath) {
        if (isAbsolutePath) {
            window.location.href = url;
        } else {
            window.location.href = $scope.addUrlToPath("/"+url);
        }
    };

    $scope.openInNewTab = function(url) {
        window.open(
            url,
            '_blank' // <- This is what makes it open in a new window.
        );
    };

    //------------------------------------------------------------------------ методы по работе с диалогами и уведомлениями

    //Toast
    $scope.showToast = function (text) {
        $mdToast.show(
            $mdToast.simple()
                .textContent($scope.translate(text))
                .position("bottom right")
                .hideDelay(3000)
        );
    };

    //Dialog
    $scope.showDialog = function (ev,$scope,htmlName,callbackForOk,onRemoving,onComplete,onShowing,outsideToClose) {
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
                onShowing : onShowing,
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

    $scope.showConfirmDialog = function(ev,title,content,ariaLabel,okBtn,cancelBtn,callback) {
        var confirm = $mdDialog.confirm()
            .title(title)
            .textContent(content)
            .targetEvent(ev)
            .ariaLabel(ariaLabel)
            .ok(okBtn ? okBtn : $scope.translate("ok"))
            .cancel(cancelBtn ? cancelBtn : $scope.translate("cancel"));
        $mdDialog.show(confirm).then(callback);
    };

    //-------------------------------------- Утилиты
    $scope.objectIsEmpty = function (obj) {
        if (!obj) return true;
        return Object.keys(obj).length === 0 && JSON.stringify(obj) === JSON.stringify({});
    };

    //Формирует из строки json в читабельный вид
    $scope.toPrettyJSON = function (objStr, tabWidth) {
        var obj;
        obj = $parse(objStr)({});
        var result = JSON.stringify(obj, null, Number(tabWidth));
        return result;
    };

    applicationService.action($scope, "baseCurrency", className.currency, "getBaseCurrency", {});
    
    //-------------------------------------- удалить их
    applicationService.list($scope , "programmingLanguages",className.programmingLanguages);
    applicationService.list($scope , "programmingStyles",className.programmingStyles);
});

model.filter("fileSize" , function () {
    return function (bytes , precision) {
        var kilobyte = 1024;
        var megabyte = kilobyte * 1024;
        var gigabyte = megabyte * 1024;
        var terabyte = gigabyte * 1024;

        if ((bytes >= 0) && (bytes < kilobyte)) {
            return bytes + ' B';

        } else if ((bytes >= kilobyte) && (bytes < megabyte)) {
            return (bytes / kilobyte).toFixed(precision) + ' KB';

        } else if ((bytes >= megabyte) && (bytes < gigabyte)) {
            return (bytes / megabyte).toFixed(precision) + ' MB';

        } else if ((bytes >= gigabyte) && (bytes < terabyte)) {
            return (bytes / gigabyte).toFixed(precision) + ' GB';

        } else if (bytes >= terabyte) {
            return (bytes / terabyte).toFixed(precision) + ' TB';

        } else {
            if (bytes == undefined) return "";
            return bytes + ' B';
        }
    };
});

model.filter("version" , function () {
    return function (version) {
        if (version) {
            return version.majorVersion + "." + version.middleVersion + "." + version.minorVersion;
        }
        return "";
    };
});

model.filter("memo" , function () {
    return function (text, limitChar) {
        if (!text || text.length < limitChar)
            return text;
        return text.substring(0,limitChar) + "...";
    };
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
        if ($scope.isReadonly()) {
            return;
        }

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
        if (!$scope.model.text) {
            return;
        }
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
    };
});

model.controller("postController",['$scope','$timeout','$state','codemirrorURL','TagService','className','applicationService','pageService',
    function($scope,$timeout,$state,codemirrorURL,TagService,className,applicationService,pageService){
    var loadString = function(string,isInit){
        $scope.$broadcast('setCaret');
        $scope.$broadcast('add', string, isInit);
    };

    $scope.tagPool = [];

        applicationService.action($scope,"" , className.userInfo,"getPosts",
            {
                userId: parseInt(pageService.getPathVariable("user",$state.params.path), 10),
                first : 0,
                max:10
            },function(result){
                if(result.message){
                    loadString(result.message, true);
                }
            });
    $scope.content = "";//;
    $scope.readData = '!!!&nbsp;{"type":"ProgramTag","name":"c","code":"var i = \\\"hello world\\\"","options":{"lineNumbers":true,"indentWithTabs":true,"mode":"javascript","theme":"blackboard"}}<br>'
    //first loading
    var initValue = $scope.content;
    $timeout(function() {
        loadString(initValue, true);
    },0);

    //open Tags Pool
    $scope.openPool = function(event){
        $scope.showDialog(event, $scope, "tagPool.html", function(){});
    };

    $scope.currentTag = null;

    $scope.setCurrentTag = function(tag){
        $scope.currentTag = tag;
    };

    //paste code
    var defaultOptions = {
        lineNumbers: true,
        indentWithTabs: true
    };

    $scope.pasteCode = function(event) {
        $scope.showDialog(event, $scope, "pasteCode.html", function (newTagInfo) {
            var newTag = new ProgramTag();
            newTag.setName(newTagInfo.title);
            newTag.setCode(newTagInfo.text);
            newTag.setMode(newTagInfo.mode.mode);
            newTag.setTheme(newTagInfo.theme.name);
            $scope.tagPool.push(newTag);
            var tag = $scope.tagPool[$scope.tagPool.length - 1];
            if (tag) {
                loadString(TagService.getEditableTag($scope.content,tag,$scope.tagPool.length - 1));
                $scope.tag = tag;
            }
        },null,refresh,function(){

        });
    };

    $scope.send = function(){
        applicationService.action($scope,"" , className.userInfo,"addPost",
            {
                userId: parseInt(pageService.getPathVariable("user",$state.params.path), 10),
                text: $scope.content
            },function(result){
        });
    };

    $scope.code = {
        text:"var i = \"hello world\"",
        theme:$scope.programmingStyles[0]
    };

    $scope.refreshCode = false;
    var refresh = function(){
        $scope.refreshCode = !$scope.refreshCode;
    };

    // The ui-codemirror option
    $scope.cmOption = angular.extend({

        onLoad : function(_editor){
            $scope.modeChanged = function(){
                var mode = $scope.code.mode.mode;
                TagService.loadMode(mode,_editor);
            };

            $scope.themeChanged = function(){
                var css = $scope.code.theme.name.toLowerCase();
                if (css !== "default"){
                    if(!TagService.loadTheme(css,_editor)){
                        _editor.setOption("theme", css);
                    }
                } else {
                    _editor.setOption("theme", css);
                }
            };

            $scope.code.mode = $scope.programmingLanguages[0];
            $scope.modeChanged();
        }
    },defaultOptions);



}]);

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

    var responsiveOption = {
        responsive: true,
        maintainAspectRatio:false
    };
    var mainGraphicsOption = {
        segmentShowStroke : false,
        showTooltips : false
    };
    var mainOptions = {};
    angular.extend(mainOptions,mainGraphicsOption);
    angular.extend(mainOptions,responsiveOption);
    
    this.getMainOption = function (visible) {
        if (visible){
            return mainOptions;
        } else {
            return mainGraphicsOption;
        }

    };


});

//tag editor
model.directive('contenteditable', ['$sce', 'TagService', function ($sce, TagService) {
    return {
        restrict: 'A', // only activate on element attribute
        require: '?ngModel', // get a hold of NgModelController
        link: function (scope, element, attrs, ngModel) {
            if (!ngModel) {
                return;
            } // do nothing if no ng-model
            var el = element[0];
            // Specify how UI should be updated
            ngModel.$render = function () {
                element.html($sce.getTrustedHtml(ngModel.$viewValue || ''));
            };

            // Listen for change events to enable binding
            element.on('blur keyup change', function () {
                scope.$evalAsync(read);
                if (!el.lastChild || el.lastChild.nodeName.toLowerCase() != "br") {
                    el.appendChild(document.createElement('br'));
                }
            });
            element.on("keypress", function (event) {
                if (event.which === 13) {
                    scope.$apply(function () {

                    });

                }
            });

            read();

            function read() {
                var html = element.html();
                ngModel.$setViewValue(html);
            }

            ngModel.$parsers.push(function (viewValue) {
                return TagService.parser(viewValue,scope);
            });


        }
    };
}]);

model.directive('contenteditableKeyListener', ['TagService',function (TagService) {
    return {
        link: function (scope, element, attrs) {
            var domElement = element[0];

            function elementContainsSelection(el) {
                var sel;
                if (window.getSelection) {
                    sel = window.getSelection();
                    if (sel.rangeCount > 0) {
                        for (var i = 0; i < sel.rangeCount; ++i) {
                            if (!isOrContains(sel.getRangeAt(i).commonAncestorContainer, el)) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
                return false;
            }

            function isOrContains(node, container) {
                while (node) {
                    if (node === container) {
                        return true;
                    }
                    node = node.parentNode;
                }
                return false;
            }

            /**
             * @param e {Event} - объект генерируемый при возникновении события
             * @param value {String} - вставляемая строка
             * @param isInitValue {Boolean} - является ли строка инициализацией
             *
             *  @description добаление текста по картке
             */
            scope.$on('add', function (e, value, isInitValue) {
                var sel, range;
                if (window.getSelection) {
                    // IE9 and non-IE
                    sel = window.getSelection();
                    if (elementContainsSelection(domElement)) {
                        if (sel.getRangeAt && sel.rangeCount) {
                            range = sel.getRangeAt(0);
                            range.deleteContents();

                            // Range.createContextualFragment() would be useful here but is
                            // non-standard and not supported in all browsers (IE9, for one)
                            var el = document.createElement("div");
                            if (isInitValue){
                                el.innerHTML = TagService.formatter(value,scope);
                            } else {
                                el.innerHTML = value;
                            }

                            var frag = document.createDocumentFragment(),
                                node, lastNode;
                            while ((node = el.firstChild)) {
                                lastNode = frag.appendChild(node);
                            }
                            range.insertNode(frag);

                            // Preserve the selection
                            if (lastNode) {
                                range = range.cloneRange();
                                range.setStartAfter(lastNode);
                                range.collapse(true);
                                sel.removeAllRanges();
                                sel.addRange(range);
                            }
                        }
                    }
                }

            });

            var range;
            element.on("mouseup keyup blur", function () {
                var doc = domElement.ownerDocument || domElement.document;
                var win = doc.defaultView || doc.parentWindow;
                var sel, r;
                if (win.getSelection) {
                    sel = win.getSelection();
                    if (sel.rangeCount > 0) {
                        var selection = win.getSelection();
                        r = selection.getRangeAt(0);
                        if (isOrContains(r.commonAncestorContainer, domElement)) {
                            range = r.cloneRange();
                        }
                    }
                }
            });

            scope.$on('setCaret', function (e) {
                domElement.focus();
                var sel = window.getSelection();
                if (sel) {
                    if (sel.rangeCount > 0) {
                        sel.removeAllRanges();
                    }
                    var r = document.createRange();
                    if (range) {
                        r.setStart(range.startContainer, range.startOffset);
                        r.setEnd(range.endContainer, range.endOffset);
                        r.collapse(true);
                    } else {
                        r.selectNodeContents(domElement);
                        r.collapse(false);
                    }
                    sel.addRange(r);
                }

            });
        }
    };
}]);

//основные опирации для работы с тегами
model.factory("TagService", function (codemirrorURL) {
    var groupSeparator = String.fromCharCode(29);
    var nonBreakingSpace = "&nbsp;";

    var startEditable = '<span contenteditable="false">';
    var middleEditable = ')_';
    var stopEditable = '</span>';

    var beforeParse = function (str) {
        return str.replace(/[\/\\]/g, "\\$&");
    };


    var getEditableTag = function (model, tag, index) {
        var before = '&#8203;', after = "";
        if (!model) {
            after = "<br>";
        }
        return startEditable + (index) + middleEditable + tag.getName() + stopEditable + after;
    };

    /**
     *
     * @param v {String} - строка для разбора
     * @param scope
     * @returns {String}
     *
     * @description Заменяет в строке ссылки пула тегов на json-представление тега
     */
     var parser = function (v,scope){
        var s = v, result = "";
        var startPos = -1, start = 0, stopPos = -1, j = -1;
        var startText = "";
        while ((startPos = s.indexOf(startEditable, stopPos + 1)) !== -1 &&
        (j = s.indexOf(middleEditable, startPos)) !== -1 &&
        (stopPos = s.indexOf(stopEditable, startPos + 1)) !== -1) {
            var value = s.substring(startPos + startEditable.length, j);
            var index = parseInt(value);
            if (isNaN(index)) {
                continue;
            }

            var tag = scope.tagPool[index];
            if (!tag) {
                continue;
            }

            result += s.substring(start, startPos) + startText + groupSeparator + tag.toString() + groupSeparator;
            startText = "";
            start = stopPos + stopEditable.length;
        }
        if (result) {
            result += s.substring(stopPos + stopEditable.length);
            return result;
        }
        return s;
    };


    /**
     * @param value {String} - строка для разбора
     * @param scope
     * @returns {String}
     *
     * @description Заменяет json-представление тега преходящее с сервера на ссылку ппула тегов, при этом происходит добавление в пул
     */
    var formatter = function(value,scope){
            var result = "";
            var startPos = -1, start = 0, stopPos = -1, j = -1;
            var startText = "";
            while ((startPos = value.indexOf(groupSeparator, stopPos + 1)) !== -1 &&
            (stopPos = value.indexOf(groupSeparator, startPos + 1)) !== -1) {
                var stringTag = value.substring(startPos + groupSeparator.length, stopPos);
                if (stringTag){
                    var jsonTag = JSON.parse(stringTag);
                    if (jsonTag) {
                        var type;
                        if (!(type = jsonTag.type)) {
                            continue;
                        }
                        var tag;
                        switch (type) {
                            case "ProgramTag":
                                tag = new ProgramTag();
                                break;
                            default :
                                tag = new Tag();
                        }

                        tag.fromJson(jsonTag);

                        scope.tagPool.push(tag);

                        result += value.substring(start, startPos) + this.getEditableTag(value,tag,scope.tagPool.length - 1);
                    }
                }
                start = stopPos + groupSeparator.length;
            }
            if (result) {
                result += value.substring(stopPos + groupSeparator.length);
                var suffix = "<br>";
                if (result.indexOf(suffix, this.length - suffix.length) === -1){
                    result += "<br>"
                }
                return result;
            }
            return value;
        };

    /**
     * @param value {String} - строка для разбора
     * @param element - DOM-элемент
     *
     * @description Добавляет DOM-элементу значения. Заменяет все json-представления тега читаемымой формой
     */
    var readFormatter = function(value,element,$compile,$scope){
        if (!value) return;
        var result = false;
        var startPos = -1, start = 0, stopPos = -1, j = -1;
        var startText = "";
        while ((startPos = value.indexOf(groupSeparator, stopPos + 1)) !== -1 &&
        (stopPos = value.indexOf(groupSeparator, startPos + 1)) !== -1) {
            var stringTag = value.substring(startPos + groupSeparator.length, stopPos);
            if (stringTag){
                var jsonTag = JSON.parse(stringTag);
                if (jsonTag) {
                    var type;
                    if (!(type = jsonTag.type)) {
                        continue;
                    }
                    var tag;
                    switch (type) {
                        case "ProgramTag":
                            tag = new ProgramTag();
                            tag.fromJson(jsonTag);
                            loadMode(tag.getMode());
                            loadTheme(tag.getTheme());
                            var newScope = $scope.$new();
                            newScope.tag = tag;
                            newScope.translate = $scope.translate;
                            element.append($compile("<div show-tag='tag' translate='translate'/>")(newScope));
                            break;
                        default :
                            tag = new Tag();
                    }
                    element.append(value.substring(start, startPos));
                    result = true;
                }
            }
            start = stopPos + groupSeparator.length;
        }
        if (result) {
            element.append(value.substring(stopPos + groupSeparator.length));
        } else {
            element.append(value);
        }

    };

    var loadTheme = function(theme,editor){
        var href = codemirrorURL +"theme/"+theme+".css";

        if  ($("link[href='"+ href+"']").length) {
            return false;
        }

        var link = document.createElement('link');
        link.onload = function(){
            if (editor) {
                editor.setOption("theme", theme);
            }
        };
        link.rel = "stylesheet";
        link.type = "text/css";
        link.href = href;

        document.getElementsByTagName('head')[0].appendChild(link);
        return true;
    };

    var loadMode = function (val,editor) {
        var  m, mode, spec;
        m = /.+\.([^.]+)$/.exec(val);
        var info;
        if (m) {
            info = CodeMirror.findModeByExtension(m[1]);
            if (info) {
                mode = info.mode;
                spec = info.mime;
            }
        } else if (/\//.test(val)) {
            info = CodeMirror.findModeByMIME(val);
            if (info) {
                mode = info.mode;
                spec = val;
            }
        } else {
            mode = spec = val;
        }
        if (mode) {
            if (editor) {
                editor.setOption("mode", spec);
                CodeMirror.autoLoadMode(editor, mode);
            } else {
                var script = document.createElement('script');
                script.src = CodeMirror.modeURL.replace(/%N/g, mode);
                script.type='text/javascript';
                document.getElementsByTagName('body')[0].appendChild(script);
            }

        }
    };

    return {
        groupSeparator: groupSeparator,
        nonBreakingSpace: nonBreakingSpace,

        getEditableTag: getEditableTag,
        startEditable: startEditable,
        middleEditable: middleEditable,
        stopEditable: stopEditable,

        parser : parser,
        formatter : formatter,
        readFormatter : readFormatter,

        loadMode : loadMode,
        loadTheme : loadTheme

    };
});

model.directive("contentListener",['$compile','TagService',function ($compile,TagService) {
    return {
        scope: {
            content: '=contentListener',
            translate:"&"
        },
        link: function (scope, element, attrs) {
            if (scope.content) {
                TagService.readFormatter(scope.content, element,$compile,scope);
                scope.translate = scope.translate();
            }
        }
    }
}]);

model.directive("showTag",['$compile','TagService',function ($compile,TagService) {
    return {
        restrict: 'A',
        scope: {
            tag: '=showTag',
            translate:'&'
        },
        controller: function ($scope) {
            $scope.translate = $scope.translate()();
            $scope.codeShown = false;
            $scope.showCode = function () {
                $scope.codeShown = !$scope.codeShown;
            };
            $scope.model = {};
        },
        templateUrl:"showTag.html"
    }
}]);

function Tag() {
    var name = "tag";

    this.getName = function () {
        return name;
    };

    this.setName = function (n) {
        if (!name || typeof name !== 'string') {
            console.error("Tag name is not a valid");
            return;
        }
        name = n;
    };

    this.toJson = function () {
        return {type: this.constructor.name, name: this.getName()};
    };

    this.fromJson = function (json) {
        if (!json){
            console.error("JSON not defined");
        }

        var value;
        if (value = json.name){
            this.setName(value);
        }
    };

    this.toString = function () {
        return angular.toJson(this.toJson());
    };
}

function ProgramTag() {
    Tag.call(this);

    var options = {
        lineNumbers: true,
        indentWithTabs: true
    };

    var code = "";

    this.getCode = function () {
        return code;
    };

    this.setCode = function (t) {
        if (!t || !angular.isString(t)) {
            console.error("Tag text is not a valid");
            return;
        }
        code = t;
    };

    this.setMode = function (m) {
        options.mode = m;
    };

    this.getMode = function () {
        return this.options.mode;
    };

    this.setTheme = function (t) {
        options.theme = t;
    };

    this.getTheme = function () {
        return this.options.theme;
    };

    this.setOptions = function(options){
        this.options = options;
    };

    this.getOptions = function () {
        return angular.copy(options);
    };

    this.getReadOnlyOptions = function () {
        return angular.extend({
            readOnly: 'true',
            value : code
        }, options);
    };


    var parentJson = this.toJson;
    this.toJson = function () {
        var json = parentJson.call(this);
        var codeToJSON = code.replace(/"/g, '\\"');
        angular.merge(json, {code: codeToJSON, options: this.getOptions()});
        return json;
    };

    var parentFromJson = this.fromJson;
    this.fromJson = function (json) {
        if (!json){
            console.error("JSON not defined");
            return;
        }
        parentFromJson.call(this,json);
        var value;
        if (value = json.code){
            this.setCode(value);
        }
        if (value = json.options){
            this.setOptions(value);
        }
    };
}


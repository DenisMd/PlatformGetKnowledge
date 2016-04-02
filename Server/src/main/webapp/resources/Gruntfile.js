module.exports = function(grunt){
    //Конфигурация
    grunt.initConfig({
        pkg:grunt.file.readJSON("package.json"),
        jshint : {
            options:{
                curly:true,
                eqeqeq:true,
                noarg:true,
                undef:true,
                eqnull:true,
                browser:true,
                validthis:true,
                globals:{
                    jQuery:true,
                    $:true,
                    console:true,
                    model:true,
                    _lastGoodResult:true,
                    Clipboard:true,
                    angular:true,
                    videojs:true,
                    CodeMirror:true,
                    pageInfo:true,
                    Tag:true,
                    Chart:true,
                    applicationService:true
                }
            },
            "<%= pkg.name %>":{
                src : ["application/**/*.js"]
            }
        }
    });

    //Подгрузка плагинов
    grunt.loadNpmTasks("grunt-contrib-jshint");

    //Регестрируем задачу по умолчанию
    grunt.registerTask('default',["jshint"]);
};
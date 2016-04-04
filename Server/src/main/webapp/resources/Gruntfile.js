module.exports = function(grunt){
    //Конфигурация
    grunt.initConfig({
        pkg:grunt.file.readJSON("package.json"),
        watch : {
            options : {},
            set1: {
                files : ['application/**/*.js'],
                tasks : ['process']
            },
            set2: {
                files : ['css/**/*.css'],
                tasks : ['process2']
            }
        },
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
                    applicationProperties:true,
                    Tag:true,
                    Chart:true,
                    applicationService:true,
                    ProgramTag:true
                }
            },
            "<%= pkg.name %>":{
                src : ["application/**/*.js"]
            }
        },
        concat : {
            options: {
                stripBanners: true,
                banner: '/*! <%= pkg.name %> - v<%= pkg.version %> - ' +
                '<%= grunt.template.today("yyyy-mm-dd") %> \n*/'
            },
            dist1: {
                src: ['application/**/*.js'],
                dest: 'dist/js/mainApplication.js'
            },
            dist2: {
                src: ['css/main/*.css'],
                dest: 'dist/css/main.css'
            },
            dist3:{
                src: ['bower_components/jquery/dist/jquery.min.js',
                      'bower_components/bootstrap/dist/js/bootstrap.min.js',
                      'bower_components/angular/angular.min.js',
                    'bower_components/angular-ui-router/release/angular-ui-router.min.js',

                      'bower_components/angular-bootstrap/ui-bootstrap.min.js',
                      'bower_components/angular-bootstrap/ui-bootstrap-tpls.min.js',
                      'bower_components/angular-material/angular-material.min.js',
                      'bower_components/angular-animate/angular-animate.min.js',
                      'bower_components/angular-file-upload/dist/angular-file-upload.min.js',
                      'bower_components/angular-loading-bar/build/loading-bar.min.js',
                      'bower_components/malihu-custom-scrollbar-plugin/jquery.mCustomScrollbar.concat.min.js',
                      'bower_components/ng-scrollbars/dist/scrollbars.min.js',
                      'bower_components/video.js/dist/video.min.js',
                      'bower_components/ng-img-crop/compile/minified/ng-img-crop.js',
                      'bower_components/angular-animate/angular-animate.js',
                      'bower_components/angular-aria/angular-aria.js',
                      'bower_components/Chart.js/Chart.min.js',
                      'bower_components/clipboard/dist/clipboard.min.js',
                      'bower_components/highlightjs/highlight.pack.min.js',
                      'bower_components/angular-highlightjs/build/angular-highlightjs.min.js',
                      'bower_components/codemirror/lib/codemirror.js',
                      'bower_components/codemirror/addon/mode/loadmode.js',
                      'bower_components/codemirror/mode/meta.js',
                      'bower_components/angular-ui-codemirror/ui-codemirror.js',
                      'bower_components/angular-sanitize/angular-sanitize.min.js'],
                dest: 'dist/js/bower.js'
            }
        },
        uglify : {
            options :{
                banner : "/*Created by Denis Markov |2016*/ \n",
                report: 'min',
                mangle: false
            },
            dist: {
                files:{
                    'dist/js/mainApplicationMin.js' : ['dist/js/mainApplication.js']
                }
            }
        },
        cssmin: {
            options: {
                shorthandCompacting: false,
                roundingPrecision: -1
            },
            target: {
                files: {
                    'dist/css/main-min.css': ['dist/css/main.css']
                }
            }
        }
    });

    //Подгрузка плагинов
    grunt.loadNpmTasks("grunt-contrib-jshint");
    grunt.loadNpmTasks("grunt-contrib-concat");
    grunt.loadNpmTasks("grunt-contrib-watch");
    grunt.loadNpmTasks("grunt-contrib-uglify");
    grunt.loadNpmTasks("grunt-contrib-cssmin");
    grunt.loadNpmTasks("grunt-newer");
    grunt.loadNpmTasks("grunt-bower-concat");

    //Регестрируем задачу по умолчанию
    grunt.registerTask('dist', ['concat:dist1']);
    grunt.registerTask('dist2', ['concat:dist2']);
    grunt.registerTask('process' , ["dist","uglify"]);
    grunt.registerTask('process2' , ["dist2","cssmin"]);
    grunt.registerTask('default',["jshint" ,"concat" ,"uglify", "cssmin"]);
};
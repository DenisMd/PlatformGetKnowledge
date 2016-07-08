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
        concat : {
            options: {
                stripBanners: true,
                banner: '/*! <%= pkg.name %> - v<%= pkg.version %> - ' +
                '<%= grunt.template.today("yyyy-mm-dd") %> \n*/'
            },
            dist1: {
                src: ['application/platform.js','application/getKnowledge.js','application/classes/**/*.js','application/controllers/**/*.js','application/animation/**/*.js','application/module/**/*.js'],
                dest: 'dist/js/mainApplication.js'
            },
            dist2: {
                src: ['css/main/*.css'],
                dest: 'dist/css/main.css'
            },
            extLibs:{
                src: [
                        //license MIT
                        'bower_components/moment/min/moment.min.js',
                        'bower_components/jquery/dist/jquery.min.js',
                        'bower_components/bootstrap/dist/js/bootstrap.min.js',
                        'bower_components/angular/angular.min.js',
                        'bower_components/angular-ui-router/release/angular-ui-router.min.js',
                        'bower_components/angular-material/angular-material.min.js',

                        //Costumize
                        //MIT
                        'bower_components/angular-animate/angular-animate.min.js',
                        'bower_components/angular-file-upload/dist/angular-file-upload.min.js',
                        'bower_components/angular-loading-bar/build/loading-bar.min.js',
                        'bower_components/malihu-custom-scrollbar-plugin/jquery.mCustomScrollbar.concat.min.js',
                        'bower_components/ng-scrollbars/dist/scrollbars.min.js',
                        'bower_components/ng-img-crop/compile/minified/ng-img-crop.js',
                        'bower_components/angular-aria/angular-aria.min.js',
                        'bower_components/Chart.js/Chart.min.js',
                        'bower_components/clipboard/dist/clipboard.min.js',
                        'bower_components/moment/min/moment-with-locales.min.js',


                        //license Apache License Version 2.0
                        'bower_components/video.js/dist/video.min.js',

                        //Mini
                        'dist/js/ext/part-ext-libs.min.js',

                        //Utils
                        'bower_components/codemirror/mode/javascript/javascript.js',
                        'bower_components/angular-sanitize/angular-sanitize.min.js'

                ],
                dest: 'dist/js/ext/external-libs.min.js'
            }
        },
        uglify : {
            options :{
                report: 'min',
                mangle: false,
                preserveComments: 'all'
            },
            dist: {
                files:{
                    'dist/js/main-application.min.js' : ['dist/js/mainApplication.js']
                }
            },
            extLibs: {
                files:{
                    'dist/js/ext/part-ext-libs.min.js' : ['bower_components/codemirror/lib/codemirror.js',
                                                    'bower_components/codemirror/addon/mode/loadmode.js',
                                                    'bower_components/codemirror/mode/meta.js',
                                                    'bower_components/angular-ui-codemirror/ui-codemirror.js',
                                                    'node_modules/angular-bootstrap-datetimepicker/src/js/datetimepicker.js',
                                                    'node_modules/angular-date-time-input/src/dateTimeInput.js',
                                                    'node_modules/angular-bootstrap-datetimepicker/src/js/datetimepicker.templates.js']
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
    grunt.loadNpmTasks("grunt-contrib-concat");
    grunt.loadNpmTasks("grunt-contrib-watch");
    grunt.loadNpmTasks("grunt-contrib-uglify");
    grunt.loadNpmTasks("grunt-contrib-cssmin");
    grunt.loadNpmTasks("grunt-newer");
    grunt.loadNpmTasks("grunt-bower-concat");

    //Регестрируем задачу по умолчанию
    grunt.registerTask("link-all", ["uglify:extLibs","concat:extLibs"]);
};
# PlatformGetKnowledge
*Платформа по разработке пользовательских курсов и продажи их через сеть*

Необходимые компоненты:
* Java 8
* Tomcat 7
* Maven 3
* NodeJs with Npm
* Bower
* Grunt
* Postgresql 9.1

Процесс установки:
1. В application.properties установить настройки подключения к базе данных и основные настройки приложения
2. В GetKnowledge\Server\src\main\webapp\resources выполнить установку bower install
3. В GetKnowledge\Server\src\main\webapp\resources выполнить установку npm install
4. Запустить команду grunt для минифицирования и компановки основных файлов
5. Для разработки front-end-а необходимо также включить watcher - командой grunt watch
6. Выполнить maven clean и maven install, для артифакта Root 
7. С помощью tomcat развернуть .war архив

Выполнить первоначальную установку:
Для этого необходимо выполнить POST-запрос по адресу [domain]/data/action
~ {
     className:com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo
     actionName:do
     data:
     { 
     "initPassword" : "Его hash можно устанавить в application.properties",
     "domain" : "главный домен",
     "email" : "по умолчанию admin",
     "password":"по умолчанию admin",
     "lastName":"фамилия",
     "firstName":"имя"
     }
 }
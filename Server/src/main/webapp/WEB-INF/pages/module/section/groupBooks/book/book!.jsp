<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/book-page/book.css">


<div ng-show="showEditableContent">
    <md-content>
        <md-tabs md-dynamic-height md-border-bottom>
            <md-tab label="{{translate('book_info')}}">
                <md-content class="md-padding" layout="column">
                    <div layout-gt-sm="row" flex>
                        <md-input-container flex-gt-sm>
                            <label>{{translate("name")}}</label>
                            <input ng-model="book.name">
                        </md-input-container>
                        <md-input-container flex-gt-sm>
                            <label>{{translate("book_author_name")}}</label>
                            <input ng-model="book.authorName">
                        </md-input-container>
                    </div>
                    <div flex>
                        <label for="description" class="md-title">{{translate("description")}}</label>
                        <textarea class="form-control book-description" rows="5" id="description" ng-model="book.description"></textarea>
                    </div>
                    <div flex>
                        <p class="book-add-url" ng-click="addUrl()">{{translate("book_add_url")}}</p>
                        <div ng-repeat="url in book.urls" layout="row">
                            <md-input-container flex>
                                <label>{{translate("url") + " " + ($index+1)}}</label>
                                <md-icon class="fa fa-times remove-url" ng-click="removeUrl($index)"></md-icon>
                                <input ng-model="url.name">
                            </md-input-container>
                        </div>
                    </div>
                    <div layout="column" class="book-tags" flex ng-init="newBook.tags = []" flex>
                        <div class="md-title" flex>{{translate("tags")}}</div>
                        <md-chips ng-model="book.tagsName" flex></md-chips>
                    </div>
                    <div layout="row" class="update-btn-container" layout-align="center" flex>
                        <button flex="none" class="btn btn-default update-book-btn" ng-click="updateBook(book)">{{translate("update")}}</button>
                    </div>
                </md-content>
            </md-tab>
            <md-tab label="{{translate('book_cover')}}">
                <md-content class="md-padding">
                    <md-tab-body>
                        <md-content class="md-padding">
                            <module-template name="inputs/selectImage" data="croppedImg"></module-template>
                        </md-content>
                    </md-tab-body>
                </md-content>
            </md-tab>
            <md-tab label="{{translate('book_data')}}">
                <md-content class="md-padding">
                    <module-template name="inputs/uploadFiles" data="uploadData"></module-template>
                    <%--<input type="file" nv-file-select uploader="uploader"/><br/>--%>
                    <%--<ul>--%>
                        <%--<li ng-repeat="item in uploader.queue">--%>
                            <%--Name: <span ng-bind="item.file.name"></span><br/>--%>
                            <%--<button ng-click="item.upload()">{{translate("upload")}}</button>--%>
                        <%--</li>--%>
                    <%--</ul>--%>
                </md-content>
            </md-tab>
        </md-tabs>
    </md-content>
</div>


<div layout="row" class="book-change-row" layout-align-gt-sm="start start" layout-align="center center">
    <button class="btn btn-default update-book-btn" ng-click="showEditableContent = !showEditableContent" ng-show="book.editable" flex="none">
        {{translate("book_change")}}
    </button>
</div>

<div layout-gt-sm="row"  layout-align-gt-sm="start start" layout-align="start center" layout="column">
  <div flex="none" >
      <img ng-src="{{book.coverUrl}}" class="book-cover">
  </div>
  <div flex="65" class="book-title">
      <h1 class="text-center">{{book.name}}</h1>
      <p class="description text-justify">
          {{book.description}}
      </p>
      <p class="book-date">{{translate("book_create_date")}} : {{book.createDate|date:"medium"}}</p>
      <md-chips ng-model="book.tagsName" readonly="true"></md-chips>
  </div>
  <div flex="35" layout="column" layout-align="start center">
      <p class="text-center book-author-title">{{translate("book_uploader")}}<p>
      <a ng-href="{{book.owner.userUrl}}" class="book-author-name">
          <img ng-src="{{book.owner.imageSrc}}" class="main-image"/>
          <p class="main-label">{{book.owner.firstName}} {{book.owner.lastName}}</p>
      </a>
      <p>{{book.owner.speciality}}</p>
  </div>
</div>

<div layout="column" layout-align="start center" class="book-links">

    <div class="book-links-title">
        {{translate("book_links")}}
    </div>

    <div ng-repeat="url in book.links track by $index" class="book-link-margin">
        <i class="fa fa-external-link" aria-hidden="true"></i>
        <a ng-href="{{url}}" class="book-link" target="_blank">{{url}}</a>
    </div>

    <div ng-if="book.fileName">
        <i class="fa fa-cloud-download" aria-hidden="true"></i>
        <a ng-href="{{book.downloadUrl}}" download>{{translate("book_download_link")}} ---> '{{book.fileName}}'</a>
    </div>
</div>

<div>
    <module-template name="comments/simpleComment" data="commentData"></module-template>
</div>

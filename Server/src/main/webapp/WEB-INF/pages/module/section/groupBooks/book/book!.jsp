<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/book-page/book.css">


<div ng-show="showEditableContent">
    <md-content>
        <md-tabs md-dynamic-height md-border-bottom>
            <md-tab label="{{translate('book_info')}}">
                <md-content class="md-padding">
                    <div>
                        <md-input-container>
                            <label>{{translate("name")}}</label>
                            <input ng-model="book.name">
                        </md-input-container>
                    </div>
                    <h2 class="md-title">{{translate("tags")}}</h2>
                    <div>
                        <md-chips ng-model="book.tagsName"></md-chips>
                    </div>
                    <div>
                        <label for="description">{{translate("description")}}</label>
                        <textarea class="form-control" rows="5" id="description" ng-model="book.description"></textarea>
                    </div>
                    <div>
                        <a href="" ng-click="addUrl()">{{translate("book_add_url")}}</a>
                        <div>
                            <div ng-repeat="url in book.urls">
                                <md-input-container>
                                    <label>{{translate("url") + " " + ($index+1)}}</label>
                                    <input ng-model="url.name"> (<a href="" ng-click="removeUrl($index)">X</a>)
                                </md-input-container>
                            </div>
                        </div>
                    </div>
                    <div>
                        <md-button class="md-raised md-primary" ng-click="updateBook(book)">{{translate("update")}}</md-button>
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
                    <input type="file" nv-file-select uploader="uploader"/><br/>
                    <ul>
                        <li ng-repeat="item in uploader.queue">
                            Name: <span ng-bind="item.file.name"></span><br/>
                            <button ng-click="item.upload()">{{translate("upload")}}</button>
                        </li>
                    </ul>
                </md-content>
            </md-tab>
        </md-tabs>
    </md-content>
</div>


<div layout="row" class="book-change-row" layout-align-gt-sm="start start" layout-align="center center">
    <button class="btn btn-default" ng-click="showEditableContent = !showEditableContent" ng-show="book.editable" flex="none">
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
      <p class="text-center book-author-title">{{translate("author")}}<p>
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

    <div ng-repeat="url in book.links">
        <i class="fa fa-external-link" aria-hidden="true"></i>
        <a ng-href="{{url}}" target="_blank">{{url}}</a>
    </div>

    <div ng-if="book.fileName">
        <i class="fa fa-cloud-download" aria-hidden="true"></i>
        <a ng-href="{{book.downloadUrl}}" download>{{translate("book_download_link")}} ---> '{{book.fileName}}'</a>
    </div>
</div>

<div>
    <module-template name="comments/simpleComment" data="commentData"></module-template>
</div>

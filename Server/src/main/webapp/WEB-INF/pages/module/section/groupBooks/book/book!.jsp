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
                        <md-chips ng-model="book.tagsName" readonly="false"></md-chips>
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
                                    <label>{{translate("url") + ($index+1)}}</label>
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
                            <module-template name="inputs/selectImage" data="getCropImageData()"></module-template>
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

<div layout="row">
  <div flex>
      <img ng-src="{{book.coverUrl}}" class="book-cover">
  </div>
  <div flex>
      <h1 class="text-center">{{book.name}}</h1>
      <p class="description text-justify">
          {{book.description}}
      </p>
  </div>
  <div flex layout="column" layout-align="center center">
      <div>
          <button class="btn btn-default" ng-click="showEditableContent = !showEditableContent" ng-show="book.editable">{{translate("change")}}</button>
      </div>
      <div>
          <h3>{{translate("author")}}</h3>
          <a ng-href="#/' +application.language+'/user/' + book.owner.id">
              <img ng-src="{{book.owner.imageSrc}}" class="main-image"/>
              <p class="main-label">{{user.firstName}} {{user.lastName}}</p>
          </a>
      </div>
      <div>
          <h3>{{translate("book_create_date")}}</h3>
          <p>{{book.createDate|date:"medium"}}</p>
      </div>

      <div>
          <h3>{{translate("tags")}}</h3>
          <md-chips ng-model="book.tagsName" readonly="true"></md-chips>
      </div>
  </div>
</div>

<div>
  {{translate("links")}}
  <ul>
    <li ng-repeat="url in book.links">
      <a ng-href="{{url}}">{{url}}</a>
    </li>
  </ul>
  <div>
    <a ng-href="{{bookData()}}" download>{{"data_link"}}</a>
  </div>
</div>

<div>
    <module-template name="comments/simpleComment" data="commentData"></module-template>
</div>

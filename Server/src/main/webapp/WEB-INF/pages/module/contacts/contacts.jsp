<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/contacts/contacts.css">

<div class="form-group">
  <label for="title">{{translate("title")}}</label>
  <input type="text" class="form-control" id="title" ng-model="message.title">
</div>

<div class="form-group">
  <label for="message">{{translate("message")}}</label>
  <textarea class="form-control" rows="5" id="message" ng-model="message.message"></textarea>
</div>

<label for="type">{{translate("type")}}</label>
<select class="form-control" id="type" ng-model="message.type">
  <option ng-repeat="type in types" value="{{type}}">{{translate("contacts_"+type)}}</option>
</select>

<div class="checkbox" ng-show="user">
  <label><input type="checkbox" ng-model="message.isReply">{{translate("contacts_isReply")}}</label>
</div>

{{message}}

<button class="btn btn-primary" ng-click="sendMessage(message)">{{translate("contacts_send")}}</button>
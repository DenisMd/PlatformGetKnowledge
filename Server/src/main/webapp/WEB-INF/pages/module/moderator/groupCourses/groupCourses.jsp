<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="row">
    <div class="col-lg-4 col-md-4">
        <module-template data="treeViewListData" name="tree-view/treeList"></module-template>
    </div>
    <div class="col-lg-8 col-md-8" ng-if="currentMenuItem">
        right block
    </div>
</div>

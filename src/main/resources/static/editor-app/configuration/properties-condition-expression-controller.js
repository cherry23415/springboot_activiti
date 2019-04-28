/*
 * Activiti Modeler component part of the Activiti project
 * Copyright 2005-2014 Alfresco Software, Ltd. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

/*
 * Condition expression
 */

var KisBpmConditionExpressionCtrl = ['$scope', '$modal', function ($scope, $modal) {
    // Config for the modal window
    var opts = {
        template: 'editor-app/configuration/properties/condition-expression-popup.html?version=' + Date.now(),
        scope: $scope
    };
    // Open the dialog
    $modal(opts);
}];
var KisBpmConditionPopupCtrl = ['$scope', '$modal', function ($scope) {
    var messenger = new Messenger('outIframe', 'projectName');
    	   messenger.listen(function(){
               window.document.getElementById('closeBtn').click()
    	 });
    $scope.close = function () {
        $scope.property.mode = 'read';
        $scope.$hide();
        findConditionList();
    };
    var urlStr = document.referrer+"#/sysSetting/formFlow/conditionFormula/edit?activity=true";
    var iframeStr = "<iframe src='"+urlStr+"' width='100%' height='600px' style='border:none'></iframe>"
    setTimeout(function () {
        document.getElementById('iframeText').innerHTML = iframeStr;
    },500)

}];
function findConditionList() {
    xmlHttp = GetXmlHttpObject();
    if (xmlHttp == null) {
        alert('您的浏览器不支持AJAX！');
        return;
    }
    var modelId = window.location.search.split('=')[1];
    var url = baseUrlRel + "/design/cond/list/all?modelId="+modelId;
    xmlHttp.open("GET", url, true);
    xmlHttp.onreadystatechange = getCondGet;//发送事件后，收到信息了调用函数
    xmlHttp.send();
}

function getCondGet() {
    if (xmlHttp.readyState == 1 || xmlHttp.readyState == 2 || xmlHttp.readyState == 3) {
        // 本地提示：加载中
    }
    if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
        var d = xmlHttp.responseText;
        // 处理返回结果
        var result = eval('(' + d + ')');
        if (result.status == 0) {
            var relList = result.data;
            var strSc = "<option value=''> 请选择 </option>";
            for (var i = 0; i < relList.length; i++) {
                if (staticConditionVal != null && relList[i].expression == staticConditionVal) {
                    strSc = strSc + "<option value='" + relList[i].expression + "' selected>" + relList[i].condName + "</option>";
                } else {
                    strSc = strSc + "<option value='" + relList[i].expression + "'>" + relList[i].condName + "</option>";
                }
            }
            document.getElementById('staticCondition').innerHTML = strSc;
        } else {
            alert("获取条件表达式列表失败，请刷新重试");
        }
    }
}
var staticConditionVal = null;

var KisBpmConditionExpressionPopupCtrl = ['$scope', '$modal', '$translate', '$http', function ($scope,$modal, $translate, $http) {
     findConditionList();
    // Put json representing condition on scope
    if ($scope.property.value !== undefined && $scope.property.value !== null) {

        $scope.conditionExpression = {value: $scope.property.value};
        staticConditionVal = $scope.conditionExpression.value;
    } else {
        $scope.conditionExpression = {value: ''};
    }
    $scope.save = function () {
        $scope.property.value = $scope.conditionExpression.value;
        $scope.updatePropertyInModel($scope.property);
        $scope.close();
    };
    // Close button handler
    $scope.close = function () {
        $scope.property.mode = 'read';
        $scope.$hide();
    };
    $scope.add = function () {
        $scope.hosts = document.referrer+'#/sysSetting/formFlow/conditionFormula/edit?activity=true';
       // console.log('document.referrer',document.referrer)
        //console.log('window.location.host',window.location.host)
        var opts = {
            template: 'editor-app/configuration/properties/condition-formula-popup.html?version=' + Date.now(),
            scope: $scope
        };
        // Open the dialog
        $modal(opts);
    };
}];


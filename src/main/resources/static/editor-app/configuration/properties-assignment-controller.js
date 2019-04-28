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
 * Assignment
 */
var KisBpmAssignmentCtrl = ['$scope', '$modal', function ($scope, $modal) {

    // Config for the modal window
    var opts = {
        template: 'editor-app/configuration/properties/assignment-popup.html?version=' + Date.now(),
        scope: $scope
    };
    // Open the dialog
    $modal(opts);
}];

var KisBpmFormulaPopupCtrl = ['$scope', '$modal', function ($scope, $modal) {
    var messenger = new Messenger('outIframe', 'projectName');
    messenger.listen(function(){
        window.document.getElementById('closeBtn').click()
    });
    $scope.close = function () {
        $scope.property.mode = 'read';
        $scope.$hide();
        findRelationList();
    };
    var urlStr = document.referrer+"#/sysSetting/formFlow/flowFormula/edit?activity=true";
    var iframeStr = "<iframe src='"+urlStr+"' width='100%' height='600px' style='border:none'></iframe>"
    setTimeout(function () {
        document.getElementById('iframeText').innerHTML = iframeStr;
    },500)

}];
var xmlHttp;
var baseUrlRel = window.document.location.protocol + "//" + window.document.location.host;
function GetXmlHttpObject() {
    if (window.XMLHttpRequest) {
        // code for IE7+, Firefox, Chrome, Opera, Safari
        xmlhttp = new XMLHttpRequest();
    } else {// code for IE6, IE5
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    return xmlhttp;
}
function findRelationList() {
    xmlHttp = GetXmlHttpObject();
    if (xmlHttp == null) {
        alert('您的浏览器不支持AJAX！');
        return;
    }
    var modelId = window.location.search.split('=')[1];
    var url = baseUrlRel + "/design/relation/list/all?modelId="+modelId;
    xmlHttp.open("GET", url, true);
    xmlHttp.onreadystatechange = getRelationGet;//发送事件后，收到信息了调用函数
    xmlHttp.send();
}

function getRelationGet() {
    if (xmlHttp.readyState == 1 || xmlHttp.readyState == 2 || xmlHttp.readyState == 3) {
        // 本地提示：加载中
    }
    if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
        var d = xmlHttp.responseText;
        // 处理返回结果
        var result = eval('(' + d + ')');
        if (result.status == 0) {
            var relList = result.data;
            var strA = "<option value=''> 请选择 </option>";
            for (var i = 0; i < relList.length; i++) {
                if (assigneeFieldVal != null && relList[i].expression == assigneeFieldVal) {
                    strA = strA + "<option value='" + relList[i].expression + "' selected>" + relList[i].relateName + "</option>";
                } else {
                    strA = strA + "<option value='" + relList[i].expression + "'>" + relList[i].relateName + "</option>";
                }
            }
            var strU = "<option value=''> 请选择 </option>";
            for (var i = 0; i < relList.length; i++) {
                if (userFieldVal != null && relList[i].expression == userFieldVal) {
                    strU = strU + "<option value='" + relList[i].expression + "' selected>" + relList[i].relateName + "</option>";
                } else {
                    strU = strU + "<option value='" + relList[i].expression + "'>" + relList[i].relateName + "</option>";
                }
            }
            document.getElementById('assigneeField').innerHTML = strA;
            document.getElementById('userField').innerHTML = strU;
        } else {
            alert("获取审批表达式列表失败，请刷新重试");
        }
    }
}
var assigneeFieldVal = null;
var userFieldVal = null;
var KisBpmAssignmentPopupCtrl = ['$scope','$modal', function ($scope, $modal) {
    findRelationList();
    // Put json representing assignment on scope
    if ($scope.property.value !== undefined && $scope.property.value !== null
        && $scope.property.value.assignment !== undefined
        && $scope.property.value.assignment !== null) {
        $scope.assignment = $scope.property.value.assignment;
        assigneeFieldVal = $scope.assignment.assignee;
    } else {
        $scope.assignment = {};
    }

    if ($scope.assignment.candidateUsers == undefined || $scope.assignment.candidateUsers.length == 0 ) {
        $scope.assignment.candidateUsers = [{value: ''}];
        userFieldVal = '';
    } else {
        userFieldVal = $scope.assignment.candidateUsers[0].value;
    }

    // Click handler for + button after enum value
    var userValueIndex = 1;
    $scope.addCandidateUserValue = function (index) {
        $scope.assignment.candidateUsers.splice(index + 1, 0, {value: 'value ' + userValueIndex++});
    };

    // Click handler for - button after enum value
    $scope.removeCandidateUserValue = function (index) {
        $scope.assignment.candidateUsers.splice(index, 1);
    };

    $scope.save = function () {
        $scope.property.value = {};
        handleAssignmentInput($scope);
        $scope.property.value.assignment = $scope.assignment;
        $scope.updatePropertyInModel($scope.property);
        $scope.close();
    };

    // Close button handler
    $scope.close = function () {
        handleAssignmentInput($scope);
        $scope.property.mode = 'read';
        $scope.$hide();
    };
    $scope.add = function () {
        // Config for the modal window
        var opts = {
            template: 'editor-app/configuration/properties/flow-formula-popup.html?version=' + Date.now(),
            scope: $scope
        };
        // Open the dialog
        $modal(opts);
    };

    var handleAssignmentInput = function ($scope) {
        if ($scope.assignment.candidateUsers) {
            var emptyUsers = true;
            var toRemoveIndexes = [];
            for (var i = 0; i < $scope.assignment.candidateUsers.length; i++) {
                if ($scope.assignment.candidateUsers[i].value != '') {
                    emptyUsers = false;
                }
                else {
                    toRemoveIndexes[toRemoveIndexes.length] = i;
                }
            }

            for (var i = 0; i < toRemoveIndexes.length; i++) {
                $scope.assignment.candidateUsers.splice(toRemoveIndexes[i], 1);
            }
            if (emptyUsers) {
                $scope.assignment.candidateUsers = undefined;
            }
        }
    };
}];
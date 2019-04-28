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
 * Execution listeners
 */

var KisBpmSelectCtrl = ['$scope', function ($scope) {
    findCodeList();
    if ($scope.property.value == undefined && $scope.property.value == null) {
        $scope.property.value = 'None';
    }

    $scope.selectChanged = function () {
        $scope.updatePropertyInModel($scope.property);
    };

    function findCodeList() {
        xmlHttp = GetXmlHttpObject();
        if (xmlHttp == null) {
            alert('您的浏览器不支持AJAX！');
            return;
        }
        var modelId = window.location.search.split('=')[1];
        var url = baseUrlRel + "/design/form/all/list?notOtherModel=" + modelId;
        xmlHttp.open("GET", url, true);
        xmlHttp.onreadystatechange = getCode;//发送事件后，收到信息了调用函数
        xmlHttp.send();
    }

    function getCode() {
        if (xmlHttp.readyState == 1 || xmlHttp.readyState == 2 || xmlHttp.readyState == 3) {
            // 本地提示：加载中
        }
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {

            // 处理返回结果
            var result = eval('(' + xmlHttp.responseText + ')');
            if (result.status == 0) {
                var relList = result.data;
                var strSc = "<option value=''> 请选择 </option>";
                for (var i = 0; i < relList.length; i++) {
                    strSc = strSc + "<option value='" + relList[i].formKey + "' id='" + relList[i].formName + "'>" + relList[i].formName + "</option>";
                }
                document.getElementById('formCode').innerHTML = strSc;
            } else {
                alert("获取表单编号失败，请刷新重试");
            }
        }
    }
}];

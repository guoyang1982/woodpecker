<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="../common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>新增用户配置</title>
</head>
<body>
<!-- BEGIN PAGE CONTAINER-->
<div class="container-fluid">
    <!-- BEGIN PAGE CONTENT LIST-->
    <div id="contentList" class="row-fluid">
        <!-- BEGIN FORM-->
        <form id="form_user" action="#" class="form-horizontal">
            <div class="control-group">
                <label class="control-label">用户ID<span class="required">*</span></label>
                <div class="controls">
                    <input type="text" name="loginName" class="span6 m-wrap" value="${userInfo.loginName}" readonly="readonly" />
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">登录密码<span class="required">*</span></label>
                <div class="controls">
                    <input type="password" name="password" class="span6 m-wrap" value=""/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">用户身份<span class="required">*</span></label>
                <div class="controls">
                    <input type="text" name="userRole" class="span6 m-wrap" value="${userInfo.userRole}"/> <a><font color="red">默认是普通用户</font></a>
                </div>
            </div>
            <div class="form-actions">
                <button id="btn_user_edit_save" type="button" class="btn green">保存</button>
                <button id="btn_user_edit_back" type="button" class="btn">返回</button>
            </div>
        </form>
        <!-- END FORM-->
    </div>
    <!-- END PAGE CONTENT LIST-->

</div>
<!-- END PAGE CONTAINER-->
</body>
</html>
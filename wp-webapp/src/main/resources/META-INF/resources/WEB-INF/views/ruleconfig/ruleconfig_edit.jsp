<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="../common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>编辑告警配置</title>
</head>
<body>
<!-- BEGIN PAGE CONTAINER-->
<div class="container-fluid">
    <!-- BEGIN PAGE CONTENT LIST-->
    <div id="contentList" class="row-fluid">
        <!-- BEGIN FORM-->
        <form id="form_ruleconfig" action="#" class="form-horizontal">
            <input type="hidden" name="userName" class="span6 m-wrap" value="${username}"/>
            <input type="hidden" name="ruleId" class="span6 m-wrap" value="${ruleConfig.ruleId}"/>

            <div class="control-group">
                <label class="control-label">应用名称<span class="required">*</span></label>
                <div class="controls">
                    <select name="appName" class="m-wrap span6" id="appName">
                        <option value="0">应用名称</option>
                        <c:forEach items="${appInfos}" var="appInfo">
                            <option value="${appInfo.appName}" <c:if test="${ruleConfig.appName==appInfo.appName}">selected="selected"</c:if>>${appInfo.appName}</option>
                        </c:forEach>
                    </select>

                </div>
            </div>

            <div class="control-group">
                <label class="control-label">规则名称<span class="required">*</span></label>
                <div class="controls">
                    <input type="text" name="ruleName" class="span6 m-wrap" value="${ruleConfig.ruleName}" />
                </div>
            </div>

            <div class="control-group">
                <label class="control-label">规则描述<span class="required">*</span></label>
                <div class="controls">
                    <textarea name="ruleDesc" class="span8 m-wrap" rows="10">${ruleConfig.ruleDesc}</textarea>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label">规则内容<span class="required">*</span></label>
                <div class="controls">
                    <textarea name="ruleConfig" class="span8 m-wrap" rows="10">${ruleConfig.ruleConfig}</textarea>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label">测试文本</label>
                <div class="controls">
                    <textarea name="exceptionInfo" class="span8 m-wrap" rows="10"></textarea>
                </div>
                <div class="controls"><button id="btn_ruleconfig_test_new" type="button" class="btn green">测试</button></div>
            </div>


            <div class="form-actions">
                <button id="btn_ruleconfig_edit" type="button" class="btn green">保存</button>
                <button id="btn_ruleconfig_back" type="button" class="btn">返回</button>
            </div>
        </form>
        <!-- END FORM-->
    </div>
    <!-- END PAGE CONTENT LIST-->

</div>
<!-- END PAGE CONTAINER-->
</body>
</html>
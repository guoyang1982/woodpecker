<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="../common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>新增分类配置</title>
</head>
<body>
<!-- BEGIN PAGE CONTAINER-->
<div class="container-fluid">
    <!-- BEGIN PAGE CONTENT LIST-->
    <div id="contentList" class="row-fluid">
        <!-- BEGIN FORM-->
        <form id="form_application" action="#" class="form-horizontal">
            <div class="control-group">
                <label class="control-label">应用创建人<span class="required">*</span></label>
                <div class="controls">
                    <input type="text" name="creator" class="span6 m-wrap" value="${username}" readonly="readonly" />
                </div>
            </div>
             <div class="control-group">
                <label class="control-label">应用名称<span class="required">*</span></label>
                <div class="controls">
                    <input type="text" name="appName" class="span6 m-wrap" value=""/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">IP<span class="required">*</span></label>
                <div class="controls">
                    <input type="text" name="ip" class="span6 m-wrap" value=""/> <a><font color="red">多个ip用;分隔</font></a>
                </div>
            </div>
           <div class="form-actions">
              <button id="btn_application_save_new" type="button" class="btn green">保存</button>
              <button id="btn_application_back_new" type="button" class="btn">返回</button>
          </div>
        </form>
        <!-- END FORM-->
    </div>
    <!-- END PAGE CONTENT LIST-->

</div>
<!-- END PAGE CONTAINER-->
</body>
</html>
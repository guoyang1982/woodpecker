<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="../common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>异常信息管理</title>
    <!-- BEGIN PAGE LEVEL STYLES -->
    <link rel="stylesheet" type="text/css" href="${ctx}/static/lib/jquery.dataTables/css/DT_bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/lib/jquery.gritter/jquery.gritter.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/lib/toggle.buttons/bootstrap-toggle-buttons.css" />
    <link rel="stylesheet" type="text/css" href="${ctx}/static/lib/bootstrap.modal/bootstrap-modal.css" />
    <!-- END PAGE LEVEL STYLES -->
    <!-- BEGIN PAGE LEVEL PLUGINS -->
    <script type="text/javascript" src="${ctx}/static/lib/jquery.dataTables/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/backdone/underscore-min.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/jquery.gritter/jquery.gritter.js"></script>
    <!-- END PAGE LEVEL PLUGINS -->
    <!-- BEGIN PAGE LEVEL SCRIPTS -->
    <script type="text/javascript" src="${ctx}/static/lib/jquery.dataTables/js/DT_bootstrap.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/jquery.validation/jquery.validate.min.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/toggle.buttons/jquery.toggle.buttons.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/uploadify/js/jquery.uploadify.min.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/backdone/underscore-min.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/bootstrap.modal/bootstrap-modal.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/bootstrap.modal/bootstrap-modalmanager.js"></script>
    <script type="text/javascript" src="${ctx}/static/lib/require/require.js"></script>
    <script type="text/javascript" src="${ctx}/static/js/app.js"></script>
    <script type="text/javascript" src="${ctx}/static/js/calendar-time.js"></script>
    <!-- END PAGE LEVEL SCRIPTS -->
    <script type="text/javascript">
        jQuery(function () {
            App.init();
            // 因jsp冲突，将模板匹配字符改为{{}}
            _.templateSettings = {
                evaluate: /\{\{([\s\S]+?)\}\}/g,
                interpolate: /\{\{=([\s\S]+?)\}\}/g,
                escape: /\{\{-([\s\S]+?)\}\}/g
            };

            require.config({
                baseUrl: "${ctx}/static/",
                paths: {'jquery': 'js/jquery-1.10.1.min'},
                shim: {}
            });

            require(['exception/exceptionlist_version2'], function (ExceptionList) {
                var exceptionList = new ExceptionList({
                    host: "${ctx}"
                });
                exceptionList.setOptions({username:"${username}"});
                exceptionList.show();
            });

        });
    </script>
    <script id="temp_op" type="text/template">
        <span data-sign="{{=sign}}" data-id="{{=id}}" data-name="{{=name}}">
            {{ _.each(ops, function(op){ }}
            <a href="javascript:void(0);" class="btn mini {{=op.color}}" data-sign="{{=op.sign}}" data-id="{{=op.id}}"
               data-name="{{=op.name}}">{{=op.btnName}}</a>
            {{ }); }}
        </span>
    </script>
</head>
<body>
<!-- BEGIN PAGE CONTAINER-->
<div class="container-fluid">
    <!-- BEGIN PAGE HEADER-->
    <div class="row-fluid">
        <!-- BEGIN PAGE TITLE & BREADCRUMB-->
        <h3 class="page-title">
            <small></small>
        </h3>
        <!-- END PAGE TITLE & BREADCRUMB-->
    </div>
    <!-- END PAGE HEADER-->

    <!-- 模板列表-->
    <div id="contentList" class="row-fluid">
        <div id="content_exceptionlist" data-sign="content" class="span12">
            <div class="portlet box blue">
                <div class="portlet-title">
                    <div class="caption"><i class="icon-reorder"></i>异常信息管理</div>
                </div>
                <div class="portlet-body">
                    <div id="form_exceptionlist_search" class="form-inline">
                        <select name="appName" class="m-wrap span2" id="appName">
                            <option value="">应用名称</option>
                            <c:forEach items="${appInfos}" var="appInfo">
                                <option value="${appInfo.appName}">${appInfo.appName}</option>
                            </c:forEach>
                        </select>
                        开始时间：<input name="startTime" type="text" style="padding-left:5px;" id="startTime" onclick="SetDate(this,'yyyy-MM-dd hh:mm:ss')" readonly="readonly" />
                        结束时间：<input name="endTime" type="text" style="padding-left:5px;" id="endTime" onclick="SetDate(this,'yyyy-MM-dd hh:mm:ss')" readonly="readonly" />
                        <button id="btn_exceptionlist_search" type="button" class="btn blue">搜索</button>
                    </div>
                    <table id="table_exceptionlist" class="table table-striped table-bordered table-hover">
                        <thead>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <!-- 模板列表结束-->

        <!-- 模板详细列表 -->
        <div id="content_exception_detail" data-sign="content" class="span12" style="margin-left: 0px;">
            <div class="portlet box blue">
                <div class="portlet-title">
                    <div class="caption"><i class="icon-list-alt"></i>异常信息管理 > 类型详细信息</div>
                </div>
                <div id="div_exception_detail" class="portlet-body form" style="min-height: 600px;">
                    <div id="form_exceptiondetail_back" class="form-inline ">
                        <button id="btn_exceptiondetail_back" type="button" class="btn green">返回上一级</button>
                    </div>
                    <div class="portlet-body">
                       <table id="table_exceptionDetail" class="table table-striped table-bordered table-hover">
                           <thead>
                           </thead>
                           <tbody>
                           </tbody>
                       </table>
                    </div>
                </div>
             </div>
         </div>
        <!-- 详细列表结束 -->

        <!-- 异常明细 -->
        <div id="content_single_detail" data-sign="content" class="span12" style="margin-left: 0px;">
            <div class="portlet box blue">
                <div class="portlet-title">
                    <div class="caption"><i class="icon-list-alt"></i>异常信息管理 > 类型详细信息 > 异常明细</div>
                </div>
                <div id="div_single_detail" class="portlet-body form" style="min-height: 600px;">
                    <div id="form_singledetail_back" class="form-inline ">
                        <button id="btn_singledetail_back" type="button" class="btn green">返回上一级</button>
                    </div>
                    <div class="portlet-body">
                       <table id="table_singleDetail" class="table table-striped table-bordered table-hover">
                           <thead>
                           </thead>
                           <tbody>
                           </tbody>
                       </table>
                    </div>
                </div>
             </div>
         </div>
           <!-- 明细列表结束 -->


</div>
</div>
</body>
</html>


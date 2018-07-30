<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="../common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>应用管理</title>
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
    <!-- END PAGE LEVEL SCRIPTS -->
    <script type="text/javascript">

        var application;
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

            require(['application/application_cluster_status'], function (Application) {
                application = new Application({
                    host: "${ctx}"
                });
                application.setOptions({userName:"${username}"});
                application.show();

            });
        });
        function refreshTable() {
            application.initTable();
        }


        function deleteUnusedAppIp(appName,Ip) {
            var that = application;
            application.confirm.show({info: '删除' + appName + "下, " + Ip.replace(/-/g,".")}, function () {
                $.ajax({
                    url: '/woodpecker/application/deleteAppNode/' + appName + '/' + Ip,
                    type: 'POST',
                    success: function(data) {
                        if (data.code === 0) {
                            that.initTable();
                            $.gritter.add({title: "提示信息：", text: "删除成功！", time: 1000});
                        }else {
                            $.gritter.add({title: "提示信息：", text: data.message, time: 2000});
                        }
                    }
                });
            });
        }

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
        <div id="content_application" data-sign="content" class="span12">
            <div class="portlet box blue">
                <div class="portlet-title">
                    <div class="caption"><i class="icon-reorder"></i>应用集群状态 </div>
                </div>
                <div class="portlet-body">
                    <div class="clearfix">
                        <div class="btn-group" style="display:none;">
                            <button id="btn_application_new"   class="btn green">
                                新增 <i class="icon-plus"></i>
                            </button>
                        </div>
                    </div>
                    <!-- <div id="form_application_search" class="form-inline">
                        <input type="text" name="search_application_key" class="span2" placeholder="源分类">
                        <button id="btn_application_search" type="button" class="btn blue">搜索</button>
                    </div>  -->
                    <table id="table_application" class="table table-striped table-bordered table-hover">
                        <thead>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <!-- 模板列表结束-->

        <!-- 模板编辑 -->
        <div id="content_application_edit" data-sign="content" class="span12" style="display: none">
            <div class="portlet box blue">
                <div class="portlet-title">
                    <div class="caption"><i class="icon-list-alt"></i>配置详细信息</div>
                </div>
                <div id="div_application_edit" class="portlet-body form" style="min-height: 600px;">
                </div>
            </div>
        </div>
        <!-- 模板编辑结束 -->
        <!-- BEGIN COMFIRM MODAL -->
        <div id="modal_confirm" class="modal hide fade" tabindex="-1" role="dialog"
             aria-labelledby="modal_confirm_title" aria-hidden="true">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>
                <h3 id="modal_confirm_title">确认信息</h3>
            </div>
            <div class="modal-body">
                <p id="modal_confirm_info" style="word-break:break-all"></p>
            </div>
            <div class="modal-footer">
                <button id="btn_modal_confirm" data-dismiss="modal" class="btn blue">确定</button>
                <button class="btn" data-dismiss="modal" aria-hidden="true">取消</button>
            </div>
        </div>
    </div>
</div>
<script>
    setInterval("refreshTable()",10000);
</script>
</body>
</html>


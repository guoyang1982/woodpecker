<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="../common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>啄木鸟</title>
    <c:set var="ctx" value="${pageContext.request.contextPath}"/>
    <%-- [EasyUI] --%>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/js/jquery-easyui/themes/gray/easyui.css">
    <link rel="stylesheet" type="text/css" href="${ctx}/static/js/jquery-easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="${ctx}/static/js/jquery-easyui/themes/color.css">
    <link rel="stylesheet" type="text/css" href="${ctx}/static/style/css/common.css">
    <link rel="stylesheet" type="text/css" href="${ctx}/static/style/css/icon.css">
    <%-- [my97日期时间控件] --%>
    <script type="text/javascript" src="${ctx}/static/js/My97DatePicker/WdatePicker.js"></script>
    <%--<script type="text/javascript" src="${ctx}/static/js/jquery-easyui/jquery.min.js"></script>--%>
    <script type="text/javascript" src="${ctx}/static/js/jquery-easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="${ctx}/static/js/jquery-easyui/locale/easyui-lang-zh_CN.js"></script>
    <%-- [扩展JS] --%>
    <!-- END PAGE LEVEL SCRIPTS -->
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


    <script type="text/javascript">
        function queryTable(){
            $("#tableHead").html();
            var appName = $("#appName").val();
            //加载表格数据和动态表头
            $.ajax({
                url:"/woodpecker/exception/dynamic/info/" + appName + "/perMinute",
                cache : false,
                dataType : "text",
                success:function(data){
                    var result = eval('(' + data + ')');
                    //统计表格
                    $('#dg2').datagrid({
                        fitColumns : true,
                        striped : true,
                        singleSelect: true,
                        //填充动态表头
                        columns:result[0].columns,
                        pagination : false,
                        loadMsg : "正在努力加载中......",
                    });
                    //填充表格数据
                    $('#dg2').datagrid("loadData",result[1]);
                }
            });
        }
    </script>

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

<!-- BEGIN PAGE HEADER-->

<div class="row-fluid">
    <!-- BEGIN PAGE TITLE & BREADCRUMB-->
    <h3 class="page-title">
        <small></small>
    </h3>
    <!-- END PAGE TITLE & BREADCRUMB-->
</div>

<div id="contentList" class="row-fluid">
    <div id="content_real_time_exceptionList" data-sign="content" class="span12">
        <div class="portlet box blue">
            <div class="portlet-title">
                <div class="caption"><i class="icon-reorder"></i>实时异常统计 > 分钟级实时异常统计</div>
            </div>
        </div>
    </div>
    <form id="searchForm" >
        <select name="appName" class="m-wrap span2" id="appName" onchange="queryTable()">
            <option value="">应用名称</option>
            <c:forEach items="${appInfos}" var="appInfo">
                <option value="${appInfo.appName}">${appInfo.appName}</option>
            </c:forEach>
        </select>
    </form>
    <div title="表格统计结果">
        <table id="dg2"></table>
    </div>
</div>
<script>
    queryTable();
    setInterval("queryTable()",3000)
</script>
</body>
</html>



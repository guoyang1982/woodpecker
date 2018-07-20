<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="../common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>异常报表</title>
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
    <script type="text/javascript" src="${ctx}/static/js/app.js"></script>
    <script type="text/javascript" src="${ctx}/static/js/calendar-time.js"></script>
    <!-- 引入 echarts.js -->
     <script src="${ctx}/static/js/echarts/dist/echarts.min.js"></script>
    <!-- END PAGE LEVEL SCRIPTS -->
    <script type="text/javascript">
        jQuery(function () {
            initCharts();
            initCharts1();

        });
        function init(){
            initCharts();
            initCharts1();
        }
        function getParams(){
            return [
            {name: "startTime",value: $("#startTime").val()},
            {name: "endTime",value: $("#endTime").val()}
           ];
        };
        function  initCharts () {
            var myChart = echarts.init(document.getElementById('exception_num_charts'));
             // 显示标题，图例和空的坐标轴
             myChart.setOption({
                 title: {
                     text: '系统异常数展示'
                 },
                 tooltip: {},
                 legend: {
                     data:['数量']
                 },
                 xAxis: {
                     data: []
                 },
                 yAxis: {},
                 series: [{
                     name: '数量',
                     type: 'bar',
                     data: []
                 }]
             });

             myChart.showLoading();    //数据加载完之前先显示一段简单的loading动画

             var names=[];    //应用名（实际用来盛放X轴坐标值）
             var nums=[];    //异常数量数组（实际用来盛放Y坐标值）

             $.ajax({
             type : "post",
             async : true,            //异步请求（同步请求将会锁住浏览器，用户其他操作必须等待请求完成才可以执行）
             url : "/woodpecker/exception/exceptionNumChart",    //请求发送到TestServlet处
             data : getParams(),
             dataType : "json",        //返回数据形式为json
             contentType: "application/x-www-form-urlencoded; charset=utf-8",
             success : function(result) {
                 //请求成功时执行该函数内容，result即为服务器返回的json对象
                 if (result) {
                        var data = result.data;
                        for(var i=0;i<data.length;i++){
                           names.push(data[i].appName);    //挨个取出类别并填入类别数组
                         }
                        for(var i=0;i<data.length;i++){
                            nums.push(data[i].count);    //挨个取出销量并填入销量数组
                          }
                        myChart.hideLoading();    //隐藏加载动画
                        myChart.setOption({        //加载数据图表
                            xAxis: {
                                data: names
                            },
                            series: [{
                                // 根据名字对应到相应的系列
                                name: '数量',
                                data: nums
                            }]
                        });
                 }
            },
             error : function(errorMsg) {
                 //请求失败时执行该函数
             alert("图表请求数据失败!");
             myChart.hideLoading();
             }
        })
        };

        function  initCharts1 () {
            // 基于准备好的dom，初始化echarts实例
            var myChart1 = echarts.init(document.getElementById('myChart'));

            myChart1.setOption({
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b}: {c} ({d}%)"
                },
                legend: {
                    orient: 'horizontal',
                    left: 'center',
                    bottom: 0,
                    data:['直达','其它外链','搜索引擎','直接输入网址或书签','cnblogs.com','微博','微信','百度','谷歌','360','必应','其他']
                },
                series: [
                    {
                        name: '应用', //内环
                        type: 'pie',
                        selectedMode: 'single', //单一选中模式
                        radius: [0, '30%'], //饼图的半径 [内半径，外半径]

                        label: {
                            normal: {
                                position: 'inner' //内置文本标签
                            }
                        },
                        labelLine: {
                            normal: {
                                show: false     //不需要设置引导线
                            }
                        },
                        data: [
                            {value: 335, name: '直达', selected: true},
                            {value: 679, name: '其它外链'},
                            {value: 1548, name: '搜索引擎'}
                        ]
                    },
                    {
                        name: '应用',
                        type: 'pie',
                        radius: ['40%', '55%'],

                        data: [

                        ]
                    }
                ]
            });

            myChart1.showLoading();    //数据加载完之前先显示一段简单的loading动画

            var names=[];    //应用名（实际用来盛放X轴坐标值）
            var appCount=[];
            var exceptionTypeArr=[];    //异常类型分组数组（实际用来盛放Y坐标值）
            $.ajax({
                type : "post",
                async : true,            //异步请求（同步请求将会锁住浏览器，用户其他操作必须等待请求完成才可以执行）
                url : "/woodpecker/exception/exceptionNumPieChart",    //请求发送到TestServlet处
                data : getParams(),
                dataType : "json",        //返回数据形式为json
                contentType: "application/x-www-form-urlencoded; charset=utf-8",
                success : function(result) {
                    //请求成功时执行该函数内容，result即为服务器返回的json对象
                    if (result) {
                        var data = result.data;
                        for(var i=0;i<data.length;i++){
                            names.push(data[i].appName);//挨个取出类别并填入类别数组
                            var  c = new Object();
                            c.value=data[i].count;
                            c.name=data[i].appName;
                            appCount.push(c)
                        }
                        var exceptionType = result.exceptionType;
                        for(var i=0;i<exceptionType.length;i++){
                            var o = new Object();
                            o.value = exceptionType[i].count;
                            o.name = exceptionType[i].appName + ":" + exceptionType[i].exceptionType;
                            exceptionTypeArr.push(o);    //挨个取出销量并填入销量数组
                        }
                        myChart1.hideLoading();    //隐藏加载动画
                        myChart1.setOption({        //加载数据图表
                            legend: {
                                orient: 'horizontal',
                                left: 'center',
                                bottom: 0,
                                data:names
                            },
                            series: [
                                {
                                    name: '应用', //内环
                                    type: 'pie',
                                    selectedMode: 'single', //单一选中模式
                                    radius: [0, '30%'], //饼图的半径 [内半径，外半径]

                                    label: {
                                        normal: {
                                            position: 'inner' //内置文本标签
                                        }
                                    },
                                    labelLine: {
                                        normal: {
                                            show: false     //不需要设置引导线
                                        }
                                    },
                                    data: appCount
                                },
                                {
                                    name: '应用',
                                    type: 'pie',
                                    radius: ['40%', '55%'],

                                    data: exceptionTypeArr
                                }
                            ]
                        });
                    }
                },
                error : function(errorMsg) {
                    //请求失败时执行该函数
                    alert("图表请求数据失败!");
                    myChart1.hideLoading();
                }
            })
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

    <div id="contentList" class="row-fluid">
        <div id="content_charts" data-sign="content" class="span12">
            <div class="portlet box blue">
                <div class="portlet-title">
                    <div class="caption"><i class="icon-reorder"></i>异常报表 </div>
                </div>
                <div class="portlet-body">
                     <div id="form_charts_search" class="form-inline">
                        开始时间：<input name="startTime" type="text" style="padding-left:5px;" id="startTime" onclick="SetDate(this,'yyyy-MM-dd hh:mm:ss')" readonly="readonly" />
                        结束时间：<input name="endTime" type="text" style="padding-left:5px;" id="endTime" onclick="SetDate(this,'yyyy-MM-dd hh:mm:ss')" readonly="readonly" />
                        <button id="btn_exceptionlist_search" type="button" class="btn blue" onclick="init()">搜索</button>
                    </div>
                    <!-- 为ECharts准备一个具备大小（宽高）的Dom -->
                    <div id="exception_num_charts" style="width: 600px;height:400px;"></div>

                    <div id="myChart" style="width: 600px;height:400px;"></div>

                </div>
            </div>
        </div>


</div>
</div>
</body>
</html>


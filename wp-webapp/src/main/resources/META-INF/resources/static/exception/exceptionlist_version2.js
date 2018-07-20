
define(["./exception_detail","../common/confirm"],
    function (ExceptionDetail,Confirm) {

        function ExceptionList(options) {
            this.init(options);
        }

        ExceptionList.prototype = {
            init: function (options) {
                this.options = options;
                this.confirm = new Confirm();
                this.exception_detail = new ExceptionDetail({host: this.options.host});
            },
            setOptions: function (options) {
                this.options = $.extend(this.options, options);
            },
            show: function () {
                $("div[data-sign=content]").hide();
                $("#content_exceptionlist").show();
                this.initEvents();
                this.initTable();
            },
            initEvents: function () {
                var that = this;
                // 搜索
                $("#btn_exceptionlist_search").off("click.exceptionlist_search").on("click.exceptionlist_search", function () {
                    var searchParam = {};
                    $("#form_exceptionlist_search").find("input[type=text]").each(function () {
                        searchParam[this.name] = $(this).val();
                    });
                    that.setOptions(searchParam);
                    that.initTable();
                });

                // 详细信息事件注册（事件代理模式）
                $("#table_exceptionlist tbody").off("click.op_exception_detail").on("click.op_exception_detail", "a[data-sign=op_exception_detail]", function () {
                    var $op_product = $(this).closest("span[data-sign=op_exception]");
                    that.opDetail({
                        id: $op_product.data("id"),
                        name: $op_product.data("name")
                    });
                });
            },
            getQueryParams: function () {
                return [
                    {name: "appName", value: $("#appName").val()},
                    {name: "startTime",value: $("#startTime").val()},
                    {name: "endTime",value: $("#endTime").val()},
                    {name: "username",value: this.options.username}
                ];
            },
            initTable: function () {
                var that = this;
                this.dataTable = $("#table_exceptionlist").dataTable({
                    iDisplayLength: 10,
                    bProcessing: true,
                    bServerSide: true,
                    bSort: false,
                    bFilter: false,
                    bAutoWidth: false,
                    bDestroy: true,
                    bInfo: true,//页脚信息
                    pagingType:   "full_numbers",
                    sDom: "tr<'row-fluid'<'span6'i><'span6'l><'span6'p>>",
                    sAjaxDataProp:'data',
                    sAjaxSource: "/woodpecker/exception/queryExceptionPage",
                    sServerMethod: "POST",
                    aoColumns: [
                        { sTitle: "应用名称", mData: "appName"},
                        { sTitle: "异常类型", mData: "exceptionType"},
                        {sTitle:"异常数",mData:"count"},
                        { sTitle: "操作", mData: null,
                            fnRender: function (obj) {
                                return _.template($("#temp_op").html(), {
                                    sign: "op_exception",
                                    id: obj.aData.appName,
                                    name:obj.aData.exceptionType + "_" +that.getQueryParams().startTime + "_" + that.getQueryParams().startTime ,
                                    ops: [
                                        {color: "blue", sign: "op_exception_detail", id: "",  btnName: "详细信息"}
                                    ]
                                });
                            }
                        }
                    ],
                    fnServerParams: function (aoData) {
                        aoData = $.merge(aoData, that.getQueryParams());
                    }
                });

            },
            refreshTable: function () {
                if (this.dataTable) {
                    this.dataTable.fnDraw();
                } else {
                    this.initTable();
                }
            },
            opDetail: function (data) {
                var that = this;
                var appName = data.id;
                var exceptionType = data.name.split("_")[0];
                var startTime;
                if(data.name.split("_")[1] == "undefined"){
                    startTime = ""
                }else{
                    startTime = data.name.split("_")[1]
                }
                var endTime;
                if(data.name.split("_")[2] == "undefined"){
                    endTime = ""
                }else{
                    endTime = data.name.split("_")[1]
                }
//                var startTime = (data.name.split("_")[1] == "undefined"）? "":data.name.split("_")[1];
//                var endTime = (data.name.split("_")[2] == "undefined"）? "":data.name.split("_")[2];
                this.exception_detail.setOptions({
                    appName: appName,
                    exceptionType: exceptionType,
                    startTime: startTime,
                    endTime: endTime,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.exception_detail.show();
            },
            refreshTable: function () {
                if (this.dataTable) {
                    this.dataTable.fnDraw();
                } else {
                    this.initTable();
                }
            }
        };
        return ExceptionList;
    });

    function alertObj(obj){
        	var output = "";
        	for(var i in obj){
        		var property=obj[i];
        		output+=i+" = "+property+"\n";
        	}
        	alert(output);
        }

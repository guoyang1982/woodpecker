
define(["./exception_detail_two","../common/confirm"],
    function (ExceptionDetailTwo,Confirm) {

        function ExceptionDetail(options) {
            this.init(options);
        }

        ExceptionDetail.prototype = {
            init: function (options) {
                this.options = options;
                this.confirm = new Confirm();
                this.exception_detail_two = new ExceptionDetailTwo({host: this.options.host});
            },
            setOptions: function (options) {
                this.options = $.extend(this.options, options);
            },
            show: function () {
                $("div[data-sign=content]").hide();
                $("#content_exception_detail").show();
                this.initTable();
                this.initEvents();
            },
            initEvents: function () {
                var that = this;
                // 搜索e
                $("#btn_exceptionDetail_search").off("click.exceptionDetail_search").on("click.exceptionDetail_search", function () {
                    var searchParam = {};
                    $("#form_exceptionDetail_search").find("input[type=text]").each(function () {
                        searchParam[this.name] = $(this).val();
                    });
                    that.setOptions(searchParam);
                    that.initTable();
                });
                //这是返回
                $("#btn_exceptiondetail_back").off("click.exceptiondetail_back").on("click.exceptiondetail_back", function () {
                    that.btnBack();
                });

                // 详细信息事件注册（事件代理模式）
                $("#table_exceptionDetail tbody").off("click.op_exception_detail").on("click.op_exception_detail", "a[data-sign=op_exception_detail]", function () {
                    var $op_product = $(this).closest("span[data-sign=op_exception]");
                    that.opDetailTwo({
                        id: $op_product.data("id"),
                        name: $op_product.data("name")
                    });
                });

            },
            getQueryParams: function () {
                return [
                    {name: "appName", value: this.options.appName},
                    {name: "startTime", value: this.options.startTime},
                    {name: "endTime", value: this.options.endTime},
                    {name: "exceptionType", value: this.options.exceptionType}
                ];
            },
            initTable: function () {
                var that = this;
                this.dataTable = $("#table_exceptionDetail").dataTable({
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
                    sAjaxSource: "/woodpecker/exception/classifyByMd5",
                    sScrollY: "60%",
                    sScrollX: "2000px",
                    sServerMethod: "POST",
                    aoColumns: [
                        { sTitle: "应用名称", mData: "appName"},
                        { sTitle: "异常类型", mData: "exceptionType"},
                        {sTitle:"异常数",mData:"count"},
                        {sTitle:"异常信息",mData:"content"},
                        { sTitle: "操作", mData: null,
                            fnRender: function (obj) {
                                return _.template($("#temp_op").html(), {
                                    sign: "op_exception",
                                    id: obj.aData.appName,
                                    name:obj.aData.contentMd5,
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
            opDetailTwo: function (data) {
                var that = this;
                var appName = this.options.appName;
                var exceptionType = this.options.exceptionType;
                var startTime = this.options.startTime;
                var endTime = this.options.endTime;
                var contentMd5 = data.name;
                this.exception_detail_two.setOptions({
                    appName: appName,
                    exceptionType: exceptionType,
                    contentMd5: contentMd5,
                    startTime: startTime,
                    endTime: endTime,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.exception_detail_two.show();
            },

            btnBack: function () {
                $("div[data-sign=content]").hide();
                $("#content_exceptionlist").show();
                if($.isFunction(this.options.callback_btnBack)){
                    this.options.callback_btnBack();
                }
            },

            refreshTable: function () {
                if (this.dataTable) {
                    this.dataTable.fnDraw();
                } else {
                    this.initTable();
                }
            }
        };
        return ExceptionDetail;
    });

    function alertObj(obj){
            	var output = "";
            	for(var i in obj){
            		var property=obj[i];
            		output+=i+" = "+property+"\n";
            	}
            	alert(output);
            }

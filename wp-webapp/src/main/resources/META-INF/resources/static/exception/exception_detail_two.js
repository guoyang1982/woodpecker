
define(["../common/confirm"],
    function (Confirm) {

        function ExceptionDetailTwo(options) {
            this.init(options);
        }

        ExceptionDetailTwo.prototype = {
            init: function (options) {
                this.options = options;
                this.confirm = new Confirm();
            },
            setOptions: function (options) {
                this.options = $.extend(this.options, options);
            },
            show: function () {
                $("div[data-sign=content]").hide();
                $("#content_single_detail").show();
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
                $("#btn_singledetail_back").off("click.btn_singledetail_back").on("click.btn_singledetail_back", function () {
                    that.btnBack();
                });

            },
            getQueryParams: function () {
                return [
                    {name: "appName", value: this.options.appName},
                    {name: "startTime", value: this.options.startTime},
                    {name: "endTime", value: this.options.endTime},
                    {name: "exceptionType", value: this.options.exceptionType},
                    {name: "contentMd5", value: this.options.contentMd5}


                ];
            },
            initTable: function () {
                var that = this;
                this.dataTable = $("#table_singleDetail").dataTable({
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
                    sAjaxSource: "/woodpecker/exception/exceptionListByDetail",
                    sServerMethod: "POST",
                    sScrollY: "60%",
                    sScrollX: "2000px",
                    aoColumns: [
                        { sTitle: "应用名称", mData: "appName"},
                        { sTitle: "IP", mData: "ip"},
                        { sTitle: "异常类型", mData: "exceptionType"},
                        { sTitle: "创建时间", mData: "createTime"},
                        { sTitle: "日志时间", mData: "logTime"},
                        {sTitle:"异常信息",mData:"msg"}
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

            btnBack: function () {
                $("div[data-sign=content]").hide();
                $("#content_exception_detail").show();
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
        return ExceptionDetailTwo;
    });

    function alertObj(obj){
            	var output = "";
            	for(var i in obj){
            		var property=obj[i];
            		output+=i+" = "+property+"\n";
            	}
            	alert(output);
            }

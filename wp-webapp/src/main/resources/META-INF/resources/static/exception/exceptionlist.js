/**
 * Created by wangruifeng on 14-5-12.
 */
define(["../common/confirm"],
    function (Confirm) {

        function ExceptionList(options) {
            this.init(options);
        }

        ExceptionList.prototype = {
            init: function (options) {
                this.options = options;
                this.confirm = new Confirm();
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
                    that.initTable();
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
                    sAjaxSource: "/woodpecker/exception/queryAllExceptionsPage",
                    sServerMethod: "POST",
                    aoColumns: [
                        { sTitle: "应用名称", mData: "appName"},
                        { sTitle: "IP", mData: "ip"},
                        { sTitle: "异常类型", mData: "exceptionType"},
                        { sTitle: "创建时间", mData: "createTime"},
                        { sTitle: "日志时间", mData: "logTime"},
                        { sTitle: "异常内容", mData: "msg"}
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
            }
        };
        return ExceptionList;
    });

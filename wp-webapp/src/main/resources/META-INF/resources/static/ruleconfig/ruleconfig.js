/**
 * Created by wangruifeng on 14-5-12.
 */
define(["./ruleconfig_new", "./ruleconfig_edit", "../common/confirm"],
    function (ruleconfigNew, ruleEdit, Confirm) {

        function ruleconfig(options) {
            this.init(options);
        }

        ruleconfig.prototype = {
            init: function (options) {
                this.options = options;
                this.ruleconfig_new = new ruleconfigNew({host: this.options.host});
                this.ruleconfig_edit = new ruleEdit({host: this.options.host});

                this.confirm = new Confirm();
            },
            setOptions: function (options) {
                this.options = $.extend(this.options, options);
            },
            show: function () {
                $("div[data-sign=content]").hide();
                $("#content_ruleconfig").show();
                this.initEvents();
                this.initTable();
            },
            initEvents: function () {
                var that = this;
                // 新增
                $("#btn_ruleconfig_new").off("click.ruleconfig_new").on("click.ruleconfig_new", function () {
                    that.opNew();
                });

                // 搜索
                $("#btn_ruleconfig_search").off("click.ruleconfig_search").on("click.ruleconfig_search", function () {
                    var searchParam = {};
                    $("#form_ruleconfig_search").find("input[type=text]").each(function () {

                        searchParam[this.name] = $(this).val();
                    });

                    that.setOptions(searchParam);
                    that.initTable();
                });
                // 修改事件注册（事件代理模式）
                $("#table_ruleconfig tbody").off("click.op_rule_edit").on("click.op_rule_edit", "a[data-sign=op_rule_edit]", function () {
                    var $op_product = $(this).closest("span[data-sign=op_rule]");
                    that.opEdit({
                        id: $op_product.data("id"),
                        name: $op_product.data("name")
                    });
                });
                // 启用停用
                $("#table_ruleconfig tbody").off("click.op_rule_delete").on("click.op_rule_delete", "a[data-sign=op_rule_delete]", function () {
                    var $op_product = $(this).closest("span[data-sign=op_rule]");
                    that.opDelete({
                        id: $op_product.data("id"),
                        name: $op_product.data("name")
                    });
                });
            },
            getQueryParams: function () {
                return [
                    {name: "username", value: this.options.userName},
                    {name: "id", value: 0}
                ];
            },

            initTable: function () {
                var that = this;
                this.dataTable = $("#table_ruleconfig").dataTable({
                    iDisplayLength: 10,
                    bProcessing: true,
                    bServerSide: true,
                    bSort: false,
                    bFilter: false,
                    bAutoWidth: true,
                    bDestroy: true,
                    bInfo: true,//页脚信息
                    pagingType: "full_numbers",
                    sDom: "tr<'row-fluid'<'span6'i><'span6'l><'span6'p>>",
                    sAjaxDataProp: 'data',
                    sAjaxSource: "/woodpecker/ruleConfig/queryRuleConfigPage",
                    sServerMethod: "POST",
                    sScrollY: "60%",
                    sScrollX: "2000px",
                    aoColumns: [
                        {sTitle: "规则id", mData: "ruleId"},
                        {sTitle: "应用名称", mData: "appName"},
                        {sTitle: "规则名称", mData: "ruleName"},
                        {sTitle: "规则描述", mData: "ruleDesc"},
                        {sTitle: "创建者", mData: "userName"},
                        {sTitle: "规则内容", mData: "ruleConfig"},
                        {
                            sTitle: "操作", mData: null,
                            fnRender: function (obj) {
                                return _.template($("#temp_op").html(), {
                                    sign: "op_rule",
                                    id: obj.aData.ruleId,
                                    name: obj.aData.appName,
                                    ops: [
                                        {color: "blue", sign: "op_rule_edit", id: "", name: "", btnName: "修改"},

                                        {color: "red", sign: "op_rule_delete", id: "", btnName: "删除"}
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
                    //this.initTable();
                }
            },
            opEdit: function (data) {
                var that = this;
                this.ruleconfig_edit.setOptions({
                    id: data.id,
                    username: this.options.userName,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.ruleconfig_edit.show();
            },
            opDelete: function (data) {
                var id = data.id;
                var that = this;
                this.confirm.show({info: data.name}, function () {
                    $.ajax({
                        url: '/woodpecker/ruleConfig/deleteConfig/' + id,
                        type: 'DELETE',
                        success: function (data) {
                            if (data.code == "0") {
                                that.initTable();
                                $.gritter.add({title: "提示信息：", text: "删除成功！", time: 1000});
                            } else {
                                $.gritter.add({title: "提示信息：", text: data.message, time: 2000});
                            }
                        }
                    });
                });
            },
            opNew: function () {
                var that = this;
                this.ruleconfig_new.setOptions({
                    username: this.options.userName,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.ruleconfig_new.show();
            }
        };
        return ruleconfig;
    });

function alertObj(obj) {
    var output = "";
    for (var i in obj) {
        var property = obj[i];
        output += i + " = " + property + "\n";
    }
    alert(output);
}

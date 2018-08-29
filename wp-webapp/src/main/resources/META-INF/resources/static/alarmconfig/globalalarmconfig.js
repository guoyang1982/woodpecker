/**
 * Created by wangruifeng on 14-5-12.
 */
define(["./globalalarmconfig_new", "./globalalarmconfig_edit", "../common/confirm"],
    function (alarmconfigNew, alarmEdit, Confirm) {

        function alarmconfig(options) {
            this.init(options);
        }

        alarmconfig.prototype = {
            init: function (options) {
                this.options = options;
                this.alarmconfig_new = new alarmconfigNew({host: this.options.host});
                this.alarmconfig_edit = new alarmEdit({host: this.options.host});

                this.confirm = new Confirm();
            },
            setOptions: function (options) {
                this.options = $.extend(this.options, options);
            },
            show: function () {
                $("div[data-sign=content]").hide();
                $("#content_alarmconfig").show();
                this.initEvents();
                this.initTable();
            },
            initEvents: function () {
                var that = this;
                // 新增
                $("#btn_alarmconfig_new").off("click.alarmconfig_new").on("click.alarmconfig_new", function () {
                    that.opNew();
                });

                // 搜索
                $("#btn_alarmconfig_search").off("click.alarmconfig_search").on("click.alarmconfig_search", function () {
                    var searchParam = {};
                    $("#form_alarmconfig_search").find("input[type=text]").each(function () {

                        searchParam[this.name] = $(this).val();
                    });

                    that.setOptions(searchParam);
                    that.initTable();
                });
                // 修改事件注册（事件代理模式）
                $("#table_alarmconfig tbody").off("click.op_alarm_edit").on("click.op_alarm_edit", "a[data-sign=op_alarm_edit]", function () {
                    var $op_product = $(this).closest("span[data-sign=op_alarm]");
                    that.opEdit({
                        id: $op_product.data("id"),
                        name: $op_product.data("name")
                    });
                });
                // 启用停用
                $("#table_alarmconfig tbody").off("click.op_alarm_delete").on("click.op_alarm_delete", "a[data-sign=op_alarm_delete]", function () {
                    var $op_product = $(this).closest("span[data-sign=op_alarm]");
                    that.opDelete({
                        id: $op_product.data("id"),
                        name: $op_product.data("name")
                    });
                });
            },
            getQueryParams: function () {
                return [
                    {name: "username", value: this.options.userName},
                    {name: "id", value: 0},
                    {name: "configType", value: "GLOBAL"}
                ];
            },

            initTable: function () {
                var that = this;
                this.dataTable = $("#table_alarmconfig").dataTable({
                    iDisplayLength: 10,
                    configType: "GLOBAL",
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
                    sAjaxSource: "/woodpecker/alarmconfig/queryAlarmConfigPage",
                    sServerMethod: "POST",
                    sScrollY: "60%",
                    sScrollX: "2000px",
                    aoColumns: [
                        {mData : "alarmId",  bVisible : false},
                        {sTitle: "应用名称", mData: "appName"},
                        {sTitle: "规则id", mData: null,
                            fnRender: function (obj) {
                                return obj.aData.ruleId
                            }
                        },
                        {sTitle: "告警阀值", mData: null,
                            fnRender: function (obj) {
                                return obj.aData.threshold
                            }
                        },
                        {sTitle: "告警频率", mData: null,
                            fnRender: function (obj) {
                                return obj.aData.alarmFrequency
                            }
                        },
                        {sTitle: "告警倍率", mData: null,
                            fnRender: function (obj) {
                                return obj.aData.multiple
                            }
                        },
                        {sTitle: "告警邮箱", mData: null,
                            fnRender: function (obj) {
                                return obj.aData.email
                            }
                        },
                        {sTitle: "告警手机号", mData: null,
                            fnRender: function (obj) {
                                return obj.aData.phoneNum
                            }
                        },
                        {sTitle:"告警微信",mData: null,
                            fnRender: function (obj) {
                                return obj.aData.corpid+'</br>'+obj.aData.secret+'</br>'+obj.aData.toparty+'</br>'+obj.aData.agentid
                            }
                        },
                        {
                            sTitle: "操作", mData: null,
                            fnRender: function (obj) {
                                return _.template($("#temp_op").html(), {
                                    sign: "op_alarm",
                                    id: obj.aData.alarmId,
                                    name: obj.aData.appName + '_' + obj.aData.ip + '_' + obj.aData.exceptionType + '_' + obj.aData.alarmId,
                                    ops: [
                                        {color: "blue", sign: "op_alarm_edit", id: "", name: "", btnName: "修改"},

                                        {color: "red", sign: "op_alarm_delete", id: "", btnName: "删除"}
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
                this.alarmconfig_edit.setOptions({
                    id: data.id,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.alarmconfig_edit.show();
            },
            opDelete: function (data) {
                var alarmId = data.id;
                var that = this;
                this.confirm.show({info: data.name}, function () {
                    $.ajax({
                        url: '/woodpecker/alarmconfig/deleteConfig/' + alarmId,
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
                this.alarmconfig_new.setOptions({
                    username: this.options.userName,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.alarmconfig_new.show();
            }
        };
        return alarmconfig;
    });

function alertObj(obj) {
    var output = "";
    for (var i in obj) {
        var property = obj[i];
        output += i + " = " + property + "\n";
    }
    alert(output);
}

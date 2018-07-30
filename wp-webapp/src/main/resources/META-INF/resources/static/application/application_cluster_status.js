/**
 * Created by wangruifeng on 14-5-12.
 */
define(["./application_new","./application_edit","./application_invite","../common/confirm"],
    function (ApplicationNew ,ApplicationEdit, ApplicationInvite, Confirm) {

        function Application(options) {
            this.init(options);
        }

        Application.prototype = {
            init: function (options) {
                this.options = options;
                this.application_new = new ApplicationNew({host: this.options.host});
                this.application_edit = new ApplicationEdit({host: this.options.host});
                this.application_invite = new ApplicationInvite({host: this.options.host});
                this.confirm = new Confirm();
            },
            setOptions: function (options) {
                this.options = $.extend(this.options, options);
            },
            show: function () {
                $("div[data-sign=content]").hide();
                $("#content_application").show();
                this.initEvents();
                this.initTable();
            },
            initEvents: function () {
                var that = this;
                // 新增
                $("#btn_application_new").off("click.application_new").on("click.application_new", function () {
                    that.opNew();
                });

                $("#table_application tbody").off("click.op_app_edit").on("click.op_app_edit", "a[data-sign=op_app_edit]", function () {
                    var $op_product = $(this).closest("span[data-sign=op_application]");
                    that.opEdit({
                        id: $op_product.data("id"),
                        name: $op_product.data("name")
                    });
                });

                $("#table_application tbody").off("click.op_app_invite").on("click.op_app_invite", "a[data-sign=op_app_invite]", function () {
                    var $op_product = $(this).closest("span[data-sign=op_application]");
                    that.opInvite({
                        id: $op_product.data("id"),
                        name: $op_product.data("name")
                    });
                });
                // 启用停用
                $("#table_application tbody").off("click.op_app_del").on("click.op_app_del", "a[data-sign=op_app_del]", function () {
                    var $op_product = $(this).closest("span[data-sign=op_application]");
                    that.opDelete({
                        id: $op_product.data("id"),
                        name: $op_product.data("name")
                    });
                });
            },
            getQueryParams: function () {
                return [
                    {name: "username", value: this.options.userName}
                ];
            },
            initTable: function () {
                var that = this;
                this.dataTable = $("#table_application").dataTable({
                    iDisplayLength: 10,
                    bProcessing: true,
                    bServerSide: true,
                    bSort: false,
                    bFilter: false,
                    bAutoWidth: true,
                    bDestroy: true,
                    bInfo: true,//页脚信息
                    pagingType:   "full_numbers",
                    sDom: "tr<'row-fluid'<'span6'i><'span6'l><'span6'p>>",
                    sAjaxDataProp:'data',
                    sAjaxSource: "/woodpecker/application/queryAllClusterPage",
                    sServerMethod: "POST",
                    aoColumns: [
                        { sTitle: "应用名称", mData: "appName"},
                        {sTitle:"集群状态",mData:"ip"}
                        // { sTitle: "操作", mData: null,
                        //     fnRender: function (obj) {
                        //         return _.template($("#temp_op").html(), {
                        //             sign: "op_application",
                        //             id: obj.aData.creator,
                        //             name: obj.aData.appName,
                        //             ops: [
                        //                 {color: "red", sign: "op_app_del", id: "",  btnName: "删除"}
                        //             ]
                        //         });
                        //     }
                        // }
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
            opDelete: function (data) {
                var that = this;
                this.confirm.show({info: '删除' + data.name}, function () {
                    $.ajax({
                        url: '/woodpecker/application/deleteApp/'+ data.name,
                        type: 'DELETE',
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
            },
            opEdit: function (data) {
                var that = this;
                this.application_edit.setOptions({
                    id: data.name,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.application_edit.show();
            },
            opNew: function () {
                var that = this;
                this.application_new.setOptions({
                    callback_btnSave: function () {
                        that.show();

                    }
                });
                this.application_new.show();
            },
            opInvite: function (data) {
                var that = this;
                this.application_invite.setOptions({
                    id: data.name,
                    callback_btnSave: function () {
                        that.show();

                    }
                });
                this.application_invite.show();
            }
        };
        return Application;
    });

function alertObj(obj){
    var output = "";
    for(var i in obj){
        var property=obj[i];
        output+=i+" = "+property+"\n";
    }
    alert(output);
}

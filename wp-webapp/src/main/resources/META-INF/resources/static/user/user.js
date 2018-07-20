/**
 * Created by wangruifeng on 14-5-12.
 */
define(["./user_new","./user_edit","./user_change_status","../common/confirm"],
    function (UserNew, UserEdit, UserChangeStatus, Confirm) {

        function User(options) {
            this.init(options);
        }

        User.prototype = {
            init: function (options) {
                this.options = options;
                this.user_new = new UserNew({host: this.options.host});
                this.user_edit = new UserEdit({host: this.options.host});
                this.user_change_status = new UserChangeStatus({host: this.options.host});
                this.confirm = new Confirm();
            },
            setOptions: function (options) {
                this.options = $.extend(this.options, options);
            },
            show: function () {
                $("div[data-sign=content]").hide();
                $("#content_user").show();
                this.initEvents();
                this.initTable();
            },
            initEvents: function () {
                var that = this;
                // 新增
                $("#btn_user_new").off("click.user_new").on("click.user_new", function () {
                    that.opNew();
                });
                // 搜索功能
                // 修改事件注册（事件代理模式）
                $("#table_user tbody").off("click.op_user_edit").on("click.op_user_edit","a[data-sign=op_user_edit]", function(){
                    var $op_product = $(this).closest("span[data-sign=op_user]");
                    that.opEdit({
                        id: $op_product.data("id"),
                        name: $op_product.data("name")
                    });
                });

                $("#table_user tbody").off("click.op_user_change_status").on("click.op_user_change_status","a[data-sign=op_user_change_status]", function(){
                    var $op_product = $(this).closest("span[data-sign=op_user]");
                    that.opChangeStatus({
                        id: $op_product.data("id"),
                        name: $op_product.data("name")
                    });
                });
                // 启用停用
                $("#table_user tbody").off("click.op_user_del").on("click.op_user_del", "a[data-sign=op_user_del]", function () {
                    var $op_product = $(this).closest("span[data-sign=op_user]");
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
                this.dataTable = $("#table_user").dataTable({
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
                    sAjaxSource: "/woodpecker/user/queryAll",
                    sServerMethod: "POST",
                    aoColumns: [
                        { sTitle: "用户id", mData: "loginName"},
                        { sTitle: "用户身份", mData: "roleName"},
                        { sTitle: "操作", mData: null,
                            fnRender: function (obj) {
                                return _.template($("#temp_op").html(), {
                                    sign: "op_user",
                                    id: obj.aData.loginName,
                                    name: obj.aData.roleName,
                                    ops: [
                                        {color: "blue", sign: "op_user_edit", id: "", btnName: "修改密码"},
                                        {color: "yellow", sign: "op_user_change_status", id: "", btnName: "变更权限"},
                                        {color: "red", sign: "op_user_del", id: "",  btnName: "删除"}
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
            opDelete: function (data) {
                var that = this;
                this.confirm.show({info:  '删除' + data.id}, function () {
                    $.ajax({
                        url: '/woodpecker/user/deleteUser/' + data.id,
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
            },
            opNew: function () {
                var that = this;
                this.user_new.setOptions({
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.user_new.show();
            },
            opEdit: function (data) {
                var that = this;
                this.user_edit.setOptions({
                    id: data.id,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.user_edit.show();
            },
            opChangeStatus: function (data) {
                var that=this;
                this.user_change_status.setOptions({
                    id: data.id,
                    callback_btnSave: function () {
                        that.show();
                    }
                });
                this.user_change_status.show();
            }
        };
        return User;
    });

function alertObj(obj){
    var output = "";
    for(var i in obj){
        var property=obj[i];
        output+=i+" = "+property+"\n";
    }
    alert(output);
}

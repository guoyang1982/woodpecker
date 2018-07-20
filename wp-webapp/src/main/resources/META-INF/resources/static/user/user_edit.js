/**
 * Created by wangruifeng on 14-5-12.
 */
define(function(){

    function UserNew(options){
        this.init(options);
    }

    UserNew.prototype = {
        init: function (options) {
            this.options = options;
        },
        setOptions: function (options) {
            this.options = $.extend(this.options, options);
        },
        show: function () {
            $("div[data-sign=content]").hide();
            $("#content_user_edit").show();
            this.initEvents();
            this.loadContent();
        },
        initEvents: function () {
            var that = this;
            // 保存
            $("#div_user_edit").off("click.user_edit_save").on("click.user_edit_save", "#btn_user_edit_save", function () {
                that.btnSave();
            });
            // 返回
            $("#div_user_edit").off("click.user_edit_back").on("click.user_edit_back", "#btn_user_edit_back", function () {
                that.btnBack();
            });

        },
        loadContent: function () {
            // 将页面中form_temp部分的内容加载到容器中
            var id = this.options.id;
            var url = this.options.host + "/woodpecker/user/queryUser/" + id;
            var params = {};
            var that = this;
            $("#div_user_edit").empty();
            $("#div_user_edit").block({message: "Loading..."});
            $("#div_user_edit").load(url + " #form_user", params, function (responseText, textStatus, XMLHttpRequest) {
                $("#div_user_edit").unblock();

            });
        },

        getFormData: function () {
            var data = {};
            // 获取表单的值
            $("#form_user").find("input[type=hidden],input[type=text],input[type=password],textarea,select").each(function () {
                data[this.name] = $(this).val();
            });
            return data;
        },
        btnSave: function () {
            var that = this;
            $("#btn_user_save_new").attr("disabled", true);
            // 保存请求
            $.ajax({
                url: "/woodpecker/user/editUser",
                type: "post",
                data: this.getFormData(),
                success: function (data) {
                    $("#btn_user_edit_save").attr("disabled", false);
                    if (data.code === 0) {
                        $.gritter.add({title: "提示信息：", text: "保存成功！", time: 1000});
                        if ($.isFunction(that.options.callback_btnSave)) {
                            that.options.callback_btnSave(data);
                        }
                    } else {
                        $.gritter.add({title: "提示信息：", text: data.message, time: 2000});
                    }
                }
            });
        },
        btnBack: function () {
            $("div[data-sign=content]").hide();
            $("#content_user").show();
            if ($.isFunction(this.options.callback_btnBack)) {
                this.options.callback_btnBack();
            }
        }
    };
    return UserNew ;

});
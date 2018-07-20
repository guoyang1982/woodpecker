/**
 * Created by wangruifeng on 14-5-12.
 */
define(function(){

    function ApplicationInvite(options){
        this.init(options);
    }

    ApplicationInvite.prototype = {
        init: function (options) {
            this.options = options;
        },
        setOptions: function (options) {
            this.options = $.extend(this.options, options);
        },
        show: function () {
            $("div[data-sign=content]").hide();
            $("#content_application_edit").show();
            this.initEvents();
            this.loadContent();
        },
        initEvents: function () {
            var that = this;
            // 保存
            $("#div_application_edit").off("click.application_save_invite").on("click.application_save_invite", "#btn_application_save_invite", function () {
                that.btnSave();
            });
            // 返回
            $("#div_application_edit").off("click.application_back_invite").on("click.application_back_invite", "#btn_application_back_invite", function () {
                that.btnBack();
            });

        },
        loadContent: function () {
            // 将页面中form_temp部分的内容加载到容器中,编辑
            var appName = this.options.id;
            var url = "/woodpecker/application/inviteUser/" + appName;
            var params = {};
            var that = this;
            $("#div_application_edit").empty();
            $("#div_application_edit").block({message: "Loading..."});
            $("#div_application_edit").load(url + " #form_application", params, function (responseText, textStatus, XMLHttpRequest) {
                $("#div_application_edit").unblock();

            });
        },

        getFormData: function () {
            var data = {};
            // 获取表单的值
            $("#form_application").find("input[type=hidden],input[type=text],textarea,select").each(function () {
                data[this.name] = $(this).val();
            });
            return data;
        },
        btnSave: function () {
            var that = this;
            $("#btn_application_save_invite").attr("disabled", true);
            // 保存请求
            $.ajax({
                url: "/woodpecker/application/saveUserAppInfo",
                type: "post",
                data: this.getFormData(),
                success: function (data) {
                    $("#btn_application_save_invite").attr("disabled", false);
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
            $("#content_application").show();
            if ($.isFunction(this.options.callback_btnBack)) {
                this.options.callback_btnBack();
            }
        }
    };
    return ApplicationInvite;

});
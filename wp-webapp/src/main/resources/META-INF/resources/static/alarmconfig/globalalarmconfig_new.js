/**
 * Created by wangruifeng on 14-5-12.
 */
define(function () {

    function alarmconfigNew(options) {
        this.init(options);
    }

    alarmconfigNew.prototype = {
        init: function (options) {
            this.options = options;
        },
        setOptions: function (options) {
            this.options = $.extend(this.options, options);
        },
        show: function () {
            $("div[data-sign=content]").hide();
            $("#content_alarmconfig_edit").show();
            this.initEvents();
            this.loadContent();
        },
        initEvents: function () {
            var that = this;
            // 保存
            $("#div_alarmconfig_edit").off("click.alarmconfig_save_new").on("click.alarmconfig_save_new", "#btn_alarmconfig_save_new", function () {
                that.btnSave();
            });
            // 返回
            $("#div_alarmconfig_edit").off("click.alarmconfig_back_new").on("click.alarmconfig_back_new", "#btn_alarmconfig_back_new", function () {
                that.btnBack();
            });
            $("#div_alarmconfig_edit").off("change.appName_selected").on("change.appName_selected", "#appName", function () {
                that.getIpByAppName();
                that.getRuleByAppName();
            });

        },
        loadContent: function () {
            // 将页面中form_temp部分的内容加载到容器中
            var url = "/woodpecker/alarmconfig/toAlarmConfigAddPage";
            var params = {username: this.options.username,configType: "GLOBAL"};
            var that = this;
            $("#div_alarmconfig_edit").empty();
            $("#div_alarmconfig_edit").block({message: "Loading..."});
            $("#div_alarmconfig_edit").load(url + " #form_alarmconfig", params, function (responseText, textStatus, XMLHttpRequest) {
                $("#div_alarmconfig_edit").unblock();

            });
        },
        getIpByAppName: function () {
            $("#ip option").remove();
            var username = this.options.username;
            var appName = $("#appName").val();
            $.ajax({
                url: "/woodpecker/application/getIpByAppName/" + username + '/' + appName,
                type: "GET",
                success: function (data) {
                    if (data.code == "0") {
                        for (var p in data.data) {
                            $("#ip").append("<option  value='" + data.data[p] + "'>" + data.data[p] + "</option>");
                        }
                        $("#ip").append("<option  value='all'>all</option>");
                        $("#ip").append("<option  value='each'>each</option>");
                    } else {
                        $.gritter.add({title: "提示信息：", text: data.message, time: 2000});
                    }
                }
            });
        },
        getRuleByAppName: function () {
            $("#ruleId option").remove();
            var username = this.options.username;
            var appName = $("#appName").val();
            $.ajax({
                url: "/woodpecker/ruleConfig/getRuleByAppName/" + appName,
                type: "GET",
                success: function (data) {
                    if (data.code == "0") {
                        for (var p in data.data) {
                            $("#ruleId").append("<option  value='" + data.data[p].ruleId + "'>" + data.data[p].ruleName + "</option>");
                        }
                    } else {
                        $.gritter.add({title: "提示信息：", text: data.message, time: 2000});
                    }
                }
            });
        },

        getFormData: function () {
            var data = {};
            // 获取表单的值
            $("#form_alarmconfig").find("input[type=hidden],input[type=text],textarea,select").each(function () {
                data[this.name] = $(this).val();
            });
            return data;
        },
        btnSave: function () {
            var that = this;
            $("#btn_alarmconfig_save_new").attr("disabled", true);
            // 保存请求
            $.ajax({
                url: "/woodpecker/alarmconfig/saveAlarmConfig",
                type: "post",
                data: this.getFormData(),
                success: function (data) {
                    $("#btn_alarmconfig_save_new").attr("disabled", false);
                    if (data.code == "0") {
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
            $("#content_alarmconfig").show();
            if ($.isFunction(this.options.callback_btnBack)) {
                this.options.callback_btnBack();
            }
        }
    };
    return alarmconfigNew;

});
/**
 * Created by wangruifeng on 14-5-12.
 */
define(function () {

    function ruleEdit(options) {
        this.init(options);
    }
    var editor;
    ruleEdit.prototype = {

        init: function (options) {
            this.options = options;
        },
        setOptions: function (options) {
            this.options = $.extend(this.options, options);
        },
        show: function () {
            $("div[data-sign=content]").hide();
            $("#content_ruleconfig_edit").show();
            this.initEvents();
            this.loadContent();

        },
        initEvents: function () {
            var that = this;
            $("#div_ruleconfig_edit").off("click.btn_ruleconfig_edit").on("click.btn_ruleconfig_edit", "#btn_ruleconfig_edit", function () {
                that.btnSave();
            });
            // 测试
            $("#div_ruleconfig_edit").off("click.ruleconfig_test_new").on("click.ruleconfig_test_new", "#btn_ruleconfig_test_new", function () {
                that.btnTest();
            });
            $("#div_ruleconfig_edit").off("click.btn_ruleconfig_back").on("click.btn_ruleconfig_back", "#btn_ruleconfig_back", function () {
                that.btnBack();
            });

        },
        loadContent: function () {
            var id = this.options.id;
            var username = this.options.username;

            // 将页面中form_temp部分的内容加载到容器中
            var url = this.options.host + "/woodpecker/ruleConfig/queryById/" + id + '/' + username;
            var params = {};
            var that = this;
            $("#div_ruleconfig_edit").empty();
            $("#div_ruleconfig_edit").block({message: "Loading..."});
            $("#div_ruleconfig_edit").load(url + " #form_ruleconfig", params, function (responseText, textStatus, XMLHttpRequest) {
                editor = CodeMirror.fromTextArea($($("#form_ruleconfig").find("textarea")).get(1),{
                    lineNumbers: true,
                    matchBrackets: true,
                    theme:"darcula",
                    mode: "text/x-groovy"
                });
                editor.setValue($($("#form_ruleconfig").find("textarea")).get(1).value);
                $("#div_ruleconfig_edit").unblock();
            });


        },
        getFormData: function () {
            var data = {};
            // 获取表单的值
            $("#form_ruleconfig").find("input[type=hidden],input[type=text],textarea,select").each(function () {
                data[this.name] = $(this).val();
            });
            data[$($("#form_ruleconfig").find("textarea")).get(1).name]=editor.getValue();
            return data;
        },
        btnSave: function () {
            var that = this;
            $("#btn_ruleconfig_edit").attr("disabled", true);
            // 保存请求
            $.ajax({
                url: "/woodpecker/ruleConfig/modifyRuleConfig",
                type: "post",
                data: this.getFormData(),
                success: function (data) {
                    $("#btn_ruleconfig_edit").attr("disabled", false);
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
        btnTest: function () {
            var that = this;
            $("#btn_ruleconfig_test_new").attr("disabled", true);
            // 保存请求
            $.ajax({
                url: "/woodpecker/ruleConfig/ruleConfigTest",
                type: "post",
                data: this.getFormData(),
                success: function (data) {
                    $("#btn_ruleconfig_test_new").attr("disabled", false);
                    if (data.code == "0") {
                        $.gritter.add({title: "提示信息：", text: "测试成功！value=" + data.data, time: 5000});
                        // if ($.isFunction(that.options.callback_btnSave)) {
                        //     that.options.callback_btnSave(data);
                        // }
                    } else {
                        $.gritter.add({title: "提示信息：", text: data.message, time: 5000});
                    }
                }
            });
        },
        btnBack: function () {
            $("div[data-sign=content]").hide();
            $("#content_ruleconfig").show();
            if ($.isFunction(this.options.callback_btnBack)) {
                this.options.callback_btnBack();
            }
        }
    };
    return ruleEdit;

});
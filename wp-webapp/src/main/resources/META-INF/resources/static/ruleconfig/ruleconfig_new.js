/**
 * Created by wangruifeng on 14-5-12.
 */
define(function(){
	
	function ruleconfigNew(options){
		this.init(options);
	}

    ruleconfigNew.prototype = {
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
            // 保存
            $("#div_ruleconfig_edit").off("click.ruleconfig_save_new").on("click.ruleconfig_save_new", "#btn_ruleconfig_save_new", function () {
                that.btnSave();
            });
            // 测试
            $("#div_ruleconfig_edit").off("click.ruleconfig_test_new").on("click.ruleconfig_test_new", "#btn_ruleconfig_test_new", function () {
                that.btnTest();
            });
            // 返回
            $("#div_ruleconfig_edit").off("click.ruleconfig_back_new").on("click.ruleconfig_back_new", "#btn_ruleconfig_back_new", function () {
                that.btnBack();
            });
            $("#div_ruleconfig_edit").off("change.appName_selected").on("change.appName_selected","#appName",function(){
                that.getIpByAppName();
            });

        },
        loadContent: function () {
            // 将页面中form_temp部分的内容加载到容器中
            var url = "/woodpecker/ruleConfig/toRuleConfigAddPage";
            var params = {username:this.options.username};
            var that = this;
            $("#div_ruleconfig_edit").empty();
            $("#div_ruleconfig_edit").block({message: "Loading..."});
            $("#div_ruleconfig_edit").load(url + " #form_ruleconfig", params, function (responseText, textStatus, XMLHttpRequest) {
                $("#div_ruleconfig_edit").unblock();

            });
        },
        getIpByAppName:function(){
             $("#ip option").remove() ;
             var username = this.options.username;
             var appName = $("#appName").val() ;
             $.ajax({
                    url: "/woodpecker/application/getIpByAppName/"+ username+'/' +appName,
                    type: "GET",
                    success: function (data) {
                        if (data.code == "0") {
                         for(var p in data.data ){
                                $("#ip").append("<option  value='" + data.data[p] + "'>"+ data.data[p] +"</option>") ;
                             }
                         $("#ip").append("<option  value='all'>all</option>");
                         $("#ip").append("<option  value='each'>each</option>");
                        } else {
                            $.gritter.add({title: "提示信息：", text: data.message, time: 2000});
                        }
                    }
                });
       },

        getFormData: function () {
            var data = {};
            // 获取表单的值
            $("#form_ruleconfig").find("input[type=hidden],input[type=text],textarea,select").each(function () {
                 data[this.name] = $(this).val();
            });
            return data;
        },
        btnSave: function () {
            var that = this;
            $("#btn_ruleconfig_save_new").attr("disabled", true);
            // 保存请求
            $.ajax({
                url: "/woodpecker/ruleConfig/saveRuleConfig",
                type: "post",
                data: this.getFormData(),
                success: function (data) {
                    $("#btn_ruleconfig_save_new").attr("disabled", false);
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
                        $.gritter.add({title: "提示信息：", text: "测试成功！value="+data.data, time: 5000});
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
	return ruleconfigNew ;
	
});
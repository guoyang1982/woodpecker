/**
 * Created by wangruifeng on 14-5-12.
 */
define(function(){
	
	function alarmEdit(options){
		this.init(options);
	}

    alarmEdit.prototype = {
			
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
            $("#div_alarmconfig_edit").off("click.btn_alarmconfig_edit").on("click.btn_alarmconfig_edit", "#btn_alarmconfig_edit", function () {
                that.btnSave();
            });
            $("#div_alarmconfig_edit").off("click.btn_alarmconfig_back").on("click.btn_alarmconfig_back", "#btn_alarmconfig_back", function () {
                that.btnBack();
            });
        },
        loadContent: function () {

            var id = this.options.id;

            // 将页面中form_temp部分的内容加载到容器中
            var url = this.options.host + "/woodpecker/alarmconfig/queryById/" + id;
            var params = {};
            var that = this;
            $("#div_alarmconfig_edit").empty();
            $("#div_alarmconfig_edit").block({message: "Loading..."});
            $("#div_alarmconfig_edit").load(url + " #form_alarmconfig", params, function (responseText, textStatus, XMLHttpRequest) {
                $("#div_alarmconfig_edit").unblock();
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
            // if(!this.validate.form()){
            //     this.validate.focusInvalid();
            //     return;
            // }
            var that = this;
            $("#btn_alarmconfig_edit").attr("disabled", true);
         // 保存请求
            $.ajax({
                url: "/woodpecker/alarmconfig/modifyAlarmConfig",
                type: "post",
                data: this.getFormData(),
                success: function (data) {
                    $("#btn_alarmconfig_edit").attr("disabled", false);
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
	return alarmEdit ;
	
});
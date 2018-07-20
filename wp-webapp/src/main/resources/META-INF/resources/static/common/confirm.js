/**
 * Created by zhuminghua on 14-5-9.
 */
define(function () {
    function Confirm() {
        this.data = {
            info: "继续",
            width: 400,
            height: 300
        };
    }

    Confirm.prototype = {
        show: function (data, callback) {
            $.extend(this.data, data);
            $("#modal_confirm_info").html("确认" + this.data.info + "？");
            $("#modal_confirm").modal({
                width: this.data.width,
                maxHeight: this.data.height
            });
            $("#btn_modal_confirm").off("click").one("click", function () {
                $("#modal_confirm").modal("hide");
                if ($.isFunction(callback)) {
                    callback();
                }
            });
        }
    }

    return Confirm;
});
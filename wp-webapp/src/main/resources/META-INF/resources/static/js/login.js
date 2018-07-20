var Login = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
           $('.login-form').validate({
	            errorElement: 'label', //default input error message container
	            errorClass: 'help-inline', // default input error message class
	            focusInvalid: true, // do not focus the last invalid input
	            rules: {
	                loginname: {
	                    required: true
	                },
	                password: {
	                    required: true
	                }
	            },

	            messages: {
	                loginname: {
	                    required: "用户名必填"
	                },
	                password: {
	                    required: "密码必填"
	                }
	            },

	            invalidHandler: function (event, validator) { //display error alert on form submit   
	                $('#empty-alert', $('.login-form')).show();
	            },

	            highlight: function (element) { // hightlight error inputs
	                $(element)
	                    .closest('.control-group').addClass('error'); // set error class to the control group
	            },

	            success: function (label) {
	                label.closest('.control-group').removeClass('error');
	                label.remove();
	            },

	            errorPlacement: function (error, element) {
	                error.addClass('help-small no-left-padding').insertAfter(element.closest('.input-icon'));
	            },

	            submitHandler: function (form) {
                    form.submit();
	            }
	        });

	        $('.login-form #loginname').keypress(function (e) {
	            if (e.which == 13) {
	                if ($(this).val() != '') {
                        $('.login-form #password').focus();
	                }else{
                        $('.login-form').submit();
                    }
	                return false;
	            }
	        });

            $('.login-form #password').keypress(function (e) {
                if (e.which == 13) {
                    if ($('.login-form').validate().form()) {
                        $('.login-form').submit();
                    }
                    return false;
                }
            });

            $('.login-form #loginname').focus();
        }

    };

}();
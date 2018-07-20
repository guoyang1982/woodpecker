<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="common/global.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>注册啄木鸟后台</title>
    <meta name="viewport" content="width=device-width">
    <%@ include file="common/basejs.jsp" %>
    <link rel="stylesheet" type="text/css" href="${staticPath }/static/style/css/login.css?v=201612202107" />
    <script type="text/javascript" src="${staticPath }/static/register.js?v=20170115" charset="utf-8"></script>
</head>
<body>
<div class="top_div"></div>
<div style="background: rgb(255, 255, 255); margin: -100px auto auto; border: 1px solid rgb(231, 231, 231);border-image:none;width:400px;text-align: center;">
    <form method="post" id="registerForm" action="${path}/woodpecker/register">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <div style="width: 165px; height: 96px; position: absolute;">
            <div class="tou"></div>
            <div class="initial_left_hand" id="left_hand"></div>
            <div class="initial_right_hand" id="right_hand"></div>
        </div>
        <P style="padding: 30px 0px 10px; position: relative;">
            <span class="u_logo"></span>
            <input class="ipt" type="text" name="userName" placeholder="请输入登录名" required="required"/>
        </P>
        <P style="padding: 0px 0px 10px; position: relative;">
            <span class="p_logo"></span>
            <input class="ipt" id="password" type="password" name="password" placeholder="请输入密码" onblur="checkPwd()" required="required"/>
        </P>
        <div align="center" style="color:#F00" id="pwd_prompt">密码由英文字母和数字组成的4-10位字符</div>
        <P style="position: relative;">
            <span class="p_logo"></span>
            <input class="ipt" id="repassword" type="password" name="repassword" placeholder="请再次输入密码" onblur="checkRepwd()" required="required"/>
        </P>
        <div align="center" style="color:#F00" id="repwd_prompt"></div>

        <P style="padding: 10px 0px 10px; position: relative;">
            <input class="captcha" type="text" name="captcha" placeholder="请输入验证码" required="required"/>
            <img id="captcha" alt="验证码" src="${path }/captcha.jpg" data-src="${path }/captcha.jpg?t=" style="vertical-align:middle;border-radius:4px;width:94.5px;height:35px;cursor:pointer;">
        </P>

        <div style="height: 50px; line-height: 50px; margin-top: 10px;border-top-color: rgb(231, 231, 231); border-top-width: 1px; border-top-style: solid;">
            <P style="margin: 0px 35px 20px 45px;">
                <i style="float: right;">
                    <a style="background: rgb(0, 142, 173); padding: 7px 10px; border-radius: 4px; border: 1px solid rgb(26, 117, 152); border-image: none; color: rgb(255, 255, 255); font-weight: bold;" href="javascript:;" onclick="submitForm()">注册</a>
                </i>
            </P>
        </div>
    </form>
</div>

<script>

    function divId(elementId){
        return document.getElementById(elementId);
    }

    /** 用户名校验*/
    function checkUserName() {

    }

    /*密码验证*/
    function checkPwd(){
        var pwd=document.getElementById('password').value;
        var pwdId=divId('pwd_prompt');
        pwdId.innerHTML="";
        var reg=/^[a-zA-Z0-9]{4,10}$/;
        if(reg.test(pwd)===false){
            pwdId.innerHTML="密码不能含有非法字符，长度在4-10之间";
            return false;
        }
        return true;
    }

    function checkRepwd(){
        var pwd=document.getElementById('password').value;
        var repwd=document.getElementById('repassword').value;

        var repwdId=divId('repwd_prompt');
        repwdId.innerHTML="";
        if(pwd!==repwd){
            repwdId.innerHTML="两次输入的密码不一致";
            return false;
        }
        return true;
    }
</script>
</body>
</html>

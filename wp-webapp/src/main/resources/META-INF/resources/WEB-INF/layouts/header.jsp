<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="../views/common/taglibs.jsp" %>
<!-- BEGIN HEADER -->
<div class="header navbar navbar-inverse navbar-fixed-top">
<!-- BEGIN TOP NAVIGATION BAR -->
<div class="navbar-inner">
<div class="container-fluid">
<!-- BEGIN LOGO -->
<a class="brand" href="${ctx }/">
    <img src="${ctx }/static/image/logo-woodpecher.png" alt="logo"/>
</a>
<!-- END LOGO -->

<!-- BEGIN HORIZANTAL MENU -->
<div class="navbar hor-menu hidden-phone hidden-tablet">
    <div class="navbar-inner">
        <ul class="nav">
        </ul>
    </div>
</div>

<!-- END HORIZANTAL MENU -->
<!-- BEGIN RESPONSIVE MENU TOGGLER -->
<a href="javascript:;" class="btn-navbar collapsed" data-toggle="collapse" data-target=".nav-collapse">
    <img src="${ctx }/static/image/menu-toggler.png" alt=""/>
</a>
<!-- END RESPONSIVE MENU TOGGLER -->
<!-- BEGIN TOP NAVIGATION MENU -->
<ul class="nav pull-right">
<!-- BEGIN USER LOGIN DROPDOWN -->
<li class="dropdown user">
    <a href="#" class="dropdown-toggle" data-toggle="dropdown">
        <img alt="" src="${ctx }/static/image/user-32.png"/>
        <span class="username">${username}</span>
        <i class="icon-angle-down"></i>
    </a>
    <ul class="dropdown-menu">
        <li><a href="#logoutModal" data-toggle="modal"><i class="icon-key"></i> 退出</a></li>
    </ul>

</li>
<!-- END USER LOGIN DROPDOWN -->
</ul>

<!-- END TOP NAVIGATION MENU -->
</div>
</div>
<!-- END TOP NAVIGATION BAR -->
</div>
<!-- END HEADER -->
<div id="logoutModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="logoutModalLabel" aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>
        <h3 id="logoutModalLabel">操作确认</h3>
    </div>
    <div class="modal-body">
        <p>确定退出系统吗？</p>
    </div>
    <div class="modal-footer">
        <button class="btn" data-dismiss="modal" aria-hidden="true">取消</button>
        <button  class="btn blue" onclick="window.location.href = '${ctx }/logout';">确定</button>
    </div>
</div>
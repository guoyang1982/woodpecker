<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="../views/common/taglibs.jsp" %>
<style type="text/css">
    html, body, h1, h2, h3, h4, h5, h6, hr, p, blockquote,
    dl, dt, dd, ul, ol, li,
    pre,
    form, fieldset, iframe, legend, label, button, input, textarea,
    tr, th, td {
        margin: 0;
        padding: 0;
    }

    h1, h2, h3, h4, h5, h6 {
        font-size: 100%;
    }

    address, cite, dfn, var {
        font-style: normal;
    }

    code, kbd, pre, samp, tt {
        font-family: "Courier New", Courier, monospace;
    }

    small {
        font-size: 12px;
    }

    ul, ol {
        list-style: none;
    }

    a {
        text-decoration: none;
    }

    a:hover {
        text-decoration: none;
    }

    fieldset, img, button {
        border: none;
        vertical-align: middle;
    }

    button, input, select, textarea {
        font-size: 100%;
    }

    img, iframe, table, form {
        vertical-align: top;
    }

    /* 重置表格元素 */
    table {
        border-collapse: collapse;
        border-spacing: 0;
    }

    hr {
        border: none;
        height: 1px;
    }

    .clearfix {
        zoom: 1;
    }

    .clearfix:after, .clearfix:before {
        content: "\200B";
        display: block;
        height: 0;
        visibility: hidden;
    }

    .clearfix:after {
        clear: both;
    }

    .navTwo {
        display: none;
    }

    .page-sidebar-menu h2 a {
        display: block;
        font-size: 16px;
        color: #fff;
        padding-left: 10px;
    }

    .page-sidebar-menu {
        padding-left: 10px;
        #5c5c5c background: #303030;
        width: 225px;
        color: #fff;
        margin-top: 12px !important;
        float: left;
    }

    ul.page-sidebar-menu > li {
        border-top: 1px solid #fff;
    }

    .active {
        background-color: #e02222 !important;
    }

    .navFir h2 {
        padding-left: 15px
    }

    .navTwo {
        border-top: 1px solid #fff;
    }

    .navTwo a {
        position: relative;
        text-decoration: none;
        display: block;
        padding: 10px 25px;
        color: #fff;
    }

    #page-sidebar-menu > li > h2:hover {
        background: #5c5c5c
    }

    #page-sidebar-menu > li > h2:hover a {
        text-decoration: none !important;
    }

    #page-sidebar-menu .hit:hover {
        background: #5c5c5c
    }

    .navTwo > li {
        padding: 0px 10px;
    }

    ul.page-sidebar-menu > li.active > a .selected {
        display: block;
        width: 8px;
        height: 25px;
        background-image: url("../image/sidebar-menu-arrow.png");
        float: right;
        position: absolute;
        right: 0px;
        top: 8px;
    }

</style>
<!-- BEGIN SIDEBAR -->
<div class="page-sidebar nav-collapse collapse">
    <!-- BEGIN SIDEBAR MENU -->
    <ul class="page-sidebar-menu" id="page-sidebar-menu">
        <li class="navFir">
            <h2>首页</h2>
            <input type="hidden" name="fid" id="fid" value="${fid}">
        </li>
        <c:if test="${role == 1}">
            <li class="navFir">
                <h2>系统管理</h2>
                <ul class="navTwo">
                    <li class="hit"><a dataName="1" target="_self" class="hita" href="/woodpecker/user/toUserListPage">用户管理
                        <span class="selected"></span>
                    </a>
                    </li>
                </ul>
            </li>
        </c:if>
        <li class="navFir">
            <h2>应用管理</h2>
            <ul class="navTwo">
                <li class="hit"><a dataName="1" target="_self" class="hita" href="/woodpecker/application/toAppListPage">应用管理
                    <span class="selected"></span>
                </a>
                </li>
                <li class="hit"><a dataName="1" target="_self" class="hita" href="/woodpecker/alarmconfig/toAlarmConfigPage">告警配置
                    <span class="selected"></span>
                </a>
                </li>
                <li class="hit"><a dataName="1" target="_self" class="hita" href="/woodpecker/ruleConfig/ruleConfigPage">规则配置
                    <span class="selected"></span>
                </a>
                </li>
            </ul>
        </li>
        <li class="navFir">
            <h2>异常管理</h2>
            <ul class="navTwo">
                <li class="hit"><a dataName="1" target="_self" class="hita" href="/woodpecker/exception/toAllExceptionsPage">全部异常信息
                    <span class="selected"></span>
                </a>
                </li>
                <li class="hit"><a dataName="1" target="_self" class="hita" href="/woodpecker/exception/toExceptionVersion2Page">异常信息管理
                    <span class="selected"></span>
                </a>
                </li>
            </ul>
        </li>

        <li class="navFir">
            <h2>实时异常统计</h2>
            <ul class="navTwo">
                <li class="hit"><a dataName="1" target="_self" class="hita" href="/woodpecker/exception/realExceptionPerMinute">Minute级实时异常统计
                    <span class="selected"></span>
                </a>
                </li>
                <li class="hit"><a dataName="1" target="_self" class="hita" href="/woodpecker/exception/realExceptionPerHour">Hour级实时异常统计
                    <span class="selected"></span>
                </a>
                </li>
                <li class="hit"><a dataName="1" target="_self" class="hita" href="/woodpecker/exception/realExceptionPerDay">Day级实时异常统计
                    <span class="selected"></span>
                </a>
                </li>
            </ul>
        </li>

        <li class="navFir">
            <h2>异常报表</h2>
            <ul class="navTwo">
                <li class="hit"><a dataName="1" target="_self" class="hita" href="/woodpecker/exception/toExceptionChartsPage">应用异常总报表
                    <span class="selected"></span>
                </a>
                </li>
                <li class="hit"><a dataName="1" target="_self" class="hita" href="/woodpecker/exception/toExceptionChartsPage">分应用异常报表
                    <span class="selected"></span>
                </a>
                </li>
            </ul>
        </li>

        <li class="navFir">
            <h2>帮助</h2>
            <ul class="navTwo">
                <li class="hit"><a dataName="1" target="_self" class="hita" href="/woodpecker/introduction">系统介绍及使用
                    <span class="selected"></span>
                </a>
                </li>
            </ul>
        </li>
    </ul>

    <!-- END SIDEBAR MENU -->
</div>
<!-- END SIDEBAR -->
<script charset="utf-8" type="text/javascript">
    function TwoMenu(options) {
        this.init(options)
    }
    TwoMenu.prototype = {
        init: function (options) {
            this.options = options;
            this.show()
        },
        show: function () {
            this.initEvents();
        },
        initEvents: function () {
            var that = this;
            var posThr = $('#posTir');
            var posTwo = $('#posTwo');
            $(this.options).off('click').on('click', 'h2', function () {
                var val = $(this).html();
                var posFir = $('#posFir');
                posFir.html(val);
                posThr.html('');
                posTwo.html('');
                $(this).next()
                    .stop().slideToggle(300)
                    .parent().siblings().children(".navTwo")
                    .stop().slideUp(300);
                that.opNew()
            });

            $(this.options).find('li').off('click').on('click', '.hit', function () {
                var val = $(this).html();
                $(this).addClass('active');
                $(this).siblings().removeClass('active');
                $(this).parent().parent().siblings().find('li').removeClass('active');
                $(this).parent().siblings().children(".navTwo");
                posThr.html('>');
                posTwo.html(val);
                that.opNew()
            });
        },
        opNew: function () {
            //这个方法是打开的新的地址
            console.log("ok")
        }
    }
    var twomenu = new TwoMenu("#page-sidebar-menu");
    $(document).ready(function () {
        $('.navTwo li a').each(function (index, item) {
            var input = document.getElementById('fid');
            var dataname = $(item).attr("dataName");
            if (input.value == dataname) {
                console.log(this)
                $(this).parent().addClass('active');
                $(this).parent().parent()[0].style.display = "block"
            }
        });
    })
</script>


<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="../views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html lang="en" class="no-js">
<head>
    <meta charset="utf-8"/>
    <title><sitemesh:title/></title>
    <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
    <meta content="" name="description"/>
    <meta content="" name="author"/>
    <!-- BEGIN GLOBAL MANDATORY STYLES -->
    <link href="${ctx }/static/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx }/static/css/bootstrap-responsive.min.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx }/static/css/font-awesome.min.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx }/static/css/style-metro.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx }/static/css/style.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx }/static/css/style-responsive.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx }/static/css/default.css" rel="stylesheet" type="text/css" id="style_color"/>
    <link href="${ctx }/static/css/uniform.default.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx }/static/css/codemirror.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx }/static/css/theme/darcula.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx }/static/css/theme/3024-day.css" rel="stylesheet" type="text/css"/>

    <style type="text/css">
        .CodeMirror{border:1px solid black;font-size:15px;width:65.95744680851064%;height:214px}
    </style>
    <link rel="stylesheet" href="${ctx }/static/jshint/fold/foldgutter.css"/>
    <script src="${ctx }/static/js/fold/foldcode.js"></script>
    <script src="${ctx }/static/js/fold/foldgutter.js"></script>
    <script src="${ctx }/static/js/fold/brace-fold.js"></script>
    <script src="${ctx }/static/js/fold/comment-fold.js"></script>

    <link rel="stylesheet" href="${ctx }/static/js/hint/show-hint.css">

    <!-- END GLOBAL MANDATORY STYLES -->
    <link rel="shortcut icon" href="${ctx }/static/image/favicon.ico"/>
    <!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
    <!-- BEGIN CORE PLUGINS -->
    <script src="${ctx }/static/js/jquery-1.10.1.min.js" type="text/javascript"></script>
    <script src="${ctx }/static/js/jquery-migrate-1.2.1.min.js" type="text/javascript"></script>
    <!-- IMPORTANT! Load jquery-ui-1.10.1.custom.min.js before bootstrap.min.js to fix bootstrap tooltip conflict with jquery ui tooltip -->
    <script src="${ctx }/static/js/jquery-ui-1.10.1.custom.min.js" type="text/javascript"></script>
    <script src="${ctx }/static/js/bootstrap.min.js" type="text/javascript"></script>
    <!--[if lt IE 9]>
    <script src="${ctx }/static/js/excanvas.min.js"></script>
    <script src="${ctx }/static/js/respond.min.js"></script>
    <![endif]-->
    <script src="${ctx }/static/js/jquery.slimscroll.min.js" type="text/javascript"></script>
    <script src="${ctx }/static/js/jquery.blockui.min.js" type="text/javascript"></script>
    <script src="${ctx }/static/js/jquery.cookie.min.js" type="text/javascript"></script>
    <script src="${ctx }/static/js/jquery.uniform.min.js" type="text/javascript"></script>
    <script src="${ctx }/static/js/codemirror.js" type="text/javascript"></script>
    <script src="${ctx }/static/js/matchbrackets.js" type="text/javascript"></script>
    <script src="${ctx }/static/js/groovy.js" type="text/javascript"></script>
    <script src="${ctx }/static/js/hint/show-hint.js" type="text/javascript"></script>
    <script src="${ctx }/static/js/hint/anyword-hint.js" type="text/javascript"></script>
    <!-- END CORE PLUGINS -->
    <!-- END JAVASCRIPTS -->
    <sitemesh:head/>
</head>
<!-- END HEAD -->
<!-- BEGIN BODY -->
<body class="page-header-fixed">
<%@ include file="../layouts/header.jsp"%>
<!-- BEGIN CONTAINER -->
<div class="page-container">
    <%@ include file="../layouts/left.jsp"%>
    <!-- BEGIN PAGE -->
    <div class="page-content">
        <!-- BEGIN PAGE CONTAINER-->
        <sitemesh:body />
        <!-- END PAGE CONTAINER-->
    </div>
    <!-- END PAGE -->
</div>
<!-- END CONTAINER -->
<%@ include file="../layouts/footer.jsp"%>
<!-- END BODY -->
</html>
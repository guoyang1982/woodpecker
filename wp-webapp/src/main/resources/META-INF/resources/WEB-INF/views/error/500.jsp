<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true"%>
<html>
<head>
    <title>出现错误</title>
    <meta charset="utf-8"/>
</head>
<body>
<H1>错误：</H1><%=exception%>
<H2>错误内容：</H2>
<%
    exception.printStackTrace(response.getWriter());
%>
</body>
</html>

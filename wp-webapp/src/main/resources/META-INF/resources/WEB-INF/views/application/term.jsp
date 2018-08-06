<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="../common/taglibs.jsp" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <title>应用管理</title>
    <!-- BEGIN PAGE LEVEL STYLES -->


    <script type="text/javascript" src="${ctx}/static/js/termlib.js"></script>

    <!-- END PAGE LEVEL SCRIPTS -->
    <script type="text/javascript">
        var term;
        var wsUrl = "ws://localhost:${webSocketPort}/websocket"
        websocket = new WebSocket(wsUrl);
        websocket.onopen = function (evt) {
            //alert("dddd");
            term = new Terminal(
                {
                    x: 255,
                    y: 55,
                    cols: 136,
                    rows: 35,
                    ps:'wp>',
                    termDiv: 'termDiv',
                    bgColor: '#232e45',
                    initHandler: termInitHandler,
                    handler: termHandler,
                    exitHandler: termExitHandler,
                    ctrlHandler: termCtrlHandler
                }
            );
            term.open();
        }

        websocket.onmessage = function (evt) {
            term.newLine();
            if (evt.data == "over") {
                term.prompt();
            } else {
                term.write(evt.data);
            }
        }

        function termInitHandler() {
            websocket.send("connection=${ip}");
        }

        function termCtrlHandler() {
            if (this.inputChar == 100) {
                websocket.send("kill");
            }

        }

        function termExitHandler() {
            websocket.send("quit");
        }


        function termHandler() {
            this.newLine();
            this.lineBuffer = this.lineBuffer.replace(/^\s+/, '');
            var argv = this.lineBuffer.split(/\s+/);
            var cmd = argv[0];

            switch (cmd) {
                case 'exit':
                    this.close();
                    return;

                default:
                    if (this.lineBuffer != '') {
                        websocket.send(this.lineBuffer);
                    }
            }
            this.prompt();
        }

        function stop() {
            websocket.send("kill");

        }

    </script>


    <style type="text/css">
        body, p, a, td, li {
            font-family: courier, fixed, swiss, sans-serif;
            font-size: 12px;
            color: #cccccc;
        }

        .lh15 {
            line-height: 15px;
        }

        .term {
            font-family: "Courier New", courier, fixed, monospace;
            font-size: 12px;
            color: #94aad6;
            background: none;
            letter-spacing: 1px;
        }

        .term .termReverse {
            color: #232e45;
            background: #95a9d5;
        }

        a, a:link, a:visited {
            text-decoration: none;
            color: #77dd11;
        }

        a:hover {
            text-decoration: underline;
            color: #77dd11;
        }

        a:active {
            text-decoration: underline;
            color: #eeeeee;
        }

        a.termopen, a.termopen:link, a.termopen:visited {
            text-decoration: none;
            color: #77dd11;
            background: none;
        }

        a.termopen:hover {
            text-decoration: none;
            color: #222222;
            background: #77dd11;
        }

        a.termopen:active {
            text-decoration: none;
            color: #222222;
            background: #eeeeee;
        }

        table.inventory td {
            padding-bottom: 20px !important;
        }

        tt, pre {
            font-family: courier, fixed, monospace;
            color: #ccffaa;
            font-size: 12px;
            line-height: 15px;
        }

        li {
            line-height: 15px;
            margin-bottom: 8px !important;
        }

        .dimmed, .dimmed *, .dimmed * * {
            background-color: #222222 !important;
            color: #333333 !important;
        }

        @media print {
            body {
                background-color: #ffffff;
            }

            body, p, a, td, li, tt {
                color: #000000;
            }

            pre, .prop {
                color: #000000;
            }

            h1 {
                color: #000000;
            }

            a, a:link, a:visited {
                color: #000000;
            }

            a:hover {
                color: #000000;
            }

            a:active {
                color: #000000;
            }

            table.inventory {
                display: none;
            }
        }
        .footer_term{
            position: fixed;
            bottom: 0;
        }
    </style>

</head>
<body>

<div id="termDiv" style="position:absolute; visibility: hidden; z-index:1;"></div>

<%--<div id="term" class="footer_term">--%>
    <%--&lt;%&ndash;<a href="javascript:stop()">退出当前命令</a>&ndash;%&gt;--%>
    <%--<button id="stop" onclick="stop()" type="button" class="btn green">退出当前命令</button>--%>
<%--</div>--%>

</body>
</html>


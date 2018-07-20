<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ include file="common/taglibs.jsp" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title></title>
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <style type="text/css">
        .all_section{
            font-family: "Microsoft YaHei";
            font-size: 13px;
            line-height: 16px;
            color: #333;
            padding-top: 40px;
            padding-left: 40px;
        }
        .all_section li{
            list-style-type:none;
            padding-bottom: 2px;
        }
        .all_section ol{
            padding-left: 30px;
        }
        .top_title{
            font-size: 18px;
            font-weight: bold;
            padding-bottom: 5px;
        }
        .title{
            font-size: 15px;
            font-weight: 400;
        }
        .title_list01{
            color: #606266;
            padding-left: 10px;

        }
        .title_list02{
            color: #606266;
            padding-left: 30px;

        }
        .title_list03{
            color: #606266;
            padding-left: 50px;
        }
    </style>
</head>
<body>
<section class="all_section">
    <p class="top_title">介绍</p >
    <ol>
        <li class="title">啄木鸟（woodpecker）是一个集成异常搜集、告警及分析的分布式系统，由探针、server端、web端三部分组成。</li>
        <li class="title_list01">1.探针采用javaagent实现，进行无侵入式的异常日志搜集并上报给异常队列。</li>
        <li class="title_list01">2.server端可以集群部署，从异常队列解析并处理异常信息，可以实现灵活的告警包括短信、邮件以及微信。</li>
        <li class="title_list01">3.web端用于应用信息注册、告警配置、规则配置以及异常信息可视化等。</li>
    </ol>
    <p class="top_title">部署</p >

    <ol>
        <li class="title">1. 部署server端</li>
        <li class="title_list01">server端采用SpringBoot编写可以以jar方式直接启动，集群部署启动多个实例即可，启动前需要配置相应环境的redis集群和monggdb的信息。</li>

    </ol>
    <ol>
        <li class="title">2. 部署web端</li>
        <li class="title_list01">web端采用SpringBoot编写可以以jar方式直接启动，暂不支持集群部署，启动前需要配置redis和MongoDB的信息</li>
    </ol>

    <p class="top_title">使用</p >
    <ol>
        <li class="title">1. web端配置</li>
        <li class="title_list01">1.1 登录web端</li>
        <li class="title_list02">1.1.1 用户管理</li>
        <li class="title_list03">
            只有管理员权限才能看到，可以对用户修改密码、升级权限、添加新用户。
        </li>
        <li class="title_list02">1.1.2 配置应用</li>
        <li class="title_list03">
            应用名只能有一个，如果已经存在可以联系管理员或者应用管理者邀请加入,新建的应用自己就是应用管理者。
        </li>
        <li class="title_list02">1.1.3 配置规则</li>
        <li class="title_list03">
            规则是为了过滤异常信息，可以控制哪些异常信息不需要发送报警，规则可以和报警关联。
            规则配置是用groovy脚本写的，返回值是false或者true，如果为false就是不过滤异常发送报警，如果为true则过滤异常信息发送报警。
        </li>
        <li class="title_list02">1.1.4 配置报警</li>
        <li class="title_list03">
            报警配置的作用是为了控制发送报警的应用。</br>
            能够配置要发送报警的应用、服务器ip、异常类型，并能关联过滤规则，配置告警阀值（指的是距离上次报警时间的数量）、频率（指的是配置时间内发送1次）。</br>
            配置的应用+服务器ip+异常类型确定一个配置规则，如果有多条，可以按需配置多个。</br>
            配置发送通道，目前支持：邮件、短信、微信公众号。
        </li>

        <li class="title">2. 客户端配置</li>

        <li class="title_list01">1.1 获取探针</li>
        <li class="title_list02">1.1.1 手动编译</li>
        <li class="title_list02">1.1.2 直接使用中央仓库稳定版本</li>
        <li class="title_list02">1.1.3 配置woodpecker.properties文件</li>
        <li class="title_list03">
            主要配置里面的如下几项:</br>
            application.name=web配置的应用名称（一定要一致）</br>
            redis.cluster.host=redis集群地址</br>
            redis.cluster.password=redis集群密码，可以为空</br>
            log.netty.server.port=远程控制的端口号，防止端口冲突，如果冲突可以换一个
        </li>

        <li class="title_list01">1.2 运行</li>
        <li class="title_list02">1.2.1 SpringBoot</li>
        <li class="title_list03">java -javaagent:/jar包路径/wpclient-agent.jar=/配置文件路径/woodpecker.properties  -jar 运行的jar包.jar</li>
        </li>
        <li class="title_list02">1.2.2 Tomcat</li>
        <li class="title_list03">加入agent到CATALINA_OPTS 在 Tomcat 启动脚本 (catalina.sh). CATALINA_OPTS="$CATALINA_OPTS -javaagent:/jar包路径/wpclient-agent.jar=/配置文件路径/woodpecker.properties"</li>
        </li>
        <li class="title_list02">1.2.3 Resin</li>
        <li class="title_list03">加入以下配置到 /conf/resin.xml: <jvm-arg>-javaagent:/jar包路径/wpclient-agent.jar=/配置文件路径/woodpecker.properties</jvm-arg></li>
        </li>
        <li class="title_list01">1.3 <a target="_blank" href="https://github.com/guoyang1982/woodpecker-client">使用详情</a ></li>
    </ol>

</section>
</body>
</html>


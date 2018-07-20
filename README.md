# woodpecker
    包含日志收集的server端，和日志相关展现和告警配置的web端。
   [日志报警的客户端](https://github.com/guoyang1982/woodpecker-client)
   按照链接里的步骤使用客户端后，应用里的log的日志输出就都能收集到，默认是error级别的日志。如：
   ```java
      try {
         return HttpUtils.get(url, null, params, TIME_OUT);
       } catch (Exception e) {
         logger.error("call url fail!e={}", e);
         return "";
       }
   ```
   
# 框架图

![Aaron Swartz](https://github.com/guoyang1982/woodpecker/blob/master/doc/%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%9B%BE.jpg)


![Aaron Swartz](https://github.com/guoyang1982/woodpecker/blob/master/doc/%E7%89%A9%E7%90%86%E6%A1%86%E6%9E%B6%E5%9B%BE.jpg)


# 如何使用

## 1.clone代码
    git clone git@github.com:guoyang1982/woodpecker.git
## 2.编译安装
    mvn clean insall
## 3.快速安装redis集群
    如果想快速在本机搭建验证整个系统，可以按照如下方式快速搭建redis集群，需要本机安装docker
   #### clone代码
    git clone git@github.com:guoyang1982/docker-redis-cluster.git
   #### build镜像
    docker-compose -f compose.yml
   #### 启动镜像
    docker-compose -f compose.yml up -d
   
   通过如上步骤即可搭建本机的redis集群，可能构建有点慢，需要耐心，详细的可看：
   [传送门](https://github.com/guoyang1982/docker-redis-cluster)

## 4.快速安装mongodb
    如果想快速在本机搭建验证整个系统，可以按照如下方式快速安装mongodb，需要本机安装docker
   #### 拉取mongo镜像:
     docker pull mongo:3.2
   #### 运行mongo镜像:
     docker run -p 27017:27017 -v $PWD/db:/data/db -d mongo:3.2

## 5.项目配置
    server端和web端默认都是dev环境，按照以上方法安装完redis集群和mongodb后不需要改动就能快速验证。
    如果想发布到生产环境需要改application-prod.properties，运行时启动生产环境的配置就可以。
## 6.运行
   #### server端启动：
    java -jar wp-server-0.0.1-SNAPSHOT.jar
   #### web端启动：
    java -jar wp-webapp-0.0.1-SNAPSHOT.jar

## 7.维护人员
   * 郭阳
   * 梅俊杰

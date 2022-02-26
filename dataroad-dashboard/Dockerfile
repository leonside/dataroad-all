FROM bus.ga/base/dragon-java:jdk8-alpine-x86
#作者
MAINTAINER leon
#声明一个挂载点，容器内此路径会对应宿主机的某个文件夹
#VOLUME /tmp
#复制上下文目录下的target/demo-1.0.0.jar 到容器里
COPY target/dataroad-dashboard-0.5.jar /opt/dataroad-dashboard-0.5.jar

COPY target/dataroad-dist /opt/dataroad-dist

#bash方式执行，使demo-0.0.1jar可访问
#RUN新建立一层，在其上执行这些命令，执行结束后， commit 这一层的修改，构成新的镜像。
#RUN bash -c "touch /opt/dataroad-dashboard-0.5.jar"

#声明运行时容器提供服务端口，这只是一个声明，在运行时并不会因为这个声明应用就会开启这个端口的服务
EXPOSE 8089

#指定容器启动程序及参数   <ENTRYPOINT> "<CMD>"
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005","-jar","/opt/dataroad-dashboard-0.5.jar"]
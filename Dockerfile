FROM centos:7
WORKDIR /opt/app

# Do not change this line position, because its used by the below nginx yum install command
COPY deploy/docker/nginx.repo /etc/yum.repos.d/nginx.repo

RUN yum -y update; yum clean all
RUN yum install -y nginx-1.20.1; yum clean all
RUN yum -y install openssl-devel openssl wget zip unzip dnf which; yum clean all
RUN set -x && dnf install --nodocs java-11-openjdk -y && dnf autoremove -y && dnf clean all -y && rm -rf /var/cache/dnf

RUN mkdir /etc/nginx/logs
RUN mkdir /opt/app/lib
RUN mkdir /opt/app/ts_data

COPY deploy/docker/nginx.conf /etc/nginx/nginx.conf
COPY deploy/docker/cacerts /usr/lib/jvm/jre/lib/security/
COPY deploy/docker/entrypoint.sh /opt/app/entrypoint.sh
COPY ui/dist/testsigma-angular /opt/app/angular/
COPY server/target/testsigma-server.jar /opt/app/testsigma-server.jar
COPY server/target/lib/ /opt/app/lib/
COPY server/src/main/scripts/posix/start.sh /opt/app/

RUN rm -f /etc/nginx/conf.d/default.conf
RUN chmod +x /opt/app/start.sh
RUN chmod +x /opt/app/entrypoint.sh

ENV IS_DOCKER_ENV=true
ENV MYSQL_HOST_NAME=${MYSQL_HOST_NAME:-mysql}
ENV TS_DATA_DIR=/opt/app/ts_data
ENV TESTSIGMA_WEB_PORT=${TESTSIGMA_WEB_PORT:-443}
ENV TESTSIGMA_SERVER_PORT=${TESTSIGMA_SERVER_PORT:-9090}

EXPOSE $TESTSIGMA_WEB_PORT
EXPOSE $TESTSIGMA_SERVER_PORT

ENTRYPOINT ["/opt/app/entrypoint.sh"]

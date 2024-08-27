#!/bin/bash

APP_MAIN=com.webank.wsas.front.FrontApplication
CURRENT_DIR=$(cd `dirname $0` ;pwd)
CLASSPATH=${CURRENT_DIR}/conf/:${CURRENT_DIR}/apps/*:${CURRENT_DIR}/lib/*
#LOG_DIR=${CURRENT_DIR}/logs
LOG_DIR=/data/app/logs/wsas-front
CONF_DIR=${CURRENT_DIR}/conf
#JDK路径
#JAVA_HOME="/nemo/jdk1.8.0_141"
JAVA_HOME="/data/wepkg/openjdk8u382-b05"
# AOMP会读取到host的ip
HOST_IP=[@HOSTIP]

SERVER_PORT=$(cat $CONF_DIR/application.yml | grep "server:" -A 3 | grep "port" | awk '{print $2}'| sed 's/\r//')
if [ ${SERVER_PORT}"" = "" ];then
    echo "$CONF_DIR/application.yml server port has not been configured"
    exit 1
fi

JMX_PORT=`expr $SERVER_PORT + 1`

if [ ${JAVA_HOME}"" = "" ];then
    echo "JAVA_HOME has not been configured"
    exit 1
fi

mkdir -p $LOG_DIR

startWaitTime=30
processPid=0
processStatus=0
checkProcess(){
    server_pid=`ps aux | grep java | grep $LOG_DIR | grep $APP_MAIN | awk '{print $2}'`
    if [ -n "$server_pid" ]; then
        processPid=$server_pid
        processStatus=1
    else
        processPid=0
        processStatus=0
    fi
}

JAVA_OPTS=" -Dfile.encoding=UTF-8"
JAVA_OPTS+=" -Dcom.sun.management.jmxremote.port=$JMX_PORT -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"
JAVA_OPTS+=" -Djava.rmi.server.hostname=${HOST_IP}"
#JAVA_OPTS+=" -Xmx1024m -Xms1024m -Xss512k -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m"
JAVA_OPTS+=" -Xmx2048m -Xms2048m -Xss512k -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m"
JAVA_OPTS+=" -Dreactor.netty.pool.leasingStrategy=lifo"
JAVA_OPTS+=" -XX:+DisableExplicitGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:${LOG_DIR}/jvm_%t.log"
JAVA_OPTS+=" -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${LOG_DIR}/heap_error.hprof"

start(){
    checkProcess
    echo "==============================================================================================="
    if [ $processStatus == 1 ]; then
        echo "Server $APP_MAIN Port $SERVER_PORT is running PID($processPid)"
        echo "==============================================================================================="
    else
        echo -n "Server $APP_MAIN Port $SERVER_PORT ..."
        nohup $JAVA_HOME/bin/java $JAVA_OPTS -cp $CLASSPATH $APP_MAIN >> $LOG_DIR/server.out 2>&1 &

        count=1
        result=0
        while [ $count -lt $startWaitTime ] ; do
           checkProcess
           if [ $processPid -ne 0 ]; then
               result=1
               break
           fi
           let count++
           echo -n "."
           sleep 1
       done

       if [ $result -ne 0 ]; then
           echo "PID($processPid) [Starting]. Please check message through the log file (default path:$LOG_DIR)."
           echo "==============================================================================================="
       else
           echo "[Failed]. Please check message through the log file (default path:$LOG_DIR)."
           echo "==============================================================================================="
       fi
    fi
}

start

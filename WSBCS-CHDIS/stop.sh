#!/bin/bash

APP_MAIN=com.webank.wsbcs.chdis.Application
CURRENT_DIR=$(cd `dirname $0` ;pwd)
CONF_DIR=${CURRENT_DIR}/conf

SERVER_PORT=$(cat $CONF_DIR/application.yml | grep "server:" -A 3 | grep "port" | awk '{print $2}'| sed 's/\r//')
if [ ${SERVER_PORT}"" = "" ];then
    echo "$CONF_DIR/application.yml server port has not been configured"
    exit 1
fi
JMX_PORT=`expr $SERVER_PORT + 1`

processPid=0
checkProcess(){
    server_pid=`ps aux | grep java  | grep $JMX_PORT | grep $APP_MAIN | awk '{print $2}'`
    if [ -n "$server_pid" ] && [ -n "$(echo $server_pid| sed -n "/^[0-9]\+$/p")" ]; then
        processPid=$server_pid
    else
        processPid=0
    fi
}

stop(){
    checkProcess
    echo "==============================================================================================="
    if [ $processPid -ne 0 ]; then
        echo -n "Stopping Server $APP_MAIN Port $SERVER_PORT PID($processPid)..."
        kill -9 $processPid
        if [ $? -eq 0 ]; then
            echo "[Success]"
            echo "==============================================================================================="
        else
            echo "[Failed]"
            echo "==============================================================================================="
            exit 1
        fi
    else
        echo "Server $APP_MAIN Port $SERVER_PORT is not running"
        echo "==============================================================================================="
        exit 0
    fi
}

stop
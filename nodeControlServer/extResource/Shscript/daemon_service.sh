#!/bin/bash
 
BASE_DIR=/Project2018Servers/nodeControlServer/build/libs
START_COMMAND="java -jar $BASE_DIR/Project2018Servers.jar"
PID_FILE=$BASE_DIR/dropwizard-rest-stub.pid
LOG_DIR=$BASE_DIR/logs
 
mkdir $LOG_DIR
 
start() {
  PID=`$START_COMMAND > $LOG_DIR/init.log 2>$LOG_DIR/init.error.log & echo $!`
}
 
case "$1" in
start)
    if [ -f $PID_FILE ]; then
        PID=`cat $PID_FILE`
        if [ -z "`ps axf | grep ${PID} | grep -v grep`" ]; then
            start
        else
            echo "Already running [$PID]"
            exit 0
        fi
    else
        start
    fi
 
    if [ -z $PID ]; then
        echo "Failed starting"
        exit 1
    else
        echo $PID > $PID_FILE
        echo "Started [$PID]"
        exit 0
    fi
;;
status)
    if [ -f $PID_FILE ]; then
        PID=`cat $PID_FILE`
        if [ -z "`ps axf | grep ${PID} | grep -v grep`" ]; then
            echo "Not running (process dead but PID file exists)"
            exit 1
        else
            echo "Running [$PID]"
            exit 0
        fi
    else
        echo "Not running"
        exit 0
    fi
;;
stop)
    if [ -f $PID_FILE ]; then
        PID=`cat $PID_FILE`
        if [ -z "`ps axf | grep ${PID} | grep -v grep`" ]; then
            echo "Not running (process dead but PID file exists)"
            rm -f $PID_FILE
            exit 1
        else
            PID=`cat $PID_FILE`
            kill -term $PID
            echo "Stopped [$PID]"
            rm -f $PID_FILE
            exit 0
        fi
    else
        echo "Not running (PID not found)"
        exit 0
    fi
;;
restart)
    $0 stop
    $0 start
;;
*)
    echo "Usage: $0 {status|start|stop|restart}"
    exit 0
esac
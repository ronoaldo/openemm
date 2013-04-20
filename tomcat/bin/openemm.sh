#!/bin/bash

# ENV:
VERSION="1.0.2"
LANG="en_US.ISO8859_1"
OPENEMM_SW="/opt/openemm"
CATALINA_BASE="$HOME"
CATALINA_HOME="$OPENEMM_SW/tomcat"
CATALINA_TMPDIR="$HOME/temp"
JAVA_HOME="$OPENEMM_SW/java"
JAVA_OPTS="-Xms256m -Xmx512m -XX:MaxPermSize=256m -Xss256k"
PROGRAM_NAME=`basename $0`

test_user() {
	if [ $USER != "openemm" ]; then
		echo "Wrong user $USER! Please change user to openemm."
		exit 2
	fi
}
search_file_exist() {
	if [ -e $1 ]; then
		echo "OK"
	else
		echo FAILED
		exit 2
	fi
}
test_config() {
    echo -n "Check Java installation          :"
	search_file_exist $JAVA_HOME/bin/java
    echo -n "Check Tomcat installation        :"
	search_file_exist $CATALINA_HOME/bin/bootstrap.jar
}

test_tomcat_running() {
	JPS_RUNNING_PID=`ps -eo pid,command|grep org.apache.catalina|grep -v grep|awk '{print $1}'`
	}

start_gui() {
	test_tomcat_running
        cd $HOME
        if [ $JPS_RUNNING_PID ]; then
        	echo "Sorry, Tomcat is already running as PID $JPS_RUNNING_PID!"
            exit 2
        fi
        echo "Starting up Tomcat ......"
        test_config
        $CATALINA_HOME/bin/startup.sh
}

start() {
	$HOME/bin/bounce.sh start
	$HOME/bin/merger.sh start
	$HOME/bin/mailer.sh start
	$HOME/bin/slrtscn.sh start
	start_gui
}

stop_gui() {
        test_tomcat_running
        if [ $JPS_RUNNING_PID ]; then
			echo "Shutting down Tomcat ......"
			$CATALINA_HOME/bin/shutdown.sh
                sleep 2
                n=0
                echo -n "Waiting for shutdown of Tomcat:"
				test_tomcat_running
		        while [ $JPS_RUNNING_PID ]; do
					if [ $n -gt 9 ]; then
						kill -9 $JPS_RUNNING_PID
						echo "Terminating Tomcat the hard way with kill -9 $JPS_RUNNING_PID"
		else
                	echo -n "."
		fi
		sleep 2
		test_tomcat_running
		n=`expr $n + 1`
	done
	echo "."
        else
                echo "No Tomcat is running."
        fi
}

stop() {
	stop_gui
	$HOME/bin/slrtscn.sh stop
	$HOME/bin/mailer.sh stop
	$HOME/bin/merger.sh stop
	$HOME/bin/bounce.sh stop
}

restart() {
	stop
	start
}

test_user
$HOME/bin/scripts/config.sh
cd $HOME

export CATALINA_BASE CATALINA_HOME CATALINA_TMPDIR JAVA_HOME JAVA_OPTS LANG
export PATH="$PATH:$JAVA_HOME/bin"

case "$1" in
	start)
		start
		;;
	start_gui)
		start_gui
		;;
	status)
		test_tomcat_running
                if [ $JPS_RUNNING_PID ]; then
                        echo "Tomcat is running as PID $JPS_RUNNING_PID."
		else
			echo "No Tomcat process is running."
                fi
		;;
	restart)
		restart
		;;
	version)
		echo "$PROGRAM_NAME:	$VERSION"
		echo "Username: 	$USER"
		echo "Hostname: 	$HOSTNAME"
		$JAVA_HOME/bin/java -classpath "$CATALINA_HOME/lib/catalina.jar" org.apache.catalina.util.ServerInfo
		;;
	stop)
		stop
		;;
	stop_gui)
		stop_gui
		;;
	*)
		echo "Error, permitted parameters are:"
		echo "$PROGRAM_NAME (start | stop | restart | status | version)"
		exit 2
		;;
esac
exit 0

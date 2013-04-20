@echo off & setlocal enableextensions

IF NOT "%JAVA_HOME%"=="" GOTO got_java_home

FOR /F "tokens=2,*" %%a IN (
	'REG QUERY "HKLM\SOFTWARE\JavaSoft\Java Development Kit" /V CurrentVersion ^| find "REG_SZ"'
) do set _jdkversion=%%b
IF NOT "%_jdkversion%"=="" GOTO got_jdkversion
ECHO JDK not found
GOTO end

:got_jdkversion
FOR /F "tokens=2,*" %%a IN (
	'REG QUERY "HKLM\SOFTWARE\JavaSoft\Java Development Kit\%_jdkversion%" /V JavaHome ^| find "REG_SZ"'
) do set JAVA_HOME=%%b

:got_java_home
echo JAVA_HOME=%JAVA_HOME%

set DEPLOY=..\webapps\openemm
set CLASSPATH=%DEPLOY%\WEB-INF\classes;%DEPLOY%\WEB-INF\lib\saaj.jar;%DEPLOY%\WEB-INF\lib\axis.jar;%DEPLOY%\WEB-INF\lib\jaxrpc.jar;%DEPLOY%\WEB-INF\lib\commons-logging-1.1.jar;%DEPLOY%\WEB-INF\lib\commons-digester-1.8.jar;%DEPLOY%\WEB-INF\lib\commons-discovery.jar;%DEPLOY%\WEB-INF\lib\wsdl4j.jar;%DEPLOY%\WEB-INF\lib\activation.jar;%DEPLOY%\WEB-INF\lib\mail.jar

"%JAVA_HOME%\bin\java" -classpath %CLASSPATH% org.apache.axis.wsdl.Java2WSDL %1 %2 %3 %4 %5 %6 %7 %8 %9
:end
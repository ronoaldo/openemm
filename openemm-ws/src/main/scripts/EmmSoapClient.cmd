@echo off

set JAVA_HOME=C:\Programme\Java\jdk1.6.0_xx
set DEPLOY=..\webapps\openemm

%JAVA_HOME%\bin\java -Xbootclasspath/a:%DEPLOY%\WEB-INF\lib\mail.jar;%DEPLOY%\WEB-INF\lib\jaxrpc.jar;%DEPLOY%\WEB-INF\lib\wsdl4j-1.6.1.jar;%DEPLOY%\WEB-INF\lib\axis.jar;%DEPLOY%\WEB-INF\lib\commons-logging-1.1.jar;%DEPLOY%\WEB-INF\lib\commons-discovery.jar;%DEPLOY%\WEB-INF\lib\saaj.jar;%DEPLOY%\WEB-INF\classes -Djavax.net.ssl.trustStore=client.keystore org.agnitas.webservice.EmmSoapClient %1 %2 %3 %4 %5 %6 %7 %8 %9

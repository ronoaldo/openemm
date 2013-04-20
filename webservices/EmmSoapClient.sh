#!/bin/bash
DEPLOY=../webapps/openemm
java -Xbootclasspath/a:$DEPLOY/WEB-INF/lib/mail.jar:$DEPLOY/WEB-INF/lib/jaxrpc.jar:$DEPLOY/WEB-INF/lib/activation.jar:$DEPLOY/WEB-INF/lib/wsdl4j.jar:$DEPLOY/WEB-INF/lib/axis.jar:$DEPLOY/WEB-INF/lib/commons-logging-1.1.jar:$DEPLOY/WEB-INF/lib/commons-discovery.jar:$DEPLOY/WEB-INF/lib/saaj.jar:$DEPLOY/WEB-INF/classes -Djavax.net.ssl.trustStore=client.keystore org.agnitas.webservice.EmmSoapClient "$@"

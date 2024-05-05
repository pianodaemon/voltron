#!/bin/sh

VOLTRON_PATH="./glue/build/libs"   # XXX: Set this up to any convenient path of yours
WLP_HOME=~/wlp                     # XXX: Set this up to any convenient path of yours
JMX_REMOTE_USER_ID="admin"
JMX_REMOTE_USER_PASS="1234qwer"
KEY_STORE_PASSWORD="1234qwer"
ALGO_KEY=PKCS12

MAIN_CMD="java \
  -Djava.class.path="${VOLTRON_PATH}/voltron.jar:${WLP_HOME}/clients/restConnector.jar:jconsole.jar" \
  -Djavax.net.ssl.trustStore="${WLP_HOME}/usr/servers/defaultServer/resources/security/key.p12" \
  -Djavax.net.ssl.trustStorePassword=${KEY_STORE_PASSWORD} \
  -Djavax.net.ssl.trustStoreType=$ALGO_KEY \
  -Djmx.remote.credentials=${JMX_REMOTE_USER_ID},${JMX_REMOTE_USER_PASS} \
  voltron.cli.Main"

$MAIN_CMD $@

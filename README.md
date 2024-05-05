## Voltron (Ready To Form Voltron)
Voltron stands for tampering the liberty server xml file through a command line approach.

### Build and run for debugging purposes
#### Compile and run along with needed arguments
```sh
./gradlew run --args=[any application arguments]
```

### Build and deploy in production
#### Compile and bundle along with dependencies for later execution
Once the shadowJar has been succesfully compiled, please proceed to move it into your production's path
```sh
./gradlew shadowJar
mv ./makeup/build/libs/voltron.jar $YOUR_PRODUCTION_PATH
```

#### Execution via jre (java runtime)
Within production path proceed to execute via the java runtime.
```sh
java -jar  ./voltron.jar [any application arguments]
```
> Note: the `java runtime` is distributed by oracle without sdk.

#### Execution via bourne shell script
System administrators can also wrap the execution through a shell script featuring the following content
```sh
cat voltron.sh
#!/bin/sh

VOLTRON_PATH="."  # XXX: Set this up to any convenient path of yours
WLP_HOME=~/wlp
KEY_STORE_PASSWORD="1234qwer"
ALGO_KEY=PKCS12

MAIN_CMD="java \
  -Djava.class.path="${VOLTRON_PATH}/voltron.jar:${WLP_HOME}/clients/restConnector.jar:jconsole.jar" \
  -Djavax.net.ssl.trustStore="${WLP_HOME}/usr/servers/defaultServer/resources/security/key.p12" \
  -Djavax.net.ssl.trustStorePassword=${KEY_STORE_PASSWORD} \
  -Djavax.net.ssl.trustStoreType=$ALGO_KEY \
  -Djmx.remote.credentials=admin,1234qwer \
  voltron.cli.Main"

$MAIN_CMD $@
```

Make the script executable
```sh
chmod +x voltron.sh
```

Then it would be executed as follow
```sh
./voltron.sh [any application arguments]
```

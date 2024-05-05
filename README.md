## Voltron (Ready To Form Voltron)
Voltron stands for tampering the liberty server xml file through a command line approach.

### Build and deploy in production
#### Compile and bundle along with dependencies for later execution
Once the shadowJar has been succesfully compiled, please proceed to move it into your production's path
```sh
./gradlew shadowJar
mv ./glue/build/libs/voltron.jar $YOUR_PRODUCTION_PATH
```

#### Liberty server configuration (defaultServer)
We configure a secure JMX connection by enabling the Transport Security feature, which supports TLS connections for the rest connector. Additionally we set QuickStart security because is the simplest way to configure a user registry for testing purposes. (Below we can see how server.xml would look like)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<server>
    <featureManager>
        <feature>javaee-8.0</feature>
        <feature>restConnector-2.0</feature>
        <feature>transportSecurity-1.0</feature>
    </featureManager>
    <httpEndpoint id="defaultHttpEndpoint" host="*" httpPort="9080" httpsPort="9443"/>
    <applicationManager autoExpand="true"/>
    <keyStore id="defaultKeyStore" password="1234qwer" location="resources/security/key.p12"/>
    <quickStartSecurity userName="admin" userPassword="1234qwer"/>
    <enterpriseApplication id="guide-maven-multimodules-ear" location="guide-maven-multimodules-ear.ear" name="guide-maven-multimodules-ear"/>
    <webApplication id="app00" location="simplistic-1.0-OMEGA.war" name="app00" contextRoot="pocland00"/>
    <webApplication id="app01" location="simplistic-1.0-OMEGA.war" name="app01" contextRoot="pocland01"/>
    <webApplication id="app02" location="simplistic-1.0-OMEGA.war" name="app02" contextRoot="pocland02"/>
</server>
```

#### Private Key and Self-Signed Certificate with OpenSSL (KeyStore in Liberty)
We can use OpenSSL to create key pairs and certificates, which can then be imported into a Java KeyStore (JKS). This approach allows you to work with certificates and keys in a way that is familiar to many developers and administrators.
```sh
pianodaemon@LAPTOP-4RSVIK4C:~/voltron$ ./genks.sh
Generating RSA private key, 2048 bit long modulus (2 primes)
.............................................................................+++++
...............................................................................................................+++++
e is 65537 (0x010001)
You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.
-----
Country Name (2 letter code) [AU]:US
State or Province Name (full name) [Some-State]:California
Locality Name (eg, city) []:SJO
Organization Name (eg, company) [Internet Widgits Pty Ltd]:Immortal crab systems
Organizational Unit Name (eg, section) []:research
Common Name (e.g. server FQDN or YOUR name) []:immortalcrab.com
Email Address []:webmaster@immortalcrab.com
Importing keystore key.p12 to key.jks...
Entry for alias eplauchu successfully imported.
Import command completed:  1 entries successfully imported, 0 entries failed or cancelled
Keystore type: PKCS12
Keystore provider: SUN

Your keystore contains 1 entry

eplauchu, May 5, 2024, PrivateKeyEntry,
Certificate fingerprint (SHA-256): 5D:24:B4:F9:AC:DB:77:F8:43:01:D6:02:20:57:DC:C9:EC:9D:57:08:3D:8C:DF:4C:33:8C:A2:4A:AC:60:3D:CD
pianodaemon@LAPTOP-4RSVIK4C:~/voltron$ ls -lhs
total 20K
4.0K -rwxr-xr-x 1 pianodaemon pianodaemon  836 May  4 11:41 generate_key_store.sh
4.0K -rw-r--r-- 1 pianodaemon pianodaemon 2.9K May  5 11:00 key.jks
4.0K -rw------- 1 pianodaemon pianodaemon 2.7K May  5 11:00 key.p12
4.0K -rw------- 1 pianodaemon pianodaemon 1.7K May  5 11:00 private.key
4.0K -rw-r--r-- 1 pianodaemon pianodaemon 1.5K May  5 11:00 selfsigned.crt
```
> Note: Please do not forget to `move` these keys into $WLP_HOME/usr/servers/defaultServer/server.xml

#### Execution via bourne shell script
System administrators can also wrap the execution through a shell script featuring the following content
```sh
pianodaemon@LAPTOP-4RSVIK4C:~/voltron$ cat voltron.sh
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
```
> Note: Whenever Java attempts to connect to another application over `SSL` (e.g.: HTTPS, IMAPS, LDAPS), it will only be able to connect to applications it can trust.  The way trust is handled in Java is that you have a `truststore`

Make the script executable
```sh
chmod +x voltron.sh
```

Then it would be executed as follow
```sh
./voltron.sh [any application arguments]
```

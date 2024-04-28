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
mv ./makeup/build/libs/voltron_xml.jar $YOUR_PRODUCTION_PATH
```

#### Execution via jre (java runtime)
Within production path proceed to execute via the java runtime.
```sh
java -jar  ./voltron_xml.jar [any application arguments]
```
> Note: the `java runtime` is distributed by oracle without sdk.

#### Execution via bourne shell script
System administrators can also wrap the execution through a shell script featuring the following content
```sh
cat <<'EOF' >>voltron.sh
#!/bin/sh

VOLTRON_PATH="."  # XXX: Set this up to any convenient path of yours

java -jar $VOLTRON_PATH/voltron_xml.jar $@
EOF
```

Make the script executable
```sh
chmod +x voltron.sh
```

Then it would be executed as follow
```sh
./voltron.sh [any application arguments]
```

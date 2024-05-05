#!/bin/sh

KEY_PASSWORD="1234qwer"
KEY_STORE_PASSWORD="1234qwer"

# Generate a private key
openssl genrsa -out private.key 2048

# Generate a self-signed certificate (valid for 365 days)
openssl req -new -x509 -key private.key -out selfsigned.crt -days 365

# Create a PKCS#12 (PFX) file with the private key and certificate
openssl pkcs12 -export -in selfsigned.crt -inkey private.key -out key.p12 -name eplauchu -password pass:$KEY_PASSWORD

# Create a new JKS and import the PKCS#12 file
# I must be copied to
# cp key.jks $WLP_HOME/usr/servers/defaultServer/resources/security/
keytool -importkeystore \
  -srckeystore key.p12 \
  -srcstoretype PKCS12 \
  -srcstorepass $KEY_PASSWORD \
  -destkeystore key.jks \
  -deststoretype PKCS12 \
  -deststorepass $KEY_STORE_PASSWORD

keytool -list -keystore key.jks -storepass $KEY_STORE_PASSWORD

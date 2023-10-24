#!/bin/zsh

#Gen Tokens - with password
#openssl genrsa -des3 -out private_key.pem 2048
#openssl pkcs8 -topk8 -inform PEM -in private_key.pem -out priv_token.pem -nocrypt
YOUR_ALIAS='YOUR_ALIAS'
YOUR_KEY_PASSWORD='YOUR_KEY_PASSWORD'
YOUR_STORE_PASSWORD='YOUR_STORE_PASSWORD'

keytool -genkeypair -keystore clientkeystore.jks -alias ${YOUR_ALIAS} -keyalg rsa -keypass ${YOUR_KEY_PASSWORD} -storepass ${YOUR_STORE_PASSWORD}
#!/usr/bin/env bash

#+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
#+ Configure your environment variable here. +
#+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
export JAVA_HOME=$JAVA_HOME

PARAM=$1

DIRNAME=$0
if [ "${DIRNAME:0:1}" = "/" ];then
    CURDIR=$(dirname "$DIRNAME")
else
    CURDIR="$(pwd)"/"$(dirname "$DIRNAME")"
fi
CLOUDHUB_CLIENT_HOME=$CURDIR/../

if [ "$CLOUDHUB_CLIENT_HOME" = '' ]; then
  echo "Not set CLOUDHUB_CLIENT_HOME variable in the env, program exit with code 2."
  exit 2
fi

CONF_DIR=$CLOUDHUB_CLIENT_HOME/conf

cd "$CLOUDHUB_CLIENT_HOME" || echo "Not found the path $CLOUDHUB_CLIENT_HOME, program exit with code 3." exit 3
echo "Configuration directory is in the $CONF_DIR. If you need changes settings, please follow direction to modify files in the folder."

printf "Starting cloudhub-disk-client server......\n"

if [ "$PARAM" = "-daemon" ]; then
  nohup "$JAVA_HOME"/bin/java -jar bin/cloudhub-disk-client.jar --conf conf > /dev/null 2>&1 &
  echo "Starting cloudhub-disk-client server......[OK]"
  exit 0
else
  echo "Usage: $0 [-daemon]"
fi

exec "$JAVA_HOME"/bin/java -jar bin/cloudhub-disk-client.jar --conf "$CONF_DIR"
echo "Starting cloudhub-disk-client server......[OK]"

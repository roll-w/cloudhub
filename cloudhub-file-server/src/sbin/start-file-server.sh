#!/usr/bin/env bash

#+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
#+ Configure your environment variable here. +
#+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
export JAVA_HOME=$JAVA_HOME


DIRNAME=$0
if [ "${DIRNAME:0:1}" = "/" ];then
    CURDIR=$(dirname "$DIRNAME")
else
    CURDIR="$(pwd)"/"$(dirname "$DIRNAME")"
fi
CLOUDHUB_FILE_HOME=$CURDIR/../

if [ "$CLOUDHUB_FILE_HOME" = '' ]; then
  echo "Not set CLOUDHUB_FILE_HOME variable in the env, program exit with code 2."
  exit 2
fi

CONF_DIR=$CLOUDHUB_FILE_HOME/conf

cd "$CLOUDHUB_FILE_HOME" || echo "Not found the path $CLOUDHUB_FILE_HOME, program exit with code 3." exit 3

echo "Conf dir is in the $CONF_DIR. If you need changes settings, please follow direction to modify files in the dir."
printf "Starting cloudhub-file-server......\n"

exec "$JAVA_HOME"/bin/java -jar bin/cloudhub-file-server.jar --conf "$CONF_DIR"

echo "Starting cloudhub-file-server......[OK]"

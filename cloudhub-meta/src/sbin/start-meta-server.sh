#!/usr/bin/env bash

export JAVA_HOME=$JAVA_HOME

DIRNAME=$0
if [ "${DIRNAME:0:1}" = "/" ];then
    CURDIR=$(dirname "$DIRNAME")
else
    CURDIR="$(pwd)"/"$(dirname "$DIRNAME")"
fi
CLOUDHUB_META_HOME=$CURDIR/../

if [ "$CLOUDHUB_META_HOME" = '' ]; then
  echo "Not set CLOUDHUB_META_HOME variable in the env, program exit with code 2."
  exit 2
fi

CONF_DIR=$CLOUDHUB_META_HOME/conf

cd "$CLOUDHUB_META_HOME" || echo "Not found the path $CLOUDHUB_META_HOME, program exit with code 3." exit 3

echo "Conf dir is in the $CONF_DIR. If you need changes settings, please follow direction to modify files in the dir."
echo "Starting cloudhub-meta-server......"

exec "$JAVA_HOME"/bin/java -jar bin/cloudhub-meta-server.jar --conf "$CONF_DIR"

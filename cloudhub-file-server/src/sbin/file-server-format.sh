#!/usr/bin/env bash

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
cd "$CLOUDHUB_FILE_HOME" || exit 2

CONF_DIR=$CLOUDHUB_FILE_HOME/conf

prop(){
    grep "${1}" "$CONF_DIR"/cloudhub.conf | cut -d'=' -f2 | sed 's/\r//'
}

DATA_DIR=$(prop "cloudhub.file.store_dir")

echo "Now clear data store directory: $DATA_DIR"

rm -rf "$DATA_DIR"/data/ "$DATA_DIR"/meta/

printf "Clear successfully.\n"

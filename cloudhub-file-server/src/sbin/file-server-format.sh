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

function prop {
	[ -f "$CONF_DIR" ] && grep -P "^\s*[^#]?${1}=.*$" "$CONF_DIR" | cut -d'=' -f2
}

CONF_DIR=$CLOUDHUB_FILE_HOME/conf

DATA_DIR=$(prop "cloudhub.file.store_dir")

rm -rf "$DATA_DIR"/data "$DATA_DIR"/meta


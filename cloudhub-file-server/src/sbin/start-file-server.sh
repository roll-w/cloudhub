#!/usr/bin/env bash

#
# Cloudhub - A high available, scalable distributed file system.
# Copyright (C) 2022 Cloudhub
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
#

#+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
#+ Configure your environment variable here. +
#+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
export JAVA_HOME=$JAVA_HOME

DIRNAME=$0

if [ "${DIRNAME:0:1}" = "/" ]; then
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

CONFIG_PARAM=''

HAS_HELP=0
HAS_CONFIG=0
HAS_DAEMON=0
CONFIG_PARAM_INDEX=-1

print_help() {
  echo ""
  echo "Usage: start-file-server.sh [--config <conf-file-path>] [--daemon]"
  echo ""
  echo "    --config   -c    Specify the configuration file path."
  echo "    --daemon   -d    Run cloudhub-file-server in daemon mode, default is non-daemon mode."
  echo "                     Will not output log to the console if run in daemon mode."
  echo "    --help     -h    Print this help message."
  echo ""
}

index=1;
for i in "$@"; do
  case "$i" in
  --config|-c)
    HAS_CONFIG=1
    CONFIG_PARAM_INDEX=$((index+1))
    CONFIG_PARAM="${!CONFIG_PARAM_INDEX}"
    if [ "$CONFIG_PARAM" = "" ]; then
      echo "The parameter '--config' must be followed by a configuration file path."
      exit 1
    fi
    ;;
  --daemon|-d)
    HAS_DAEMON=1
    ;;
  --help|-h)
    HAS_HELP=1
    ;;
  *)
    if [ "$CONFIG_PARAM_INDEX" == -1 ] || [ "$index" != "$CONFIG_PARAM_INDEX" ]; then
      echo "Unknown parameter: $i"
      print_help
      exit 1
    fi
    ;;
  esac
  index=$((index+1))
done


if [ "$HAS_HELP" = 1 ]; then
  print_help
  exit 0
fi

echo "Configuration directory is in the $CONF_DIR. If you need changes settings, please follow direction to modify files in the folder."

printf "Starting cloudhub-file-server......\n"

prop() {
  grep "${1}" "$CONF_DIR"/cloudhub.conf | cut -d'=' -f2 | sed 's/\r//'
}

LOG_DIR=$(prop "cloudhub.file.log.path")

if [ "$HAS_DAEMON" = 1 ]; then
  if [ "$HAS_CONFIG" = 1 ]; then
    echo "Starting cloudhub-file-server with configuration file $CONFIG_PARAM"
    nohup "$JAVA_HOME"/bin/java -jar bin/cloudhub-file-server.jar --config "$CONFIG_PARAM" --daemon >/dev/null 2>&1 &
  else
    nohup "$JAVA_HOME"/bin/java -jar bin/cloudhub-file-server.jar --daemon >/dev/null 2>&1 &
  fi
  echo "Starting cloudhub-file-server......[OK]"
  echo "Log file is in the $LOG_DIR, you can use the command 'tail -f $LOG_DIR/cloudhub-file-server.out' to trace the log."
  exit 0
fi

exec "$JAVA_HOME"/bin/java -jar bin/cloudhub-file-server.jar
echo "Starting cloudhub-file-server......[OK]"

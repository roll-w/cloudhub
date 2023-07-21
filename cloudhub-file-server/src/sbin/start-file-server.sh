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

PARAM=$1

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
echo "Configuration directory is in the $CONF_DIR. If you need changes settings, please follow direction to modify files in the folder."

printf "Starting cloudhub-file-server......\n"

prop(){
    grep "${1}" "$CONF_DIR"/cloudhub.conf | cut -d'=' -f2 | sed 's/\r//'
}

LOG_DIR=$(prop "cloudhub.file.log.path")

if [ "$PARAM" = "-daemon" ]; then
  nohup "$JAVA_HOME"/bin/java -jar bin/cloudhub-file-server.jar --conf "$CONF_DIR" --daemon > /dev/null 2>&1 &
  echo "Starting cloudhub-file-server......[OK]"
  echo "Log file is in the $LOG_DIR, you can use the command 'tail -f $LOG_DIR/cloudhub-file-server.out' to trace the log."
  exit 0
elif [ "$PARAM" != "" ]; then
  echo ""
  echo "Starting cloudhub-file-server failed."
  echo "Unknown parameter $PARAM"
  echo "Usage: $0 [-daemon]"
  exit 1
fi

exec "$JAVA_HOME"/bin/java -jar bin/cloudhub-file-server.jar --conf "$CONF_DIR"
echo "Starting cloudhub-file-server......[OK]"

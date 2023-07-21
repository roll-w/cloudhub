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

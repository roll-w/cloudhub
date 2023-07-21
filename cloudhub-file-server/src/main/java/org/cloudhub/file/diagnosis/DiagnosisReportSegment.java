/*
 * Cloudhub - A high available, scalable distributed file system.
 * Copyright (C) 2022 Cloudhub
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.cloudhub.file.diagnosis;

/**
 * @author RollW
 */
public class DiagnosisReportSegment<D> {
    private final Type type;
    private final D data;

    public DiagnosisReportSegment(Type type,
                                  D data) {
        this.type = type;
        this.data = data;
    }

    public Type getDiagnosisReportType() {
        return type;
    }

    public D getData() {
        return data;
    }

    public enum Type {
        HEALTHY,
        /**
         * Partly damaged, still can use some functions.
         */
        DAMAGED,
        /**
         * Totally down.
         */
        DOWN;

        public Type plus(Type other) {
            if (other == DOWN|| this == DOWN) {
                return DOWN;
            }
            if (other == DAMAGED || this == DAMAGED) {
                return DAMAGED;
            }
            return HEALTHY;
        }
    }
}

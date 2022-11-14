package org.huel.cloudhub.file.diagnosis;

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

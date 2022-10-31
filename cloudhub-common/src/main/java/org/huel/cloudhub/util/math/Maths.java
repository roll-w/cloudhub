package org.huel.cloudhub.util.math;

/**
 * @author RollW
 */
public class Maths {

    public static int ceilDivide(int a, int b) {
        if (a == 0 || b < 1) {
            return (a + b - 1) / b;
        }
        return (a - 1) / b + 1;
    }

    public static long ceilDivide(long a, long b) {
        if (a == 0 || b < 1) {
            return (a + b - 1) / b;
        }
        return (a - 1) / b + 1;
    }

    public static int ceilDivideReturnsInt(long a, long b) {
        return (int) ceilDivide(a, b);
    }

    private Maths() {
    }
}

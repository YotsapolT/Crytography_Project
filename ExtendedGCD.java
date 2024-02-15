public class ExtendedGCD {

    public static void main(String[] args) {
        int a = 48;
        int b = 18;

        int[] result = extendedGCD(a, b);
        int gcd = result[0];
        int x = result[1];
        int y = result[2];

        System.out.println("GCD: " + gcd);
        System.out.println("x: " + x);
        System.out.println("y: " + y);
        System.out.println(Math.floorMod(-7, 5));
    }

    public static int[] extendedGCD(int a, int b) {
        if (a == 0) {
            return new int[] { b, 0, 1 };
        }

        int[] result = extendedGCD(b % a, a);
        int gcd = result[0];
        int x1 = result[1];
        int y1 = result[2];

        int inv_a = y1 - (b / a) * x1;
        int inv_b = x1;

        return new int[] { gcd, inv_a, inv_b };
    }
}

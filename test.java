import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

public class test {
    public static void main(String[] args) {
        System.out.println(Arrays.toString(GenRandomNowithInverse(148169422)));
    }

    public static boolean isPrime(int n) {
        if (LehmannTest(n)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean LehmannTest(int n) {
        if (n == 2 || n == 3) {
            return true;
        } else if ((n % 2) == 0 || n == 0 || n == 1){
            return false;
        }

        Random rand = new Random();
        int a = rand.nextInt(n - 3) + 2;

        int e = (n - 1) / 2;
        int round = 100;

        while (round > 0) {
            // System.out.println("a: " + a + " e: " + e + " n: " + n);
            int result = FastExpo(a, e, n);
            if ((result % n) == 1 || (result % n) == (n - 1)) {
                a = rand.nextInt(n - 3) + 2;
                round -= 1;
            } else {
                return false;
            }
        }
        return true;
    }

    public static int FastExpo(int a, int e, int n) {
        String bi_e = Integer.toBinaryString(e);
        int[] preCompute = new int[bi_e.length()];
        int result = 1;
        for (int i = preCompute.length - 1; i >= 0; i--) {
            if (i == preCompute.length - 1) {
                preCompute[i] = a % n;
            } else {
                preCompute[i] = (preCompute[i + 1] * preCompute[i + 1]) % n;
            }
        }
        System.out.println(Arrays.toString(preCompute));
        for (int i = 0; i < bi_e.length(); i++) {
            if (bi_e.charAt(i) == '1') {
                result *= preCompute[i];
                if (result > n) {
                    result = result % n;
                }
            }
        }
        return result;
    }

    public static boolean old_isPrime(int n) {

        if (n <= 1)
            return false;

        if (n == 2 || n == 3)
            return true;

        if (n % 2 == 0 || n % 3 == 0)
            return false;

        for (int i = 5; i <= Math.sqrt(n); i = i + 6)
            if (n % i == 0 || n % (i + 2) == 0)
                return false;

        return true;
    }

    public static int GCD(int a, int n) { // Euclidean's algorithm1
        while (n != 0) {
            int t = n;
            n = a % n;
            a = t;
        }
        return a;
    }

    public static int[] extendedGCD(int a, int b) { // Extended Euclidean's algorithm
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

    public static int FindInverse(int a, int n) {
        int[] result = extendedGCD(a, n);
        int inv_a = result[1];
        if (inv_a < 0) { // java '%' operator is Remainder operator. Remainder ⊆ I but Mod ⊆ I+
            inv_a = ((inv_a % n) + n) % n;
        }
        return inv_a;
    }

    public static int[] GenRandomNowithInverse(int n){
        SecureRandom random = new SecureRandom();
        int e;
        while(true){
            e = random.nextInt();
            if (GCD(e, n) == 1) {
                break;
            }
        }
        int inv_e = FindInverse(e, n);
        return new int[] {e, inv_e, n};
    }
}

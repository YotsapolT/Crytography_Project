import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class test {
    public static void main(String[] args) {
        System.out.println(LehmannTest(2));
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

}

import java.io.File;
import java.io.FileInputStream;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class project_noBigInt {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter number of bits: ");
        int n = in.nextInt();
        in.nextLine();
        System.out.print("Enter file path(don't forget to escape backslash): ");
        String filePath = in.nextLine();
        String ans = GenPrime(n, filePath);
        System.out.println("output bit: " + ans);
        System.out.println("to int: " + Long.parseLong(ans, 2));
        System.out.println("isPrime: " + isPrime(Long.parseLong(ans, 2)));
        in.close();

        System.out.println(Arrays.toString(GenRandomNowithInverse(Long.parseLong(ans, 2))));
    }

    public static String GenPrime(int n, String filename) {
        File file = new File(filename);
        byte[] fileData = new byte[(int) file.length()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileData);
            in.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        String content = "";
        for (byte b : fileData) {
            content += ByteToBits(b);
        }

        String biFromFile = "";
        if (n > content.length()) {
            return "The number of bits is more than binary input file.";
        } else {
            for (int i = 0; i < content.length(); i++) {
                if (content.charAt(i) == '1') { // start binary with '1'
                    biFromFile = content.substring(i, i + n);
                    break;
                }
            }
        }

        System.out.println("original bits: " + biFromFile);
        System.out.println("original decimal: " + Long.parseLong(biFromFile, 2));
        long decimal = Long.parseLong(biFromFile, 2);
        if (isPrime(decimal)) {
            System.out.println("original bits is Prime");
            return biFromFile;
        } else {
            while (!isPrime(decimal)) {
                if ((decimal % 2) == 0) {
                    if (decimal + 1 < FastExpo(2, n, Long.MAX_VALUE) && decimal + 1 > 0) {
                        decimal += 1;
                    } else {
                        System.out.println("reach maximum bits. There's no prime");
                        break;
                    }
                } else {
                    if (decimal + 2 < FastExpo(2, n, Long.MAX_VALUE) && decimal + 2 > 0) {
                        decimal += 2;
                    } else {
                        System.out.println("reach maximum bits, there's no prime");
                        break;
                    }
                }
            }
            biFromFile = Long.toBinaryString(decimal);
            System.out.println("original bits is not Prime");
            return biFromFile;
        }
    }

    public static String ByteToBits(byte b) {
        String result = "";
        for (int i = 7; i >= 0; i--) {
            result += (b & (1 << i)) == 0 ? "0" : "1";
        }
        // [i=7] 00110001 AND (00000001 << 7) -> 00110001 AND 00000000 -> 00000000 -> 0
        // [i=6] 00110001 AND (00000001 << 6) -> 00110001 AND 00000000 -> 00000000 -> 0
        // [i=5] 00110001 AND (00000001 << 5) -> 00110001 AND 00100000 -> 00100000 -> 1
        // [i=4] 00110001 AND (00000001 << 4) -> 00110001 AND 00010000 -> 00010000 -> 1
        // [i=3] 00110001 AND (00000001 << 3) -> 00110001 AND 00000000 -> 00000000 -> 0
        // [i=2] 00110001 AND (00000001 << 2) -> 00110001 AND 00000000 -> 00000000 -> 0
        // [i=1] 00110001 AND (00000001 << 1) -> 00110001 AND 00000000 -> 00000000 -> 0
        // [i=0] 00110001 AND (00000001 << 0) -> 00110001 AND 00000001 -> 00000001 -> 1
        // result = 00110001
        return result;
    }

    public static boolean isPrime(long n) {
        if (LehmannTest(n)) {
            return true;
        } else {
            return false;
        }
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

    public static boolean LehmannTest(long n) {
        if (n == 2 || n == 3) {
            return true;
        } else if ((n % 2) == 0 || n == 0 || n == 1) {
            return false;
        }

        Random rand = new Random();
        long e = (n - 1) / 2;
        for (int i = 0; i < 100; i++) {
            long a = rand.nextLong(n - 3) + 2;
            long result = FastExpo(a, e, n);
            if ((result % n) == 1 || (result % n) == (n - 1)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    public static long FastExpo(long a, long e, long n) {
        String bi_e = Long.toBinaryString(e);
        long[] preCompute = new long[bi_e.length()];
        long result = 1;
        for (int i = preCompute.length - 1; i >= 0; i--) {
            if (i == preCompute.length - 1) {
                preCompute[i] = a % n;
            } else {
                preCompute[i] = (preCompute[i + 1] * preCompute[i + 1]) % n;
            }
        }
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

    public static long GCD(long a, long n) { // Euclidean's algorithm
        while (n != 0) {
            long t = n;
            n = a % n;
            a = t;
        }
        return a;
    }

    public static long[] extendedGCD(long a, long b) { // Extended Euclidean's algorithm
        if (a == 0) {
            return new long[] { b, 0, 1 };
        }

        long[] result = extendedGCD(b % a, a);
        long gcd = result[0];
        long x1 = result[1];
        long y1 = result[2];

        long inv_a = y1 - (b / a) * x1;
        long inv_b = x1;

        return new long[] { gcd, inv_a, inv_b };
    }

    public static long FindInverse(long a, long n) {
        long[] result = extendedGCD(a, n);
        long inv_a = result[1];
        if (inv_a < 0) { // java '%' operator is Remainder operator. Remainder ⊆ I but Mod ⊆ I+
            inv_a = ((inv_a % n) + n) % n;
        }
        return inv_a;
    }

    public static long[] GenRandomNowithInverse(long n) {
        SecureRandom random = new SecureRandom();
        long e;
        while (true) {
            e = random.nextLong(n);
            if (GCD(e, n) == 1) {
                break;
            }
        }
        long inv_e = FindInverse(e, n);
        return new long[] { e, inv_e, n };
    }
}
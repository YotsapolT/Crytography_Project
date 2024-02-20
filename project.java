import java.io.File;
import java.io.FileInputStream;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Scanner;

public class project {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter number of bits: ");
        int n = in.nextInt();
        in.nextLine();
        System.out.print("Enter file path(don't forget to escape backslash): ");
        String filePath = in.nextLine();
        String ans = GenPrime(n, filePath);
        System.out.println("output bit: " + ans);
        System.out.println("to int: " + Integer.parseInt(ans, 2));
        System.out.println("isPrime: " + isPrime(Integer.parseInt(ans, 2)));
        in.close();
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
        int decimal = Integer.parseInt(biFromFile, 2);
        if (old_isPrime(decimal)) {
            System.out.println("original bits is Prime");
            return biFromFile;
        } else {
            while (!old_isPrime(decimal)) {
                if ((decimal % 2) == 0) {
                    if (decimal + 1 < Math.pow(2, n) && decimal + 1 > 0) {
                        decimal += 1;
                    } else {
                        System.out.println("reach maximum bits. There's no prime");
                        break;
                    }
                } else {
                    if (decimal + 2 < Math.pow(2, n) && decimal + 2 > 0) {
                        decimal += 2;
                        if (decimal % 5 == 0) {
                            decimal += 2;
                        }
                    } else {
                        System.out.println("reach maximum bits, there's no prime");
                        break;
                    }
                }
            }
            biFromFile = Integer.toBinaryString(decimal);
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

    public static boolean isPrime(int n) {
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
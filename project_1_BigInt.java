import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class project_1_BigInt {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter number of bits: ");
        int n = in.nextInt();
        in.nextLine();
        System.out.print("Enter file path(don't forget to escape backslash): ");
        String filePath = in.nextLine();
        String ans = GenPrime(n, filePath);
        System.out.println("output bit: " + ans);
        System.out.println("to int: " + new BigInteger(ans, 2));
        System.out.println("isPrime: " + isPrime(new BigInteger(ans, 2)));
        in.close();

        System.out.println(Arrays.toString(GenRandomNowithInverse(new BigInteger(ans, 2))));
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
        System.out.println("original decimal: " + new BigInteger(biFromFile, 2));
        BigInteger decimal = new BigInteger(biFromFile, 2);

        // calculate Upper bound
        String upperBound_bi = "";
        for (int i = 0; i < decimal.bitLength(); i++) {
            upperBound_bi += "1";
        }
        BigInteger upperBound = new BigInteger(upperBound_bi, 2);

        if (isPrime(decimal)) {
            System.out.println("original bits is Prime");
            return biFromFile;
        } else {
            while (!isPrime(decimal)) {
                if (decimal.mod(new BigInteger("2")).equals(new BigInteger("0"))) {
                    if ((decimal.add(new BigInteger("1"))).compareTo(upperBound.add(new BigInteger("1"))) == -1
                            && decimal.add(new BigInteger("1")).compareTo(new BigInteger("0")) == 1) {
                        decimal = decimal.add(new BigInteger("1"));
                    } else {
                        System.out.println("reach maximum bits. There's no prime");
                        break;
                    }
                } else {
                    if ((decimal.add(new BigInteger("2"))).compareTo(upperBound.add(new BigInteger("1"))) == -1
                            && decimal.add(new BigInteger("2")).compareTo(new BigInteger("0")) == 1) {
                        decimal = decimal.add(new BigInteger("2"));
                    } else {
                        System.out.println("reach maximum bits, there's no prime");
                        break;
                    }
                }
            }
            biFromFile = decimal.toString(2);
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

    public static boolean isPrime(BigInteger n) {
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

    public static boolean LehmannTest(BigInteger n) {
        if (n.equals(new BigInteger("2")) || n.equals(new BigInteger("3"))) {
            return true;
        } else if ((n.mod(new BigInteger("2"))).equals(new BigInteger("0"))
                || n.equals(new BigInteger("0")) || n.equals(new BigInteger("1"))) {
            return false;
        }

        Random rand = new Random();
        BigInteger e = (n.subtract(new BigInteger("1"))).divide(new BigInteger("2"));

        for (int i = 0; i < 100; i++) {
            BigInteger a = new BigInteger(n.bitLength(), rand);
            if (a.equals(new BigInteger("0")) || a.equals(new BigInteger("1")) || a.compareTo(n) != -1) {
                i--;
                continue;
            }
            BigInteger result = FastExpo(a, e, n);
            if ((result.mod(n)).equals(new BigInteger("1"))
                    || (result.mod(n)).equals((n.subtract(new BigInteger("1"))))) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    public static BigInteger FastExpo(BigInteger a, BigInteger e, BigInteger n) {
        String bi_e = e.toString(2);
        BigInteger[] preCompute = new BigInteger[bi_e.length()];
        BigInteger result = new BigInteger("1");
        for (int i = preCompute.length - 1; i >= 0; i--) {
            if (i == preCompute.length - 1) {
                preCompute[i] = a.mod(n);
            } else {
                preCompute[i] = (preCompute[i + 1].multiply(preCompute[i + 1])).mod(n);
            }
        }
        for (int i = 0; i < bi_e.length(); i++) {
            if (bi_e.charAt(i) == '1') {
                result = result.multiply(preCompute[i]);
                if (result.compareTo(n) == 1) {
                    result = result.mod(n);
                }
            }
        }
        return result;
    }

    public static BigInteger GCD(BigInteger a, BigInteger n) { // Euclidean's algorithm
        while (!n.equals(new BigInteger("0"))) {
            BigInteger t = n;
            n = a.mod(n);
            a = t;
        }
        return a;
    }

    public static BigInteger[] extendedGCD(BigInteger a, BigInteger b) { // Extended Euclidean's algorithm
        if (a.equals(new BigInteger("0"))) {
            return new BigInteger[] { b, new BigInteger("0"), new BigInteger("1") };
        }

        BigInteger[] result = extendedGCD(b.mod(a), a);
        BigInteger gcd = result[0];
        BigInteger x1 = result[1];
        BigInteger y1 = result[2];

        BigInteger inv_a = y1.subtract(((b.divide(a))).multiply(x1));
        BigInteger inv_b = x1;

        return new BigInteger[] { gcd, inv_a, inv_b };
    }

    public static BigInteger FindInverse(BigInteger a, BigInteger n) {
        BigInteger[] result = extendedGCD(a, n);
        BigInteger inv_a = result[1];
        if (inv_a.compareTo(new BigInteger("0")) == -1) { // java '%' operator is Remainder operator ⊆ I but Mod ⊆ I+
            inv_a = ((inv_a.mod(n)).add(n)).mod(n);
        }
        return inv_a;
    }

    public static BigInteger[] GenRandomNowithInverse(BigInteger n) {
        SecureRandom random = new SecureRandom();
        BigInteger e;
        while (true) {
            e = new BigInteger(n.bitLength(), random);
            if (GCD(e, n).equals(new BigInteger("1"))) {
                break;
            }
        }
        BigInteger inv_e = FindInverse(e, n);
        return new BigInteger[] { e, inv_e, n };
    }
}
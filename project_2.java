import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class project_2 {

    public static void main(String[] args) {
        BigInteger p = new BigInteger(project_1_BigInt.GenPrime(10, "text.txt"), 2);
        HashMap<String, BigInteger> elgamalKey = ElgamalKeyGen(p);
        System.out.println(elgamalKey);
        byte[] ciphertext = ElgamalEncrypt(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"),
                "a".getBytes()[0]);
        System.out.println("ciphertext: " + Arrays.toString(ciphertext));
        byte[] plaintext = ElgamalDecrypt(elgamalKey.get("p"), elgamalKey.get("u"), ciphertext);
        System.out.println("plaintext: " + (char) plaintext[0]);
    }

    public static BigInteger genGenerator(BigInteger p) {
        BigInteger g = new BigInteger("0");
        SecureRandom rand = new SecureRandom();
        while (true) {
            g = new BigInteger(p.bitLength(), rand);
            if (g.compareTo(new BigInteger("1")) == 1 && g.compareTo(p) == -1
                    && !g.mod(p).equals(p.subtract(new BigInteger("1")))) {
                BigInteger p1 = new BigInteger("0");
                p1 = p.subtract(new BigInteger("1")).divide(new BigInteger("2"));
                if (project_1_BigInt.isPrime(p1)) {
                    BigInteger primeEleTest = project_1_BigInt.FastExpo(g,
                            p.subtract(new BigInteger("1")).divide(new BigInteger("2")), p);
                    if (!primeEleTest.equals(new BigInteger("1"))) {
                        return g;
                    } else {
                        g = g.negate();
                        g = ((g.mod(p)).add(p)).mod(p);
                        return g;
                    }
                } else {
                    BigInteger tmp = new BigInteger("2");
                    LinkedList<BigInteger> modList = new LinkedList<BigInteger>();
                    while (tmp.compareTo(p) == -1) {
                        BigInteger mod = project_1_BigInt.FastExpo(g, tmp, p);
                        if (modList.contains(mod)) {
                            tmp = new BigInteger("-1");
                            break;
                        } else {
                            modList.add(mod);
                        }
                        tmp = tmp.add(new BigInteger("1"));
                    }
                    if (tmp.equals(new BigInteger("-1"))) {
                        continue;
                    } else {
                        return g;
                    }
                }
            } else {
                continue;
            }
        }
    }

    public static HashMap<String, BigInteger> ElgamalKeyGen(BigInteger p) {
        HashMap<String, BigInteger> ElgamalKey = new HashMap<String, BigInteger>();
        BigInteger g = genGenerator(p);
        SecureRandom rand = new SecureRandom();
        BigInteger u;
        while (true) {
            u = new BigInteger(p.bitLength(), rand);
            if (u.compareTo(new BigInteger("1")) == 1 && u.compareTo(p) == -1) {
                break;
            }
        }
        BigInteger y = project_1_BigInt.FastExpo(g, u, p);
        ElgamalKey.put("p", p);
        ElgamalKey.put("g", g);
        ElgamalKey.put("y", y);
        ElgamalKey.put("u", u);
        return ElgamalKey;
    }

    public static byte[] ElgamalEncrypt(BigInteger p, BigInteger g, BigInteger y, byte singleByte) {
        SecureRandom rand = new SecureRandom();
        byte[] ciphertext = new byte[p.toByteArray().length * 2];
        byte[] cipher_a = new byte[p.toByteArray().length];
        byte[] cipher_b = new byte[p.toByteArray().length];
        while (true) {
            BigInteger k = new BigInteger(p.bitLength(), rand);
            BigInteger a;
            BigInteger b;
            if (k.compareTo(new BigInteger("1")) == 1 && k.compareTo(p) == -1
                    && project_1_BigInt.GCD(k, p.subtract(new BigInteger("1"))).equals(new BigInteger("1"))) {
                a = project_1_BigInt.FastExpo(g, k, p);

                String str_singleByte = String.valueOf(singleByte);
                BigInteger X = new BigInteger(str_singleByte);
                b = project_1_BigInt.FastExpo(y, k, p).multiply((X.mod(p))).mod(p);

                byte[] cipher_a_nopad = a.toByteArray();
                byte[] cipher_b_nopad = b.toByteArray();
                for (int i = 0; i < cipher_a_nopad.length; i++) {
                    cipher_a[(cipher_a.length - 1) - i] = cipher_a_nopad[(cipher_a_nopad.length - 1) - i];
                }
                for (int i = 0; i < cipher_b_nopad.length; i++) {
                    cipher_b[(cipher_b.length - 1) - i] = cipher_b_nopad[(cipher_b_nopad.length - 1) - i];
                }
                for (int i = 0; i < ciphertext.length / 2; i++) {
                    ciphertext[i] = cipher_a[i];
                    ciphertext[(ciphertext.length / 2) + i] = cipher_b[i];
                }
                break;
            }
        }
        return ciphertext;
    }

    public static byte[] ElgamalDecrypt(BigInteger p, BigInteger u, byte[] ciphertext) {
        byte[] plaintext;
        byte[] cipher_a = new byte[p.toByteArray().length];
        byte[] cipher_b = new byte[p.toByteArray().length];

        for (int i = 0; i < ciphertext.length / 2; i++) {
            cipher_a[i] = ciphertext[i];
            cipher_b[i] = ciphertext[(ciphertext.length / 2) + i];
        }

        BigInteger a = new BigInteger(cipher_a);
        BigInteger b = new BigInteger(cipher_b);
        BigInteger a_dec = project_1_BigInt.FastExpo(a, p.subtract(new BigInteger("1")).subtract(u), p);
        BigInteger dec = b.multiply(a_dec).mod(p);
        plaintext = dec.toByteArray();

        return plaintext;
    }
}

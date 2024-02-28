import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.LinkedList;

public class project_2 {

    public static void main(String[] args) {
        BigInteger p = new BigInteger(project_1_BigInt.GenPrime(10, "text.txt"), 2);
        HashMap<String, BigInteger> elgamalKey = ElgamalKeyGen(p);
        System.out.println(elgamalKey);
        String ciphertext = ElgamalEncrypt(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"), "abcdef");
        System.out.println("ciphertext(hex): " + ciphertext);
        String plaintext = ElgamalDecrypt(elgamalKey.get("p"), elgamalKey.get("u"), ciphertext);
        System.out.println("plaintext: " + plaintext);
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

    public static String ElgamalEncrypt(BigInteger p, BigInteger g, BigInteger y, String plaintext) {
        char[] char_arr_p = plaintext.toCharArray();
        SecureRandom rand = new SecureRandom();
        String ciphertext = "";
        for (int i = 0; i < char_arr_p.length; i++) {
            String c_a = "";
            String c_b = "";
            while (true) {
                BigInteger k = new BigInteger(p.bitLength(), rand);
                BigInteger a;
                BigInteger b;
                if (k.compareTo(new BigInteger("1")) == 1 && k.compareTo(p) == -1
                        && project_1_BigInt.GCD(k, p.subtract(new BigInteger("1"))).equals(new BigInteger("1"))) {
                    a = project_1_BigInt.FastExpo(g, k, p);
                    String char_p = String.valueOf((int) char_arr_p[i]);
                    b = project_1_BigInt.FastExpo(y, k, p).multiply((new BigInteger(char_p).mod(p))).mod(p);

                    c_a = a.toString(16);
                    c_b = b.toString(16);

                    int maxHex = 0;
                    if (p.bitLength() % 4 != 0) {
                        maxHex = (p.bitLength() / 4) + 1;
                    } else {
                        maxHex = p.bitLength() / 4;
                    }

                    if (c_a.length() != maxHex) {
                        String zeros = "";
                        for (int x = 0; x < maxHex - c_a.length(); x++) {
                            zeros += "0";
                        }
                        c_a = zeros + c_a;
                    }

                    if (c_b.length() != maxHex) {
                        String zeros = "";
                        for (int x = 0; x < maxHex - c_b.length(); x++) {
                            zeros += "0";
                        }
                        c_b = zeros + c_b;
                    }
                    break;
                } else {
                    continue;
                }
            }
            ciphertext += (c_a + c_b);
        }
        return ciphertext;
    }

    public static String ElgamalDecrypt(BigInteger p, BigInteger u, String ciphertext) {
        String plaintext = "";

        int maxHex = 0;
        if (p.bitLength() % 4 != 0) {
            maxHex = (p.bitLength() / 4) + 1;
        } else {
            maxHex = p.bitLength() / 4;
        }

        BigInteger[] arr_c = new BigInteger[ciphertext.length() / maxHex];
        for (int i = 0; i < arr_c.length; i++) {
            String c_maxHexSize = ciphertext.substring(i * maxHex, (i + 1) * maxHex);
            arr_c[i] = new BigInteger(c_maxHexSize, 16);
        }

        for (int i = 0; i < arr_c.length / 2; i++) {
            BigInteger b = arr_c[(i * 2) + 1];
            BigInteger a = arr_c[(i * 2)];
            BigInteger a_dec = project_1_BigInt.FastExpo(a, p.subtract(new BigInteger("1")).subtract(u), p);
            BigInteger dec = b.multiply(a_dec).mod(p);
            plaintext += (char) dec.intValue();
        }

        return plaintext;
    }
}

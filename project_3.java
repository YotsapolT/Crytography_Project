import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.security.SecureRandom;

public class project_3 {
    public static void main(String[] args) throws Exception{
        BigInteger p = new BigInteger(project_1_BigInt.GenSafePrime(5, "taylor.txt"), 2);
        System.out.println("Prime: " + p + "[" + p.toString(2).length() + " bits]");
        HashMap<String, BigInteger> elgamalKey = project_2.ElgamalKeyGen(p);
        System.out.println(elgamalKey);

        byte[] plainText = new byte[] { Byte.parseByte("1") };
        byte[] sign = ElgamalSignature(elgamalKey.get("p"), elgamalKey.get("u"), elgamalKey.get("g"), plainText);
        System.out.println(Arrays.toString(sign));
        boolean isVerify = ElgamalVerification(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"), plainText, sign);
        System.out.println(isVerify);
    }

    public static byte[] ElgamalSignature(BigInteger p, BigInteger u, BigInteger g, byte[] plaintext){
        SecureRandom rand = new SecureRandom();
        byte[] sign = new byte[p.toByteArray().length * 2];
        byte[] cipher_r = new byte[p.toByteArray().length];
        byte[] cipher_s = new byte[p.toByteArray().length];
        while (true) {
            BigInteger k = new BigInteger(p.bitLength(), rand);
            BigInteger r;
            BigInteger s;
            if (k.compareTo(new BigInteger("1")) == 1 && k.compareTo(p) == -1 && project_1_BigInt.GCD(k, p.subtract(new BigInteger("1"))).equals(new BigInteger("1"))) {
                r = project_1_BigInt.FastExpo(g, k, p);

                BigInteger k_inv = project_1_BigInt.FindInverse(k, p.subtract(new BigInteger("1")));
                BigInteger X = new BigInteger(plaintext);
                s = k_inv.multiply(X.subtract(u.multiply(r))).mod(p.subtract(new BigInteger("1")));

                byte[] cipher_r_nopad = r.toByteArray();
                byte[] cipher_s_nopad = s.toByteArray();
                for (int i = 0; i < cipher_r_nopad.length; i++) {
                    cipher_r[(cipher_r.length - 1) - i] = cipher_r_nopad[(cipher_r_nopad.length - 1) - i];
                }
                for (int i = 0; i < cipher_s_nopad.length; i++) {
                    cipher_s[(cipher_s.length - 1) - i] = cipher_s_nopad[(cipher_s_nopad.length - 1) - i];
                }
                for (int i = 0; i < sign.length / 2; i++) {
                    sign[i] = cipher_r[i];
                    sign[(sign.length / 2) + i] = cipher_s[i];
                }
                break;
            }
        }
        System.out.println("plain text and signed text: " + Arrays.deepToString(new byte[][] { plaintext , sign } ));
        return sign;
    }

    public static boolean ElgamalVerification(BigInteger p, BigInteger g, BigInteger y, byte[] plainText, byte[] signedText) throws Exception{
        if (signedText.length < p.toByteArray().length * 2){
            byte[] newSignedText = new byte[p.toByteArray().length * 2];
            for (int i = 0; i < signedText.length; i++){
                newSignedText[(newSignedText.length - 1) - i] = signedText[(signedText.length - 1) - i];
            }
            signedText = newSignedText;
        }
        BigInteger r = new BigInteger(Arrays.copyOfRange(signedText, 0, p.toByteArray().length));
        BigInteger s = new BigInteger(Arrays.copyOfRange(signedText, p.toByteArray().length, 2 * p.toByteArray().length));
        if(r.compareTo(p) == 1 || s.compareTo(p) == 1){
            throw new Exception("Cipher text is bigger than prime");
        }
        
        BigInteger X = new BigInteger(plainText);
        BigInteger msg = project_1_BigInt.FastExpo(g, X, p);
        BigInteger signedMSG = project_1_BigInt.FastExpo(y, r, p).multiply(project_1_BigInt.FastExpo(r, s, p)).mod(p);
        if(msg.compareTo(signedMSG) == 0){
            System.out.println("compare g^X and (y^r)(r^s): " + msg + " == " + signedMSG);
            return true;
        }else{
            System.out.println("compare g^X and (y^r)(r^s): " + msg + " != " + signedMSG);
            return false;
        }
    }

    public static byte[] RWHash(BigInteger p, byte[] plainText){
        int size = p.bitLength();
        BigInteger[] chunk = new BigInteger[5];
        byte[] hashedText = new byte[p.toByteArray().length];
        return hashedText;
    }
}

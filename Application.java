import java.math.BigInteger;
import java.util.HashMap;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int numBits = in.nextInt();
        String fileName = in.nextLine();

        BigInteger p = genSafePrime(numBits, fileName);
        System.out.println("Prime: " + p + "[" + p.toString(2).length() + " bits]");

        HashMap<String, BigInteger> elgamalKey = ElgamalKeyGenerate(p);
        System.out.println(elgamalKey);
        savePublicKey(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"), "1");
        project_2.savePrivateKey(elgamalKey.get("p"), elgamalKey.get("u"), "1");
    }

    public static BigInteger genSafePrime(int numBits, String fileName){
        BigInteger p = new BigInteger(project_1_BigInt.GenSafePrime(numBits, fileName), 2);
        return p;
    }

    public static HashMap<String, BigInteger> ElgamalKeyGenerate(BigInteger p){
        HashMap<String, BigInteger> elgamalKey = project_2.ElgamalKeyGen(p);
        return elgamalKey;
    }

    public static void savePublicKey(BigInteger p, BigInteger g, BigInteger y, String pkName){
        project_2.savePublicKey(p, g, y, pkName);
    }

    public static void savePrivateKey(BigInteger p, BigInteger u, String pkName){
        project_2.savePrivateKey(p, u, pkName);
    }
}

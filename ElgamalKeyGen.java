import java.math.BigInteger;
import java.util.HashMap;
import java.util.Scanner;

public class ElgamalKeyGen {
    static Scanner in = new Scanner(System.in);
    public static void main(String[] args) throws Exception{
        BigInteger p = genSafePrime();
        HashMap<String, BigInteger> elgamalKey = ElgamalKeyGenerate(p);
        savePublicKey(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"));
        savePrivateKey(elgamalKey.get("p"), elgamalKey.get("u"), elgamalKey.get("g"));
        in.close();
    }

    public static BigInteger genSafePrime(){
        System.out.print("Enter number of bit to generate safe prime number: ");
        int numBits = in.nextInt();
        System.out.print("Enter file path to generate safe prime number: ");
        in.nextLine();
        String fileName = in.nextLine();
        BigInteger p = new BigInteger(project_1_BigInt.GenSafePrime(numBits, fileName), 2);
        System.out.println("Prime: " + p + "[" + p.toString(2).length() + " bits:" + p.toByteArray().length +" bytes]");
        return p;
    }

    public static HashMap<String, BigInteger> ElgamalKeyGenerate(BigInteger p){
        System.out.println("Starting generate key...");
        HashMap<String, BigInteger> elgamalKey = project_2.ElgamalKeyGen(p);
        System.out.println("Successfully generated key!");
        System.out.println(elgamalKey);
        return elgamalKey;
    }

    public static void savePublicKey(BigInteger p, BigInteger g, BigInteger y){
        System.out.print("Enter file name to save public key: ");
        String pkFileName = in.nextLine();
        project_2.savePublicKey(p, g, y, pkFileName);
        System.out.println("Successfully saved public key to " + "pk_" + pkFileName + ".txt");
    }

    public static void savePrivateKey(BigInteger p, BigInteger u, BigInteger g){
        System.out.print("Enter file name to save private key: ");
        String skFileName = in.nextLine();
        project_2.savePrivateKey(p, u, g, skFileName);
        System.out.println("Successfully saved public key to " + "sk_" + skFileName + ".txt");
    }
}

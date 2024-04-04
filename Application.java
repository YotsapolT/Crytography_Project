import java.math.BigInteger;
import java.util.HashMap;
import java.util.Scanner;

public class Application {
    static Scanner in = new Scanner(System.in);
    public static void main(String[] args) throws Exception{
        System.out.println("Choose Elgamal algorithm!");
        System.out.println("1. Generate Key");
        System.out.println("2. Encryption");
        System.out.println("3. Decryption");
        System.out.println("4. Signature");
        System.out.println("5. Verification");
        System.out.print("Enter a number to choose: ");
        int algorithm = in.nextInt();

        switch (algorithm) {
            case 1:
                ElgamalGenerateKey();
                break;
            case 2:
                ElgamalEncryptFile();
                break;
            case 3:
                ElgamalDecryptFile();
                break;
            case 4:
                ElgamalSignFile();
                break;
            case 5:
                ElgamalVerifyFile();
                break;
            default:
                System.out.println("Please enter a number between 1-5");
                break;
        }
        in.close();
    }

    public static void ElgamalGenerateKey() {
        in.nextLine();
        BigInteger p = genSafePrime();
        HashMap<String, BigInteger> elgamalKey = ElgamalKeyGenerate(p);
        savePublicKey(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"));
        savePrivateKey(elgamalKey.get("p"), elgamalKey.get("u"), elgamalKey.get("g"));
    }

    public static BigInteger genSafePrime() {
        System.out.print("Enter number of bit to generate safe prime number: ");
        int numBits = in.nextInt();
        System.out.print("Enter file path to generate safe prime number: ");
        in.nextLine();
        String fileName = in.nextLine();
        BigInteger p = new BigInteger(project_1_BigInt.GenSafePrime(numBits, fileName), 2);
        System.out.println("Prime: " + p + "[" + p.toString(2).length() + " bits:" + p.toByteArray().length +" bytes]");
        return p;
    }

    public static HashMap<String, BigInteger> ElgamalKeyGenerate(BigInteger p) {
        System.out.println("Starting generate key...");
        HashMap<String, BigInteger> elgamalKey = project_2.ElgamalKeyGen(p);
        System.out.println("Successfully generated key!");
        System.out.println(elgamalKey);
        return elgamalKey;
    }

    public static void savePublicKey(BigInteger p, BigInteger g, BigInteger y) {
        System.out.print("Enter file name to save public key: ");
        String pkFileName = in.nextLine();
        project_2.savePublicKey(p, g, y, pkFileName);
        System.out.println("Successfully saved public key to " + "pk_" + pkFileName + ".txt");
    }

    public static void savePrivateKey(BigInteger p, BigInteger u, BigInteger g) {
        System.out.print("Enter file name to save private key: ");
        String skFileName = in.nextLine();
        project_2.savePrivateKey(p, u, g, skFileName);
        System.out.println("Successfully saved public key to " + "sk_" + skFileName + ".txt");
    }

    public static HashMap<String, BigInteger> readPublicKey(String filePath) {
        HashMap<String, BigInteger> ElgamalPublicKey = project_2.readPublicKey(filePath);
        return ElgamalPublicKey;
    }

    public static HashMap<String, BigInteger> readPrivateKey(String filePath) {
        HashMap<String, BigInteger> ElgamalPrivateKey = project_2.readPrivateKey(filePath);
        return ElgamalPrivateKey;
    }

    public static void ElgamalEncryptFile() {
        in.nextLine();
        System.out.print("Enter file path you want to encrypt: ");
        String filePath = in.nextLine();
        System.out.print("Enter public key's receiver file path: ");
        String pkFilePath = in.nextLine();
        HashMap<String, BigInteger> PublicKey = readPublicKey(pkFilePath);
        project_2.ElgamalEncryptFile(PublicKey.get("p"), PublicKey.get("g"), PublicKey.get("y"), filePath);
    }

    public static void ElgamalDecryptFile() {
        in.nextLine();
        System.out.print("Enter file path you want to decrypt: ");
        String filePath = in.nextLine();
        System.out.print("Enter private key's receiver file path: ");
        String skFilePath = in.nextLine();
        HashMap<String, BigInteger> PrivateKey = readPrivateKey(skFilePath);
        project_2.ElgamalDecryptFile(PrivateKey.get("p"), PrivateKey.get("u"), filePath);
    }
    
    public static void ElgamalSignFile() {
        in.nextLine();
        System.out.print("Enter file path you want to sign: ");
        String filePath = in.nextLine();
        System.out.print("Enter private key's sender file path: ");
        String skFilePath = in.nextLine();
        HashMap<String, BigInteger> PrivateKey = readPrivateKey(skFilePath);
        project_3.ElgamalSignFile(PrivateKey.get("p"), PrivateKey.get("u"), PrivateKey.get("g"), filePath);
    }

    public static void ElgamalVerifyFile() {
        in.nextLine();
        System.out.print("Enter file path: ");
        String filePath = in.nextLine();
        System.out.print("Enter signature's file path of file path you want to verify: ");
        String signedFilePath = in.nextLine();
        System.out.print("Enter public key's sender file path: ");
        String pkFilePath = in.nextLine();
        HashMap<String, BigInteger> PublicKey = readPublicKey(pkFilePath);
        project_3.ElgamalVerifyFile(PublicKey.get("p"), PublicKey.get("g"), PublicKey.get("y"), filePath, signedFilePath);
    }

}

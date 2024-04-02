import java.math.BigInteger;
import java.util.HashMap;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) throws Exception{
        Scanner in = new Scanner(System.in);
        System.out.println("");
        int numBits = in.nextInt();
        String fileName = in.nextLine();

        BigInteger p = genSafePrime(numBits, fileName);
        System.out.println("Prime: " + p + "[" + p.toString(2).length() + " bits]");

        HashMap<String, BigInteger> elgamalKey = ElgamalKeyGenerate(p);
        System.out.println(elgamalKey);
        savePublicKey(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"), "1");
        savePrivateKey(elgamalKey.get("p"), elgamalKey.get("u"), "1");

        
        System.out.print("1. text scanner, 2. file(all type), 3. image (with bitmap): ");
        int encryptType = in.nextInt();
        switch (encryptType) {
            case 1:
                System.out.print("Enter message: ");
                in.nextLine();
                project_2.ElgamalEncryptScanner(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"));
                break;
            case 2:
                System.out.print("Enter file path you want to Encrypt: ");
                in.nextLine();
                String encFileName = in.nextLine();
                project_2.ElgamalEncryptFile(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"), encFileName);
                break;
            case 3:
                System.out.print("Enter image file path(.png, .jpg, .jpeg): ");
                in.nextLine();
                String encImageFileName = in.nextLine();
                project_2.ElgamalEncryptImageFile(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"), encImageFileName);
            default:
                System.out.println("please enter a number between 1-3!");
                break;
        }
        in.close();
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

    public static HashMap<String, BigInteger> readPublicKey(String filePath){
        HashMap<String, BigInteger> ElgamalPublicKey = project_2.readPublicKey(filePath);
        return ElgamalPublicKey;
    }

    public static HashMap<String, BigInteger> readPrivate(String filePath){
        HashMap<String, BigInteger> ElgamalPrivateKey = project_2.readPrivateKey(filePath);
        return ElgamalPrivateKey;
    }
}

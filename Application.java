import java.math.BigInteger;
import java.util.HashMap;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) throws Exception{
        Scanner in = new Scanner(System.in);
        // System.out.print("Enter number of bit to generate safe prime number: ");
        // int numBits = in.nextInt();
        // System.out.print("Enter file path to generate safe prime number: ");
        // in.nextLine();
        // String fileName = in.nextLine();

        // BigInteger p = genSafePrime(numBits, fileName);
        // System.out.println("Prime: " + p + "[" + p.toString(2).length() + " bits]");

        // System.out.println("Starting generate key...");
        // HashMap<String, BigInteger> elgamalKey = ElgamalKeyGenerate(p);
        // System.out.println("Successfully generated key.");
        // System.out.println(elgamalKey);

        // System.out.print("Enter file name to save public key: ");
        // String pkFileName = in.nextLine();
        // savePublicKey(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"), pkFileName);
        // System.out.println("Successfully saved public key to " + "pk_" + pkFileName + ".txt");

        // System.out.print("Enter file name to save private key: ");
        // String skFileName = in.nextLine();
        // savePrivateKey(elgamalKey.get("p"), elgamalKey.get("u"), skFileName);
        // System.out.println("Successfully saved public key to " + "sk_" + skFileName + ".txt");
         
        System.out.print("Enter file name to read private key: ");
        String skFileName = in.nextLine();
        HashMap<String, BigInteger> ElgamalPrivateKey = readPrivateKey(skFileName);
        System.out.println("Successfully read private key: " + ElgamalPrivateKey);

        System.out.println("choose method to decrypt.");
        System.out.print("1. File(all type), 2. Image file (with bitmap): ");
        int encryptType = in.nextInt();
        switch (encryptType) {
            case 1:
                System.out.print("Enter file path to decrypt: ");
                in.nextLine();
                String decFilePath = in.nextLine();
                project_2.ElgamalDecryptFile(ElgamalPrivateKey.get("p"), ElgamalPrivateKey.get("u"), decFilePath);
                break;
            case 2:
                System.out.print("Enter image file path to decrypt(.png, .jpg, .jpeg): ");
                in.nextLine();
                String decImageFilePath = in.nextLine();
                project_2.ElgamalDecryptImageFile(ElgamalPrivateKey.get("p"), ElgamalPrivateKey.get("u"), decImageFilePath);
                break;
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

    public static HashMap<String, BigInteger> readPrivateKey(String filePath){
        HashMap<String, BigInteger> ElgamalPrivateKey = project_2.readPrivateKey(filePath);
        return ElgamalPrivateKey;
    }

}

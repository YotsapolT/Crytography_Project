import java.math.BigInteger;
import java.util.HashMap;
import java.util.Scanner;

public class Application {
    static Scanner in = new Scanner(System.in);
    public static void main(String[] args) throws Exception{
        System.out.println("Choose Elgamal algorithm!");
        System.out.println("1. Encryption");
        System.out.println("2. Decryption");
        System.out.println("3. Signature");
        System.out.println("4. Verification");
        System.out.print("Enter a number to choose: ");
        int algorithm = in.nextInt();

        switch (algorithm) {
            case 1:
                ElgamalEncryptFile();
                break;
            case 2:
                ElgamalDecryptFile();
                break;
            case 3:
                ElgamalSignFile();
                break;
            case 4:
                ElgamalVerifyFile();
                break;
            default:
                System.out.println("Please enter a number between 1-4");
                break;
        }
        in.close();
    }

    public static HashMap<String, BigInteger> readPublicKey(String filePath){
        HashMap<String, BigInteger> ElgamalPublicKey = project_2.readPublicKey(filePath);
        return ElgamalPublicKey;
    }

    public static HashMap<String, BigInteger> readPrivateKey(String filePath){
        HashMap<String, BigInteger> ElgamalPrivateKey = project_2.readPrivateKey(filePath);
        return ElgamalPrivateKey;
    }

    public static void ElgamalEncryptFile() throws Exception{
        in.nextLine();
        System.out.print("Enter file path you want to encrypt: ");
        String filePath = in.nextLine();
        System.out.print("Enter public key's receiver file path: ");
        String pkFilePath = in.nextLine();
        HashMap<String, BigInteger> PublicKey = readPublicKey(pkFilePath);
        project_2.ElgamalEncryptFile(PublicKey.get("p"), PublicKey.get("g"), PublicKey.get("y"), filePath);
    }

    public static void ElgamalDecryptFile() throws Exception{
        in.nextLine();
        System.out.print("Enter file path you want to decrypt: ");
        String filePath = in.nextLine();
        System.out.print("Enter private key's receiver file path: ");
        String skFilePath = in.nextLine();
        HashMap<String, BigInteger> PrivateKey = readPrivateKey(skFilePath);
        project_2.ElgamalDecryptFile(PrivateKey.get("p"), PrivateKey.get("u"), filePath);
    }
    
    public static void ElgamalSignFile(){
        System.out.println("Coming soon!");
    }

    public static void ElgamalVerifyFile(){
        System.out.println("Coming soon!");
    }

}

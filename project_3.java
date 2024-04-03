import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.security.SecureRandom;

public class project_3 {
    public static void main(String[] args) throws Exception{
        BigInteger p = new BigInteger(project_1_BigInt.GenSafePrime(128, "test_file\\taylor.txt"), 2);
        System.out.println("Prime: " + p + "[" + p.toString(2).length() + " bits]");
        HashMap<String, BigInteger> elgamalKey = project_2.ElgamalKeyGen(p);
        // System.out.println(elgamalKey);

        // byte[] plainText = new byte[] { Byte.parseByte("1") };
        // byte[] sign = ElgamalSignature(elgamalKey.get("p"), elgamalKey.get("u"), elgamalKey.get("g"), plainText);
        // System.out.println(Arrays.toString(sign));
        // boolean isVerify = ElgamalVerification(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"), plainText, sign);
        // System.out.println(isVerify);

        // byte[] hashedText = RWHash(p, "test_file\\text.txt");
        // System.out.println(Arrays.toString(hashedText));

        ElgamalSignFile(elgamalKey.get("p"), elgamalKey.get("u"), elgamalKey.get("g"), "test_file\\text.txt");
        ElgamalVerifyFile(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"), "test_file\\text.txt", "signature_text.txt");
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
            // System.out.println("compare g^X and (y^r)(r^s): " + msg + " == " + signedMSG);
            return true;
        }else{
            // System.out.println("compare g^X and (y^r)(r^s): " + msg + " != " + signedMSG);
            return false;
        }
    }

    public static byte[] RWHash(BigInteger p, String filePath){
        int size = p.bitLength();
        int totalBits = 5 * size * 4;
        BigInteger[] hashBlock = new BigInteger[5];
        try{
            File file = new File(filePath);
            FileInputStream in = new FileInputStream(file);
            byte[] fileData = new byte[(int) file.length()];
            in.read(fileData);

            LinkedList<Character> fileDataBinList = new LinkedList<Character>();

            for (int i = 0; i < fileData.length; i++) {
                char[] fileDataBinChr = project_1_BigInt.ByteToBits(fileData[i]).toCharArray();

                for (int j = 0; j < fileDataBinChr.length; j++) {
                    if (fileDataBinList.size() <= totalBits){
                        fileDataBinList.add((Character) fileDataBinChr[j]);
                    }else{
                        i = fileData.length;
                        break;
                    }
                }
            }

            if (fileDataBinList.size() < totalBits){
                for (int i = fileDataBinList.size(); i < totalBits; i++) {
                    fileDataBinList.add('1');
                }
            }

            char[] fileDataBinArr = new char[fileDataBinList.size()];
            for (int i = 0; i < fileDataBinList.size(); i++) {
                fileDataBinArr[i] = fileDataBinList.get(i);
            }
            for (int i = 0; i < hashBlock.length; i++) {
                if (i == 0){
                    hashBlock[i] = new BigInteger(String.valueOf(fileData.length * 8));
                }else {
                    String C1_str = String.valueOf(fileDataBinArr, ((i - 1) * 5 * size) + 0, size);
                    String C2_str = String.valueOf(fileDataBinArr, ((i - 1) * 5 * size) + size, size);
                    String C3_str = String.valueOf(fileDataBinArr, ((i - 1) * 5 * size) + (2 * size), size);
                    String C4_str = String.valueOf(fileDataBinArr, ((i - 1) * 5 * size) + (3 * size), size);
                    String C5_str = String.valueOf(fileDataBinArr, ((i - 1) * 5 * size) + (4 * size), size);
    
                    BigInteger C1 = new BigInteger(C1_str, 2);
                    BigInteger C2 = project_1_BigInt.FastExpo(new BigInteger(C2_str, 2), new BigInteger("2"), p);
                    BigInteger C3 = project_1_BigInt.FastExpo(new BigInteger(C3_str, 3), new BigInteger("2"), p);
                    BigInteger C4 = project_1_BigInt.FastExpo(new BigInteger(C4_str, 4), new BigInteger("2"), p);
                    BigInteger C5 = project_1_BigInt.FastExpo(new BigInteger(C5_str, 5), new BigInteger("2"), p);
    
                    BigInteger sumCi = C1.add(C2).add(C3).add(C4).add(C5).mod(p);
                    hashBlock[i] = hashBlock[i - 1].add(sumCi).shiftRight(16).mod(p);
                }
            }
            in.close();
        }catch (IOException e){
            System.out.println(e);
        }
        byte[] hashedText = hashBlock[4].toByteArray();
        return hashedText;
    }

    public static void ElgamalSignFile(BigInteger p, BigInteger u, BigInteger g, String filePath){
        byte[] hashedText = RWHash(p, filePath);
        byte[] signature_byte = ElgamalSignature(p, u, g, hashedText);
        File file = new File(filePath);
        String fileName = file.getName().split("\\.")[0]; 
        String outputFileName = "signature_" + fileName + ".txt";
        try {
            FileOutputStream out = new FileOutputStream(outputFileName);
            char[] signature_chr = new BigInteger(signature_byte).toString().toCharArray();
            for (int i = 0; i < signature_chr.length; i++) {
                out.write(signature_chr[i]);
            }
            out.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    } 

    public static void ElgamalVerifyFile(BigInteger p, BigInteger g, BigInteger y, String plainTextFilePath, String signedTextFilePath){
        byte[] hashedText = RWHash(p, plainTextFilePath);
        try{
            File file = new File(signedTextFilePath);
            FileInputStream in = new FileInputStream(file);
            byte[] signedTextData_chr = new byte[(int) file.length()];
            in.read(signedTextData_chr);

            String signedText = "";
            for (int i = 0; i < signedTextData_chr.length; i++) {
                signedText += (char) signedTextData_chr[i];
            }
            boolean isVerify = ElgamalVerification(p, g, y, hashedText, new BigInteger(signedText).toByteArray());
            if (isVerify) {
                System.out.println("Verified!");
            }else{
                System.out.println("Not verified!");
            }

            in.close();
        }catch (Exception e){
            System.out.println(e);
        }
    }

}

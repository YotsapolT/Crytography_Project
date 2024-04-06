import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.security.SecureRandom;

public class project_3 {
    public static void main(String[] args) {
        RWHash(new BigInteger("523"), "test.txt");   
    }

    public static byte[] ElgamalSignature(BigInteger p, BigInteger u, BigInteger g, byte[] plaintext) {
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

    public static boolean ElgamalVerification(BigInteger p, BigInteger g, BigInteger y, byte[] plainText, byte[] signedText) {
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
            System.out.println("Signed text is bigger than prime");
            System.exit(0);
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

    public static byte[] RWHash(BigInteger p, String filePath) {
        int size = p.bitLength() - 1;
        // System.out.println("size: " + size);
        int totalBitsPerBlock = 5 * size;
        // System.out.println("totalBitsPerBlock: " + totalBitsPerBlock);
        byte[] hashedText = null;
        try{
            File file = new File(filePath);
            FileInputStream in = new FileInputStream(file);
            byte[] fileData = new byte[(int) file.length()];
            in.read(fileData);

            // System.out.println("fileData.length: " + fileData.length + " bytes => " + fileData.length * 8 + " bits");
            BigInteger[] hashBlock = new BigInteger[(int) Math.ceil((fileData.length * 8) / (double) totalBitsPerBlock) + 1];

            // System.out.println("hashBlock.length: " + hashBlock.length);
            String[][] binEachHashBlock = new String[hashBlock.length - 1][1];
            for (int i = 0; i < binEachHashBlock.length; i++) {
                for (int j = 0; j < binEachHashBlock[i].length; j++) {
                    binEachHashBlock[i][j] = "";
                }
            }

            int tmpHashBlock = 0;
            for (int i = 0; i < fileData.length; i++) {
                char[] fileDataBinChr = project_1_BigInt.ByteToBits(fileData[i]).toCharArray();

                for (int j = 0; j < fileDataBinChr.length; j++) {
                    if (binEachHashBlock[tmpHashBlock][0].length() < totalBitsPerBlock){
                        binEachHashBlock[tmpHashBlock][0] += fileDataBinChr[j];
                    }else{
                        tmpHashBlock++;
                        binEachHashBlock[tmpHashBlock][0] += fileDataBinChr[j];
                    }
                }
            }

            while (tmpHashBlock < hashBlock.length - 1){
                if (binEachHashBlock[tmpHashBlock][0].length() < totalBitsPerBlock){
                    while (binEachHashBlock[tmpHashBlock][0].length() < totalBitsPerBlock) {
                        binEachHashBlock[tmpHashBlock][0] += '1';
                    }
                }
                tmpHashBlock++;
            }

            for (int i = 0; i < hashBlock.length; i++) {
                BigInteger[] chunks = new BigInteger[5];
                if (i == 0){
                    hashBlock[i] = new BigInteger(String.valueOf(fileData.length * 8));
                }else{
                    for (int j = 0; j < chunks.length; j++) {
                        chunks[j] = new BigInteger(binEachHashBlock[i - 1][0].substring((j * size), ((j + 1) * size)), 2);
                        chunks[j] = project_1_BigInt.FastExpo(chunks[j], new BigInteger(String.valueOf(j + 1)), p);
                    }
                    BigInteger sumCi = chunks[0].add(chunks[1]).add(chunks[2]).add(chunks[3]).add(chunks[4]).mod(p);
                    hashBlock[i] = hashBlock[i - 1].add(sumCi).mod(p);
                }
            }

            in.close();
            hashedText = hashBlock[hashBlock.length - 1].toByteArray();
        }catch (IOException e){
            System.out.println(e);
        }
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
            System.out.println("Signature file signed successfully. Signature file saved as: " + outputFileName);

            out.close();
        } catch (IOException e) {
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
        }catch (IOException e){
            System.out.println(e);
        }
    }

}

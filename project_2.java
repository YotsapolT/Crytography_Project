import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
public class project_2 {

    public static void main(String[] args) throws Exception {
        BigInteger p = new BigInteger(project_1_BigInt.GenSafePrime(12, "taylor.txt"), 2);
        System.out.println("Prime: " + p + "[" + p.toString(2).length() + " bits]");
        HashMap<String, BigInteger> elgamalKey = ElgamalKeyGen(p);
        System.out.println(elgamalKey);
        // savePublicKey(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"), "1");
        // savePrivateKey(elgamalKey.get("p"), elgamalKey.get("u"), "1");

        // HashMap<String, BigInteger> ElgamalPublicKey = readPublicKey("pk_1.txt");
        // System.out.println(ElgamalPublicKey);
        // HashMap<String, BigInteger> ElgamalPrivateKey = readPrivateKey("sk_1.txt");
        // System.out.println(ElgamalPrivateKey);

        // byte[] cipherText = ElgamalEncrypt(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"), new byte[] { Byte.parseByte("-1")});
        // System.out.println(Arrays.toString(cipherText));
        // byte[] plaintText = ElgamalDecrypt(elgamalKey.get("p"), elgamalKey.get("u"), cipherText);
        // System.out.println(Arrays.toString(plaintText));

        // ElgamalEncryptScanner(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"));
        // ElgamalDecryptFile(elgamalKey.get("p"), elgamalKey.get("u"), "encrypted_textScanner.txt");

        ElgamalEncryptFileNew(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"), "sunthana.jpg");
        ElgamalDecryptFileNew(elgamalKey.get("p"), elgamalKey.get("u"), "encrypted_sunthana.jpg");

        // ElgamalEncryptImageFile(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"), "sunthana.jpg");
        // ElgamalDecryptImageFile(elgamalKey.get("p"), elgamalKey.get("u"), "encrypted_sunthana.ppm");
    }

    public static BigInteger genGenerator(BigInteger p) {
        BigInteger g = new BigInteger("0");
        SecureRandom rand = new SecureRandom();
        while (true) {
            g = new BigInteger(p.bitLength(), rand);
            if (g.compareTo(new BigInteger("1")) == 1 && g.compareTo(p) == -1 && !g.mod(p).equals(p.subtract(new BigInteger("1")))) {
                BigInteger p1 = new BigInteger("0");
                p1 = p.subtract(new BigInteger("1")).divide(new BigInteger("2"));
                if (project_1_BigInt.isPrime(p1)) { // safe prime
                    BigInteger primeEleTest = project_1_BigInt.FastExpo(g, p.subtract(new BigInteger("1")).divide(new BigInteger("2")), p);
                    if (!primeEleTest.equals(new BigInteger("1"))) {
                        return g;
                    } else {
                        g = g.negate();
                        g = ((g.mod(p)).add(p)).mod(p);
                        return g;
                    }
                } else { // not safe prime
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

    public static byte[] ElgamalEncrypt(BigInteger p, BigInteger g, BigInteger y, byte[] plaintText) throws Exception {
        if (new BigInteger(plaintText).compareTo(p) == 1) {
            throw new Exception("Plain text is bigger than prime");
        }
        SecureRandom rand = new SecureRandom();
        byte[] ciphertext = new byte[p.toByteArray().length * 2];
        byte[] cipher_a = new byte[p.toByteArray().length];
        byte[] cipher_b = new byte[p.toByteArray().length];
        while (true) {
            BigInteger k = new BigInteger(p.bitLength(), rand);
            BigInteger a;
            BigInteger b;
            if (k.compareTo(new BigInteger("1")) == 1 && k.compareTo(p) == -1 && project_1_BigInt.GCD(k, p.subtract(new BigInteger("1"))).equals(new BigInteger("1"))) {
                a = project_1_BigInt.FastExpo(g, k, p);

                BigInteger X = new BigInteger(plaintText);
                b = project_1_BigInt.FastExpo(y, k, p).multiply((X.mod(p))).mod(p);

                byte[] cipher_a_nopad = a.toByteArray();
                byte[] cipher_b_nopad = b.toByteArray();
                for (int i = 0; i < cipher_a_nopad.length; i++) {
                    cipher_a[(cipher_a.length - 1) - i] = cipher_a_nopad[(cipher_a_nopad.length - 1) - i];
                }
                for (int i = 0; i < cipher_b_nopad.length; i++) {
                    cipher_b[(cipher_b.length - 1) - i] = cipher_b_nopad[(cipher_b_nopad.length - 1) - i];
                }
                for (int i = 0; i < ciphertext.length / 2; i++) {
                    ciphertext[i] = cipher_a[i];
                    ciphertext[(ciphertext.length / 2) + i] = cipher_b[i];
                }
                break;
            }
        }
        return ciphertext;
    }

    public static byte[] ElgamalDecrypt(BigInteger p, BigInteger u, byte[] ciphertext) throws Exception{
        if (ciphertext.length < p.toByteArray().length * 2){
            byte[] newCipherText = new byte[p.toByteArray().length * 2];
            for (int i = 0; i < ciphertext.length; i++){
                newCipherText[(newCipherText.length - 1) - i] = ciphertext[(ciphertext.length - 1) - i];
            }
            ciphertext = newCipherText;
        }
        BigInteger a = new BigInteger(Arrays.copyOfRange(ciphertext, 0, p.toByteArray().length));
        BigInteger b = new BigInteger(Arrays.copyOfRange(ciphertext, p.toByteArray().length, 2 * p.toByteArray().length));
        if(a.compareTo(p) == 1 || b.compareTo(p) == 1){
            throw new Exception("Cipher text is bigger than prime");
        }

        byte[] plaintext;
        BigInteger a_dec = project_1_BigInt.FastExpo(a, p.subtract(new BigInteger("1")).subtract(u), p);
        BigInteger dec = b.multiply(a_dec).mod(p);
        plaintext = dec.toByteArray();

        return plaintext;
    }

    public static void ElgamalEncryptScanner(BigInteger p, BigInteger g, BigInteger y) throws Exception {
        Scanner in = new Scanner(System.in);

        try {
            System.out.print("Enter the text you want to encrypt:");
            String str = in.nextLine();
            in.close();

            // Create a new text file
            File file = new File("textScanner.txt");
            FileWriter out = new FileWriter(file);

            // Write the input text to the file
            out.write(str);
            out.close();

            ElgamalEncryptFile(p, g, y, "textScanner.txt");
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void ElgamalEncryptFile(BigInteger p, BigInteger g, BigInteger y, String filePath) throws Exception {
        File file = new File(filePath);
        byte[] fileData = new byte[(int) file.length()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileData);
            in.close();

            String outputFilename = "encrypted_" + file.getName();
            FileOutputStream out = new FileOutputStream(outputFilename);

            LinkedList<Byte> encryptedByteList = new LinkedList<Byte>();
            LinkedList<Byte> signedList = new LinkedList<Byte>();
            byte[] tmpArr = new byte[fileData.length];
            int tmp = 0;

            for (int z = 0; z < fileData.length; z++) {
                if (fileData[z] == 0){
                    if (z != 0 && tmp != 0){
                        BigInteger plainText = new BigInteger(Arrays.copyOfRange(tmpArr, 0, tmp));
                        if(plainText.compareTo(new BigInteger("0")) == -1){
                            plainText = plainText.negate();
                            signedList.add(Byte.parseByte("1"));
                        }else{
                            signedList.add(Byte.parseByte("0"));
                        }
                        byte[] encryptedByte = ElgamalEncrypt(p.abs(), g, y, plainText.toByteArray());
                        for (byte encrypted : encryptedByte) {
                            encryptedByteList.add(encrypted);
                        }

                        for (int i = 0; i < tmp; i++) {
                            tmpArr[i] = Byte.parseByte("0");
                        }
                        tmp = 0;

                        signedList.add(Byte.parseByte("0"));
                        plainText = new BigInteger(new byte[] { fileData[z] });
                        encryptedByte = ElgamalEncrypt(p.abs(), g, y, plainText.toByteArray());
                        for (byte encrypted : encryptedByte) {
                            encryptedByteList.add(encrypted);
                        }
                    }else {
                        signedList.add(Byte.parseByte("0"));
                        BigInteger plainText = new BigInteger(new byte[] { fileData[z] });
                        byte[] encryptedByte = ElgamalEncrypt(p.abs(), g, y, plainText.toByteArray());
                        for (byte encrypted : encryptedByte) {
                            encryptedByteList.add(encrypted);
                        }
                        tmp = 0;
                    }
                    continue;
                }

                if(z != 0 && fileData[z - 1] == -1 && fileData[z] < 0){
                    BigInteger plainText = new BigInteger(Arrays.copyOfRange(tmpArr, 0, tmp));
                    if(plainText.compareTo(new BigInteger("0")) == -1){
                        plainText = plainText.negate();
                        signedList.add(Byte.parseByte("1"));
                    }else{
                        signedList.add(Byte.parseByte("0"));
                    }
                    byte[] encryptedByte = ElgamalEncrypt(p.abs(), g, y, plainText.toByteArray());
                    for (byte encrypted : encryptedByte) {
                        encryptedByteList.add(encrypted);
                    }

                    signedList.add(Byte.parseByte("1"));
                    plainText = new BigInteger(new byte[] { fileData[z] });
                    plainText = plainText.negate();
                    encryptedByte = ElgamalEncrypt(p.abs(), g, y, plainText.toByteArray());
                    for (byte encrypted : encryptedByte) {
                        encryptedByteList.add(encrypted);
                    }
                    tmpArr[0] = 0; 
                    tmp = 0;
                    continue;
                }

                tmpArr[tmp] = fileData[z];
                tmp++;
                int signed = 1;
                if(tmpArr[0] < 0){
                    if(p.compareTo(new BigInteger("0")) == 1){
                        p = p.negate();
                    }
                    signed = -signed;
                }else{
                    if(p.compareTo(new BigInteger("0")) == -1){
                        p = p.negate();
                    }
                }
                
                if (new BigInteger(Arrays.copyOfRange(tmpArr, 0, tmp)).compareTo(p) == -signed && 
                        new BigInteger(Arrays.copyOfRange(tmpArr, 0, tmp)).negate().compareTo(p.negate()) == signed &&
                        p.abs().toByteArray().length >= tmp) {
                    continue;
                }else {
                    BigInteger plainText = new BigInteger(Arrays.copyOfRange(tmpArr, 0, tmp - 1));
                    if(plainText.compareTo(new BigInteger("0")) == -1){
                        plainText = plainText.negate();
                        signedList.add(Byte.parseByte("1"));
                    }else{
                        signedList.add(Byte.parseByte("0"));
                    }
                    byte[] encryptedByte = ElgamalEncrypt(p.abs(), g, y, plainText.toByteArray());
                    for (byte encrypted : encryptedByte) {
                        encryptedByteList.add(encrypted);
                    }

                    tmpArr[0] = tmpArr[tmp - 1];
                    for (int i = 1; i < tmp; i++) {
                        tmpArr[i] = Byte.parseByte("0");
                    }
                    tmp = 1;
                }
            }
            
            //last round
            if ( tmpArr[0] != 0 ){
                BigInteger plainText = new BigInteger(Arrays.copyOfRange(tmpArr, 0, tmp));
                if(plainText.compareTo(new BigInteger("0")) == -1){
                    plainText = plainText.negate();
                    signedList.add(Byte.parseByte("1"));
                }else{
                    signedList.add(Byte.parseByte("0"));
                }
                byte[] encryptedByte = ElgamalEncrypt(p.abs(), g, y, plainText.toByteArray());
                for (byte encrypted : encryptedByte) {
                    encryptedByteList.add(encrypted);
                }
            }
            byte[] encryptedfileData = new byte[encryptedByteList.size() + signedList.size()]; 
            for (int i = 0; i < encryptedByteList.size(); i++) {
                encryptedfileData[i] = encryptedByteList.get(i);
            }
            for (int i = encryptedByteList.size(); i < encryptedfileData.length; i++) {
                encryptedfileData[i] = signedList.get(i - encryptedByteList.size());
            }
            out.write(encryptedfileData);
            out.close();
            System.out.println("File encrypted successfully. Encrypted file saved as: " + outputFilename);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void ElgamalEncryptFileNew(BigInteger p, BigInteger g, BigInteger y, String filePath) throws Exception {
        File file = new File(filePath);
        byte[] fileData = new byte[(int) file.length()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileData);
            in.close();

            String outputFilename = "encrypted_" + file.getName();
            FileOutputStream out = new FileOutputStream(outputFilename);

            LinkedList<Byte> encryptedByteList = new LinkedList<Byte>();
            byte[] tmpArr = new byte[fileData.length];
            int tmp = 0;

            for (int i = 0; i < fileData.length; i++) {
                // System.out.println(fileData[i]);
                tmpArr[tmp] = fileData[i];
                tmp++;
                if (tmpArr[0] == 0){
                    if (i == 0){
                        BigInteger plainText = new BigInteger(1, Arrays.copyOfRange(tmpArr, 0, tmp));
                        byte[] encryptedByte = ElgamalEncrypt(p, g, y, plainText.toByteArray());
                        // System.out.println(Arrays.toString(Arrays.copyOfRange(tmpArr, 0, tmp)));
                        for (byte encrypted : encryptedByte) {
                            encryptedByteList.add(encrypted);
                        }
                        tmp = 0;
                    }else {
                        BigInteger plainText = new BigInteger(1, Arrays.copyOfRange(tmpArr, 0, tmp - 1));
                        byte[] encryptedByte = ElgamalEncrypt(p, g, y, plainText.toByteArray());
                        // System.out.println(Arrays.toString(Arrays.copyOfRange(tmpArr, 0, tmp - 1)));
                        for (byte encrypted : encryptedByte) {
                            encryptedByteList.add(encrypted);
                        }

                        tmpArr[0] = tmpArr[tmp - 1];
                        for (int j = 1; j < tmp; j++) {
                            tmpArr[j] = Byte.parseByte("0");
                        }
                        tmp = 1;
                    }
                }else if (new BigInteger(1, Arrays.copyOfRange(tmpArr, 0, tmp)).compareTo(p) == -1) {
                    continue;
                }else {
                    BigInteger plainText = new BigInteger(1, Arrays.copyOfRange(tmpArr, 0, tmp - 1));
                    byte[] encryptedByte = ElgamalEncrypt(p, g, y, plainText.toByteArray());
                    // System.out.println(Arrays.toString(Arrays.copyOfRange(tmpArr, 0, tmp - 1)));
                    for (byte encrypted : encryptedByte) {
                        encryptedByteList.add(encrypted);
                    }

                    tmpArr[0] = tmpArr[tmp - 1];
                    for (int j = 1; j < tmp; j++) {
                        tmpArr[j] = Byte.parseByte("0");
                    }
                    tmp = 1;
                }
            }
            //last round
            BigInteger plainText = new BigInteger(1, Arrays.copyOfRange(tmpArr, 0, tmp));
            byte[] encryptedByte = ElgamalEncrypt(p, g, y, plainText.toByteArray());
            // System.out.println(Arrays.toString(Arrays.copyOfRange(tmpArr, 0, tmp)));
            for (byte encrypted : encryptedByte) {
                encryptedByteList.add(encrypted);
            }

            byte[] encryptedfileData = new byte[encryptedByteList.size()];
            for (int i = 0; i < encryptedByteList.size(); i++) {
                encryptedfileData[i] = encryptedByteList.get(i);
            }

            out.write(encryptedfileData);
            out.close();
            System.out.println("File encrypted successfully. Encrypted file saved as: " + outputFilename);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static void ElgamalDecryptFile(BigInteger p, BigInteger u, String filePath) throws Exception{
        File file = new File(filePath);
        byte[] fileData = new byte[(int) file.length()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileData);
            in.close();

            int blockSize = p.toByteArray().length;
            int idxEncryptedFileData = -1;
            int tmpIdx = fileData.length / (blockSize * 2);
            for (int i = tmpIdx; i > 1; i--){
                if (fileData.length == (i * blockSize * 2) + i){
                    idxEncryptedFileData = i * blockSize * 2;
                }
            }
            byte[] encryptedFileData = Arrays.copyOfRange(fileData, 0, idxEncryptedFileData);
            byte[] signedList = Arrays.copyOfRange(fileData, idxEncryptedFileData, fileData.length);
            LinkedList<Byte> decryptedByteList = new LinkedList<Byte>();
            for (int i = 0; i < encryptedFileData.length; i += (blockSize * 2)) {
                byte[] decryptedByte = ElgamalDecrypt(p, u, Arrays.copyOfRange(fileData, i, i + (blockSize * 2)));
                if(signedList[i / (blockSize * 2)] == Byte.parseByte("1")){
                    decryptedByte = new BigInteger(decryptedByte).negate().toByteArray();
                }
                for (byte decrypted : decryptedByte) {
                    decryptedByteList.add(decrypted);
                }
            }

            byte[] plaintext = new byte[decryptedByteList.size()];
            for (int i = 0; i < plaintext.length; i++){
                plaintext[i] = decryptedByteList.get(i);
            }

            String outputFilename = "decrypted_" + file.getName().split("encrypted_")[1];
            FileOutputStream out = new FileOutputStream(outputFilename);
            out.write(plaintext);
            out.close();
            System.out.println("File decrypted successfully. Decrypted file saved as: " + outputFilename);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void ElgamalDecryptFileNew(BigInteger p, BigInteger u, String filePath) throws Exception{
        File file = new File(filePath);
        byte[] fileData = new byte[(int) file.length()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileData);
            in.close();

            int blockSize = p.toByteArray().length;
            LinkedList<Byte> decryptedByteList = new LinkedList<Byte>();
            for (int i = 0; i < fileData.length; i += (blockSize * 2)) {
                byte[] decryptedByte = ElgamalDecrypt(p, u, Arrays.copyOfRange(fileData, i, i + (blockSize * 2)));
                // System.out.println(Arrays.toString(decryptedByte));
                if (decryptedByte[0] == 0 && decryptedByte.length > 1){
                    decryptedByte = Arrays.copyOfRange(decryptedByte, 1, decryptedByte.length);
                }
                for (byte decrypted : decryptedByte) {
                    decryptedByteList.add(decrypted);
                }
            }

            byte[] plaintext = new byte[decryptedByteList.size()];
            for (int i = 0; i < plaintext.length; i++){
                plaintext[i] = decryptedByteList.get(i);
            }

            String outputFilename = "decrypted_" + file.getName().split("encrypted_")[1];
            FileOutputStream out = new FileOutputStream(outputFilename);
            out.write(plaintext);
            out.close();
            System.out.println("File decrypted successfully. Decrypted file saved as: " + outputFilename);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void ElgamalEncryptImageFile(BigInteger p, BigInteger g, BigInteger y, String filePath) throws Exception {
        String[] allowType = new String[] { "png", "jpg", "jpeg" };
        String[] arr_filePath = filePath.split("\\.");
        boolean isAllow = false;
        for (int i = 0; i < allowType.length; i++) {
            if (arr_filePath[1].equals(allowType[i])) {
                isAllow = true;
                break;
            }
        }
        if (!isAllow) {
            System.out.println("This file type(." + arr_filePath[1] + ") is not allow");
            return;
        }

        String convertToPPM = arr_filePath[0] + ".ppm";
        String encryptedPPM = "encrypted_" + arr_filePath[0] + ".ppm";
        String encryptedImage = "encrypted_" + arr_filePath[0] + "." +
                arr_filePath[1];
        try {
            ppm_converter.writePPM(filePath, convertToPPM);

            File file = new File(convertToPPM);
            byte[] fileData = new byte[(int) file.length()];
            FileInputStream in = new FileInputStream(file);
            in.read(fileData);
            in.close();

            String str_fileData = new String(fileData);
            String[] split_fileData = str_fileData.split("[\\x0A\\x0D\\x20]+");

            File outputFile = new File(encryptedPPM);
            FileOutputStream fos = new FileOutputStream(outputFile);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(split_fileData[0] + "\n");
            writer.write(split_fileData[1] + " " + split_fileData[2] + "\n");
            writer.write(split_fileData[3] + "\n");

            String[][][] fileDatawoHeader = new String[Integer.parseInt(split_fileData[2])][Integer.parseInt(split_fileData[1])][3];
            int count = 4;
            for (int i = 0; i < fileDatawoHeader.length; i++) {
                for (int j = 0; j < fileDatawoHeader[i].length; j++) {
                    for (int k = 0; k < fileDatawoHeader[i][j].length; k++) {
                        fileDatawoHeader[i][j][k] = split_fileData[count];
                        count++;
                    }
                }
            }

            for (int i = 0; i < Integer.parseInt(split_fileData[2]); i++) {
                for (int j = 0; j < Integer.parseInt(split_fileData[1]); j++) {
                    BigInteger red = new BigInteger(fileDatawoHeader[i][j][0]);
                    BigInteger green = new BigInteger(fileDatawoHeader[i][j][1]);
                    BigInteger blue = new BigInteger(fileDatawoHeader[i][j][2]);

                    byte[] b_enc_red = ElgamalEncrypt(p, g, y, red.toByteArray());
                    byte[] b_enc_green = ElgamalEncrypt(p, g, y, green.toByteArray());
                    byte[] b_enc_blue = ElgamalEncrypt(p, g, y, blue.toByteArray());

                    String encrypted_red = new BigInteger(b_enc_red).toString();
                    String encrypted_green = new BigInteger(b_enc_green).toString();
                    String encrypted_blue = new BigInteger(b_enc_blue).toString();
                    writer.write(encrypted_red + " " + encrypted_green + " " + encrypted_blue + " ");
                }
                writer.write("\n");
            }
            writer.close();
            ppm_converter.readPPM(encryptedPPM, encryptedImage);
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    public static void ElgamalDecryptImageFile(BigInteger p, BigInteger u, String filePath) throws Exception {
        String[] allowType = new String[] { "ppm" };
        String[] arr_filePath = filePath.split("\\.");
        boolean isAllow = false;
        for (int i = 0; i < allowType.length; i++) {
            if (arr_filePath[1].equals(allowType[i])) {
                isAllow = true;
                break;
            }
        }
        if (!isAllow) {
            System.out.println("This file type(." + arr_filePath[1] + ") is not allow");
            return;
        }

        String decryptedPPM = "decrypted_" + arr_filePath[0] + ".ppm";
        String decryptedImage = "decrypted_" + arr_filePath[0] + ".png";
        try {
            File file = new File(filePath);
            byte[] fileData = new byte[(int) file.length()];
            FileInputStream in = new FileInputStream(file);
            in.read(fileData);
            in.close();

            String str_fileData = new String(fileData);
            String[] split_fileData = str_fileData.split("[\\x0A\\x0D\\x20]+");
            File outputFile = new File(decryptedPPM);
            FileOutputStream fos = new FileOutputStream(outputFile);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(split_fileData[0] + "\n");
            writer.write(split_fileData[1] + " " + split_fileData[2] + "\n");
            writer.write(split_fileData[3] + "\n");

            String[][][] fileDatawoHeader = new String[Integer.parseInt(split_fileData[2])][Integer.parseInt(split_fileData[1])][3];
            int count = 4;
            for (int i = 0; i < fileDatawoHeader.length; i++) {
                for (int j = 0; j < fileDatawoHeader[i].length; j++) {
                    for (int k = 0; k < fileDatawoHeader[i][j].length; k++) {
                        fileDatawoHeader[i][j][k] = split_fileData[count];
                        count++;
                    }
                }
            }

            for (int i = 0; i < Integer.parseInt(split_fileData[2]); i++) {
                for (int j = 0; j < Integer.parseInt(split_fileData[1]); j++) {
                    BigInteger encrypted_red = new BigInteger(fileDatawoHeader[i][j][0]);
                    BigInteger encrypted_green = new BigInteger(fileDatawoHeader[i][j][1]);
                    BigInteger encrypted_blue = new BigInteger(fileDatawoHeader[i][j][2]);

                    byte[] b_dec_red = ElgamalDecrypt(p, u, encrypted_red.toByteArray());
                    byte[] b_dec_green = ElgamalDecrypt(p, u, encrypted_green.toByteArray());
                    byte[] b_dec_blue = ElgamalDecrypt(p, u, encrypted_blue.toByteArray());

                    String decrypted_red = new BigInteger(b_dec_red).toString();
                    String decrypted_green = new BigInteger(b_dec_green).toString();
                    String decrypted_blue = new BigInteger(b_dec_blue).toString();
                    writer.write(decrypted_red + " " + decrypted_green + " " + decrypted_blue + " ");
                }
                writer.write("\n");
            }
            writer.close();

            ppm_converter.readPPM(decryptedPPM, decryptedImage);
            System.out.println("Image file decrypted successfully. Decrypted file saved as: " + decryptedImage);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void savePublicKey(BigInteger p, BigInteger g, BigInteger y, String pkName){
        String outputFileName = "pk_" + pkName + ".txt";
        try{
            FileOutputStream out = new FileOutputStream(outputFileName);
            char[] p_chrArr = p.toString().toCharArray();
            char[] g_chrArr = g.toString().toCharArray();
            char[] y_charArr = y.toString().toCharArray();
            byte[] pkFileData = new byte[p_chrArr.length + g_chrArr.length + y_charArr.length + 2];

            for (int i = 0; i < p_chrArr.length; i++) {
                pkFileData[i] = (byte) p_chrArr[i];
            }
            pkFileData[p_chrArr.length] = Byte.parseByte("10");
            for (int i = 0; i < g_chrArr.length; i++) {
                pkFileData[p_chrArr.length + 1 + i] = (byte) g_chrArr[i];
            }
            pkFileData[p_chrArr.length + g_chrArr.length + 1] = Byte.parseByte("10");
            for (int i = 0; i < y_charArr.length; i++) {
                pkFileData[p_chrArr.length + g_chrArr.length + 2 + i] = (byte) y_charArr[i];
            }

            out.write(pkFileData);
            out.close();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static void savePrivateKey(BigInteger p, BigInteger u, String skName){
        String outputFileName = "sk_" + skName + ".txt";
        try{
            FileOutputStream out = new FileOutputStream(outputFileName);
            char[] p_chrArr = p.toString().toCharArray();
            char[] u_chrArr = u.toString().toCharArray();
            byte[] pkFileData = new byte[p_chrArr.length + u_chrArr.length + 1];

            for (int i = 0; i < p_chrArr.length; i++) {
                pkFileData[i] = (byte) p_chrArr[i];
            }
            pkFileData[p_chrArr.length] = Byte.parseByte("10");
            for (int i = 0; i < u_chrArr.length; i++) {
                pkFileData[p_chrArr.length + 1 + i] = (byte) u_chrArr[i];
            }

            out.write(pkFileData);
            out.close();
        }catch (Exception e){
            
        }
    }

    public static HashMap<String, BigInteger> readPublicKey(String filePath){
        HashMap<String, BigInteger> ElgamalPublicKey = new HashMap<String, BigInteger>();
        File file = new File(filePath);
        byte[] fileData = new byte[(int) file.length()];
        try{
            FileInputStream in = new FileInputStream(file);
            in.read(fileData);
            in.close();

            int tmpStart = 0;
            int tmpStop = 0;
            int LFcount = 0;
            for (int i = 0; i < fileData.length; i++) {
                if (fileData[i] == 10 && LFcount == 0){
                    char[] p_chrArr = new char[tmpStop - tmpStart];
                    for (int j = 0; j < tmpStop - tmpStart; j++) {
                        p_chrArr[j] = (char) fileData[tmpStart + j]; 
                    }
                    BigInteger p = new BigInteger(new String(p_chrArr));
                    ElgamalPublicKey.put("p", p);
                    tmpStart = tmpStop + 1;
                    tmpStop = tmpStart;
                    LFcount++;
                }else if (fileData[i] == 10 && LFcount == 1){
                    char[] g_chrArr = new char[tmpStop - tmpStart];
                    for (int j = 0; j < tmpStop - tmpStart; j++) {
                        g_chrArr[j] = (char) fileData[tmpStart + j]; 
                    }
                    BigInteger g = new BigInteger(new String(g_chrArr));
                    ElgamalPublicKey.put("g", g);
                    tmpStart = tmpStop + 1;
                    tmpStop = tmpStart;
                    LFcount++;
                }else if (tmpStop == fileData.length - 1 && LFcount == 2){
                    tmpStop++;
                    char[] y_chrArr = new char[tmpStop - tmpStart];
                    for (int j = 0; j < tmpStop - tmpStart; j++) {
                        y_chrArr[j] = (char) fileData[tmpStart + j]; 
                    }
                    BigInteger y = new BigInteger(new String(y_chrArr));
                    ElgamalPublicKey.put("y", y);
                    tmpStart = tmpStop + 1;
                    tmpStop = tmpStart;
                    LFcount++;
                }else{
                    tmpStop++;
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return ElgamalPublicKey;
    }

    public static HashMap<String, BigInteger> readPrivateKey(String filePath){    
        HashMap<String, BigInteger> ElgamalPrivateKey = new HashMap<String, BigInteger>();
        File file = new File(filePath);
        byte[] fileData = new byte[(int) file.length()];
        try{
            FileInputStream in = new FileInputStream(file);
            in.read(fileData);
            in.close();

            int tmpStart = 0;
            int tmpStop = 0;
            int LFcount = 0;
            for (int i = 0; i < fileData.length; i++) {
                if (fileData[i] == 10 && LFcount == 0){
                    char[] p_chrArr = new char[tmpStop - tmpStart];
                    for (int j = 0; j < tmpStop - tmpStart; j++) {
                        p_chrArr[j] = (char) fileData[tmpStart + j]; 
                    }
                    BigInteger p = new BigInteger(new String(p_chrArr));
                    ElgamalPrivateKey.put("p", p);
                    tmpStart = tmpStop + 1;
                    tmpStop = tmpStart;
                    LFcount++;
                }else if (tmpStop == fileData.length - 1 && LFcount == 1){
                    tmpStop++;
                    char[] u_chrArr = new char[tmpStop - tmpStart];
                    for (int j = 0; j < tmpStop - tmpStart; j++) {
                        u_chrArr[j] = (char) fileData[tmpStart + j]; 
                    }
                    BigInteger u = new BigInteger(new String(u_chrArr));
                    ElgamalPrivateKey.put("u", u);
                    tmpStart = tmpStop + 1;
                    tmpStop = tmpStart;
                    LFcount++;
                }else{
                    tmpStop++;
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return ElgamalPrivateKey;
    }
}
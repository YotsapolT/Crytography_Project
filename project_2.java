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

    public static void main(String[] args) {
        BigInteger p = new BigInteger(project_1_BigInt.GenPrime(14, "text.txt"), 2);
        HashMap<String, BigInteger> elgamalKey = ElgamalKeyGen(p);
        System.out.println(elgamalKey);

        // byte[] ci = ElgamalEncrypt(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"),
        // new byte[] { Byte.parseByte("49") });
        // System.out.println(Arrays.toString(ci));
        // byte[] ci = new BigInteger("8258115").toByteArray();
        // byte[] pl = ElgamalDecrypt(elgamalKey.get("p"), elgamalKey.get("u"), ci);
        // System.out.println(Arrays.toString(pl));

        ElgamalEncryptScanner(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"));

        ElgamalEncryptTextFile(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"), "text.txt");

        ElgamalDecryptTextFile(elgamalKey.get("p"), elgamalKey.get("u"), "encrypted_textScanner.txt");

        ElgamalEncryptImageFile(elgamalKey.get("p"), elgamalKey.get("g"), elgamalKey.get("y"), "sunthana.jpg");

        ElgamalDecryptImageFile(elgamalKey.get("p"), elgamalKey.get("u"), "encrypted_sunthana.ppm");
    }

    public static BigInteger genGenerator(BigInteger p) {
        BigInteger g = new BigInteger("0");
        SecureRandom rand = new SecureRandom();
        while (true) {
            g = new BigInteger(p.bitLength(), rand);
            if (g.compareTo(new BigInteger("1")) == 1 && g.compareTo(p) == -1
                    && !g.mod(p).equals(p.subtract(new BigInteger("1")))) {
                BigInteger p1 = new BigInteger("0");
                p1 = p.subtract(new BigInteger("1")).divide(new BigInteger("2"));
                if (project_1_BigInt.isPrime(p1)) { // safe prime
                    BigInteger primeEleTest = project_1_BigInt.FastExpo(g,
                            p.subtract(new BigInteger("1")).divide(new BigInteger("2")), p);
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

    public static byte[] ElgamalEncrypt(BigInteger p, BigInteger g, BigInteger y, byte[] plaintText) {
        SecureRandom rand = new SecureRandom();
        byte[] ciphertext = new byte[p.toByteArray().length * 2];
        byte[] cipher_a = new byte[p.toByteArray().length];
        byte[] cipher_b = new byte[p.toByteArray().length];
        while (true) {
            BigInteger k = new BigInteger(p.bitLength(), rand);
            BigInteger a;
            BigInteger b;
            if (k.compareTo(new BigInteger("1")) == 1 && k.compareTo(p) == -1
                    && project_1_BigInt.GCD(k, p.subtract(new BigInteger("1"))).equals(new BigInteger("1"))) {
                a = project_1_BigInt.FastExpo(g, k, p);

                // String str_singleByte = String.valueOf(singleByte);
                // BigInteger X = new BigInteger(str_singleByte);
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

    public static byte[] ElgamalDecrypt(BigInteger p, BigInteger u, byte[] ciphertext) {
        if (ciphertext.length != p.toByteArray().length * 2) {
            byte[] tmp = Arrays.copyOf(ciphertext, ciphertext.length);
            ciphertext = new byte[p.toByteArray().length * 2];
            for (int i = 0; i < tmp.length; i++) {
                ciphertext[(ciphertext.length - 1) - i] = tmp[(tmp.length - 1) - i];
            }
        }
        byte[] plaintext;
        byte[] cipher_a = new byte[p.toByteArray().length];
        byte[] cipher_b = new byte[p.toByteArray().length];

        for (int i = 0; i < ciphertext.length / 2; i++) {
            cipher_a[i] = ciphertext[i];
            cipher_b[i] = ciphertext[(ciphertext.length / 2) + i];
        }

        BigInteger a = new BigInteger(cipher_a);
        BigInteger b = new BigInteger(cipher_b);
        BigInteger a_dec = project_1_BigInt.FastExpo(a, p.subtract(new BigInteger("1")).subtract(u), p);
        BigInteger dec = b.multiply(a_dec).mod(p);
        plaintext = dec.toByteArray();

        return plaintext;
    }

    public static void ElgamalEncryptScanner(BigInteger p, BigInteger g, BigInteger y) {
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

            ElgamalEncryptTextFile(p, g, y, "textScanner.txt");
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void ElgamalEncryptTextFile(BigInteger p, BigInteger g, BigInteger y, String filePath) {
        File file = new File(filePath);
        byte[] fileData = new byte[(int) file.length()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileData);
            in.close();

            String outputFilename = "encrypted_" + file.getName();
            FileOutputStream out = new FileOutputStream(outputFilename);

            byte[] encryptedfileData = new byte[p.toByteArray().length * 2 * fileData.length];
            int count = 0;

            for (byte b : fileData) {
                // Encrypt the byte using ElGamal encryption
                byte[] encryptedByte = ElgamalEncrypt(p, g, y, new byte[] { b });
                for (byte encrypted : encryptedByte) {
                    encryptedfileData[count] = encrypted;
                    count++;
                }

            }
            System.out.println("encryptedfileData " + Arrays.toString(encryptedfileData));
            out.write(encryptedfileData);
            out.close();

            System.out.println("File encrypted successfully. Encrypted file saved as: " + outputFilename);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void ElgamalDecryptTextFile(BigInteger p, BigInteger u, String filePath) {
        File file = new File(filePath);
        byte[] fileData = new byte[(int) file.length()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileData);
            in.close();

            int blockSize = p.toByteArray().length;
            byte[] plaintext = new byte[fileData.length / (blockSize * 2)];
            for (int i = 0; i < plaintext.length; i++) {
                plaintext[i] = ElgamalDecrypt(p, u,
                        Arrays.copyOfRange(fileData, i * (blockSize * 2), ((i + 1) * (blockSize * 2))))[0];
            }

            String outputFilename = "decrypted_" + file.getName().split("encrypted_")[1];
            FileOutputStream out = new FileOutputStream(outputFilename);

            out.write(plaintext);
            out.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void ElgamalEncryptImageFile(BigInteger p, BigInteger g, BigInteger y, String filePath) {
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

            String[][][] fileDatawoHeader = new String[Integer.parseInt(split_fileData[2])][Integer
                    .parseInt(split_fileData[1])][3];
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

    public static void ElgamalDecryptImageFile(BigInteger p, BigInteger u, String filePath) {
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

            String[][][] fileDatawoHeader = new String[Integer.parseInt(split_fileData[2])][Integer
                    .parseInt(split_fileData[1])][3];
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
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}

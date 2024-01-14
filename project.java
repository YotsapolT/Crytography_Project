import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

public class project {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter number of bits: ");
        int n = in.nextInt();
        in.nextLine();
        System.out.print("Enter file path(don't forget to escape backslash): ");
        String filePath = in.nextLine();
        String ans = getBinaryFromfile(n, filePath);
        System.out.println(ans);
        System.out.println("to int: " + Integer.parseInt(ans, 2));
        System.out.println("isPrime: " + String.valueOf(isPrime(Integer.parseInt(ans, 2))));
        in.close();
    }

    public static String getBinaryFromfile(int n, String filename) {
        File file = new File(filename);
        byte[] fileData = new byte[(int) file.length()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileData);
            in.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        String content = "";
        for (byte b : fileData) {
            content += ByteToBits(b);
        }

        String biFromFile = "";
        if (n > content.length()) {
            return "The number of bits is more than binary input file.";
        } else {
            for (int i = 0; i < content.length(); i++) {
                if (content.charAt(i) == '1') {
                    biFromFile = content.substring(i, i + n);
                    break;
                }
            }
        }

        return biFromFile;
    }

    public static String ByteToBits(byte b) {
        String result = "";
        for (int i = 7; i >= 0; i--) {
            result += (b & (1 << i)) == 0 ? "0" : "1";
        }
        // [i=7] 00110001 AND (00000001 << 7) -> 00110001 AND 00000000 -> 00000000 -> 0
        // [i=6] 00110001 AND (00000001 << 6) -> 00110001 AND 00000000 -> 00000000 -> 0
        // [i=5] 00110001 AND (00000001 << 5) -> 00110001 AND 00100000 -> 00100000 -> 1
        // [i=4] 00110001 AND (00000001 << 4) -> 00110001 AND 00010000 -> 00010000 -> 1
        // [i=3] 00110001 AND (00000001 << 3) -> 00110001 AND 00000000 -> 00000000 -> 0
        // [i=2] 00110001 AND (00000001 << 2) -> 00110001 AND 00000000 -> 00000000 -> 0
        // [i=1] 00110001 AND (00000001 << 1) -> 00110001 AND 00000000 -> 00000000 -> 0
        // [i=0] 00110001 AND (00000001 << 0) -> 00110001 AND 00000001 -> 00000001 -> 1
        // result = 00110001
        return result;
    }

    public static boolean isPrime(int n) {
        if (n <= 1)
            return false;
        if (n == 2 || n == 3)
            return true;
        if (n % 2 == 0 || n % 3 == 0)
            return false;
        for (int i = 5; i <= Math.sqrt(n); i = i + 6)
            if (n % i == 0 || n % (i + 2) == 0)
                return false;
        return true;
    }

    public static int gcd(int a, int b) {
        while (b != 0) {
            int t = b;
            b = a % b;
            a = t;
        }
        return a;
    }
}
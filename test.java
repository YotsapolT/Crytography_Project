import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.Arrays;

public class test {

    public static void main(String[] args) {
        byte[] byte_a = new byte[] { 0, 57 };
        BigInteger a = new BigInteger(byte_a);
        System.out.println(a);

        byte[] byte_b = new byte[] { 57 };
        BigInteger b = new BigInteger(byte_b);
        System.out.println(b);
    }
}
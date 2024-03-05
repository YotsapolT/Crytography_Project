import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.Arrays;

public class test {

    public static void main(String[] args) {
        byte[] a = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        BigInteger c = new BigInteger("1321");
        byte[] b = c.toByteArray(); // [1, 0]
        byte[] d = new byte[24];
        for (int i = 0; i < b.length; i++) {
            a[a.length - b.length + i] = b[i];
        }

        for (int i = 0; i < d.length / 8; i++) {
            for (int j = 0; j < a.length; j++) {
                d[(i * a.length) + j] = a[j];
            }
        }
        System.out.println(Arrays.toString(a));
        System.out.println(Arrays.toString(b));
        System.out.println(new BigInteger(a));
        System.out.println(Arrays.toString(d));
    }
}
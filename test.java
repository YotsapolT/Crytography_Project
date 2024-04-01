import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.Arrays;

public class test {
    public static void main(String[] args) {
        byte b = Byte.parseByte("0");
        BigInteger a = new BigInteger(new byte[] { b });
        System.out.println(a.abs().compareTo(new BigInteger("0")) == 0);
    }
}

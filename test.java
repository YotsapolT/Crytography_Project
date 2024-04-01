import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.Arrays;

public class test {
    public static void main(String[] args) {
        try{
            // byte[] b = new byte[] { Byte.parseByte("-8"), Byte.parseByte("3"), Byte.parseByte("0"), Byte.parseByte("0"), Byte.parseByte("-1"), Byte.parseByte("-1"),Byte.parseByte("3") };
            // String outputFilename = "tong.txt";
            // FileOutputStream out = new FileOutputStream(outputFilename);
            // out.write(b);
            // out.close();

            File file = new File("sunthana.jpg");
            byte[] fileData = new byte[(int) file.length()];
            FileInputStream in = new FileInputStream(file);
            in.read(fileData);
            in.close();

            File file1 = new File("decrypted_sunthana.jpg");
            byte[] fileData1 = new byte[(int) file1.length()];
            FileInputStream in1 = new FileInputStream(file1);
            in1.read(fileData1);
            in1.close();

            // for(int i = 0; i < 10000; i++){
            //     if (fileData[i] != fileData1[i]){
            //         System.out.println(i);
            //         System.out.println(fileData[i] + " : " + fileData1[i]);
            //         break;
            //     }
            // }

            for(int i = 0; i < 20; i++){
                System.out.println(fileData[i + 170] + " : " + fileData1[i]);
            }
        }catch (Exception e){
            System.out.println(e);
        }
        
    }
}

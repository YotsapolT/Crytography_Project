import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.Arrays;

public class test {
    public static void main(String[] args) {
        try {
            File file = new File("cap.docx");
            File file1 = new File("decrypted_cap.docx");
            FileInputStream in = new FileInputStream(file);
            FileInputStream in1 = new FileInputStream(file1);
            byte[] fileData = new byte[(int) file.length()];
            byte[] fileData1 = new byte[(int) file1.length()];
            in.read(fileData);
            in1.read(fileData1);

            for (int i = 0; i < 10; i++) {
                // if(fileData[i] != fileData1[i]){
                    System.out.println("fileData[" + i + "]: " + fileData[i]);
                    System.out.println("fileData1[" + i + "]: " + fileData1[i]);
                    System.out.println("----------------------------------------");
                    // break;
                // }
            }

            in.close();
            in1.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        
        
    }
}

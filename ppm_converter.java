import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public class ppm_converter {
    public static void writePPM(String inputImageFilePath, String outputPPMFilePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(inputImageFilePath));
        int width = image.getWidth();
        int height = image.getHeight();

        // Create the output file
        File outputFile = new File(outputPPMFilePath);
        FileOutputStream fos = new FileOutputStream(outputFile);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));

        // Write the PPM header
        writer.write("P3\n");
        writer.write(width + " " + height + "\n");
        writer.write("255\n");

        // Write the pixel data
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                writer.write(red + " " + green + " " + blue + " ");
            }
            writer.write("\n");
        }

        // Close the writer
        writer.close();
    }

    public static void readPPM(String inputPPMFilePath, String outputImageFilePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputPPMFilePath))) {
            // Read the PPM header
            String magicNumber = reader.readLine();
            if (!magicNumber.equals("P3")) {
                throw new IOException("Invalid PPM file format.");
            }

            String dimensionsLine = reader.readLine();
            StringTokenizer tokenizer = new StringTokenizer(dimensionsLine);
            int width = Integer.parseInt(tokenizer.nextToken());
            int height = Integer.parseInt(tokenizer.nextToken());
            reader.readLine(); // Maximum color value

            // Create a new BufferedImage
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // Read pixel data
            for (int y = 0; y < height; y++) {
                String line = reader.readLine();
                tokenizer = new StringTokenizer(line);
                for (int x = 0; x < width; x++) {
                    int red = Integer.parseInt(tokenizer.nextToken());
                    int green = Integer.parseInt(tokenizer.nextToken());
                    int blue = Integer.parseInt(tokenizer.nextToken());
                    int rgb = (red << 16) | (green << 8) | blue;
                    image.setRGB(x, y, rgb);
                }
            }

            ImageIO.write(image, "jpg", new File(outputImageFilePath));
        }
    }

}

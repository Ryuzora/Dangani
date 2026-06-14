import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class RemoveWatermark {
    public static void main(String[] args) throws Exception {
        String inputPath = "C:\\Users\\ASUS\\.gemini\\antigravity\\brain\\b68f19c1-546b-4b56-9099-30919d14c596\\media__1781430089663.jpg";
        String outputPath = "C:\\Users\\ASUS\\AndroidStudioProjects\\Dangani\\app\\src\\main\\res\\drawable\\app_logo.png";
        
        File file = new File(inputPath);
        if (!file.exists()) {
            System.out.println("No image found at " + inputPath);
            return;
        }
        
        System.out.println("Processing: " + file.getAbsolutePath());
        BufferedImage img = ImageIO.read(file);
        if (img == null) {
            System.out.println("Failed to read image.");
            return;
        }
        
        int w = img.getWidth();
        int h = img.getHeight();
        System.out.println("Size: " + w + "x" + h);
        
        // Copy a patch from the top-right corner to cover the bottom-right corner
        int patchW = w / 6;
        int patchH = h / 6;
        BufferedImage patch = img.getSubimage(w - patchW, 0, patchW, patchH);
        
        Graphics2D g = img.createGraphics();
        g.drawImage(patch, w - patchW, h - patchH, null);
        g.dispose();
        
        File out = new File(outputPath);
        ImageIO.write(img, "png", out);
        System.out.println("Saved to " + out.getAbsolutePath());
    }
}

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class rtwImage {
    private BufferedImage image;
    private int width, height;

    public rtwImage(String filename) {
        try {
            image = ImageIO.read(new File(filename));
            width = image.getWidth();
            height = image.getHeight();
            System.out.println("Image loaded: " + filename + " (" + width + "x"  + height + ")");
        } catch (IOException e) {
            System.err.println("ERROR: Could not load image file '" + filename + "'.");
            image = null;
        }
    }

    // Get image width
    public int getWidth() {
        return width;
    }

    // Get image height
    public int getHeight() {
        return height;
    }

    // Get the normalized color value for a specific pixel (u, v)
    public float[] getPixelData(int x, int y) {
        if (image == null) {
            return new float[]{1.0f, 0.0f, 1.0f}; // Return magenta if image is not loaded
        }
      
        x = clamp(x,0,width);
        y = clamp(y,0,height);

        int pixel = image.getRGB(x, y);

        // Extract RGB components and normalize to [0, 1]
        float r = ((pixel >> 16) & 0xFF);
        float g = ((pixel >> 8) & 0xFF);
        float b = (pixel & 0xFF);

        return new float[]{r, g, b};
    }
    private int clamp(int x, int low, int high){
        if(x < low){return low;}
        if(x < high){return x;}
        return high-1;
    }
}


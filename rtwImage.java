import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

// Class for loading and accessing image data in a ray tracing program
public class rtwImage {
    private BufferedImage image;  // Holds the image data
    private int width, height;  // Dimensions of the image

    // Constructor to load an image from a file
    public rtwImage(String filename) {
        try {
            // Attempt to read the image file
            image = ImageIO.read(new File(filename));
            width = image.getWidth();  // Get the width of the image
            height = image.getHeight();  // Get the height of the image
            System.out.println("Image loaded: " + filename + " (" + width + "x"  + height + ")");
        } catch (IOException e) {
            // Handle any IO errors (e.g., file not found)
            System.err.println("ERROR: Could not load image file '" + filename + "'.");
            image = null;  // Set image to null in case of error
        }
    }

    // Get the width of the image
    public int getWidth() {
        return width;
    }

    // Get the height of the image
    public int getHeight() {
        return height;
    }

    // Get the RGB color data of a specific pixel (x, y)
    public float[] getPixelData(int x, int y) {
        // Return a magenta color if the image is not loaded successfully
        if (image == null) {
            return new float[]{1.0f, 0.0f, 1.0f};  // Magenta indicates an error state
        }

        // Clamp the coordinates to valid range (0 to width-1 for x, 0 to height-1 for y)
        x = clamp(x, 0, width);
        y = clamp(y, 0, height);

        // Get the pixel color at (x, y) and extract RGB components
        int pixel = image.getRGB(x, y);

        // Extract each color component from the integer representation
        float r = ((pixel >> 16) & 0xFF);  // Red component (8 bits)
        float g = ((pixel >> 8) & 0xFF);   // Green component (8 bits)
        float b = (pixel & 0xFF);          // Blue component (8 bits)

        // Normalize the RGB components by dividing by 255 (to range [0, 1])
        return new float[]{r / 255.0f, g / 255.0f, b / 255.0f};
    }

    // Helper method to clamp an integer value between a low and high range
    private int clamp(int x, int low, int high) {
        if (x < low) {
            return low;  // Return low if x is below the range
        }
        if (x < high) {
            return x;    // Return x if it is within the valid range
        }
        return high - 1;  // Return high-1 if x exceeds the range
    }
}

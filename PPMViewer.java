import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.swing.*;

public class PPMViewer extends JPanel {
    private BufferedImage image;  // Holds the image to display

    // Constructor to initialize the viewer with an image from a PPM file
    public PPMViewer(String filename) {
        try {
            image = readPPM(filename);  // Attempt to load the image
        } catch (Exception e) {
            e.printStackTrace();  // Print any exceptions that occur
            // Create a default placeholder image if loading fails
            image = new BufferedImage(600, 600, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setColor(Color.BLACK);  // Set background color to black
            g.fillRect(0, 0, 600, 600);  // Fill the image with black
            g.setColor(Color.WHITE);  // Set text color to white
            g.drawString("No image loaded", 250, 300);  // Display a message
            g.dispose();  // Clean up graphics context
        }
    }

    // Reads the PPM file and returns a BufferedImage
    private BufferedImage readPPM(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String magicNumber = br.readLine();  // Read the magic number (should be "P3")

        // Check if the magic number is correct
        if (!"P3".equals(magicNumber)) {
            throw new IllegalArgumentException("Not a valid PPM file!");  // Invalid PPM format
        }

        String line;
        // Skip comment lines (lines starting with #)
        while ((line = br.readLine()).startsWith("#"));

        // Read image dimensions (width and height)
        String[] dimensions = line.split(" ");
        int width = Integer.parseInt(dimensions[0]);
        int height = Integer.parseInt(dimensions[1]);

        // Read the maximum color value (must be 255)
        int maxColorValue = Integer.parseInt(br.readLine());
        if (maxColorValue != 255) {
            throw new IllegalArgumentException("Max color value is not 255!");  // PPM standard is 255
        }

        // Check that the dimensions are valid
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be > 0!");  // Validates dimensions
        }

        // Create a BufferedImage to hold the loaded PPM data
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Read pixel data and convert to RGB format
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                String[] rgb = br.readLine().split(" ");  // Read RGB values
                int r = Integer.parseInt(rgb[0]);  // Red component
                int g = Integer.parseInt(rgb[1]);  // Green component
                int b = Integer.parseInt(rgb[2]);  // Blue component
                int rgbValue = (r << 16) | (g << 8) | b;  // Combine RGB components into a single integer
                img.setRGB(x, y, rgbValue);  // Set pixel in BufferedImage
            }
        }

        br.close();  // Close the file reader
        return img;  // Return the loaded image
    }

    // Paints the component, drawing the image on the JPanel
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, this);  // Draw the image at the top-left corner
        }
    }

    // Reloads the image from a new PPM file and repaints the component
    public void reloadImage(String filename) {
        try {
            image = readPPM(filename);  // Attempt to load the new image
        } catch (Exception e) {
            e.printStackTrace();  // Print any exceptions that occur
        }
        repaint();  // Repaint the component to display the new image
    }
}

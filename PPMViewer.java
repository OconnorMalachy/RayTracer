import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.swing.*;

public class PPMViewer extends JPanel {
    private BufferedImage image;

    public PPMViewer(String filename) {
        try {
            image = readPPM(filename);
        } catch (Exception e) {
            e.printStackTrace();
            // Create a default placeholder image if loading fails
            image = new BufferedImage(600, 600, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 600, 600);
            g.setColor(Color.WHITE);
            g.drawString("No image loaded", 250, 300);
            g.dispose();
        }
    }

    private BufferedImage readPPM(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String magicNumber = br.readLine();

        if (!"P3".equals(magicNumber)) {
            throw new IllegalArgumentException("Not a valid PPM file!");
        }

        String line;
        while ((line = br.readLine()).startsWith("#"));

        String[] dimensions = line.split(" ");
        int width = Integer.parseInt(dimensions[0]);
        int height = Integer.parseInt(dimensions[1]);

        int maxColorValue = Integer.parseInt(br.readLine());
        if (maxColorValue != 255) {
            throw new IllegalArgumentException("Max color value is not 255!");
        }

        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be > 0!");
        }

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                String[] rgb = br.readLine().split(" ");
                int r = Integer.parseInt(rgb[0]);
                int g = Integer.parseInt(rgb[1]);
                int b = Integer.parseInt(rgb[2]);
                int rgbValue = (r << 16) | (g << 8) | b;
                img.setRGB(x, y, rgbValue);
            }
        }

        br.close();
        return img;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        }
    }

    public void reloadImage(String filename) {
        try {
            image = readPPM(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
        repaint();
    }
}


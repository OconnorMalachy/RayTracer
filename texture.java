// Abstract class for textures, serves as the base class for all texture types
public abstract class texture {
    // Abstract method to get the color value based on texture coordinates and a point in 3D space
    public abstract color value(double u, double v, vec3 p);
}

// Solid color texture: returns a constant color for any texture coordinate
class solidColor extends texture {
    private final color albedo;  // The color of the texture

    // Constructor that takes a color object
    public solidColor(color albedo) {
        this.albedo = albedo;
    }

    // Constructor that takes RGB values directly
    public solidColor(double red, double green, double blue) {
        this(new color(red, green, blue));  // Create a color object with the given values
    }

    // Override the value method to return the constant color
    @Override
    public color value(double u, double v, vec3 p) {
        return albedo;  // Return the albedo color for any u, v, and p
    }
}

// Checkerboard texture: alternates between two textures based on the position
class checkerTexture extends texture {
    private double invScale;  // The inverse scale factor to control the checkerboard frequency
    private texture even;     // The texture used for even positions
    private texture odd;      // The texture used for odd positions

    // Constructor that takes scale and two texture objects for the even and odd checkers
    checkerTexture(double scale, texture even, texture odd) {
        this.invScale = 1.0 / scale;  // Inverse scale to adjust checkerboard size
        this.even = even;  // Assign the even texture
        this.odd = odd;    // Assign the odd texture
    }

    // Constructor that takes scale and two colors, converting them to solidColor textures
    checkerTexture(double scale, color c1, color c2) {
        this(scale, new solidColor(c1), new solidColor(c2));
    }

    // Override the value method to return the appropriate texture (even or odd) based on the position
    @Override
    public color value(double u, double v, vec3 p) {
        int x = (int) Math.floor(invScale * p.x());  // Calculate the x position in the checkerboard grid
        int y = (int) Math.floor(invScale * p.y());  // Calculate the y position in the checkerboard grid
        int z = (int) Math.floor(invScale * p.z());  // Calculate the z position in the checkerboard grid
        
        // Check if the sum of x, y, and z is even or odd
        boolean isEven = (x + y + z) % 2 == 0;
        
        // Return the even or odd texture based on the result
        return isEven ? even.value(u, v, p) : odd.value(u, v, p);
    }
}

// Noise texture: generates a noisy texture based on Perlin noise
class noiseTexture extends texture {
    private perlin noise = new perlin();  // The Perlin noise object used to generate the noise pattern
    private double scale;  // The scale factor that adjusts the frequency of the noise

    // Constructor that takes a scale value
    noiseTexture(double scale) {
        this.scale = scale;
    }

    // Override the value method to return a noisy color based on Perlin noise
    @Override
    public color value(double u, double v, vec3 p) {
        // Generate a noisy value based on the Perlin noise at the scaled position
        color col = new color(0.5, 0.5, 0.5);  // Start with a base gray color
        col.mult((1 + Math.sin(scale * p.z() + 10 * noise.turb(p, 7))));  // Apply a sine wave effect for variation
        return col;  // Return the modified color
    }
}

// Image texture: loads an image file and uses its pixel data as a texture
class imageTexture extends texture {
    private rtwImage image;  // The image object used to store the texture data

    // Constructor that takes an image filename and loads the image
    public imageTexture(String filename) {
        image = new rtwImage(filename);  // Load the image from the file
    }

    // Override the value method to return the color from the image at the given (u, v) coordinates
    @Override
    public color value(double u, double v, vec3 p) {
        // Return a default color (cyan) if the image is not loaded or the dimensions are invalid
        if (image.getHeight() <= 0) {
            return new color(0, 1, 1);  // Cyan indicates an error state
        }

        // Clamp the u and v coordinates to ensure they are within the valid range [0, 1]
        u = new interval(0, 1).clamp(u);
        v = 1.0 - new interval(0, 1).clamp(v);  // Flip the v-coordinate to match image coordinate system

        // Convert (u, v) to pixel coordinates
        int i = (int) (u * image.getWidth());
        int j = (int) (v * image.getHeight());

        // Get the pixel color data from the image at (i, j)
        float[] pixel = image.getPixelData(i, j);
        
        // Normalize the RGB values to the range [0, 1] (divide by 255)
        float colorScale = 1.0f / 255.0f;
        return new color(colorScale * pixel[0], colorScale * pixel[1], colorScale * pixel[2]);
    }
}

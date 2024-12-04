import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;
import java.util.function.Consumer;

public class camera {
    public double aspectRatio;  // Aspect ratio of the image (width/height)
    public int imageWidth;      // Width of the output image
    public int imageHeight;     // Height of the output image

    public double vFov = 90;    // Vertical field of view in degrees
    public vec3 lookFrom = new vec3();  // Camera position
    public vec3 lookAt = new vec3(0, 0, -1);  // The point the camera is looking at
    public vec3 vUp = new vec3(0, 1, 0);    // Up direction vector

    public double defocusAngle = 0;  // Angle of defocus for depth of field effect
    public double focusDist = 10;    // Distance at which the camera is focused

    public int maxDepth = 10;        // Maximum recursion depth for ray tracing
    public int samplesPerPixel = 10; // Number of samples per pixel for anti-aliasing
    public color background;         // Background color when no hit occurs

    private double pixelSamplesScale; // Scale for pixel samples to average color

    private vec3 center = new vec3(); // Camera center position
    private vec3 pixel00LOC = new vec3();  // Location of the top-left corner of the image
    private vec3 pixelDeltaU = new vec3(); // Change in U direction per pixel
    private vec3 pixelDeltaV = new vec3(); // Change in V direction per pixel

    private vec3 u;  // U axis of the camera viewport
    private vec3 v;  // V axis of the camera viewport
    private vec3 w;  // W axis (view direction, opposite of lookAt)

    private vec3 defocusDiskU = new vec3(); // U direction for defocus disk
    private vec3 defocusDiskV = new vec3(); // V direction for defocus disk

    // Render method, starts the rendering process
    public void render(hittable worldHittables, Consumer<Integer> progressCallback) {
        initialize();  // Initialize camera setup and calculate the viewport
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());  // Use multi-threading for rendering

        try {
            File outFile = new File("Output.ppm");  // Create the output file
            if (outFile.createNewFile()) {
                System.out.println("File created successfully.");
            } else {
                System.out.println("File already exists.");
            }

            PrintStream fileOut = new PrintStream(new FileOutputStream(outFile));
            fileOut.format("P3\n%d %d\n%d\n", imageWidth, imageHeight, constants.u8int - 1);  // Write PPM header

            color[][] imageData = new color[imageHeight][imageWidth];  // Image pixel data
            AtomicInteger completedRows = new AtomicInteger(0);  // Keep track of rows completed for progress reporting

            // Render each row in parallel
            for (int j = 0; j < imageHeight; j++) {
                final int currentRow = j;
                executor.submit(() -> {
                    // Process each pixel in the row
                    for (int i = 0; i < imageWidth; i++) {
                        color pixelColor = new color(0, 0, 0);  // Initialize pixel color to black
                        for (int k = 0; k < samplesPerPixel; k++) {
                            ray r = getRay(i, currentRow);  // Generate a ray for the pixel
                            pixelColor.add(rayColor(r, maxDepth, worldHittables));  // Accumulate color from multiple samples
                        }
                        // Scale the color by the number of samples
                        vec3 tempCol = vec3.multiply(pixelSamplesScale, pixelColor);
                        imageData[currentRow][i] = new color(tempCol.x(), tempCol.y(), tempCol.z());
                    }
                    // Update progress and notify via callback
                    int rowsComplete = completedRows.incrementAndGet();
                    int progressPercentage = (int) ((rowsComplete / (double) imageHeight) * 100);
                    if (progressCallback != null) {
                        progressCallback.accept(progressPercentage);  // Send progress update
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);  // Wait for all tasks to finish

            // Write the image data to the output file
            for (int j = 0; j < imageHeight; j++) {
                for (int i = 0; i < imageWidth; i++) {
                    color.writeColor(fileOut, imageData[j][i]);  // Write pixel data in PPM format
                }
            }

            fileOut.close();  // Close the output file
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    // Initialize the camera setup and viewport parameters
    private void initialize() {
        imageHeight = (int) (imageWidth / aspectRatio);  // Calculate height based on aspect ratio
        imageHeight = (imageHeight < 1) ? 1 : imageHeight;  // Ensure height is at least 1

        pixelSamplesScale = 1.0 / samplesPerPixel;  // Calculate pixel sample scaling

        center = lookFrom;  // Set the camera center position

        // Calculate the vertical field of view in radians
        double theta = constants.degToRads(vFov);
        double h = Math.tan(theta / 2);  // Half the vertical field of view
        double viewportHeight = 2 * h * focusDist;  // Height of the viewport
        double viewportWidth = viewportHeight * ((double) (imageWidth) / imageHeight);  // Width of the viewport

        // Set up the camera coordinate system
        w = vec3.unitVector(vec3.subtract(lookFrom, lookAt));  // W is the view direction (normalized)
        u = vec3.unitVector(vec3.cross(vUp, w));  // U is perpendicular to W and vUp
        v = vec3.cross(w, u);  // V completes the orthogonal basis

        // Calculate viewport directions in the U and V axes
        vec3 viewportU = vec3.multiply(viewportWidth, u);
        vec3 viewportV = vec3.multiply(viewportHeight, v.neg());  // V is reversed (downwards)

        // Calculate delta values for pixel spacing in U and V directions
        pixelDeltaU = vec3.divide(viewportU, imageWidth);
        pixelDeltaV = vec3.divide(viewportV, imageHeight);

        // Calculate the top-left corner of the viewport
        vec3 viewportUpperLeft = vec3.subtract(vec3.subtract(vec3.subtract(center, vec3.multiply(focusDist, w)),
                vec3.divide(viewportU, 2)), vec3.divide(viewportV, 2));
        pixel00LOC = vec3.add(viewportUpperLeft, vec3.multiply(0.5, vec3.add(pixelDeltaU, pixelDeltaV)));

        // Set up defocus for depth of field effect
        double defocusRadius = focusDist * Math.tan(constants.degToRads(defocusAngle / 2));  // Defocus radius
        defocusDiskU = vec3.multiply(defocusRadius, u);
        defocusDiskV = vec3.multiply(defocusRadius, v);
    }

    // Sample a random point on the defocus disk (for depth of field effect)
    private vec3 defocusDiskSample() {
        vec3 p = vec3.randomInUnitDisk();  // Random point on a unit disk
        return vec3.add(vec3.add(center, vec3.multiply(p.getEl(0), defocusDiskU)),
                vec3.multiply(p.getEl(1), defocusDiskV));  // Apply defocus to the sampled point
    }

    // Generate a ray for a given pixel (x, y)
    private ray getRay(int x, int y) {
        vec3 offset = sampleSquare();  // Small random offset for anti-aliasing
        vec3 pixelSample = vec3.add(pixel00LOC, vec3.add(vec3.multiply((x + offset.x()), pixelDeltaU),
                vec3.multiply((y + offset.y()), pixelDeltaV)));  // Pixel location in world space
        vec3 rayOrigin = (defocusAngle <= 0) ? center : defocusDiskSample();  // Apply defocus if necessary
        vec3 rayDirection = vec3.subtract(pixelSample, rayOrigin);  // Direction from origin to pixel

        double rayTime = constants.randomDouble();  // Random time for motion blur (optional)
        return new ray(rayOrigin, rayDirection, rayTime);  // Return the generated ray
    }

    // Sample a small square for anti-aliasing
    private vec3 sampleSquare() {
        return new vec3(constants.randomDouble() - 0.5, constants.randomDouble() - 0.5, 0);
    }

    // Calculate the color of the ray by tracing it through the scene
    private color rayColor(ray r, int depth, hittable world) {
        if (depth <= 0) {
            return new color(0, 0, 0);  // No color if maximum recursion depth is exceeded
        }

        hitRecord rec = new hitRecord();
        if (world.hit(r, new interval(0.001, Double.POSITIVE_INFINITY), rec)) {
            ray scattered = new ray();
            color attenuation = new color();
            if (rec.mat.scatter(r, rec, attenuation, scattered)) {
                // Calculate the color by tracing the scattered ray recursively
                return attenuation.multiply(rayColor(scattered, depth - 1, world));
            } else {
                return new color(0, 0, 0);  // No color if the ray doesn't scatter
            }
        } else {
            return background;  // Return the background color if no hit
        }
    }
}

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;
import java.util.function.Consumer;

public class camera{
    public double aspectRatio;
    public int imageWidth;
    public int imageHeight;

    public double vFov = 90;
    public vec3 lookFrom = new vec3();
    public vec3 lookAt = new vec3(0,0,-1);
    public vec3 vUp = new vec3(0,1,0);
    
    public double defocusAngle = 0;
    public double focusDist = 10;

    public int maxDepth = 10;
    public int samplesPerPixel = 10;
    public color background;

    private double pixelSamplesScale;
  
    private vec3 center = new vec3();
    private vec3 pixel00LOC = new vec3();
    private vec3 pixelDeltaU = new vec3();
    private vec3 pixelDeltaV = new vec3();
    
    private vec3 u;
    private vec3 v;
    private vec3 w;
  
    private vec3 defocusDiskU = new vec3();
    private vec3 defocusDiskV = new vec3();

    public void render(hittable worldHittables, Consumer<Integer> progressCallback) {
        initialize();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            File outFile = new File("Output.ppm");
            if (outFile.createNewFile()) {
                System.out.println("File created successfully.");
            } else {
                System.out.println("File already exists.");
            }

            PrintStream fileOut = new PrintStream(new FileOutputStream(outFile));
            fileOut.format("P3\n%d %d\n%d\n", imageWidth, imageHeight, constants.u8int - 1);

            color[][] imageData = new color[imageHeight][imageWidth];
            AtomicInteger completedRows = new AtomicInteger(0);

            for (int j = 0; j < imageHeight; j++) {
                final int currentRow = j;
                executor.submit(() -> {
                    for (int i = 0; i < imageWidth; i++) {
                        color pixelColor = new color(0, 0, 0);
                        for (int k = 0; k < samplesPerPixel; k++) {
                            ray r = getRay(i, currentRow);
                            pixelColor.add(rayColor(r, maxDepth, worldHittables));
                        }
                        vec3 tempCol = vec3.multiply(pixelSamplesScale, pixelColor);
                        imageData[currentRow][i] = new color(tempCol.x(), tempCol.y(), tempCol.z());
                    }
                    int rowsComplete = completedRows.incrementAndGet();
                    int progressPercentage = (int) ((rowsComplete / (double) imageHeight) * 100);
                    if (progressCallback != null) {
                        progressCallback.accept(progressPercentage);
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            for (int j = 0; j < imageHeight; j++) {
                for (int i = 0; i < imageWidth; i++) {
                    color.writeColor(fileOut, imageData[j][i]);
                }
            }

            fileOut.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private void initialize(){
        imageHeight = (int)(imageWidth / aspectRatio);
        imageHeight = (imageHeight < 1) ? 1 : imageHeight;
    
        pixelSamplesScale = 1.0 / samplesPerPixel;

        center = lookFrom;

        double theta = constants.degToRads(vFov);
        double h = Math.tan(theta/2);
        double viewportHeight = 2 * h * focusDist;
        double viewportWidth = viewportHeight * ((double)(imageWidth)/imageHeight);
        
        w = vec3.unitVector(vec3.subtract(lookFrom,lookAt));
        u = vec3.unitVector(vec3.cross(vUp,w));
        v = vec3.cross(w,u);

        vec3 viewportU = vec3.multiply(viewportWidth, u);
        vec3 viewportV = vec3.multiply(viewportHeight, v.neg());

        pixelDeltaU = vec3.divide(viewportU,imageWidth);
        pixelDeltaV = vec3.divide(viewportV, imageHeight);
        
        vec3 viewportUpperLeft = vec3.subtract(vec3.subtract(vec3.subtract(center, vec3.multiply(focusDist,w)), vec3.divide(viewportU,2)),vec3.divide(viewportV, 2));
        pixel00LOC = vec3.add(viewportUpperLeft,vec3.multiply(0.5,vec3.add(pixelDeltaU,pixelDeltaV)));
        
        double defocusRadius = focusDist * Math.tan(constants.degToRads(defocusAngle/2));
        defocusDiskU = vec3.multiply(defocusRadius,u);
        defocusDiskV = vec3.multiply(defocusRadius,v);
    }
    private vec3 defocusDiskSample(){
        vec3 p = vec3.randomInUnitDisk();
        return vec3.add(vec3.add(center, vec3.multiply(p.getEl(0), defocusDiskU)),vec3.multiply(p.getEl(1), defocusDiskV));
    }
    private ray getRay(int x, int y){
        vec3 offset = sampleSquare();
        vec3 pixelSample = vec3.add(pixel00LOC, vec3.add(vec3.multiply((x + offset.x()), pixelDeltaU), vec3.multiply((y + offset.y()), pixelDeltaV)));      
        vec3 rayOrigin = (defocusAngle <= 0) ? center : defocusDiskSample();
        vec3 rayDirection =   vec3.subtract(pixelSample, rayOrigin);
        double rayTime = constants.randomDouble();

        return new ray(rayOrigin, rayDirection, rayTime);
    }
    private vec3 sampleSquare(){return new vec3(constants.randomDouble() - 0.5, constants.randomDouble() - 0.5, 0);}

    private color rayColor(ray r, int depth, hittable world){
        // If we've exceeded the ray bounce limit, no more light is gathered.
        if(depth <= 0){
            return new color(0, 0, 0);  // black color
        }

        hitRecord rec = new hitRecord();

        // If the ray hits nothing, return the background color.
        if(!world.hit(r, new interval(0.001, constants.infinity), rec)){
            return background;  // background color, typically black or sky color
        }

        // Get emitted light from the material at the hit point.
        color colorFromEmission = rec.mat.emitted(rec.u, rec.v, rec.p);
        // Try to scatter the ray.
        ray scattered = new ray();
        color attenuation = new color();
        if(!rec.mat.scatter(r, rec, attenuation, scattered)){
            return colorFromEmission;  // If no scattering, return the emitted color.
        }
        // Recursively calculate color from scattered ray.
        color colorFromScatter = color.vecToCol(vec3.multiply(attenuation, rayColor(scattered, depth - 1, world)));
        //if(colorFromScatter.x() > 0){System.out.println(colorFromScatter);}
        // Return the combined color: emission + scattered color.
        return color.vecToCol(vec3.add(colorFromEmission,colorFromScatter));
    }

}

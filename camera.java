import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;

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

    public void render(hittable worldHittables){

        initialize();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try{
            File out = new File("Output.ppm");
            boolean fileCreated = out.createNewFile();
            if (fileCreated) {
                System.out.println("File created successfully.");
            } else {
                System.out.println("File already exists.");
            }

            // Redirect output to he file
            File outFile = new File("Output.ppm");
            PrintStream fileOut = new PrintStream(new FileOutputStream(outFile));         
            // Write PPM file header
            fileOut.format("P3\n%d %d\n%d\n",imageWidth,imageHeight,constants.u8int-1);

            color[][] imageData = new color[imageHeight][imageWidth];
            AtomicInteger completedRows = new AtomicInteger(0);

            for (int j = 0; j < imageHeight; j++) {
                final int currentRow = j;
                executor.submit(() -> {
                    for (int i = 0; i < imageWidth; i++) {
                        color pixelColor = new color(0,0,0);
                        for(int k = 0; k < samplesPerPixel; k++){
                            ray r = getRay(i,currentRow);
                            pixelColor.add(rayColor(r,maxDepth,worldHittables));
                        }
                        vec3 tempCol = vec3.multiply(pixelSamplesScale, pixelColor);
                        color col = new color(tempCol.x(),tempCol.y(),tempCol.z());
                        imageData[currentRow][i] = col;
                    }
                    int rowsComplete = completedRows.incrementAndGet();
                    int progressPercentage = (int)((rowsComplete/ (double)imageHeight) * 100);
                    System.out.printf("\rRendering: %d%% completed [%d/%d]", progressPercentage, rowsComplete, imageHeight);
              });
            }
              
            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); 
            } catch (InterruptedException e) {
                System.err.println("Rendering interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
            System.out.println();
            for(int j = 0; j < imageHeight; j++){
                for(int i = 0; i < imageWidth; i++){
                   color.writeColor(fileOut, imageData[j][i]);
                }
                int progressPercentage = (int)(((j+1)/(double)imageHeight) * 100);
                System.out.printf("\rWriting: %d%% completed [%d/%d]", progressPercentage, (j+1), imageHeight);
            }
            System.out.flush();
            System.out.println("\nDONE");         
            fileOut.close();
         } catch (IOException e) {
            e.printStackTrace();
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
        if(depth <= 0){return new color(0,0,0);}
        hitRecord rec = new hitRecord();
        if(world.hit(r,new interval(0.001,constants.infinity),rec)){
            ray scattered = new ray();
            color attenuation = new color();
            if(rec.mat.scatter(r,rec,attenuation,scattered)){
                return color.vecToCol(vec3.multiply(attenuation, rayColor(scattered,depth-1,world)));
            }
            return new color(0,0,0);
            //vec3 direction = vec3.add(rec.normal, vec3.randomUnitVector());
            //return color.vecToCol(vec3.multiply(0.5, rayColor(new ray(rec.p, direction), depth-1, world))); 
        }

        vec3 unitDirection = vec3.unitVector(r.getDirection());
        double a = 0.5 * (unitDirection.y() + 1.0);
        vec3 c = vec3.add(vec3.multiply((1.0 -a), new vec3(1.0,1.0,1.0)), vec3.multiply(a, new vec3(0.5,0.7,1.0)));
        return new color(c.x(),c.y(),c.z());   
    }  
}

import javax.swing.JFrame;

public class main {
    static camera cam = new camera();
    public static void main(String[] args) {
        switch(7) {
            case 1: spheres(); break;
            case 2: checkeredSpheres(); break;
            case 3: earth(); break;
            case 4: perlinSpheres(); break;
            case 5: quads(); break;
            case 6: simpleLight(); break;
            case 7: cornellBox(); break;
        }
        
        JFrame frame = new JFrame("PPM Viewer");
        PPMViewer panel = new PPMViewer("Output.ppm");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        
        frame.pack(); // Let the layout manager set the initial size
        int targetWidth = cam.imageWidth;
        int targetHeight = (int) (cam.imageWidth / cam.aspectRatio);
        
        // Calculate the actual content size
        int contentWidth = targetWidth + frame.getInsets().left + frame.getInsets().right;
        int contentHeight = targetHeight + frame.getInsets().top + frame.getInsets().bottom;

        // Apply the calculated size
        frame.setSize(contentWidth, contentHeight);
        frame.setVisible(true);
    }
    public static void cornellBox(){
        hittableList world = new hittableList();
        
        lambertian red = new lambertian(new color(.65, .05, .05));
        lambertian white = new lambertian(new color(.73, .73, .73));
        lambertian green = new lambertian(new color(.12, .45, .15));
        diffuseLight light = new diffuseLight(new color(15,15,15));

        world.add(new quad(new vec3(555, 0, 0), new vec3(0, 555, 0), new vec3(0, 0, 555), green));
        world.add(new quad(new vec3(0, 0, 0), new vec3(0, 555, 0), new vec3(0, 0, 555), red));
        world.add(new quad(new vec3(343, 554, 332), new vec3(-130, 0, 0), new vec3(0, 0, -105), light));
        world.add(new quad(new vec3(0, 0, 0), new vec3(555, 0, 0), new vec3(0, 0, 555), white));
        world.add(new quad(new vec3(555, 555, 555), new vec3(-555, 0, 0), new vec3(0, 0, -555), white));
        world.add(new quad(new vec3(0, 0, 555), new vec3(555, 0, 0), new vec3(0, 555, 0), white));

        hittable box1 = quad.box(new vec3(0, 0, 0), new vec3(165, 330, 165), white);
        box1 = new rotateY(box1, 15);
        box1 = new translate(box1, new vec3(265, 0, 295));
        world.add(box1);

        hittable box2 = quad.box(new vec3(0, 0, 0), new vec3(165, 165, 165), white);
        box2 = new rotateY(box2, -18);
        box2 = new translate(box2, new vec3(130, 0, 65));
        world.add(box2);
        cam.aspectRatio      = 1.0;
        cam.imageWidth       =  600;
        cam.samplesPerPixel =  100;
        cam.maxDepth         = 50;
        
        cam.vFov     = 40;
        cam.lookFrom = new vec3(278,278,-800);
        cam.lookAt   = new vec3(278,278,0);
        cam.vUp      = new vec3(0,1,0);

        cam.defocusAngle = 0;
        cam.background        = new color(0,0,0);
        long startTime = System.nanoTime();
        cam.render(world);
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1_000_000_000.0;
        System.out.printf("Rendering & Writing completed in %.2f seconds%n", duration);
    }
    public static void simpleLight(){
        hittableList world = new hittableList();
        noiseTexture perText = new noiseTexture(4);
        world.add(new sphere(new vec3(0,-1000,0),1000, new lambertian(perText)));
        world.add(new sphere(new vec3(0,2,0),2,new lambertian(perText)));
         
        diffuseLight diffLight = new diffuseLight(new color(4,4,4));
        world.add(new quad(new vec3(3,1,-2), new vec3(2,0,0), new vec3(0,2,0), diffLight));
        //world.add(new sphere(new vec3(0,7,0),2,diffLight));
        cam.aspectRatio      = 16.0 / 9.0;
        cam.imageWidth       =  500;
        cam.samplesPerPixel =  200;
        cam.maxDepth         = 100;
        
        cam.vFov     =20;
        cam.lookFrom = new vec3(26,3,6);
        cam.lookAt   = new vec3(0,2,0);
        cam.vUp      = new vec3(0,1,0);

        cam.defocusAngle = 0;
        cam.background        = new color(0.1,0,0);
        long startTime = System.nanoTime();
        cam.render(world);
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1_000_000_000.0;
        System.out.printf("Rendering & Writing completed in %.2f seconds%n", duration);
    }
    public static void quads(){
        hittableList world = new hittableList();
        lambertian leftRed = new lambertian(new color(1.0,0.2,0.2));
        lambertian backGreen = new lambertian(new color(0.2,1.0,0.2));
        lambertian rightBlue = new lambertian(new color(0.2,0.2,1.0));
        lambertian upperOrange = new lambertian(new color(1.0,0.5,0.0));
        lambertian lowerTeal = new lambertian(new color(0.2,0.8,0.8));
        world.add(new quad(new vec3(-3, -2, 5), new vec3(0, 0, -4), new vec3(0, 4, 0), leftRed));
        world.add(new quad(new vec3(-2, -2, 0), new vec3(4, 0, 0), new vec3(0, 4, 0), backGreen));
        world.add(new quad(new vec3(3, -2, 1), new vec3(0, 0, 4), new vec3(0, 4, 0), rightBlue));
        world.add(new quad(new vec3(-2, 3, 1), new vec3(4, 0, 0), new vec3(0, 0, 4), upperOrange));
        world.add(new quad(new vec3(-2, -3, 5), new vec3(4, 0, 0), new vec3(0, 0, -4), lowerTeal));

        cam.aspectRatio      = 1.0;
        cam.imageWidth       =  500;
        cam.samplesPerPixel =  100;
        cam.maxDepth         = 50;

        cam.vFov     = 80;
        cam.lookFrom = new vec3(0,0,9);
        cam.lookAt   = new vec3(0,0,0);
        cam.vUp      = new vec3(0,1,0);

        cam.defocusAngle = 0;
        cam.background        = new color(0.70, 0.80, 1.00);
        long startTime = System.nanoTime();
        cam.render(world);
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1_000_000_000.0;
        System.out.printf("Rendering & Writing completed in %.2f seconds%n", duration);
    }
    public static void perlinSpheres(){
        hittableList world = new hittableList();
        noiseTexture perText = new noiseTexture(4);
        world.add(new sphere(new vec3(0,-1000,0),1000, new lambertian(perText)));
        world.add(new sphere(new vec3(0,2,0),2, new lambertian(perText)));

        cam.aspectRatio      = 16.0 / 9.0;
        cam.imageWidth       = 1000;
        cam.samplesPerPixel =  100;
        cam.maxDepth         = 50;

        cam.vFov     = 20;
        cam.lookFrom = new vec3(13,2,3);
        cam.lookAt   = new vec3(0,0,0);
        cam.vUp      = new vec3(0,1,0);
        cam.background        = new color(0.70, 0.80, 1.00);
        cam.defocusAngle = 0;
        long startTime = System.nanoTime();
        cam.render(world);
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1_000_000_000.0;
        System.out.printf("Rendering & Writing completed in %.2f seconds%n", duration);
    }
    public static void earth(){
        imageTexture earthTexture = new imageTexture("earthmap.jpg");
        lambertian earthSurface = new lambertian(earthTexture);
        sphere globe = new sphere(new vec3(0,0,0),2,earthSurface);

        cam.aspectRatio      = 16.0 / 9.0;
        cam.imageWidth       = 500;
        cam.samplesPerPixel = 50;
        cam.maxDepth         = 50;

        cam.vFov     = 20;
        cam.lookFrom = new vec3(0,0,12);
        cam.lookAt   = new vec3(0,0,0);
        cam.vUp      = new vec3(0,1,0);

        cam.defocusAngle = 0;
        cam.background        = new color(0.70, 0.80, 1.00);
        long startTime = System.nanoTime();
        cam.render(new hittableList(globe));
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1_000_000_000.0;
        System.out.printf("Rendering & Writing completed in %.2f seconds%n", duration);
    }
    public static void spheres() {
        hittableList world = new hittableList();
        texture checker = new checkerTexture(0.32, new color(0.2, 0.3, 0.1), new color(0.9, 0.9, 0.9));
        world.add(new sphere(new vec3(0, -1000, 0), 1000, new lambertian(checker)));
        
        for (int a = -11; a < 11; a++) {
            for (int b = -11; b < 11; b++) {
                double chooseMat = constants.randomDouble();
                vec3 center = new vec3(a + 0.9 * constants.randomDouble(), 0.2, b + 0.9 * constants.randomDouble());

                if (vec3.subtract(center, new vec3(4, 0.2, 0)).len() > 0.9) {
                    material sphereMaterial;

                    if (chooseMat < 0.8) {
                        color albedo = color.vecToCol(vec3.multiply(color.random(), color.random()));
                        sphereMaterial = new lambertian(albedo);
                        vec3 center2 = vec3.add(center, new vec3(0, constants.randomDouble(0, 0.5), 0));
                        world.add(new sphere(center, 0.2, sphereMaterial));

                    } else if (chooseMat < 0.95) {
                        color albedo = color.vecToCol(color.random(0.5, 1));
                        double fuzz = constants.randomDouble(0, 0.5);
                        sphereMaterial = new metal(albedo, fuzz);
                        world.add(new sphere(center, 0.2, sphereMaterial));

                    } else {
                        sphereMaterial = new dielectric(1.5);
                        world.add(new sphere(center, 0.2, sphereMaterial));
                    }
                }
            }
        }

        world.add(new sphere(new vec3(0, 1, 0), 1.0, new dielectric(1.5)));
        world.add(new sphere(new vec3(-4, 1, 0), 1.0, new lambertian(new color(0.4, 0.2, 0.1))));
        world.add(new sphere(new vec3(4, 1, 0), 1.0, new metal(new color(0.7, 0.6, 0.5), 0.0)));

        world = new hittableList(new bvh(world));

        cam.aspectRatio = 16.0 / 9.0;
        cam.imageWidth = 1000;
        cam.samplesPerPixel = 100;
        cam.maxDepth = 50;
        cam.vFov = 20;
        cam.lookFrom = new vec3(13, 2, 3);
        cam.lookAt = new vec3(0, 0, 0);
        cam.vUp = new vec3(0, 1, 0);
        cam.defocusAngle = 0.6;
        cam.focusDist = 10.0;
        cam.background        = new color(0.70, 0.80, 1.00);
        // Render the scene
        long startTime = System.nanoTime();
        cam.render(world);
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1_000_000_000.0;
        System.out.printf("Rendering & Writing completed in %.2f seconds%n", duration);
    }
    
    public static void checkeredSpheres() {
        hittableList world = new hittableList();
        texture checker = new checkerTexture(0.32, new color(0.2, 0.3, 0.1), new color(0.9, 0.9, 0.9));
        world.add(new sphere(new vec3(0, -10, 0), 10, new lambertian(checker)));
        world.add(new sphere(new vec3(0, 10, 0), 10, new lambertian(checker)));

        cam.aspectRatio = 16.0 / 9.0;
        cam.imageWidth = 500;
        cam.samplesPerPixel = 250;
        cam.maxDepth = 50;
        cam.vFov = 20;
        cam.lookFrom = new vec3(13, 2, 3);
        cam.lookAt = new vec3(0, 0, 0);
        cam.vUp = new vec3(0, 1, 0);
        cam.defocusAngle = 0;
        cam.background        = new color(0.70, 0.80, 1.00);
        // Render the scene
        long startTime = System.nanoTime();
        cam.render(world);
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1_000_000_000.0;
        System.out.printf("Rendering & Writing completed in %.2f seconds%n", duration);
    }
}

import javax.swing.JFrame;

public class main {
    public static void main(String[] args) {

        hittableList world = new hittableList();
        texture checker = new checkerTexture(0.32,new color(0.2,0.3,0.1), new color(0.9,0.9,0.9));
        world.add(new sphere(new vec3(0, -1000, 0), 1000, new lambertian(checker)));
        
        for (int a = -11; a < 11; a++) {
            for (int b = -11; b < 11; b++) {
                double chooseMat = constants.randomDouble();
                vec3 center = new vec3(a + 0.9 * constants.randomDouble(), 0.2, b + 0.9 * constants.randomDouble());

                if (vec3.subtract(center, new vec3(4, 0.2, 0)).len() > 0.9) {
                    material sphereMaterial;

                    if (chooseMat < 0.8) {
                        color albedo = color.vecToCol(vec3.multiply(color.random(),color.random()));
                        sphereMaterial = new lambertian(albedo);
                        vec3 center2 = vec3.add(center, new vec3(0,constants.randomDouble(0,0.5),0));
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

        material material1 = new dielectric(1.5);
        world.add(new sphere(new vec3(0, 1, 0), 1.0, material1));

        material material2 = new lambertian(new color(0.4, 0.2, 0.1));
        world.add(new sphere(new vec3(-4, 1, 0), 1.0, material2));

        material material3 = new metal(new color(0.7, 0.6, 0.5), 0.0);
        world.add(new sphere(new vec3(4, 1, 0), 1.0, material3));
        world = new hittableList(new bvh(world));
        camera cam = new camera();
        cam.aspectRatio = 16.0 / 9.0;
        cam.imageWidth = 500;
        cam.samplesPerPixel = 250;
        cam.maxDepth = 50;

        cam.vFov = 20;
        cam.lookFrom = new vec3(13, 2, 3);
        cam.lookAt = new vec3(0, 0, 0);
        cam.vUp = new vec3(0, 1, 0);

        cam.defocusAngle = 0.6;
        cam.focusDist = 10.0;
        
        long startTime = System.nanoTime();
        
        cam.render(world);

        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1_000_000_000.0;
        System.out.printf("Rendering & Writing completed in %.2f seconds%n", duration);

        JFrame frame = new JFrame("PPM Viewer");
        PPMViewer panel = new PPMViewer("Output.ppm");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setSize(cam.imageWidth, (int) (cam.imageWidth / cam.aspectRatio));
        frame.setVisible(true);
    }
}


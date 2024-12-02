import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class main {
    static camera cam = new camera();
    private static JProgressBar progressBar;
    private static PPMViewer panel;
    private static String currentScene = "Spheres"; // Default scene

    public static void main(String[] args) {
        JFrame frame = new JFrame("Ray Tracer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create the menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu sceneMenu = new JMenu("Scenes");

        // Add scene options to the menu
        JMenuItem spheresScene = new JMenuItem("Spheres");
        JMenuItem cornellBoxScene = new JMenuItem("Cornell Box");
        JMenuItem showcaseScene = new JMenuItem("Showcase");

        sceneMenu.add(spheresScene);
        sceneMenu.add(cornellBoxScene);
        sceneMenu.add(showcaseScene);
        menuBar.add(sceneMenu);
        frame.setJMenuBar(menuBar);

        // Scene selection actions
        spheresScene.addActionListener(e -> currentScene = "Spheres");
        cornellBoxScene.addActionListener(e -> currentScene = "Cornell Box");
        showcaseScene.addActionListener(e -> currentScene = "Showcase");

        // Create and add UI components
        panel = new PPMViewer("Output.ppm");
        frame.add(panel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton renderButton = new JButton("Render");
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        controlPanel.add(renderButton);
        controlPanel.add(progressBar);
        frame.add(controlPanel, BorderLayout.SOUTH);

        // Render button action
        renderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renderScene();
            }
        });

        // Adjust frame size
        frame.pack();
        frame.setSize(800, 700);
        frame.setVisible(true);
    }

    private static void renderScene() {
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                hittableList world;
                if ("Cornell Box".equals(currentScene)) {
                    world = setupCornellBox();
                } else if("Showcase".equals(currentScene)){
                    world = setupShowcase();
                }else { // Default to Spheres
                    world = setupSpheres();
                }

                cam.aspectRatio = 1.0;
                cam.imageWidth = 600;
                cam.samplesPerPixel = 1000;
                cam.maxDepth = 50;

                // Start rendering
                long startTime = System.nanoTime();
                cam.render(world, (progress) -> publish(progress));
                long endTime = System.nanoTime();

                double duration = (endTime - startTime) / 1_000_000_000.0;
                System.out.printf("Rendering completed in %.2f seconds%n", duration);

                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                // Update progress bar
                int latestProgress = chunks.get(chunks.size() - 1);
                progressBar.setValue(latestProgress);
            }

            @Override
            protected void done() {
                // Reload the image in the PPMViewer
                panel.reloadImage("Output.ppm");
                panel.repaint();
            }
        };

        worker.execute();
    }

    private static hittableList setupCornellBox() {
        cam.vFov = 40;
        cam.lookFrom = new vec3(278, 278, -800);
        cam.lookAt = new vec3(278, 278, 0);
        cam.vUp = new vec3(0, 1, 0);
        cam.defocusAngle = 0;
        cam.background = new color(0, 0, 0);
        hittableList world = new hittableList();

        // Setup the Cornell box as before
        lambertian red = new lambertian(new color(.65, .05, .05));
        lambertian white = new lambertian(new color(.73, .73, .73));
        lambertian green = new lambertian(new color(.12, .45, .15));
        diffuseLight light = new diffuseLight(new color(15, 15, 15));

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

        return world;
    }
    private static hittableList setupSpheres(){
        cam.vFov = 20;
        cam.lookFrom = new vec3(15, 2, 3);
        cam.lookAt = new vec3(0, 0, 0);
        cam.vUp = new vec3(0, 1, 0);
        cam.defocusAngle = 0.6;
        cam.focusDist = 10.0;
        cam.background = new color(0.70, 0.80, 1.00);

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
        return world;
    }
    private static hittableList setupShowcase(){
        cam.vFov = 40;
        cam.lookFrom = new vec3(478, 278, -600);
        cam.lookAt = new vec3(278, 278, 0);
        cam.vUp = new vec3(0, 1, 0);

        cam.defocusAngle = 0;
        cam.background = new color(0, 0, 0);
        hittableList boxes1 = new hittableList();
        lambertian ground = new lambertian(new color(0.48, 0.83, 0.53));

        int boxesPerSide = 20;
        for(int i = 0; i < boxesPerSide; i++){
            for(int j = 0; j < boxesPerSide; j++){
                double w = 100.0;
                double x0 = -1000.0 + i*w;
                double z0 = -1000.0 + j*w;
                double y0 = 0.0;
                
                double x1 = x0 + w;
                double y1 = constants.randomDouble(1,101);
                double z1 = z0 + w;
                boxes1.add(quad.box(new vec3(x0,y0,z0), new vec3(x1,y1,z1), ground));
            }
        }

        hittableList world = new hittableList();
        world.add(new bvh(boxes1));

        diffuseLight light = new diffuseLight(new color(7,7,7));
        world.add(new quad(new vec3(123, 554, 147), new vec3(300,0,0), new vec3(0,0,265),light));

        vec3 c1 = new vec3(400,400,400);
        vec3 c2 = vec3.add(c1, new vec3(30,0,0));
        lambertian sphereMat = new lambertian(new color(0.7,0.3,0.1));
        world.add(new sphere(c1,c2,50,sphereMat));

        world.add(new sphere(new vec3(260,150,45), 50, new dielectric(1.5)));
        world.add(new sphere(new vec3(0,150,145), 50, new metal(new color(0.8, 0.8, 0.9), 1.0)));

        sphere boundary = new sphere(new vec3(360, 150, 145), 70, new dielectric(1.5));
        world.add(boundary);
        world.add(new constantMedium(boundary, 0.2, new color(0.2, 0.4, 0.9)));
        boundary = new sphere(new vec3(0,0,0), 5000, new dielectric(1.5));
        world.add(new constantMedium(boundary, .0001, new color(1,1,1)));
        
        lambertian emat = new lambertian(new imageTexture("earthmap.jpg"));
        world.add(new sphere(new vec3(400, 200, 400), 100, emat));
        world.add(new sphere(new vec3(220, 280, 300), 80, new lambertian(new color(0.4,0.1,0.7))));

        hittableList boxes2 = new hittableList();
        lambertian white = new lambertian(new color(.73,.73,.73));
        int ns = 1000;
        for(int j = 0; j < ns; j++){
            boxes2.add(new sphere(vec3.random(0,165), 10, white));
        }
        world.add(new translate(new rotateY(new bvh(boxes2), 115), new vec3(-100, 270, 395)));

        return world;
    }
}

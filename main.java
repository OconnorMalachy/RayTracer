import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.UIManager.*;
public class main {
    static camera cam = new camera();
    private static CardLayout cardLayout;
    private static JPanel mainPanel;
    private static JProgressBar progressBar;
    private static PPMViewer panel;
    private static String currentScene = "SPHERES";
  // Class-level variable
    private static JLabel sceneLabel = new JLabel("Current Scene: " + currentScene);
    private static JButton renderButton = new JButton("Render");
    public static void main(String[] args) {
        cam.aspectRatio = 1.0;
        cam.imageWidth = 600;
        cam.samplesPerPixel = 25;
        cam.maxDepth = 50;
        sceneLabel.setForeground(Color.WHITE);
        try {
            // Set Nimbus Look and Feel
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

            // Set global color properties
            UIManager.put("control", new Color(40, 40, 40));  // Background color of panels
            UIManager.put("Button.background", new Color(44, 62, 80));  // Dark button color
            UIManager.put("Button.foreground", Color.BLACK);  // Button text color (white)
            UIManager.put("Label.foreground", Color.WHITE);  // Label text color (white)
            UIManager.put("TextField.background", new Color(44, 62, 80));  // TextField background color (dark)
            UIManager.put("TextField.foreground", Color.WHITE);  // TextField text color (white)
            UIManager.put("Panel.background", new Color(34, 49, 63));  // Panel background color (dark)
            UIManager.put("TextArea.background", new Color(44, 62, 80)); // Text area background color (dark)
            UIManager.put("TextArea.foreground", Color.WHITE); // Text area text color (white)
            UIManager.put("ScrollPane.background", new Color(34, 49, 63)); // Scroll pane background

        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("Ray Tracer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add the welcome screen
        mainPanel.add(createWelcomePanel(frame), "Welcome");

        // Add the renderer panel
        mainPanel.add(createRendererPanel(), "Renderer");

        frame.add(mainPanel);
        frame.setSize(800, 700);
        frame.setVisible(true);
        frame.getRootPane().setBackground(new Color(44, 62, 80));
        // Show the welcome screen first
        cardLayout.show(mainPanel, "Welcome");
    }

    private static JPanel createWelcomePanel(JFrame frame) {
        JPanel welcomePanel = new JPanel(new BorderLayout());

        // Title
        JLabel title = new JLabel("Physically Based Rendering", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        welcomePanel.add(title, BorderLayout.NORTH);

        // Image section
        JPanel imagePanel = new JPanel(new GridLayout(1, 3, 10, 10));
        imagePanel.add(createLabeledImage("Spheres", "spheres.png"));
        imagePanel.add(createLabeledImage("Cornell Box", "cornell.png"));
        imagePanel.add(createLabeledImage("Showcase", "showcase.png"));
        welcomePanel.add(imagePanel, BorderLayout.CENTER);

        // Continue button
        JButton continueButton = new JButton("Continue");
        continueButton.addActionListener(e -> cardLayout.show(mainPanel, "Renderer"));
        welcomePanel.add(continueButton, BorderLayout.SOUTH);

        return welcomePanel;
    }

    private static JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        // Create buttons
        JButton spheresButton = new JButton("Spheres");
        JButton cornellBoxButton = new JButton("Cornell Box");
        JButton showcaseButton = new JButton("Showcase");
        JButton settingsButton = new JButton("Settings");

        // Define a uniform button size
        Dimension buttonSize = new Dimension(150, 40); // Width: 150px, Height: 40px
        spheresButton.setPreferredSize(buttonSize);
        cornellBoxButton.setPreferredSize(buttonSize);
        showcaseButton.setPreferredSize(buttonSize);
        settingsButton.setPreferredSize(buttonSize);

        // Set maximum and minimum sizes to enforce uniformity
        spheresButton.setMaximumSize(buttonSize);
        cornellBoxButton.setMaximumSize(buttonSize);
        showcaseButton.setMaximumSize(buttonSize);
        settingsButton.setMaximumSize(buttonSize);

        // Add action listeners for each button
        spheresButton.addActionListener(e -> configureSpheres()); 
        cornellBoxButton.addActionListener(e -> configureCornellBox());
        showcaseButton.addActionListener(e -> configureShowcase());
        settingsButton.addActionListener(e -> configureSettings()); 

        // Add buttons to the menu panel
        menuPanel.add(spheresButton);
        menuPanel.add(Box.createVerticalStrut(10)); // Add spacing
        menuPanel.add(cornellBoxButton);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(showcaseButton);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(settingsButton);

        // Add padding to the menu
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        return menuPanel;
    }

 
    private static void configureCornellBox() {
        currentScene = "CORNELL BOX"; // Update the current scene
        sceneLabel.setText("Current Scene: " + currentScene); // Update the label text
        // Other existing code for configuring the Cornell Box
    }

    private static void configureSpheres() {
        currentScene = "SPHERES"; // Update the current scene
        sceneLabel.setText("Current Scene: " + currentScene); // Update the label text
        // Other existing code for configuring the Spheres scene
    }

    private static void configureShowcase() {
        currentScene = "SHOWCASE"; // Update the current scene
        sceneLabel.setText("Current Scene: " + currentScene); // Update the label text
        // Other existing code for configuring the Showcase scene
    }

    private static void configureSettings() {

        JDialog configDialog = new JDialog((Frame) null, "SETTINGS", true);
        configDialog.setLayout(new BorderLayout());
        configDialog.setSize(400, 300);

        // Panel for sliders
        JPanel slidersPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        // Width slider
        JLabel widthLabel = new JLabel("Image Width:");
        widthLabel.setForeground(Color.WHITE);
        JSlider widthSlider = new JSlider(100, 600, cam.imageWidth);
        JLabel widthValueLabel = new JLabel(String.valueOf(cam.imageWidth));
        widthValueLabel.setForeground(Color.WHITE);
        widthSlider.addChangeListener(e -> widthValueLabel.setText(String.valueOf(widthSlider.getValue())));

        // Samples per pixel slider
        JLabel samplesLabel = new JLabel("Samples:");
        samplesLabel.setForeground(Color.WHITE);
        JSlider samplesSlider = new JSlider(1, 1000, cam.samplesPerPixel);
        JLabel samplesValueLabel = new JLabel(String.valueOf(cam.samplesPerPixel));
        samplesValueLabel.setForeground(Color.WHITE);

        samplesSlider.addChangeListener(e -> samplesValueLabel.setText(String.valueOf(samplesSlider.getValue())));

        // Max depth slider
        JLabel depthLabel = new JLabel("Max Depth:");
        depthLabel.setForeground(Color.WHITE);
        JSlider depthSlider = new JSlider(1, 100, cam.maxDepth);
        JLabel depthValueLabel = new JLabel(String.valueOf(cam.maxDepth));
        depthValueLabel.setForeground(Color.WHITE);
        depthSlider.addChangeListener(e -> depthValueLabel.setText(String.valueOf(depthSlider.getValue())));

        // Add sliders and labels to the panel
        slidersPanel.add(widthLabel);
        slidersPanel.add(widthSlider);
        slidersPanel.add(widthValueLabel);
        slidersPanel.add(samplesLabel);
        slidersPanel.add(samplesSlider);
        slidersPanel.add(samplesValueLabel);
        slidersPanel.add(depthLabel);
        slidersPanel.add(depthSlider);
        slidersPanel.add(depthValueLabel);

        // Confirm button
        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(e -> {
            // Set camera settings based on slider values
            cam.imageWidth = widthSlider.getValue();
            cam.imageHeight = cam.imageWidth; // Maintain aspect ratio as 1:1
            cam.samplesPerPixel = samplesSlider.getValue();
            cam.maxDepth = depthSlider.getValue();

            System.out.println("Settings updated:");
            System.out.println("  Image Width: " + cam.imageWidth + " x " + cam.imageHeight);
            System.out.println("  Samples Per Pixel: " + cam.samplesPerPixel);
            System.out.println("  Max Depth: " + cam.maxDepth);

            configDialog.dispose(); // Close dialog
        });

        configDialog.add(slidersPanel, BorderLayout.CENTER);
        configDialog.add(confirmButton, BorderLayout.SOUTH);

        configDialog.setLocationRelativeTo(null); // Center the dialog on the screen
        configDialog.setVisible(true);
    }


    private static void showConfigurationWindow(String title) {
        JDialog configDialog = new JDialog((Frame) null, title + " Configuration", true);
        configDialog.setLayout(new BorderLayout());
        configDialog.setSize(400, 300);

        // Placeholder: Add your configuration components here
        JLabel placeholderLabel = new JLabel("Configure settings for " + title, JLabel.CENTER);
        JButton confirmButton = new JButton("Confirm");

        confirmButton.addActionListener(e -> configDialog.dispose()); // Close dialog on confirm

        configDialog.add(placeholderLabel, BorderLayout.CENTER);
        configDialog.add(confirmButton, BorderLayout.SOUTH);

        configDialog.setLocationRelativeTo(null); // Center the dialog on the screen
        configDialog.setVisible(true);
    }


    private static JPanel createRendererPanel() {
        JPanel rendererPanel = new JPanel(new BorderLayout());

        // Create and add the image viewer panel
        panel = new PPMViewer("Output.ppm");
        rendererPanel.add(panel, BorderLayout.CENTER);

        // Label to display current scene
        sceneLabel.setFont(new Font("Serif", Font.BOLD, 18));
        rendererPanel.add(sceneLabel, BorderLayout.NORTH);

        // Create and add the menu panel on the right
        JPanel menuPanel = createMenuPanel();
        rendererPanel.add(menuPanel, BorderLayout.EAST);

        // Create and add the control panel at the bottom
        JPanel controlPanel = new JPanel();
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        controlPanel.add(renderButton);
        controlPanel.add(progressBar);
        rendererPanel.add(controlPanel, BorderLayout.SOUTH);

        // Render button action with feedback
        renderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renderButton.setText("Rendering..."); // Change button text
                renderButton.setEnabled(false); // Disable button during rendering
                progressBar.setValue(0); // Reset progress bar
                renderScene();
            }
        });

        return rendererPanel;
    }




    private static JPanel createLabeledImage(String title, String imagePath) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(Color.WHITE);
        JLabel imageLabel;
        try {
            ImageIcon originalIcon = new ImageIcon(imagePath);
            Image originalImage = originalIcon.getImage();

            int maxWidth = 300;  // Example max width
            int maxHeight = 300; // Example max height
            double aspectRatio = (double) originalImage.getWidth(null) / originalImage.getHeight(null);

            int scaledWidth, scaledHeight;
            if (aspectRatio >= 1) { // Wider than tall
                scaledWidth = Math.min(maxWidth, originalImage.getWidth(null));
                scaledHeight = (int) (scaledWidth / aspectRatio);
            } else { // Taller than wide
                scaledHeight = Math.min(maxHeight, originalImage.getHeight(null));
                scaledWidth = (int) (scaledHeight * aspectRatio);
            }

            Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        } catch (Exception e) {
            imageLabel = new JLabel("Image Not Found", JLabel.CENTER);
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        panel.add(titleLabel);
        panel.add(imageLabel);

        return panel;
    }
    private static void renderScene() {
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                hittableList world;
                if ("CORNELL BOX".equals(currentScene)) {
                    world = setupCornellBox();
                } else if ("SHOWCASE".equals(currentScene)) {
                    world = setupShowcase();
                } else { // Default to Spheres
                    world = setupSpheres();
                }

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

                // Enable the render button after rendering is done
                renderButton.setText("Render"); // Reset button text
                renderButton.setEnabled(true); // Re-enable button
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

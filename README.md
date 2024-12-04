# RayTracer

This ray tracing program includes a graphical user interface and customizable material and camera properties. It provides a physically-based render with no denoising applied.

## To Run
To compile and run the project, use the following commands:

```bash
javac -d bin src/*.java
java -cp bin main
```
Resources
Physically-Based Rendering book
Ray Tracing in One Weekend series
Building a BVH from Scratch blog
Rubric
1. Project Complexity
1.1 Advanced Algorithms
Several algorithms are used, including those for primitive-ray intersection, vector operations, texture generation, and trigonometric operations. The most important is the implementation of the rendering equation in the camera class.

1.2 Appropriate Data Structures
Lists, arrays, and array-lists are used. A binary tree is used to manage complexity by splitting the primitives into BVH nodes.

1.3 Design Patterns and OOP Principles
Material and Texture are both abstract classes with several subclasses.
Hittable is an interface with several classes implementing it.
OOP principles are used to make the code highly modular and easily expandable. Each class serves a distinct function with minimal overlap.

1.4 Modularity
The project is designed to be modular, allowing for easy addition of new materials, textures, and primitives (e.g., triangles) via inheritance.

2. Continued Development and Maintenance
2.1 Documentation
The README explains the project adequately, and the code is thoroughly commented. Resources serve as design documents to explain the fundamental implementation.

2.2 Modularity
Refer to 1.4—modularity is prevalent, allowing for easy expansion by adding new materials, textures, and primitives.

2.3 Version Control
Git was used to manage versions, and the project was periodically updated on GitHub after each major change.

2.4 Maintenance
Refer to 2.2 regarding the modularity of the project.

3. Visual Component and Design
3.1 UI Structure
The menu screen and customization sub-windows clearly separate the render from its associated options.

3.2 Custom Theme
The application adopts a custom dark theme for better visual appeal.

3.3 Look and Feel
The visual components are updated based on a modified Swing look and feel.

3.4 Screen Size
The screen size can be changed; however, due to the computational demands of the program, the default settings are recommended. Any changes to the screen size will not affect the image size, which is capped at 600 pixels. This is to ensure faster render times and preserve quality.

4. User Interaction
4.1 Error Handling
Error handling is implemented for file operations and usage of the PPM format. Other user interactions are validated, and sliders and menus ensure only valid data is entered.

4.2 Performance Considerations
The GUI is responsive, but physically-based ray tracers are computationally expensive. The application runs on the CPU and uses parallel processing, which may affect the overall performance of the computer during rendering. While leveraging the GPU is common for improving performance, it introduces data management challenges. The CPU implementation, with the Bounding Volume Hierarchy (BVH), improves ray-object intersection performance.

4.3 Feedback and Usability
The buttons provide clear feedback on user actions, ensuring users understand when an action has been completed.

4.4 Camera Controls
The user can modify the camera's properties to manipulate the scene, providing a great degree of control over the rendered image.        4.2:
            The GUI is responsive however physically based raytracers are incredibly expensive algorithms. Because the ray tracer runs in parralel on the CPU and takes all the processing
            power it can, during rendering the overall performance of the computer may drop. To alleviate this the most common approach is to leverage the power of the GPU however this
            introduces a range of problems with how data is managed and sent. For a physically-based renderer (which had to be written in java) the application runs as expected on the CPU 
            and the Bounding Volume Hierarchy greatly improves the most taxing aspect of a ray-tracer, ray-object intersections.
        4.3: 
            The buttons make it clear what is occuring, if an action went through, and the combination of text ensures feedback to user actions.
        4.4: 
            Modifying the properties of the camera allow the user to manipulate the scenes to a great degree. 
        
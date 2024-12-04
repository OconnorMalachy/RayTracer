# RayTracer

This ray tracing program includes a graphical user interface and customizable material and camera properties. It provides a physically-based render with no denoising applied.

## To Run
To compile and run the project, use the following commands:

```bash
javac *.java
java main
```
Resources
Physically-Based Rendering book
Ray Tracing in One Weekend series
Building a BVH from Scratch blog
Rubric

##Project Complexity

1.1 Advanced Algorithms
Several algorithms are used, including those for primitive-ray intersection, vector operations, texture generation, and trigonometric operations. The most important is the implementation of the rendering equation in the camera class.

1.2 Appropriate Data Structures
Lists, arrays, and array-lists are used. A binary tree is used to manage complexity by splitting the primitives into BVH nodes.

1.3 Design Patterns and OOP Principles
Material and Texture are both abstract classes with several subclasses.
Hittable is an interface with several classes implementing it.
OOP principles are used to make the code highly modular and easily expandable. Each class serves a distinct function with minimal overlap.

1.4 Code Organization and OOP Principles
The project is designed to be modular, allowing for easy addition of new materials, textures, and primitives (e.g., triangles) via inheritance.

##Continued Development and Maintenance

2.1 Documentation
The README explains the project adequately, and the code is thoroughly commented. Resources serve as design documents to explain the fundamental implementation.

2.2 Code Modularity
Refer to 1.4—modularity is prevalent, allowing for easy expansion by adding new materials, textures, and primitives.

2.3 Version Control Usage
Git was used to manage versions, and the project was periodically updated on GitHub after each major change.

2.4 Scalability and Extensibility
Refer to 2.2 regarding the modularity of the project.

##Visual Component and Design 

3.1 Interface Design and Layout
The menu screen and customization sub-windows clearly separate the render from its associated options.

3.2 Visual Appeal and Aesthetics
The application adopts a custom dark theme for better visual appeal.

3.3 Custom or Advanced Visual Components
The visual components are updated based on a modified Swing look and feel.

3.4 Responsiveness and Adaptability.
The screen size can be changed; however, due to the computational demands of the program, the default settings are recommended. Any changes to the screen size will not affect the image size, which is capped at 600 pixels. This is to ensure faster render times and preserve quality.

##User Interaction

4.1 Input Handling and Validation 
Error handling is implemented for file operations and usage of the PPM format. Other user interactions are validated, and sliders and menus ensure only valid data is entered.

4.2 Responsiveness and Performance 
The GUI is responsive, but physically-based ray tracers are computationally expensive. The application runs on the CPU and uses parallel processing, which may affect the overall performance of the computer during rendering. While leveraging the GPU is common for improving performance, it introduces data management challenges. The CPU implementation, with the Bounding Volume Hierarchy (BVH), improves ray-object intersection performance.

4.3 Feedback and Notifications 
The buttons provide clear feedback on user actions, ensuring users understand when an action has been completed.

4.4 Interactive Features 
The user can modify the camera's properties to manipulate the scene, providing a great degree of control over the rendered image.     

##Innovation and Creativity

5.1 
Raytracing is a very difficult algorithm to implement effeciently, especially in a languange such as Java which is neither as fast as C nor has the GPU compatibility that C has.

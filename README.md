# RayTracer
This ray tracing program includes a graphical user interface and customizable material and camera properties.
It provides a physically-based render with no denoising applied. 
To Run : javac -d bin src/*.java; java -cp bin main; 
Resources:
    physically-based rendering book.
    raytracing in one weekend series.
    building a bvh from scratch blog.
Rubric:
    1. Project Complexity
        1.1: Advanced Algorithms
            Several algorithms are used including those for primitive-ray intersection, vector operations, texture generation and trigonometric operations.
            The most important is of course the implementation of the rendering equation in the camera class.
        1.2: Appropiate Data Structures
            Lists, arrays and array-lists are used. A binary tree is used to manage the complexity by splitting the primitives into BVH nodes.
        1.3: Design patterns and OOP Principles:
            Material and Texture are both abstract classes with several classes dependent on them. Hittable is an interface and also have several classes
            dependent on it.
        1.4:
            OOP principles are used to make the code incredibly modular and easily expandable. As well each class serves a distinct function with minimal
            overlap.
    2. Continued Development and Maintenance
        2.1: 
            The README explains the project adequately, the code is thouroghly commented and the resources act as the design documents to explain the fundamental
            implementation.
        2.2:
            Refer to 1.4, modularity is prevalent as new materials, textures, and primitives(the triangle being the most obvious and useful example) can be added
            easily through inheritance.
        2.3:
            Git was used to manage versions and work was periodically published on GitHub following every major change.
        2.4:
            Refer to 2.2.
    3. Visual Component and Design  
        3.1:
            Menu screen as well as customization sub-windows clearly seperate the render from the options associated with it.
        3.2:
            Adopts a custom dark-theme.
        3.3:
            The visual components are all updated based on a modified swing look & feel.
        3.4:
            The screen size can be changed, however due to the computational magnitude of the program the default settings are recommended and any change to the screen size 
            will not change the image size which is capped at 600 pixels. This is to ensure faster render times and not sacrifice quality by scaling the image.
    4. User Interaction
        4.1:
            Panicks are implemented when dealing with files and usage of the PPM format. Other interactions from the user are measured, sliders and menus allow for only valid
            data to be entered.
        4.2:
            The GUI is responsive however physically based raytracers are incredibly expensive algorithms. Because the ray tracer runs in parralel on the CPU and takes all the processing
            power it can, during rendering the overall performance of the computer may drop. To alleviate this the most common approach is to leverage the power of the GPU however this
            introduces a range of problems with how data is managed and sent. For a physically-based renderer (which had to be written in java) the application runs as expected on the CPU 
            and the Bounding Volume Hierarchy greatly improves the most taxing aspect of a ray-tracer, ray-object intersections.
        4.3: 
            The buttons make it clear what is occuring, if an action went through, and the combination of text ensures feedback to user actions.
        4.4: 
            Modifying the properties of the camera allow the user to manipulate the scenes to a great degree. 
        

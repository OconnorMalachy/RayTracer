import java.io.*;
// vec3 class represents a 3D vector with utility methods for various vector operations.
public class vec3 {
    double[] e = new double[3];  // Array holding the 3 components of the vector (x, y, z).

    // Default constructor initializes the vector to (0, 0, 0)
    public vec3(){
        e[0] = 0.0;
        e[1] = 0.0;
        e[2] = 0.0;
    }

    // Constructor initializing vector with specific x, y, z components
    public vec3(double e0, double e1, double e2){
        e[0] = e0;
        e[1] = e1;
        e[2] = e2;
    }

    // Getter methods for x, y, and z components of the vector
    public double x(){ return e[0]; }
    public double y(){ return e[1]; }
    public double z(){ return e[2]; }

    // Negates the vector (inverts the signs of all components)
    public vec3 neg(){ return new vec3(-e[0], -e[1], -e[2]); }

    // Getter and setter for individual components of the vector
    public double getEl(int i){ return e[i]; }
    public void setEl(int i, double val){ e[i] = val; }

    // Adds another vector to this one
    public void add(vec3 v){
        e[0] += v.getEl(0);
        e[1] += v.getEl(1);
        e[2] += v.getEl(2);
    }

    // Scales the vector by a scalar value t
    public void mult(double t){
        e[0] *= t;
        e[1] *= t;
        e[2] *= t;
    }

    // Divides the vector by scalar t (equivalent to multiplying by 1/t)
    public void div(double t){ mult(1/t); }

    // Returns the length (magnitude) of the vector
    public double len(){ return Math.sqrt(lenSqr()); }

    // Returns the squared length of the vector (avoids computing square root)
    public double lenSqr(){
        return e[0] * e[0] + e[1] * e[1] + e[2] * e[2];
    }

    // Checks if the vector is near the zero vector (very small components)
    public boolean nearZero(){
        double s = 1e-8;  // A small tolerance value
        return (Math.abs(e[0]) < s) && (Math.abs(e[1]) < s) && (Math.abs(e[2]) < s);
    }

    // Utility method for printing the vector components as a string
    public String toString(){
        return String.format("%f %f %f", e[0], e[1], e[2]);
    }

    // Generates a random vector with components between 0 and 1
    public static vec3 random(){
        return new vec3(constants.randomDouble(), constants.randomDouble(), constants.randomDouble());
    }

    // Generates a random vector with components between min and max
    public static vec3 random(double min, double max){
        return new vec3(constants.randomDouble(min, max), constants.randomDouble(min, max), constants.randomDouble(min, max));
    }

    // Adds two vectors and returns the result as a new vector
    public static vec3 add(vec3 u, vec3 v) {
        return new vec3(u.e[0] + v.e[0], u.e[1] + v.e[1], u.e[2] + v.e[2]);
    }

    // Subtracts vector v from vector u and returns the result as a new vector
    public static vec3 subtract(vec3 u, vec3 v) {
        return new vec3(u.e[0] - v.e[0], u.e[1] - v.e[1], u.e[2] - v.e[2]);
    }

    // Multiplies two vectors component-wise and returns the result as a new vector
    public static vec3 multiply(vec3 u, vec3 v) {
        return new vec3(u.e[0] * v.e[0], u.e[1] * v.e[1], u.e[2] * v.e[2]);
    }

    // Multiplies a vector by a scalar t and returns the result as a new vector
    public static vec3 multiply(double t, vec3 v) {
        return new vec3(t * v.e[0], t * v.e[1], t * v.e[2]);
    }

    // Divides a vector by a scalar t and returns the result as a new vector
    public static vec3 divide(vec3 v, double t) {
        return multiply(1 / t, v);
    }

    // Calculates and returns the dot product of two vectors
    public static double dot(vec3 u, vec3 v) {
        return u.e[0] * v.e[0] + u.e[1] * v.e[1] + u.e[2] * v.e[2];
    }

    // Calculates and returns the cross product of two vectors as a new vector
    public static vec3 cross(vec3 u, vec3 v) {
        return new vec3(u.e[1] * v.e[2] - u.e[2] * v.e[1],
                        u.e[2] * v.e[0] - u.e[0] * v.e[2],
                        u.e[0] * v.e[1] - u.e[1] * v.e[0]);
    }

    // Returns a unit vector (normalized) from a given vector v
    public static vec3 unitVector(vec3 v) {
        return divide(v, v.len());
    }

    // Generates a random vector inside the unit disk (z = 0)
    public static vec3 randomInUnitDisk(){
        while(true){
            vec3 p = new vec3(constants.randomDouble(-1, 1), constants.randomDouble(-1, 1), 0);
            if(p.lenSqr() < 1) { return p; }
        }
    }

    // Generates a random unit vector, making sure it's on the unit sphere
    public static vec3 randomUnitVector(){
        while(true){
            vec3 p = vec3.random(-1, 1);
            double lenSq = p.lenSqr();
            if(1e-160 < lenSq && lenSq <= 1) { // Avoid very small vectors
                return vec3.divide(p, Math.sqrt(lenSq));  // Normalize and return
            }
        }
    }

    // Generates a random vector on the hemisphere defined by the normal
    public static vec3 randomOnHemisphere(vec3 normal){
        vec3 onUnitSphere = randomUnitVector();
        if(dot(onUnitSphere, normal) > 0.0) { return onUnitSphere; }
        return onUnitSphere.neg();  // Reflect to the opposite side if not in the hemisphere
    }

    // Reflects a vector off a surface defined by a normal vector
    public static vec3 reflect(vec3 v, vec3 n){
        return subtract(v, multiply(2 * dot(v, n), n));
    }

    // Refracts a vector through a surface based on the normal and refractive index ratio
    public static vec3 refract(vec3 uv, vec3 n, double etaiOverEtat) {
        double cosTheta = Math.min(dot(uv.neg(), n), 1.0);
        vec3 rOutPerp = multiply(etaiOverEtat, add(uv, multiply(cosTheta, n)));
        vec3 rOutParallel = multiply(-Math.sqrt(Math.abs(1.0 - rOutPerp.lenSqr())), n);
        return vec3.add(rOutPerp, rOutParallel);  // Final refracted vector
    }
}

// color class inherits from vec3 to represent color in RGB format
class color extends vec3 {
    public color() { super(); }
    public color(double r, double g, double b) { super(r, g, b); }

    // Converts a linear component to gamma space (corrects color for perceptual accuracy)
    public static double linearToGamma(double linearComponent){
        if (linearComponent > 0) { return Math.sqrt(linearComponent); }
        return 0.0;
    }

    // Writes color to an output stream in the format for PPM (PostScript image format)
    public static void writeColor(PrintStream out, color pixelColor){
        double r = pixelColor.x();
        double g = pixelColor.y();
        double b = pixelColor.z();

        r = linearToGamma(r);
        b = linearToGamma(b);
        g = linearToGamma(g);

        final interval intensity = new interval(0.000, 0.999);
        int rbyte = (int) (constants.u8int * intensity.clamp(r));
        int gbyte = (int) (constants.u8int * intensity.clamp(g));
        int bbyte = (int) (constants.u8int * intensity.clamp(b));

        out.println(String.format("%d %d %d", rbyte, gbyte, bbyte));  // Output RGB values
    }

    // Converts a vec3 object to a color object
    public static color vecToCol(vec3 v) { return new color(v.x(), v.y(), v.z()); }

    // Sets this color to the values of another color object
    public void set(color c){
        e[0] = c.x();
        e[1] = c.y();
        e[2] = c.z();
    }
}

// Class representing a ray in 3D space, defined by an origin point and a direction vector.
public class ray {
    private vec3 origin;  // The origin point of the ray
    private vec3 direction; // The direction of the ray
    private double tm;  // The time associated with the ray (used for motion or animation)

    // Constructor for initializing the ray with an origin, direction, and time.
    public ray(vec3 or, vec3 dir, double time) {
        origin = or;  // Set the origin of the ray
        direction = dir;  // Set the direction of the ray
        tm = time;  // Set the time associated with the ray
    }

    // Constructor for initializing the ray with an origin, direction, and default time (0.0).
    public ray(vec3 or, vec3 dir) {
        this(or, dir, 0.0);  // Call the first constructor with a default time of 0.0
    }

    // Default constructor that initializes the ray with zero vectors (origin at the origin, and direction along the zero vector).
    public ray() {
        origin = new vec3();  // Create a new vec3 for the origin (initialized to (0, 0, 0))
        direction = new vec3();  // Create a new vec3 for the direction (initialized to (0, 0, 0))
    }

    // Getter for the time value associated with the ray.
    public double getTime() {
        return tm;  // Return the time value of the ray
    }

    // Getter for the origin vector of the ray.
    public vec3 getOrigin() {
        return origin;  // Return the origin vector of the ray
    }

    // Getter for the direction vector of the ray.
    public vec3 getDirection() {
        return direction;  // Return the direction vector of the ray
    }

    // Method to compute a point along the ray at a given parameter t.
    public vec3 at(double t) {
        // Return the point at time t, which is the origin + direction * t
        return vec3.add(origin, vec3.multiply(t, direction));
    }

    // Setter for setting the origin, direction, and time of the ray.
    public void set(vec3 o, vec3 dir, double t) {
        origin = o;  // Set the origin of the ray
        direction = dir;  // Set the direction of the ray
        tm = t;  // Set the time associated with the ray
    }

    // Setter for setting the origin and direction of the ray with a default time of 0.0.
    public void set(vec3 o, vec3 dir) {
        this.set(o, dir, 0.0);  // Call the first setter with a default time of 0.0
    }
}

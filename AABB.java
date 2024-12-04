// Axis-Aligned Bounding Box (AABB) class
public class AABB {
    // Defines the intervals for each axis (x, y, z)
    public interval x, y, z;

    // Predefined constant for an empty AABB (no space)
    public static final AABB EMPTY = new AABB(interval.EMPTY, interval.EMPTY, interval.EMPTY);
    
    // Predefined constant for the universe, covering the entire space
    public static final AABB UNIVERSE = new AABB(interval.UNIVERSE, interval.UNIVERSE, interval.UNIVERSE);

    // Default constructor (creates an AABB with no intervals)
    public AABB() {}

    // Constructor that initializes the AABB with specified intervals for each axis
    public AABB(interval x, interval y, interval z) {
        this.x = x;
        this.y = y;
        this.z = z;
        padToMinimums(); // Ensures the AABB has a minimum size (avoids degenerate boxes)
    }

    // Constructor that initializes the AABB using two points in 3D space (vec3)
    public AABB(vec3 pA, vec3 pB) {
        // Create arrays to hold the x, y, z values of the points
        double[] a = { pA.getEl(0), pA.getEl(1), pA.getEl(2) };
        double[] b = { pB.getEl(0), pB.getEl(1), pB.getEl(2) };

        // Define intervals for each axis by comparing the coordinates of pA and pB
        this.x = (a[0] < b[0]) ? new interval(a[0], b[0]) : new interval(b[0], a[0]);
        this.y = (a[1] < b[1]) ? new interval(a[1], b[1]) : new interval(b[1], a[1]);
        this.z = (a[2] < b[2]) ? new interval(a[2], b[2]) : new interval(b[2], a[2]);

        // Ensure the AABB has a minimum size
        padToMinimums();
    }

    // Constructor that creates an AABB from the union of two other AABBs
    public AABB(AABB box0, AABB box1) {
        this.x = new interval(box0.x, box1.x); // Combine the x intervals
        this.y = new interval(box0.y, box1.y); // Combine the y intervals
        this.z = new interval(box0.z, box1.z); // Combine the z intervals
    }

    // Returns the longest axis of the AABB (0 for x, 1 for y, 2 for z)
    public int longestAxis() {
        double xSize = x.size();
        double ySize = y.size();
        double zSize = z.size();

        // Determines which axis has the largest size
        if (xSize > ySize) {
            return (xSize > zSize) ? 0 : 2; // x is the largest
        } else {
            return (ySize > zSize) ? 1 : 2; // y is the largest
        }
    }

    // Returns the interval corresponding to a specific axis (0 = x, 1 = y, 2 = z)
    public interval axisInterval(int n) {
        if (n == 1) {
            return y; // Return y-axis interval
        }
        if (n == 2) {
            return z; // Return z-axis interval
        }
        return x; // Return x-axis interval
    }

    // Adds an offset to an existing AABB (translation)
    public static AABB add(AABB bbox, vec3 offset) {
        // Translate the intervals of the AABB by the given offset vector
        return new AABB(interval.add(bbox.x, offset.x()), interval.add(bbox.y, offset.y()), interval.add(bbox.z, offset.z()));
    }

    // Checks if a ray intersects this AABB and updates the ray's t interval
    public boolean hit(ray r, interval rayT) {
        vec3 rayOrigin = r.getOrigin();  // Ray origin
        vec3 rayDir = r.getDirection();  // Ray direction

        // Loop through each axis (x, y, z)
        for (int axis = 0; axis < 3; axis++) {
            interval ax = axisInterval(axis);  // Get the interval for the current axis
            double adInv = 1.0 / rayDir.getEl(axis); // Inverse of the ray direction for the current axis

            // Calculate the entry and exit points along the ray for the current axis
            double t0 = (ax.min - rayOrigin.getEl(axis)) * adInv;
            double t1 = (ax.max - rayOrigin.getEl(axis)) * adInv;

            // If t0 < t1, swap the values to keep t0 as the entry point and t1 as the exit point
            if (t0 < t1) {
                if (t0 > rayT.min) rayT.min = t0; // Update the ray's minimum t value
                if (t1 < rayT.max) rayT.max = t1; // Update the ray's maximum t value
            } else {
                if (t1 > rayT.min) rayT.min = t1;
                if (t0 < rayT.max) rayT.max = t0;
            }

            // If no intersection exists (max t < min t), return false
            if (rayT.max <= rayT.min) {
                return false;
            }
        }
        return true; // Return true if all axis intersections are valid
    }

    // Ensures that the AABB has a minimum size to avoid degenerate boxes
    private void padToMinimums() {
        double delta = 0.0001; // Small threshold value
        if (x.size() < delta) x = x.expand(delta); // Expand the x interval if it's too small
        if (y.size() < delta) y = y.expand(delta); // Expand the y interval if it's too small
        if (z.size() < delta) z = z.expand(delta); // Expand the z interval if it's too small
    }

    // Converts the AABB to a string for easy debugging and display
    public String toString() {
        return "X: " + x + " Y: " + y + " Z: " + z;
    }
}

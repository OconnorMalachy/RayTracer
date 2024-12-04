// Perlin Noise class used for generating smooth random noise
public class perlin {
    private static int pointCount = 256; // The number of random points for the Perlin noise grid
    private vec3[] randVec = new vec3[pointCount]; // Array to store random vectors for noise calculation
    private int[] permX = new int[pointCount]; // Permutation table for the X axis
    private int[] permY = new int[pointCount]; // Permutation table for the Y axis
    private int[] permZ = new int[pointCount]; // Permutation table for the Z axis

    // Method to generate a permutation table for randomization
    private static void perlinGeneratePerm(int[] p) {
        for (int i = 0; i < pointCount; i++) {
            p[i] = i; // Initialize the array with sequential integers
        }
        permute(p, pointCount); // Shuffle the array to randomize the values
    }

    // Helper method to shuffle the permutation table using the Fisher-Yates algorithm
    private static void permute(int[] p, int n) {
        for (int i = n - 1; i > 0; i--) {
            // Randomly select a target index and swap the current index with it
            int target = constants.randomInt(0, i);
            int tmp = p[i];
            p[i] = p[target];
            p[target] = tmp;
        }
    }

    // Constructor for the Perlin class, initializing random vectors and permutation tables
    perlin() {
        for (int i = 0; i < pointCount; i++) {
            randVec[i] = vec3.unitVector(vec3.random(-1.0, 1.0)); // Generate random unit vectors
        }
        perlinGeneratePerm(permX); // Generate permutation table for X
        perlinGeneratePerm(permY); // Generate permutation table for Y
        perlinGeneratePerm(permZ); // Generate permutation table for Z
    }

    // Method to generate Perlin noise value for a given point in 3D space
    public double noise(vec3 p) {
        // Compute the fractional part of the coordinates
        double u = p.x() - Math.floor(p.x());
        double v = p.y() - Math.floor(p.y());
        double w = p.z() - Math.floor(p.z());

        // Find the integer part of the coordinates
        int i = (int) Math.floor(p.x());
        int j = (int) Math.floor(p.y());
        int k = (int) Math.floor(p.z());

        // 3x3x3 array to hold random vectors for interpolation
        vec3[][][] c = new vec3[2][2][2];

        // Fetch random vectors for each corner of the cube surrounding the point
        for (int di = 0; di < 2; di++) {
            for (int dj = 0; dj < 2; dj++) {
                for (int dk = 0; dk < 2; dk++) {
                    c[di][dj][dk] = randVec[
                        permX[(i + di) & 255] ^ // Hash using permutation table for X
                        permY[(j + dj) & 255] ^ // Hash using permutation table for Y
                        permZ[(k + dk) & 255]   // Hash using permutation table for Z
                    ];
                }
            }
        }

        // Perform Perlin interpolation and return the result
        return perlinInterp(c, u, v, w);
    }

    // Method to generate turbulence (roughness) at a given point with multiple iterations
    public double turb(vec3 p, int depth) {
        double accum = 0.0; // Accumulated turbulence
        vec3 tempP = p; // Temporary point to modify during iterations
        double weight = 1.0; // Weight factor that decreases with each iteration

        // Perform turbulence over several octaves
        for (int i = 0; i < depth; i++) {
            accum += weight * noise(tempP); // Add the noise value weighted by the current weight
            weight *= 0.5; // Reduce weight for next iteration
            tempP.mult(2); // Scale the point for the next octave (zoom in)
        }

        // Return the absolute value of the accumulated turbulence (ensuring positive values)
        return Math.abs(accum);
    }

    // Method to interpolate between the random values at the corners of the cube using Perlin interpolation
    public static double perlinInterp(vec3[][][] c, double u, double v, double w) {
        // Calculate the smoothstep curve for each axis
        double uu = u * u * (3 - 2 * u);
        double vv = v * v * (3 - 2 * v);
        double ww = w * w * (3 - 2 * w);

        double accum = 0.0; // The resulting interpolated value

        // Perform trilinear interpolation over the 8 surrounding points (2x2x2 cube)
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    // Calculate the weight vector from the point to the current corner
                    vec3 weightV = new vec3(u - i, v - j, w - k);
                    // Apply the smoothstep curve and compute the contribution from each corner
                    accum += (i * uu + (1 - i) * (1 - uu)) 
                           * (j * vv + (1 - j) * (1 - vv)) 
                           * (k * ww + (1 - k) * (1 - ww)) 
                           * vec3.dot(c[i][j][k], weightV); // Dot product with the random vector at each corner
                }
            }
        }

        return accum; // Return the final interpolated noise value
    }
}

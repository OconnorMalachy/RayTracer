public class hitRecord {
    public vec3 p;           // The point of intersection between the ray and the surface
    public vec3 normal;      // The normal vector at the point of intersection
    public double t;         // The parameter 't' that indicates the ray's distance to the intersection point (used for ray parameterization)
    public double u;         // The u texture coordinate of the intersection point (useful for textures)
    public double v;         // The v texture coordinate of the intersection point (useful for textures)
    public boolean frontFace;  // Whether the ray hit the front or back face of the surface
    public material mat;     // The material of the surface at the intersection point (e.g., diffuse, reflective)

    /**
     * This method sets the correct normal for the hit point, ensuring the normal vector
     * is pointing outward, which is important for correct lighting calculations.
     * 
     * @param r The ray that hit the surface
     * @param outwardNormal The normal vector pointing away from the surface
     */
    public void setFaceNormal(ray r, vec3 outwardNormal) {
        // The front face of the surface is the one where the ray is traveling in the opposite direction of the normal
        frontFace = vec3.dot(r.getDirection(), outwardNormal) < 0;
        // If the ray hits the front face, the normal is the same; otherwise, it's reversed
        normal = frontFace ? outwardNormal : outwardNormal.neg();
    }

    /**
     * This method copies the information from another hitRecord to this one.
     * It is useful for creating or modifying hitRecord objects during ray tracing.
     * 
     * @param h The hitRecord to copy information from
     */
    public void set(hitRecord h) {
        p = h.p;           // Copy the intersection point
        normal = h.normal; // Copy the normal vector
        t = h.t;           // Copy the ray parameter 't'
        frontFace = h.frontFace;  // Copy the frontFace status
        mat = h.mat;       // Copy the material information
        u = h.u;           // Copy the u texture coordinate
        v = h.v;           // Copy the v texture coordinate
    }
}

public class rotateY implements hittable{
    private final hittable object;
    private final double sinTheta;
    private final double cosTheta;
    private final AABB bbox;

    public rotateY(hittable object, double angle) {
        this.object = object;

        // Compute sine and cosine of the rotation angle in radians
        double radians = Math.toRadians(angle);
        this.sinTheta = Math.sin(radians);
        this.cosTheta = Math.cos(radians);

        // Get the original bounding box of the object
        AABB originalBbox = object.boundingBox();
        if (originalBbox == null) {
            this.bbox = null;
            return;
        }

        // Compute the rotated bounding box
        vec3 min = new vec3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        vec3 max = new vec3(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    // Determine corner coordinates
                    double x = i * originalBbox.x.max + (1 - i) * originalBbox.x.min;
                    double y = j * originalBbox.y.max + (1 - j) * originalBbox.y.min;
                    double z = k * originalBbox.z.max + (1 - k) * originalBbox.z.min;

                    // Rotate the corner
                    double newX = cosTheta * x + sinTheta * z;
                    double newZ = -sinTheta * x + cosTheta * z;

                    vec3 tester = new vec3(newX, y, newZ);

                    // Update bounding box min and max
                    min = new vec3(
                        Math.min(min.x(), tester.x()),
                        Math.min(min.y(), tester.y()),
                        Math.min(min.z(), tester.z())
                    );

                    max = new vec3(
                        Math.max(max.x(), tester.x()),
                        Math.max(max.y(), tester.y()),
                        Math.max(max.z(), tester.z())
                    );
                }
            }
        }

        this.bbox = new AABB(min, max);
    }

    @Override
    public boolean hit(ray r, interval rayT, hitRecord rec) {
        // Transform the ray from world space to object space.
        vec3 origin = new vec3(
            (cosTheta * r.getOrigin().x()) - (sinTheta * r.getOrigin().z()),
            r.getOrigin().y(),
            (sinTheta * r.getOrigin().x()) + (cosTheta * r.getOrigin().z())
        );

        vec3 direction = new vec3(
            (cosTheta * r.getDirection().x()) - (sinTheta * r.getDirection().z()),
            r.getDirection().y(),
            (sinTheta * r.getDirection().x()) + (cosTheta * r.getDirection().z())
        );

        ray rotatedRay = new ray(origin, direction, r.getTime());

        // Check for an intersection in object space.
        if (!object.hit(rotatedRay, rayT, rec)) {
            return false;
        }

        // Transform the intersection point from object space back to world space.
        rec.p = new vec3(
            (cosTheta * rec.p.x()) + (sinTheta * rec.p.z()),
            rec.p.y(),
            (-sinTheta * rec.p.x()) + (cosTheta * rec.p.z())
        );

        rec.normal = new vec3(
            (cosTheta * rec.normal.x()) + (sinTheta * rec.normal.z()),
            rec.normal.y(),
            (-sinTheta * rec.normal.x()) + (cosTheta * rec.normal.z())
        );

        return true;
    }

    @Override
    public AABB boundingBox() {
        return bbox;
    }}

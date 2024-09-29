public class AABB {
    public interval x, y, z;
    public AABB() {}

    public AABB(interval x, interval y, interval z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public AABB(vec3 a, vec3 b) {
        // Treat the two points a and b as extrema for the bounding box
        this.x = (a.x() <= b.x()) ? new interval(a.x(), b.x()) : new interval(b.x(), a.x());
        this.y = (a.y() <= b.y()) ? new interval(a.y(), b.y()) : new interval(b.y(), a.y());
        this.z = (a.z() <= b.z()) ? new interval(a.z(), b.z()) : new interval(b.z(), a.z());
    }

    public AABB(AABB box0, AABB box1) {
        this.x = new interval(box0.x, box1.x);
        this.y = new interval(box0.y, box1.y);
        this.z = new interval(box0.z, box1.z);
    }

    public interval getAxisInterval(int axis) {
        switch (axis) {
            case 1:
                return y;
            case 2:
                return z;
            default:
                return x;
        }
    }

    public boolean hit(ray r, interval rayT) {
        vec3 rayOrigin = r.getOrigin();
        vec3 rayDir = r.getDirection();
        for (int axis = 0; axis < 3; axis++) {
            interval axisInterval = getAxisInterval(axis);
            double adinv = 1.0 / rayDir.getEl(axis);

            double t0 = (axisInterval.min - rayOrigin.getEl(axis)) * adinv;
            double t1 = (axisInterval.max - rayOrigin.getEl(axis)) * adinv;

            if (t0 < t1) {
                rayT.min = Math.max(rayT.min, t0);
                rayT.max = Math.min(rayT.max, t1);
            } else {
                rayT.min = Math.max(rayT.min, t1);
                rayT.max = Math.min(rayT.max, t0);
            }

            if (rayT.max <= rayT.min)
                return false;
        }
        return true;
    }
}


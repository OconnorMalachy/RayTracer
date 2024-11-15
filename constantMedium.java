public class constantMedium implements hittable{
    private hittable boundary;
    private double negInvDensity;
    private material phaseFunction;
    
    public constantMedium(hittable boundary, double density, texture tex) {
        this.boundary = boundary;
        this.negInvDensity = -1.0 / density;
        this.phaseFunction = new isotropic(tex);
    }

    public constantMedium(hittable boundary, double density, color albedo) {
        this.boundary = boundary;
        this.negInvDensity = -1.0 / density;
        this.phaseFunction = new isotropic(albedo);
    }

    @Override
    public boolean hit(ray r, interval rayT, hitRecord rec) {
        hitRecord rec1 = new hitRecord();
        hitRecord rec2 = new hitRecord();

        if (!boundary.hit(r, interval.UNIVERSE, rec1)) {
            return false;
        }

        if (!boundary.hit(r, new interval(rec1.t + 0.0001, Double.POSITIVE_INFINITY), rec2)) {
            return false;
        }

        if (rec1.t < rayT.min) rec1.t = rayT.min;
        if (rec2.t > rayT.max) rec2.t = rayT.max;

        if (rec1.t >= rec2.t) {
            return false;
        }

        if (rec1.t < 0) {
            rec1.t = 0;
        }

        double rayLength = r.getDirection().len();
        double distanceInsideBoundary = (rec2.t - rec1.t) * rayLength;
        double hitDistance = negInvDensity * Math.log(Math.random());

        if (hitDistance > distanceInsideBoundary) {
            return false;
        }

        rec.t = rec1.t + hitDistance / rayLength;
        rec.p = r.at(rec.t);

        rec.normal = new vec3(1, 0, 0); // Arbitrary
        rec.frontFace = true;          // Also arbitrary
        rec.mat = phaseFunction;

        return true;
    }

    @Override
    public AABB boundingBox() {
        return boundary.boundingBox();
    }
}

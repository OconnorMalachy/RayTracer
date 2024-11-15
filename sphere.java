public class sphere implements hittable {
    private ray center;
    private double radius;
    private material mat;
    private AABB bbox;

    public sphere(vec3 c, double r, material m) {
        center = new ray(c, new vec3());
        radius = Math.max(0, r);
        mat = m;
        
        vec3 radiusVec = new vec3(radius, radius, radius);
        bbox = new AABB(vec3.subtract(c,radiusVec), vec3.add(c,radiusVec));
        System.out.println(bbox);
        System.out.println();
    }
    public sphere(vec3 c1, vec3 c2, double r, material m){
        center = new ray(c1, vec3.subtract(c1,c2));
        radius = Math.max(0,r);
        mat = m;

        vec3 radiusVec = new vec3(radius, radius, radius);
        AABB box1 = new AABB(vec3.subtract(center.at(0),radiusVec), vec3.add(center.at(0),radiusVec));
        AABB box2 = new AABB(vec3.subtract(center.at(1),radiusVec), vec3.add(center.at(1),radiusVec));
        bbox = new AABB(box1, box2);
    }
    public static void getSphereUV(vec3 p, hitRecord rec){
        double theta =  Math.acos(-p.y());
        double phi = Math.atan2(-p.z(), p.x()) + Math.PI;
      
        rec.u = phi / (2 * Math.PI);
        rec.v = theta / Math.PI;
    }
    @Override
    public AABB boundingBox(){return bbox;}
    @Override
    public boolean hit(ray r, interval rayT, hitRecord rec) {
        vec3 currCenter = center.at(r.getTime());
        vec3 oc = vec3.subtract(currCenter, r.getOrigin());
        double a = r.getDirection().lenSqr();
        double h = vec3.dot(r.getDirection(), oc);
        double c = oc.lenSqr() - radius * radius;

        double discriminant = h * h - a * c;
        if (discriminant < 0) {
            return false;
        }

        double sqrtd = Math.sqrt(discriminant);
        double root = (h - sqrtd) / a;

        if (!rayT.surrounds(root)) {
            root = (h + sqrtd) / a;
            if (!rayT.surrounds(root)) {
                return false;
            }
        }

        rec.t = root;
        rec.p = r.at(rec.t);
        vec3 outwardNormal = vec3.divide((vec3.subtract(rec.p, currCenter)),radius);
        rec.setFaceNormal(r,outwardNormal);
        getSphereUV(outwardNormal, rec);
        rec.mat = mat;
        return true;
    }

}


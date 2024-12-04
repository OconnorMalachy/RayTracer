import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public interface hittable {
    public boolean hit(ray r, interval rayT, hitRecord rec);
    public AABB boundingBox();
}
class hittableList implements hittable {
    public List<hittable> objects;
    public hittableList() {objects = new ArrayList<>();}
    private AABB bbox = AABB.EMPTY;
    public hittableList(hittable object) {
        this();
        add(object);
    }

    public void clear() {
        objects.clear();
    }

    public void add(hittable object) {
        objects.add(object);
        System.out.println(bbox);
        bbox = new AABB(bbox, object.boundingBox());
        System.out.println(bbox);
        System.out.println();
    }
    @Override
    public AABB boundingBox(){return bbox;}
    @Override
    public boolean hit(ray r, interval rayT, hitRecord rec) {
        hitRecord tempRec = new hitRecord();
        boolean hitAnything = false;
        double closestSoFar = rayT.max;

        for (hittable object : objects) {
            if (object.hit(r, new interval(rayT.min, closestSoFar), tempRec)) {
                hitAnything = true;
                closestSoFar = tempRec.t;
                rec.set(tempRec);
            }
        }

        return hitAnything;
    }
}
class translate implements hittable{
    private hittable object;
    private vec3 offset;
    private AABB bbox;
    public translate(hittable object, vec3 offset){
        this.object = object;
        this.offset = offset;
        bbox = AABB.add(object.boundingBox(), offset);
    }
    @Override
    public AABB boundingBox(){return bbox;}
    @Override
    public boolean hit(ray r, interval rayT, hitRecord rec){
        ray offsetR = new ray(vec3.subtract(r.getOrigin() , offset), r.getDirection(), r.getTime());
        if(!object.hit(offsetR, rayT, rec)){return false;}
        rec.p =  vec3.add(rec.p , offset);
        return true;
    }
}
class bvh implements hittable {
    private hittable left;
    private hittable right;
    private AABB bbox;

    // Constructor that accepts a HittableList
    public bvh(hittableList list) {
        this(list.objects, 0, list.objects.size(),0);
    }

    // Constructor that accepts a sublist of objects and indices
    public bvh(List<hittable> objects, int start, int end,  int depth) {
        // Create a bounding box for the current subset of objects
        bbox = AABB.EMPTY;
        for (int i = start; i < end; i++) {
            bbox = new AABB(bbox, objects.get(i).boundingBox());
        }

        int axis = bbox.longestAxis();

        
        // Choose comparator based on the axis
        Comparator<hittable> comparator = (axis == 0) ? boxXComparator
                                   : (axis == 1) ? boxYComparator
                                                 : boxZComparator;

        int objectSpan = end - start;
        /*
        System.out.println("Depth: " + depth + ", Object Span: " + objectSpan);
        System.out.println("Bounding Box: " + bbox);
        System.out.println("Objects in this BVH node:");
        */
        for (int i = start; i < end; i++) {
            System.out.println(" - " + objects.get(i));
        }
        System.out.println();
        if (objectSpan == 1) {
            left = right = objects.get(start);
        } else if (objectSpan == 2) {
            left = objects.get(start);
            right = objects.get(start + 1);
        } else {
            // Sort the objects based on the comparator for the current axis
            Collections.sort(objects.subList(start, end), comparator);
            int mid = start + objectSpan / 2;
            left = new bvh(objects, start, mid, depth+1);
            right = new bvh(objects, mid, end, depth+1);
        }

    }

    @Override
    public AABB boundingBox() {
        return bbox;
    }

    @Override
    public boolean hit(ray ray, interval rayT, hitRecord rec) {
        if (!bbox.hit(ray, rayT)) {
            return false;
        }

        interval leftRayT = new interval(rayT.min, rayT.max);
        boolean hitLeft = left.hit(ray, leftRayT, rec);

        interval rightRayT = new interval(rayT.min, hitLeft ? rec.t : rayT.max);
        boolean hitRight = right.hit(ray, rightRayT, rec);

        return hitLeft || hitRight;
    }

    // Comparators for sorting based on bounding box axis intervals
    private static final Comparator<hittable> boxXComparator = (a, b) -> boxCompare(a, b, 0);
    private static final Comparator<hittable> boxYComparator = (a, b) -> boxCompare(a, b, 1);
    private static final Comparator<hittable> boxZComparator = (a, b) -> boxCompare(a, b, 2);

    private static int boxCompare(hittable a, hittable b, int axisIndex) {
        double aMin = a.boundingBox().axisInterval(axisIndex).min;
        double bMin = b.boundingBox().axisInterval(axisIndex).min;
        return Double.compare(aMin, bMin);
    }
}
class sphere implements hittable {
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
        //System.out.println(bbox);
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
class constantMedium implements hittable{
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
class rotateY implements hittable{
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
    }
}
class quad implements hittable{
    private vec3 Q, u, v;
    private material mat;
    private AABB bbox;
    private vec3 normal;
    private double D;
    private vec3 w;
    
    public static hittableList box(vec3 a, vec3 b, material mat){
        hittableList sides = new hittableList();
        vec3 min = new vec3(Math.min(a.x(), b.x()), Math.min(a.y(), b.y()), Math.min(a.z(), b.z()));
        vec3 max = new vec3(Math.max(a.x(), b.x()), Math.max(a.y(), b.y()), Math.max(a.z(), b.z()));

        vec3 dx = new vec3(max.x() - min.x(), 0, 0);
        vec3 dy = new vec3(0, max.y() - min.y(), 0);
        vec3 dz = new vec3(0, 0, max.z() - min.z());   

        sides.add(new quad(new vec3(min.x(), min.y(), max.z()), dx, dy, mat));  
        sides.add(new quad(new vec3(max.x(), min.y(), max.z()), dz.neg(), dy, mat));
        sides.add(new quad(new vec3(max.x(), min.y(), min.z()), dx.neg(), dy, mat));
        sides.add(new quad(new vec3(min.x(), min.y(), min.z()), dz, dy, mat)); 
        sides.add(new quad(new vec3(min.x(), max.y(), max.z()), dx, dz.neg(), mat));
        sides.add(new quad(new vec3(min.x(), min.y(), min.z()), dx, dz, mat));

        return sides;
    }
    quad(vec3 Q, vec3 u, vec3 v, material mat){
        this.Q = Q;
        this.u = u;
        this.v = v;
        this.mat = mat;

        vec3 n = vec3.cross(u,v);
        normal = vec3.unitVector(n);
        D = vec3.dot(normal,Q);
        w = vec3.divide(n , vec3.dot(n,n));
        setBoundingBox();
    }
    public void setBoundingBox(){
        AABB bboxDiagonal1 = new AABB(Q, vec3.add(vec3.add(Q,u),v));
        AABB bboxDiagonal2 = new AABB(vec3.add(Q,u),vec3.add(Q,v));
        bbox = new AABB(bboxDiagonal1, bboxDiagonal2);
    }
    @Override
    public AABB boundingBox(){return bbox;}
    @Override
    public boolean hit(ray r, interval rayT, hitRecord rec){
        double denom = vec3.dot(normal, r.getDirection());
        if(Math.abs(denom) < 1e-8){return false;}
        
        double t = (D - vec3.dot(normal, r.getOrigin())) / denom;
        if(!rayT.contains(t)){return false;}
        
        vec3 intersection = r.at(t);
        vec3 planarHitptVector = vec3.subtract(intersection,Q);
        double alpha = vec3.dot(w,vec3.cross(planarHitptVector,v));
        double beta = vec3.dot(w,vec3.cross(u, planarHitptVector));
        
        if(!isInterior(alpha,beta,rec)){return false;}
        rec.t = t;
        rec.p = intersection;
        rec.mat = mat;
        rec.setFaceNormal(r,normal);

        return true;
    }
    public boolean isInterior(double a, double b, hitRecord rec){
        interval unitInterval = new interval(0,1);
        if(!unitInterval.contains(a) || !unitInterval.contains(b)){return false;}
        rec.u = a;
        rec.v = b;
        return true;
    }
}



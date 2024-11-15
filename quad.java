public class quad implements hittable{
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

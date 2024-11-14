public class hitRecord {
    public vec3 p;
    public vec3 normal;
    public double t;
    public double u;
    public double v;
    public boolean frontFace;
    public material mat;

    public void setFaceNormal(ray r, vec3 outwardNormal){
        frontFace = vec3.dot(r.getDirection(), outwardNormal) < 0;
        normal = frontFace ? outwardNormal : outwardNormal.neg();
    }
    public void set(hitRecord h){
        p =  h.p;
        normal = h.normal;
        t = h.t;
        frontFace = h.frontFace;
        mat = h.mat;
        
    }
}

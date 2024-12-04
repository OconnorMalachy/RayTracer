public class AABB{
    public interval x,y,z;
    public static final AABB EMPTY = new AABB(interval.EMPTY, interval.EMPTY, interval.EMPTY);
    public static final AABB UNIVERSE = new AABB(interval.UNIVERSE, interval.UNIVERSE, interval.UNIVERSE);
    public AABB(){}
    public AABB(interval x, interval y, interval z){
        this.x = x;
        this.y = y;
        this.z = z;
        padToMinimums();
    }
    public AABB(vec3 pA, vec3 pB){
        double[] a = {pA.getEl(0), pA.getEl(1), pA.getEl(2)};
        double[] b = {pB.getEl(0), pB.getEl(1), pB.getEl(2)};
        this.x = (a[0]<b[0]) ? new interval(a[0], b[0]) : new interval(b[0],a[0]);
        this.y = (a[1]<b[1]) ? new interval(a[1], b[1]) : new interval(b[1],a[1]);
        this.z = (a[2]<b[2]) ? new interval(a[2], b[2]) : new interval(b[2],a[2]);
        padToMinimums();
    }
    public AABB(AABB box0, AABB box1){
        this.x = new interval(box0.x, box1.x);
        this.y = new interval(box0.y, box1.y);
        this.z = new interval(box0.z, box1.z);
    }
    public int longestAxis() {
        double xSize = x.size();
        double ySize = y.size();
        double zSize = z.size();

        if (xSize > ySize) {
            return (xSize > zSize) ? 0 : 2;
        } else {
            return (ySize > zSize) ? 1 : 2;
        }
    }
    public interval axisInterval(int n){
        if(n == 1){return y;}
        if(n == 2){return z;}
        return x;
    }
    public static AABB add(AABB bbox, vec3 offset){
        return new AABB(interval.add(bbox.x,offset.x()),interval.add(bbox.y , offset.y()),interval.add(bbox.z , offset.z()));
    }    
    public boolean hit(ray r, interval rayT){
        vec3 rayOrigin = r.getOrigin();
        vec3 rayDir = r.getDirection();
        
        for(int axis = 0; axis < 3; axis++){
            interval ax = axisInterval(axis);
            double adInv = 1.0/rayDir.getEl(axis);
            
            double t0 = (ax.min - rayOrigin.getEl(axis)) * adInv;
            double t1 = (ax.max - rayOrigin.getEl(axis)) * adInv;

            if (t0 < t1) {
                if (t0 > rayT.min) rayT.min = t0;
                if (t1 < rayT.max) rayT.max = t1;
            } else {
                if (t1 > rayT.min) rayT.min = t1;
                if (t0 < rayT.max) rayT.max = t0;
            }
            
            if(rayT.max <= rayT.min){return false;}
        }
        return true;
    }
    private void padToMinimums(){
        double delta = 0.0001;
        if (x.size() < delta) x = x.expand(delta);
        if (y.size() < delta) y = y.expand(delta);
        if (z.size() < delta) z = z.expand(delta);    
    }
    public String toString(){
        return "X: " + x + "Y: " + y + "Z: " +z;
    } 
}

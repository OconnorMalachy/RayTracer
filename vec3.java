public class vec3
{
    double[] e = new double[3];
    public vec3(){
        e[0] = 0.0;
        e[1] = 0.0;
        e[2] = 0.0;
    }
    public vec3(double e0, double e1, double e2){
        e[0] = e0;
        e[1] = e1;
        e[2] = e2;
    }
    public double x(){return e[0];}
    public double y(){return e[1];}
    public double z(){return e[2];}
    public vec3 neg(){return new vec3(-e[0],-e[1],-e[2]);}
    public double getEl(int i){return e[i];}
    public void setEl(int i, double val){e[i] = val;}

    public void add(vec3 v){
        e[0] += v.getEl(0);
        e[1] += v.getEl(1);
        e[2] += v.getEl(2);
    }
    public void mult(double t){
        e[0] *= t;
        e[1] *= t;
        e[2] *= t;
    }
    public void div(double t){mult(1/t);}
    public double len(){return Math.sqrt(lenSqr());}
    public double lenSqr(){
        return e[0] * e[0] + e[1] * e[1]  + e[2] * e[2];
    }
    public boolean nearZero(){
        double s = 1e-8;
        return (Math.abs(e[0]) < s) && (Math.abs(e[1]) < s) && (Math.abs(e[2]) < s);
    }
    // Utilities
    public String toString(){
        return String.format("%f %f %f",e[0],e[1],e[2]);
    }
    public static vec3 random(){
        return new vec3(constants.randomDouble(), constants.randomDouble(), constants.randomDouble());
    }
    public static vec3 random(double min, double max){
        return new vec3(constants.randomDouble(min,max), constants.randomDouble(min,max), constants.randomDouble(min,max));

    }
    public static vec3 add(vec3 u, vec3 v) {
        return new vec3(u.e[0] + v.e[0], u.e[1] + v.e[1], u.e[2] + v.e[2]);
    }

    public static vec3 subtract(vec3 u, vec3 v) {
        return new vec3(u.e[0] - v.e[0], u.e[1] - v.e[1], u.e[2] - v.e[2]);
    }

    public static vec3 multiply(vec3 u, vec3 v) {
        return new vec3(u.e[0] * v.e[0], u.e[1] * v.e[1], u.e[2] * v.e[2]);
    }

    public static vec3 multiply(double t, vec3 v) {
        return new vec3(t * v.e[0], t * v.e[1], t * v.e[2]);
    }

    public static vec3 divide(vec3 v, double t) {
        return multiply(1 / t, v);
    }

    public static double dot(vec3 u, vec3 v) {
        return u.e[0] * v.e[0] + u.e[1] * v.e[1] + u.e[2] * v.e[2];
    }

    public static vec3 cross(vec3 u, vec3 v) {
        return new vec3(u.e[1] * v.e[2] - u.e[2] * v.e[1],
                        u.e[2] * v.e[0] - u.e[0] * v.e[2],
                        u.e[0] * v.e[1] - u.e[1] * v.e[0]);
    }

    public static vec3 unitVector(vec3 v) {
        return divide(v,v.len());
    }
    public static vec3 randomInUnitDisk(){
        while(true){
            vec3 p = new vec3(constants.randomDouble(-1,1), constants.randomDouble(-1,1),0);
            if(p.lenSqr() < 1){return p;}
        } 
    }
    public static vec3 randomUnitVector(){
        while(true){
            vec3 p = vec3.random(-1,1);
            double lenSq = p.lenSqr();
            if(1e-160 < lenSq && lenSq <= 1){
                return vec3.divide(p, Math.sqrt(lenSq));
            }
        }
    }
    public static vec3 randomOnHemisphere(vec3 normal){
        vec3 onUnitSphere = randomUnitVector();
        if(dot(onUnitSphere, normal) > 0.0){return onUnitSphere;}
        return onUnitSphere.neg();
    }
    public static vec3 reflect(vec3 v, vec3 n){
        return subtract(v,multiply(2*dot(v,n), n));
    }
    public static vec3 refract(vec3 uv, vec3 n, double etaiOverEtat) {
        double cosTheta = Math.min(dot(uv.neg(), n), 1.0);
        vec3 rOutPerp = multiply(etaiOverEtat, add(uv, multiply(cosTheta, n)));
        vec3 rOutParallel = multiply(-Math.sqrt(Math.abs(1.0 - rOutPerp.lenSqr())), n);
        return vec3.add(rOutPerp, rOutParallel);
    }

}

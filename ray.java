public class ray {
    private vec3 origin;
    private vec3 direction;
    private double tm;
    public ray(vec3 or, vec3 dir, double time) {
        origin = or;
        direction = dir;
        tm = time;
    }
    public ray(vec3 or, vec3 dir){this(or,dir,0.0);}
    public ray(){
        origin = new vec3();
        direction = new vec3();
    }
    public double getTime() {return tm;}
    public vec3 getOrigin() {return origin;}
    public vec3 getDirection() {return direction;}
    public vec3 at(double t) {
        return vec3.add(origin,vec3.multiply(t,direction));
    }
    public void set(vec3 o, vec3 dir, double t){
        origin = o;
        direction = dir;
        tm = t;
    }
    public void set(vec3 o, vec3 dir){this.set(o,dir, 0.0);}
}


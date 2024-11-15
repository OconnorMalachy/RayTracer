public abstract class material{
    public color emitted(double u, double v, vec3 p){
        return new color(0,0,0);
    }
    public boolean scatter(ray rIn, hitRecord rec, color attenuation, ray scattered){
        return false;
    }

}

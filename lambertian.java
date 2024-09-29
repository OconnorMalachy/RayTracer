public class lambertian extends material{
    private color albedo;
    public lambertian(color alb){albedo = alb;}
    @Override
    public boolean scatter(ray rIn, hitRecord rec, color attenuation, ray scattered){
        vec3 scatterDirection = vec3.add(rec.normal, vec3.randomUnitVector());
        if(scatterDirection.nearZero()){scatterDirection = rec.normal;}
        scattered.set(rec.p, scatterDirection, rIn.getTime());
        attenuation.set(albedo);
        return true;
    }
}

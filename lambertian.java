public class lambertian extends material{
    private texture tex;
    public lambertian(color alb){tex = new solidColor(alb);}
    public lambertian(texture tex){this.tex = tex;}
    @Override
    public boolean scatter(ray rIn, hitRecord rec, color attenuation, ray scattered){
        vec3 scatterDirection = vec3.add(rec.normal, vec3.randomUnitVector());
        if(scatterDirection.nearZero()){scatterDirection = rec.normal;}
        scattered.set(rec.p, scatterDirection, rIn.getTime());
        attenuation.set(tex.value(rec.u,rec.v,rec.p));
        return true;
    }
}

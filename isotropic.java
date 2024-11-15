public class isotropic extends material{
    private texture tex;
    public isotropic(color albedo){tex = new solidColor(albedo);}
    public isotropic(texture tex){this.tex = tex;}
    
    @Override
    public boolean scatter(ray rIn, hitRecord rec, color attenuation, ray scattered){
        scattered = new ray(rec.p, vec3.randomUnitVector(),rIn.getTime());
        attenuation = tex.value(rec.u, rec.v, rec.p);
        return true;
    }
}

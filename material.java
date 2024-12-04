public abstract class material{
    public color emitted(double u, double v, vec3 p){
        return new color(0,0,0);
    }
    public boolean scatter(ray rIn, hitRecord rec, color attenuation, ray scattered){
        return false;
    }

}
class metal extends material{
    private color albedo;
    private double fuzz;
    public metal(color alb,double fuzziness){
        fuzz = (fuzziness < 1 ? fuzziness: 1);
        albedo = alb;
    }
    @Override
    public boolean scatter(ray rIn, hitRecord rec, color attenuation, ray scattered){
        vec3 reflected = vec3.reflect(rIn.getDirection(), rec.normal);
        reflected = vec3.add(vec3.unitVector(reflected),vec3.multiply(fuzz, vec3.randomUnitVector()));
        scattered.set(rec.p, reflected, rIn.getTime());
        attenuation.set(albedo);
        return (vec3.dot(scattered.getDirection(),rec.normal) > 0);
    }
}
class lambertian extends material{
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
class isotropic extends material{
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
class dielectric extends material{
    private double refractionIndex;

    public dielectric(double rI){refractionIndex = rI;}
    @Override
    public boolean scatter(ray rIn, hitRecord rec, color attenuation, ray scattered){
        attenuation.set(new color(1.0,1.0,1.0));
        double rI = rec.frontFace ? (1.0/refractionIndex) : refractionIndex;
        
        vec3 unitDirection = vec3.unitVector(rIn.getDirection());
        double cosTheta = Math.min(vec3.dot(unitDirection.neg(),rec.normal),1.0);
        double sinTheta = Math.sqrt(1.0 - cosTheta*cosTheta);
        
        boolean canRefract = rI * sinTheta <= 1.0;
        vec3 direction = new vec3();

        if(!canRefract || reflectance(cosTheta, rI) > constants.randomDouble()){direction = vec3.reflect(unitDirection, rec.normal);}
        else{direction = vec3.refract(unitDirection, rec.normal, rI);}
        
        scattered.set(rec.p, direction,rIn.getTime());
        return true;
    }
    private static double reflectance(double cos, double indexOfRefraction){
        double r0 = (1-indexOfRefraction) / (1+indexOfRefraction);
        r0 *= r0;
        return r0 + (1-r0)*Math.pow((1-cos),5);
    }
}
class diffuseLight extends material{
    private texture tex;
    diffuseLight(texture tex){this.tex = tex;}
    diffuseLight(color emit){this.tex = new solidColor(emit);}
    
    @Override
    public color emitted(double u, double v, vec3 p){
        return tex.value(u,v,p);
    }
}




public class dielectric extends material{
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



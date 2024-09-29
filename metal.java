public class metal extends material{
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


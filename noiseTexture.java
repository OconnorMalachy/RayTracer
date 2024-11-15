public class noiseTexture extends texture{
    private perlin noise = new perlin();
    private double scale;
    noiseTexture(double scale){this.scale = scale;}
    @Override
    public color value(double u, double v, vec3 p){
        //double noiseValue = (1.0 + noise.noise(vec3.multiply(scale,p)));
        color col = new color(0.5,0.5,0.5);
        col.mult((1 + Math.sin(scale * p.z() + 10 * noise.turb(p,7))));
        return col;
        //vec3 t = vec3.multiply(0.5, col);
        //return new color(t.x(), t.y(), t.z());
    }
}

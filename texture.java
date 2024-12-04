public abstract class texture {
    public abstract color value(double u, double v, vec3 p);
}
class solidColor extends texture {
    private final color albedo;

    public solidColor(color albedo) {
        this.albedo = albedo;
    }

    public solidColor(double red, double green, double blue) {
        this(new color(red, green, blue));
    }

    @Override
    public color value(double u, double v, vec3 p) {
        return albedo;
    }
}
class checkerTexture extends texture{
    private double invScale;
    private texture even;
    private texture odd;
    checkerTexture(double scale, texture even, texture odd){
        this.invScale = 1.0/scale;
        this.even = even;
        this.odd = odd;
    }
    checkerTexture(double scale, color c1, color c2){
        this(scale, new solidColor(c1), new solidColor(c2));
    }
    @Override
    public color value(double u, double v, vec3 p){
        
        int x = (int) Math.floor(invScale * p.x());
        int y = (int) Math.floor(invScale * p.y());
        int z = (int) Math.floor(invScale * p.z());    
        
        boolean isEven = (x+y+z) % 2 == 0;
        return isEven ? even.value(u,v,p) : odd.value(u,v,p);
    } 
}
class noiseTexture extends texture{
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
 class imageTexture extends texture{
    private rtwImage image;

    public imageTexture(String filename) {
        image = new rtwImage(filename);
    }
    @Override
    public color value(double u, double v, vec3 p){
        if(image.getHeight()<= 0){return new color(0,1,1);}
        u = new interval(0,1).clamp(u);
        v = 1.0 - new interval(0,1).clamp(v);
        int i = (int) (u * image.getWidth());
        int j = (int) (v * image.getHeight());
        // Get the pixel color data from the image
        float[] pixel = image.getPixelData(i, j);
        // Normalize the RGB values to the range [0, 1]
        float colorScale = 1.0f / 255.0f;
        return new color(colorScale * pixel[0], colorScale * pixel[1], colorScale * pixel[2]);
    }
}



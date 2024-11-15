public class checkerTexture extends texture{
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

public class diffuseLight extends material{
    private texture tex;
    diffuseLight(texture tex){this.tex = tex;}
    diffuseLight(color emit){this.tex = new solidColor(emit);}
    
    @Override
    public color emitted(double u, double v, vec3 p){
        return tex.value(u,v,p);
    }
}

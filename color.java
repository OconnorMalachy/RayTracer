import java.io.*;

public class color extends vec3{
    public color(){super();}
    public color(double r, double g, double b){super(r,g,b);}
    public static double linearToGamma(double linearComponent){
        if(linearComponent > 0){return Math.sqrt(linearComponent);}
        return 0.0;
    } 
    public static void writeColor(PrintStream out, color pixelColor){
        double r = pixelColor.x();
        double g = pixelColor.y();
        double b = pixelColor.z();
        
        r = linearToGamma(r);
        b = linearToGamma(b);
        g = linearToGamma(g);
      
        final interval intensity = new interval(0.000, 0.999);
        int rbyte = (int) (constants.u8int * intensity.clamp(r));
        int gbyte = (int) (constants.u8int * intensity.clamp(g));
        int bbyte = (int) (constants.u8int * intensity.clamp(b));

        out.println(String.format("%d %d %d",rbyte,gbyte,bbyte));
    }
    public static color vecToCol(vec3 v){return new color(v.x(),v.y(),v.z());}
    public void set(color c){
        e[0] = c.x();
        e[1] = c.y();
        e[2] = c.z();
    }
}

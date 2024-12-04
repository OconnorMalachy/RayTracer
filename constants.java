import java.util.Random;

/*
This is a collection of constants and simple conversions / utilities.
Most are implemented internally in Java so this file exists soley for organizational purposes.
*/
public class constants{
    public static final int u8int = 256;
    public static final double pi = 3.1415926535897932385;
    public static final double infinity = Double.MAX_VALUE;

    public static double degToRads(double degrees){return degrees * pi/180.0;}
    public static double randomDouble(){
        Random r = new Random();
        return r.nextDouble();
    }
    public static double randomDouble(double min, double max){return min + (max-min)*(randomDouble());}
    public static int randomInt(int min, int max){return (int)(randomDouble(min,max+1));}
}


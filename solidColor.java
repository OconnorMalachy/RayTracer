public class solidColor extends texture {
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

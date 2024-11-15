public class imageTexture extends texture{
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


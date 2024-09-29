public class interval{
    public double min, max;
    public interval(){
        min = constants.infinity;
        max = -constants.infinity;
    }
    public interval(double minimum, double maximum){
        min = minimum;
        max = maximum;
    }
    public interval(interval a, interval b) {
        this.min = Math.min(a.min, b.min);
        this.max = Math.max(a.max, b.max);
    }
    public interval expand(double delta) {
        double padding = delta / 2;
        return new interval(min - padding, max + padding);
    }
    public double size(){return max - min;}
    public boolean contatins(double x){return min <=x && x <= max;}
    public boolean surrounds(double x){return min < x && x < max;}
    public double clamp(double x){
        if(x < min){return min;}
        if(x > max){return max;}
        return x;
    }
    public static final interval EMPTY = new interval(constants.infinity, -constants.infinity);
    public static final interval UNIVERSE = new interval(-constants.infinity, constants.infinity);
}

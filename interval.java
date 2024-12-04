public class interval {
    public double min, max; // The minimum and maximum values defining the interval

    // Default constructor initializes the interval to a large range (-∞, +∞)
    public interval() {
        min = constants.infinity;
        max = -constants.infinity;
    }

    // Constructor to define the interval with given minimum and maximum values
    public interval(double minimum, double maximum) {
        min = minimum;
        max = maximum;
    }

    // Constructor to create a new interval that combines two intervals (the union of both)
    public interval(interval a, interval b) {
        // The new interval will have the smallest minimum and the largest maximum from both intervals
        min = a.min <= b.min ? a.min : b.min;
        max = a.max >= b.max ? a.max : b.max;    
    }

    // Expands the interval by a given delta (padding the range on both sides)
    public interval expand(double delta) {
        double padding = delta / 2; // Split the delta equally on both sides
        return new interval(min - padding, max + padding); // Return the expanded interval
    }

    // Returns the size (length) of the interval (difference between max and min)
    public double size() {
        return max - min;
    }

    // Checks if a value x is within the interval (inclusive of the bounds)
    public boolean contains(double x) {
        return min <= x && x <= max;
    }

    // Checks if a value x is strictly between the bounds of the interval (exclusive of the bounds)
    public boolean surrounds(double x) {
        return min < x && x < max;
    }

    // Clamps a value x to be within the interval, returning min or max if x is outside the bounds
    public double clamp(double x) {
        if (x < min) {
            return min; // Return min if x is smaller than min
        }
        if (x > max) {
            return max; // Return max if x is larger than max
        }
        return x; // Return x if it is within the interval
    }

    // Constant representing an empty interval (-∞, +∞)
    public static final interval EMPTY = new interval(constants.infinity, -constants.infinity);
    // Constant representing the entire real line (-∞, +∞)
    public static final interval UNIVERSE = new interval(-constants.infinity, constants.infinity);

    // Provides a string representation of the interval
    public String toString() {
        return "MIN: " + min + " MAX: " + max;
    }

    // Adds a displacement to an interval, shifting both min and max by the same amount
    public static interval add(interval ival, double displacement) {
        return new interval(ival.min + displacement, ival.max + displacement);
    }

    // Another version of the add method, to allow the displacement to come first
    public static interval add(double displacement, interval ival) {
        return add(ival, displacement);
    }
}

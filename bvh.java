import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class bvh implements hittable {
    private hittable left;
    private hittable right;
    private AABB bbox;

    // Constructor that accepts a HittableList
    public bvh(hittableList list) {
        this(list.objects, 0, list.objects.size(),0);
    }

    // Constructor that accepts a sublist of objects and indices
    public bvh(List<hittable> objects, int start, int end,  int depth) {
        // Create a bounding box for the current subset of objects
        bbox = AABB.EMPTY;
        for (int i = start; i < end; i++) {
            bbox = new AABB(bbox, objects.get(i).boundingBox());
        }

        int axis = bbox.longestAxis();

        
        // Choose comparator based on the axis
        Comparator<hittable> comparator = (axis == 0) ? boxXComparator
                                   : (axis == 1) ? boxYComparator
                                                 : boxZComparator;

        int objectSpan = end - start;
        
        System.out.println("Depth: " + depth + ", Object Span: " + objectSpan);
        System.out.println("Bounding Box: " + bbox);
        System.out.println("Objects in this BVH node:");
        for (int i = start; i < end; i++) {
            System.out.println(" - " + objects.get(i));
        }
        System.out.println();
        if (objectSpan == 1) {
            left = right = objects.get(start);
        } else if (objectSpan == 2) {
            left = objects.get(start);
            right = objects.get(start + 1);
        } else {
            // Sort the objects based on the comparator for the current axis
            Collections.sort(objects.subList(start, end), comparator);
            int mid = start + objectSpan / 2;
            left = new bvh(objects, start, mid, depth+1);
            right = new bvh(objects, mid, end, depth+1);
        }

    }

    @Override
    public AABB boundingBox() {
        return bbox;
    }

    @Override
    public boolean hit(ray ray, interval rayT, hitRecord rec) {
        if (!bbox.hit(ray, rayT)) {
            return false;
        }

        interval leftRayT = new interval(rayT.min, rayT.max);
        boolean hitLeft = left.hit(ray, leftRayT, rec);

        interval rightRayT = new interval(rayT.min, hitLeft ? rec.t : rayT.max);
        boolean hitRight = right.hit(ray, rightRayT, rec);

        return hitLeft || hitRight;
    }

    // Comparators for sorting based on bounding box axis intervals
    private static final Comparator<hittable> boxXComparator = (a, b) -> boxCompare(a, b, 0);
    private static final Comparator<hittable> boxYComparator = (a, b) -> boxCompare(a, b, 1);
    private static final Comparator<hittable> boxZComparator = (a, b) -> boxCompare(a, b, 2);

    private static int boxCompare(hittable a, hittable b, int axisIndex) {
        double aMin = a.boundingBox().axisInterval(axisIndex).min;
        double bMin = b.boundingBox().axisInterval(axisIndex).min;
        return Double.compare(aMin, bMin);
    }
}


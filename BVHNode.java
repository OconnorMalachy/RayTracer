import java.util.List;
import java.util.Collections;

public class BVHNode implements hittable {
    private hittable left, right;
    private AABB bbox;

    public BVHNode(hittableList list) {
        this(list.objects, 0, list.objects.size());
    }
    public hittable getLeft(){return left;}
    public hittable getRight(){return right;}
    public BVHNode(List<hittable> objects, int start, int end) {
        int axis = constants.randomInt(0, 2);
        int objectSpan = end - start;
        if (objectSpan == 1) {
            left = right = objects.get(start);
        } else if (objectSpan == 2) {
            left = objects.get(start);
            right = objects.get(start + 1);
        } else {
            Collections.sort(objects.subList(start, end), (a, b) -> boxCompare(a, b, axis));

            int mid = start + (end - start) / 2;
            left = new BVHNode(objects, start, mid);
            right = new BVHNode(objects, mid, end);
        }

        bbox = new AABB(left.boundingBox(), right.boundingBox());
    }

    @Override
    public boolean hit(ray r, interval rayT, hitRecord rec) {
        if (!bbox.hit(r, rayT))
            return false;

        boolean hitLeft = left.hit(r, rayT, rec);
        boolean hitRight = right.hit(r, new interval(rayT.min, hitLeft ? rec.t : rayT.max), rec);
        return hitLeft || hitRight;
    }

    @Override
    public AABB boundingBox() {
        return bbox;
    }

    private static int boxCompare(hittable a, hittable b, int axis) {
        interval aInterval = a.boundingBox().getAxisInterval(axis);
        interval bInterval = b.boundingBox().getAxisInterval(axis);
        return Double.compare(aInterval.min, bInterval.min);
    }
    public String toString(){
        interval xInt = bbox.getAxisInterval(0);
        interval yInt = bbox.getAxisInterval(1);
        interval zInt = bbox.getAxisInterval(2);
        return String.format("x [%f,%f]\ny [%f,%f]\nz [%f,%f]",xInt.min,xInt.max,yInt.min,yInt.max,zInt.min,zInt.max);
    }
}


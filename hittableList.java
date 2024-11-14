import java.util.ArrayList;
import java.util.List;

public class hittableList implements hittable {
    public List<hittable> objects;
    public hittableList() {objects = new ArrayList<>();}
    private AABB bbox = AABB.EMPTY;
    public hittableList(hittable object) {
        this();
        add(object);
    }

    public void clear() {
        objects.clear();
    }

    public void add(hittable object) {
        objects.add(object);
        System.out.println(bbox);
        bbox = new AABB(bbox, object.boundingBox());
        System.out.println(bbox);
        System.out.println();
    }
    @Override
    public AABB boundingBox(){return bbox;}
    @Override
    public boolean hit(ray r, interval rayT, hitRecord rec) {
        hitRecord tempRec = new hitRecord();
        boolean hitAnything = false;
        double closestSoFar = rayT.max;

        for (hittable object : objects) {
            if (object.hit(r, new interval(rayT.min, closestSoFar), tempRec)) {
                hitAnything = true;
                closestSoFar = tempRec.t;
                rec.set(tempRec);
            }
        }

        return hitAnything;
    }
}



public interface hittable {
    public boolean hit(ray r, interval rayT, hitRecord rec);
    public AABB boundingBox();
}

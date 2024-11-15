public class translate implements hittable{
    private hittable object;
    private vec3 offset;
    private AABB bbox;
    public translate(hittable object, vec3 offset){
        this.object = object;
        this.offset = offset;
        bbox = AABB.add(object.boundingBox(), offset);
    }
    @Override
    public AABB boundingBox(){return bbox;}
    @Override
    public boolean hit(ray r, interval rayT, hitRecord rec){
        ray offsetR = new ray(vec3.subtract(r.getOrigin() , offset), r.getDirection(), r.getTime());
        if(!object.hit(offsetR, rayT, rec)){return false;}
        rec.p =  vec3.add(rec.p , offset);
        return true;
    }
}

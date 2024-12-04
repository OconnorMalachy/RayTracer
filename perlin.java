public class perlin{
    private static int pointCount = 256;
    private vec3[] randVec = new vec3[pointCount];
    private int[] permX = new int[pointCount];
    private int[] permY = new int[pointCount];
    private int[] permZ = new int[pointCount];


    private static void perlinGeneratePerm(int[] p){
        for(int i = 0; i < pointCount; i++){
            p[i] = i;
        }
        permute(p,pointCount);
    }
    private static void permute(int[] p, int n){
        for(int i = n-1; i > 0; i--){
            int target = constants.randomInt(0,i);
            int tmp = p[i];
            p[i] = p[target];
            p[target] = tmp;
        }
    }
    perlin(){
        for(int i = 0; i < pointCount; i++){
            randVec[i] = vec3.unitVector(vec3.random(-1.0,1.0)); 
        }
        perlinGeneratePerm(permX);
        perlinGeneratePerm(permY);
        perlinGeneratePerm(permZ);
    }
    public double noise(vec3 p){
        double u = p.x() - Math.floor(p.x());
        double v = p.y() - Math.floor(p.y());
        double w = p.z() - Math.floor(p.z());

        int i = (int) Math.floor(p.x());
        int j = (int) Math.floor(p.y());
        int k = (int) Math.floor(p.z());

        vec3[][][] c = new vec3[2][2][2];

        for (int di = 0; di < 2; di++) {
            for (int dj = 0; dj < 2; dj++) {
                for (int dk = 0; dk < 2; dk++) {
                    c[di][dj][dk] = randVec[
                        permX[(i + di) & 255] ^
                        permY[(j + dj) & 255] ^
                        permZ[(k + dk) & 255]
                    ];
                }
            }
        }

        return perlinInterp(c, u, v, w);
    }
    public double turb(vec3 p, int depth){
        double accum = 0.0;
        vec3 tempP = p;
        double weight = 1.0;

        for(int i = 0; i < depth; i++){
            accum += weight * noise(tempP);
            weight *= 0.5;
            tempP.mult(2);
        }
        return Math.abs(accum);
    }
    public static double perlinInterp(vec3[][][] c, double u, double v, double w) {
        double uu = u * u * (3 - 2 * u);
        double vv = v * v * (3 - 2 * v);
        double ww = w * w * (3 - 2 * w);
        double accum = 0.0;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    vec3 weightV = new vec3(u - i, v - j, w - k);
                    accum += (i * uu + (1 - i) * (1 - uu)) 
                           * (j * vv + (1 - j) * (1 - vv)) 
                           * (k * ww + (1 - k) * (1 - ww)) 
                           * vec3.dot(c[i][j][k],weightV);
                }
            }
        }

        return accum;
    }

}

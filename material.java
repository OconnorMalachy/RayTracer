// Abstract base class for all materials
public abstract class material {
    // Default emitted light (no emission, black color)
    public color emitted(double u, double v, vec3 p) {
        return new color(0, 0, 0); // No light emitted
    }

    // Method to scatter the incoming ray. Returns false by default (no scattering)
    public boolean scatter(ray rIn, hitRecord rec, color attenuation, ray scattered) {
        return false; // No scattering behavior by default
    }
}

// Metal material, reflects light with fuzziness
class metal extends material {
    private color albedo; // Color of the metal
    private double fuzz;  // Fuzziness that controls the blur of the reflection

    // Constructor to initialize the metal with albedo color and fuzziness
    public metal(color alb, double fuzziness) {
        fuzz = (fuzziness < 1 ? fuzziness : 1); // Clamp fuzziness to be <= 1
        albedo = alb; // Set the albedo color
    }

    // Scatter method for metal material: reflects rays with fuzziness
    @Override
    public boolean scatter(ray rIn, hitRecord rec, color attenuation, ray scattered) {
        // Reflect the incoming ray using the normal of the surface
        vec3 reflected = vec3.reflect(rIn.getDirection(), rec.normal);
        // Add fuzziness by slightly perturbing the reflection direction
        reflected = vec3.add(vec3.unitVector(reflected), vec3.multiply(fuzz, vec3.randomUnitVector()));
        scattered.set(rec.p, reflected, rIn.getTime()); // Set the scattered ray
        attenuation.set(albedo); // Set the attenuation to the albedo color
        return (vec3.dot(scattered.getDirection(), rec.normal) > 0); // Check if the scattered ray goes in the correct direction
    }
}

// Lambertian material: diffuse scattering (matte surface)
class lambertian extends material {
    private texture tex; // The texture used for the material

    // Constructor with a solid color as the texture
    public lambertian(color alb) {
        tex = new solidColor(alb);
    }

    // Constructor with a custom texture
    public lambertian(texture tex) {
        this.tex = tex;
    }

    // Scatter method for lambertian material: scatter light uniformly based on the surface normal
    @Override
    public boolean scatter(ray rIn, hitRecord rec, color attenuation, ray scattered) {
        // Randomly perturb the normal direction to scatter the light
        vec3 scatterDirection = vec3.add(rec.normal, vec3.randomUnitVector());
        // Ensure the scatter direction is not near zero
        if (scatterDirection.nearZero()) { scatterDirection = rec.normal; }
        scattered.set(rec.p, scatterDirection, rIn.getTime()); // Set the scattered ray
        attenuation.set(tex.value(rec.u, rec.v, rec.p)); // Set the attenuation color from the texture
        return true; // Lambertian materials always scatter light
    }
}

// Isotropic material: scatters light uniformly in all directions (like a frosted glass)
class isotropic extends material {
    private texture tex; // Texture for the material

    // Constructor with a solid color as the texture
    public isotropic(color albedo) {
        tex = new solidColor(albedo);
    }

    // Constructor with a custom texture
    public isotropic(texture tex) {
        this.tex = tex;
    }

    // Scatter method for isotropic material: scatter in a random direction
    @Override
    public boolean scatter(ray rIn, hitRecord rec, color attenuation, ray scattered) {
        // Scatter light in a random direction
        scattered = new ray(rec.p, vec3.randomUnitVector(), rIn.getTime());
        attenuation = tex.value(rec.u, rec.v, rec.p); // Set attenuation based on the texture
        return true; // Isotropic materials always scatter light
    }
}

// Dielectric material: transparent material like glass or water, supports refraction and reflection
class dielectric extends material {
    private double refractionIndex; // The index of refraction for the material

    // Constructor to set the refraction index
    public dielectric(double rI) {
        refractionIndex = rI;
    }

    // Scatter method for dielectric material: refracts or reflects based on the incident angle
    @Override
    public boolean scatter(ray rIn, hitRecord rec, color attenuation, ray scattered) {
        attenuation.set(new color(1.0, 1.0, 1.0)); // Dielectrics are transparent, so the attenuation is always white (no absorption)
        
        // Determine if we need to use the refractive index based on the side of the surface (front/back face)
        double rI = rec.frontFace ? (1.0 / refractionIndex) : refractionIndex;
        
        vec3 unitDirection = vec3.unitVector(rIn.getDirection()); // Get unit direction of the incoming ray
        double cosTheta = Math.min(vec3.dot(unitDirection.neg(), rec.normal), 1.0); // Calculate cosine of the angle between the ray and normal
        double sinTheta = Math.sqrt(1.0 - cosTheta * cosTheta); // Calculate sine of the angle
        
        boolean canRefract = rI * sinTheta <= 1.0; // Check if refraction is possible based on Snell's law
        vec3 direction = new vec3(); // Direction of the scattered ray

        // If refraction is not possible or the reflection is more likely, reflect the ray
        if (!canRefract || reflectance(cosTheta, rI) > constants.randomDouble()) {
            direction = vec3.reflect(unitDirection, rec.normal); // Reflect the ray
        } else {
            direction = vec3.refract(unitDirection, rec.normal, rI); // Refract the ray
        }

        scattered.set(rec.p, direction, rIn.getTime()); // Set the scattered ray
        return true; // Dielectrics always scatter rays (either refract or reflect)
    }

    // Reflectance calculation based on the incident angle and the refraction index
    private static double reflectance(double cos, double indexOfRefraction) {
        double r0 = (1 - indexOfRefraction) / (1 + indexOfRefraction);
        r0 *= r0; // Reflectance at normal incidence
        return r0 + (1 - r0) * Math.pow((1 - cos), 5); // Schlick's approximation for reflectance
    }
}

// Diffuse light-emitting material (light source)
class diffuseLight extends material {
    private texture tex; // Texture for the emitted light

    // Constructor with a texture for emission
    diffuseLight(texture tex) {
        this.tex = tex;
    }

    // Constructor with a color for emission, creates a solid color texture
    diffuseLight(color emit) {
        this.tex = new solidColor(emit);
    }

    // Returns the emitted color for the light source material
    @Override
    public color emitted(double u, double v, vec3 p) {
        return tex.value(u, v, p); // Return the emission color based on the texture
    }
}

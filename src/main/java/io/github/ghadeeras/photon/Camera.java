package io.github.ghadeeras.photon;

import io.github.ghadeeras.photon.imaging.Image;
import io.github.ghadeeras.photon.structs.Range;

public final class Camera {

    public final Sensor sensor;
    public final Lens lens;
    public final double focalDistance;
    public final double aperture;
    public final double exposure;

    private final Lens.Focuser focuser;
    private final Sampler<Double> timeSampler;

    public Camera(Sensor sensor, Lens lens, double focalDistance, double aperture, double exposure) {
        this.sensor = sensor;
        this.lens = lens;
        this.focalDistance = focalDistance;
        this.aperture = aperture;
        this.exposure = exposure;

        this.focuser = lens.focuser(aperture, focalDistance);
        this.timeSampler = exposure > 0 ? Range.of(Math.exp(-exposure), 1).sampler()
            .map(Math::log)
            .caching(0x10000) : () -> 0D;
    }

    public Image render(World world, double time, boolean withOptimization) {
        final var optimizedWorld = withOptimization ? world.optimized(time - exposure, time) : world;
        return sensor.render(samplePos -> optimizedWorld.trace(focuser.ray(samplePos, before(time))));
    }

    private double before(double time) {
        return time + timeSampler.next();
    }

}

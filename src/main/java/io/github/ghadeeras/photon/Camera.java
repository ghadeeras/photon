package io.github.ghadeeras.photon;

import io.github.ghadeeras.photon.imaging.Image;

public record Camera(Sensor sensor, Lens lens) {

    public Image render(World world) {
        return sensor.render(samplePos -> world.trace(lens.ray(samplePos)));
    }

}

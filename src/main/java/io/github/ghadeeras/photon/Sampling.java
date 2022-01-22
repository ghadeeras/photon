package io.github.ghadeeras.photon;

import io.github.ghadeeras.photon.structs.Vector;

public interface Sampling {

    Vector samplePos(int sample);

    int samplesPerPixel();

}

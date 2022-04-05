package io.github.ghadeeras.photon.things;

import io.github.ghadeeras.photon.geometries.BoundingBox;
import io.github.ghadeeras.photon.geometries.CompositeSurface;
import io.github.ghadeeras.photon.geometries.GeometricSurface;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Range;
import io.github.ghadeeras.photon.structs.Ray;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public record ThingsTree(Thing thing1, Thing thing2, BoundingBox boundingVolume) implements Thing {

    public static ThingsTree of(Thing thing1, Thing thing2, double time1, double time2) {
        var boundingVolume = thing1.surface().boundingVolume(time1, time2).enclose(thing2.surface().boundingVolume(time1, time2));
        return new ThingsTree(thing1, thing2, boundingVolume);
    }

    @Override
    public Incident incident(Ray ray, double min, double max) {
        return boundingVolume.potentialHitRange(ray, min, max) instanceof Range.Bounded range ?
            thing1 != thing2 && thing1.incident(ray, range.min(), range.max()) instanceof Incident.Hit hit1 ?
                thing2.incident(ray, range.min(), hit1.distance()) instanceof Incident.Hit hit2 ?
                    hit2 :
                    hit1 :
                thing2.incident(ray, range.min(), range.max()) :
            Incident.miss;
    }

    @Override
    public GeometricSurface surface() {
        return CompositeSurface.of(thing1.surface(), thing2.surface());
    }

    @Override
    public List<Thing> flatten() {
        return Stream.of(thing1, thing2)
            .flatMap(thing -> thing.flatten().stream())
            .collect(toList());
    }

}

package io.github.ghadeeras.photon;

import io.github.ghadeeras.photon.structs.*;
import io.github.ghadeeras.photon.things.Thing;

import java.util.function.Function;

public record World(Thing thing, Function<Vector, Color> background, int depth) {

    public Color trace(Ray ray) {
        return trace(ray.unit(), Color.colorWhite, depth);
    }

    private Color trace(Ray ray, Color color, int depth) {
        if (depth == 0) {
            return Color.colorBlack;
        }
        var incident = ray.incidentOn(thing, 0.001, Double.POSITIVE_INFINITY);
        return incident instanceof Incident.Hit hit ?
            colorOf(hit, color, depth) :
            color.mul(background.apply(ray.direction().unit()));
    }

    private Color colorOf(Incident.Hit hit, Color color, int depth) {
        Effect effect = hit.thing().material().effectOf(hit);
        var newColor = color.mul(effect.color());
        return effect instanceof Effect.Redirection redirection ?
            trace(Ray.of(hit.ray().time(), hit.point().position(), redirection.vector()), newColor, depth - 1) :
            newColor;
    }

    public World optimized(double time1, double time2) {
        return new World(thing.optimized(time1, time2), background, depth);
    }

}

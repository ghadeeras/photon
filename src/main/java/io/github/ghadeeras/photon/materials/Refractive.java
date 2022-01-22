package io.github.ghadeeras.photon.materials;

import io.github.ghadeeras.photon.Material;
import io.github.ghadeeras.photon.RND;
import io.github.ghadeeras.photon.structs.Color;
import io.github.ghadeeras.photon.structs.Effect;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.function.DoubleUnaryOperator;

public record Refractive(double index, Color color, double fuzziness, DoubleUnaryOperator reflectance) implements Material {

    public static Refractive of(double index, Color color) {
        return of(index, color, 0);
    }

    public static Refractive of(double index, Color color, double fuzziness) {
        return new Refractive(index, color, fuzziness, schlickReflectance(index));
    }

    public static Refractive of(double index, Color color, double fuzziness, DoubleUnaryOperator reflectance) {
        return new Refractive(index, color, fuzziness, reflectance);
    }

    @Override
    public Effect effectOf(Incident.Hit hit) {
        var normal = fuzziness != 0 ?
            hit.normal().plus(RND.randomVectorInSphere(0, fuzziness)).unit():
            hit.normal();

        var redirection = redirection(hit.ray().direction(), normal);

        return Effect.redirectionOf(color, redirection);
    }

    private Vector redirection(Vector incident, Vector normal) {
        var cosAngle = Math.min(Math.max(normal.dot(incident), -1), 1);
        var reciprocatedIndex = cosAngle < 0 ? 1 / this.index : this.index;
        var perpendicularIncident = normal.scale(cosAngle);
        var parallelIncident = incident.minus(perpendicularIncident);
        var parallelRefraction = parallelIncident.scale(reciprocatedIndex);
        var parallelRefractionLengthSquared = parallelRefraction.lengthSquared();
        if (parallelRefractionLengthSquared < 1 && RND.anyUnsigned() >= reflectance.applyAsDouble(cosAngle)) {
            var perpendicularRefractionLengthSquared = 1 - parallelRefractionLengthSquared;
            var perpendicularRefraction = normal.scale(Math.sqrt(perpendicularRefractionLengthSquared));
            return cosAngle < 0 ?
                parallelRefraction.minus(perpendicularRefraction) :
                parallelRefraction.plus(perpendicularRefraction);
        } else {
            return incident.minus(normal.scale(2 * cosAngle));
        }
    }

    public static DoubleUnaryOperator schlickReflectance(double index) {
        var i = (index - 1) / (index + 1);
        var i2 = i * i;
        return cosAngle -> i2 + (1 - i2) * Math.pow(1 - Math.abs(cosAngle), 5);
    }

}

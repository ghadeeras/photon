package io.github.ghadeeras.photon.materials;

import io.github.ghadeeras.photon.sampling.Sampler;
import io.github.ghadeeras.photon.structs.Color;
import io.github.ghadeeras.photon.structs.Effect;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.function.DoubleUnaryOperator;

import static io.github.ghadeeras.photon.sampling.Samplers.sphere;
import static io.github.ghadeeras.photon.sampling.Samplers.unsigned;

public record Refractive(double index, Color color, Sampler<Vector>  fuzzinessSampler, DoubleUnaryOperator reflectance) implements Material {

    private static final Sampler<Double> unsignedScalars = unsigned().caching(0x10000);

    public static Refractive of(double index, Color color) {
        return of(index, color, 0);
    }

    public static Refractive of(double index, Color color, double fuzziness) {
        return new Refractive(index, color, fuzzinessSampler(fuzziness), schlickReflectance(index));
    }

    public static Refractive of(double index, Color color, double fuzziness, DoubleUnaryOperator reflectance) {
        return new Refractive(index, color, fuzzinessSampler(fuzziness), reflectance);
    }

    private static Sampler<Vector> fuzzinessSampler(double fuzziness) {
        return fuzziness != 0 ? sphere().map(v -> v.scale(fuzziness)).caching(0x10000) : null;
    }

    @Override
    public Effect effectOf(Incident.Hit hit) {
        var normal = fuzzinessSampler != null ?
            hit.point().sampleArea().unit().plus(fuzzinessSampler.next()).unit():
            hit.point().sampleArea().unit();

        var redirection = redirection(hit.ray().direction(), normal);

        return Effect.redirectionOf(color, redirection);
    }

    private Vector redirection(Vector incident, Vector normal) {
        var incidentPerpendicularComponentValue = normal.dot(incident);
        var reciprocatedIndex = incidentPerpendicularComponentValue < 0 ? 1 / this.index : this.index;
        var incidentPerpendicularComponent = normal.scale(incidentPerpendicularComponentValue);
        var incidentParallelComponent = incident.minus(incidentPerpendicularComponent);
        var refractionParallelComponent = incidentParallelComponent.scale(reciprocatedIndex);
        var refractionParallelComponentLengthSquared = refractionParallelComponent.lengthSquared();
        var refractionPerpendicularComponentLengthSquared = refractionPerpendicularComponentLengthSquared(refractionParallelComponentLengthSquared, incidentPerpendicularComponentValue, incident);
        if (refractionPerpendicularComponentLengthSquared >= 0) {
            var refractionPerpendicularComponent = normal.scale(Math.sqrt(refractionPerpendicularComponentLengthSquared));
            return incidentPerpendicularComponentValue < 0 ?
                refractionParallelComponent.minus(refractionPerpendicularComponent) :
                refractionParallelComponent.plus(refractionPerpendicularComponent);
        } else {
            return incident.minus(incidentPerpendicularComponent.scale(2));
        }
    }

    private double refractionPerpendicularComponentLengthSquared(double refractionParallelComponentLengthSquared, double incidentPerpendicularComponentValue, Vector incident) {
        var incidentOrRefractionLengthSquared = incident.lengthSquared();
        var refractionPerpendicularComponentLengthSquared = incidentOrRefractionLengthSquared - refractionParallelComponentLengthSquared;
        if (refractionPerpendicularComponentLengthSquared >= 0) {
            var cosAngle = incidentPerpendicularComponentValue / Math.sqrt(incidentOrRefractionLengthSquared);
            return unsignedScalars.next() >= reflectance.applyAsDouble(cosAngle) ? refractionPerpendicularComponentLengthSquared : -1;
        }
        return refractionPerpendicularComponentLengthSquared;
    }

    public static DoubleUnaryOperator schlickReflectance(double index) {
        var i = (index - 1) / (index + 1);
        var i2 = i * i;
        return cosAngle -> i2 + (1 - i2) * Math.pow(1 - Math.abs(cosAngle), 5);
    }

}

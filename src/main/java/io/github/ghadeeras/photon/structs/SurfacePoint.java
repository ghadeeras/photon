package io.github.ghadeeras.photon.structs;

public record SurfacePoint(Vector position, Vector normal) {

    public static SurfacePoint of(Vector position, Vector normal) {
        return new SurfacePoint(position, normal);
    }

    public SurfacePoint transform(Vector translation) {
        return new SurfacePoint(position.plus(translation), normal);
    }

    public SurfacePoint transform(Matrix matrix) {
        return transform(matrix, matrix.antiMatrix());
    }

    public SurfacePoint transform(Matrix matrix, Vector translation) {
        return transform(matrix).transform(translation);
    }

    public SurfacePoint transform(Matrix matrix, Matrix antiMatrix) {
        return new SurfacePoint(matrix.mul(position), antiMatrix.mul(normal));
    }

}

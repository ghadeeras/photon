package io.github.ghadeeras.photon.misc;

import java.util.function.Supplier;

public class LazyValue<T> implements Supplier<T> {

    private final Supplier<T> supplier;

    private T value;

    public LazyValue(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        return value != null
            ? value
            : (value = supplier.get());
    }

}

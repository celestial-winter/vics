package com.infinityworks.common.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * Additions to lists
 */
public final class ListExtras {
    public static boolean isNullOrEmpty(List<?> underTest) {
        return underTest == null || underTest.isEmpty();
    }

    public static boolean noneNull(Collection<?> underTest) {
        return underTest != null && underTest.stream().allMatch(o -> o != null);
    }

    /**
     * Executes the supplied function n times and returns the results
     *
     * @param supplier the operation to invoke
     * @param n        the number of times to invoke the operation
     * @param <T>      the return type
     * @return a collection with the results of the invocations
     */
    public static <T> List<T> times(Supplier<T> supplier, int n) {
        List<T> elements = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            elements.add(supplier.get());
        }
        return elements;
    }

    private ListExtras() {
        throw new UnsupportedOperationException("Do not instantiate");
    }
}

package com.infinityworks.common.lang;

public final class StringExtras {

    public static boolean isNullOrEmpty(String underTest) {
        return underTest == null || underTest.isEmpty();
    }

    private StringExtras() {
        throw new UnsupportedOperationException("Do not instantiate");
    }

    /**
     * Added because {@link String#valueOf} produces "null" string for null values
     */
    public static String nullToEmpty(Object o) {
        if (o == null) {
            return "";
        } else {
            return String.valueOf(o);
        }
    }
}

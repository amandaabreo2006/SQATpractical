package com.sqat.orangehrm.data;

import java.util.List;

/**
 * Generic 7-point Boundary Value Analysis generator for a closed range [min, max]:
 * min-1, min, min+1, nominal, max-1, max, max+1.
 */
public final class BoundaryValues {

    private BoundaryValues() {
    }

    /** One boundary test point. */
    public record Point(String label, int value, boolean valid) {
    }

    public static List<Point> sevenPoint(int min, int max) {
        int nominal = (min + max) / 2;
        return List.of(
                new Point("min-1", min - 1, false),
                new Point("min", min, true),
                new Point("min+1", min + 1, true),
                new Point("nominal", nominal, true),
                new Point("max-1", max - 1, true),
                new Point("max", max, true),
                new Point("max+1", max + 1, false));
    }
}

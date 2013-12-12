package org.jsilib.model;

/**
 *
 * @author Kirk
 */
public abstract class Filters {

    public static int[] getMediaFilter() {
        int mask[] = {1, 1, 1,
            1, 1, 1,
            1, 1, 1};
        return mask;
    }

    public static int[] getLaplacianoRealceBorda() {
        int mask[] = {-1, -1, -1,
            -1, 8, -1,
            -1, -1, -1};
        return mask;
    }

    public static int[] getLaplaciano() {
        int mask[] = {0, -1, 0,
            -1, 4, -1,
            0, -1, 0};
        return mask;
    }

    public static int[] getSobelH() {
        int mask[] = {-1, -2, -1,
            0, 0, 0,
            1, 2, 1};
        return mask;
    }

    public static int[] getSobelV() {
        int mask[] = {-1, 0, 1,
            -2, 0, 2,
            -1, 0, 1};
        return mask;
    }

    public static int[] getPrewitH() {
        int mask[] = {-1, -1, -1,
            0, 0, 0,
            1, 1, 1};
        return mask;
    }

    public static int[] getPrewitV() {
        int mask[] = {-1, 0, 1,
            -1, 0, 1,
            -1, 0, 1};
        return mask;
    }

}

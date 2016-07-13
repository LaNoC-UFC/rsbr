package util;

public class Range {

    private int numberOfDimensions = 0;
    private int[] min;
    private int[] max;

    public static Range OneDimensionalRange(int min, int max) {
        assert min <= max;
        return new Range(1, new int[]{min}, new int[]{max});
    }

    public static Range TwoDimensionalRange(int xMin, int xMax, int yMin, int yMax) {
        return new Range(2, new int[]{xMin, yMin}, new int[]{xMax, yMax});
    }

    private Range(int dimensions, int[] min, int[] max) {
        this.numberOfDimensions = dimensions;
        this.min = min;
        this.max = max;
    }

    public int dimensions() {
        return numberOfDimensions;
    }

    public int min(int dimension) {
        assert dimension < dimensions();
        return this.min[dimension];
    }

    public int max(int dimension) {
        assert dimension < dimensions();
        return this.max[dimension];
    }
}

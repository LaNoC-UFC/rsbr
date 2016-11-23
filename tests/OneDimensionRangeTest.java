import org.junit.*;
import util.*;

public class OneDimensionRangeTest {

    @Test
    public void Contains() throws Exception {
        Range range1 = Range.OneDimensionalRange(1,4);
        Assert.assertTrue(range1.contains(1));
        Assert.assertTrue(range1.contains(3));
    }

    @Test
    public void NotContains() throws Exception {
        Range range1 = Range.OneDimensionalRange(1,4);
        Assert.assertFalse(range1.contains(0));
        Assert.assertFalse(range1.contains(5));
    }

    @Test
    public void Equals() throws Exception {
        Range range1 = Range.OneDimensionalRange(0, 20);
        Range range2 = Range.OneDimensionalRange(0, 20);
        Assert.assertEquals(range1, range2);
    }

    @Test
    public void NotEquals() throws Exception {
        Range range1 = Range.OneDimensionalRange(0, 20);
        Range range2 = Range.OneDimensionalRange(10, 20);
        Assert.assertNotEquals(range1, range2);
    }

    @Test
    public void Includes() throws Exception {
        Range range1 = Range.OneDimensionalRange(10, 50);
        Range range2 = Range.OneDimensionalRange(10, 30);
        Assert.assertTrue(range1.includes(range2));
    }

    @Test
    public void NotIncludes() throws Exception {
        Range range1 = Range.OneDimensionalRange(10, 50);
        Range range2 = Range.OneDimensionalRange(10, 30);
        Assert.assertFalse(range2.includes(range1));
    }

    @Test
    public void OverlapsBefore() throws Exception {
        Range range1 = Range.OneDimensionalRange(5, 10);
        Range range2 = Range.OneDimensionalRange(2, 7);
        Assert.assertTrue(range1.overlaps(range2));
    }

    @Test
    public void OverlapsAfter() throws Exception {
        Range range1 = Range.OneDimensionalRange(5, 10);
        Range range2 = Range.OneDimensionalRange(2, 7);
        Assert.assertTrue(range2.overlaps(range1));
    }

    @Test
    public void OverlapsMiddle() throws Exception {
        Range range1 = Range.OneDimensionalRange(5, 10);
        Range range2 = Range.OneDimensionalRange(7, 8);
        Assert.assertTrue(range1.overlaps(range2));
    }

    @Test
    public void OverlapsEquals() throws Exception {
        Range range1 = Range.OneDimensionalRange(5, 10);
        Range range2 = Range.OneDimensionalRange(5, 10);
        Assert.assertTrue(range1.overlaps(range2));
    }

    @Test
    public void NotOverlaps() throws Exception {
        Range range1 = Range.OneDimensionalRange(5, 10);
        Range range2 = Range.OneDimensionalRange(0, 3);
        Assert.assertFalse(range1.overlaps(range2));
    }

    @Test
    public void Abuts() throws Exception {
        Range range1 = Range.OneDimensionalRange(0,4);
        Range range2 = Range.OneDimensionalRange(5,6);
        Assert.assertTrue(range1.abuts(range2));
    }

    @Test
    public void NotAbuts() throws Exception {
        Range range1 = Range.OneDimensionalRange(0,4);
        Range range2 = Range.OneDimensionalRange(4,6);
        Assert.assertFalse(range1.abuts(range2));
    }

    @Test
    public void isContiguous() throws Exception {
        Range range1 = Range.OneDimensionalRange(0,4);
        Range range2 = Range.OneDimensionalRange(5,6);
        Assert.assertTrue(range1.isContiguous(range2));
    }

    @Test
    public void Combination() throws Exception {
        Range range1 = Range.OneDimensionalRange(0, 20);
        Range range2 = Range.OneDimensionalRange(21, 40);
        Range range3 = Range.OneDimensionalRange(0, 40);
        Range combination = range1.combination(range2);
        Assert.assertEquals(combination, range3);
    }

    @Test
    public void MinGreaterThanMaxIsEmpty() throws Exception {
        Range range = Range.OneDimensionalRange(1, 0);
        Assert.assertEquals(Range.ONEDIMENSIONEMPTY, range);
    }
}

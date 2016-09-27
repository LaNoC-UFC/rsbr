import org.junit.Assert;
import org.junit.Test;
import util.Range;

public class TwoDimensionRangeTest {

    @Test
    public void Contains() throws Exception {
        Range range1 = Range.TwoDimensionalRange(0, 20, 0, 20);
        Assert.assertTrue(range1.contains(0, 0));
        Assert.assertTrue(range1.contains(20, 20));
        Assert.assertTrue(range1.contains(10, 10));
    }

    @Test
    public void NotContains() throws Exception {
        Range range1 = Range.TwoDimensionalRange(0, 20, 0, 20);
        Assert.assertFalse(range1.contains(0, 21));
        Assert.assertFalse(range1.contains(21, 20));
    }

    @Test
    public void Equals() throws Exception {
        Range range1 = Range.TwoDimensionalRange(0, 5, 0, 6);
        Range range2 = Range.TwoDimensionalRange(0, 5, 0, 6);
        Assert.assertEquals(range1, range2);
    }

    @Test
    public void NotEquals() throws Exception {
        Range range1 = Range.TwoDimensionalRange(0, 5, 0, 6);
        Range range2 = Range.TwoDimensionalRange(3, 8, 3, 9);
        Assert.assertNotEquals(range1, range2);
    }

    @Test
    public void Includes() throws Exception {
        Range range1 = Range.TwoDimensionalRange(0, 20, 0, 20);
        Range range2 = Range.TwoDimensionalRange(10, 20, 10, 20);
        Assert.assertTrue(range1.includes(range2));
    }

    @Test
    public void NotIncludes() throws Exception {
        Range range1 = Range.TwoDimensionalRange(0, 20, 0, 20);
        Range range2 = Range.TwoDimensionalRange(10, 20, 10, 20);
        Assert.assertFalse(range2.includes(range1));
    }

    @Test
    public void Overlaps() throws Exception {
        Range range1 = Range.TwoDimensionalRange(0 ,20, 10, 20);
        Range range2 = Range.TwoDimensionalRange(18, 30, 5, 15);
        Assert.assertTrue(range1.overlaps(range2));
    }

    @Test
    public void OverlapsWhenEquals() throws Exception {
        Range range1 = Range.TwoDimensionalRange(0 ,20, 10, 20);
        Range range2 = Range.TwoDimensionalRange(0, 20, 10, 20);
        Assert.assertTrue(range1.overlaps(range2));
    }

    @Test
    public void OverlapsWhenSideBySide() throws Exception {
        Range range1 = Range.TwoDimensionalRange(0 ,20, 10, 20);
        Range range2 = Range.TwoDimensionalRange(20, 40, 0, 20);
        Assert.assertTrue(range1.overlaps(range2));
    }

    @Test
    public void NotOverlaps() throws Exception {
        Range range1 = Range.TwoDimensionalRange(10 ,20, 10, 20);
        Range range2 = Range.TwoDimensionalRange(10, 20, 0, 5);
        Assert.assertFalse(range1.overlaps(range2));
    }

    @Test
    public void AbutsRight() throws Exception {
        Range range1 = Range.TwoDimensionalRange(50, 80, 50, 80);
        Range range2 = Range.TwoDimensionalRange(81, 150, 50 , 80);
        Assert.assertTrue(range1.abuts(range2));
    }

    @Test
    public void AbutsUp() throws Exception {
        Range range1 = Range.TwoDimensionalRange(50, 80, 50, 80);
        Range range2 = Range.TwoDimensionalRange(50, 80, 20 , 49);
        Assert.assertTrue(range1.abuts(range2));
    }

    @Test
    public void AbutsLeft() throws Exception {
        Range range1 = Range.TwoDimensionalRange(50, 80, 50, 80);
        Range range2 = Range.TwoDimensionalRange(20, 49, 50 , 80);
        Assert.assertTrue(range1.abuts(range2));
    }

    @Test
    public void AbutsDown() throws Exception {
        Range range1 = Range.TwoDimensionalRange(50, 80, 50, 80);
        Range range2 = Range.TwoDimensionalRange(50, 80, 81 , 120);
        Assert.assertTrue(range1.abuts(range2));
    }

    @Test
    public void NotAbuts() throws Exception {
        Range range1 = Range.TwoDimensionalRange(50, 80, 50, 80);
        Range range2 = Range.TwoDimensionalRange(90, 120, 50 , 80);
        Assert.assertFalse(range1.abuts(range2));
    }

    @Test
    public void isContiguousRight() throws Exception {
        Range range1 = Range.TwoDimensionalRange(0, 20, 10, 20);
        Range range2 = Range.TwoDimensionalRange(21, 40, 10, 20);
        Assert.assertTrue(range1.isContiguous(range2));

    }

    @Test
    public void isContiguousUp() throws Exception {
        Range range1 = Range.TwoDimensionalRange(0, 20, 10, 20);
        Range range2 = Range.TwoDimensionalRange(21, 40, 10, 20);
        Assert.assertTrue(range1.isContiguous(range2));

    }

    @Test
    public void isCombination() throws Exception {
        Range range1 = Range.TwoDimensionalRange(0, 20, 0, 20);
        Range range2 = Range.TwoDimensionalRange(21, 41, 0, 20);
        Range range3 = Range.TwoDimensionalRange(0, 41, 0, 20);
        Range combination = range1.combination(range2);
        Assert.assertEquals(combination, range3);
    }

    @Test
    public void CombinationWhenNotContiguous() throws Exception {
        Range range1 = Range.TwoDimensionalRange(0, 20, 0, 20);
        Range range2 = Range.TwoDimensionalRange(22, 41, 0, 20);
        Range combination = range1.combination(range2);
        Assert.assertEquals(combination, Range.EMPTY);
    }
}

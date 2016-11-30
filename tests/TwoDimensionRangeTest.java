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
    public void SideBySideRangesDoAbut() throws Exception {
        Range subject = Range.TwoDimensionalRange(10, 20, 0, 10);
        Range left = Range.TwoDimensionalRange(21, 30, 0, 10);
        Range up = Range.TwoDimensionalRange(10, 20, 11, 20);
        Assert.assertTrue(subject.abuts(left));
        Assert.assertTrue(left.abuts(subject));
        Assert.assertTrue(subject.abuts(up));
        Assert.assertTrue(up.abuts(subject));
    }

    @Test
    public void OverlappedRangesDoNotAbut() throws Exception {
        Range subject = Range.TwoDimensionalRange(10, 20, 0, 10);
        Range left = Range.TwoDimensionalRange(20, 30, 0, 10);
        Range up = Range.TwoDimensionalRange(10, 20, 10, 20);
        Assert.assertFalse(subject.abuts(left));
        Assert.assertFalse(left.abuts(subject));
        Assert.assertFalse(subject.abuts(up));
        Assert.assertFalse(up.abuts(subject));
    }

    // Reproduces bug #97
    @Test
    public void DistantRangesDoNotAbut() throws Exception {
        Range subject = Range.TwoDimensionalRange(10, 20, 0, 10);
        Range left = Range.TwoDimensionalRange(30, 40, 0, 10);
        Range up = Range.TwoDimensionalRange(10, 20, 20, 30);
        Range itsUpperLeft = Range.TwoDimensionalRange(21, 31, 11, 21);
        Assert.assertFalse(subject.abuts(left));
        Assert.assertFalse(left.abuts(subject));
        Assert.assertFalse(subject.abuts(up));
        Assert.assertFalse(up.abuts(subject));
        Assert.assertFalse(subject.abuts(itsUpperLeft));
        Assert.assertFalse(itsUpperLeft.abuts(subject));
    }

    @Test
    public void UnalignedTouchingRangesDoAbut() throws Exception {
        Range subject = Range.TwoDimensionalRange(10, 20, 0, 10);
        Range left = Range.TwoDimensionalRange(21, 30, 5, 15);
        Assert.assertTrue(subject.abuts(left));
        Assert.assertTrue(left.abuts(subject));
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
        Assert.assertEquals(combination, Range.TWODIMENSIONEMPTY);
    }

    @Test
    public void MinGreaterThanMaxIsEmpty() throws Exception {
        Range emptyX = Range.TwoDimensionalRange(1, 0, 0, 0);
        Range emptyY = Range.TwoDimensionalRange(0, 0, 1, 0);
        Assert.assertEquals(Range.TWODIMENSIONEMPTY, emptyX);
        Assert.assertEquals(Range.TWODIMENSIONEMPTY, emptyY);
    }
}

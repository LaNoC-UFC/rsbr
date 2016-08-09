import org.junit.*;
import sbr.SR;
import util.*;

public class FaultyGraphSegmentationTest {
    @Test
    public void faulty3x3Graph() throws Exception {
        for (int i = 0; i < 100; i++) {
            Graph noc = RandomFaultyGraphBuilder.generateGraph(3, 3, 1);
            SR sr = new SR(noc);
            sr.computeSegments();
            sr.setrestrictions();
            Assert.assertEquals(3, sr.segments().size());
            Assert.assertEquals(3, sr.restrictions().size());
            Assert.assertEquals(1, sr.startVertices().size());
            Assert.assertTrue(1 >= sr.terminalVertices().size());
        }
    }

    @Ignore
    public void faulty3x3Graph2() throws Exception {
        for (int i = 0; i < 10; i++) {
            Graph noc = RandomFaultyGraphBuilder.generateGraph(3, 3, 2);
            SR sr = new SR(noc);
            sr.computeSegments();
            sr.setrestrictions();
            Assert.assertEquals(2, sr.segments().size());
            Assert.assertEquals(2, sr.restrictions().size());
            Assert.assertEquals(1, sr.startVertices().size());
            Assert.assertTrue(2 >= sr.terminalVertices().size());
        }
    }
}

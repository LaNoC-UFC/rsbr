import org.junit.*;
import java.util.*;

import util.*;
import sbr.*;

public class FaultyGraphSegmentationTest {
    @Test
    public void faulty3x3Graph() throws Exception {
        for (int numberOfFaults = 0; numberOfFaults <= maxOfFaultyLinks(3, 3); numberOfFaults++) {
            for (int i = 0; i < 1000; i++) {
                Graph noc = RandomFaultyGraphBuilder.generateGraph(3, 3, numberOfFaults);
                SR sr = new SR(noc);
                sr.computeSegments();
                sr.setrestrictions();
                validateEdges(noc, sr.segments());
            }
        }
    }

    @Test
    public void faultyGraph() throws Exception {
        for (int x = 2; x < 8; x++) {
            for (int y = 2; y < 8; y++) {
                for (int numberOfFaults = 0; numberOfFaults <= maxOfFaultyLinks(x, y); numberOfFaults++) {
                    Graph noc = RandomFaultyGraphBuilder.generateGraph(x, y, numberOfFaults);
                    SR sr = new SR(noc);
                    sr.computeSegments();
                    sr.setrestrictions();
                    validateEdges(noc, sr.segments());
                }
            }
        }
    }

    private void validateEdges(Graph noc, Collection<Segment> segments) {
        for (Edge candidate : noc.getEdges()) {
            if (isBridge(noc, candidate))
                Assert.assertFalse(isInASegment(segments, candidate));
            else
                Assert.assertTrue(isInASegment(segments, candidate));
        }
    }

    private boolean isInASegment(Collection<Segment> segments, Edge candidate) {
        int count = 0;
        for (Segment seg : segments)
            if (seg.getLinks().contains(candidate) || seg.getLinks().contains(sibling(candidate)))
                count++;
        return count == 1;
    }

    private boolean isBridge(Graph graph, Edge candidate) {
        List<Edge> bridges = new Bridge(graph).bridges();
        return bridges.contains(candidate) || bridges.contains(sibling(candidate));
    }

    private Edge sibling(Edge one) {
        return one.destination().edge(one.source());
    }

    private int maxOfFaultyLinks(int x, int y) {
        int minOfLinks = x*y - 1;
        int totalOfLinks = (x - 1)*y + (y - 1)*x;
        return totalOfLinks - minOfLinks;
    }
}

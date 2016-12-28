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
                SR sr = new SR(noc, new BidimensionalSBRPolicy(noc));
                sr.computeSegments();
                sr.setrestrictions();
                edgesAreEitherInASegmentOrAreBridges(noc, sr.segments());
                verticesAreEitherInASegmentOrAreAloneInASubnet(noc, sr);
                //onlyTerminalVerticesAreLinkedToBridges(noc, sr);
            }
        }
    }

    @Test
    public void faultyGraph() throws Exception {
        for (int numberOfRows = 2; numberOfRows < 8; numberOfRows++) {
            for (int numberOfColumns = 2; numberOfColumns < 8; numberOfColumns++) {
                for (int numberOfFaults = 0; numberOfFaults <= maxOfFaultyLinks(numberOfRows, numberOfColumns); numberOfFaults++) {
                    Graph noc = RandomFaultyGraphBuilder.generateGraph(numberOfRows, numberOfColumns, numberOfFaults);
                    SR sr = new SR(noc, new BidimensionalSBRPolicy(noc));
                    sr.computeSegments();
                    sr.setrestrictions();
                    edgesAreEitherInASegmentOrAreBridges(noc, sr.segments());
                    verticesAreEitherInASegmentOrAreAloneInASubnet(noc, sr);
                    //onlyTerminalVerticesAreLinkedToBridges(noc, sr);
                }
            }
        }
    }

    private void verticesAreEitherInASegmentOrAreAloneInASubnet(Graph noc, SR sr) {
        for (Vertex candidate : noc.getVertices()) {
            if (isInASegment(sr.segments(), candidate)) {
                Assert.assertFalse(isSubNet(candidate, sr));
            }
            else {
                Assert.assertTrue(isSubNet(candidate, sr));
            }
        }
    }

    private void onlyTerminalVerticesAreLinkedToBridges(Graph noc, SR sr) {
        for (Vertex candidate : noc.getVertices()) {
            if (sr.terminalVertices().contains(candidate)) {
                Assert.assertTrue(isLinkedToBridge(candidate, noc));
            } else {
                Assert.assertFalse(isLinkedToBridge(candidate, noc));
            }
        }
    }

    private boolean isLinkedToBridge(Vertex candidate, Graph noc) {
        for (Edge e : noc.adjunctsOf(candidate)) {
            if (isBridge(noc, e)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSubNet(Vertex candidate, SR sr) {
        return sr.startVertices().contains(candidate) && sr.terminalVertices().contains(candidate);
    }

    private void edgesAreEitherInASegmentOrAreBridges(Graph noc, Collection<Segment> segments) {
        for (Edge candidate : noc.getEdges()) {
            if (isBridge(noc, candidate))
                Assert.assertFalse(isInASegment(noc, segments, candidate));
            else
                Assert.assertTrue(isInASegment(noc, segments, candidate));
        }
    }

    private boolean isInASegment(Collection<Segment> segments, Vertex candidate) {
        int count = 0;
        for (Segment seg : segments) {
            if (seg.vertices().contains(candidate)) {
                count++;
            }
        }
        return count == 1;
    }

    private boolean isInASegment(Graph noc, Collection<Segment> segments, Edge candidate) {
        int count = 0;
        for (Segment seg : segments)
            if (seg.edges().contains(candidate) || seg.edges().contains(sibling(noc, candidate)))
                count++;
        return count == 1;
    }

    private boolean isBridge(Graph graph, Edge candidate) {
        List<Edge> bridges = new Bridge(graph).bridges();
        return bridges.contains(candidate) || bridges.contains(sibling(graph, candidate));
    }

    private Edge sibling(Graph graph, Edge one) {
        return graph.adjunct(one.destination(), one.source());
    }

    private int maxOfFaultyLinks(int rows, int columns) {
        int minOfLinks = rows*columns - 1;
        int totalOfLinks = (rows - 1)*columns + (columns - 1)*rows;
        return totalOfLinks - minOfLinks;
    }
}

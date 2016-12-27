package sbr;

import org.junit.*;
import util.*;

public class SegmentAcceptVisitorTest {
    private Graph graph = new RegularGraphBuilder().generateGraph(2, 2);

    @Test
    public void emptySegmentDoNotVisit() throws Exception {
        MockSegmentVisitor visitor = new MockSegmentVisitor();
        ISegment emptySegment = new Segment();
        emptySegment.accept(visitor);
        Assert.assertFalse(visitor.wasVisited());
    }

    @Test
    public void acceptVisitsUnitarySegment() throws Exception {
        MockSegmentVisitor visitor = new MockSegmentVisitor();
        ISegment unitarySegment = segmentUnitary();
        unitarySegment.accept(visitor);
        Assert.assertTrue(visitor.unitaryWasVisited());
    }

    @Test
    public void acceptVisitsStartSegment() throws Exception {
        MockSegmentVisitor visitor = new MockSegmentVisitor();
        ISegment startSegment = segmentStart();
        startSegment.accept(visitor);
        Assert.assertTrue(visitor.startWasVisited());
    }

    @Test
    public void acceptVisitsRegularSegment() throws Exception {
        MockSegmentVisitor visitor = new MockSegmentVisitor();
        ISegment regularSegment = segmentRegular();
        regularSegment.accept(visitor);
        Assert.assertTrue(visitor.regularWasVisited());
    }

    private ISegment segmentRegular() {
        Segment regularSegment = new Segment();
        regularSegment.add(graph.adjunct(graph.vertex("0.0"), graph.vertex("0.1")));
        regularSegment.add(graph.vertex("0.1"));
        regularSegment.add(graph.adjunct(graph.vertex("0.1"), graph.vertex("1.1")));
        return regularSegment;
    }

    private ISegment segmentStart() {
        Segment startSegment = new Segment();
        startSegment.add(graph.vertex("0.0"));
        startSegment.add(graph.adjunct(graph.vertex("0.0"), graph.vertex("0.1")));
        startSegment.add(graph.vertex("0.1"));
        startSegment.add(graph.adjunct(graph.vertex("0.1"), graph.vertex("1.1")));
        startSegment.add(graph.vertex("1.1"));
        startSegment.add(graph.adjunct(graph.vertex("1.1"), graph.vertex("1.0")));
        startSegment.add(graph.vertex("1.0"));
        startSegment.add(graph.adjunct(graph.vertex("1.0"), graph.vertex("0.0")));
        return startSegment;
    }

    private ISegment segmentUnitary() {
        Segment unitarySegment = new Segment();
        unitarySegment.add(graph.getEdges().get(0));
        return unitarySegment;
    }

    private class MockSegmentVisitor implements SegmentVisitor {
        private boolean unitaryWasVisited = false;
        private boolean startWasVisited = false;
        private boolean regularWasVisited = false;

        boolean wasVisited() {
            return unitaryWasVisited || startWasVisited || regularWasVisited;
        }

        @Override
        public void visitUnitarySegment(ISegment unitarySegment) {
            unitaryWasVisited = true;
        }

        @Override
        public void visitStartSegment(ISegment startSegment) {
            startWasVisited = true;
        }

        @Override
        public void visitRegularSegment(ISegment regularSegment) {
            regularWasVisited = true;
        }

        boolean unitaryWasVisited() {
            return unitaryWasVisited;
        }

        boolean startWasVisited() {
            return startWasVisited;
        }

        boolean regularWasVisited() {
            return regularWasVisited;
        }
    }
}

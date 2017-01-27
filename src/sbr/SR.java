package sbr;

import util.*;

import java.util.*;

public class SR {

    private Range currentWindow;

    private Graph graph;
    private GraphRestrictions restrictions;

    private int subNet, maxSN;
    private List<Segment> segments;
    private Segment currentSegment;
    private List<Edge> bridges;

    private List<Vertex> visitedVertices, unvisitedVertices;
    private List<Vertex> startVertices, terminalVertices;
    private List<Edge> visitedEdges, unvisitedEdges;
    private Map<Vertex, Integer> subnetForVertex;
    private SBRPolicy policy;

    public SR(Graph graph, SBRPolicy policy) {
        this.policy = policy;
        this.graph = graph;
        this.restrictions = new GraphRestrictions(graph);
        segments = new ArrayList<>();
        visitedVertices = new ArrayList<>();
        unvisitedVertices = new ArrayList<>(graph.getVertices());
        startVertices = new ArrayList<>();
        terminalVertices = new ArrayList<>();
        visitedEdges = new ArrayList<>();
        unvisitedEdges = new ArrayList<>(graph.getEdges());
        bridges = new Bridge(graph).bridges();
        subNet = 0;
        maxSN = -1;
        subnetForVertex = new HashMap<>();
    }

    public void computeSegments() {
        int maxX = graph.columns() - 1;
        int maxY = graph.rows() - 1;
        for (int currentY = maxY; currentY >= 0; currentY--) {
            currentWindow = Range.TwoDimensionalRange(0, maxX, currentY, maxY);
            computeSegmentsInRange();
            for (Segment seg : segments)
                assert !seg.edges().isEmpty();
        }
    }

    public Collection<Segment> segments() {
        return segments;
    }

    public Collection<Vertex> terminalVertices() {
        return terminalVertices;
    }

    public Collection<Vertex> startVertices() {
        return startVertices;
    }

    public GraphRestrictions restrictions() {
        return this.restrictions;
    }

    public void setrestrictions() {
        SegmentVisitor visitor = new AddRestrictionsSegmentVisitor(restrictions, graph);
        for (Segment segment : segments) {
            segment.accept(visitor);
        }
    }

    private void computeSegmentsInRange() {
        terminalVertices = new ArrayList<>();

        Optional<Vertex> start = pickFirstVertex();
        while (start.isPresent()) {
            computeSegmentsInExistingSubNets(start.get());
            start = pickNextStartVertex();
            /* If no segment was created from the start vertex picked and the graph has no bridges
            (ideal places to pick starts) then we should try to pick any non-visited vertex to
            start forming segments even in the first run.
            Even if the graph does have bridges, in the case no segment was formed at all I cannot
            see a problem in pick 'any' vertex to start segments.
             */
        }
    }

    private Optional<Vertex> pickFirstVertex() {
        int xMin = currentWindow.min(0);
        int yMin = currentWindow.min(1);
        int xMax = currentWindow.max(0) - 1;
        int yMax = currentWindow.max(1) - 1;

        boolean isFirstTurn = (yMin + 1 == yMax);
        Vertex sw;
        Vertex left = graph.vertex(xMin + "." + yMin);
        Vertex right = graph.vertex(xMax + "." + yMin);
        boolean pair = ((yMin + 1) % 2 == 0);

        if (isFirstTurn) {
            sw = (pair) ? left : right;
            setStart(sw);
            subnetForVertex.put(sw, ++maxSN);
            return Optional.ofNullable(sw);
        }

        Optional<Vertex> nextVisited = nextVisited();
        if (nextVisited.isPresent()) {
            return nextVisited;
        }

        Optional<Vertex> nextBridgeLinkedStartVertex = nextBridgeLinkedStartVertex();
        sw = nextBridgeLinkedStartVertex.orElse((pair) ? left : right);
        setStart(sw);
        subnetForVertex.put(sw, ++maxSN);
        return Optional.ofNullable(sw);
    }

    private void computeSegmentsInExistingSubNets(Vertex sw) {
        Optional<Vertex> vertex = Optional.of(sw);
        while (vertex.isPresent()) {
            policy.resetRRIndex();
            subNet = subnetForVertex.get(vertex.get());
            Segment seg = formSegmentFrom(vertex.get());
            if (segmentIsValid(seg)) {
                segments.add(seg);
                visit(seg);
            }
            vertex = nextVisited();
        }
    }

    private void visit(Segment segment) {
        for (Vertex v : segment.vertices()) {
            visit(v);
            subnetForVertex.put(v, subNet);
        }
        for (Edge e : segment.edges()) {
            visit(e);
        }
    }

    private Optional<Vertex> pickNextStartVertex() {
        Optional<Vertex> sw = nextBridgeLinkedStartVertex();
        if (isFinalTurn() && sw.isPresent()) {
            setStart(sw.get());
            subnetForVertex.put(sw.get(), ++maxSN);
            return sw;
        }
        return Optional.empty();
    }

    private Optional<Vertex> nextVisited() {
        List<Vertex> suitables = suitableVisitedVertices();
        Collections.reverse(suitables);
        for (Vertex tic : suitables) {
            if (suitableLinks(tic).size() > 1)
                return Optional.of(tic);
            for (int j = suitables.indexOf(tic) + 1; j < suitables.size(); j++) {
                Vertex tac = suitables.get(j);
                if (subnetForVertex.get(tic).equals(subnetForVertex.get(tac))) {
                    return Optional.of(tic);
                }
            }
        }
        return Optional.empty();
    }

    private Optional<Vertex> nextBridgeLinkedStartVertex() {
        for (Edge b : bridges) {
            Vertex dst = b.destination();
            if (canBeStart(dst)) {
                return Optional.ofNullable(dst);
            }
            Vertex src = b.source();
            if (canBeStart(src)) {
                return Optional.ofNullable(src);
            }
        }
        return Optional.empty();
    }

    private Segment formSegmentFrom(Vertex sw) {
        currentSegment = new Segment();
        if (!isVisited(sw)) {
            currentSegment.add(sw);
        }
        Segment segment = extendedSegment(currentSegment, sw);
        if (!segmentIsValid(segment)) {
            if (isVisited(sw)) {
                setTerminal(sw);
            } else if (isFinalTurn()) {
                visit(sw);
                setTerminal(sw);
            } else {
                unsetStart(sw);
            }
        }
        return segment;
    }

    private boolean segmentIsValid(Segment seg) {
        return !seg.edges().isEmpty();
    }

    private boolean isFinalTurn() {
        return (currentWindow.min(0) == 0 && currentWindow.min(1) == 0);
    }

    private List<Vertex> suitableVisitedVertices() {
        List<Vertex> result = new ArrayList<>();
        for (Vertex sw : visitedVertices) {
            if (!isTerminal(sw) && sw.isIn(currentWindow) && hasSuitableLinks(sw))
                result.add(sw);
        }
        return result;
    }

    private boolean hasSuitableLinks(Vertex sw) {
        return !suitableLinks(sw).isEmpty();
    }

    private boolean canBeStart(Vertex sw) {
        return (!isVisited(sw) && sw.isIn(currentWindow));
    }

    private Segment extendedSegment(Segment base, Vertex from) {
        List<Edge> links = suitableLinks(from, base);
        Segment extendedSegment = new Segment();
        while (!links.isEmpty() && !segmentIsValid(extendedSegment)) {
            Edge currentLink = policy.getNextLink(links);
            links.remove(currentLink);
            extendedSegment = extendedSegment(new Segment(base, currentLink));
        }
        return extendedSegment;
    }

    private Segment extendedSegment(Segment edgeEndedSegment) {
        Edge currentLink = edgeEndedSegment.edges().get(edgeEndedSegment.edges().size() - 1);
        Vertex nextVertex = currentLink.destination();
        if (isVisited(nextVertex) && subnetForVertex.get(nextVertex) == subNet) {
            return edgeEndedSegment;
        } else if (isStart(nextVertex) && subnetForVertex.get(nextVertex) == subNet) {
            return edgeEndedSegment;
        }
        return extendedSegment(new Segment(edgeEndedSegment, nextVertex), nextVertex);
    }

    private List<Edge> suitableLinks(Vertex v) {
        List<Edge> result = new ArrayList<>();
        for (Edge ln : graph.adjunctsOf(v)) {
            Vertex dst = ln.destination();
            boolean isBridge = bridges.contains(ln) || bridges.contains(graph.adjunct(dst, ln.source()));
            if (!isVisited(ln) && dst.isIn(currentWindow) && !isBridge)
                result.add(ln);
        }
        return result;
    }

    private List<Edge> suitableLinks(Vertex v, Segment segment) {
        currentSegment = segment;
        List<Edge> result = new ArrayList<>();
        for (Edge ln : graph.adjunctsOf(v)) {
            Vertex dst = ln.destination();
            boolean crosses = isTVisited(dst) && !isStart(dst);
            boolean isBridge = bridges.contains(ln) || bridges.contains(graph.adjunct(dst, ln.source()));
            if (!isVisited(ln) && !isTVisited(ln) && dst.isIn(currentWindow) && !crosses && !isBridge)
                result.add(ln);
        }
        return result;
    }

    private boolean isVisited(Vertex aVertex) {
        return visitedVertices.contains(aVertex);
    }

    private void visit(Vertex aVertex) {
        visitedVertices.add(aVertex);
        unvisitedVertices.remove(aVertex);
    }

    private boolean isTVisited(Vertex aVertex) {
        return currentSegment.vertices().contains(aVertex);
    }

    private boolean isVisited(Edge anEdge) {
        return visitedEdges.contains(anEdge);
    }

    private void visit(Edge anEdge) {
        Edge itsSibling = graph.adjunct(anEdge.destination(), anEdge.source());
        visitedEdges.add(anEdge);
        visitedEdges.add(itsSibling);
        unvisitedEdges.remove(anEdge);
        unvisitedEdges.remove(itsSibling);
    }

    private boolean isTVisited(Edge anEdge) {
        Edge itsSibling = graph.adjunct(anEdge.destination(), anEdge.source());
        return (currentSegment.edges().contains(anEdge) || currentSegment.edges().contains(itsSibling));
    }

    private void setStart(Vertex aVertex) {
        assert !isStart(aVertex) : "Vertex already startVertices";

        startVertices.add(aVertex);
    }

    private void unsetStart(Vertex aVertex) {
        assert isStart(aVertex) : "Vertex not startVertices";

        startVertices.remove(aVertex);
    }

    private boolean isStart(Vertex aVertex) {
        return startVertices.contains(aVertex);
    }

    private void setTerminal(Vertex aVertex) {
        assert !isTerminal(aVertex) : "Vertex already terminalVertices";
        assert isVisited(aVertex) : "Vertex not visited";

        terminalVertices.add(aVertex);
    }

    private boolean isTerminal(Vertex aVertex) {
        return terminalVertices.contains(aVertex);
    }
}

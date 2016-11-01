package sbr;

import util.*;
import java.util.*;

public class SR {

    private final boolean debug = false;
    private Range currentWindow;

    private Graph graph;
    private GraphRestrictions restrictions;

    private int subNet, maxSN;
    private ArrayList<Segment> segments;
    private Segment currentSegment;
    private List<Edge> bridges;

    private List<Vertex> visitedVertices, unvisitedVertices, start, terminal;
    private List<Edge> visitedEdges, unvisitedEdges;
    private HashMap<Vertex, Segment> segmentForVertex;
    private HashMap<Vertex, Integer> subnetForVertex;
    private SBRPolicy policy;

    public SR(Graph graph , SBRPolicy policy)
    {
        this.policy = policy;
        this.graph = graph;
        this.restrictions = new GraphRestrictions(graph);
        segments = new ArrayList<>();
        visitedVertices = new ArrayList<>();
        unvisitedVertices = new ArrayList<>(graph.getVertices());
        start = new ArrayList<>();
        terminal = new ArrayList<>();
        visitedEdges = new ArrayList<>();
        unvisitedEdges = new ArrayList<>(graph.getEdges());
        bridges = new Bridge(graph).bridges();
        subNet = 0;
        maxSN = -1;
        segmentForVertex = new HashMap<>();
        subnetForVertex = new HashMap<>();
    }

    public void computeSegments() {
        int maxX = graph.columns()-1;
        int maxY = graph.rows()-1;
        for (int currentY = maxY; currentY >= 0; currentY--) {
            currentWindow = Range.TwoDimensionalRange(0, maxX, currentY, maxY);
            computeSegmentsInRange();
            for (Segment seg : segments)
                assert !seg.getLinks().isEmpty();
        }
    }

    public Collection<Segment> segments() {
        return segments;
    }

    public Collection<Vertex> terminalVertices() {
        return terminal;
    }

    public Collection<Vertex> startVertices() {
        return start;
    }

    private void computeSegmentsInRange() {
        terminal = new ArrayList<>();

        Vertex start = pickFirstVertex();
        while (start != null) {
            computeSegmentsInExistingSubNets(start);
            start = pickNextStartVertex();
            /* If no segment was created from the start vertex picked and the graph has no bridges
            (ideal places to pick starts) then we should try to pick any non-visited vertex to
            start forming segments even in the first run.
            Even if the graph does have bridges, in the case no segment was formed at all I cannot
            see a problem in pick 'any' vertex to start segments.
             */
        }
    }

    private void computeSegmentsInExistingSubNets(Vertex sw) {
        while (null != sw) {
            policy.resetRRIndex();
            subNet = subnetForVertex.get(sw);
            Segment seg = formSegmentFrom(sw);
            if(segmentIsValid(seg))
                segments.add(seg);
            sw = nextVisited();
        }
    }

    private boolean segmentIsValid(Segment seg) {
        return (null != seg && !seg.getLinks().isEmpty());
    }

    private Vertex pickFirstVertex() {
        int xMin = currentWindow.min(0);
        int yMin = currentWindow.min(1);
        int xMax = currentWindow.max(0);
        int yMax = currentWindow.max(1);

        boolean isFirstTurn = (yMin + 1 == yMax);
        Vertex sw;
        Vertex left = graph.vertex(xMin + "." + yMin);
        Vertex right = graph.vertex(xMax + "." + yMin);
        boolean pair = ((yMin + 1) % 2 == 0);

        if (isFirstTurn) {
            sw = (pair) ? left : right;
            setStart(sw);
            subnetForVertex.put(sw, ++maxSN);
            return sw;
        }

        if (isVisited(left) || isVisited(right)) {
            sw = (isVisited(left)) ? left : right;
            return sw;
        }

        sw = nextVisited();
        if (null != sw) {
            return sw;
        }

        sw = nextBridgeLinkedStartVertex();
        if (sw == null) {
            sw = (pair) ? left : right;
        }
        setStart(sw);
        subnetForVertex.put(sw, ++maxSN);
        return sw;
    }

    private Segment formSegmentFrom(Vertex sw) {
        currentSegment = new Segment();
        Segment segment = null;
        if (find(sw)) {
            segment = currentSegment;
        } else if (isVisited(sw)) {
            setTerminal(sw);
        } else if (isFinalTurn()) {
            visit(sw);
            setTerminal(sw);
        } else {
            unsetStart(sw);
        }
        return segment;
    }

    private Vertex pickNextStartVertex() {
        Vertex sw = null;
        if (isFinalTurn() && (sw = nextBridgeLinkedStartVertex()) != null) {
            setStart(sw);
            subnetForVertex.put(sw, ++maxSN);
        }
        return sw;
    }

    private boolean isFinalTurn() {
        return (currentWindow.min(0) == 0 && currentWindow.min(1) == 0);
    }

    private boolean find(Vertex sw) {
        Segment segm = currentSegment;
        if (!isVisited(sw)) {
            segm.add(sw);
            segmentForVertex.put(sw, segm);
            setTVisited(sw);
        } else if (subnetForVertex.get(sw) != subNet && !(isStart(sw) && isTerminal(sw)))
            return false;

        ArrayList<Edge> links = suitableLinks(sw);
        while (!links.isEmpty()) {
            Edge ln = policy.getNextLink(links);
            links.remove(ln);
            Edge nl = graph.adjunct(ln.destination(), ln.source());
            if (debug) System.err.println("Link now: "+ln.source().name()+" <-> "+ln.destination().name());
            setTVisited(ln);
            setTVisited(nl);
            segm.add(ln);
            Vertex nsw = ln.destination();
            if (nsw.isIn(currentWindow)) {
                if (((isVisited(nsw) || isStart(nsw)) && subnetForVertex.get(nsw) == subNet) || find(nsw)) {
                    visit(ln);
                    visit(nl);
                    if(!isVisited(sw)) visit(sw);
                    if(!isVisited(nsw)) visit(nsw);
                    if (isTerminal(nsw) && isStart(nsw) && subnetForVertex.get(nsw) != subNet && !segmentForVertex.containsKey(nsw)) {
                        unsetTerminal(nsw);
                        unsetStart(nsw);
                        segm.add(nsw);
                        segmentForVertex.put(nsw, segm);
                    }
                    subnetForVertex.put(nsw, subNet);
                    return true;
                }
            }
            unsetTVisited(ln);
            unsetTVisited(nl);
            segm.remove(ln);
        }
        segm.remove(sw);
        segmentForVertex.remove(sw);
        if(isTVisited(sw)) unsetTVisited(sw);
        return false;
    }

    private Vertex nextVisited() {
        List<Vertex> suitables = suitableVisitedVertices();
        Collections.reverse(suitables);
        for (Vertex tic : suitables) {
            if (suitableLinks(tic).size() > 1)
                return tic;
            for (int j = suitables.indexOf(tic) + 1; j < suitables.size(); j++) {
                Vertex tac = suitables.get(j);
                if (subnetForVertex.get(tic).equals(subnetForVertex.get(tac))) {
                    return tic;
                }
            }
        }
        return null;
    }

    private List<Vertex> suitableVisitedVertices() {
        ArrayList<Vertex> result = new ArrayList<>();
        for (Vertex sw : visitedVertices) {
            if (!isTerminal(sw) && sw.isIn(currentWindow) && hasSuitableLinks(sw))
                    result.add(sw);
        }
        return result;
    }

    private Vertex nextBridgeLinkedStartVertex() {
        for (Edge b: bridges) {
            Vertex dst = b.destination();
            if(canBeStart(dst)) {
                return dst;
            }
            Vertex src = b.source();
            if(canBeStart(src)) {
                return src;
            }
        }
        return null;
    }

    private boolean canBeStart(Vertex sw) {
        return (!isVisited(sw) && sw.isIn(currentWindow));
    }

    private boolean hasSuitableLinks(Vertex sw) {
        return !suitableLinks(sw).isEmpty();
    }

    public GraphRestrictions restrictions() {
        return this.restrictions;
    }

    public void setrestrictions() {
        for (Segment segment : segments) {
            if (segment.getLinks().isEmpty()) {
                continue;
            }
            if (segment.isUnitary()) {
                // No traffic allowed at link
                Vertex Starting = segment.getLinks().get(0).source();
                Vertex Ending = segment.getLinks().get(0).destination();
                // Restrictions at Starting core
                for (Edge link : graph.adjunctsOf(Starting)) {
                        restrictions.addRestriction(Starting, link.color(), graph.adjunct(Starting, Ending).color());
                }
                // Restrictions at Ending core
                for (Edge link : graph.adjunctsOf(Ending)) {
                        restrictions.addRestriction(Ending, link.color(),  graph.adjunct(Ending, Starting).color());
                }
                continue;
            }
            // Put it at first or second link
            if (segment.getSwitchs().size() == 1) {
                Vertex sw = segment.getSwitchs().get(0);
                restrictions.addRestriction(sw, TopologyKnowledge.getInvColor(segment.getLinks().get(0).color()), segment.getLinks().get(1).color());
                restrictions.addRestriction(sw, segment.getLinks().get(1).color(), TopologyKnowledge.getInvColor(segment.getLinks().get(0).color()));
                continue;
            }
            // At this point we have or starting or regular segment
            if (segment.isRegular()) {
                Vertex restrict = segment.getSwitchs().get(1);
                restrictions.addRestriction(restrict, TopologyKnowledge.getInvColor(segment.getLinks().get(1).color()), segment.getLinks().get(2).color());
                restrictions.addRestriction(restrict, segment.getLinks().get(2).color(), TopologyKnowledge.getInvColor(segment.getLinks().get(1).color()));
                continue;
            }
            if (segment.isStarting()) {
                Vertex restrict = segment.getSwitchs().get(1);
                restrictions.addRestriction(restrict, TopologyKnowledge.getInvColor(segment.getLinks().get(0).color()), segment.getLinks().get(1).color());
                restrictions.addRestriction(restrict, segment.getLinks().get(1).color(), TopologyKnowledge.getInvColor(segment.getLinks().get(0).color()));
            }
        }
    }

    private boolean isVisited(Vertex v) {
        return visitedVertices.contains(v);
    }

    private void visit(Vertex v) {
        assert !visitedVertices.contains(v) : "Vertex jah visitado?";
        //assert unvisitedVertices.contains(v) : "Vertex (t)visitado?";

        visitedVertices.add(v);
        unvisitedVertices.remove(v);
    }

    private boolean isVisited(Edge a) {
        return visitedEdges.contains(a);
    }

    private void visit(Edge a) {
        assert !visitedEdges.contains(a) : "Edge jah visitada?";
        //assert unvisitedEdges.contains(a) : "Edge (t)visitada?";

        visitedEdges.add(a);
        unvisitedEdges.remove(a);
    }

    private boolean isTVisited(Vertex v) {
        return !visitedVertices.contains(v) && !unvisitedVertices.contains(v);
    }

    private void setTVisited(Vertex v) {
        assert !visitedVertices.contains(v) : "Vertex jah visitado?";
        assert unvisitedVertices.contains(v) : "Vertex jah tvisitado?";

        unvisitedVertices.remove(v);
    }

    private boolean isTVisited(Edge a) {
        return !visitedEdges.contains(a) && !unvisitedEdges.contains(a);
    }

    private void setTVisited(Edge a) {
        assert unvisitedEdges.contains(a) : "Edge jah tvisitada?";

        unvisitedEdges.remove(a);
    }

    private void unsetTVisited(Edge a) {
        assert !unvisitedEdges.contains(a) : "Edge nao tvisitada?";

        unvisitedEdges.add(a);
    }

    private void unsetTVisited(Vertex v) {
        assert !unvisitedVertices.contains(v) : "Vertex nao tvisitado?";

        unvisitedVertices.add(v);
    }

    private boolean isStart(Vertex v) {
        return start.contains(v);
    }

    private void setStart(Vertex v) {
        assert !start.contains(v) : "Vertex jah start?";

        start.add(v);
    }

    private void unsetStart(Vertex v) {
        assert start.contains(v) : "Vertex nao start?";

        start.remove(v);
    }

    private boolean isTerminal(Vertex v) {
        return terminal.contains(v);
    }

    private void setTerminal(Vertex v) {
        assert !terminal.contains(v) : "Vertex jah terminal?";
        assert visitedVertices.contains(v) : "Vertex nao tvisitado?";

        terminal.add(v);
    }

    private void unsetTerminal(Vertex v) {
        assert terminal.contains(v) : "Vertex nao terminal?";

        terminal.remove(v);
    }

    private ArrayList<Edge> suitableLinks(Vertex v) {
        ArrayList<Edge> result = new ArrayList<>();
        for(Edge ln : graph.adjunctsOf(v)) {
            Vertex dst = ln.destination();
            boolean cruza = isTVisited(dst) && !isStart(dst);
            boolean isBridge = bridges.contains(ln) || bridges.contains(graph.adjunct(dst, ln.source()));
            if(!isVisited(ln) && !isTVisited(ln) && dst.isIn(currentWindow) && !cruza && !isBridge)
                result.add(ln);
        }
        return result;
    }
}

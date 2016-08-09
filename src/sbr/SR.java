package sbr;

import util.*;

import java.util.*;

public class SR {

	private final boolean debug = false;
	private final static String[] RoundRobin = { "N", "E", "S", "W" };
	private static int RRIndex[];
	private Range currentWindow;

	private Graph graph;
	private GraphRestrictions restrictions;

	private int subNet, maxSN;
	private ArrayList<Segment> segments;
	private ArrayList<Edge> bridges;

	private List<Vertex> visitedVertices, unvisitedVertices, start, terminal;
	private List<Edge> visitedEdges, unvisitedEdges;

	private HashMap<Vertex, Segment> segmentForVertex;
	private HashMap<Vertex, Integer> subnetForVertex;

	public SR(Graph graph) 
	{
		this.graph = graph;
		this.restrictions = new GraphRestrictions(graph);
		segments = new ArrayList<>();
		visitedVertices = new ArrayList<>();
		unvisitedVertices = new ArrayList<>(graph.getVertices());
		start = new ArrayList<>();
		terminal = new ArrayList<>();
		visitedEdges = new ArrayList<>();
		unvisitedEdges = new ArrayList<>(graph.getEdges());
		bridges = new ArrayList<>();
		RRIndex = new int[2];
		RRIndex[0] = -1;
		RRIndex[1] = -1;
		subNet = 0;
		maxSN = 0;
		new Bridge(graph);
		segmentForVertex = new HashMap<>();
		subnetForVertex = new HashMap<>();
	}

	public void computeSegments() {
		int maxX = graph.dimX()-1;

		int maxY = graph.dimY()-1;
		for (int currentY = maxY - 1; currentY >= 0; currentY--) {
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
		Segment sg = new Segment();
		segments.add(sg);

		Vertex sw = pickFirstVertex();
		while (sw != null) {
			this.resetRRIndex();
			formSegmentFrom(sw);
			sw = pickNextVertex();
			/* If no segment was created from the start vertex picked and the graph has no bridges
			(ideal places to pick starts) then we should try to pick any non-visited vertex to
			start forming segments even in the first run.
			Even if the graph does have bridges, in the case no segment was formed at all I cannot
			see a problem in pick 'any' vertex to start segments.
			 */
		}
	}

	private Vertex pickFirstVertex() {
		int xMin = currentWindow.min(0);
		int yMin = currentWindow.min(1);
		int xMax = currentWindow.max(0);
		int yMax = currentWindow.max(1);

		// Choose the start switch
		boolean isFirstTurn = (yMin + 1 == yMax);
		Vertex sw;
		Vertex left = graph.vertex(xMin + "." + (yMin + 1));
		Vertex right = graph.vertex(xMax + "." + (yMin + 1));

		if(isFirstTurn) {
			boolean pair = ((yMin + 1) % 2 == 0);
			sw = (pair) ? left : right;
			setStart(sw);
			subnetForVertex.put(sw, subNet);
		}
		else if(isVisited(left) || isVisited(right)){
			sw = (isVisited(left)) ? left : right;
			subNet = subnetForVertex.get(sw);
		}
		else {
			sw = nextVisited();
			if (sw == null) {
				sw = nextBridgeLinkedStartVertex();
				if(sw == null) {
					boolean pair = ((yMin + 1) % 2 == 0);
					sw = (pair) ? left : right;
					setStart(sw);
					subnetForVertex.put(sw, subNet);
					return sw;
				}
				setStart(sw);
				subNet = ++maxSN;
				subnetForVertex.put(sw, subNet);
			}
			subNet = subnetForVertex.get(sw);
		}
		return sw;
	}

	private void formSegmentFrom(Vertex sw) {
		if (find(sw)) {
			segments.add(new Segment());
		} else if (isVisited(sw)) {
			setTerminal(sw);
		} else if (isFinalTurn()) {
			visit(sw);
			setTerminal(sw);
		} else {
			unsetStart(sw);
		}
	}

	private Vertex pickNextVertex() {
		Vertex sw = nextVisited();
		if (sw == null) { // if didnt find
			if (isFinalTurn() && (sw = nextBridgeLinkedStartVertex()) != null) {
				subNet = ++maxSN;
				segments.get(segments.size() - 1).add(sw);// sg.add(sw);
				segmentForVertex.put(sw, segments.get(segments.size() - 1));
				setStart(sw);
				visit(sw);
				subnetForVertex.put(sw, subNet);
			}
			else {
				if (segments.get(segments.size()-1).getLinks().isEmpty()/* sg.getLinks().isEmpty()*/)
					segments.remove(segments.get(segments.size()-1));
				return null;
			}
		}
		return sw;
	}

	private boolean isFinalTurn() {
		return (currentWindow.min(0) == 0 && currentWindow.min(1) == 0);
	}

	private boolean find(Vertex sw) {
		Segment segm = segments.get(segments.size()-1);
		if (!isVisited(sw)) {
			segm.add(sw);
			segmentForVertex.put(sw, segm);
			setTVisited(sw);
		} else if (subnetForVertex.get(sw) != subNet && !(isStart(sw) && isTerminal(sw)))
			return false;
			
		ArrayList<Edge> links = suitableLinks(sw);
		while (!links.isEmpty()) {
			Edge ln = getNextLink(links);
			links.remove(ln);
			Edge nl = ln.destination().edge(ln.source());
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
		for (int i = 0; i < suitables.size(); i++) {
			Vertex tic = suitables.get(i);
			for (int j = i + 1; j < suitables.size(); j++) {
				Vertex tac = suitables.get(j);
				if (subnetForVertex.get(tic).equals(subnetForVertex.get(tac))) {
					subNet = subnetForVertex.get(tic);
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
		return (!isVisited(sw) && sw.isIn(currentWindow) && hasSuitableLinks(sw));
	}

	private boolean hasSuitableLinks(Vertex sw) {
		return !suitableLinks(sw).isEmpty();
	}
	/*
	 * try to make a small segment by choosing a link for close a cycle making a
	 * turn every time. RRIndex keeps track of the last turn
	 */
	private Edge getNextLink(ArrayList<Edge> links) {
		Edge got = null;
		int index;
		if (RRIndex[0] == -1) {
			if (RRIndex[1] == -1) { // first choice of this computation
				index = 0;
			} else { // second choice
				index = (RRIndex[1] + 1) % 4;
			}
		} else { // others choices
			index = (RRIndex[0] + 2) % 4;
			if ((index + RRIndex[1]) % 2 == 0) {
				index = (index + 1) % 4;
			}
		}
		while (true) {
			for (Edge ln : links) {
				if (ln.color() == RoundRobin[index]) {
					got = ln;
					break;
				}
			}
			if (got != null)
				break;
			else {
				if (RRIndex[1] == ((RRIndex[0] + 1) % 4))
					index = (index + 3) % 4;
				else
					index = (index + 1) % 4;
			}
		}
		// updates the last turn
		if (index != RRIndex[1]) {
			RRIndex[0] = RRIndex[1];
			RRIndex[1] = index;
		}
		return got;
	}

	private void resetRRIndex() {
		RRIndex[0] = -1;
		RRIndex[1] = -1;
	}

	public GraphRestrictions restrictions() {
		return this.restrictions;
	}

	public void setrestrictions() {
		for (Segment segment : segments) {
			if (segment.getLinks().isEmpty())
				continue;

			if (segment.isUnitary()) {
				// No traffic allowed at link
				Vertex Starting = segment.getLinks().get(0).source();
				Vertex Ending = segment.getLinks().get(0).destination();
				//System.err.println("Start: " + Starting.name() + " Ending: "+ Ending.name());
				// Restricted link
				String opStarting = Starting.edge(Ending).color();
				String opEnding = Ending.edge(Starting).color();
				// Restrictions at Starting core
				for (Edge link : Starting.adjuncts())
					if (link.color() != opStarting) {
						restrictions.addRestriction(Starting, link.color(), opStarting);
					}
				// Restrictions at Ending core
				for (Edge link : Ending.adjuncts())
					if (link.color() != opEnding) {
						restrictions.addRestriction(Ending, link.color(), opEnding);
					}
				continue;
			}
			// Put it at first or second link
			if (segment.getSwitchs().size() == 1) {
				Vertex sw = segment.getSwitchs().get(0);

				restrictions.addRestriction(sw, EdgeColor.getInvColor(segment.getLinks().get(0).color()),
						segment.getLinks().get(1).color());
				restrictions.addRestriction(sw, segment.getLinks().get(1).color(),
						EdgeColor.getInvColor(segment.getLinks().get(0).color()));
				continue;
			}
			// At this point we have or starting or regular segment
			if (segment.isRegular()) {
				Vertex restrict = segment.getSwitchs().get(1);
				restrictions.addRestriction(restrict, EdgeColor.getInvColor(segment.getLinks().get(1).color()), segment
						.getLinks().get(2).color());
				restrictions.addRestriction(restrict, segment.getLinks().get(2).color(),
						EdgeColor.getInvColor(segment.getLinks().get(1).color()));
				continue;
			}
			if (segment.isStarting()) {
				Vertex restrict = segment.getSwitchs().get(1);
				restrictions.addRestriction(restrict, EdgeColor.getInvColor(segment.getLinks().get(0).color()), segment
						.getLinks().get(1).color());
				restrictions.addRestriction(restrict, segment.getLinks().get(1).color(),
						EdgeColor.getInvColor(segment.getLinks().get(0).color()));
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
		for(Edge ln : v.adjuncts()) {
			Vertex dst = ln.destination();
			boolean cruza = isTVisited(dst) && !isStart(dst);
			boolean isBridge = bridges.contains(ln) || bridges.contains(dst.edge(ln.source()));
			if(!isVisited(ln) && !isTVisited(ln) && dst.isIn(currentWindow) && !cruza && !isBridge)
				result.add(ln);
		}
		return result;
	}

	private class Bridge {
		private int cnt; // counter
		private int[] pre; // pre[v] = order in which dfs examines v
		private int[] low; // low[v] = lowest preorder of any vertex connected to v

		public Bridge(Graph G) {
			assert G != null : "Ponteiro nulo para grafo!";
			low = new int[G.getVertices().size()];
			pre = new int[G.getVertices().size()];
			cnt = 0;
			for (int v = 0; v < G.getVertices().size(); v++)
				low[v] = pre[v] = -1;

			for (Vertex v: G.getVertices())
				
				if (pre[G.indexOf(v)] == -1)
					dfs(G, v, v);
		}

		private void dfs(Graph g, Vertex u, Vertex v) {
			assert g != null && u != null && v != null : "Null pointer to vertices or graph!";
			low[g.indexOf(v)] = pre[g.indexOf(v)] = cnt++;
			for(Edge e : v.adjuncts()) {
				Vertex w = e.destination();
				if (pre[g.indexOf(w)] == -1) {
					dfs(g, v, w);
					low[g.indexOf(v)] = Math.min(low[g.indexOf(v)], low[g.indexOf(w)]);
					if (low[g.indexOf(w)] == pre[g.indexOf(w)]) {
						bridges.add(e);
					}
				}
				else if (!w.equals(u))
					low[g.indexOf(v)] = Math.min(low[g.indexOf(v)], low[g.indexOf(w)]);
			}
		}
		
	}

}

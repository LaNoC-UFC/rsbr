package sbr;

import util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SR {

	private final boolean debug = false;
	private final static String[] RoundRobin = { "N", "E", "S", "W" };
	private static int RRIndex[];

	private Graph graph;
	private GraphRestrictions restrictions;

	private int subNet, maxSN;
	private ArrayList<Segment> segments;
	private ArrayList<Edge> bridge;

	private List<Vertex> visitedVertices, unvisitedVertices, start, terminal;
	private List<Edge> visitedEdges, unvisitedEdges;
	//private List<Vertex> visiteds;
	//private List<Vertex> nVisiteds;

	private HashMap<Vertex, Segment> segmentForVertex;
	private HashMap<Vertex, Integer> subnetForVertex;

	public SR(Graph graph) 
	{
		//graph = new Graph(fileName);
		this.graph = graph;
		this.restrictions = new GraphRestrictions(graph);
		if(debug) System.err.println(graph);
		segments = new ArrayList<>();
		visitedVertices = new ArrayList<>();
		unvisitedVertices = new ArrayList<>();
		start = new ArrayList<>();
		visitedEdges = new ArrayList<>();
		unvisitedEdges = new ArrayList<>();
		bridge = new ArrayList<>();
		//nVisiteds = new ArrayList<>();
		RRIndex = new int[2];
		RRIndex[0] = -1;
		RRIndex[1] = -1;
		subNet = 0;
		maxSN = 0;
		new Bridge(graph);
		System.out.println(bridge.size()+" bridges.");
		segmentForVertex = new HashMap<>();
		subnetForVertex = new HashMap<>();
	}

	public void computeSegments() {
		// fill not visiteds' list
		unvisitedEdges.addAll(graph.getEdges());
		unvisitedVertices.addAll(graph.getVertices());
		int Nx = graph.dimX()-1;
		int Ny = graph.dimY()-1;
		String max = Nx + "." + Ny;
		for (int i = Ny - 1; i >= 0; i--) {
			String min = 0 + "." + i;
			if(debug) System.err.println("#Min: " + min + " #Max: " + max);
			computeSegments(min, max);
		}
	}

	private void computeSegments(String min, String max) {

		terminal = new ArrayList<>();
		int xMin = Integer.valueOf(min.split("\\.")[0]);
		int yMin = Integer.valueOf(min.split("\\.")[1]);
		int xMax = Integer.valueOf(max.split("\\.")[0]);
		int yMax = Integer.valueOf(max.split("\\.")[1]);

		if (debug)
			System.err.println("Subnet now: " + subNet);

		// Choose the start switch
		boolean first = (yMin + 1 == yMax);
		boolean pair = ((yMin + 1) % 2 == 0);
		Vertex sw;
		Vertex left = graph.vertex(xMin + "." + (yMin + 1));
		Vertex right = graph.vertex(xMax + "." + (yMin + 1));
		
		if(first) {
			sw = (pair) ? left : right;
			setStart(sw);
			subnetForVertex.put(sw, subNet);
		}
		else if(isVisited(left) || isVisited(right)){
			sw = (isVisited(left)) ? left : right;
			subNet = subnetForVertex.get(sw);
		}
		else {
			sw = nextVisited(min, max);
			if (sw == null) {
				sw = nextNotVisited(min, max);
				if(sw == null) return;
				setStart(sw);
				subNet = ++maxSN;
				subnetForVertex.put(sw, subNet);
			}
			subNet = subnetForVertex.get(sw);
		}

		Segment sg = new Segment();
		segments.add(sg);

		if (debug) System.err.println("#starting: " + sw.name());

		do {
			this.resetRRIndex();
			
			// try to form a segment
			if (find(sw, min, max)) {
				sg = new Segment();
				segments.add(sg);
				if (debug) System.err.println("New Segment.");
			} else if (isVisited(sw)) {
				setTerminal(sw);
				if (debug) System.err.println(sw.name() + " is Terminal.");

			} else if (xMin == 0 && yMin == 0) { // eh a ultima rodada
				visit(sw);
				setTerminal(sw);
				if (debug) System.err.println(sw.name() + " is Terminal.");
					
			} else {
				unsetStart(sw);
			}
			
			// look for a not visited switch to form the next segment
			sw = nextVisited(min, max);
			if (sw == null) { // if didnt find
				if ((xMin == 0 && yMin == 0) && (sw = nextNotVisited(min, max)) != null) {
					subNet = ++maxSN;
					if (debug) System.err.println("Subnet now: " + subNet);
					segments.get(segments.size()-1).add(sw);// sg.add(sw);
					segmentForVertex.put(sw, segments.get(segments.size()-1));
					setStart(sw);
					if (debug)
						System.err.println(sw.name() + " is Start.");
					visit(sw);
					subnetForVertex.put(sw, subNet);
				} else {
					if (segments.get(segments.size()-1).getLinks().isEmpty()/* sg.getLinks().isEmpty()*/)
						segments.remove(sg);
					return;
				}
			}
		} while (sw != null);

	}

	protected boolean find(Vertex sw, String min, String max) {
		Segment segm = segments.get(segments.size()-1);
		if (!isVisited(sw)) {
			segm.add(sw);
			segmentForVertex.put(sw, segm);
			setTVisited(sw);
		} else if (subnetForVertex.get(sw) != subNet && !(isStart(sw) && isTerminal(sw)))
			return false;
			
		if (debug) System.err.println("Switch now: " + sw.name());
		
		ArrayList<Edge> links = suitableLinks(sw, min, max);
		if (links == null) {
			if (debug) System.err.println("No Suitable Links found.");
			if(isTVisited(sw)) unsetTVisited(sw);
			segm.remove(sw);
			segmentForVertex.remove(sw);
			return false;
		}
		
		while (!links.isEmpty()) {
			Edge ln = getNextLink(links);
			links.remove(ln);
			Edge nl = ln.destination().edge(ln.source());
			if (debug) System.err.println("Link now: "+ln.source().name()+" <-> "+ln.destination().name());
			setTVisited(ln);
			setTVisited(nl);
			segm.add(ln);
			Vertex nsw = ln.other(sw);
			if (nsw.isIn(min, max)) {
				if (((isVisited(nsw) || isStart(nsw)) && subnetForVertex.get(nsw) == subNet) || find(nsw, min, max)) {
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

	/*
	 * search for a switch marked as visited, belonging to the current subnet,
	 * and with at least one link not marked as visited.
	 */
	protected Vertex nextVisited(String min, String max) {
		ArrayList<Vertex> next = new ArrayList<>();
		// get switches from visiteds' list
		for (int i = visitedVertices.size() - 1; i >= 0; i--) {
			Vertex sw = visitedVertices.get(i);
			if (!isTerminal(sw) && sw.isIn(min, max) && suitableLinks(sw, min, max) != null) {
				
				// agora só retorna se existir mais de um visitado com links
				// favoráveis
				if(next.isEmpty())
					next.add(sw);
				else {
					for(Vertex n : next) {
						if(subnetForVertex.get(n) == subnetForVertex.get(sw)) {
							if (debug) System.err.println("nextVisited " + n.name());
							subNet = subnetForVertex.get(n);
							return n;							
						}
					}
					next.add(sw);
				}
			}
		}
		if (debug)
			System.err.println("nextVisited not found for subnet " + subNet);
		return null;
	}

	/*
	 * look for a switch that is not marked as visited not marked as terminal,
	 * and attached to a terminal switch.
	 */
	protected Vertex nextNotVisited(String min, String max) {
		for (Edge b: bridge) {
			Vertex sw = b.destination();
			if(!isVisited(sw) && sw.isIn(min, max) && suitableLinks(sw, min, max) != null) {
				if (debug) System.err.println("nextNotVisited " + sw.name());
				return sw;				
			}
			sw = b.source();
			if(!isVisited(sw) && sw.isIn(min, max) && suitableLinks(sw, min, max) != null) {
				if (debug) System.err.println("nextNotVisited " + sw.name());
				return sw;				
			}
		}
		/*
		for (Vertex sw : unvisitedVertices) {
			if (sw.isIn(min, max) && isTerminal(sw)) {
				List<Vertex> lS = sw.getNeighbors();
				for (Vertex s : lS) {
					if (!isVisited(s) && !isTerminal(s) && s.isIn(min, max)) {
						if (debug) System.err.println("nextNotVisited " + s.name());
						return s;
					}
				}
			}
		}
		*/
		if (debug)
			System.err.println("nextNotVisited not found");
		return null;
	}

	/*
	 * try to make a small segment by choosing a link for close a cycle making a
	 * turn every time. RRIndex keeps track of the last turn
	 */
	protected Edge getNextLink(ArrayList<Edge> links) {
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

	public ArrayList<Edge> suitableLinks(Vertex v, String min, String max)
	{
		ArrayList<Edge> adj = v.adjuncts();
		if(adj.isEmpty())
			return null;
		
		ArrayList<Edge> slinks = new ArrayList<>();
		for(Edge ln : adj) {
			Vertex dst = ln.destination();
			boolean cruza = isTVisited(dst) && !isStart(dst);
			boolean bdg = bridge.contains(ln) || bridge.contains(dst.edge(ln.source()));
			if(!isVisited(ln) && !isTVisited(ln) && dst.isIn(min, max) && !cruza && !bdg)
				slinks.add(ln);
		}

		return (slinks.isEmpty())? null : slinks;
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
						bridge.add(e);
					}
				}
				else if (!w.equals(u))
					low[g.indexOf(v)] = Math.min(low[g.indexOf(v)], low[g.indexOf(w)]);
			}
		}
		
	}

}

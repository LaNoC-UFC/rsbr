package sbr;

import util.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SR {

	private final boolean debug = false;
	private final static String[] RoundRobin = { "N", "E", "S", "W" };
	private static int RRIndex[];

	private Graph graph;

	private int subNet, maxSN;
	private ArrayList<Segment> segments;
	private ArrayList<Aresta> bridge;

	private List<Vertice> visitedVertices, unvisitedVertices, start, terminal;
	private List<Aresta> visitedArestas, unvisitedArestas;
	//private List<Vertice> visiteds;
	//private List<Vertice> nVisiteds;


	public SR(Graph graph) 
	{
		//graph = new Graph(fileName);
		this.graph = graph;
		if(debug) System.err.println(graph);
		segments = new ArrayList<>();
		visitedVertices = new ArrayList<>();
		unvisitedVertices = new ArrayList<>();
		start = new ArrayList<>();
		visitedArestas = new ArrayList<>();
		unvisitedArestas = new ArrayList<>();
		bridge = new ArrayList<>();
		//nVisiteds = new ArrayList<>();
		RRIndex = new int[2];
		RRIndex[0] = -1;
		RRIndex[1] = -1;
		subNet = 0;
		maxSN = 0;
		new Bridge(graph);
		System.out.println(bridge.size()+" bridges.");
	}

	public void computeSegments() {
		// fill not visiteds' list
		unvisitedArestas.addAll(graph.getArestas());
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
		Vertice sw;
		Vertice left = graph.getVertice(xMin + "." + (yMin + 1));
		Vertice right = graph.getVertice(xMax + "." + (yMin + 1));
		
		if(first) {
			sw = (pair) ? left : right;
			setStart(sw);
			sw.setSubNet(subNet);
		}
		else if(isVisited(left) || isVisited(right)){
			sw = (isVisited(left)) ? left : right;
			subNet = sw.getSubNet();
		}
		else {
			sw = nextVisited(min, max);
			if (sw == null) {
				sw = nextNotVisited(min, max);
				if(sw == null) return;
				setStart(sw);
				subNet = ++maxSN;
				sw.setSubNet(subNet);
			}
			subNet = sw.getSubNet();
		}

		Segment sg = new Segment();
		segments.add(sg);

		if (debug) System.err.println("#starting: " + sw.getNome());

		do {
			this.resetRRIndex();
			
			// try to form a segment
			if (find(sw, min, max)) {
				sg = new Segment();
				segments.add(sg);
				if (debug) System.err.println("New Segment.");
			} else if (isVisited(sw)) {
				setTerminal(sw);
				if (debug) System.err.println(sw.getNome() + " is Terminal.");

			} else if (xMin == 0 && yMin == 0) { // eh a ultima rodada
				visit(sw);
				setTerminal(sw);
				if (debug) System.err.println(sw.getNome() + " is Terminal.");
					
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
					setStart(sw);
					if (debug)
						System.err.println(sw.getNome() + " is Start.");
					visit(sw);
					sw.setSubNet(subNet);
				} else {
					if (segments.get(segments.size()-1).getLinks().isEmpty()/* sg.getLinks().isEmpty()*/)
						segments.remove(sg);
					return;
				}
			}
		} while (sw != null);

	}

	protected boolean find(Vertice sw, String min, String max) {
		Segment segm = segments.get(segments.size()-1);
		if (!isVisited(sw)) {
			segm.add(sw);
			//sw.setSegment(segm);
			setTVisited(sw);
		} else if (!sw.belongsTo(subNet) && !(isStart(sw) && isTerminal(sw)))
			return false;
			
		if (debug) System.err.println("Switch now: " + sw.getNome());
		
		ArrayList<Aresta> links = suitableLinks(sw, min, max);
		if (links == null) {
			if (debug) System.err.println("No Suitable Links found.");
			if(isTVisited(sw)) unsetTVisited(sw);
			//sw.setSegment(null);
			segm.remove(sw);
			return false;
		}
		
		while (!links.isEmpty()) {
			Aresta ln = getNextLink(links);
			links.remove(ln);
			Aresta nl = ln.getDestino().getAresta(ln.getOrigem());
			if (debug) System.err.println("Link now: "+ln.getOrigem().getNome()+" <-> "+ln.getDestino().getNome());
			setTVisited(ln);
			setTVisited(nl);
			segm.add(ln);
			Vertice nsw = ln.other(sw);
			if (nsw.isIn(min, max)) {
				if (((isVisited(nsw) || isStart(nsw)) && nsw.belongsTo(subNet)) || find(nsw, min, max)) {
					visit(ln);
					visit(nl);
					if(!isVisited(sw)) visit(sw);
					if(!isVisited(nsw)) visit(nsw);
					if (isTerminal(nsw) && isStart(nsw) && !nsw.belongsTo(subNet) && (nsw.getSegment() == null)) {
						unsetTerminal(nsw);
						unsetStart(nsw);
						//nsw.setSegment(segm);
						segm.add(nsw);
					}
					nsw.setSubNet(subNet);
					return true;
				}
			}
			unsetTVisited(ln);
			unsetTVisited(nl);
			segm.remove(ln);
		}
		segm.remove(sw);
		//sw.setSegment(null);
		if(isTVisited(sw)) unsetTVisited(sw);

		return false;
	}

	/*
	 * search for a switch marked as visited, belonging to the current subnet,
	 * and with at least one link not marked as visited.
	 */
	protected Vertice nextVisited(String min, String max) {
		ArrayList<Vertice> next = new ArrayList<>();
		// get switches from visiteds' list
		for (int i = visitedVertices.size() - 1; i >= 0; i--) {
			Vertice sw = visitedVertices.get(i);
			if (!isTerminal(sw) && sw.isIn(min, max) && suitableLinks(sw, min, max) != null) {
				
				// agora só retorna se existir mais de um visitado com links
				// favoráveis
				if(next.isEmpty())
					next.add(sw);
				else {
					for(Vertice n : next) {
						if(n.getSubNet() == sw.getSubNet()) {
							if (debug) System.err.println("nextVisited " + n.getNome());
							subNet = n.getSubNet();
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
	protected Vertice nextNotVisited(String min, String max) {
		for (Aresta b: bridge) {
			Vertice sw = b.getDestino();
			if(!isVisited(sw) && sw.isIn(min, max) && suitableLinks(sw, min, max) != null) {
				if (debug) System.err.println("nextNotVisited " + sw.getNome());
				return sw;				
			}
			sw = b.getOrigem();
			if(!isVisited(sw) && sw.isIn(min, max) && suitableLinks(sw, min, max) != null) {
				if (debug) System.err.println("nextNotVisited " + sw.getNome());
				return sw;				
			}
		}
		/*
		for (Vertice sw : unvisitedVertices) {
			if (sw.isIn(min, max) && isTerminal(sw)) {
				List<Vertice> lS = sw.getNeighbors();
				for (Vertice s : lS) {
					if (!isVisited(s) && !isTerminal(s) && s.isIn(min, max)) {
						if (debug) System.err.println("nextNotVisited " + s.getNome());
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
	protected Aresta getNextLink(ArrayList<Aresta> links) {
		Aresta got = null;
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
			for (Aresta ln : links) {
				if (ln.getCor() == RoundRobin[index]) {
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

	public void setrestrictions() {
		for (Segment segment : segments) {
			if (segment.getLinks().isEmpty())
				continue;

			if (segment.isUnitary()) {
				// No traffic allowed at link
				Vertice Starting = segment.getLinks().get(0).getOrigem();
				Vertice Ending = segment.getLinks().get(0).getDestino();
				//System.err.println("Start: " + Starting.getNome() + " Ending: "+ Ending.getNome());
				// Restricted link
				String opStarting = Starting.getAresta(Ending).getCor();
				String opEnding = Ending.getAresta(Starting).getCor();
				// Restrictions at Starting core
				for (Aresta link : Starting.getAdj())
					if (link.getCor() != opStarting)
						Starting.addRestriction(link.getCor(), opStarting);
				// Restrictions at Ending core
				for (Aresta link : Ending.getAdj())
					if (link.getCor() != opEnding)
						Ending.addRestriction(link.getCor(), opEnding);
				continue;
			}
			// Put it at first or second link
			if (segment.getSwitchs().size() == 1) {
				segment.getSwitchs()
						.get(0)
						.addRestriction(
								segment.getLinks().get(0).getInvColor(),
								segment.getLinks().get(1).getCor());
				segment.getSwitchs()
						.get(0)
						.addRestriction(segment.getLinks().get(1).getCor(),
								segment.getLinks().get(0).getInvColor());
				continue;
			}
			// At this point we have or starting or regular segment
			if (segment.isRegular()) {
				Vertice restrict = segment.getSwitchs().get(1);
				restrict.addRestriction(
						segment.getLinks().get(1).getInvColor(), segment
								.getLinks().get(2).getCor());
				restrict.addRestriction(segment.getLinks().get(2).getCor(),
						segment.getLinks().get(1).getInvColor());
				continue;
			}
			if (segment.isStarting()) {
				Vertice restrict = segment.getSwitchs().get(1);
				restrict.addRestriction(
						segment.getLinks().get(0).getInvColor(), segment
								.getLinks().get(1).getCor());
				restrict.addRestriction(segment.getLinks().get(1).getCor(),
						segment.getLinks().get(0).getInvColor());
			}
		}
	}
	
	private boolean isVisited(Vertice v) {
		return visitedVertices.contains(v);
	}

	private void visit(Vertice v) {
		assert !visitedVertices.contains(v) : "Vertice jah visitado?";
		//assert unvisitedVertices.contains(v) : "Vertice (t)visitado?";
		
		visitedVertices.add(v);
		unvisitedVertices.remove(v);
	}

	private boolean isVisited(Aresta a) {
		return visitedArestas.contains(a);
	}

	private void visit(Aresta a) {
		assert !visitedArestas.contains(a) : "Aresta jah visitada?";
		//assert unvisitedArestas.contains(a) : "Aresta (t)visitada?";
		
		visitedArestas.add(a);
		unvisitedArestas.remove(a);
	}

	private boolean isTVisited(Vertice v) {
		return !visitedVertices.contains(v) && !unvisitedVertices.contains(v);
	}

	private void setTVisited(Vertice v) {
		assert !visitedVertices.contains(v) : "Vertice jah visitado?";
		assert unvisitedVertices.contains(v) : "Vertice jah tvisitado?";
		
		unvisitedVertices.remove(v);
	}

	private boolean isTVisited(Aresta a) {
		return !visitedArestas.contains(a) && !unvisitedArestas.contains(a);
	}

	private void setTVisited(Aresta a) {
		assert unvisitedArestas.contains(a) : "Aresta jah tvisitada?";
		
		unvisitedArestas.remove(a);
	}

	private void unsetTVisited(Aresta a) {
		assert !unvisitedArestas.contains(a) : "Aresta nao tvisitada?";
		
		unvisitedArestas.add(a);
	}

	private void unsetTVisited(Vertice v) {
		assert !unvisitedVertices.contains(v) : "Vertice nao tvisitado?";
		
		unvisitedVertices.add(v);
	}

	private boolean isStart(Vertice v) {
		return start.contains(v);
	}

	private void setStart(Vertice v) {
		assert !start.contains(v) : "Vertice jah start?";
		
		start.add(v);
	}

	private void unsetStart(Vertice v) {
		assert start.contains(v) : "Vertice nao start?";
		
		start.remove(v);
	}

	private boolean isTerminal(Vertice v) {
		return terminal.contains(v);
	}

	private void setTerminal(Vertice v) {
		assert !terminal.contains(v) : "Vertice jah terminal?";
		assert visitedVertices.contains(v) : "Vertice nao tvisitado?";

		terminal.add(v);
	}

	private void unsetTerminal(Vertice v) {
		assert terminal.contains(v) : "Vertice nao terminal?";
		
		terminal.remove(v);
	}

	public ArrayList<Aresta> suitableLinks(Vertice v, String min, String max) 
	{
		ArrayList<Aresta> adj = v.getAdj(); 
		if(adj.isEmpty())
			return null;
		
		ArrayList<Aresta> slinks = new ArrayList<>();
		for(Aresta ln : adj) {
			Vertice dst = ln.getDestino();
			boolean cruza = isTVisited(dst) && !isStart(dst);
			boolean bdg = bridge.contains(ln) || bridge.contains(dst.getAresta(ln.getOrigem()));
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

			for (Vertice v: G.getVertices())
				
				if (pre[G.indexOf(v)] == -1)
					dfs(G, v, v);
		}

		private void dfs(Graph g, Vertice u, Vertice v) {
			assert g != null && u != null && v != null : "Ponteiro(s) nulo(s) para vertice(s) ou grafo!";
			low[g.indexOf(v)] = pre[g.indexOf(v)] = cnt++;
			for(Aresta e : v.getAdj()) {
				Vertice w = e.getDestino();
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

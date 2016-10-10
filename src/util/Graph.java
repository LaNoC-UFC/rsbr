package util;

import java.util.*;

public class Graph {
	private ArrayList<Vertex> vertices;
	private ArrayList<Edge> edges;
	private int dimX;
	private int dimY;

	public Graph(int rows, int columns) {
		vertices = new ArrayList<>();
		edges = new ArrayList<>();
		dimX = columns;
		dimY = rows;
	}

	public boolean hasIsolatedCores() {
		Set<Vertex> reachedVertices = new HashSet<>();
		reachAdjuncts(vertex("0.0"), reachedVertices);
		return !(reachedVertices.size() == vertices.size());
	}

	private void reachAdjuncts(Vertex vertex, Set<Vertex> reachable) {
		if (reachable.contains(vertex)){
			return;
		}
		reachable.add(vertex);
		for (Edge adj : vertex.adjuncts()){
			Vertex neigh = adj.destination();
			reachAdjuncts(neigh, reachable);
		}
	}

	public ArrayList<Vertex> getVertices() {
		return this.vertices;
	}

	public ArrayList<Edge> getEdges() {
		return this.edges;
	}

	public Vertex vertex(String name) {
		Vertex vertex = null;

		for (Vertex v : this.vertices) {
			if (v.name().equals(name))
				vertex = v;
		}

		if (vertex == null) {
			System.out.println("Vertex: " + name + " nao encontrado");
			return null;
		}

		return vertex;
	}

	void addVertex(String nome) {
		Vertex v = new Vertex(nome);
		vertices.add(v);
	}

	void addEdge(Vertex origem, Vertex destino, Character cor) {
		Edge e = new Edge(origem, destino, cor);
		origem.addAdjunct(e);
		edges.add(e);
	}

	void addEdge(Edge toAdd) {
		toAdd.source().adjuncts().add(toAdd);
		edges.add(toAdd);
	}

	void removeEdge(Edge toRemove) {
		toRemove.source().adjuncts().remove(toRemove);
		edges.remove(toRemove);
	}

	public String toString() {
		String r = "";
		System.out.println("Graph:");
		for (Vertex u : vertices) {
			r += u.name() + " -> ";
			for (Edge e : u.adjuncts()) {
				Vertex v = e.destination();
				r += v.name() + e.color() + ", ";
			}
			r += "\n";
		}
		return r;
	}

	public int dimX() {
		return dimX;
	}

	public int dimY() {
		return dimY;
	}

	public int indexOf(Vertex v) {
		return indexOf(v.name());
	}

	private int indexOf(String xy) {
		int x = Integer.parseInt(xy.split("\\.")[0]);
		int y = Integer.parseInt(xy.split("\\.")[1]);
		return x + y*this.dimX();
	}

	public List<Edge> bridges() {
		return new Bridge(this).bridges();
	}

	private class Bridge {
		private int cnt; // counter
		private int[] pre; // pre[v] = order in which dfs examines v
		private int[] low; // low[v] = lowest preorder of any vertex connected to v
		private List<Edge> bridges;

		private Bridge(Graph G) {
			assert G != null : "Ponteiro nulo para grafo!";
			bridges = new ArrayList<>();
			low = new int[G.getVertices().size()];
			pre = new int[G.getVertices().size()];
			cnt = 0;
			for (int v = 0; v < G.getVertices().size(); v++)
				low[v] = pre[v] = -1;

			for (Vertex v: G.getVertices())

				if (pre[G.indexOf(v)] == -1)
					dfs(G, v, v);
		}

		List<Edge> bridges() {
			return bridges;
		}

		private void dfs(Graph g, Vertex u, Vertex v) {
			if (null == g)
				return;
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

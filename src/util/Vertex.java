package util;

import java.util.ArrayList;

public class Vertex {

	private String name;
	private ArrayList<Edge> adjuncts;

	public Vertex(String name) {
		this.name = name;
		adjuncts = new ArrayList<>();
	}

	public Edge edge(Vertex dst) {
		for (Edge v : adjuncts)
			if (v.destination().name().equals(dst.name()))
				return v;
		return null;
	}

	public void addAdjunct(Edge e) {
		adjuncts.add(e);
	}

	public ArrayList<Edge> adjuncts() {
		return this.adjuncts;
	}

	public Edge adjunct(String color) {
		for (Edge a : this.adjuncts)
			if (a.color().equals(color))
				return a;
		System.out.println("ERROR : There isn't a Op " + color + "?");
		return null;
	}

	public String name() {
		return this.name;
	}

	public boolean isIn(String min, String max) {
		int xMin = Integer.valueOf(min.split("\\.")[0]);
		int yMin = Integer.valueOf(min.split("\\.")[1]);
		int xMax = Integer.valueOf(max.split("\\.")[0]);
		int yMax = Integer.valueOf(max.split("\\.")[1]);

		int x = Integer.valueOf(name.split("\\.")[0]);
		int y = Integer.valueOf(name.split("\\.")[1]);

		return (x <= xMax && x >= xMin && y <= yMax && y >= yMin);
	}

	public void checkIsolation(ArrayList<Vertex> alc) {
		if (!alc.contains(this))
			alc.add(this); // Adiciona primeiro core analisado aos alcancaveis
		for (Edge adj : this.adjuncts) {
			// So adiciona aos alcancaveis cores que ainda nao foram adicionados
			if (alc.contains(adj.destination()))
				continue;
			Vertex neigh = adj.destination();
			alc.add(neigh);
			// checa para vizinhos
			neigh.checkIsolation(alc);
		}
	}
}

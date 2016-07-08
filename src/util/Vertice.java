package util;

import java.util.ArrayList;

public class Vertice {

	private String nome;
	private ArrayList<Edge> adj;

	public Vertice(String name) {
		nome = name;
		adj = new ArrayList<>();
	}

	public Edge edge(Vertice destino) {
		for (Edge v : adj)
			if (v.destination().getNome().equals(destino.getNome()))
				return v;
		return null;
	}

	public void addAdj(Edge e) {
		adj.add(e);
	}

	public ArrayList<Edge> getAdj() {
		return this.adj;
	}

	public Edge getAdj(String color) {
		for (Edge a : this.adj)
			if (a.color().equals(color))
				return a;

		System.out.println("ERROR : There isn't a Op " + color + "?");
		return null;
	}

	public String getNome() {
		return this.nome;
	}

	public boolean isIn(String min, String max) {
		int xMin = Integer.valueOf(min.split("\\.")[0]);
		int yMin = Integer.valueOf(min.split("\\.")[1]);
		int xMax = Integer.valueOf(max.split("\\.")[0]);
		int yMax = Integer.valueOf(max.split("\\.")[1]);

		int x = Integer.valueOf(nome.split("\\.")[0]);
		int y = Integer.valueOf(nome.split("\\.")[1]);

		return (x <= xMax && x >= xMin && y <= yMax && y >= yMin);
	}

	public void checkIsolation(ArrayList<Vertice> alc) {
		if (!alc.contains(this))
			alc.add(this); // Adiciona primeiro core analisado aos alcancaveis
		for (Edge adj : this.adj) {
			// So adiciona aos alcancaveis cores que ainda nao foram adicionados
			if (alc.contains(adj.destination()))
				continue;
			Vertice neigh = adj.destination();
			alc.add(neigh);
			// checa para vizinhos
			neigh.checkIsolation(alc);
		}
	}
}

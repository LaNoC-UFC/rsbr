package util;

import rbr.Region;

import java.util.ArrayList;

public class Vertice implements Comparable<Vertice> {
															// [3]-E [4]-W
	private int distancia;

	private String nome;
	private ArrayList<Edge> adj;
	private ArrayList<rbr.Region> Regions = new ArrayList<>();

	public Vertice(String name) {
		nome = name;
		adj = new ArrayList<Edge>();
	}

	public void initRegions() {
		this.Regions = new ArrayList<>();
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

	public ArrayList<Region> getRegions() {
		return Regions;
	}

	public void setRegions(ArrayList<Region> Regions) {
		this.Regions = Regions;
	}

	public void addRegion(String ip, ArrayList<String> dsts, String op) {
		rbr.Region region = new rbr.Region(ip, dsts, op);
		this.Regions.add(region);
	}

	public boolean reaches(Vertice dest) {
		return this.reaches(dest, "I");
	}

	private boolean reaches(Vertice dest, String ipColor) {
		if (dest == this)
			return true;
		String opColor = this.getOpColor(dest, ipColor);
		if (opColor == null)
			return false;
		return this.getAdj(opColor).destination()
				.reaches(dest, EdgeColor.getInvColor(getAdj(opColor).color()));
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

	private String getOpColor(Vertice dest, String ipColor) {
		String router = dest.getNome();
		for (rbr.Region reg : this.Regions)
			if (reg.contains(router) && reg.getIp().contains(ipColor))
				return (reg.getOp().substring(0, 1));

		System.err.println("ERROR : There isn't Op on " + this.getNome()
				+ " for " + dest.getNome() + " " + ipColor);
		return null;
	}

	public int compareTo(Vertice outroVertice) {
		if (this.distancia < outroVertice.distancia) {
			return -1;
		}
		if (this.distancia > outroVertice.distancia) {
			return 1;
		}
		return 0;
	}
}

package util;

import rbr.Region;
import sbr.Segment;

import java.util.ArrayList;
import java.util.Arrays;

public class Vertice implements Comparable<Vertice> {
	private String[] restrictions = { "", "", "", "", "" }; // [0]-I [1]-N [2]-S
															// [3]-E [4]-W
	private Segment seg;
	private int snet;

	private int distancia;

	private ArrayList<rbr.RoutingPath> routingPaths = new ArrayList<>();
	private String nome;
	private ArrayList<Aresta> adj;
	private ArrayList<rbr.Region> Regions = new ArrayList<>();

	public Vertice(String name) {
		seg = null;
		snet = -1;
		nome = name;
		adj = new ArrayList<Aresta>();
		// restrictions = nome + ": I{} N{} S{} E{} W{}";
	}

	public void initRoutingOptions() {
		this.routingPaths = new ArrayList<>();
	}

	public void initRegions() {
		this.Regions = new ArrayList<>();
	}

	public void addRestriction(String op, String rest) {
		/*
		 * String op1 = op+"{"; this.restrictions =
		 * restrictions.substring(0,restrictions
		 * .indexOf(op1)+2)+rest+restrictions
		 * .substring(restrictions.indexOf(op1)+2);
		 */
		switch (op) {
		case "I":
			restrictions[0] = restrictions[0] + "" + rest;
			break;
		case "N":
			restrictions[1] = restrictions[1] + "" + rest;
			break;
		case "S":
			restrictions[2] = restrictions[2] + "" + rest;
			break;
		case "E":
			restrictions[3] = restrictions[3] + "" + rest;
			break;
		case "W":
			restrictions[4] = restrictions[4] + "" + rest;
			break;
		}
	}

	public String getRestriction(String op) {
		switch (op) {
		case "I":
			return restrictions[0];
		case "N":
			return restrictions[1];
		case "S":
			return restrictions[2];
		case "E":
			return restrictions[3];
		case "W":
			return restrictions[4];
		default:
			return null;
		}
	}

	public String[] getRestrictions() {
		return this.restrictions;
	}

	public Aresta getAresta(Vertice destino) {
		for (Aresta v : adj)
			if (v.getDestino().getNome().equals(destino.getNome()))
				return v;

		return null;

	}

	public void addAdj(Aresta e) {

		adj.add(e);
	}

	public ArrayList<Aresta> getAdj() {

		return this.adj;

	}

	public Aresta getAdj(String color) {
		for (Aresta a : this.adj)
			if (a.getCor().equals(color))
				return a;

		System.out.println("ERROR : There isn't a Op " + color + "?");
		return null;
	}

	public String getNome() {

		return this.nome;

	}

	public Segment getSegment() {
		return this.seg;
	}

	public void setSegment(Segment sg) {
		seg = sg;
	}

	public int getSubNet() {
		return this.snet;
	}

	public void setSubNet(int sn) {
		snet = sn;

	}

	public boolean belongsTo(int sn) {
		return (sn == snet);
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

	public void addRP(String ip, String dst, String op) {
		ip = sortStrAlf(ip);
		op = sortStrAlf(op);
		if (!this.AlreadyExists(ip, dst, op)) {
			rbr.RoutingPath RP = new rbr.RoutingPath(ip, dst, op);
			this.routingPaths.add(RP);
		}
	}

	private boolean AlreadyExists(String ip, String dst, String op) {
		for (int a = 0; a < this.routingPaths.size(); a++)
			if (this.routingPaths.get(a).getIp().equals(ip)
					&& this.routingPaths.get(a).getDst().equals(dst)
					&& this.routingPaths.get(a).getOp().equals(op))
				return true;

		return false;
	}

	public static String sortStrAlf(String input) {
		char[] ip1 = input.toCharArray();
		Arrays.sort(ip1);

		return String.valueOf(ip1);
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
		return this.getAdj(opColor).getDestino()
				.reaches(dest, getAdj(opColor).getInvColor());
	}

	public void checkIsolation(ArrayList<Vertice> alc) {
		if (!alc.contains(this))
			alc.add(this); // Adiciona primeiro core analisado aos alcancaveis
		for (Aresta adj : this.adj) {
			// So adiciona aos alcancaveis cores que ainda nao foram adicionados
			if (alc.contains(adj.getDestino()))
				continue;
			Vertice neigh = adj.getDestino();
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

	public ArrayList<rbr.RoutingPath> getRoutingPaths() {
		return routingPaths;
	}

	public void setRoutingPaths(ArrayList<rbr.RoutingPath> routingPaths) {
		this.routingPaths = routingPaths;
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

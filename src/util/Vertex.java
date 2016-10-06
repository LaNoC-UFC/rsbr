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

	public Edge adjunct(Character color) {
		for (Edge a : this.adjuncts)
			if (a.color().equals(color))
				return a;
		System.out.println("ERROR : There isn't a Op " + color + "?");
		return null;
	}

	public String name() {
		return this.name;
	}

	public boolean isIn(Range box) {
		int xMin = box.min(0);
		int yMin = box.min(1);
		int xMax = box.max(0);
		int yMax = box.max(1);

		int x = Integer.valueOf(name.split("\\.")[0]);
		int y = Integer.valueOf(name.split("\\.")[1]);

		return (x <= xMax && x >= xMin && y <= yMax && y >= yMin);
	}
}

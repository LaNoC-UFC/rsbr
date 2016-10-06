package util;

public class Edge {
	private Vertex source;
	private Vertex destination;
	private Character color;
	private double weight;

	Edge(Vertex src, Vertex dst, Character color) {
		this.color = color;
		this.source = src;
		this.destination = dst;
		this.weight = 0.0;
	}

	public Character color() {
		return this.color;
	}

	public Vertex destination() {
		return this.destination;
	}

	public Vertex source() {
		return this.source;
	}

	public Vertex other(Vertex v) {
		assert source == v || destination == v : "Vertex is not conected to this Edge";
		return (v == source) ? destination : source;
	}

	public double weight() {
		return this.weight;
	}
}

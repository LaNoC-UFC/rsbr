package util;

public class Edge {
	private Vertex source;
	private Vertex destination;
	private String color;
	private double weight;

	Edge(Vertex src, Vertex dst, String color) {
		this.color = color;
		this.source = src;
		this.destination = dst;
		this.weight = 0.0;
	}

	public String color() {
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

	void setWeight(double volume) {
		assert 0 <= volume : "Link Weight should be non-negative.";
		this.weight = volume;
	}

	public double weight() {
		return this.weight;
	}
}

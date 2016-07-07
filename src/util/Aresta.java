package util;

public class Aresta {
	private Vertice source;
	private Vertice destination;
	private String color;
	private double weight;

	Aresta(Vertice src, Vertice dst, String color) {
		this.color = color;
		this.source = src;
		this.destination = dst;
		this.weight = 0.0;
	}

	public String color() {
		return this.color;
	}

	public Vertice destination() {
		return this.destination;
	}

	public Vertice source() {
		return this.source;
	}

	public Vertice other(Vertice v) {
		assert source == v || destination == v : "Vertice is not conected to this Aresta";
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

package util;

public class Aresta {
	protected Vertice starting;
	protected Vertice ending;
	private String cor;
	private double weight;

	public Aresta(Vertice ori, Vertice dest, String cor) {
		this.cor = cor;
		this.starting = ori;
		this.ending = dest;
		this.weight = 0.0;
	}

	public String getCor() {
		return this.cor;
	}

	public String getInvColor() {
		switch (this.cor) {
		case "E":
			return String.valueOf('W');
		case "W":
			return String.valueOf('E');
		case "N":
			return String.valueOf('S');
		case "S":
			return String.valueOf('N');
		case "I":
			return String.valueOf('I');
		default:
			System.out.println("ERROR : Wrong port Color.");
			return null;
		}
	}

	public Vertice getDestino() {

		return this.ending;

	}

	public Vertice getOrigem() {
		return this.starting;
	}

	public Vertice other(Vertice v) {
		if (v == starting)
			return ending;
		if (v == ending)
			return starting;
		return null; // error condition
	}

	public void incremWeight(double vol) {
		this.weight += vol;
	}

	public void decremWeight(double vol) {
		if (this.weight != 0)
			this.weight -= vol;
	}

	public double getWeight() {
		return this.weight;
	}

}

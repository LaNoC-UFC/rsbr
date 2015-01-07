package util;

import java.util.ArrayList;
import java.util.Comparator;

public class Path extends ArrayList<Vertice> implements Comparable<Path> {

	private static final long serialVersionUID = 1L;

	public static class MinWeight implements Comparator<Path> {

		@Override
		public int compare(Path p0, Path p1) {
			if(p0.getWeight() < p1.getWeight()) return -1;
			if(p0.getWeight() > p1.getWeight()) return +1;
			return 0;
		}

	}
	

	public static class MaxWeight implements Comparator<Path> {

		@Override
		public int compare(Path p0, Path p1) {
			if(p0.getWeight() < p1.getWeight()) return +1;
			if(p0.getWeight() > p1.getWeight()) return -1;
			return 0;
		}
		
	}
	

	public Path() {
		super();
	}

	public Path(Path p) {
		super(p);
	}

	// #Arestas
	private int numArestas() {
		return this.size() - 1;
	}

	public Vertice dst() {
		return (this.size() != 0) ? this.get(this.size() - 1) : null;
	}

	public Vertice src() {
		return (this.size() != 0) ? this.get(0) : null;
	}

	// Sum of Aresta's weight
	public double getWeight() {
		double weight = 0;

		for (int i = 0; i < numArestas(); i++)
			weight += this.get(i).getAresta(this.get(i + 1)).getWeight();

		return weight;
	}

	public void incremWeight() {
		for (int i = 0; i < numArestas(); i++)
			this.get(i).getAresta(this.get(i + 1)).incremWeight();
	}

	public void decremWeight() {
		for (int i = 0; i < numArestas(); i++)
			this.get(i).getAresta(this.get(i + 1)).decremWeight();
	}

	public int compareTo(Path other) {
		if (this.numArestas() < other.numArestas())
			return -1;

		if (this.numArestas() > other.numArestas())
			return 1;
		return 0;

	}

	public void printArestaWeight() {
		for (int i = 0; i < numArestas(); i++) {
			System.out.println(this.get(i).getNome());
			System.out.println(this.get(i).getAresta(this.get(i + 1))
					.getWeight());
		}
	}

	public String toString() {

		return "src: " + this.src().getNome() + ", dst: "
				+ this.dst().getNome() + ", size: " + this.numArestas()
				+ ", weight: " + this.getWeight();

	}

}

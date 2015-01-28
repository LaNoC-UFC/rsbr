package util;

import java.util.ArrayList;
import java.util.Comparator;

public class Path extends ArrayList<Vertice> implements Comparable<Path> {

	private static final long serialVersionUID = 1L;
	private double volume;

	public static class MinWeight implements Comparator<Path> {

		@Override
		public int compare(Path p0, Path p1) {
			if(p0.getWeight() < p1.getWeight()) return -1;
			if(p0.getWeight() > p1.getWeight()) return +1;
			return 0;
		}

	}
	
	public static class SrcDst implements Comparator<Path> {

		@Override
		public int compare(Path p0, Path p1) {
			int src = p0.src().getNome().compareTo(p1.src().getNome());
			int dst = p0.dst().getNome().compareTo(p1.dst().getNome());
			return (src != 0) ? src : dst;
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
	

	public class MedWeight implements Comparator<Path> {

		private double med; // peso total dividido pelo numero de paths
		
		public MedWeight(double med) {
			this.med = med;
		}
		
		@Override
		public int compare(Path p0, Path p1) {
			double err0 = Math.abs(p0.getWeight()-med);
			double err1 = Math.abs(p1.getWeight()-med);
			if(err0 < err1) return -1;
			if(err0 > err1) return +1;
			return 0;
		}
		
	}
	
	public class PropWeight implements Comparator<Path> {

		private double linkWeightMean; // peso medio dos links a ser mutiplicado pelo tamanho do path
		
		public PropWeight(double linkWeightMean) {
			this.linkWeightMean = linkWeightMean;
		}
		
		@Override
		public int compare(Path p0, Path p1) {
			double err0 = Math.abs(p0.getWeight()-linkWeightMean*(double)p0.numArestas());
			double err1 = Math.abs(p1.getWeight()-linkWeightMean*(double)p1.numArestas());
			if(err0 < err1) return -1;
			if(err0 > err1) return +1;
			return 0;
		}
		
	}
	
	public Path() {
		super();
		volume = 1;
	}

	public Path(Path p) {
		super(p);
		this.volume = p.volume;
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
	
	public int linksSize() {
		return this.size()-1;
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
			this.get(i).getAresta(this.get(i + 1)).incremWeight(volume);
	}

	public void decremWeight() {
		for (int i = 0; i < numArestas(); i++)
			this.get(i).getAresta(this.get(i + 1)).decremWeight(volume);
	}
	
	public double volume() {
		return volume;
	}

	public void setVolume(double vol) {
		this.volume = vol;
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

	/**
	 * Return the string of the volume and all routers of this path.
	 */
	public String toString() {

//		return "src: " + this.src().getNome() + ", dst: "
//				+ this.dst().getNome() + ", size: " + this.numArestas()
//				+ ", weight: " + this.getWeight();
		String pathLine = "" + volume + ":";
		for(Vertice v : this)
		{
			pathLine += " " + v.getNome();
		}
		return pathLine;

	}

}

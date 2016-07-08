package util;

import java.util.ArrayList;
import java.util.Comparator;

public class Path extends ArrayList<Vertex> implements Comparable<Path> {

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
			int src = p0.src().name().compareTo(p1.src().name());
			int dst = p0.dst().name().compareTo(p1.dst().name());
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
			double err0 = Math.abs(p0.getWeight()-linkWeightMean*(double)p0.edgesCount());
			double err1 = Math.abs(p1.getWeight()-linkWeightMean*(double)p1.edgesCount());
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

	private int edgesCount() {
		return this.size() - 1;
	}

	public Vertex dst() {
		return (this.size() != 0) ? this.get(this.size() - 1) : null;
	}

	public Vertex src() {
		return (this.size() != 0) ? this.get(0) : null;
	}
	
	public int linksSize() {
		return this.size()-1;
	}

	// Sum of Edge's weight
	public double getWeight() {
		double weight = 0;

		for (int i = 0; i < edgesCount(); i++)
			weight += this.get(i).edge(this.get(i + 1)).weight();

		return weight;
	}

	public void incremWeight() {
		for (int i = 0; i < edgesCount(); i++) {
			Edge edge = this.get(i).edge(this.get(i + 1));
			edge.setWeight(edge.weight() + volume);
		}
	}

	public void decremWeight() {
		for (int i = 0; i < edgesCount(); i++) {
			Edge edge = this.get(i).edge(this.get(i + 1));
			edge.setWeight(edge.weight() - volume);
		}
	}
	
	public double volume() {
		return volume;
	}

	public void setVolume(double vol) {
		this.volume = vol;
	}

	public int compareTo(Path other) {
		if (this.edgesCount() < other.edgesCount())
			return -1;

		if (this.edgesCount() > other.edgesCount())
			return 1;
		return 0;

	}

	/**
	 * Return the string of the volume and all routers of this path.
	 */
	public String toString() {

//		return "src: " + this.src().name() + ", dst: "
//				+ this.dst().name() + ", size: " + this.edgesCount()
//				+ ", weight: " + this.weight();
		String pathLine = "" + volume + ":";
		for(Vertex v : this)
		{
			pathLine += " " + v.name();
		}
		return pathLine;

	}

}

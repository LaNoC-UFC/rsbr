package util;

import java.util.ArrayList;
import java.util.Iterator;
/*import java.util.Spliterator;
import java.util.function.Consumer;*/


public class Path implements Iterable<Vertice>, Comparable<Path>
{
	private ArrayList<Vertice> itself;
	private ArrayList<Aresta> Arestas = new ArrayList<Aresta>(); 
	
	public Path() {
		itself = new ArrayList<Vertice>();
	}
	
	public Path(Path p) {
		itself = new ArrayList<Vertice>(p.itself);
	}
	
	public Path(ArrayList<Vertice> path) {
		itself = path;
	}
	
	public void initializeArestas() {
		for(int i=0; i<(itself.size()-1);i++)
			this.Arestas.add(itself.get(i).getAresta(itself.get(i+1)));
	}
	
	//#Vertices
	public int size() {
		return itself.size();
	}
	
	//#Arestas
	public int ArestasSize() {
		return itself.size()-1;
	}
	
	public Vertice dst() {
		assert itself.size() != 0;
		return itself.get(itself.size()-1);
	}

	public Vertice src() 
	{
		assert itself.size() != 0;
		return itself.get(0);
	}
	
	public void add(Vertice r) 
	{
		itself.add(r);
	}
	
	public void remove(Vertice r) 
	{
		itself.remove(r);
	}

	public void remove(int index) 
	{
		itself.remove(index);
	}
	
	public Vertice get(int index) 
	{
		return itself.get(index);
	}
	
	public int indexOf(Vertice Vertice) 
	{
		return itself.indexOf(Vertice);
	}
	
	//Sum of Aresta's weight
	public double getWeight() 	
	{
		
		double weight=0;
		
		for(int i=0; i<(itself.size()-1);i++)
			weight+=itself.get(i).getAresta(itself.get(i+1)).getWeight();
		
		return weight;
		
	}
	
	public void incremWeight() {
		for(int i=0; i<(itself.size()-1);i++)
			itself.get(i).getAresta(itself.get(i+1)).incremWeight();
	}
		
	public void decremWeight() {
		for(int i=0; i<(itself.size()-1);i++)
			itself.get(i).getAresta(itself.get(i+1)).decremWeight();
	}
	
	public int compareTo(Path other) 
	{    
		if (this.ArestasSize() < other.ArestasSize()) 
				return -1;

		if (this.ArestasSize() > other.ArestasSize()) 
				return 1;				
		return 0;
    
	}
	
	public void printArestaWeight() {
		for(int i=0; i<(itself.size()-1);i++) {
			System.out.println(itself.get(i).getNome());			
			System.out.println(itself.get(i).getAresta(itself.get(i+1)).getWeight());
		}
	}
	
	public String toString() {
		
		return "src: "+this.src().getNome()+", dst: "+this.dst().getNome()+", size: "+this.ArestasSize()+", weight: "+this.getWeight();
		
	}

	/*@Override
	public void forEach(Consumer<? super Vertice> arg0) {
		// TODO Auto-generated method stub
		
	}*/

	@Override
	public Iterator<Vertice> iterator() 
	{
		return itself.iterator();
	}

	/*@Override
	public Spliterator<Vertice> spliterator() 
	{
		return null;
	}*/		
}

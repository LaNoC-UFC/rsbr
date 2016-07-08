package util;

import java.util.ArrayList;

public class Graph {
	boolean debug = true;
	static private String[] ports = { "N", "S", "E", "W" };
	ArrayList<Vertex> vertices;
	ArrayList<Edge> edges;
	int dimX;
	int dimY;

	public Graph() {
		vertices = new ArrayList<>();
		edges = new ArrayList<>();
	}
	
	public Graph(int dim, double perc) {
		this(dim, dim, perc);
	}
	
	public Graph(int dim) {
		this(dim, dim, 0);
	}
	
	public Graph(int dX,int dY, double perc) {
		
		vertices = new ArrayList<>();
		edges = new ArrayList<>();
		
		dimX=dX;
		dimY=dY;
		int NumberOfEdges = (dimX-1)*dimY + dimX*(dimY-1);
		int nFalts = (int)Math.ceil((double)NumberOfEdges*perc);
		System.out.println("#Edges: "+NumberOfEdges);
		System.out.println("#Faults: "+nFalts);
			
		//Adiciona Vertices
		for(int x=0; x<dimX; x++)
			for(int y=0; y<dimY; y++)
				addVertice(x+"."+y);
		
		//Add Edges
		for(int y=0; y<dimY; y++)
			for(int x=0; x<dimX; x++)
			{
				if(contem(x+"."+(y+1)))
					addEdge(getVertice(x+"."+y), getVertice(x+"."+(y+1)), ports[0]);
				if(contem(x+"."+(y-1)))
					addEdge(getVertice(x+"."+y), getVertice(x+"."+(y-1)), ports[1]);
				if(contem((x+1)+"."+y)) 
					addEdge(getVertice(x+"."+y), getVertice((x+1)+"."+y), ports[2]);
				if(contem((x-1)+"."+y)) 
					addEdge(getVertice(x+"."+y), getVertice((x-1)+"."+y), ports[3]);
			}				
		
		//Adiciona Falhas e checa isolamento
		for(int i=0;i<nFalts;i++)
		{
			while(true)
			{
				int idx = (int)(Math.random()*((double) edges.size()));
				Edge toRemoveIndo = edges.get(idx);
				Edge toRemoveVindo = toRemoveIndo.destination().edge(toRemoveIndo.source());
				
				if (debug) System.out.println("Removing: "+toRemoveIndo.source().name()
						+"->"+toRemoveIndo.destination().name());
				
				removeEdge(toRemoveIndo);
				removeEdge(toRemoveVindo);
				
				if(haveIsolatedCores())
				{
					addEdge(toRemoveIndo);
					addEdge(toRemoveVindo);
				}
				else break;
			}
		}
	}
	
	public boolean haveIsolatedCores() {
		ArrayList<Vertex> alc = new ArrayList<Vertex>();
		//Escolha do 0.0 para ser o core inicial. Garantido a existencia do primeiro nodo em todas as topologias
		getVertice("0.0").checkIsolation(alc);
		
		//Se lista de alcancaveis for igual ao total de cores nao existe isolamento
		if(!(alc.size()==vertices.size())) return true;
		
    	return false;
	}

	private boolean contem(String vertex) {

		for (int i = 0; i < vertices.size(); i++) {

			if (vertex.equals(vertices.get(i).name())) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<Vertex> getVertices() {

		return this.vertices;

	}

	public ArrayList<Edge> getEdges() {
		return this.edges;
	}

	public Vertex getVertice(String nomeVertice) {
		Vertex vertex = null;

		for (Vertex v : this.vertices) {
			if (v.name().equals(nomeVertice))
				vertex = v;
		}

		if (vertex == null) {
			System.out.println("Vertex: " + nomeVertice + " nao encontrado");
			return null;
		}

		return vertex;
	}

	public void addVertice(String nome) {
		Vertex v = new Vertex(nome);
		vertices.add(v);
	}
	
	public void addEdge(Vertex origem, Vertex destino, String cor) {
		Edge e = new Edge(origem, destino, cor);
		origem.addAdjunct(e);
		edges.add(e);
	}
	
	private void addEdge(Edge toAdd) {
		toAdd.source().adjuncts().add(toAdd);
		edges.add(toAdd);
	}
	
	private void removeEdge(Edge toRemove) {
		toRemove.source().adjuncts().remove(toRemove);
		edges.remove(toRemove);
	}

	public String toString() {
		String r = "";
		System.out.println("Graph:");
		for (Vertex u : vertices) {
			r += u.name() + " -> ";
			for (Edge e : u.adjuncts()) {
				Vertex v = e.destination();
				r += v.name() + e.color() + ", ";
			}
			r += "\n";
		}
		return r;
	}
	
	public int dimX() {
		return dimX;
	}

	public int dimY() {
		return dimY;
	}

	public int indexOf(Vertex v) {
		return indexOf(v.name());
	}

	private int indexOf(String xy) {
		int x = Integer.parseInt(xy.split("\\.")[0]);
		int y = Integer.parseInt(xy.split("\\.")[1]);
		return x + y*this.dimX();
	}

}

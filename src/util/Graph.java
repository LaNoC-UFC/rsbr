package util;

import java.util.ArrayList;

public class Graph {
	boolean debug = true;
	static private String[] ports = { "N", "S", "E", "W" };
	ArrayList<Vertice> vertices;
	ArrayList<Aresta> arestas;
	int dimX;
	int dimY;

	public Graph() {
		vertices = new ArrayList<>();
		arestas = new ArrayList<>();
	}
	
	public Graph(int dim, double perc) {
		this(dim, dim, perc);
	}
	
	public Graph(int dim) {
		this(dim, dim, 0);
	}
	
	public Graph(int dX,int dY, double perc) {
		
		vertices = new ArrayList<>();
		arestas = new ArrayList<>();
		
		dimX=dX;
		dimY=dY;
		int nArests = (dimX-1)*dimY + dimX*(dimY-1);
		int nFalts = (int)Math.ceil((double)nArests*perc);
		System.out.println("#Arestas: "+nArests);
		System.out.println("#Faults: "+nFalts);
			
		//Adiciona Vertices
		for(int x=0; x<dimX; x++)
			for(int y=0; y<dimY; y++)
				addVertice(x+"."+y);
		
		//Adiciona Arestas
		for(int y=0; y<dimY; y++)
			for(int x=0; x<dimX; x++)
			{
				if(contem(x+"."+(y+1)))
					addAresta(getVertice(x+"."+y), getVertice(x+"."+(y+1)), ports[0]);
				if(contem(x+"."+(y-1)))
					addAresta(getVertice(x+"."+y), getVertice(x+"."+(y-1)), ports[1]);
				if(contem((x+1)+"."+y)) 
					addAresta(getVertice(x+"."+y), getVertice((x+1)+"."+y), ports[2]);	
				if(contem((x-1)+"."+y)) 
					addAresta(getVertice(x+"."+y), getVertice((x-1)+"."+y), ports[3]);	
			}				
		
		//Adiciona Falhas e checa isolamento
		for(int i=0;i<nFalts;i++)
		{
			while(true)
			{
				int idx = (int)(Math.random()*((double)arestas.size()));
				Aresta toRemoveIndo = arestas.get(idx);
				Aresta toRemoveVindo = toRemoveIndo.getDestino().getAresta(toRemoveIndo.getOrigem());
				
				if (debug) System.out.println("Removing: "+toRemoveIndo.getOrigem().getNome()
						+"->"+toRemoveIndo.getDestino().getNome());
				
				removeAresta(toRemoveIndo);
				removeAresta(toRemoveVindo);
				
				if(haveIsolatedCores())
				{
					AddAresta(toRemoveIndo);
					AddAresta(toRemoveVindo);
				}
				else break;
			}
		}
	}
	
	public boolean haveIsolatedCores() {
		ArrayList<Vertice> alc = new ArrayList<Vertice>();
		//Escolha do 0.0 para ser o core inicial. Garantido a existencia do primeiro nodo em todas as topologias
		getVertice("0.0").checkIsolation(alc);
		
		//Se lista de alcancaveis for igual ao total de cores nao existe isolamento
		if(!(alc.size()==vertices.size())) return true;
		
    	return false;
	}

	private boolean contem(String vertice) {

		for (int i = 0; i < vertices.size(); i++) {

			if (vertice.equals(vertices.get(i).getNome())) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<Vertice> getVertices() {

		return this.vertices;

	}

	public ArrayList<Aresta> getArestas() {
		return this.arestas;
	}

	public Vertice getVertice(String nomeVertice) {
		Vertice vertice = null;

		for (Vertice v : this.vertices) {
			if (v.getNome().equals(nomeVertice))
				vertice = v;
		}

		if (vertice == null) {
			System.out.println("Vertice: " + nomeVertice + " nao encontrado");
			return null;
		}

		return vertice;
	}

	public void addVertice(String nome) {
		Vertice v = new Vertice(nome);
		vertices.add(v);
	}
	
	public void addAresta(Vertice origem, Vertice destino, String cor) {
		Aresta e = new Aresta(origem, destino, cor);
		origem.addAdj(e);
		arestas.add(e);
	}
	
	private void AddAresta(Aresta toAdd) {
		toAdd.getOrigem().getAdj().add(toAdd);
		arestas.add(toAdd);
	}
	
	private void removeAresta(Aresta toRemove) {
		toRemove.getOrigem().getAdj().remove(toRemove);
		arestas.remove(toRemove);		
	}

	public String toString() {
		String r = "";
		System.out.println("Graph:");
		for (Vertice u : vertices) {
			r += u.getNome() + " -> ";
			for (Aresta e : u.getAdj()) {
				Vertice v = e.getDestino();
				r += v.getNome() + e.getCor() + ", ";
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

	public int indexOf(Vertice v) {
		return indexOf(v.getNome());
	}

	private int indexOf(String xy) {
		int x = Integer.parseInt(xy.split("\\.")[0]);
		int y = Integer.parseInt(xy.split("\\.")[1]);
		return x + y*this.dimX();
	}

}

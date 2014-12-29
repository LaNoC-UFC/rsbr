package util;

import java.util.ArrayList;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Graph 
{
	static private String[] ports = {"N","S","E","W"};
    ArrayList<Vertice> vertices;
    ArrayList<Aresta> arestas;
    int dimX;
    int dimY;
    
    public Graph() 
    {
        vertices = new ArrayList<>();
        arestas = new ArrayList<>();
    }
    
    public Graph(File topology)
    {
        vertices = new ArrayList<>();
        arestas = new ArrayList<>();
             
        try 
        {
            Scanner sc = new Scanner(new FileReader(topology));
            
            String[] lines = null, columns = null;
            if(sc.hasNextLine())
            	lines = sc.nextLine().split("; ");
            if(sc.hasNextLine())
            	columns = sc.nextLine().split("; ");
            
            dimX = lines[0].split(" ").length + 1;
            dimY = lines.length;
            
            for(int i = 0; i < dimX; i++) {
            	for(int j = 0; j < dimY; j++) {
            		String vertice = i + "." + j;
            		this.addVertice(vertice);
            		
            	}
            }
            
            for(int i = 0; i < lines.length; i++) 
            {
            	String [] line = lines[i].split(" ");
            	for(int j = 0; j < line.length; j++) 
            	{
            		if(line[j].charAt(0) == '0') //there is a link 
            		{ 
                		Vertice starting = this.getVertice(j + "." + (columns.length - i));
                		Vertice ending = this.getVertice((j+1) + "." + (columns.length - i));
                		this.addAresta(starting, ending, ports[2]);
                		this.addAresta(ending, starting, ports[3]);            			
            		}
            	}
            }

            for(int i = 0; i < columns.length; i++) 
            {
            	String [] column = columns[i].split(" ");
            	for(int j = 0; j < column.length; j++) 
            	{
            		if(column[j].charAt(0) == '0') //there is a link 
            		{ 
            			Vertice starting = this.getVertice(j + "." + (columns.length - i));
            			Vertice ending = this.getVertice(j + "." + (columns.length - 1 - i));
                		this.addAresta(starting, ending, ports[1]);
                		this.addAresta(ending, starting, ports[0]);    			
            		}
            	}
            }

            sc.close();
            
            
        } catch(Exception ex) { 
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setGraph() 
    {
    	//Dijkstra
        for(Vertice v : this.vertices) 
        {
           v.setPai(null);
           v.setVisitado(false);
           v.setDistancia(Integer.MAX_VALUE);
        }
    }
    
    
    
    public int contem(String vertice) 
    {
        
        for(int i=0;i<vertices.size();i++) 
        {
            
            if(vertice.equals(vertices.get(i).getNome())) 
            {                
                return i;               
            }    
        }       
        return -1;
    }
    
    public ArrayList<Vertice> getVertices() 
    {
        
        return this.vertices;
        
    }
    
    public ArrayList<Aresta> getArestas()
    {
    	return this.arestas;
    }
    
    public Vertice getVertice(String nomeVertice) 
    {
    	Vertice vertice  = null;
        
        for(Vertice v : this.vertices) 
        {
            if(v.getNome().equals(nomeVertice))
                vertice = v;
        }
        
        if(vertice == null) 
        {
            System.out.println("Vertice: " + nomeVertice + " não encontrado");
            return null;
        }
        
        return vertice;
   }
    
    private void addVertice(String nome) 
    {
        Vertice v = new Vertice(nome);
        vertices.add(v);
    }

    private void addAresta(Vertice origem, Vertice destino, String cor) 
    {
        Aresta e = new Aresta(origem, destino, cor);
        origem.addAdj(e);
        arestas.add(e);
    }
    
    public String toString() 
    {
        String r = "";
        System.out.println("Graph:");
        for (Vertice u : vertices) 
        {
            r += u.getNome() + " -> ";
            for (Aresta e : u.getAdj()) 
            {
            	Vertice v = e.getDestino();
                r += v.getNome() + e.getCor() + ", ";
            }
            r += "\n";
        }
        return r;
    }
    
    public ArrayList<Vertice> getVertices(String min, String max) 
    {
    	ArrayList<Vertice> sws = new ArrayList<Vertice>();
    	int xMin = Integer.valueOf(min.substring(0, 1));
    	int yMin = Integer.valueOf(min.substring(1, 2));
    	int xMax = Integer.valueOf(max.substring(0, 1));
    	int yMax = Integer.valueOf(max.substring(1, 2));
        for(int x = xMin; x <= xMax; x++)
        	for(int y = yMin; y <= yMax; y++)
        		sws.add(this.getVertice(x+""+y));

        if(sws.size() == 0) sws = null;
        return sws;
        
    }
    
    public int dimension()
    {
    	
    	return (int) Math.sqrt((double)this.vertices.size());
    }
    
    public int dimX()
    {
    	return dimX;
    }
    
    public int dimY()
    {
    	return dimY;
    }

}

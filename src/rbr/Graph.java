
package rbr;


import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Graph 
{
    static private String[] ports = {"N","S","E","W"};
    List<Router> vertices;
    List<Link> links;
    
    public Graph() 
    {
        vertices = new ArrayList<>();
        links = new ArrayList<>();
    }
    
    //Make Graph from topology file
    public Graph(File topology)
    {
        vertices = new ArrayList<>();
        links = new ArrayList<>();
             
        try {
            Scanner sc = new Scanner(new FileReader(topology));
            
            String[] lines = null, columns = null;
            if(sc.hasNextLine())
            	lines = sc.nextLine().split("; ");
            if(sc.hasNextLine())
            	columns = sc.nextLine().split("; ");
            
            for(int i = 0; i < lines.length; i++) {
            	for(int j = 0; j <= columns.length; j++) {
            		String vertice = i + "" + j;
            		this.addVertice(vertice);
            		
            	}
            }
            
            for(int i = 0; i < lines.length; i++) {
            	String [] line = lines[i].split(" ");
            	for(int j = 0; j < line.length; j++) {
            		if(line[j].charAt(0) == '0') { //there is a link
                		Router starting = this.getVertice("" + j + (columns.length - i));
                		Router ending = this.getVertice("" + (j+1) + (columns.length - i));
                		this.addAresta(starting, ending, ports[2]);
                		this.addAresta(ending, starting, ports[3]);            			
            		}
            	}
            }

            for(int i = 0; i < columns.length; i++) {
            	String [] column = columns[i].split(" ");
            	for(int j = 0; j < column.length; j++) {
            		if(column[j].charAt(0) == '0') { //there is a link
                		Router starting = this.getVertice(j + "" + (columns.length - i));
                		Router ending = this.getVertice(j + "" + (columns.length - 1 - i));
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
        for(Router v : this.vertices) 
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
        
    public List<Router> getVertices() 
    {
        
        return this.vertices;
        
    }
    
    public List<Link> getLinks()
    {
    	return this.links;
    }
    
    public Router getVertice(String nomeVertice) {
        Router vertice  = null;
        
        for(Router v : this.vertices) {
            if(v.getNome().equals(nomeVertice))
                vertice = v;
        }
        
        if(vertice == null) {
            System.out.println("Vertice: " + nomeVertice + " não encontrado");
            return null;
        }
        
        return vertice;
   }

    Router addVertice(String nome) {
        Router v = new Router(nome);
        vertices.add(v);
        return v;
    }

    Link addAresta(Router origem, Router destino, String cor) {
        Link e = new Link(origem, destino, cor);
        origem.addAdj(e);
        links.add(e);
        return e;
    }

    @Override
    public String toString() {
        String r = "";
        System.out.println("Graph:");
        for (Router u : vertices) {
            r += u.getNome() + " -> ";
            for (Link e : u.getAdj()) {
                Router v = e.getDestino();
                r += v.getNome() + e.getCor() + ", ";
            }
            r += "\n";
        }
        return r;
    }
}

package sbr;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Graph {
    static private String[] ports = {"N","S","E","W"}; //Porta para ser representada pelo arco (cor)
    private List<Switch> switches;
    private List<Link> links;
    
    public Graph() 
    {
        switches = new ArrayList<>();
        links = new ArrayList<>();
    }
    
    public void setGraph() 
    {
        //Seta características relacionadas a Dijkstra
        for(Switch v : this.switches) 
        {
           v.setPai(null);
           v.setVisitado(false);
           v.setDistancia(99999);
        }
    }
    
    public int contem(String vertice) {
        
        for(int i=0;i<switches.size();i++) {
            
            if(vertice.equals(switches.get(i).getNome())) {                
                return i;               
            }    
        }       
        return -1;
    }
    
    public List<Switch> getSwitches() {
        
        return this.switches;
        
    }
    
    public List<Switch> getSwitches(String min, String max) {
    	List<Switch> sws = new ArrayList<Switch>();
    	int xMin = Integer.valueOf(min.substring(0, 1));
    	int yMin = Integer.valueOf(min.substring(1, 2));
    	int xMax = Integer.valueOf(max.substring(0, 1));
    	int yMax = Integer.valueOf(max.substring(1, 2));
        for(int x = xMin; x <= xMax; x++) {
        	for(int y = yMin; y <= yMax; y++) {
        		sws.add(this.getSwitch(x+""+y));
        	}
        }
        if(sws.size() == 0) sws = null;
        return sws;
        
    }
    
    public Switch getSwitch(String nomeSwitch) {
        Switch vertice = null;
        
        for(Switch v : this.switches) {
            if(v.getNome().equals(nomeSwitch))
                vertice = v;
        }
        
        if(vertice == null) {
            System.out.println("Switch: " + nomeSwitch + " não encontrado");
        }
        
        return vertice;
   }

    Switch addSwitch(String nome) {
        Switch v = new Switch(nome);
        switches.add(v);
        return v;
    }

    Link addLink(Switch starting, Switch ending, String cor) {
        Link e = new Link(starting, ending, cor);
        starting.addAdj(e);
        links.add(e);
        return e;
    }

    @Override
    public String toString() {
        String r = "";
        r += "Grafo gerado:\n";
        for (Switch u : switches) {
            r += u.getNome() + " -> ";
            for (Link e : u.getAdj()) {
                Switch v = e.getDestino();
                r += v.getNome() + e.getCor() + ", ";
            }
            r += "\n";
        }
        return r;
    }
    
    /*
     * Cria graph a partir de um arquivo de topologia
     * Switchs representam os switches e as arestas os links
     * Arestas possuem cor para identifar qual porta ela está representando 
     */
    public Graph(String FileName) {
        switches = new ArrayList<>();
        links = new ArrayList<>();

        Switch starting = new Switch("aa"); //So pra inicializar
        Switch ending;
        
        File topology = new File(FileName); //Arquivo que descreve a topologia
        
        try {
            Scanner sc = new Scanner(new FileReader(topology));
            
            while(sc.hasNextLine()) {
                String[] vertice = sc.nextLine().split(" ");
                
                for(int i=0; i<vertice.length; i++) {                    
                    if(vertice[i].equals("XX")) //XX representa falta de ligação em determinada porta
                        continue;
                    
                    int b = this.contem(vertice[i]);
                    if(b == -1) { //Switch não pertence ao graph
                        if(i==0) {
                        	starting = this.addSwitch(vertice[i]);
                        	continue;
                        }
                        else {
                            ending = this.addSwitch(vertice[i]);
                        }
                    }
                    else {
                    	if(i==0) {
                    		starting = this.getSwitches().get(b);
                            continue;
                        }
                        else {
                            ending = this.getSwitches().get(b);
                        }
                    }                    
                    if(i != 0) {
                        this.addLink(starting, ending, ports[i-1]); //adiciona aresta ao graph
                    }
                }
            }
            sc.close();
            
            
        } catch(Exception ex) { 
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
        }
 
    }
    
    public Graph(File topology) {

        switches = new ArrayList<>();
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
            		this.addSwitch(vertice);
            		
            	}
            }
            
            for(int i = 0; i < lines.length; i++) {
            	String [] line = lines[i].split(" ");
            	for(int j = 0; j < line.length; j++) {
            		if(line[j].charAt(0) == '0') { //there is a link
                		Switch starting = this.getSwitch("" + j + (columns.length - i));
                		Switch ending = this.getSwitch("" + (j+1) + (columns.length - i));
                		this.addLink(starting, ending, ports[2]);
                		this.addLink(ending, starting, ports[3]);            			
            		}
            	}
            }

            for(int i = 0; i < columns.length; i++) {
            	String [] column = columns[i].split(" ");
            	for(int j = 0; j < column.length; j++) {
            		if(column[j].charAt(0) == '0') { //there is a link
                		Switch starting = this.getSwitch(j + "" + (columns.length - i));
                		Switch ending = this.getSwitch(j + "" + (columns.length - 1 - i));
                		this.addLink(starting, ending, ports[1]);
                		this.addLink(ending, starting, ports[0]);    			
            		}
            	}
            }

            sc.close();
            
            
        } catch(Exception ex) { 
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
        }
 
    }
}

package sbr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Switch {//implements Comparable<Switch>{

	private	boolean starting;
	private boolean visited;
	private boolean tvisited;
	private boolean terminal;
	private String restrictions;
	private Segment seg;
	private int snet;

    private String nome;
    private ArrayList<Link> adj;

    private Switch pai; //Representa a prescedência
    private int distancia;
    private boolean visitado;

    public Switch(String name) {
		starting = false;
		visited = false;
		tvisited = false;
		terminal = false;
		seg = null;
		snet = -1;
		nome = name;
		adj = new ArrayList<Link>();
		restrictions = nome + ": I{} N{} S{} E{} W{}";
	}
    
    public void addRestriction(String op, String rest)
    {
    	String op1 = op+"{";
    	this.restrictions = restrictions.substring(0,restrictions.indexOf(op1)+2)+rest+restrictions.substring(restrictions.indexOf(op1)+2);    	    
    }
    
    public String getRestrictions()
    {
    	return this.restrictions;
    }
	
    //Ordem Alfabética
    /*public static String groupStr(String input)
    {
        char[] ip1  = input.toCharArray();
        Arrays.sort(ip1);
        
        return String.valueOf(ip1);
    }*/
        
    
    private String IntToBitsString(int a, int size)
    {
            String out = Integer.toBinaryString(a);
            while(out.length()<size)
                out = "0" + out;
            
            return out;
    }
    
    public String GetBitsOp(String op)
    {
        String outOp;
        
        switch(op)
            {
                case "E":
                    outOp = IntToBitsString(1, 5);
                    break;
                case "W":
                    outOp = IntToBitsString(2, 5);
                    break;
                case "N":
                    outOp = IntToBitsString(4, 5);
                    break;
                case "S":
                    outOp = IntToBitsString(8, 5);
                    break;
                case "I":
                    outOp = IntToBitsString(16, 5);
                    break;
                default:
                    outOp = IntToBitsString(0, 5);
            }
        
        return outOp;
    }
    
                   
    //Dirá se o vértice é alcançavel dependendo das restrições do algoritmo roteamento
/*public boolean Reaches(Switch destino) 
{
    boolean reaches = true;
    if(this.pai == null) return true;
    String corLinkPai = this.pai.getLink((Switch)this).getCor();
    String corLinkAtual = this.getLink(destino).getCor();
        
    //Restrições de roteamento - Se vier de N ou S não pode ir nem pra W nem pra E
    if((corLinkPai.equals("N")||corLinkPai.equals("S"))&&(corLinkAtual.equals("W")||corLinkAtual.equals("E")))
    {		
        reaches = false;
    }
        
    return reaches;
}*/
	
	public Link getLink(Switch destino) {
	    for(Link v : adj) {
	    	if(v.getDestino().getNome().equals(destino.getNome())) {            
	            return v;
	        }
	    }
	    return null;
		
	}
	
	public ArrayList<Link> getLinks() {
		return adj;
	}
	
    public boolean verificarVisita() {
        
        return this.visitado;
        
    }
    
    public void visitar() {
        
        this.visitado = true;
        
    }
    
    public void setVisitado(boolean visitado) 
    {
        this.visitado = visitado;        
    }
        

    void addAdj(Link e) {
            
        adj.add(e);
    }
        
    public void setDistancia(int distancia) {
        
        this.distancia = distancia;
            
    }
    
    public int getDistancia() {

        return this.distancia;
            
    }
        
    public void setPai (Switch Pai) {
            
        this.pai = Pai;
            
    }
        
    public Switch getPai () {
            
        return this.pai;
            
    }
        
    public List<Link> getAdj() {
            
        return this.adj;
            
    }
    
    public String getNome (){
        
        return this.nome;
        
    }
    
    public int getSubNet() {
    	return this.snet;
    }
    
    public Segment getSegment() {
    	return this.seg;
    }

    /*@Override
    public int compareTo(Switch outroSwitch) 
    {    
        if (this.distancia < outroSwitch.distancia) {
            return -1;
        }
        if (this.distancia > outroSwitch.distancia) {
            return 1;
        }
        return 0;
    }
    */
	public boolean isStart() {
		return starting;
	}
			
	public boolean isVisited() {
		return visited;
	}

	public boolean isTVisited() {
		return tvisited;
	}

	public boolean isTerminal() {
		return terminal;
	}
	
	public void setStart() {
		starting = true;
	}
	
	public void setVisited() {
		visited = true;
	}
	
	public void setTVisited() {
		tvisited = true;
	}
	
	public void setTerminal() {
		terminal = true;
	}
	
	public void unsetStart() {
		starting = false;
	}
	
	public void unsetTerminal() {
		terminal = false;
	}
	
	public void unsetTVisited() {
		tvisited = false;
	}
	
	public void setSegment(Segment sg) {
		seg = sg;
	}
	
	public void setSubNet(int sn) {
		snet = sn;
	}
	
	public boolean belongsTo(Segment sg) {
		return (sg == seg);
	}
	
	public boolean isIn(String min, String max) {
		String[] Min = min.split("\\.");
		int xMin = Integer.valueOf(Min[0]);
		int yMin = Integer.valueOf(Min[1]);
		String[] Max = max.split("\\.");
		int xMax = Integer.valueOf(Max[0]);
		int yMax = Integer.valueOf(Max[1]);
		String[] xy = nome.split("\\.");
    	int x = Integer.valueOf(xy[0]);
    	int y = Integer.valueOf(xy[1]);
    	return (x <= xMax && x >= xMin && y <= yMax && y >= yMin);
	}
	
	public boolean belongsTo(int sn) {
		return (sn == snet);
	}
	
	/*
	 * a set of links attached to the current switch is built, this set only
	 * includes links not marked as visited, nor as tvisited
	 */
	public ArrayList<Link> suitableLinks() {
		if(adj.isEmpty())
			return null;
		ArrayList<Link> slinks = new ArrayList<>();
		for(Link ln : this.adj) {
			if(!ln.isVisited() && !ln.isTVisited() && !(ln.getDestino().isTVisited() && !ln.getDestino().isStart())) {
				slinks.add(ln);
			}
		}
		return (slinks.isEmpty())? null : slinks;
	}
	
	public ArrayList<Link> suitableLinks(String min, String max) {
		if(adj.isEmpty())
			return null;
		ArrayList<Link> slinks = new ArrayList<>();
		for(Link ln : this.adj) {
			if(!ln.isVisited() && !ln.isTVisited() && ln.getDestino().isIn(min, max) && !(ln.getDestino().isTVisited() && !ln.getDestino().isStart())) {
				slinks.add(ln);
			}
		}
		return (slinks.isEmpty())? null : slinks;
	}

	public List<Switch> getNeighbors() {
		if(adj.isEmpty())
			return null;
		List<Switch> neighbors = new ArrayList<>();
		for(Link ln : adj) {
			neighbors.add(ln.other(this));
		}
		return neighbors;
	}

}

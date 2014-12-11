package sbr;

public class Link {
	private boolean visited;
	private boolean tvisited;
	private Segment seg;
	private int snet;
    protected Switch starting;
    protected Switch ending;
    private int peso;
    private String cor;


	public Link() {
        this.cor = null;
        this.starting = null;
        this.ending = null;
		visited = false;
		tvisited = false;
		seg = null;
		snet = -1;
	}
	
	public Link(Switch ori, Switch dest, String cor) {
        this.cor = cor;
        this.starting = ori;
        this.ending = dest;
        this.peso = 1;
		visited = false;
		tvisited = false;
		seg = null;
		snet = -1;
	}
	
    public String getCor () {        
        return this.cor;
    }
    
    public String getInvColor()
    {
    	switch(this.cor)
    	{
        case "E":
            return String.valueOf('W');
        case "W":
            return String.valueOf('E');
        case "N":
            return String.valueOf('S');
        case "S":
            return String.valueOf('N');
        case "I":
            return String.valueOf('I');
        default:
            System.out.println("ERROR : Wrong port Color.");
            return null;
    	}
    }
        
    public void setPeso(int peso) {
            
        this.peso = peso;
            
    }
        
    public int getPeso() {
            
        return this.peso;
            
    }
        
    public Switch getDestino() {
            
        return this.ending;
               
    }
    
    public Switch getOrigem() {
    	return this.starting;
    }
    
    public boolean isVisited() {
		return visited;
	}

	public boolean isTVisited() {
		return tvisited;
	}

	public void setVisited() {
		//ending.getLink(starting).setVisited(); // from ending to starting
		visited = true; // from starting to ending
	}
	
	public void setTVisited() {
		//Link ln = ending.getLink(starting);
		//ln.setTVisited(); // from ending to starting
		tvisited = true;
	}
	
	public void unsetTVisited() {
		//ending.getLink(starting).unsetTVisited(); // from ending to starting
		tvisited = false;
	}
	
	public void setSegment(Segment sg) {
		//ending.getLink(starting).setSegment(sg); // from ending to starting
		seg = sg;
	}
	
	public void setSubNet(int sn) {
		//ending.getLink(starting).setSubNet(sn); // from ending to starting
		snet = sn;
	}
	
	public boolean belongsTo(Segment sg) {
		return (sg == seg);
	}
	
	public boolean belongsTo(int sn) {
		return (sn == snet);
	}
	
    public Switch other(Switch v) {
    	if(v == starting)
    		return ending;
    	if(v == ending)
    		return starting;
    	return null; // error condition
    }

}


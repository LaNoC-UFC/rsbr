package util;

public class Aresta 
{
	private boolean visited;
	private boolean tvisited;
	private sbr.Segment seg;
	private int snet;
    protected Vertice starting;
    protected Vertice ending;
    private int peso;
    private String cor;
    private double weight;
    
    public Aresta() 
	{
        this.cor = null;
        this.starting = null;
        this.ending = null;
		visited = false;
		tvisited = false;
		seg = null;
		snet = -1;
	}
	
	public Aresta(Vertice ori, Vertice dest, String cor) 
	{
        this.cor = cor;
        this.starting = ori;
        this.ending = dest;
        this.peso = 1;
        this.weight=0.0;
		visited = false;
		tvisited = false;
		seg = null;
		snet = -1;
	}
	
	public String getCor () 
	{        
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
        
    public void setPeso(int peso) 
    {
            
        this.peso = peso;
            
    }
        
    public int getPeso() 
    {
            
        return this.peso;
            
    }
        
    public Vertice getDestino() 
    {
               
        return this.ending;
               
    }
    
    public Vertice getOrigem() 
    {
    	return this.starting;
    }
    
    public boolean isVisited() 
    {
		return visited;
	}

	public boolean isTVisited() 
	{
		return tvisited;
	}

	public void setVisited() 
	{	
		// from starting to ending
		visited = true; 
	}
	
	public void setTVisited() 
	{		
		// from ending to starting
		tvisited = true;
	}
	
	public void unsetTVisited() 
	{
		//ending.getLink(starting).unsetTVisited(); // from ending to starting
		tvisited = false;
	}
	
	public void setSegment(sbr.Segment sg) 
	{
		//ending.getLink(starting).setSegment(sg); // from ending to starting
		seg = sg;
	}
	
	public void setSubNet(int sn) 
	{
		//ending.getLink(starting).setSubNet(sn); // from ending to starting
		snet = sn;
	}
	
	public boolean belongsTo(sbr.Segment sg) 
	{
		return (sg == seg);
	}
	
	public boolean belongsTo(int sn) 
	{
		return (sn == snet);
	}
	
    public Vertice other(Vertice v) 
    {
    	if(v == starting)
    		return ending;
    	if(v == ending)
    		return starting;
    	return null; // error condition
    }
    
    public void setWeight(double weight)
    {
    	this.weight=weight;
    }
    
    public void incremWeight()
    {
    	this.weight++;
    }
    
    public void decremWeight()
    {
    	if(this.weight!=0)
    		this.weight--;
    }
    
    public double getWeight() 
    {
    	return this.weight;
    }
    


}

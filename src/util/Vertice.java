package util;

import rbr.Region;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;




public class Vertice implements Comparable<Vertice>
{
	private	boolean starting;
	private boolean visited;
	private boolean tvisited;
	private boolean terminal;
	//private String restrictions;
	private String[] restrictions = {"","","","",""};; //[0]-I [1]-N [2]-S [3]-E [4]-W
	private sbr.Segment seg;
	private int snet;
	
	private Vertice pai; 
    private int distancia;
    private boolean visitado;
	
	private ArrayList<rbr.RoutingPath> routingPaths = new ArrayList<>();
    private ArrayList<rbr.RoutingPath> combinedOutputs = new ArrayList<>();
    private String nome;
    public ArrayList<Aresta> adj;    
    public ArrayList<rbr.Region> Regions = new ArrayList<>();
	public ArrayList<Vertice> preds = new ArrayList<>();
	
	
	public Vertice(String name) 
	{
		starting = false;
		visited = false;
		tvisited = false;
		terminal = false;
		seg = null;
		snet = -1;
		nome = name;
		adj = new ArrayList<Aresta>();
		//restrictions = nome + ": I{} N{} S{} E{} W{}";
	}
	
	public void initRoutingOptions()
	{
		this.routingPaths = new ArrayList<>();
	}
	
	public void initRegions()
	{
		this.Regions = new ArrayList<>();
	}
	
	public void addRestriction(String op, String rest)
    {
    	/*String op1 = op+"{";
    	this.restrictions = restrictions.substring(0,restrictions.indexOf(op1)+2)+rest+restrictions.substring(restrictions.indexOf(op1)+2);*/
    	switch(op)
    	{
    		case "I":
    			restrictions[0] = restrictions[0]+""+rest; 
    			break;
    		case "N":
    			restrictions[1] = restrictions[1]+""+rest;
    			break;
    		case "S":
    			restrictions[2] = restrictions[2]+""+rest;
    			break;
    		case "E":
    			restrictions[3] = restrictions[3]+""+rest;
    			break;
    		case "W":
    			restrictions[4] = restrictions[4]+""+rest;
    			break;
    	}
    }
	
	public String getRestriction(String op)
    {
    	switch(op)
    	{
    		case "I":
    			return restrictions[0];     			
    		case "N":
    			return restrictions[1];
    		case "S":
    			return restrictions[2];
    		case "E":
    			return restrictions[3];
    		case "W":
    			return restrictions[4];
    		default:
    			return null;
    	}
    }
    
    public String[] getRestrictions()
    {
    	return this.restrictions;
    }
    
    private String IntToBitsString(int a, int size)
    {
            String out = Integer.toBinaryString(a);
            while(out.length()<size)
                out = "0" + out;
            
            return out;
    }
    
    public String opToBinary(String ports)
    {
        char[] outOp= {'0','0','0','0','0'};
        char[] port = ports.toCharArray();
        
        for(char pt : port)
        {
        	switch(pt)
        	{
                case 'E':
                    outOp[4]= '1';
                    break;
                case 'W':
                    outOp[3]= '1';
                    break;
                case 'N':
                    outOp[2] = '1';
                    break;
                case 'S':
                    outOp[1] = '1';
                    break;
                case 'I':
                    outOp[0] = '1';
                    break;                
        	}
        }
        
        return String.valueOf(outOp);
    }
    
    public Aresta getAresta(Vertice destino) 
    {
	    for(Aresta v : adj)
	    	if(v.getDestino().getNome().equals(destino.getNome()))             
	            return v;
	        
	    return null;
		
	}
    
    public ArrayList<Aresta> getArestas() 
    {
		return adj;
	}
	
    public boolean verificarVisita() 
    {
        
        return this.visitado;
        
    }
    
    public void visitar() 
    {
        
        this.visitado = true;
        
    }
    
    public void setVisitado(boolean visitado) 
    {
        this.visitado = visitado;        
    }
        

    void addAdj(Aresta e) 
    {
            
        adj.add(e);
    }
        
    public void setDistancia(int distancia) 
    {
        
        this.distancia = distancia;
            
    }
    
    public int getDistancia() 
    {

        return this.distancia;
            
    }
        
    public void setPai (Vertice Pai) 
    {
            
        this.pai = Pai;
            
    }
        
    public Vertice getPai () 
    {
            
        return this.pai;
            
    }
        
    public ArrayList<Aresta> getAdj() 
    {
            
        return this.adj;
            
    }
    
    public Aresta getAdj(String color)
    {
    	for(Aresta a : this.adj)
    		if( a.getCor().equals(color))
    			return a;
    	
    	System.out.println("ERROR : There isn't a Op " + color + "?");
    	return null;
    }
    
    public String getNome ()
    {
        
        return this.nome;
        
    }
    
    public int getSubNet() 
    {
    	return this.snet;
    }
    
    public sbr.Segment getSegment() 
    {
    	return this.seg;
    }

	public boolean isStart() 
	{
		return starting;
	}
			
	public boolean isVisited() 
	{
		return visited;
	}

	public boolean isTVisited() 
	{
		return tvisited;
	}

	public boolean isTerminal() 
	{
		return terminal;
	}
	
	public void setStart() 
	{
		starting = true;
	}
	
	public void setVisited() 
	{
		visited = true;
	}
	
	public void setTVisited() 
	{
		tvisited = true;
	}
	
	public void setTerminal() 
	{
		terminal = true;
	}
	
	public void unsetStart() 
	{
		starting = false;
	}
	
	public void unsetTerminal() 
	{
		terminal = false;
	}
	
	public void unsetTVisited() 
	{
		tvisited = false;
	}
	
	public void setSegment(sbr.Segment sg) 
	{
		seg = sg;
	}
	
	public void setSubNet(int sn) {
		snet = sn;
		
	}
	
	public boolean belongsTo(sbr.Segment sg) 
	{
		return (sg == seg);
	}
	
	public boolean isIn(String min, String max) 
	{
    	int xMin = Integer.valueOf(min.split("\\.")[0]);
    	int yMin = Integer.valueOf(min.split("\\.")[1]);
    	int xMax = Integer.valueOf(max.split("\\.")[0]);
    	int yMax = Integer.valueOf(max.split("\\.")[1]);
    	
    	int x = Integer.valueOf(nome.split("\\.")[0]);
    	int y = Integer.valueOf(nome.split("\\.")[1]);
    	
    	return (x <= xMax && x >= xMin && y <= yMax && y >= yMin);
	}
	
	public boolean belongsTo(int sn) 
	{
		return (sn == snet);
	}
	
	/*
	 * a set of links attached to the current switch is built, this set only
	 * includes links not marked as visited, nor as tvisited
	 */
	public ArrayList<Aresta> suitableLinks() 
	{
		if(adj.isEmpty())
			return null;
		ArrayList<Aresta> slinks = new ArrayList<>();
		for(Aresta ln : this.adj)
			if(!ln.isVisited() && !ln.isTVisited() && !(ln.getDestino().isTVisited() && !ln.getDestino().isStart()))
				slinks.add(ln);

		return (slinks.isEmpty())? null : slinks;
	}
	
	public ArrayList<Aresta> suitableLinks(String min, String max) 
	{
		if(adj.isEmpty())
			return null;
		
		ArrayList<Aresta> slinks = new ArrayList<>();
		for(Aresta ln : this.adj)
			if(!ln.isVisited() && !ln.isTVisited() && ln.getDestino().isIn(min, max) && !(ln.getDestino().isTVisited() && !ln.getDestino().isStart()))
				slinks.add(ln);

		return (slinks.isEmpty())? null : slinks;
	}

	public ArrayList<Vertice> getNeighbors() 
	{
		if(adj.isEmpty())
			return null;
		ArrayList<Vertice> neighbors = new ArrayList<>();
		for(Aresta ln : adj) {
			neighbors.add(ln.other(this));
		}
		return neighbors;
	}
	
	public ArrayList<Region> getRegions() 
	{
        return Regions;
    }
	
	public void setRegions(ArrayList<Region> Regions) 
	{
        this.Regions = Regions;
    }
	
	public void addRegion(String ip,ArrayList<String> dsts, String op)
    {
        rbr.Region region = new rbr.Region(ip,dsts,op);
        this.Regions.add(region);        
    }
	
	public boolean CheckRegion(String ip, String op)
    {
        boolean check = false;
        
        for(rbr.Region region : this.Regions)
            if(region.getIp().equals(ip)&&region.getOp().equals(op)||(ip.equals("")&&op.equals("")))
                check = true;
        
        return check;
    }
	
	public void addRP (String ip, String dst, String op) 
    {
    	ip = sortStrAlf(ip);
    	op = sortStrAlf(op);
    	if(!this.AlreadyExists(ip,dst,op))
    	{
    		rbr.RoutingPath RP = new rbr.RoutingPath(ip,dst,op);
    		this.routingPaths.add(RP);
    	}
    }
	
	private boolean AlreadyExists(String ip, String dst, String op)
    {
        for(int a=0;a<this.routingPaths.size();a++)           
            if(this.routingPaths.get(a).getIp().equals(ip)&&this.routingPaths.get(a).getDst().equals(dst)&&this.routingPaths.get(a).getOp().equals(op))
                return true;

        return false;
    }

    public static String sortStrAlf(String input)
    {
        char[] ip1  = input.toCharArray();
        Arrays.sort(ip1);
        
        return String.valueOf(ip1);
    }
    
    public boolean reaches(Vertice dest)
    {
    	return this.reaches(dest, "I");
    }

    private boolean reaches(Vertice dest, String ipColor) 
    {
    	if(dest == this)
    		return true;
    	String opColor = this.getOpColor(dest, ipColor); 
    	if(opColor == null)
    		return false;
    	return this.getAdj(opColor).getDestino().reaches(dest, getAdj(opColor).getInvColor());	
    }
    
    
    private String getOpColor(Vertice dest, String ipColor)
    {
    	String router = dest.getNome();
        for(rbr.Region reg : this.Regions)
            if(reg.contains(router) && reg.getIp().contains(ipColor))
                return (reg.getOp().substring(0, 1)); 

        System.err.println("ERROR : There isn't Op on "+this.getNome()+" for "+dest.getNome() + " " + ipColor);
        return null;
    }
    
    public ArrayList<rbr.RoutingPath> getRoutingPaths() 
    {
        return routingPaths;
    }

    public void setRoutingPaths(ArrayList<rbr.RoutingPath> routingPaths) 
    {
        this.routingPaths = routingPaths;
    }
    
    public boolean hasSuperposition()
    {
    	for(int i = 0; i < this.Regions.size(); i++)
        	for(int j = i+1; j < this.Regions.size(); j++)
        		if(this.Regions.get(i).hasSuperpositionWith(this.Regions.get(j)) && this.Regions.get(i).sharesIpWith(this.Regions.get(j)))
        			return true;
    		
    	return false;
    }
    
    public ArrayList<String> getIpSets() 
    {
    	ArrayList<String> ips = new ArrayList<String>();
    	for(rbr.RoutingPath rp : this.getRoutingPaths()) //right collection?
    	{
    		if(!ips.contains(rp.getIp())) 
    		{
    			ips.add(rp.getIp());
    		}
    	}
    	return ips;
    }
    
    public ArrayList<rbr.RoutingPath> getCombinedOutputs() 
    {
        return combinedOutputs;
    }

    public void setCombinedOutputs(ArrayList<rbr.RoutingPath> combinedOutputs) 
    {
        this.combinedOutputs = combinedOutputs;
    }
    
    public void PrintRegions(float[] stats,BufferedWriter bw, int nBits)
    {              
    	int maxRegion = (int)stats[0];    	
    	try 
    	{        
    		bw.append("\n -- Router " + this.getNome() + "\n");
    		bw.append("(");
        
    		for(int a=0;a<this.Regions.size(); a++)
    		{            
    			int Xmin = Integer.parseInt(this.Regions.get(a).getDownLeft().split("\\.")[0]);                        
    			int Ymin = Integer.parseInt(this.Regions.get(a).getDownLeft().split("\\.")[1]);            
    			int Xmax = Integer.parseInt(this.Regions.get(a).getUpRight().split("\\.")[0]);            
    			int Ymax = Integer.parseInt(this.Regions.get(a).getUpRight().split("\\.")[1]);
            
    			//Write on file
    			String outLine = "(\""+opToBinary(this.Regions.get(a).getIp())+IntToBitsString(Xmin, nBits)+IntToBitsString(Ymin, nBits)+IntToBitsString(Xmax, nBits)+IntToBitsString(Ymax, nBits)+opToBinary(this.Regions.get(a).getOp())+"\")";                    
            
    			bw.append(outLine);

            
    			if(a!=this.Regions.size()-1 || (a==this.Regions.size()-1)&&(this.Regions.size()<maxRegion))
    			{
    				bw.append(",");
    			}                                               
    			bw.newLine();            
    		}
      
    		//If less then Max Region    		
    		if(this.Regions.size()<maxRegion)
    		{
    			int a = this.Regions.size();
    			while(a<maxRegion)
    			{
    				a++;
    				String outLine = "(\""+IntToBitsString(0,4*nBits+10)+"\")";
    				bw.append(outLine);
              
    				if(a<maxRegion)
    					bw.append(",");
              
    				bw.newLine();
    			}
    		}
        
    		bw.append(")");
    		//bw.flush();
    		//bw.close();
    	}
    	catch (IOException ex) 
    	{
    		Logger.getLogger(Vertice.class.getName()).log(Level.SEVERE, null, ex);
    	}                                                               
    }
    
    public int compareTo(Vertice outroVertice) 
    {    
        if (this.distancia < outroVertice.distancia) {
            return -1;
        }
        if (this.distancia > outroVertice.distancia) {
            return 1;
        }
        return 0;
    }

}

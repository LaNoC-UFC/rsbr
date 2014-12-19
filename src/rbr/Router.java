
package rbr;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Router implements Comparable<Router> 
{
    
    private ArrayList<RoutingPath> routingPaths = new ArrayList<>();
    private ArrayList<RoutingPath> combinedOutputs = new ArrayList<>();
    private String name;
    private List<Link> adj;    
    ArrayList<Region> Regions = new ArrayList<>();
	ArrayList<Router> preds = new ArrayList<>();
    
    private Router pai; 
    private int distancia;
    private boolean visitado;

    Router(String name) 
    {           
        this.name = name;
        this.adj = new ArrayList<>();
    }
    
    public void addRegion(String ip,ArrayList<String> dsts, String op)
    {
        Region region = new Region(ip,dsts,op);
        this.Regions.add(region);
        
    }    
    
    public boolean CheckRegion(String ip, String op)
    {
        boolean check = false;
        
        for(Region region : this.Regions)
        {
            if(region.getIp().equals(ip)&&region.getOp().equals(op)||(ip.equals("")&&op.equals("")))
            {
                check = true;
            }
        }        
        return check;
    }
    
    //Add routing path
    public void addRP (String ip, String dst, String op) 
    {
    	ip = sortStrAlf(ip);
    	op = sortStrAlf(op);
    	   if(!this.AlreadyExists(ip,dst,op))
    	   {
		        RoutingPath RP = new RoutingPath(ip,dst,op);
		        this.routingPaths.add(RP);
    	   }
    }

    
    private boolean AlreadyExists(String ip, String dst, String op)
    {
        for(int a=0;a<this.routingPaths.size();a++)
        {            
            if(this.routingPaths.get(a).getIp().equals(ip)&&this.routingPaths.get(a).getDst().equals(dst)&&this.routingPaths.get(a).getOp().equals(op))
                return true;
        }
        return false;
    }

    public static String sortStrAlf(String input)
    {
        char[] ip1  = input.toCharArray();
        Arrays.sort(ip1);
        
        return String.valueOf(ip1);
    }
        
    
    private String IntToBitsString(int a, int size)
    {
            String out = Integer.toBinaryString(a);
            while(out.length()<size)
                out = "0" + out;
            
            return out;
    }
    
    public String GetBitsPort(String ports)
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
    
    private static String getInvColor(String pColor)
    {
    	switch(pColor)
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
    
    //Print the regions at a file
    public void PrintRegions(float[] stats,BufferedWriter bw, int nBits)
    {              
    	int maxRegion = (int)stats[0];    	
    	try 
    	{        
    		//File outFile = new File("RountingTables.txt");
    		//BufferedWriter bw = new BufferedWriter(new FileWriter(outFile)); 
    		bw.append("\n -- Router " + this.getNome() + "\n");
    		bw.append("(");
        
    		for(int a=0;a<this.Regions.size(); a++)
    		{            
    			int Xmin = Integer.parseInt(this.Regions.get(a).getDownLeft().split("\\.")[0]);                        
    			int Ymin = Integer.parseInt(this.Regions.get(a).getDownLeft().split("\\.")[1]);            
    			int Xmax = Integer.parseInt(this.Regions.get(a).getUpRight().split("\\.")[0]);            
    			int Ymax = Integer.parseInt(this.Regions.get(a).getUpRight().split("\\.")[1]);
            
    			//Write on file
    			String outLine = "(\""+GetBitsPort(this.Regions.get(a).getIp())+IntToBitsString(Xmin, nBits)+IntToBitsString(Ymin, nBits)+IntToBitsString(Xmax, nBits)+IntToBitsString(Ymax, nBits)+GetBitsPort(this.Regions.get(a).getOp())+"\")";                    
            
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
    		Logger.getLogger(Router.class.getName()).log(Level.SEVERE, null, ex);
    	}                                                               
    }

public boolean reaches(Router dest)
{
	return this.reaches(dest, "I");
}

private boolean reaches(Router dest, String ipColor) {
	if(dest == this)
		return true;
	String opColor = this.getOpColor(dest, ipColor); // by routing table
	if(opColor == null)
		return false;
	return this.getAdj(opColor).getDestino().reaches(dest, getInvColor(opColor));	
}

private String getOpColor(Router dest, String ipColor)
{
	String router = dest.getNome();
    for(Region reg : this.Regions)
    {
        if(reg.contains(router) && reg.getIp().contains(ipColor))
            return (reg.getOp().substring(0, 1)); 
    }
    System.err.println("ERROR : There isn't Op on "+this.getNome()+" for "+dest.getNome() + " " + ipColor);
    return null;
}
	
public Link getLink(Router destino) 
{	    		
    for(Link v : this.adj) //Arestas adjacentes
    {        
	if(v.getDestino().getNome().equals(destino.getNome()))
        {            
            return v;            
        }
    }   
    return null;	
}

    public ArrayList<RoutingPath> getCombinedOutputs() {
        return combinedOutputs;
    }

    public void setCombinedOutputs(ArrayList<RoutingPath> combinedOutputs) {
        this.combinedOutputs = combinedOutputs;
    }

    public ArrayList<Region> getRegions() {
        return Regions;
    }

    public void setRegions(ArrayList<Region> Regions) {
        this.Regions = Regions;
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
        
    public void setPai (Router Pai) {
            
        this.pai = Pai;
            
    }
        
    public Router getPai () {
            
        return this.pai;
            
    }
        
    public List<Link> getAdj() {
            
        return this.adj;
            
    }
    
    public Link getAdj(String color)
    {
    	for(Link a : this.adj)
    	{
    		if( a.getCor().equals(color))
    			return a;
    	}
    	System.out.println("ERROR : There isn't a Op " + color + "?");
    	return null;
    }
    
    public String getNome (){
        
        return this.name;
        
    }

    public ArrayList<RoutingPath> getRoutingPaths() 
    {
        return routingPaths;
    }

    public void setRoutingPaths(ArrayList<RoutingPath> routingPaths) 
    {
        this.routingPaths = routingPaths;
    }

    
    @Override
    public int compareTo(Router outroVertice) 
    {    
        if (this.distancia < outroVertice.distancia) {
            return -1;
        }
        if (this.distancia > outroVertice.distancia) {
            return 1;
        }
        return 0;
    }  
    
    public void printRegions()
    {
    	for(Region reg : this.Regions)
    	{
    		System.out.println(reg.toString());
    	}
    	
    }
    
    public void printRP()
    {
    	for(RoutingPath rp : this.routingPaths){
    		System.out.println("(" + rp.getIp() + " " + rp.getDst() + " " + rp.getOp() + ")");
    	}
    }
    
    public boolean hasSuperposition()
    {
    	for(int i = 0; i < this.Regions.size(); i++)
    	{
        	for(int j = i+1; j < this.Regions.size(); j++)
        	{
        		if(this.Regions.get(i).hasSuperpositionWith(this.Regions.get(j)) &&
        				this.Regions.get(i).sharesIpWith(this.Regions.get(j)))
        			return true;
        	}
    		
    	}
    	return false;
    }
    
    //Return a collection with all ips
    public ArrayList<String> getIpSets() 
    {
    	ArrayList<String> ips = new ArrayList<String>();
    	for(RoutingPath rp : this.getRoutingPaths()) //right collection?
    	{
    		if(!ips.contains(rp.getIp())) 
    		{
    			ips.add(rp.getIp());
    		}
    	}
    	return ips;
    }

}

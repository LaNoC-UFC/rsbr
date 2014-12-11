package sbr;

import java.util.ArrayList;

public class Segment {
	private ArrayList<Link> links;
	private ArrayList<Switch> switches;
	private int sn;
	
	public Segment() {
		links = new ArrayList<>();
		switches = new ArrayList<>();
	}
	
	public boolean isStarting() 
	{		
		//Checa se o destino do ultimo link � o primeiro switch
		
		//TESTE PROVIS�RIO, SER� APAGADO
		if(links.size()==1)
			return false;
		
		if(links.get(links.size()-1).getDestino().getNome().equals(switches.get(0).getNome()))
			return true;
		
		return false;
	}
	
	public boolean isUnitary() 
	{		
		
		if(links.size()==1 && switches.size()==0)
		{
			return true;
		}
		
		return false;		
	}
	
	
	public boolean isRegular() 
	{
		
		if(!this.isUnitary() && !this.isStarting())
			return true;
		
		return false;
	}
	
	public void setSubNet(int sbnt) {
		sn = sbnt;
	}
	
	public boolean belongsTo(int sbnt) {
		if(switches.size() == 0) return false;
		return (switches.get(0).belongsTo(sn));
		//return (sbnt == sn);
	}
	
	public void add(Link ln) {
		links.add(ln);
		ln.setSegment(this);
	}

	public void add(Switch sw) {
		switches.add(sw);
		sw.setSegment(this);
	}

	public void remove(Link ln) {
		ln.setSegment(null);
		links.remove(ln);
	}

	public void remove(Switch sw) {
		sw.setSegment(null);
		/*@RM*/		
		//Remove the last occurrence instead of the first 
		if(switches.lastIndexOf(sw)!=-1)
			switches.remove(switches.lastIndexOf(sw));
		/*@RM*/
		//switches.remove(sw);
		
	}
	
	public String toString() {
		String r = "";
		int sw = 0, ln = 0;
		/*while(sw < switches.size() || ln < links.size()) {
			if(sw < switches.size()) 
				r += (switches.get(sw++).getNome() + " ");
			if(ln < links.size()) {
				r += (links.get(ln).getOrigem().getNome() + " <=> " + links.get(ln).getDestino().getNome() + " ");
				ln++;				
			}
		}
		*/
		while(sw < switches.size()) {
			r+= (switches.get(sw++).getNome() + " ");
		}
		r+= '\n';
		while(ln < links.size()) {
			r += (links.get(ln).getOrigem().getNome() + " <=> " + links.get(ln).getDestino().getNome() + " ");
			ln++;
		}
		return r;
	}

	public ArrayList<Switch> getSwitchs()
	{
		return this.switches;
	}
	
	public ArrayList<Link> getLinks()
	{
		return this.links;
	}

	public boolean isEmpty() {
		return (links.isEmpty() && switches.isEmpty()) ? true : false;
	}

}

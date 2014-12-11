
package rbr;

import java.util.ArrayList;
import java.util.Arrays;

public class Region implements Comparable<Region>
{
    private int maxRegions = 4;
    private String ip;
    private String op;
    private String upRight;
    private String downLeft;
    private float size;
    private ArrayList<String> dst = new ArrayList<>();
    
    public Region(String ip, ArrayList<String> dsts, String op)
    {
        this.dst = dsts;
        this.ip = ip;
        this.op = op;
        this.setextrems();
        this.setSize();
    }
    
    public void setextrems()
    {
        String Xs = "";
        String Ys = "";
        
        for(String s : this.dst)
        {
            Xs = Xs.concat(Character.toString(s.charAt(0)));
            Ys = Ys.concat(Character.toString(s.charAt(1)));
        }
        char[] X = Xs.toCharArray();        
        char[] Y = Ys.toCharArray();
                
        Arrays.sort(X);
        Arrays.sort(Y);
        
        this.upRight = String.valueOf(X[X.length-1]) + String.valueOf(Y[Y.length-1]);
        this.downLeft = String.valueOf(X[0]) + String.valueOf(Y[0]);
                
    }
    
    public void setSize()
    {
        int Xmin = Integer.parseInt(""+this.downLeft.charAt(0));
        int Ymin = Integer.parseInt(""+this.downLeft.charAt(1));
        
        int Xmax = Integer.parseInt(""+this.upRight.charAt(0));
        int Ymax = Integer.parseInt(""+this.upRight.charAt(1));
        
        this.size = ((Xmax - Xmin)+1)*((Ymax - Ymin)+1);
    }
                    
    public int getMaxRegions() 
    {
        return maxRegions;
    }

    public void setMaxRegions(int maxRegions) 
    {
        this.maxRegions = maxRegions;
    }

    public String getIp() 
    {
        return ip;
    }

    public void setIp(String ip) 
    {
        this.ip = ip;
    }

    public String getOp() 
    {
        return op;
    }

    public void setOp(String op) 
    {
        this.op = op;
    }

    public String getDownLeft() 
    {
        return downLeft;
    }

    public void setDownLeft(String downLeft) 
    {
        this.downLeft = downLeft;
    }

    public String getUpRight() 
    {
        return upRight;
    }

    public void setUpRight(String upRight) 
    {
        this.upRight = upRight;
    }

    public ArrayList<String> getDst() 
    {
        return dst;
    }
    
    public ArrayList<String> getDst(int xmin, int ymin, int xmax, int ymax) {
    	ArrayList<String> result = new ArrayList<String>();
    	for(int x = xmin; x <= xmax; x++)
    		for(int y = ymin; y <= ymax; y++)
    			if(this.dst.contains(x+""+y))
    					result.add(x+""+y);
    	if (result.size() == 0) result = null;
    	return result;
    }

    public void setDst(ArrayList<String> dst) 
    {
        this.dst = dst;
    }
    
    @Override
    public int compareTo(Region otherRegion) 
    {    
        if (this.size < otherRegion.size) {
            return -1;
        }
        if (this.size > otherRegion.size) {
            return 1;
        }
        return 0;
    }
    
    public int getXmax(){
    	return Integer.parseInt(this.getUpRight().substring(0, 1));
    }
    
    public int getYmax(){
    	return Integer.parseInt(this.getUpRight().substring(1, 2));
    }
    
    public int getXmin(){
    	return Integer.parseInt(this.getDownLeft().substring(0, 1));
    }
    
    public int getYmin(){
    	return Integer.parseInt(this.getDownLeft().substring(1, 2));
    }
    
    public String toString()
    {
    	String out = this.downLeft + " " + this.upRight + " " + this.ip + " " + this.op;
    	return out;
    }
    
    public boolean contains(String router)
    {
        int x = Integer.parseInt(router.substring(0, 1));
        int y = Integer.parseInt(router.substring(1, 2));
        
    	int minX = Integer.parseInt(this.getDownLeft().substring(0, 1));
    	int minY = Integer.parseInt(this.getDownLeft().substring(1, 2));
    	int maxX = Integer.parseInt(this.getUpRight().substring(0, 1));
    	int maxY = Integer.parseInt(this.getUpRight().substring(1, 2));

        if(minX<=x && x<=maxX && minY<=y && y<=maxY)
        	return true;
        return false;
    }

    //check if has overlap with other
    public boolean hasSuperpositionWith(Region other)
    {
    	if(this.contains(other.getDownLeft()) || this.contains(other.getUpRight()) ||
    	   other.contains(this.getDownLeft()) || other.contains(this.getUpRight()) )
    		return true;
    	return false;
    }
    
    //check if share ip with other
    public boolean sharesIpWith(Region other)
    {
    	for(int i = 0; i < other.getIp().length();)
    	{
    		if(this.ip.contains(other.getIp().substring(i, ++i)))
    			return true;
    	}
    	return false;
    }
}

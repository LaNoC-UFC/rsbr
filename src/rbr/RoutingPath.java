
package rbr;

public class RoutingPath 
{ 
    private String router;
    private String ip;
    private String dst;
    private String op; 
    
    public RoutingPath(String ip, String dst, String op) 
    {
        this.dst = dst;
        this.ip = ip;
        this.op = op;
    }
    

    public String getRouter() 
    {
        return router;
    }

    public void setRouter(String router) 
    {
        this.router = router;
    }

    public String getIp() 
    {
        return ip;
    }

    public void setIp(String ip) 
    {
        this.ip = ip;
    }

    public String getDst() 
    {
        return dst;
    }

    public void setDst(String dst) 
    {
        this.dst = dst;
    }

    public String getOp() 
    {
        return op;
    }

    public void setOp(String op) 
    {
        this.op = op;
    }
    
}

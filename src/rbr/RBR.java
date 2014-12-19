
package rbr;

import util.*;

import java.io.File;
import java.util.ArrayList;

public class RBR 
{ 

    public static void main(String[] args) 
    {        
 
        String topologyFile;
        String restrictionFile;

        //if merge = "merge" the regions will be merged else they wont
        String merge;
        double reachability = 0.0;
        
        //Get file name and string merge
        if(args.length == 3)
        {
        	topologyFile = args[0];
            merge = args[1];
            reachability = Double.valueOf(args[2]);
            //restrictionFile = args[3];
            restrictionFile = "Restriction.txt";
        }
        else //Without arguments
        {
        	topologyFile = "4x4.txt";
            merge = "merge";
            reachability = 1.0;
            restrictionFile = "Restriction.txt";
        }
        
        System.err.println("File name: "+topologyFile);
            
        //Make File by Filename and generate graph
        File topology = new File(topologyFile);
        RBRTools tools = new RBRTools();
        //Make graph
        Graph graph = new Graph(topology);
        ArrayList<ArrayList<Vertice>> paths;
        ArrayList<ArrayList<Vertice>> simplePaths;
        System.err.println(graph); 
         
         tools.setRestricitions(restrictionFile);
         
         //Compute paths and make the routing options
         System.out.println("Paths Computation");
         paths = tools.pathsComputation(graph);

         System.out.println("Regions Computation");
         tools.regionsComput(graph);
         
         //Adjust regions to avoid overlap
         System.out.println("Regions Adjustment");
         tools.adjustsRegions(graph);
         
         //Do the merge if asked
         System.out.println("Regions Merge");
         if(merge.equals("merge"))
         {
        	 System.out.println("Merge");
        	 for(Vertice vertice : graph.getVertices())
        	 {
        		 tools.Merge(graph, vertice, reachability);
        	 }
         }
         
         //Make the routing tables
         System.out.println("Making Tables");    
         tools.doRoutingTable(tools.getRegionsStats(graph), graph);         

         System.out.println("Doing Average Routing Distance and Link Weight...");
         simplePaths = tools.getSimplePaths(paths, graph);
         //Make statistic file with hop count, regions, routing distance and link weight values 
         tools.makeStats(tools.getHopCountStats(paths), tools.getRegionsStats(graph), tools.getRoutingDistance(simplePaths, graph),tools.linkWeightStats(simplePaths, graph));
         
         //Make log file with all regions per router (will have more info)         
         //tools.makeLog(graph);         
         
         System.out.println("RBR to "+topologyFile+" done!");
    }
}



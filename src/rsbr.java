import java.io.File;
import java.util.ArrayList;

import rbr.RBRTools;
import sbr.SR;
import util.Graph;
import util.Path;
import util.Vertice;


public class rsbr 
{

	public static void main(String[] args) 
	{	Graph graph;
		String topologyFile;
		String merge;
        double reachability = 0.0;
        
		switch(args.length)
		{
		case 3:
			topologyFile = args[0];
            merge = args[1];
            reachability = Double.valueOf(args[2]);
			break;
			
		default:
			topologyFile = "2x2.txt";
            merge = "merge";
            reachability = 1.0;
            break;
		
		}
		
		System.err.println("File name: "+topologyFile);
		graph = new Graph(new File(topologyFile));
		
		SR sbr = new SR(graph);
		sbr.computeSegments();
		sbr.listSegments();
		sbr.setrestrictions();
		sbr.printRestrictions();
		
		RBRTools rbr = new RBRTools();
		ArrayList<Path> paths;
        ArrayList<Path> simplePaths;
        paths = rbr.pathComputation(graph); 
        rbr.addRoutingOptions(paths, graph);
        rbr.regionsComput(graph);
        rbr.adjustsRegions(graph);
        
        if(merge.equals("merge"))        
       	 for(Vertice vertice : graph.getVertices())
       		 rbr.Merge(graph, vertice, reachability);
        
        rbr.doRoutingTable(rbr.getRegionsStats(graph), graph);
        simplePaths = rbr.getSimplePaths(paths, graph);
        rbr.makeStats(rbr.getHopCountStats(paths), rbr.getRegionsStats(graph), rbr.getRoutingDistance(simplePaths, graph),rbr.linkWeightStats(simplePaths, graph));

	}

}

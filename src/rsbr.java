import java.io.File;
import java.util.ArrayList;

import rbr.RBR;
import sbr.SR;
import util.Graph;
import util.Path;
import util.Vertice;

public class rsbr {

	public static void main(String[] args) {
		Graph graph;
		String topologyFile;
		String merge = "merge";
		double reachability = 1.0;

		switch (args.length) {
		case 3:
			topologyFile = args[0];
			merge = args[1];
			reachability = Double.valueOf(args[2]);
			break;

		default:
			topologyFile = "Input10-5.txt";
		}

		System.out.println("Geranting graph from " + topologyFile);
		//graph = new Graph(new File(topologyFile));
		graph = new Graph(4,4,0.0);
		System.out.println("graph: "+graph);
		System.out.println(" - SR Section");
		SR sbr = new SR(graph);

		System.out.println("Compute the segments");
		sbr.computeSegments();
		// sbr.listSegments();

		System.out.println("Set the restrictions");
		sbr.setrestrictions();
		// sbr.printRestrictions();

		System.out.println(" - RBR Section");
		RBR rbr = new RBR(graph);

		System.out.println("Paths Computation");
		ArrayList<ArrayList<Path>> paths = rbr.pathsComputation();

		System.out.println("Paths Selection");
		//Um caminho aleatório
		ArrayList<ArrayList<Path>> aleatPath = rbr.pathSelection(paths);
		//Seleção por peso máximo (rodado 1 vez)
		ArrayList<ArrayList<Path>> MaxWeigthPath1 = rbr.pathSelection(paths, new Path.MaxWeight(), 1);
		//Seleção por peso máximo (rodado 2 vezes)
		ArrayList<ArrayList<Path>> MaxWeigthPath2 = rbr.pathSelection(paths, new Path.MaxWeight(), 2);
		//Seleção por peso máximo (rodado 3 vezes)
		ArrayList<ArrayList<Path>> MaxWeigthPath3 = rbr.pathSelection(paths, new Path.MaxWeight(), 3);
		//Seleção por peso mínimo (rodado 1 vez)
		ArrayList<ArrayList<Path>> MinWeigthPath1 = rbr.pathSelection(paths, new Path.MinWeight(), 1);
		//Seleção por peso mínimo (rodado 2 vezes)
		ArrayList<ArrayList<Path>> MinWeigthPath2 = rbr.pathSelection(paths, new Path.MinWeight(), 2);
		//--
		System.out.println("Regions Computation for all selections of paths");
		float[] stats = null;
		
		//All paths
		rbr.addRoutingOptions(paths);
		rbr.regionsComputation();
		for (Vertice vertice : graph.getVertices())
			rbr.merge(vertice, reachability);
		stats = rbr.getRegionsStats();
		System.out.println("Todos os caminhos:");
		System.out.println("Max: "+stats[0]+" Min: "+stats[1]+" Med: "+stats[2]);
		
		//Um caminho aleatório
		rbr.addRoutingOptions(aleatPath);
		rbr.regionsComputation();
		for (Vertice vertice : graph.getVertices())
			rbr.merge(vertice, reachability);
		stats = rbr.getRegionsStats();
		System.out.println("Um caminho aleatório:");
		System.out.println("Max: "+stats[0]+" Min: "+stats[1]+" Med: "+stats[2]);
		
		//Seleção por peso máximo (rodado uma vez)
		rbr.addRoutingOptions(MaxWeigthPath1);
		rbr.regionsComputation();
		for (Vertice vertice : graph.getVertices())
			rbr.merge(vertice, reachability);
		stats = rbr.getRegionsStats();
		System.out.println("Seleção por peso máximo (1x):");
		System.out.println("Max: "+stats[0]+" Min: "+stats[1]+" Med: "+stats[2]);
		
		//Seleção por peso máximo (rodado duas vezes)
		rbr.addRoutingOptions(MaxWeigthPath2);
		rbr.regionsComputation();
		for (Vertice vertice : graph.getVertices())
			rbr.merge(vertice, reachability);
		stats = rbr.getRegionsStats();
		System.out.println("Seleção por peso máximo (2x):");
		System.out.println("Max: "+stats[0]+" Min: "+stats[1]+" Med: "+stats[2]);
		
		//Seleção por peso máximo (rodado tres vezes)
		rbr.addRoutingOptions(MaxWeigthPath3);
		rbr.regionsComputation();
		for (Vertice vertice : graph.getVertices())
			rbr.merge(vertice, reachability);
		stats = rbr.getRegionsStats();
		System.out.println("Seleção por peso máximo (3x):");
		System.out.println("Max: "+stats[0]+" Min: "+stats[1]+" Med: "+stats[2]);

		//--
		System.out.println("Regions Adjustment");
		rbr.printLengthofPaths(aleatPath);
		//--
		System.out.println("Doing Merge");
		if (merge.equals("merge"))
			for (Vertice vertice : graph.getVertices())
				rbr.merge(vertice, reachability);
		//--
		System.out.println("Making Tables");
		rbr.doRoutingTable();
		//--
		/*System.out.println("Doing Average Routing Distance and Link Weight");
		rbr.makeStats(aleatPath);*/

		System.out.println("All done!");
	}

}

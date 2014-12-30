import java.io.File;
import java.util.ArrayList;

import rbr.RBRTools;
import sbr.SR;
import util.Graph;
import util.Path;
import util.Vertice;

public class rsbr {

	public static void main(String[] args) {
		Graph graph;
		String topologyFile;
		String merge;
		double reachability = 0.0;

		switch (args.length) {
		case 3:
			topologyFile = args[0];
			merge = args[1];
			reachability = Double.valueOf(args[2]);
			break;

		default:
			topologyFile = "2x2.txt";
			merge = "merge";
			reachability = 1.0;
		}

		System.out.println("Geranting graph from " + topologyFile);
		graph = new Graph(new File(topologyFile));

		System.out.println("SR Section");
		SR sbr = new SR(graph);

		System.out.println("Compute the segments");
		sbr.computeSegments();
		// sbr.listSegments();

		System.out.println("Set the restrictions");
		sbr.setrestrictions();
		// sbr.printRestrictions();

		System.out.println("RBR Section");
		RBRTools rbr = new RBRTools(graph);

		System.out.println("Paths Computation");
		ArrayList<Path> paths;
		paths = rbr.pathComputation();

		System.out.println("Paths Selection");
		ArrayList<Path> simplePaths;
		simplePaths = rbr.getSimplePaths(paths);
		rbr.addRoutingOptions(simplePaths);

		System.out.println("Regions Computation");
		rbr.regionsComput();

		System.out.println("Regions Adjustment");
		rbr.adjustsRegions();
		rbr.printLengthofPaths(simplePaths);

		System.out.println("Doing Merge");
		if (merge.equals("merge"))
			for (Vertice vertice : graph.getVertices())
				rbr.Merge(vertice, reachability);

		System.out.println("Making Tables");
		rbr.doRoutingTable();

		System.out.println("Doing Average Routing Distance and Link Weight...");
		rbr.makeStats(simplePaths);

	}

}

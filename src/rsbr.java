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
			topologyFile = "Input4.txt";
		}

		System.out.println("Geranting graph from " + topologyFile);
		graph = new Graph(new File(topologyFile));

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
		ArrayList<ArrayList<Path>> simplePaths = rbr.pathSelection(paths, new Path.MaxWeight(), 1);

		System.out.println("Regions Computation");
		rbr.addRoutingOptions(simplePaths);
		rbr.regionsComputation();

		System.out.println("Regions Adjustment");
		rbr.adjustsRegions();
		rbr.printLengthofPaths(simplePaths);

		System.out.println("Doing Merge");
		if (merge.equals("merge"))
			for (Vertice vertice : graph.getVertices())
				rbr.merge(vertice, reachability);

		System.out.println("Making Tables");
		rbr.doRoutingTable();

		System.out.println("Doing Average Routing Distance and Link Weight");
		float[] stats = rbr.getRegionsStats();
		System.out.println(stats[0]+" "+stats[1]+" "+stats[2]);
		rbr.makeStats(simplePaths);

		System.out.println("All done!");
	}

}

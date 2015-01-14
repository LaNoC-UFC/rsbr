import java.io.File;
import java.util.ArrayList;

import rbr.RBR;
import sbr.SR;
import util.Graph;
import util.Path;

public class rsbr {

	public static void main(String[] args) {
		Graph graph;
		String topologyFile;
		String merge = "merge";
		double reachability = 1.0;

		switch (args.length) {
		case 1:
			topologyFile = args[0];
			break;
		case 3:
			topologyFile = args[0];
			merge = args[1];
			reachability = Double.valueOf(args[2]);
			break;
		default:
			topologyFile = "Input5-4.txt";
		}

		System.out.println("Geranting graph from " + topologyFile);
		graph = new Graph(new File(topologyFile));
		//System.out.println(graph);

		System.out.println(" - SR Section");
		SR sbr = new SR(graph);

		System.out.println("Compute the segments");
		sbr.computeSegments();
		//sbr.listSegments();

		System.out.println("Set the restrictions");
		sbr.setrestrictions();
		//sbr.printRestrictions();

		System.out.println(" - RBR Section");
		RBR rbr = new RBR(graph);

		System.out.println("Paths Computation");
		//ArrayList<ArrayList<Path>> paths = rbr.pathsComputation();
		ArrayList<ArrayList<Path>> paths = rbr.pathComputation();
		
		System.out.println("Paths Selection");
		// Selecao aleatoria
		ArrayList<ArrayList<Path>> simplePaths = rbr.pathSelection(paths);
		// Peso do caminho proporcional ao seu comprimento (equaliza peso dos links)
		double lwm = rbr.linkWeightMean(paths);
		//ArrayList<ArrayList<Path>> simplePaths = rbr.pathSelection(paths, new Path().new PropWeight(lwm), 2);
		// Selecao que minimiza o peso dos caminhos
		//ArrayList<ArrayList<Path>> simplePaths = rbr.pathSelection(paths, new Path.MinWeight(), 1);
		// Selecao que equaliza o peso dos caminhos
		double pwm = rbr.pathWeightMean(paths);
		//ArrayList<ArrayList<Path>> simplePaths = rbr.pathSelection(paths, new Path().new MedWeight(pwm), 2);

		System.out.println("Regions Computation");
		rbr.addRoutingOptions(simplePaths);
		rbr.regionsComputation();

		//System.out.println("Regions Adjustment");
		//rbr.adjustsRegions();
		//rbr.printLengthofPaths(simplePaths);

		System.out.println("Doing Merge");
		if (merge.equals("merge"))
			rbr.merge(reachability);

		System.out.println("Making Tables");
		rbr.doRoutingTable();

		System.out.println("Doing Average Routing Distance and Link Weight");
		double[] reg = rbr.getRegionsStats();
		double[] lw = rbr.linkWeightStats();
		double[] pw = rbr.pathWeightStats(simplePaths);
		//rbr.makeStats(simplePaths);
		
		System.out.println("Regions - Min: "+reg[0]+", Med: "+reg[1]+", Max: "+reg[2]);
		// TODO Mostrar media e std do linkWeight e pathWeight esperados
		System.out.println("Link Weight - Esperado: "+lwm+", Alcançado: "+lw[0]+" ("+lw[1]+")");
		// TODO Mostrar media e std do pathWeight e pathWeight alcancados
		System.out.println("Path Weight - Esperado: "+pwm+", Alcançado: "+pw[0]+" ("+pw[1]+")");

		System.out.println("All done!");
	}

}

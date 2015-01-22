import java.io.File;
import java.util.ArrayList;

import rbr.RBR;
import sbr.SR;
import util.Graph;
import util.Path;

public class rsbr {

	public static void main(String[] args) {
		String topologyFile = null, volumePath = null;
		String merge = "merge";
		double reachability = 1.0;
		String tableFile = null;
		int dim = 4;
		
		System.out.println("Generating graph");
		
		Graph graph = (topologyFile != null) ? new Graph(new File(topologyFile)) :  new Graph(dim, 0.2);
		System.out.println("Isolado? :"+graph.haveIsolatedCores());
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
		ArrayList<ArrayList<Path>> paths = rbr.pathsComputation();
		
		if(volumePath != null) {
			File commvol = new File(volumePath);
			if(commvol.exists()) {
				System.out.println("Getting volumes from "+volumePath);
				rbr.setVolume(paths, commvol);
			}
		}
		
		System.out.println("Paths Selection");
		
		ArrayList<ArrayList<Path>> simplePaths = null;
		double lwm = rbr.linkWeightMean(paths);
		double pwm = rbr.pathWeightMean(paths);

		int choice = 4;
		switch(choice) {
		case 0 : // Sem seleção
			simplePaths = paths;
			break;
		case 1 : // Selecao aleatoria
			simplePaths = rbr.pathSelection(paths);
			break;
		case 2 : // Peso mínimo
			simplePaths = rbr.pathSelection(paths, new Path.MinWeight(), 10);
			break;
		case 3 : // Peso proporcional
			double pws = rbr.pathWeightStd(paths);
			System.out.println("Path Weight esperado: "+pwm+" ("+pws+")");
			simplePaths = rbr.pathSelection(paths, new Path().new PropWeight(lwm), 10);
			break;
		case 4 : // Peso médio
			simplePaths = rbr.pathSelection(paths, new Path().new MedWeight(pwm), 10);
			break;
		}
		//rbr.printLengthofPaths(simplePaths);
		
		if(tableFile != null) {
			
			System.out.println("Regions Computation");
			rbr.addRoutingOptions(simplePaths);
			rbr.regionsComputation();
			
			if (merge.equals("merge")) {
				System.out.println("Doing Merge");
				rbr.merge(reachability);
			}

			System.out.println("Making Tables");
			rbr.doRoutingTable(tableFile);
		}
		
		System.out.println("Doing Average Routing Distance and Link Weight\n");
		//double[] reg = rbr.getRegionsStats();
		double[] lw = rbr.linkWeightStats();
		double[] pw = rbr.pathWeightStats(simplePaths);
		double[] pnw = rbr.pathNormWeightStats(simplePaths);
		//rbr.makeStats(simplePaths);
		
		//System.out.println("Regions - Min: "+reg[0]+", Med: "+reg[1]+", Max: "+reg[2]);
		System.out.println("Peso dos caminhos: "+pw[0]+" ("+pw[1]+")");
		System.out.println("Peso normalizado dos caminhos: "+pnw[0]+" ("+pnw[1]+")");
		System.out.println("Peso dos links: "+lw[0]+" ("+lw[1]+")");

		System.out.println("\nAll done!");
	}

}

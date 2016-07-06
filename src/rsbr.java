import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import rbr.PathFinder;
import rbr.PathSelector;
import rbr.RBR;
import sbr.SR;
import util.Graph;
import util.GraphBuilder;
import util.Path;

public class rsbr {

	public static void main(String[] args) {

		String topologyFile = null, volumePath = null;
		String merge = "merge";
		double reachability = 1.0;
		String tableFile = "mw2";
		//String tableFile = null;
		int dim = 4;
		int dimX = dim, dimY = dim;
		int montCarl = 1;
		double[] faltPercs = { 0.0, 0.05, 0.1, 0.15, 0.2, 0, 25, 0.30 };
		ArrayList<ArrayList<Path>> chosenPaths = null;
		BufferedWriter output = null;
		try {
			output = new BufferedWriter(new FileWriter(new File(dimX + "x"
					+ dimY + "monteCarlo.txt")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		switch (args.length) {
		case 4:
			dimX = Integer.parseInt(args[0]);
			dimY = Integer.parseInt(args[1]);
			montCarl = Integer.parseInt(args[3]);
			break;
		}

		for (double faltPerc : faltPercs) {

			Graph graph;
			double[] stats = null;
			ArrayList<Double> all = new ArrayList<Double>();
			ArrayList<Double> mw2 = new ArrayList<Double>();
			RBR rbr = null;

			if (!canHasPerc(dimX, dimY, faltPerc))
				break;

			for (int s = 0; s < montCarl; s++) {
				System.out.println("Generating graph");
				graph = (topologyFile != null) ?
						GraphBuilder.generateGraph(topologyFile) :
						new Graph(dimX, dimY, faltPerc);
				// graph.printGraph("montCarlo"+montCarl);
				System.out.println("Isolado?: " + graph.haveIsolatedCores());

				System.out.println(" - SR Section");
				SR sbr = new SR(graph);

				System.out.println("Compute the segments");
				sbr.computeSegments();
				// sbr.listSegments();

				System.out.println("Set the restrictions");
				sbr.setrestrictions();
				// sbr.printRestrictions();

				System.out.println("Paths Computation");
				ArrayList<ArrayList<Path>> paths = new PathFinder(graph).pathsComputation();

				System.out.println(" - RBR Section");
				rbr = new RBR(graph);

				if (volumePath != null) {
					File commvol = new File(volumePath);
					if (commvol.exists()) {
						System.out
								.println("Getting volumes from " + volumePath);
						rbr.setVolume(paths, commvol);
					}
				}

				System.out.println("Paths Selection");

				double lwm = rbr.linkWeightMean(paths);
				double pwm = rbr.pathWeightMean(paths);

				int choice = 5;
				switch (choice) {
				case 0: // Sem seleção
					chosenPaths = paths;
					break;
				case 1: // Selecao aleatoria
					chosenPaths = new PathSelector().pathSelection(paths);
					System.out.println("Aleatoria");
					printResults(chosenPaths, rbr);
					break;
				case 2: // Peso mínimo
					chosenPaths = new PathSelector().pathSelection(paths,
							new Path.MinWeight(), 10);
					System.out.println("Peso Minimo");
					printResults(chosenPaths, rbr);
					break;
				case 3: // Peso proporcional
					chosenPaths = new PathSelector().pathSelection(paths,
							new Path().new PropWeight(lwm), 10);
					System.out.println("Peso proporcional");
					printResults(chosenPaths, rbr);
					break;
				case 4: // Peso médio
					chosenPaths = new PathSelector().pathSelection(paths,
							new Path().new MedWeight(pwm), 10);
					System.out.println("Peso médio");
					printResults(chosenPaths, rbr);
					break;
				case 5: // Peso máximo
					chosenPaths = new PathSelector().pathSelection(paths,
							new Path.MaxWeight(), 2);
				}

				if (tableFile != null) {

					System.out.println("Regions Computation");
					rbr.addRoutingOptions(paths);
					rbr.regionsComputation();

					if (merge.equals("merge")) {
						System.out.println("Doing Merge");
						rbr.merge(reachability);
					}

					System.out.println("Making Tables");
					stats = rbr.getRegionsStats();
					rbr.doRoutingTable("all");
					System.out.println("All");
					System.out.println("Max: " + stats[0] + " Min: " + stats[1]
							+ " Med: " + stats[2]);
					all.add(stats[0]);

					System.out.println("Regions Computation");
					rbr.addRoutingOptions(chosenPaths);
					rbr.regionsComputation();

					if (merge.equals("merge")) {
						System.out.println("Doing Merge");
						rbr.merge(reachability);
					}

					System.out.println("Making Tables");
					stats = rbr.getRegionsStats();
					rbr.doRoutingTable(tableFile);
					System.out.println(tableFile);
					System.out.println("Max: " + stats[0] + " Min: " + stats[1]
							+ " Med: " + stats[2]);
					mw2.add(stats[0]);
				}

			}

			rbr.printMontCarl(output, faltPerc, all, mw2); // FaltPerc AllPaths
															// mean allpaths std
															// MaxWeightx2 mean
															// MaxWeightx2 std

		}

		try {
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("All done!");

	}

	private static boolean canHasPerc(int dimX, int dimY, double perc) {

		System.out.println("e ai?");
		int nArests = (dimX - 1) * dimY + dimX * (dimY - 1);
		System.out.println(nArests);
		int nFalts = (int) Math.ceil((double) nArests * perc);
		System.out.println(nFalts);
		int links = nArests - nFalts;
		System.out.println(links);
		int nRouters = dimX * dimY;
		System.out.println(nRouters);

		if (links < nRouters - 1)
			return false;

		return true;
	}

	private static void printResults(ArrayList<ArrayList<Path>> paths, RBR rbr) {
		//double[] reg = rbr.getRegionsStats();
		double[] lw = rbr.linkWeightStats();
		double[] pw = rbr.pathWeightStats(paths);
		double[] pnw = rbr.pathNormWeightStats(paths);
		
		//System.out.println("Regions - Min: "+reg[0]+", Med: "+reg[1]+", Max: "+reg[2]);
		System.out.println("Peso dos caminhos: "+pw[0]+" ("+pw[1]+")");
		System.out.println("Peso normalizado dos caminhos: "+pnw[0]+" ("+pnw[1]+")");
		System.out.println("Peso dos links: "+lw[0]+" ("+lw[1]+")");
	}}
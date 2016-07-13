import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import rbr.*;
import sbr.SR;
import util.*;

public class rsbr {

	public static void main(String[] args) {

		String topologyFile = null, volumePath = null;
		String merge = "merge";
		double reachability = 1.0;
		String tableFile = "mw2";
		//String tableFile = null;
		int dim = 4;
		int dimX = dim, dimY = dim;
		double[] faltPercs = { 0.0, 0.05, 0.1, 0.15, 0.2, 0, 25, 0.30 };
		ArrayList<ArrayList<Path>> chosenPaths = null;

		switch (args.length) {
		case 4:
			dimX = Integer.parseInt(args[0]);
			dimY = Integer.parseInt(args[1]);
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

			System.out.println("Generating graph");
			graph = (topologyFile != null) ?
					FromFileGraphBuilder.generateGraph(topologyFile) :
					RandomFaultyGraphBuilder.generateGraph(dimX, dimY, faltPerc);

			System.out.println("Isolado?: " + graph.haveIsolatedCores());

			System.out.println(" - SR Section");
			SR sbr = new SR(graph);

			System.out.println("Compute the segments");
			sbr.computeSegments();
			// sbr.listSegments();

			System.out.println("Set the restrictions");
			sbr.setrestrictions();
			GraphRestrictions restrictions = sbr.restrictions();
			// sbr.printRestrictions();

			System.out.println("Paths Computation");
			ArrayList<ArrayList<Path>> paths = new PathFinder(graph, restrictions).pathsComputation();

			System.out.println(" - RBR Section");
			rbr = new RBR(graph);

			if (volumePath != null) {
				File commvol = new File(volumePath);
				if (commvol.exists()) {
					System.out
							.println("Getting volumes from " + volumePath);
					setCommunicationVolume(paths, commvol, graph);
				}
			}

			System.out.println("Paths Selection");
			StatisticalAnalyser statistics = new StatisticalAnalyser(graph, rbr.regions());
			double lwm = statistics.linkWeightMean(paths);
			double pwm = statistics.pathWeightMean(paths);

			int choice = 5;
			switch (choice) {
				case 0: // Sem seleção
					chosenPaths = paths;
					break;
				case 1: // Selecao aleatoria
					chosenPaths = new RandomPathSelector(paths).selection();
					System.out.println("Aleatoria");
					printResults(chosenPaths, statistics);
					break;
				case 2: // Peso mínimo
					chosenPaths = new ComparativePathSelector(paths, new Path.MinWeight(), 10).selection();
					System.out.println("Peso Minimo");
					printResults(chosenPaths, statistics);
					break;
				case 3: // Peso proporcional
					chosenPaths = new ComparativePathSelector(paths, new Path().new PropWeight(lwm), 10).selection();
					System.out.println("Peso proporcional");
					printResults(chosenPaths, statistics);
					break;
				case 4: // Peso médio
					chosenPaths = new ComparativePathSelector(paths, new Path().new MedWeight(pwm), 10).selection();
					System.out.println("Peso médio");
					printResults(chosenPaths, statistics);
					break;
				case 5: // Peso máximo
					chosenPaths = new ComparativePathSelector(paths,	new Path.MaxWeight(), 2).selection();
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
				stats = statistics.getRegionsStats();
				new RoutingTableGenerator(graph, rbr.regions()).doRoutingTable("all");
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
				stats = statistics.getRegionsStats();
				new RoutingTableGenerator(graph, rbr.regions()).doRoutingTable(tableFile);
				System.out.println(tableFile);
				System.out.println("Max: " + stats[0] + " Min: " + stats[1]
						+ " Med: " + stats[2]);
				mw2.add(stats[0]);
			}
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

	private static void printResults(ArrayList<ArrayList<Path>> paths, StatisticalAnalyser statistics) {
		double[] lw = statistics.linkWeightStats();
		double[] pw = statistics.pathWeightStats(paths);
		double[] pnw = statistics.pathNormWeightStats(paths);
		
		//System.out.println("Regions - Min: "+reg[0]+", Med: "+reg[1]+", Max: "+reg[2]);
		System.out.println("Peso dos caminhos: "+pw[0]+" ("+pw[1]+")");
		System.out.println("Peso normalizado dos caminhos: "+pnw[0]+" ("+pnw[1]+")");
		System.out.println("Peso dos links: "+lw[0]+" ("+lw[1]+")");
	}

	private static void setCommunicationVolume(ArrayList<ArrayList<Path>> paths, File commvol, Graph graph) {

		int N = graph.dimX()*graph.dimY();
		double[][] vol = new double[N][N];
		double maxVol = 0;

		try {
			Scanner sc = new Scanner(new FileReader(commvol));

			for(int i = 0; i < N; i++) {
				String[] lines = sc.nextLine().split(" \t");
				for(int j = 0; j < N; j++) {
					vol[i][j] = Double.valueOf(lines[j]);
					maxVol = (vol[i][j] > maxVol) ? vol[i][j] : maxVol;
				}
			}
			sc.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		for(ArrayList<Path> alp : paths) {
			int i = graph.indexOf(alp.get(0).src());
			int j = graph.indexOf(alp.get(0).dst());
			double volume = vol[i][j];
			for(Path path : alp) {
				path.setVolume(volume/maxVol);
			}
		}
	}

}
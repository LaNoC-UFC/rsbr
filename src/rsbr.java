import java.io.*;
import java.util.*;

import util.*;
import rbr.*;
import sbr.SR;

public class rsbr {

	public static void main(String[] args) {

		String topologyFile = null;
		String volumePath = null;
		boolean shouldMerge = true;
		int dim = 4;
		int dimX = dim;
		int dimY = dim;
		double[] faultyPercentages = { 0.0, 0.05, 0.1, 0.15, 0.2, 0, 25, 0.30 };

		if (args.length == 4) {
			dimX = Integer.parseInt(args[0]);
			dimY = Integer.parseInt(args[1]);
		}

		for (double faultyPercentage : faultyPercentages) {

			if (!hasEnoughEdges(dimX, dimY, faultyPercentage))
				break;

			System.out.println("Generating graph");
			Graph graph = (topologyFile != null) ?
					FromFileGraphBuilder.generateGraph(topologyFile) :
					RandomFaultyGraphBuilder.generateGraph(dimX, dimY, (int)Math.ceil(faultyPercentage*numberOfEdges(dimX, dimY)));

			System.out.println("Isolated?: " + graph.haveIsolatedCores());

			System.out.println(" - SR Section");
			GraphRestrictions restrictions = SBRSection(graph);

			System.out.println("Paths Computation");
			ArrayList<ArrayList<Path>> allMinimalPaths = new PathFinder(graph, restrictions).pathsComputation();

			System.out.println(" - Paths Selection Section");
			RBR rbr = new RBR(graph);

			if (volumePath != null) {
				File commvol = new File(volumePath);
				if (commvol.exists()) {
					System.out
							.println("Getting volumes from " + volumePath);
					setCommunicationVolume(allMinimalPaths, commvol, graph);
				}
			}

			StatisticalAnalyser statistics = new StatisticalAnalyser(graph, rbr.regions());
			double lwm = statistics.linkWeightMean(allMinimalPaths);
			double pwm = statistics.pathWeightMean(allMinimalPaths);

			ArrayList<ArrayList<Path>> chosenPaths = selectPaths(allMinimalPaths, lwm, pwm);
			printResults(chosenPaths, statistics);

			System.out.println(" - RBR Section");
			RBRSection(shouldMerge, graph, allMinimalPaths, rbr, statistics, "full");
			RBRSection(shouldMerge, graph, chosenPaths, rbr, statistics, "custom");
		}
	}

	private static GraphRestrictions SBRSection(Graph graph) {
		SR sbr = new SR(graph);
		System.out.println("Compute the segments");
		sbr.computeSegments();
		System.out.println("Set the restrictions");
		sbr.setrestrictions();
		return sbr.restrictions();
	}

	private static void RBRSection(boolean shouldMerge, Graph graph, ArrayList<ArrayList<Path>> allMinimalPaths, RBR rbr, StatisticalAnalyser statistics, String fileSuffix) {
		System.out.println("Regions Computation");
		rbr.addRoutingOptions(allMinimalPaths);
		rbr.regionsComputation();

		if (shouldMerge) {
            System.out.println("Doing Merge");
            rbr.merge();
            assert rbr.reachabilityIsOk();
        }

		System.out.println("Making Tables");
		new RoutingTableGenerator(graph, rbr.regions()).doRoutingTable(fileSuffix);
	}

	private static int numberOfEdges(int dimX, int dimY) {
		return (dimX - 1)*dimY + (dimY - 1)*dimX;
	}

	private static ArrayList<ArrayList<Path>> selectPaths(ArrayList<ArrayList<Path>> paths, double lwm, double pwm) {
		ArrayList<ArrayList<Path>> chosenPaths = null;

		int choice = 5;
		switch (choice) {
            case 0: // Sem seleção
                chosenPaths = paths;
                break;
            case 1: // Selecao aleatoria
                chosenPaths = new RandomPathSelector(paths).selection();
                System.out.println("Random");
                break;
            case 2: // Peso mínimo
                chosenPaths = new ComparativePathSelector(paths, new Path.MinWeight(), 10).selection();
                System.out.println("Peso Minimo");
                break;
            case 3: // Peso proporcional
                chosenPaths = new ComparativePathSelector(paths, new Path().new PropWeight(lwm), 10).selection();
                System.out.println("Peso proporcional");
                break;
            case 4: // Peso médio
                chosenPaths = new ComparativePathSelector(paths, new Path().new MedWeight(pwm), 10).selection();
                System.out.println("Peso médio");
                break;
            case 5: // Peso máximo
                chosenPaths = new ComparativePathSelector(paths,	new Path.MaxWeight(), 2).selection();
        }
		return chosenPaths;
	}

	private static boolean hasEnoughEdges(int dimX, int dimY, double percentage) {
		int numberOfFaultyEdges = (int) Math.ceil((double) numberOfEdges(dimX, dimY) * percentage);
		int numberOfGoodEdges = numberOfEdges(dimX, dimY) - numberOfFaultyEdges;
		int numberOfVertices = dimX * dimY;

		return (numberOfGoodEdges >= numberOfVertices - 1);
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

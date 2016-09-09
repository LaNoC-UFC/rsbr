import java.io.*;
import java.util.*;

import util.*;
import rbr.*;
import paths_gen.*;
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

			ArrayList<ArrayList<Path>> chosenPaths = selectPaths(allMinimalPaths);

			System.out.println(" - RBR Section");
			StatisticalAnalyser statistics = new StatisticalAnalyser(graph, rbr.regions());
			RBRSection(shouldMerge, graph, allMinimalPaths, rbr, statistics, "full");
			RBRSection(shouldMerge, graph, chosenPaths, rbr, statistics, "custom");
			printResults(chosenPaths, statistics);
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

	private static ArrayList<ArrayList<Path>> selectPaths(ArrayList<ArrayList<Path>> paths) {
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
            case 5: // Peso máximo
                chosenPaths = new ComparativePathSelector(paths, new Path.MaxWeight(), 2).selection();
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
		double lwAverage = statistics.averageLinkWeight();
		double lwStdDeviation = statistics.standardDeviationLinkWeight();
		double pwAverage = statistics.averagePathWeight(paths);
		double pwStdDeviation = statistics.standardDeviationPathWeight(paths);
		double pnwAverage = statistics.averagePathNormWeight(paths);
		double pnwStdDeviation = statistics.standardDeviationPathNormWeight(paths);
		double ard = statistics.averageRoutingDistance(paths);
		System.out.println("Peso dos caminhos: "+pwAverage+" ("+pwStdDeviation+")");
		System.out.println("Peso normalizado dos caminhos: "+pnwAverage+" ("+pnwStdDeviation+")");
		System.out.println("Peso dos links: "+lwAverage+" ("+lwStdDeviation+")");
		System.out.println("ARD: " + ard);
	}

	private static Map<Path, Double> setCommunicationVolume(ArrayList<ArrayList<Path>> paths, File commvol, Graph graph) {

		int N = graph.dimX()*graph.dimY();
		double[][] vol = new double[N][N];
		double maxVol = 0;
		Map<Path, Double> pathsVolume = new HashMap<>();

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
				pathsVolume.put(path, volume/maxVol);
			}
		}
		return pathsVolume;
	}
}

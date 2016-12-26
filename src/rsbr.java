import java.io.*;
import java.util.*;
import util.*;
import rbr.*;
import paths_gen.*;
import sbr.*;

public class rsbr {

    public static void main(String[] args) {

        String topologyFile = null;
        String volumePath = null;
        boolean shouldMerge = true;
        int dim = 4;
        int rows = dim;
        int columns = dim;
        double[] faultyPercentages = { 0.0, 0.05, 0.1, 0.15, 0.2, 0, 25, 0.30 };

        if (args.length == 4) {
            rows = Integer.parseInt(args[0]);
            columns = Integer.parseInt(args[1]);
        }

        for (double faultyPercentage : faultyPercentages) {

            if (!hasEnoughEdges(rows, columns, faultyPercentage))
                break;

            System.out.println("Generating graph");
            Graph graph = (topologyFile != null) ?
                    FromFileGraphBuilder.generateGraph(topologyFile) :
                    RandomFaultyGraphBuilder.generateGraph(rows, columns, (int)Math.ceil(faultyPercentage*numberOfEdges(rows, columns)));

            System.out.println("Isolated?: " + graph.hasIsolatedCores());

            System.out.println(" - SR Section");
            GraphRestrictions restrictions = SBRSection(graph);

            System.out.println("Paths Computation");
            List<List<Path>> allMinimalPaths = new PathFinder(graph, restrictions).pathsComputation();

            System.out.println(" - Paths Selection Section");
            RBR rbr = new RBR(graph);

            Map<Path, Double> volumes = null;
            if (volumePath != null) {
                File commvol = new File(volumePath);
                if (commvol.exists()) {
                    System.out
                            .println("Getting volumes from " + volumePath);
                    volumes = communicationVolume(allMinimalPaths, commvol, graph);
                }
            }

            List<List<Path>> chosenPaths = selectPaths(allMinimalPaths, graph, volumes);

            System.out.println(" - RBR Section");
            RBRSection(shouldMerge, graph, allMinimalPaths, rbr, "full"+"_"+faultyPercentage);
            RBRSection(shouldMerge, graph, chosenPaths, rbr, "custom"+"_"+faultyPercentage);
            StatisticalAnalyser statistics = new StatisticalAnalyser(graph, rbr.regions(), volumes);
            printResults(chosenPaths, statistics);
        }
    }

    private static GraphRestrictions SBRSection(Graph graph) {
        BidimensionalSBRPolicy policy = new BidimensionalSBRPolicy();
        SR sbr = new SR(graph,policy);
        System.out.println("Compute the segments");
        sbr.computeSegments();
        System.out.println("Set the restrictions");
        sbr.setrestrictions();
        return sbr.restrictions();
    }

    private static void RBRSection(boolean shouldMerge, Graph graph, List<List<Path>> allMinimalPaths, RBR rbr, String fileSuffix) {
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

    private static int numberOfEdges(int rows, int columns) {
        return (columns - 1)*rows + (rows - 1)*columns;
    }

    private static List<List<Path>> selectPaths(List<List<Path>> paths, Graph g, Map<Path, Double> volumes) {
        List<List<Path>> chosenPaths = null;
        LinkWeightTracker lwTracker = new LinkWeightTracker(g, volumes);
        int choice = 5;
        switch (choice) {
            case 0: // No selection (all paths)
                chosenPaths = paths;
                break;
            case 1: // Random selection
                chosenPaths = new RandomPathSelector(paths, lwTracker).selection();
                break;
            case 2: // Minimal weight
                chosenPaths = new ComparativePathSelector(paths, new Path.MinWeightComparator(lwTracker), 10, lwTracker).selection();
                break;
            case 5: // Maximal weight
                chosenPaths = new ComparativePathSelector(paths, new Path.MaxWeightComparator(lwTracker), 2, lwTracker).selection();
        }
        return chosenPaths;
    }

    private static boolean hasEnoughEdges(int rows, int columns, double percentage) {
        int numberOfFaultyEdges = (int) Math.ceil((double) numberOfEdges(rows, columns) * percentage);
        int numberOfGoodEdges = numberOfEdges(rows, columns) - numberOfFaultyEdges;
        int numberOfVertices = rows * columns;

        return (numberOfGoodEdges >= numberOfVertices - 1);
    }

    private static void printResults(List<List<Path>> paths, StatisticalAnalyser statistics) {
        double lwAverage = statistics.averageLinkWeight(paths);
        double lwStdDeviation = statistics.standardDeviationLinkWeight(paths);
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

    private static Map<Path, Double> communicationVolume(List<List<Path>> paths, File commvol, Graph graph) {

        int N = graph.columns()*graph.rows();
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

        for(List<Path> alp : paths) {
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

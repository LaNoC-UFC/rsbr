import rbr.Region;
import util.*;
import java.util.*;

public class StatisticalAnalyser {

    private Graph graph;
    private Map<Vertex, ArrayList<Region>> regionsForVertex;

    public StatisticalAnalyser(Graph graph, Map<Vertex, ArrayList<Region>> regionsForVertex) {
        this.graph = graph;
        this.regionsForVertex = regionsForVertex;
    }

    public double standardDeviationLinkWeight(ArrayList<ArrayList<Path>> paths) {
        double accumulatedDeviation = 0.0;
        double average = averageLinkWeight(paths);
        Collection<Double> edgesWeight = linkWeightTracker(paths).edgesWeight();
        for (Double linkWeight : edgesWeight) {
            accumulatedDeviation += Math.pow((linkWeight - average), 2);
        }

        int nEdges = edgesWeight.size();
        return  Math.sqrt(accumulatedDeviation / (double) nEdges);
    }

    public double averageLinkWeight(ArrayList<ArrayList<Path>> paths) {
        double accumulatedLinkWeight = 0.0;
        Collection<Double> edgesWeight = linkWeightTracker(paths).edgesWeight();
        for (Double linkWeight : edgesWeight) {
            accumulatedLinkWeight += linkWeight;
        }

        int nEdges = edgesWeight.size();
        return accumulatedLinkWeight / (double) nEdges;
    }

    public int maxNumberOfRegions() {
        return numberOfRegionsPerVertex().get(numberOfRegionsPerVertex().size() - 1);
    }

    public int minNumberOfRegions() {
        return numberOfRegionsPerVertex().get(0);
    }

    public double averageNumberOfRegions() {
        int totalNumberOfRegions = 0;
        for (int size : numberOfRegionsPerVertex())
            totalNumberOfRegions += size;
        return (double)(totalNumberOfRegions / numberOfRegionsPerVertex().size());
    }

    private List<Integer> numberOfRegionsPerVertex () {
        List<Integer> numberOfRegionsPerVertex  = new ArrayList<>();
        for (Vertex r : graph.getVertices()) {
            numberOfRegionsPerVertex .add(regionsForVertex.get(r).size());
        }
        Collections.sort(numberOfRegionsPerVertex );
        return numberOfRegionsPerVertex ;
    }

    public double standardDeviationPathWeight(ArrayList<ArrayList<Path>> paths) {
        LinkWeightTracker lwTracker = linkWeightTracker(paths);
        int nPaths = 0;
        double accumulatedDeviation = 0.0;
        double average = averagePathWeight(paths);
        for(ArrayList<Path> alp : paths) {
            nPaths += alp.size();
            for(Path path: alp) {
                accumulatedDeviation += Math.pow((lwTracker.weight(path) - average),2);
            }
        }
        return Math.sqrt(accumulatedDeviation/(double)nPaths);
    }

    public double averagePathWeight(ArrayList<ArrayList<Path>> paths) {
        LinkWeightTracker lwTracker = linkWeightTracker(paths);
        double accumulatedPathWeight = 0.0;
        int nPaths = 0;
        for(ArrayList<Path> alp : paths) {
            nPaths += alp.size();
            for(Path path: alp)
                accumulatedPathWeight += lwTracker.weight(path);
        }
        return accumulatedPathWeight/(double)nPaths;
    }

    public double standardDeviationPathNormWeight(ArrayList<ArrayList<Path>> paths) {
        LinkWeightTracker lwTracker = linkWeightTracker(paths);
        int nPaths = 0;
        double accumulatedDeviation = 0.0;
        double average = averagePathNormWeight(paths);
        for(ArrayList<Path> alp : paths) {
            nPaths += alp.size();
            for(Path path: alp) {
                accumulatedDeviation += Math.pow((average - lwTracker.weight(path)/(double)(path.size()-1)),2);
            }
        }
        return Math.sqrt(accumulatedDeviation/(double)nPaths);
    }

    public double averagePathNormWeight(ArrayList<ArrayList<Path>> paths) {
        LinkWeightTracker lwTracker = linkWeightTracker(paths);
        double accumulatedPathWeight = 0;
        int nPaths = 0;
        for(ArrayList<Path> alp : paths) {
            nPaths += alp.size();
            for(Path path: alp)
                accumulatedPathWeight += lwTracker.weight(path)/(double)(path.size()-1);
        }
        return accumulatedPathWeight/(double)nPaths;
    }

    private LinkWeightTracker linkWeightTracker(ArrayList<ArrayList<Path>> paths) {
        LinkWeightTracker lwTracker = new LinkWeightTracker(graph);
        for (ArrayList<Path> samePairPaths : paths) {
            lwTracker.addAll(samePairPaths);
        }
        return lwTracker;
    }
    public double averageRoutingDistance(ArrayList<ArrayList<Path>> paths) {
        double accumulatedPathLength = 0.0;
        int nPaths = paths.size() + graph.getVertices().size();
        for (ArrayList<Path> path : paths)
            accumulatedPathLength += path.size();
        // Cover paths with the same source and destination
        accumulatedPathLength += graph.getVertices().size();
        return accumulatedPathLength / (double)nPaths;
    }
}

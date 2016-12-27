import rbr.Region;
import util.*;
import java.util.*;

public class StatisticalAnalyser {

    private Graph graph;
    private Map<Vertex, List<Region>> regionsForVertex;
    private Map<Path, Double> pathsWeight;

    public StatisticalAnalyser(Graph graph, Map<Vertex, List<Region>> regionsForVertex, Map<Path, Double> volume) {
        this.graph = graph;
        this.regionsForVertex = regionsForVertex;
        pathsWeight = volume;
    }

    public double standardDeviationLinkWeight(List<List<Path>> paths) {
        double accumulatedDeviation = 0.0;
        double average = averageLinkWeight(paths);
        Collection<Double> edgesWeight = linkWeightTracker(paths).edgesWeight();
        for (Double linkWeight : edgesWeight) {
            accumulatedDeviation += Math.pow((linkWeight - average), 2);
        }

        int nEdges = edgesWeight.size();
        return  Math.sqrt(accumulatedDeviation / (double) nEdges);
    }

    public double averageLinkWeight(List<List<Path>> paths) {
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

    public double standardDeviationPathWeight(List<List<Path>> paths) {
        LinkWeightTracker lwTracker = linkWeightTracker(paths);
        int nPaths = 0;
        double accumulatedDeviation = 0.0;
        double average = averagePathWeight(paths);
        for(List<Path> alp : paths) {
            nPaths += alp.size();
            for(Path path: alp) {
                accumulatedDeviation += Math.pow((lwTracker.weight(path) - average),2);
            }
        }
        return Math.sqrt(accumulatedDeviation/(double)nPaths);
    }

    public double averagePathWeight(List<List<Path>> paths) {
        LinkWeightTracker lwTracker = linkWeightTracker(paths);
        double accumulatedPathWeight = 0.0;
        int nPaths = 0;
        for(List<Path> alp : paths) {
            nPaths += alp.size();
            for(Path path: alp)
                accumulatedPathWeight += lwTracker.weight(path);
        }
        return accumulatedPathWeight/(double)nPaths;
    }

    public double standardDeviationPathNormWeight(List<List<Path>> paths) {
        LinkWeightTracker lwTracker = linkWeightTracker(paths);
        int nPaths = 0;
        double accumulatedDeviation = 0.0;
        double average = averagePathNormWeight(paths);
        for(List<Path> alp : paths) {
            nPaths += alp.size();
            for(Path path: alp) {
                accumulatedDeviation += Math.pow((average - lwTracker.weight(path)/(double)(path.size()-1)),2);
            }
        }
        return Math.sqrt(accumulatedDeviation/(double)nPaths);
    }

    public double averagePathNormWeight(List<List<Path>> paths) {
        LinkWeightTracker lwTracker = linkWeightTracker(paths);
        double accumulatedPathWeight = 0;
        int nPaths = 0;
        for(List<Path> alp : paths) {
            nPaths += alp.size();
            for(Path path: alp)
                accumulatedPathWeight += lwTracker.weight(path)/(double)(path.size()-1);
        }
        return accumulatedPathWeight/(double)nPaths;
    }

    private LinkWeightTracker linkWeightTracker(List<List<Path>> paths) {
        LinkWeightTracker lwTracker = new LinkWeightTracker(graph, pathsWeight);
        for (List<Path> samePairPaths : paths) {
            lwTracker.addAll(samePairPaths);
        }
        return lwTracker;
    }

    public double averageRoutingDistance(List<List<Path>> paths) {
        double accumulatedPathLength = 0.0;
        int nPaths = oneHopPaths(graph);
        for (List<Path> alp : paths) {
            nPaths += alp.size();
            for (Path path : alp)
                accumulatedPathLength += path.size();
        }
        accumulatedPathLength += oneHopPaths(graph);
        return accumulatedPathLength / (double)nPaths;
    }

    private int oneHopPaths(Graph graph){
        return graph.getVertices().size();
    }
}

import rbr.Region;
import util.Edge;
import util.Graph;
import util.Path;
import util.Vertex;

import java.util.*;

public class StatisticalAnalyser {

    private Graph graph;
    private Map<Vertex, ArrayList<Region>> regionsForVertex;

    public StatisticalAnalyser(Graph graph, Map<Vertex, ArrayList<Region>> regionsForVertex) {
        this.graph = graph;
        this.regionsForVertex = regionsForVertex;
    }

    public double linkWeightStdDeviation() {
        double temp = 0.0;
        double mean = linkWeightAverage();

        for (Edge link : graph.getEdges())
            temp += (link.weight() - mean)
                    * (link.weight() - mean);

        int nEdges = graph.getEdges().size();
        return  Math.sqrt(temp / (double) nEdges);
    }

    public double linkWeightAverage() {
        double sum = 0.0;
        for (Edge link : graph.getEdges())
            sum += link.weight();

        int nEdges = graph.getEdges().size();
        return sum / (double) nEdges;
    }

    public int maxNumberOfRegions() {
        return regSizes().get(regSizes().size() - 1);
    }

    public int minNumberOfRegions() {
        return regSizes().get(0);
    }

    public double averageNumberOfRegions() {
        int sum = 0;
        for (int size : regSizes())
            sum += size;
        return (double)(sum / regSizes().size());
    }

    private List<Integer> regSizes() {
        List<Integer> regSizes = new ArrayList<>();
        for (Vertex r : graph.getVertices()) {
            regSizes.add(regionsForVertex.get(r).size());
        }
        Collections.sort(regSizes);
        return regSizes;
    }

    public double linkWeightMean(ArrayList<ArrayList<Path>> paths) {
        double acc = 0;
        for(ArrayList<Path> alp : paths) {
            Path path = alp.get(0);
            acc += ((double)path.size()-1.0)*path.volume();
        }
        return acc/(double)graph.getEdges().size();
    }

    public double pathWeightMean(ArrayList<ArrayList<Path>> paths) {
        double acc = 0;
        for(ArrayList<Path> alp : paths)
            acc += (double) (alp.get(0).size()-1);
        return acc*linkWeightMean(paths)/(double)paths.size();
    }

    public double pathWeightStdDeviation(ArrayList<ArrayList<Path>> paths) {
        int nPaths = 0;
        double acc = 0.0; double dev;
        double average = pathWeightAverage(paths);
        for(ArrayList<Path> alp : paths) {
            nPaths += alp.size();
            for(Path path: alp) {
                dev = path.getWeight() - average;
                acc += dev*dev;
            }
        }
        return Math.sqrt(acc/(double)nPaths);
    }

    public double pathWeightAverage(ArrayList<ArrayList<Path>> paths) {
        double acc = 0.0;
        int nPaths = 0;
        for(ArrayList<Path> alp : paths) {
            nPaths += alp.size();
            for(Path path: alp)
                acc += path.getWeight();
        }
        return acc/(double)nPaths;
    }

    public double pathNormWeightStdDeviation(ArrayList<ArrayList<Path>> paths) {
        int nPaths = 0;
        double acc = 0.0; double dev;
        double average = pathNormWeightAverage(paths);
        for(ArrayList<Path> alp : paths) {
            nPaths += alp.size();
            for(Path path: alp) {
                dev = average-path.getWeight()/(double)(path.size()-1);
                acc += dev*dev;
            }
        }
        return Math.sqrt(acc/(double)nPaths);
    }

    public double pathNormWeightAverage(ArrayList<ArrayList<Path>> paths) {
        double acc = 0;
        int nPaths = 0;
        for(ArrayList<Path> alp : paths) {
            nPaths += alp.size();
            for(Path path: alp)
                acc += path.getWeight()/(double)(path.size()-1);
        }
        return acc/(double)nPaths;
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

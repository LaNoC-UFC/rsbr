package util;

import java.math.BigInteger;
import java.util.*;

public class DeterministicFaultyGraphBuilder {

    static public Graph generateGraph(int rows, int columns, BigInteger index) {
        Graph goldenGraph = RegularGraphBuilder.generateGraph(rows, columns);
        if (BigInteger.ZERO.equals(index))
            return goldenGraph;
        if (index.compareTo(size(rows, columns)) >= 0)
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds!");

        Set<Edge> edgesToRemove = Combinatorics.combinationOf(edgesWithoutDuplicity(goldenGraph), index.subtract(BigInteger.ONE));
        removeEdges(goldenGraph, edgesToRemove);
        return goldenGraph;
    }

    private static void removeEdges(Graph graph, Set<Edge> edgesToBeRemoved) {
        for (Edge going : edgesToBeRemoved) {
            Edge coming = graph.adjunct(going.destination(), going.source());
            graph.removeEdge(going);
            graph.removeEdge(coming);
        }
    }

    private static Set<Edge> edgesWithoutDuplicity(Graph graph) {
        List<Edge> edgesOfGraph = graph.getEdges();
        for (int i = 0; i < edgesOfGraph.size(); i++) {
            Vertex source = edgesOfGraph.get(i).source();
            Vertex destination = edgesOfGraph.get(i).destination();
            for (int j = i + 1; j < edgesOfGraph.size(); j++) {
                if ((edgesOfGraph.get(j).source().equals(destination)) && (edgesOfGraph.get(j).destination().equals(source))) {
                    edgesOfGraph.remove(j);
                }
            }
        }
        return new HashSet<>(edgesOfGraph);
    }

    public static BigInteger size(int rows, int columns) {
        int maxFaultyOfGraph = maximumOfFaultyEdges(rows, columns);
        BigInteger numberOfTopologies = BigInteger.ZERO;
        for (int i = 0; i <= maxFaultyOfGraph; i++) {
            numberOfTopologies = numberOfTopologies.add(numberOfGraphsForNumberFaulty(rows, columns, i));
        }
        return numberOfTopologies;
    }

    private static BigInteger numberOfGraphsForNumberFaulty(int rows, int columns, int numberOfFaultyEdges) {
        return Combinatorics.binomialCoefficient(numberOfEdges(rows, columns), numberOfFaultyEdges);
    }

    private static int maximumOfFaultyEdges(int rows, int columns) {
        return numberOfEdges(rows, columns) - minimumOfEdges(rows, columns);
    }

    private static int numberOfEdges(int rows, int columns) {
        return (columns - 1) * rows + (rows - 1) * columns;
    }

    private static int minimumOfEdges(int rows, int columns) {
        return (rows * columns) - 1;
    }
}

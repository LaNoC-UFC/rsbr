package util;

import java.util.*;

public class DeterministicFaultyGraphBuilder {

    static public Graph generateGraph(int rows, int columns, int index) {
        Graph goldenGraph = RegularGraphBuilder.generateGraph(rows, columns);
        if (index == 0)
            return goldenGraph;
        if (index > numberOfTopologies(rows, columns))
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds!");

        List<Set<Edge>> edgesToRemove = Combinatorics.allCombinationsOf(getEdgesWithoutDuplicity(goldenGraph));
        sortListBySize(edgesToRemove);
        removeEdgesFromGoldenGraph(goldenGraph, edgesToRemove.get(index));
        return goldenGraph;
    }

    private static void sortListBySize(List<Set<Edge>> graphsCombinations) {
        Collections.sort(graphsCombinations, (o1, o2) -> o1.size() - o2.size());
    }

    private static void removeEdgesFromGoldenGraph(Graph goldenGraph, Set<Edge> sortedGraphsCombinations) {
        for (Edge s : sortedGraphsCombinations) {
            Edge t = goldenGraph.adjunct(s.destination(), s.source());
            goldenGraph.removeEdge(s);
            goldenGraph.removeEdge(t);
        }
    }

    private static Set<Edge> getEdgesWithoutDuplicity(Graph goldenGraph) {
        List<Edge> edgesOfGraph = goldenGraph.getEdges();
        for (int i = 0; i < edgesOfGraph.size(); i++) {
            Vertex source = edgesOfGraph.get(i).source();
            Vertex destination = edgesOfGraph.get(i).destination();

            for (int j = i + 1; j < edgesOfGraph.size(); j++) {
                if ((edgesOfGraph.get(j).source().equals(destination))
                        && (edgesOfGraph.get(j).destination().equals(source))) {
                    edgesOfGraph.remove(j);
                }
            }
        }
        return new HashSet<>(edgesOfGraph);
    }

    private static int numberOfEdges(int rows, int columns) {
        return (columns - 1) * rows + (rows - 1) * columns;
    }

    private static int maximumOfFaultyEdges(int rows, int columns) {
        return numberOfEdges(rows, columns) - minimumOfEdges(rows, columns);
    }

    private static int numberOfTopologies(int rows, int columns) {
        int maxFaultyOfGraph = maximumOfFaultyEdges(rows, columns);
        int numberOfTopologies = 0;
        for (int i = 0; i <= maxFaultyOfGraph; i++) {
            numberOfTopologies += numberOfGraphsForNumberFaulty(rows, columns, i);
        }
        return numberOfTopologies;
    }

    private static int numberOfGraphsForNumberFaulty(int rows, int columns, int nmFaulty) {
        return (int) Combinatorics.binomialCoefficient(numberOfEdges(rows, columns), nmFaulty);
    }

    private static int minimumOfEdges(int rows, int columns) {
        return (rows * columns) - 1;
    }
}

package util;

public class RandomFaultyGraphBuilder {

    static public Graph generateGraph(int dimX, int dimY, double faultPercentage) {

        Graph result = RegularGraphBuilder.generateGraph(dimX, dimY);
        int totalOfEdges = result.getEdges().size()/2;
        int numberOfFaultyEdges = (int)Math.ceil((double)totalOfEdges*faultPercentage);
        assert totalOfEdges - numberOfFaultyEdges > dimX*dimY - 1;
        for(int i = 0; i < numberOfFaultyEdges; i++)
            removeNoBridgeLink(result);
        return result;
    }

    static private void removeNoBridgeLink(Graph graph) {
        boolean success = false;
        while(!success) {
            int edgeIndex = (int)(Math.random()*((double) graph.getEdges().size()));
            // deal with sibling edges
            Edge tic = graph.getEdges().get(edgeIndex);
            Edge tac = tic.destination().edge(tic.source());
            success = removeEdgesOrFail(graph, tic, tac);
        }
    }

    static private boolean removeEdgesOrFail(Graph graph, Edge tic, Edge tac) {
        // do
        graph.removeEdge(tic);
        graph.removeEdge(tac);
        if(!graph.haveIsolatedCores())
            return true;
        // undo
        graph.addEdge(tic);
        graph.addEdge(tac);
        return false;
    }
}

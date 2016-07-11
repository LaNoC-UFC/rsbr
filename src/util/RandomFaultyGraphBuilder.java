package util;

public class RandomFaultyGraphBuilder {

    static public Graph generateGraph(int dimX,int dimY, double perc) {

        Graph result = RegularGraphBuilder.generateGraph(dimX, dimY);

        int NumberOfEdges = (dimX-1)*dimY + dimX*(dimY-1);
        int nFalts = (int)Math.ceil((double)NumberOfEdges*perc);

        //Adiciona Falhas e checa isolamento
        for(int i=0;i<nFalts;i++)
        {
            while(true)
            {
                int idx = (int)(Math.random()*((double) result.getEdges().size()));
                Edge toRemoveIndo = result.getEdges().get(idx);
                Edge toRemoveVindo = toRemoveIndo.destination().edge(toRemoveIndo.source());

                result.removeEdge(toRemoveIndo);
                result.removeEdge(toRemoveVindo);

                if(result.haveIsolatedCores())
                {
                    result.addEdge(toRemoveIndo);
                    result.addEdge(toRemoveVindo);
                }
                else break;
            }
        }
        return result;
    }
}

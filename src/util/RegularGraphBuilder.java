package util;

public class RegularGraphBuilder {
    static private String[] ports = { "N", "S", "E", "W" };

    static public Graph generateGraph(int dimX, int dimY) {
        Graph result = new Graph(dimX, dimY);

        //Adiciona Vertices
        for(int x=0; x<dimX; x++)
            for(int y=0; y<dimY; y++)
                result.addVertex(x+"."+y);

        //Add Edges
        for(int y=0; y<dimY; y++)
            for(int x=0; x<dimX; x++)
            {
                if(contains(result, x+"."+(y+1)))
                    result.addEdge(result.vertex(x+"."+y), result.vertex(x+"."+(y+1)), ports[0]);
                if(contains(result, x+"."+(y-1)))
                    result.addEdge(result.vertex(x+"."+y), result.vertex(x+"."+(y-1)), ports[1]);
                if(contains(result, (x+1)+"."+y))
                    result.addEdge(result.vertex(x+"."+y), result.vertex((x+1)+"."+y), ports[2]);
                if(contains(result, (x-1)+"."+y))
                    result.addEdge(result.vertex(x+"."+y), result.vertex((x-1)+"."+y), ports[3]);
            }
        return result;
    }

    static private boolean contains(Graph g, String vertex) {
        for (int i = 0; i < g.getVertices().size(); i++) {

            if (vertex.equals(g.getVertices().get(i).name())) {
                return true;
            }
        }
        return false;
    }
}

package rbr;

import util.Aresta;
import util.Graph;
import util.Path;
import util.Vertice;

import java.util.ArrayList;
import java.util.Collections;

public class PathFinder {
    private Graph graph;

    public PathFinder(Graph g){
        graph = g;
    }

    /*
	 * Nova versao da busca de pacotes. Retorna dividido por par de comunicação
	 * ordenado por tamanho dos caminhos.
	 */
    public ArrayList<ArrayList<Path>> pathsComputation() {
        ArrayList<Path> allPaths = new ArrayList<Path>();
        ArrayList<Path> lastPaths = new ArrayList<Path>();
        ArrayList<String> pairs = new ArrayList<String>();
        // N = 1 hop
        for (Vertice src : graph.getVertices()) {
            for (Aresta e : src.getAdj()) {
                Vertice dst = e.getDestino();
                if (src.getRestriction("I").contains(src.getAresta(dst).getCor())){ // nao eh permitido
//					System.out.println("LOCAL");
                    continue;
                }
                Path p = new Path();
                p.add(src);
                p.add(dst);
                lastPaths.add(p);
                String pair = src.getNome() + ":" + dst.getNome();
                pairs.add(pair);
                //System.out.println(pair + " #"+pairs.size());
            }
        }
        System.out.println("Tamanho: 1"+" - "+lastPaths.size()+" paths.");
        allPaths.addAll(lastPaths);
        //savePathInFile("paths 1 hop", lastPaths);

        int nPairs = graph.dimX() * graph.dimY()
                * (graph.dimX() * graph.dimY() - 1);
        // N > 1 hop
        while (pairs.size() < nPairs) { // pares cadastrados menor que numero de
            // fluxos
            ArrayList<Path> valid = new ArrayList<Path>(); // actual mininal paths
            //System.out.println("Tamanho atual: " + lastPaths.get(0).size());
            for (Path p : lastPaths) {
                Vertice src = p.dst(); // fonte atual
                Vertice pre = p.get(p.size() - 2); // predecessor
                String inColor = src.getAresta(pre).getCor(); // porta de
                // entrada
                for (Aresta e : src.getAdj()) {
                    Vertice dst = e.getDestino();
                    if (dst == pre) // esta voltando
                        continue;
                    //if (p.contains(dst)) // esta cruzando
                    //	continue;
                    if (src.getRestriction("I").contains(src.getAresta(dst).getCor())){ // nao eh permitido
//						System.out.println("LOCAL");
                        continue;
                    }
                    if (src.getRestriction(inColor).contains(src.getAresta(dst).getCor())) // nao eh permitido
                        continue;
                    if (pairs.contains(p.src().getNome() + ":" + dst.getNome())) // nao minimo
                        continue;
                    Path q = new Path(p);
                    q.add(dst);
                    valid.add(q);
                }
            }
            System.out.println("Tamanho: "+lastPaths.get(0).size()+" - "+valid.size()+" paths.");
            allPaths.addAll(valid);
            //savePathInFile("paths"+lastPaths.get(0).size()+"hops", valid);
            lastPaths = null;
            lastPaths = valid;
            for (Path p : valid) {
                String pair = p.src().getNome() + ":" + p.dst().getNome();
                if (!pairs.contains(pair)) {
                    pairs.add(pair);
                    //System.out.println(pair + " #"+pairs.size());
                }
            }
            valid = null;
        }
        return divideByPair(allPaths);
    }

    private ArrayList<ArrayList<Path>> divideByPair(ArrayList<Path> paths) {
        ArrayList<ArrayList<Path>> paths2 = new ArrayList<ArrayList<Path>>();
        ArrayList<Path> aux = new ArrayList<Path>();
        Collections.sort(paths, new Path.SrcDst()); // por par
        Collections.sort(paths); // soh pelo comprimento
        for(int i = 0; i < paths.size(); i++){
            Path act = paths.get(i);
            aux.add(act);
            if(i < paths.size()-1) {
                Path next = paths.get(i+1);
                if(!act.src().equals(next.src()) || !act.dst().equals(next.dst())) {
                    paths2.add(aux);
                    aux = new ArrayList<Path>();
                }
            }
            else
                paths2.add(aux);
        }
        return paths2;
    }

}

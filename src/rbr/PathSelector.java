package rbr;

import util.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PathSelector {

    /*
     * Compara duas listas de Path e considera maior aquela que contém mais
     * Path, ou seja, que possui maior adaptatividade (diversidade).
     */
    private static class BySize implements Comparator<ArrayList<Path>> {

        @Override
        public int compare(ArrayList<Path> p0, ArrayList<Path> p1) {
            if(p0.size() < p1.size()) return -1;
            if(p0.size() > p1.size()) return +1;
            return 0;
        }

    }

    private static class ByDev implements Comparator<ArrayList<Path>> {

        private Comparator<Path> comp;

        public ByDev(Comparator<Path> c) {
            comp = c;
        }

        @Override
        public int compare(ArrayList<Path> p0, ArrayList<Path> p1) {
            return comp.compare(p0.get(0), p1.get(0))*-1;
        }

    }

    /*
 * Seleciona aleatoriamente 1 caminho para cada par de comunicação.
 */
    public ArrayList<ArrayList<Path>> pathSelection(ArrayList<ArrayList<Path>> p) {
        return pathSelection(p, 0);
    }

    /*
     * Seleciona aleatoriamente perc dos caminhos de cada par de comunicação.
     */
    public ArrayList<ArrayList<Path>> pathSelection(ArrayList<ArrayList<Path>> p, double perc) {
        ArrayList<ArrayList<Path>> selec = new ArrayList<ArrayList<Path>>();
        for(ArrayList<Path> alp: p) {
            Collections.shuffle(alp);
            int n = (perc*alp.size() < 1.0) ? 1 : (int) Math.round(perc*alp.size());
            ArrayList<Path> sub = new ArrayList<Path>();
            for(int i = 0; i < n; i++) {
                alp.get(i).incremWeight();
                sub.add(alp.get(i));
            }
            selec.add(sub);
        }
        return selec;
    }

    /*
     * Seleciona 1 caminho para cada par de comunicação, usando o comparador passado
     */
    public ArrayList<ArrayList<Path>> pathSelection(ArrayList<ArrayList<Path>> p, Comparator<Path> c) {
        return pathSelection(p, 0, c, 1);
    }

    /*
     * Seleciona 1 caminho para cada par de comunicação, usando o comparador passado
     */
    public ArrayList<ArrayList<Path>> pathSelection(ArrayList<ArrayList<Path>> p, Comparator<Path> c, int iterat) {
        return pathSelection(p, 0, c, iterat);
    }

    /*
     * Seleciona perc dos caminhos de cada par de comunicação, usando o comparador passado
     */
    public ArrayList<ArrayList<Path>> pathSelection(ArrayList<ArrayList<Path>> p, double perc, Comparator<Path> c, int iterat) {
        ArrayList<ArrayList<Path>> selec = new ArrayList<ArrayList<Path>>();

        Collections.sort(p, new BySize()); // sort by number of paths by pair
        for(ArrayList<Path> alp: p) {
            Collections.sort(alp, c);
            int n = (perc*alp.size() < 1.0) ? 1 : (int) (perc*alp.size());
            ArrayList<Path> sub = new ArrayList<Path>();
            for(int i = 0; i < n; i++) {
                alp.get(i).incremWeight();
                sub.add(alp.get(i));
            }
            selec.add(sub);
        }


        for(int i = iterat; i > 1; i--) {
            // Caso busque equalização
            if(c.getClass() == Path.PropWeight.class) {
                Collections.sort(selec, new ByDev(c));
                Collections.sort(p, new ByDev(c));
            }
            for(int j = 0; j < p.size(); j++) { // each pair
                if(p.get(j).size() == 1)
                    continue;
                ArrayList<Path> pair = selec.get(j);
                for(Path path : pair)
                    path.decremWeight();
                pair.removeAll(pair); // esvazia sublista
                ArrayList<Path> alp = p.get(j);
                Collections.sort(alp, c);
                int n = (perc*alp.size() < 1.0) ? 1 : (int) (perc*alp.size());
                for(int k = 0; k < n; k++) {
                    alp.get(k).incremWeight();
                    pair.add(alp.get(k));
                }
            }
        }

        return selec;
    }

}

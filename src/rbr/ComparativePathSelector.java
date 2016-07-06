package rbr;

import util.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ComparativePathSelector {

    private ArrayList<ArrayList<Path>> paths;
    private Comparator<Path> comparator;
    private int iterationsCount = 0;
    private double percentageOfPaths = 0;

    private static class ByNumberOfPaths implements Comparator<ArrayList<Path>> {

        @Override
        public int compare(ArrayList<Path> p0, ArrayList<Path> p1) {
            if(p0.size() < p1.size()) return -1;
            if(p0.size() > p1.size()) return +1;
            return 0;
        }
    }

    private static class ByStandardDeviation implements Comparator<ArrayList<Path>> {

        private Comparator<Path> comp;

        private ByStandardDeviation(Comparator<Path> c) {
            comp = c;
        }

        @Override
        public int compare(ArrayList<Path> p0, ArrayList<Path> p1) {
            return comp.compare(p0.get(0), p1.get(0))*-1;
        }
    }

    public ComparativePathSelector(ArrayList<ArrayList<Path>> paths, Comparator<Path> comparator, int iterationsCount) {
        this.paths = paths;
        this.comparator = comparator;
        this.iterationsCount = iterationsCount;
    }

    public ArrayList<ArrayList<Path>> selection() {
        ArrayList<ArrayList<Path>> selec = new ArrayList<ArrayList<Path>>();

        Collections.sort(paths, new ByNumberOfPaths()); // sort by number of paths by pair
        for(ArrayList<Path> alp: paths) {
            Collections.sort(alp, comparator);
            int n = (percentageOfPaths *alp.size() < 1.0) ? 1 : (int) (percentageOfPaths *alp.size());
            ArrayList<Path> sub = new ArrayList<Path>();
            for(int i = 0; i < n; i++) {
                alp.get(i).incremWeight();
                sub.add(alp.get(i));
            }
            selec.add(sub);
        }

        for(int i = iterationsCount; i > 1; i--) {
            // Caso busque equalização
            if(comparator.getClass() == Path.PropWeight.class) {
                Collections.sort(selec, new ByStandardDeviation(comparator));
                Collections.sort(paths, new ByStandardDeviation(comparator));
            }
            for(int j = 0; j < paths.size(); j++) { // each pair
                if(paths.get(j).size() == 1)
                    continue;
                ArrayList<Path> pair = selec.get(j);
                for(Path path : pair)
                    path.decremWeight();
                pair.removeAll(pair); // esvazia sublista
                ArrayList<Path> alp = paths.get(j);
                Collections.sort(alp, comparator);
                int n = (percentageOfPaths *alp.size() < 1.0) ? 1 : (int) (percentageOfPaths *alp.size());
                for(int k = 0; k < n; k++) {
                    alp.get(k).incremWeight();
                    pair.add(alp.get(k));
                }
            }
        }
        return selec;
    }
}

package rbr;

import util.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ComparativePathSelector {

    private ArrayList<ArrayList<Path>> paths;
    private Comparator<Path> comparator;
    private int iterationsCount = 0;

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
        Collections.sort(paths, new ByNumberOfPaths());

        ArrayList<ArrayList<Path>> result = new ArrayList<>();
        for(ArrayList<Path> samePairPaths: paths) {
            result.add(selectPath(samePairPaths));
        }

        for(int i = 0; i < iterationsCount - 1; i++) {
            reselectPaths(result);
        }
        return result;
    }

    private void reselectPaths(ArrayList<ArrayList<Path>> selectedPaths) {
        for(int j = 0; j < paths.size(); j++) {
            ArrayList<Path> samePairPaths = paths.get(j);
            if(samePairPaths.size() == 1)
                continue;
            unselectPaths(selectedPaths, j);
            selectedPaths.add(j, selectPath(samePairPaths));
        }
    }

    private void unselectPaths(ArrayList<ArrayList<Path>> selectedPaths, int index) {
        ArrayList<Path> pair = selectedPaths.remove(index);
        for(Path path : pair)
            path.decremWeight();
    }

    private ArrayList<Path> selectPath(ArrayList<Path> samePairPaths) {
        Collections.sort(samePairPaths, comparator);
        Path selectedPath = samePairPaths.get(0);
        selectedPath.incremWeight();
        ArrayList<Path> result = new ArrayList<>();
        result.add(selectedPath);
        return result;
    }
}

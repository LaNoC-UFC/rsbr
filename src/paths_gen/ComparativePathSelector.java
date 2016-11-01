package paths_gen;

import java.util.*;
import util.*;

public class ComparativePathSelector {
    private ArrayList<ArrayList<Path>> paths;
    private Comparator<Path> comparator;
    private int iterationsCount = 0;
    private LinkWeightTracker linkWeightTracker;

    private static class ByNumberOfPaths implements Comparator<ArrayList<Path>> {
        @Override
        public int compare(ArrayList<Path> p0, ArrayList<Path> p1) {
            if(p0.size() < p1.size()) return -1;
            if(p0.size() > p1.size()) return +1;
            return 0;
        }
    }

    public ComparativePathSelector(ArrayList<ArrayList<Path>> paths,
                                   Comparator<Path> comparator,
                                   int iterationsCount,
                                   LinkWeightTracker tracker) {
        this.paths = paths;
        this.comparator = comparator;
        this.iterationsCount = iterationsCount;
        linkWeightTracker = tracker;
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
        for(ArrayList<Path> samePairPaths : paths) {
            if(samePairPaths.size() == 1)
                continue;
            unselectPaths(selectedPaths, paths.indexOf(samePairPaths));
            selectedPaths.add(paths.indexOf(samePairPaths), selectPath(samePairPaths));
        }
    }

    private void unselectPaths(ArrayList<ArrayList<Path>> selectedPaths, int index) {
        ArrayList<Path> pair = selectedPaths.remove(index);
        linkWeightTracker.removeAll(pair);
    }

    private ArrayList<Path> selectPath(ArrayList<Path> samePairPaths) {
        Collections.sort(samePairPaths, comparator);
        Path selectedPath = samePairPaths.get(0);
        linkWeightTracker.add(selectedPath);
        ArrayList<Path> result = new ArrayList<>();
        result.add(selectedPath);
        return result;
    }
}

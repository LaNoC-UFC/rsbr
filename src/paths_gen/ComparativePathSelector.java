package paths_gen;

import java.util.*;
import util.*;

public class ComparativePathSelector {
    private List<List<Path>> paths;
    private Comparator<Path> comparator;
    private int iterationsCount = 0;
    private LinkWeightTracker linkWeightTracker;

    private static class ByNumberOfPaths implements Comparator<List<Path>> {
        @Override
        public int compare(List<Path> p0, List<Path> p1) {
            if(p0.size() < p1.size()) return -1;
            if(p0.size() > p1.size()) return +1;
            return 0;
        }
    }

    public ComparativePathSelector(List<List<Path>> paths,
                                   Comparator<Path> comparator,
                                   int iterationsCount,
                                   LinkWeightTracker tracker) {
        this.paths = paths;
        this.comparator = comparator;
        this.iterationsCount = iterationsCount;
        linkWeightTracker = tracker;
    }

    public List<List<Path>> selection() {
        Collections.sort(paths, new ByNumberOfPaths());

        List<List<Path>> result = new ArrayList<>();
        for(List<Path> samePairPaths: paths) {
            result.add(selectPath(samePairPaths));
        }

        for(int i = 0; i < iterationsCount - 1; i++) {
            reselectPaths(result);
        }
        return result;
    }

    private void reselectPaths(List<List<Path>> selectedPaths) {
        for(List<Path> samePairPaths : paths) {
            if(samePairPaths.size() == 1)
                continue;
            unselectPaths(selectedPaths, paths.indexOf(samePairPaths));
            selectedPaths.add(paths.indexOf(samePairPaths), selectPath(samePairPaths));
        }
    }

    private void unselectPaths(List<List<Path>> selectedPaths, int index) {
        List<Path> pair = selectedPaths.remove(index);
        linkWeightTracker.removeAll(pair);
    }

    private List<Path> selectPath(List<Path> samePairPaths) {
        Collections.sort(samePairPaths, comparator);
        Path selectedPath = samePairPaths.get(0);
        linkWeightTracker.add(selectedPath);
        List<Path> result = new ArrayList<>();
        result.add(selectedPath);
        return result;
    }
}

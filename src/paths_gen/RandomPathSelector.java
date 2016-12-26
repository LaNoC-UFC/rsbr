package paths_gen;

import java.util.*;
import util.*;

public class RandomPathSelector {

    private List<List<Path>> paths;
    private LinkWeightTracker linkWeightTracker;

    public RandomPathSelector(List<List<Path>> paths,
                              LinkWeightTracker tracker) {
        this.paths = paths;
        linkWeightTracker = tracker;
    }

    public List<List<Path>> selection() {
        List<List<Path>> result = new ArrayList<>();
        for(List<Path> samePairPaths: paths) {
            result.add(selectPath(samePairPaths));
        }
        return result;
    }

    private List<Path> selectPath(List<Path> samePairPaths) {
        Collections.shuffle(samePairPaths);
        Path randomPath = samePairPaths.get(0);
        linkWeightTracker.add(randomPath);
        List<Path> result = new ArrayList<>();
        result.add(randomPath);
        return result;
    }
}

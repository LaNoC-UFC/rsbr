package paths_gen;

import java.util.*;
import util.*;

public class RandomPathSelector {

    private ArrayList<ArrayList<Path>> paths;
    private LinkWeightTracker linkWeightTracker;

    public RandomPathSelector(ArrayList<ArrayList<Path>> paths,
                              LinkWeightTracker tracker) {
        this.paths = paths;
        linkWeightTracker = tracker;
    }

    public ArrayList<ArrayList<Path>> selection() {
        ArrayList<ArrayList<Path>> result = new ArrayList<>();
        for(ArrayList<Path> samePairPaths: paths) {
            result.add(selectPath(samePairPaths));
        }
        return result;
    }

    private ArrayList<Path> selectPath(ArrayList<Path> samePairPaths) {
        Collections.shuffle(samePairPaths);
        Path randomPath = samePairPaths.get(0);
        linkWeightTracker.add(randomPath);
        ArrayList<Path> result = new ArrayList<>();
        result.add(randomPath);
        return result;
    }
}

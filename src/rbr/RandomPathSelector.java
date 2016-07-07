package rbr;

import util.Path;

import java.util.ArrayList;
import java.util.Collections;

public class RandomPathSelector {

    private ArrayList<ArrayList<Path>> paths;

    public RandomPathSelector(ArrayList<ArrayList<Path>> paths) {
        this.paths = paths;
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
        randomPath.incremWeight();
        ArrayList<Path> result = new ArrayList<>();
        result.add(randomPath);
        return result;
    }
}

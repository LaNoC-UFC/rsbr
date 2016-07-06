package rbr;

import util.Path;

import java.util.ArrayList;
import java.util.Collections;

public class RandomPathSelector {

    private double percentageOfPaths = 0.0;
    private ArrayList<ArrayList<Path>> paths;

    public RandomPathSelector(ArrayList<ArrayList<Path>> paths) {
        this.paths = paths;
    }

    public RandomPathSelector(ArrayList<ArrayList<Path>> paths, double percentageOfPaths) {
        this.paths = paths;
        this.percentageOfPaths = percentageOfPaths;
    }

    public ArrayList<ArrayList<Path>> selection() {
        ArrayList<ArrayList<Path>> selec = new ArrayList<ArrayList<Path>>();
        for(ArrayList<Path> alp: paths) {
            Collections.shuffle(alp);
            int n = (percentageOfPaths *alp.size() < 1.0) ? 1 : (int) Math.round(percentageOfPaths *alp.size());
            ArrayList<Path> sub = new ArrayList<Path>();
            for(int i = 0; i < n; i++) {
                alp.get(i).incremWeight();
                sub.add(alp.get(i));
            }
            selec.add(sub);
        }
        return selec;
    }
}

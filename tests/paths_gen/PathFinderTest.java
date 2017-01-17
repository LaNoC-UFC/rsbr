package paths_gen;

import org.junit.Assert;
import org.junit.Test;
import util.Graph;
import util.GraphRestrictions;
import util.Path;
import util.RegularGraphBuilder;
import util.Vertex;

import java.util.List;

public class PathFinderTest {

    // Reproduces bug #114
    @Test
    public void NonMinimalPathsTipsMatch() throws Exception {
        Graph g = RegularGraphBuilder.generateGraph(3, 4);
        PathFinder finder = new PathFinder(g, new GraphRestrictions(g));
        List<Vertex> vertices = g.getVertices();
        for (int src = 0; src < vertices.size(); src++) {
            Vertex source = vertices.get(src);
            for (int dst = src + 1; dst < vertices.size(); dst++) {
                Vertex destination = vertices.get(dst);
                for (Path p : finder.allPathsBetweenVertices(source, destination)) {
                    Assert.assertEquals(p.src(), source);
                    Assert.assertEquals(p.dst(), destination);
                }

            }
        }
    }

    @Test
    public void MinimalPathsHaveTheSameSize() throws Exception {
        Graph g = RegularGraphBuilder.generateGraph(5, 5);
        PathFinder finder = new PathFinder(g, new GraphRestrictions(g));
        List<List<Path>> minimalPaths = finder.minimalPathsForAllPairs();
        for (List<Path> paths : minimalPaths) {
            int hopCount = paths.get(0).size();
            paths.forEach(p -> Assert.assertEquals(hopCount, p.size()));
        }
    }
}

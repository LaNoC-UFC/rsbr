package rbr;

import org.junit.*;
import util.Vertex;
import java.util.*;

public class RegionsMergeTest {
    @Test
    public void SameRegionsCanBeMerged() throws Exception {
        Region oneRegion = new Region(new HashSet<>(), someDestinations(), new HashSet<>());
        Region theSameRegion = new Region(new HashSet<>(), someDestinations(), new HashSet<>());
        Assert.assertTrue(oneRegion.canBeMergedWith(theSameRegion));
    }

    @Test
    public void EmptyRegionsCanBeMerged() throws Exception {
        Region oneEmptyRegion = new Region(new HashSet<>(), new HashSet<>(), new HashSet<>());
        Region anotherEmptyRegion = new Region(new HashSet<>(), new HashSet<>(), new HashSet<>());
        Assert.assertTrue(oneEmptyRegion.canBeMergedWith(anotherEmptyRegion));
    }

    @Test
    public void EmptyRegionCanBeMergedWithAnyRegion() throws Exception {
        Region emptyRegion = new Region(new HashSet<>(), new HashSet<>(), new HashSet<>());
        Region oneRegion = new Region(new HashSet<>(), someDestinations(), new HashSet<>());
        Assert.assertTrue(emptyRegion.canBeMergedWith(oneRegion));
    }

    @Test
    public void EmptyListDoesntMerge() throws Exception {
        List<Region> noRegions = new ArrayList<>();
        Assert.assertFalse(RBR.mergeUnitary(noRegions));
    }

    @Test
    public void OneRegionDoesntMerge() throws Exception {
        List<Region> oneRegionOnly = new ArrayList<>();
        oneRegionOnly.add(new Region(new HashSet<>(), new HashSet<>(), new HashSet<>()));
        Assert.assertFalse(RBR.mergeUnitary(oneRegionOnly));
    }

    @Test
    public void SameRegionsDoMerge() throws Exception {
        List<Region> duplicatedRegions = new ArrayList<>();
        duplicatedRegions.add(new Region(new HashSet<>(), someDestinations(), new HashSet<>()));
        duplicatedRegions.add(new Region(new HashSet<>(), someDestinations(), new HashSet<>()));
        Assert.assertTrue(RBR.mergeUnitary(duplicatedRegions));
        Assert.assertEquals(1, duplicatedRegions.size());
    }

    @Test
    public void EmptyRegionDoMergeWithAnyRegion() throws Exception {
        List<Region> regions = new ArrayList<>();
        regions.add(new Region(new HashSet<>(), new HashSet<>(), new HashSet<>()));
        regions.add(new Region(new HashSet<>(), someDestinations(), new HashSet<>()));
        Assert.assertTrue(RBR.mergeUnitary(regions));
        Assert.assertEquals(1, regions.size());
    }

    static private Set<Vertex> someDestinations() {
        Set<Vertex> destinations = new HashSet<>();
        destinations.add(new Vertex("0.0"));
        destinations.add(new Vertex("0.1"));
        return destinations;
    }
}

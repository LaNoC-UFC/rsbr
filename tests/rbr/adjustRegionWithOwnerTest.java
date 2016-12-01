package rbr;

import java.util.*;
import org.junit.*;
import util.*;

public class adjustRegionWithOwnerTest {
    Set<Character> ip = new HashSet<>();
    Set<Character> op = new HashSet<>();
    Set<Vertex> dst = new HashSet<>();
    private RBR rbr;

    @Before
    public void setUp() {
        Graph graph = RegularGraphBuilder.generateGraph(4,4);
        rbr = new RBR(graph);
        ip.add('I');
        op.add('N');
        op.add('E');
        dst.add(new Vertex("0.3"));
        dst.add(new Vertex("1.3"));
        dst.add(new Vertex("1.2"));
    }

    @Test
    public void adjustedRegionIncludesOwnerWhenItIsTheOnlyOutsider() throws Exception {
        Region region = new Region(ip, dst, op);
        Vertex owner = new Vertex("0.2");
        Region adjustedRegion = rbr.adjustRegionWithOwner(region, owner);
        Assert.assertTrue(adjustedRegion.destinations().contains(owner));
        Assert.assertEquals(adjustedRegion.outsiders().size(), 0);
    }

    @Test
    public void adjustedRegionNotContainsOwner() throws Exception {
        dst.add(new Vertex("2.3"));
        Region region = new Region(ip, dst, op);
        Vertex owner = new Vertex("0.2");
        Region adjustedRegion = rbr.adjustRegionWithOwner(region, owner);
        Assert.assertFalse(adjustedRegion.destinations().contains(owner));
        Assert.assertTrue(adjustedRegion.outsiders().contains(owner));
    }

    @Test
    public void adjustedRegionIsTheSameWhenOwnerIsNotInsideBox() throws Exception {
        dst.add(new Vertex("0.2"));
        Region region = new Region(ip, dst, op);
        Vertex owner = new Vertex("0.1");
        Region adjustedRegion = rbr.adjustRegionWithOwner(region, owner);
        Assert.assertEquals(adjustedRegion, region);
    }

    @Ignore @Test
    public void adjustedRegionIncludesOwnerEvenWhenItIsNotTheOnlyOutsider() throws Exception {
        Set<Vertex> destinations = new HashSet<>();
        destinations.add(new Vertex("0.1"));
        destinations.add(new Vertex("0.3"));
        destinations.add(new Vertex("2.3"));
        destinations.add(new Vertex("2.2"));
        destinations.add(new Vertex("2.1"));
        Vertex owner = new Vertex("0.2");
        Region region = new Region(ip, destinations, op);
        Region adjustedRegion = rbr.adjustRegionWithOwner(region, owner);
        Assert.assertTrue(adjustedRegion.outsiders().contains(owner));
    }
}

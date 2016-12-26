package rbr;

import java.util.*;
import org.junit.*;
import util.*;

public class AdjustRegionsTest {

    Set<Character> ip = new HashSet<>();
    Set<Character> op = new HashSet<>();
    private Region original;
    private RBR rbr;

    @Before
    public void setUp() {
        Graph graph = RegularGraphBuilder.generateGraph(4,4);
        rbr = new RBR(graph);
        Set<Vertex> dst = new HashSet<>();
        ip.add('I');
        op.add('N');
        dst.add(new Vertex("0.1"));
        dst.add(new Vertex("0.2"));
        dst.add(new Vertex("0.3"));
        dst.add(new Vertex("1.2"));
        dst.add(new Vertex("1.3"));
        dst.add(new Vertex("2.2"));
        dst.add(new Vertex("2.3"));
        dst.add(new Vertex("3.3"));
        original = new Region(ip, dst, op);
    }

    @Test
    public void regionWithoutOutsidersIsntAdjusted() throws Exception {
        Set<Vertex> dstRegionWithoutOutsiders = new HashSet<>();
        dstRegionWithoutOutsiders.add(new Vertex("1.1"));
        Region regionWithoutOutsiders = new Region(ip, dstRegionWithoutOutsiders, op);
        List<Region> adjustedRegionWithoutOutsiders = rbr.adjustedRegionsFrom(regionWithoutOutsiders);
        List<Region> arrayRegionWithoutOutsiders = new ArrayList<>();
        arrayRegionWithoutOutsiders.add(regionWithoutOutsiders);
        Assert.assertEquals(adjustedRegionWithoutOutsiders, arrayRegionWithoutOutsiders);
    }

    @Test
    public void regionWithoutDestinationsIsntAdjusted() throws Exception {
        Set<Vertex> dstRegionWithoutDestinations = new HashSet<>();
        Region regionWithoutDestinations = new Region(ip, dstRegionWithoutDestinations, op);
        List<Region> adjustedRegionWithoutDestinations = rbr.adjustedRegionsFrom(regionWithoutDestinations);
        List<Region> arrayRegionWithoutDestinations = new ArrayList<>();
        arrayRegionWithoutDestinations.add(regionWithoutDestinations);
        Assert.assertEquals(adjustedRegionWithoutDestinations, arrayRegionWithoutDestinations);
    }

    @Test
    public void adjustedRegionsHaveSameInputPortsAsOriginal() throws Exception {
        for(Region adjustedRegion : rbr.adjustedRegionsFrom(original)) {
            Assert.assertEquals(adjustedRegion.inputPorts(), original.inputPorts());
        }
    }

    @Test
    public void adjustedRegionsHaveSameOutputPortsAsOriginal() throws Exception {
        for(Region adjustedRegion : rbr.adjustedRegionsFrom(original)) {
            Assert.assertEquals(adjustedRegion.outputPorts(), original.outputPorts());
        }
    }

    @Test
    public void adjustedRegionsHaveSameDestinationsAsOriginal() throws Exception {
        Set<Vertex> destinationsAdjustedRegions = new HashSet<>();
        for(Region adjustedRegion : rbr.adjustedRegionsFrom(original)) {
            destinationsAdjustedRegions.addAll(adjustedRegion.destinations());
        }
        Assert.assertEquals(destinationsAdjustedRegions, original.destinations());
    }

    @Test
    public void adjustedRegionsHaveNoOutsider() throws Exception {
        for(Region adjustedRegion : rbr.adjustedRegionsFrom(original)){
            Assert.assertTrue(adjustedRegion.outsiders().isEmpty());
        }
    }

    @Test
    public void adjustedRegionsDoNotIncludeOriginalOutsiders() throws Exception {
        for(Vertex originalOutsider : original.outsiders()){
            for(Region adjustedRegion : rbr.adjustedRegionsFrom(original)){
                Assert.assertFalse(adjustedRegion.destinations().contains(originalOutsider));
            }
        }
    }
}

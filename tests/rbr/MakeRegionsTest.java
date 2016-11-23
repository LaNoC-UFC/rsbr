package rbr;

import java.util.*;
import org.junit.*;
import util.*;

public class MakeRegionsTest {

    private Set<Character> inputPorts = new HashSet<>();
    private Set<Character> outputPorts = new HashSet<>();
    private Set<Vertex> destinations = new HashSet<>();
    private RBR rbr;

    @Before
    public void setUp() {
        Graph graph = RegularGraphBuilder.generateGraph(4,4);
        rbr = new RBR(graph);
        inputPorts.add('I');
        inputPorts.add('N');
        inputPorts.add('E');
        outputPorts.add('N');
        outputPorts.add('S');
        outputPorts.add('W');
        destinations.add(new Vertex("0.1"));
        destinations.add(new Vertex("0.2"));
        destinations.add(new Vertex("0.3"));
        destinations.add(new Vertex("1.0"));
        destinations.add(new Vertex("1.2"));
        destinations.add(new Vertex("1.3"));
        destinations.add(new Vertex("2.0"));
        destinations.add(new Vertex("2.3"));
        destinations.add(new Vertex("3.0"));
        destinations.add(new Vertex("3.1"));
        destinations.add(new Vertex("3.2"));
        destinations.add(new Vertex("3.3"));
    }

    @Test
    public void emptyDestinationsCreatesNoRegion() throws Exception {
        List<Region> createdRegionsWithoutDestinations = rbr.makeRegions(new HashSet<>(), inputPorts, outputPorts);
        Assert.assertTrue(createdRegionsWithoutDestinations.isEmpty());
    }

    @Test
    public void createdRegionsHaveOriginalInputPorts() throws Exception {
        for(Region createdRegion : rbr.makeRegions(destinations, inputPorts, outputPorts)) {
            Assert.assertEquals(createdRegion.inputPorts(), inputPorts);
        }
    }

    @Test
    public void createdRegionsHaveOriginalOutputPorts() throws Exception {
        for(Region createdRegion : rbr.makeRegions(destinations, inputPorts, outputPorts)) {
            Assert.assertEquals(createdRegion.outputPorts(), outputPorts);
        }
    }

    @Test
    public void createdRegionHasNoOutsider() throws Exception {
        for(Region createdRegion : rbr.makeRegions(destinations, inputPorts, outputPorts)){
            Assert.assertTrue(createdRegion.outsiders().isEmpty());
        }
    }

    @Test
    public void createdRegionsCoverAllOriginalDestinations() throws Exception {
        Set<Vertex> destinationsCreatedRegions = new HashSet<>();
        List<Region> createdRegions = rbr.makeRegions(new HashSet<>(destinations), inputPorts, outputPorts);
        for(Region createdRegion : createdRegions) {
            destinationsCreatedRegions.addAll(createdRegion.destinations());
        }
        Assert.assertEquals(destinationsCreatedRegions, destinations);
    }

    @Test
    public void numberCreatedRegionsIsMinimal() throws Exception {
        List<Region> createdRegions = rbr.makeRegions(destinations, inputPorts, outputPorts);
        for (int a = 0; a < createdRegions.size(); a++) {
            Region ra = createdRegions.get(a);
            for (int b = a + 1; b < createdRegions.size(); b++) {
                Region rb = createdRegions.get(b);
                Assert.assertFalse(ra.canBeMergedWith(rb));
            }
        }
    }
}

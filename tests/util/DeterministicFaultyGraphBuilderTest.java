package util;

import org.junit.Assert;
import org.junit.Test;

public class DeterministicFaultyGraphBuilderTest {

    @Test
    public void GraphWithoutEdgeHasOneConfiguration() throws Exception {
        Assert.assertEquals(1, DeterministicFaultyGraphBuilder.size(1, 1));
    }

    @Test
    public void GraphWithONeEdgeHasOneConfiguration() throws Exception {
        Assert.assertEquals(1, DeterministicFaultyGraphBuilder.size(1, 2));
    }

    @Test
    public void regularGraphDegree2HasFiveConfigurations() throws Exception {
        Assert.assertEquals(5, DeterministicFaultyGraphBuilder.size(2, 2));
    }

    @Test
    public void generationIsDeterministic() throws Exception {
        int size = 3;
        int index = (int) (Math.random() * DeterministicFaultyGraphBuilder.size(size, size));
        Graph aGraph = DeterministicFaultyGraphBuilder.generateGraph(size, size, index);
        Graph anotherGraph = DeterministicFaultyGraphBuilder.generateGraph(size, size, index);
        Assert.assertEquals(aGraph, anotherGraph);
    }
}

package util;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Random;

public class DeterministicFaultyGraphBuilderTest {

    @Test
    public void GraphWithoutEdgeHasOneConfiguration() throws Exception {
        Assert.assertEquals(BigInteger.ONE, DeterministicFaultyGraphBuilder.size(1, 1));
    }

    @Test
    public void GraphWithONeEdgeHasOneConfiguration() throws Exception {
        Assert.assertEquals(BigInteger.ONE, DeterministicFaultyGraphBuilder.size(1, 2));
    }

    @Test
    public void regularGraphDegree2HasFiveConfigurations() throws Exception {
        Assert.assertEquals(BigInteger.valueOf(5), DeterministicFaultyGraphBuilder.size(2, 2));
    }

    @Test
    public void generationIsDeterministic() throws Exception {
        int size = 10;
        BigInteger numberOfCombinations = DeterministicFaultyGraphBuilder.size(size, size);
        BigInteger index = bigRandomInteger(numberOfCombinations);

        Graph aGraph = DeterministicFaultyGraphBuilder.generateGraph(size, size, index);
        Graph anotherGraph = DeterministicFaultyGraphBuilder.generateGraph(size, size, index);
        Assert.assertEquals(aGraph, anotherGraph);
    }

    private BigInteger bigRandomInteger(BigInteger max) {
        BigInteger result;
        do {
            result = new BigInteger(max.bitLength(), new Random());
        } while (result.compareTo(max) >= 0);
        return result;
    }
}

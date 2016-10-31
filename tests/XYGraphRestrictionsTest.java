import org.junit.*;
import util.*;

public class XYGraphRestrictionsTest {
    @Test
    public void EmptyGraphHasNoRestriction() throws Exception {
        GraphRestrictions restrictions = XYGraphRestrictionsBuilder.XYGraphRestrictions(new Graph(0, 0));
        Assert.assertEquals(0, restrictions.size());
    }

    @Test
    public void XtoYIsAllowed() throws Exception {
        Graph g = RegularGraphBuilder.generateGraph(2, 2);
        GraphRestrictions restrictions = XYGraphRestrictionsBuilder.XYGraphRestrictions(g);
        Assert.assertFalse(restrictions.turnIsForbidden(g.vertex("0.0"), 'E', 'N'));
        Assert.assertFalse(restrictions.turnIsForbidden(g.vertex("0.1"), 'E', 'S'));
        Assert.assertFalse(restrictions.turnIsForbidden(g.vertex("1.0"), 'W', 'N'));
        Assert.assertFalse(restrictions.turnIsForbidden(g.vertex("1.1"), 'W', 'S'));
    }

    @Test
    public void YtoXIsForbidden() throws Exception {
        Graph g = RegularGraphBuilder.generateGraph(2, 2);
        GraphRestrictions restrictions = XYGraphRestrictionsBuilder.XYGraphRestrictions(g);
        Assert.assertTrue(restrictions.turnIsForbidden(g.vertex("0.0"), 'N', 'E'));
        Assert.assertTrue(restrictions.turnIsForbidden(g.vertex("0.1"), 'S', 'E'));
        Assert.assertTrue(restrictions.turnIsForbidden(g.vertex("1.0"), 'N', 'W'));
        Assert.assertTrue(restrictions.turnIsForbidden(g.vertex("1.1"), 'S', 'W'));
    }
}

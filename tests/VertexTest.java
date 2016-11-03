import org.junit.*;
import util.Vertex;

public class VertexTest {
    @Test
    public void VertexEqualsOverrideTest() throws Exception {
        Vertex a = new Vertex("0.0");
        Vertex b = new Vertex("0.0");
        Vertex c = new Vertex("0.0");
        Vertex d = new Vertex("1.1");
        // reflexive property
        Assert.assertTrue(a.equals(a));
        // symmetric property
        Assert.assertTrue(a.equals(b) == b.equals(a));
        // transitive property
        if (a.equals(b) && b.equals(c)) {
            Assert.assertTrue(a.equals(c));
        }
        // consistency property
        Assert.assertTrue(a.equals(b) == a.equals(b));
        // non-null property
        Assert.assertFalse(a.equals(null));
        // test with different Vertex
        Assert.assertFalse(a.equals(d));
    }

    @Test
    public void SameVerticesHaveSameHashCode() throws Exception {
        Vertex oneVertex = new Vertex("5.3");
        Vertex theSameVertex = new Vertex("5.3");
        Assert.assertEquals(oneVertex.hashCode(), theSameVertex.hashCode());
    }
}

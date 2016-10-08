package rbr;

import java.util.*;
import org.junit.*;
import util.*;

public class RoutingOptionTest {

	private Vertex sw;

	@Before
	public void setUp() {
		sw = new Vertex("01");
    }

	@Test
	public void equalsTest(){
		Assert.assertEquals(newRoutingOption("E", this.sw, "W"), newRoutingOption("E", this.sw, "W"));
	}

	@Test
	public void notEqualsTest(){
		Assert.assertNotEquals(newRoutingOption("ENW", this.sw, "S"), newRoutingOption("EW", this.sw, "S"));
	}

	@Test
	public void hashCodeEqualsTest(){
		Assert.assertEquals(newRoutingOption("W", this.sw, "S").hashCode(), newRoutingOption("W", this.sw, "S").hashCode());
	}

	@Test
	public void hashCodeNotEqualsTest(){
		Assert.assertNotEquals(newRoutingOption("E", this.sw, "NW").hashCode(), newRoutingOption("N", this.sw, "WE").hashCode());
	}

	private RoutingOption newRoutingOption(String ip, Vertex vertex, String op){
		Set<Character> inputPorts = new HashSet<>();
		for(Character c : ip.toCharArray()){
			inputPorts.add(c);
		}

		Set<Character> outputPorts = new HashSet<>();
		for(Character c : op.toCharArray()){
			outputPorts.add(c);
		}
		return new RoutingOption(inputPorts, vertex, outputPorts);
	}
}

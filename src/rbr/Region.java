package rbr;

import java.util.*;
import util.*;

public class Region {
	private Set<Character> inputPorts;
	private Set<Character> outputPorts;
	private Range box;
	private Set<Vertex> destinations;

	public Region(Set<Character> ip, Set<Vertex> destinations, Set<Character> op) {
		this.destinations = new HashSet<>(destinations);
		this.inputPorts = new HashSet<>(ip);
		this.outputPorts = new HashSet<>(op);
		this.updateBox();
	}

	private void updateBox() {
		int xMin = Integer.MAX_VALUE, yMin = Integer.MAX_VALUE;
		int xMax = 0, yMax = 0;
		for (Vertex vertex : this.destinations) {
			String[] xy = vertex.name().split("\\.");
			int x = Integer.valueOf(xy[0]);
			int y = Integer.valueOf(xy[1]);

			xMin = Math.min(xMin, x);
			yMin = Math.min(yMin, y);
			xMax = Math.max(xMax, x);
			yMax = Math.max(yMax, y);
		}
		this.box = Range.TwoDimensionalRange(xMin, xMax, yMin, yMax);
	}

	Set<Character> inputPorts() {
		return inputPorts;
	}

	Set<Character> outputPorts() {
		return outputPorts;
	}

	Set<Vertex> destinations() {
		return destinations;
	}

	Set<Vertex> destinationsIn(Range box) {
		Set<Vertex> result = new HashSet<>();
		for(Vertex vertex : destinations){
			if(vertex.isIn(box)) {
				result.add(vertex);
			}
		}
		return result;
	}

	Range box() {
		return box;
	}

	public String toString() {
		return this.box + " " + this.inputPorts + " " + this.outputPorts;
	}

	Region merge(Region that) {
		Set<Character> op = this.outputPorts();
		op.retainAll(that.outputPorts());
		Set<Character> ip = that.inputPorts();
		ip.addAll(this.inputPorts());
		Region reg = new Region(ip, this.destinations(), op);
		reg.box = reg.box().combination(that.box());
		reg.destinations().addAll(that.destinations());
		reg.updateBox();
		return reg;
	}

	Set<Vertex> outsiders() {
		Set<Vertex> result = new HashSet<>();
		for (int x = this.box().min(0); x <= this.box().max(0); x++) {
			for (int y = this.box().min(1); y <= this.box().max(1); y++) {
				Vertex vertex = new Vertex(x + "." + y);
				if(!destinations().contains(vertex)) {
					result.add(vertex);
				}
			}
		}
		return result;
	}

	boolean canBeMergedWith(Region that) {
		return (this.box().isContiguous(that.box()) && OutputPortIsSubSet(this.outputPorts(), that.outputPorts()));
	}

	private boolean OutputPortIsSubSet(Set<Character> outputPort1, Set<Character> outputPort2) {
		return (outputPort1.containsAll(outputPort2) || outputPort2.containsAll(outputPort1));
	}
}

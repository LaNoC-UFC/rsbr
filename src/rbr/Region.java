package rbr;

import java.util.*;
import util.*;

public class Region {
	// @ToDo inputPortsSet and outputPortsSet could actually be Sets. This would ease many operations on them:
	// @ToDo isSubset, getOpMerged (intersection), getIpMerged (union) and so on.
	private String inputPortsSet;
	private String outputPortsSet;
	private Range box;
	private Set<Vertex> destinations;

	public Region(String ip, Set<Vertex> destinations, String op) {
		this.destinations = new HashSet<>(destinations);
		this.inputPortsSet = ip;
		this.outputPortsSet = op;
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

	String inputPorts() {
		return inputPortsSet;
	}

	String outputPorts() {
		return outputPortsSet;
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
		return this.box + " " + this.inputPortsSet + " " + this.outputPortsSet;
	}

	Region merge(Region that) {
		String op = getOpMerged(this, that);
		String ip = getIpMerged(this, that);
		Region reg = new Region(ip, this.destinations(), op);
		reg.box = reg.box().combination(that.box());
		reg.destinations().addAll(that.destinations());
		reg.updateBox();
		return reg;
	}

	private static String getOpMerged(Region r1, Region r2) {
		String op;

		if (r1.outputPorts().contains(r2.outputPorts())) {
			op = r2.outputPorts();
		} else {
			op = r1.outputPorts();
		}

		return op;
	}

	private static String getIpMerged(Region r1, Region r2) {
		String ip = r2.inputPorts();
		for (int i = 0; i < r1.inputPorts().length(); i++) {
			if (!ip.contains(r1.inputPorts().substring(i, i + 1)))
				ip += r1.inputPorts().substring(i, i + 1);
		}
		return ip;
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
		return (this.box().isContiguous(that.box()) && opIsSubset(this, that));
	}

	private static boolean opIsSubset(Region r1, Region r2) {
		String r1Op = sortStrAlf(r1.outputPorts());
		String r2Op = sortStrAlf(r2.outputPorts());
		return (r1Op.contains(r2Op) || r2Op.contains(r1Op));
	}

	private static String sortStrAlf(String input) {
		char[] ip1 = input.toCharArray();
		Arrays.sort(ip1);
		return String.valueOf(ip1);
	}
}

package rbr;

import util.Range;

import java.util.ArrayList;
import java.util.Arrays;

public class Region {
	// @ToDo inputPortsSet and outputPortsSet could actually be Sets. This would ease many operations on them:
	// @ToDo isSubset, getOpMerged (intersection), getIpMerged (union) and so on.
	private String inputPortsSet;
	private String outputPortsSet;
	private Range box;
	private ArrayList<String> destinations = new ArrayList<>();

	public Region(String ip, ArrayList<String> destinations, String op) {
		this.destinations = destinations;
		this.inputPortsSet = ip;
		this.outputPortsSet = op;
		this.updateBox();
	}

	private void updateBox() {
		int xMin = Integer.MAX_VALUE, yMin = Integer.MAX_VALUE; 
		int xMax = 0, yMax = 0;
		for (String s : this.destinations) {
			String[] xy = s.split("\\.");
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

	ArrayList<String> destinations() {
		return destinations;
	}

	ArrayList<String> destinationsIn(Range box) {
		ArrayList<String> result = new ArrayList<>();
		for (int x = box.min(0); x <= box.max(0); x++)
			for (int y = box.min(1); y <= box.max(1); y++)
				if (this.destinations.contains(x + "." + y))
					result.add(x + "." + y);
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

	ArrayList<String> outsiders() {
		ArrayList<String> result = new ArrayList<>();
		for (int x = this.box().min(0); x <= this.box().max(0); x++) {
			for (int y = this.box().min(1); y <= this.box().max(1); y++) {
				String name = x + "." + y;
				if (!this.destinations().contains(name)) {
					result.add(name);
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

package rbr;

import util.Range;

import java.util.ArrayList;
import java.util.Arrays;

public class Region implements Comparable<Region> {
	private String ip;
	private String op;
	private Range box;
	private float size;
	private ArrayList<String> dst = new ArrayList<>();

	public Region(String ip, ArrayList<String> dsts, String op) {
		this.dst = dsts;
		this.ip = ip;
		this.op = op;
		this.setextrems();
		this.setSize();
	}

	public void setextrems() {
		int xMin = Integer.MAX_VALUE, yMin = Integer.MAX_VALUE; 
		int xMax = 0, yMax = 0;

		for (String s : this.dst) {
			String[] xy = s.split("\\.");
			int x = Integer.valueOf(xy[0]);
			int y = Integer.valueOf(xy[1]);
			
			xMin = (xMin < x) ? xMin : x;
			yMin = (yMin < y) ? yMin : y;
			xMax = (xMax > x) ? xMax : x;
			yMax = (yMax > y) ? yMax : y;
		}
		this.box = Range.TwoDimensionalRange(xMin, xMax, yMin, yMax);
	}

	public void setSize() {
		int Xmin = box.min(0);
		int Ymin = box.min(1);
		int Xmax = box.max(0);
		int Ymax = box.max(1);

		this.size = ((Xmax - Xmin) + 1) * ((Ymax - Ymin) + 1);
	}

	public String getIp() {
		return ip;
	}

	public String getOp() {
		return op;
	}

	public ArrayList<String> getDst() {
		return dst;
	}

	public ArrayList<String> destinationsIn(Range box) {
		ArrayList<String> result = new ArrayList<String>();
		for (int x = box.min(0); x <= box.max(0); x++)
			for (int y = box.min(1); y <= box.max(1); y++)
				if (this.dst.contains(x + "." + y))
					result.add(x + "." + y);
		return result;
	}

	public Range box() {
		return box;
	}

	@Override
	public int compareTo(Region otherRegion) {
		if (this.size < otherRegion.size) {
			return -1;
		}
		if (this.size > otherRegion.size) {
			return 1;
		}
		return 0;
	}

	public String toString() {
		String out = this.box + " " + this.ip + " "
				+ this.op;
		return out;
	}

	public boolean contains(String router) {
		String[] xy = router.split("\\.");
		int x = Integer.parseInt(xy[0]);
		int y = Integer.parseInt(xy[1]);

		int minX = box().min(0);
		int minY = box().min(1);
		int maxX = box().max(0);
		int maxY = box().max(1);
		
		if (minX <= x && x <= maxX && minY <= y && y <= maxY)
			return true;
		return false;
	}

	Region merge(Region that) {
		String op = getOpMerged(this, that);
		String ip = getIpMerged(this, that);
		Region reg = new Region(ip, this.getDst(), op);
		reg.box = mergedBox(this, that);
		reg.getDst().addAll(that.getDst());
		reg.setSize();
		return reg;
	}

	private static Range mergedBox(Region tic, Region tac) {
		Range thisBox = tic.box();
		Range thatBox = tac.box();
		return Range.TwoDimensionalRange(
				Math.min(thisBox.min(0), thatBox.min(0)),
				Math.max(thisBox.max(0), thatBox.max(0)),
				Math.min(thisBox.min(1), thatBox.min(1)),
				Math.max(thisBox.max(1), thatBox.max(1))
		);
	}

	// return the Output ports after merge
	private static String getOpMerged(Region r1, Region r2) {
		String op;

		if (r1.getOp().contains(r2.getOp())) {
			op = r2.getOp();
		} else {
			op = r1.getOp();
		}

		return op;
	}

	// return the Input ports after merge
	private static String getIpMerged(Region r1, Region r2) {
		String ip = new String(r2.getIp());

		for (int i = 0; i < r1.getIp().length(); i++) {
			if (!ip.contains(r1.getIp().substring(i, i + 1)))
				ip += r1.getIp().substring(i, i + 1);
		}
		return ip;
	}

	ArrayList<String> outsiders() {
		ArrayList<String> result = new ArrayList<>();
		for (int x = this.box().min(0); x <= this.box().max(0); x++) {
			for (int y = this.box().min(1); y <= this.box().max(1); y++) {
				String name = x + "." + y;
				if (!this.getDst().contains(name)) {
					result.add(name);
				}
			}
		}
		return result;
	}

	boolean canBeMergedWith(Region that) {
		return (this.isNeighborOf(that) && this.formBoxWith(that) && opIsSubset(this, that));
	}

	private boolean isNeighborOf(Region that) {
		boolean areNeighbours = false;

		int Xmax1 = this.box().max(0);
		int Xmax2 = that.box().max(0);
		int Ymax1 = this.box().max(1);
		int Ymax2 = that.box().max(1);

		int Xmin1 = this.box().min(0);
		int Xmin2 = that.box().min(0);
		int Ymin1 = this.box().min(1);
		int Ymin2 = that.box().min(1);

		if (Xmax1 > Xmax2) {
			if (Xmin1 == Xmax2 + 1)
				areNeighbours = true;
		}

		if (Xmax1 < Xmax2) {
			if (Xmin2 == Xmax1 + 1)
				areNeighbours = true;
		}

		if (Ymax1 > Ymax2) {
			if (Ymax2 == Ymin1 - 1)
				areNeighbours = true;
		}

		if (Ymax1 < Ymax2) {
			if (Ymax1 == Ymin2 - 1)
				areNeighbours = true;
		}
		return areNeighbours;
	}

	private boolean formBoxWith(Region that) {
		return ((this.box().max(0) == that.box().max(0)
				&& 	this.box().min(0) == that.box().min(0))
				|| (this.box().max(1) == that.box().max(1)
				&& this.box().min(1) == that.box().min(1)));
	}

	private static boolean opIsSubset(Region r1, Region r2) {
		String r1Op = sortStrAlf(r1.getOp());
		String r2Op = sortStrAlf(r2.getOp());
		return (r1Op.contains(r2Op) || r2Op.contains(r1Op));
	}

	private static String sortStrAlf(String input) {
		char[] ip1 = input.toCharArray();
		Arrays.sort(ip1);
		return String.valueOf(ip1);
	}

}

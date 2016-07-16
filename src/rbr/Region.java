package rbr;

import util.Range;

import java.util.ArrayList;

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

	public void setBox(Range mergedBox) {
		this.box = mergedBox;
	}
}

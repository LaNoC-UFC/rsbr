package sbr;

import java.util.*;
import util.*;

public class BidimensionalSBRPolicy implements SBRPolicy {

	private final static char[] RoundRobin = { 'N', 'E', 'S', 'W' };
	private static int RRIndex[];
	private ArrayList<Edge> edge;

	public BidimensionalSBRPolicy(Graph graph){
		edge = graph.getEdges();
		RRIndex = new int[2];
		RRIndex[0] = -1;
		RRIndex[1] = -1;
	}

	public BidimensionalSBRPolicy() {
		RRIndex = new int[2];
		RRIndex[0] = -1;
		RRIndex[1] = -1;
	}

	public Edge getNextLink(ArrayList<Edge> links) {
		Edge got = null;
		int index;
		if (RRIndex[0] == -1) {
			if (RRIndex[1] == -1) { // first choice of this computation
				index = 0;
			} else { // second choice
				index = (RRIndex[1] + 1) % 4;
			}
		} else { // others choices
			index = (RRIndex[0] + 2) % 4;
			if ((index + RRIndex[1]) % 2 == 0) {
				index = (index + 1) % 4;
			}
		}
		while (true) {
			for (Edge ln : links) {
				if (ln.color() == RoundRobin[index]) {
					got = ln;
					break;
					}
			}
			if (got != null){
				break;
				}
			else {
				if (RRIndex[1] == ((RRIndex[0] + 1) % 4))
					index = (index + 3) % 4;
				else
					index = (index + 1) % 4;
			}
		}
		// updates the last turn
		if (index != RRIndex[1]) {
			RRIndex[0] = RRIndex[1];
			RRIndex[1] = index;
		}
		return got;
	}

	public void resetRRIndex() {
		RRIndex[0] = -1;
		RRIndex[1] = -1;
	}
}

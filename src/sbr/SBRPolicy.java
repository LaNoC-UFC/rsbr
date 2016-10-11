package sbr;

import java.util.*;
import util.Edge;

public interface SBRPolicy {
	 public  Edge getNextLink(ArrayList<Edge> links);
	 public void resetRRIndex();
}

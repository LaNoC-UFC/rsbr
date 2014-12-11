package rbr;

import java.util.ArrayList;
import java.util.Iterator;

public class Path implements Iterable<Router>{
	private ArrayList<Router> itself;
	
	public Path() {
		itself = new ArrayList<Router>();
	}
	
	public int size() {
		return itself.size();
	}
	
	public Router dst() {
		assert itself.size() != 0;
		return itself.get(itself.size()-1);
	}

	public Router src() {
		assert itself.size() != 0;
		return itself.get(0);
	}
	
	public void add(Router r) {
		itself.add(r);
	}
	
	public void remove(Router r) {
		itself.remove(r);
	}

	public void remove(int index) {
		itself.remove(index);
	}

	public Iterator<Router> iterator() {
		return itself.iterator();
	}
	
}

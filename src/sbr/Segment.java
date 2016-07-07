package sbr;

import util.*;
import java.util.ArrayList;

public class Segment {
	private ArrayList<Edge> links;
	private ArrayList<Vertice> switches;

	public Segment() {
		links = new ArrayList<>();
		switches = new ArrayList<>();
	}

	public boolean isStarting() {
		// Checa se o destino do ultimo link eh o primeiro switch

		// TESTE PROVISORIO, SERA APAGADO
		if (links.size() == 1)
			return false;

		if (links.get(links.size() - 1).destination().getNome()
				.equals(switches.get(0).getNome()))
			return true;

		return false;
	}

	public boolean isUnitary() {

		return (links.size() == 1 && switches.size() == 0);
	}

	public boolean isRegular() {

		return (!this.isUnitary() && !this.isStarting());
	}

	public void add(Edge ln) {
		links.add(ln);
		// ln.setSegment(this);
	}

	public void add(Vertice sw) {
		switches.add(sw);
		sw.setSegment(this);
	}

	public void remove(Edge ln) {
		// ln.setSegment(null);
		links.remove(ln);
	}

	public void remove(Vertice sw) {
		sw.setSegment(null);
		/* @RM */
		// Remove the last occurrence instead of the first
		if (switches.lastIndexOf(sw) != -1)
			switches.remove(switches.lastIndexOf(sw));
		/* @RM */
		// switches.remove(sw);

	}

	public String toString() {
		String r = "";
		int sw = 0, ln = 0;
		while (sw < switches.size()) {
			r += (switches.get(sw++).getNome() + " ");
		}
		r += '\n';
		while (ln < links.size()) {
			r += (links.get(ln).source().getNome() + " <=> "
					+ links.get(ln).destination().getNome() + " ");
			ln++;
		}
		return r;
	}

	public ArrayList<Vertice> getSwitchs() {
		return this.switches;
	}

	public ArrayList<Edge> getLinks() {
		return this.links;
	}

}

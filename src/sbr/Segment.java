package sbr;

import util.*;
import java.util.ArrayList;

public class Segment {
    private ArrayList<Edge> links;
    private ArrayList<Vertex> switches;

    public Segment() {
        links = new ArrayList<>();
        switches = new ArrayList<>();
    }

    public Segment(Segment that, Edge toAppend) {
        this(that);
        this.add(toAppend);
    }

    public Segment(Segment that, Vertex toAppend) {
        this(that);
        this.add(toAppend);
    }

    public Segment(Segment that) {
        this.links = new ArrayList<>(that.links);
        this.switches = new ArrayList<>(that.switches);
    }

    public boolean isStarting() {
        // Checa se o destino do ultimo link eh o primeiro switch

        // TESTE PROVISORIO, SERA APAGADO
        if (links.size() == 1)
            return false;

        if (links.get(links.size() - 1).destination().equals(switches.get(0)))
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

    public void add(Vertex sw) {
        switches.add(sw);
    }

    public void remove(Edge ln) {
        // ln.setSegment(null);
        links.remove(ln);
    }

    public void remove(Vertex sw) {
        /* @RM */
        // Remove the last occurrence instead of the first
        if (switches.lastIndexOf(sw) != -1)
            switches.remove(switches.lastIndexOf(sw));
        /* @RM */
        // switches.remove(sw);

    }
    @Override
    public String toString() {
        String r = "";
        int sw = 0, ln = 0;
        while (sw < switches.size()) {
            r += (switches.get(sw++).name() + " ");
        }
        r += '\n';
        while (ln < links.size()) {
            r += (links.get(ln).source().name() + " <=> "
                    + links.get(ln).destination().name() + " ");
            ln++;
        }
        return r;
    }

    public ArrayList<Vertex> getSwitchs() {
        return this.switches;
    }

    public ArrayList<Edge> getLinks() {
        return this.links;
    }

}

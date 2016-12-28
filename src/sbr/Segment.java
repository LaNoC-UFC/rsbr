package sbr;

import util.*;
import java.util.*;

public class Segment implements ISegment {
    private List<Edge> links;
    private List<Vertex> switches;

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
        if (links.size() <= 1)
            return false;

        if (links.get(links.size() - 1).destination().equals(switches.get(0)))
            return true;

        return false;
    }

    public boolean isUnitary() {

        return (links.size() == 1 && switches.size() == 0);
    }

    public boolean isRegular() {

        return (!this.isUnitary() && !this.isStarting() && links.size() > 0);
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

    @Override
    public List<Vertex> vertices() {
        return this.switches;
    }

    @Override
    public List<Edge> edges() {
        return this.links;
    }

    @Override
    public void accept(SegmentVisitor visitor) {
        if (this.isUnitary()) {
            visitor.visitUnitarySegment(this);
        }
        else if (this.isStarting()) {
            visitor.visitStartSegment(this);
        }
        else if (this.isRegular()) {
            visitor.visitRegularSegment(this);
        }
    }
}

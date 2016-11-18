package rbr;

import java.util.*;
import util.*;

public final class Region {
    private final Set<Character> inputPorts;
    private final Set<Character> outputPorts;
    private final Range box;
    private final Set<Vertex> destinations;

    public Region(Set<Character> ip, Set<Vertex> destinations, Set<Character> op) {
        this.destinations = new HashSet<>(destinations);
        this.inputPorts = new HashSet<>(ip);
        this.outputPorts = new HashSet<>(op);
        this.box = TopologyKnowledge.box(destinations);
    }

    Set<Character> inputPorts() {
        return new HashSet<>(inputPorts);
    }

    Set<Character> outputPorts() {
        return new HashSet<>(outputPorts);
    }

    Set<Vertex> destinations() {
        return new HashSet<>(destinations);
    }

    Range box() {
        return box;
    }
    @Override
    public String toString() {
        return this.box + " " + this.inputPorts + " " + this.outputPorts;
    }

    Region merge(Region that) {
        Set<Character> op = this.outputPorts();
        op.retainAll(that.outputPorts());
        Set<Character> ip = that.inputPorts();
        ip.addAll(this.inputPorts());
        Set<Vertex> destinations = this.destinations();
        destinations.addAll(that.destinations());
        return new Region(ip, destinations, op);
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
        boolean anyBoxIsEmpty = this.box().equals(Range.EMPTY) || that.box().equals(Range.EMPTY);
        boolean boxesCanBeMerged = this.box().isContiguous(that.box()) || this.box().equals(that.box()) || anyBoxIsEmpty;
        boolean outputPortsCanBeMerged = OutputPortIsSubSet(this.outputPorts(), that.outputPorts());
        return boxesCanBeMerged && outputPortsCanBeMerged;
    }

    private boolean OutputPortIsSubSet(Set<Character> outputPort1, Set<Character> outputPort2) {
        return (outputPort1.containsAll(outputPort2) || outputPort2.containsAll(outputPort1));
    }
}

package rbr;

import util.Vertex;
import java.util.*;

final class RoutingOption {
    private final Set<Character> ip;
    private final Set<Character> op;
    private final Vertex dst;

    RoutingOption(Set<Character> ip, Vertex dst, Set<Character> op) {
        this.dst = dst;
        this.ip = new HashSet<>(ip);
        this.op = new HashSet<>(op);
    }

    RoutingOption(Character ip, Vertex dst, Character op) {
        this.dst = dst;
        this.ip = new HashSet<>();
        this.op = new HashSet<>();
        this.ip.add(ip);
        this.op.add(op);
    }

    public Set<Character> getIp() {
        return new HashSet<>(ip);
    }

    Vertex destination() {
        return dst;
    }

    public Set<Character> getOp() {
        return new HashSet<>(op);
    }

    @Override
    public boolean equals(Object _that) {
        if(this == _that)
            return true;
        if(this.getClass() != _that.getClass())
            return false;
        RoutingOption that = (RoutingOption) _that;
        if(!this.getIp().equals(that.getIp()))
            return false;
        if(!this.getOp().equals(that.getOp()))
            return false;
        if(!this.destination().equals(that.destination()))
            return false;
        return true;
    }

    @Override
    public int hashCode(){
        int result = ip.hashCode();
        result = 37 * result + op.hashCode();
        result = 37 * result + dst.hashCode();
        return result;
    }
}

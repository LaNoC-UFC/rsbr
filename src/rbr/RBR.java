package rbr;

import java.util.*;
import util.*;

public class RBR {
    private Graph graph;
    private HashMap<Vertex, Set<RoutingOption>> routingPathForVertex;
    private HashMap<Vertex, List<Region>> regionsForVertex;

    public RBR(Graph g) {
        graph = g;
        routingPathForVertex = new HashMap<>();
        regionsForVertex = new HashMap<>();
    }

    public HashMap<Vertex, List<Region>> regions() {
        return this.regionsForVertex;
    }

    // Pack routing options if they have the same input port and the same
    // destination
    private void packOutputPort(Vertex atual) {
        Set<RoutingOption> actRP = routingPathForVertex.get(atual);
        routingPathForVertex.put(atual, new HashSet<>());
        for (RoutingOption a : actRP) {
            Set<Character> op = a.getOp();
            Set<Character> ip = a.getIp();
            Vertex dst = a.destination();

            for (RoutingOption b : actRP) {
                if (ip.equals(b.getIp()) && dst.equals(b.destination())) {
                        op.addAll(b.getOp());
                }
            }
            routingPathForVertex.get(atual).add(new RoutingOption(ip, dst, op));
        }
    }

    // Pack routing options if they have the same output port and the same
    // destination
    public void packInputPort(Vertex atual) {
        Set<RoutingOption> actRP = routingPathForVertex.get(atual);
        routingPathForVertex.put(atual, new HashSet<>());
        for (RoutingOption a : actRP) {
            Set<Character> op = a.getOp();
            Set<Character> ip = a.getIp();
            Vertex dst = a.destination();

            for (RoutingOption b : actRP) {
                if (op.equals(b.getOp()) && dst.equals(b.destination())) {
                        ip.addAll(b.getIp());
                }
            }
            routingPathForVertex.get(atual).add(new RoutingOption(ip, dst, op));
        }
    }

    public void addRoutingOptions(ArrayList<ArrayList<Path>> paths) {

        for(Vertex v : graph.getVertices())
            routingPathForVertex.put(v, new HashSet<>());

        for(ArrayList<Path> alp : paths) {
            for (Path path : alp) {
                for (Vertex sw : path) {
                    if (path.indexOf(sw) != path.size() - 1) {
                        Character op = graph.adjunct(sw, path.get(path.indexOf(sw) + 1)).color();
                        Character ip;

                        if(path.indexOf(sw) == 0) {
                            ip = 'I';
                        }
                        else {
                            ip = graph.adjunct(sw, path.get(path.indexOf(sw) - 1) ).color();
                        }
                        routingPathForVertex.get(sw).add(new RoutingOption(ip, path.dst(), op));
                    }
                }
            }
        }
        for (Vertex atual : graph.getVertices()) {
            packOutputPort(atual);
            // packInputPort(atual);
        }
    }
    
    private static List<Set> allCombinationsOf(Set objects) {
        List<Set> result = new ArrayList<>();
        Object[] arrayObjects = objects.toArray();
        for (int m = 1; m != 1 << arrayObjects.length; m++) {
            Set<Object> aCombination = new HashSet<>();
            for (int i = 0; i != arrayObjects.length; i++) {
                if ((m & (1 << i)) != 0) {
                    aCombination.add(arrayObjects[i]);
                }
            }
            result.add(aCombination);
        }
        return result;
    }

    // Compute the regions
    public void regionsComputation() {
        Set<Character> opt = new HashSet<>();
        opt.add('E');
        opt.add('N');
        opt.add('S');
        opt.add('W');
        List<Set> opComb = allCombinationsOf(opt);
        for (Vertex sw : graph.getVertices()) {
            regionsForVertex.put(sw, new ArrayList<>());
            for (Set<Character> op : opComb) {
                Set<Character> ip = new HashSet<>();
                Set<Vertex> destinations = new HashSet<>();
                for (RoutingOption rp : routingPathForVertex.get(sw)) {
                    if (rp.getOp().equals(op)) {
                        destinations.add(rp.destination());
                        ip.addAll(rp.getIp());
                    }
                }
                if (destinations.size() != 0) {
                    regionsForVertex.get(sw).add(new Region(ip, destinations, op));
                }
            }
        }
        for(Vertex v : graph.getVertices())
            adjustRegions(v);
        assert reachabilityIsOk();
    }

    void adjustRegions(Vertex sw) {
        List<Region> newRegions = new ArrayList<>();
        for (Region currentRegion : regionsForVertex.get(sw)) {
            newRegions.addAll(adjustedRegionsFrom(currentRegion));
        }
        regionsForVertex.put(sw, newRegions);
    }

    ArrayList<Region> adjustedRegionsFrom(Region r) {
        ArrayList<Region> adjustedRegions = new ArrayList<>();
        Set<Vertex> outsiders = r.outsiders();
        if(outsiders.isEmpty()){
            adjustedRegions.add(r);
        } else {
            adjustedRegions.addAll(makeRegions(r.destinations(), r.inputPorts(), r.outputPorts()));
        }
        return adjustedRegions;
    }

    private static List<Region> splitRegionExcludingOutsiders(Region region, Range outsidersBox) {
        Set<Set<Vertex>> dsts = new HashSet<>();
        // up
        Range upBox = Range.TwoDimensionalRange(region.box().min(0), region.box().max(0), region.box().min(1), outsidersBox.min(1) - 1);
        Set<Vertex> upDestinations = region.destinationsIn(upBox);
        dsts.add(upDestinations);
        // down
        Range downBox = Range.TwoDimensionalRange(region.box().min(0), region.box().max(0), outsidersBox.max(1) + 1, region.box().max(1));
        Set<Vertex> downDestinations = region.destinationsIn(downBox);
        dsts.add(downDestinations);
        // left
        Range leftBox = Range.TwoDimensionalRange(outsidersBox.max(0) + 1, region.box().max(0), region.box().min(1), region.box().max(1));
        Set<Vertex> leftDestinations = region.destinationsIn(leftBox);
        dsts.add(leftDestinations);
        // right
        Range rightBox = Range.TwoDimensionalRange(region.box().min(0), outsidersBox.min(0) - 1, region.box().min(1), region.box().max(1));
        Set<Vertex> rightDestinations = region.destinationsIn(rightBox);
        dsts.add(rightDestinations);

        List<Region> result = new ArrayList<>();
        for (Set<Vertex> dst : dsts) {
            if(dst.isEmpty())
                continue;
            Region r = new Region(region.inputPorts(), dst, region.outputPorts());
            result.add(r);
        }
        return result;
    }

    // Make regions only with correct destinations
    private List<Region> makeRegions(Set<Vertex> dsts, Set<Character> ip, Set<Character> op) {
        List<Region> result = new ArrayList<>();
        Range box = TopologyKnowledge.box(dsts);

        while (!dsts.isEmpty()) {
            int Lmin = box.min(1), Cmax = box.max(0);
            int Cmin = box.min(0), Lmax = box.max(1);

            boolean first = true;
            for (int line = Lmax; line >= Lmin; line--) {
                for (int col = Cmin; col <= Cmax; col++) {
                    if (first) {
                        if (dsts.contains(graph.vertex(col + "." + line))) {
                            Cmin = col;
                            Lmax = line;
                            first = false;
                        }
                    } else {
                        if (!dsts.contains(graph.vertex(col + "." + line))) { // if stranger
                            if (line == Lmax) { // first line
                                Cmax = col - 1;
                            } else if (col > (Cmax - Cmin) / 2 && col > Cmin) {
                                Cmax = col - 1;
                            } else {
                                Lmin = ++line;
                            }

                            if (line == Lmin) { // last line
                                Region rg = makeRegion(Cmin, Lmin, Cmax, Lmax,ip, op);
                                dsts.removeAll(rg.destinations());
                                result.add(rg);
                            }
                            break;
                        }
                    }
                    if (line == Lmin && col == Cmax) { // last line
                        Region rg = makeRegion(Cmin, Lmin, Cmax, Lmax, ip, op);
                        dsts.removeAll(rg.destinations());
                        result.add(rg);
                    }
                }
            }
        }
        return result;
    }

    private Region makeRegion(int xmin, int ymin, int xmax, int ymax, Set<Character> ip, Set<Character> op) {
        Set<Vertex> dst = new HashSet<>();
        for (int x = xmin; x <= xmax; x++) {
            for (int y = ymin; y <= ymax; y++) {
                dst.add(graph.vertex(x + "." + y));
            }
        }
        return (new Region(ip, dst, op));
    }

    public boolean reachabilityIsOk() {
        for (Vertex src : graph.getVertices()) {
            if(!reachesAllDestinations(src)) {
                return false;
            }
        }
        return true;
    }

    public void merge() {
        for (Vertex vertex : graph.getVertices()) {
            merge(vertex);
        }
    }

    private void merge(Vertex router) {
        List<Region> bkpListRegion = null;
        boolean wasPossible = true;
        while (reachesAllDestinations(router) && wasPossible) {
            bkpListRegion = new ArrayList<>(regionsForVertex.get(router));
            wasPossible = mergeUnitary(regionsForVertex.get(router));
        }
        if (bkpListRegion != null) {
            regionsForVertex.put(router, bkpListRegion);
        }
    }

    static boolean mergeUnitary(List<Region> regions) {
        for (int a = 0; a < regions.size(); a++) {
            Region ra = regions.get(a);
            for (int b = a + 1; b < regions.size(); b++) {
                Region rb = regions.get(b);
                if (ra.canBeMergedWith(rb)) {
                    Region reg = ra.merge(rb);
                    regions.add(reg);
                    regions.remove(ra);
                    regions.remove(rb);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean reachesAllDestinations(Vertex orig) {
        for (Vertex dst : graph.getVertices()) {
            if (!orig.equals(dst) && !reaches(orig, dst)) {
                return false;
            }
        }
        return true;
    }

    private boolean reaches(Vertex src, Vertex dest) {
        return reaches(src, dest, 'I');
    }

    private boolean reaches(Vertex src, Vertex dest, Character ipColor) {
        if (dest == src) {
            return true;
        }
        Character opColor = getOpColor(src, dest, ipColor);
        if (opColor == null) {
            return false;
        }
        return reaches(graph.adjunctOf(src, opColor).destination(), dest,
                TopologyKnowledge.getInvColor(graph.adjunctOf(src, opColor).color()));
    }

    private Character getOpColor(Vertex src, Vertex dest, Character ipColor) {
        int destX = Integer.valueOf(dest.name().split("\\.")[0]);
        int destY = Integer.valueOf(dest.name().split("\\.")[1]);
        for (rbr.Region reg : regionsForVertex.get(src)) {
            if (reg.box().contains(destX, destY) && reg.inputPorts().contains(ipColor)) {
                return (Character)reg.outputPorts().toArray()[0];
            }
        }
        System.err.println("ERROR : There isn't Op on " + src.name() + "("  + ipColor + ") going to " + dest.name());
        return null;
    }
}

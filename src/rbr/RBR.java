package rbr;

import util.*;

import java.util.*;

public class RBR {
    private Graph graph;
    private HashMap<Vertex, Set<RoutingOption>> routingPathForVertex;
    private HashMap<Vertex, ArrayList<Region>> regionsForVertex;

    public RBR(Graph g) {
        graph = g;
        routingPathForVertex = new HashMap<>();
        regionsForVertex = new HashMap<>();
    }

    public HashMap<Vertex, ArrayList<Region>> regions() {
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

    private void adjustRegions(Vertex sw) {
        ArrayList<Region> newRegions = new ArrayList<>();
        ArrayList<Region> regionsToBeRemoved = new ArrayList<>();
        for (Region currentRegion : regionsForVertex.get(sw)) {
            Set<Vertex> outsiders = currentRegion.outsiders();
            if(outsiders.isEmpty())
                continue;
            Range outsidersBox = TopologyKnowledge.box(outsiders);
            Set<Vertex> trulyDestinationsInOutsidersRange = currentRegion.destinationsIn(outsidersBox);

            regionsToBeRemoved.add(currentRegion);
            ArrayList<Region> regionsToAdd = splitRegionExcludingOutsiders(currentRegion, outsidersBox);
            newRegions.addAll(regionsToAdd);
            // use others routers to make others regions
            newRegions.addAll(makeRegions(trulyDestinationsInOutsidersRange, currentRegion.inputPorts(), currentRegion.outputPorts()));
        }
        regionsForVertex.get(sw).removeAll(regionsToBeRemoved);
        regionsForVertex.get(sw).addAll(newRegions);
    }

    private static ArrayList<Region> splitRegionExcludingOutsiders(Region region, Range outsidersBox) {
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

        ArrayList<Region> result = new ArrayList<>();
        for (Set<Vertex> dst : dsts) {
            if(dst.isEmpty())
                continue;
            Region r = new Region(region.inputPorts(), dst, region.outputPorts());
            result.add(r);
        }
        return result;
    }

    // Make regions only with correct destinations
    private ArrayList<Region> makeRegions(Set<Vertex> dsts, Set<Character> ip, Set<Character> op) {
        ArrayList<Region> result = new ArrayList<>();
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
        for (Vertex dest : graph.getVertices()) {
            if(reachability(dest) < 1) {
                return false;
            }
        }
        return true;
    }

    // Calculates reachability
    private double reachability(Vertex orig) {
        double reaches = 0, total = graph.getVertices().size() - 1;
        for (Vertex dest : graph.getVertices()) {
            if (orig != dest) {
                if (reaches(orig, dest)) {
                    reaches++;
                }
            }
        }
        return (reaches / total);
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

    public void merge() {
        for (Vertex vertex : graph.getVertices())
            merge(vertex);
    }

    // Merge the regions of a router
    private void merge(Vertex router) {
        ArrayList<Region> bkpListRegion = null;
        boolean wasPossible = true;

        while (reachability(router) == 1 && wasPossible) {
            bkpListRegion = new ArrayList<>(regionsForVertex.get(router));
            wasPossible = mergeUnitary(router);
        }
        if (bkpListRegion != null) {
            regionsForVertex.put(router, bkpListRegion);
        }

    }

    /*
     * Tries to make one (and only one) merge and returns true in case of
     * success
     */
    private boolean mergeUnitary(Vertex router) {
        for (int a = 0; a < regionsForVertex.get(router).size(); a++) {
            Region ra = regionsForVertex.get(router).get(a);
            for (int b = a + 1; b < regionsForVertex.get(router).size(); b++) {
                Region rb = regionsForVertex.get(router).get(b);
                if (ra.canBeMergedWith(rb)) {
                    Region reg = ra.merge(rb);
                    regionsForVertex.get(router).add(reg);
                    regionsForVertex.get(router).remove(ra);
                    regionsForVertex.get(router).remove(rb);
                    return true;
                }
            }
        }
        return false;
    }
}

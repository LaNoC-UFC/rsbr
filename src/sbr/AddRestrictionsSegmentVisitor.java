package sbr;

import util.*;

class AddRestrictionsSegmentVisitor implements SegmentVisitor {

    private GraphRestrictions restrictions;
    private Graph graph;

    AddRestrictionsSegmentVisitor(GraphRestrictions restrictions, Graph graph) {
        this.restrictions = restrictions;
        this.graph = graph;
    }

    @Override
    public void visitUnitarySegment(ISegment unitarySegment) {
        Vertex Starting = unitarySegment.edges().get(0).source();
        Vertex Ending = unitarySegment.edges().get(0).destination();
        for (Edge link : graph.adjunctsOf(Starting)) {
            restrictions.addRestriction(Starting, link.color(), graph.adjunct(Starting, Ending).color());
        }
        for (Edge link : graph.adjunctsOf(Ending)) {
            restrictions.addRestriction(Ending, link.color(), graph.adjunct(Ending, Starting).color());
        }
    }

    @Override
    public void visitStartSegment(ISegment startSegment) {
        Vertex restrict = startSegment.vertices().get(1);
        restrictions.addRestriction(restrict, TopologyKnowledge.getInvColor(startSegment.edges().get(0).color()), startSegment.edges().get(1).color());
        restrictions.addRestriction(restrict, startSegment.edges().get(1).color(), TopologyKnowledge.getInvColor(startSegment.edges().get(0).color()));
    }

    @Override
    public void visitRegularSegment(ISegment regularSegment) {
        if (regularSegment.vertices().size() == 1) {
            Vertex sw = regularSegment.vertices().get(0);
            restrictions.addRestriction(sw, TopologyKnowledge.getInvColor(regularSegment.edges().get(0).color()), regularSegment.edges().get(1).color());
            restrictions.addRestriction(sw, regularSegment.edges().get(1).color(), TopologyKnowledge.getInvColor(regularSegment.edges().get(0).color()));
        } else {
            Vertex sw = regularSegment.vertices().get(1);
            restrictions.addRestriction(sw, TopologyKnowledge.getInvColor(regularSegment.edges().get(1).color()), regularSegment.edges().get(2).color());
            restrictions.addRestriction(sw, regularSegment.edges().get(2).color(), TopologyKnowledge.getInvColor(regularSegment.edges().get(1).color()));
        }
    }
}

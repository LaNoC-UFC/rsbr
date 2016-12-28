package sbr;

interface SegmentVisitor {
    void visitUnitarySegment(ISegment unitarySegment);

    void visitStartSegment(ISegment startSegment);

    void visitRegularSegment(ISegment regularSegment);
}

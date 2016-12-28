package sbr;

import util.*;

import java.util.List;

interface ISegment {
    void accept(SegmentVisitor visitor);

    List<Vertex> vertices();

    List<Edge> edges();
}

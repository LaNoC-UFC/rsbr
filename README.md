rsbr
====

Segmentation plus Region-based routing algorithms

[Segment-Based Routing (SBR)](http://ieeexplore.ieee.org/document/1639341) is a
methodology to generate deadlock-free routing algorithms to Networks-on-Chip
(NoCs).
SBR requires as input a Graph that represents the NoC topology and it mainly
outputs the restrictions that guarantee deadlock-freedom.

[Region-based Routing (RBR)](http://ieeexplore.ieee.org/document/4209007) is
technique to decrease the ambiguity on routing tables (thus compressing them).
RBR requires as input a graph (topology) and a set of paths that constitutes
the routing algorithm. It outputs the regions to each vertex (router).

Between SBR and RBR, an additional step is performed to find the paths that
connect the vertices and respect the SBR restrictions.

This tool can have as output a VHDL file with the RBR-encoded routing tables to
be used on hardware simulation. 

It's also capable of calculate some metrics:
* Average Routing Distance (ARD),
* Network Link Weight (LW),
* Standard deviation of LW (STDLW),
* Maximum quantity of regions for a network,
* Number of unitary links,
* Number of subnets and bridges

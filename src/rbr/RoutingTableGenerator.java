package rbr;

import util.*;
import java.io.*;
import java.util.*;

public class RoutingTableGenerator {

    private Graph graph;
    private HashMap<Vertex, List<Region>> regionsForVertex;
    private int bitPerCoordinate = 0;
    private int maxOfRegions = 0;

    public RoutingTableGenerator(Graph graph, HashMap<Vertex, List<Region>> regionsForVertex) {
        this.graph = graph;
        this.regionsForVertex = regionsForVertex;
        int size = Math.max(graph.columns(), graph.rows());
        this.bitPerCoordinate = (int) Math.ceil(Math.log(size) / Math.log(2));
        this.maxOfRegions = maxOfRegions();
    }

    public void doRoutingTable(String ext) {
        String fileName = "Table_package_"+ext+".vhd";
        File routingTable = new File(fileName);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(routingTable));
            writeRoutingTable(bw);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeRoutingTable(BufferedWriter bw) throws IOException {
        writeRoutingTableHeader(bw);
        writeRoutingTableBody(bw);
        writeRoutingTableTail(bw);
    }

    private void writeRoutingTableHeader(BufferedWriter bw) throws IOException {
        bw.append("library IEEE;\n"
                + "use ieee.std_logic_1164.all;\n"
                + "use ieee.numeric_std.all;\n"
                + "use work.HermesPackage.all;\n\n"
                + "package TablePackage is\n\n"
                + "constant NREG : integer := "
                + maxOfRegions
                + ";\n"
                + "constant MEMORY_SIZE : integer := NREG;\n"
                + "constant NBITS : integer := "
                + bitPerCoordinate
                + ";\n"
                + "constant CELL_SIZE : integer := 2*NPORT+4*NBITS;\n\n"
                + "subtype cell is std_logic_vector(CELL_SIZE-1 downto 0);\n"
                + "subtype regAddr is std_logic_vector(2*NBITS-1 downto 0);\n"
                + "type memory is array (0 to MEMORY_SIZE-1) of cell;\n"
                + "type tables is array (0 to NROT-1) of memory;\n\n");
    }

    private void writeRoutingTableBody(BufferedWriter bw) throws IOException {
        bw.append("constant TAB: tables :=(");
        for (Vertex router : graph.getVertices()) {
            PrintRegions(router, bw);
            if (!isLastVertex(router))
                bw.append(",");
        }
        bw.append("\n);\n");
    }

    private void writeRoutingTableTail(BufferedWriter bw) throws IOException {
        bw.append("end TablePackage;\n"
                + "\n"
                + "package body TablePackage is\n"
                + "end TablePackage;\n");
    }

    private boolean isLastVertex(Vertex v) {
        return (graph.getVertices().indexOf(v) == graph.getVertices().size() - 1);
    }

    private int maxOfRegions() {
        int result = 0;
        for(Vertex v : graph.getVertices())
            result = Math.max(result, regionsForVertex.get(v).size());
        return result;
    }

    private void PrintRegions(Vertex router, BufferedWriter bw) throws IOException {
        bw.append("\n -- Router " + router.name() + "\n");
        bw.append("(");
        for (int regionIndex = 0; regionIndex < regionsForVertex.get(router).size(); regionIndex++) {
            Region currentRegion = regionsForVertex.get(router).get(regionIndex);
            int Xmin = currentRegion.box().min(0);
            int Ymin = currentRegion.box().min(1);
            int Xmax = currentRegion.box().max(0);
            int Ymax = currentRegion.box().max(1);

            String region = "(\""
                    + opToBinary(regionsForVertex.get(router).get(regionIndex).inputPorts())
                    + intToBinary(Xmin, bitPerCoordinate)
                    + intToBinary(Ymin, bitPerCoordinate)
                    + intToBinary(Xmax, bitPerCoordinate)
                    + intToBinary(Ymax, bitPerCoordinate)
                    + opToBinary(regionsForVertex.get(router).get(regionIndex).outputPorts())
                    + "\")";

            bw.append(region);
            if(regionIndex < maxOfRegions - 1)
                bw.append(",");
            bw.newLine();
        }
        paddingToMaxRegions(bw, router);

        bw.append(")");
    }

    private void paddingToMaxRegions(BufferedWriter bw, Vertex router) throws IOException{
        int numberOfCoordinates = 4;
        int numberOfInputPorts = 5;
        int numberOfOutputPorts = 5;
        int totalOfPorts = numberOfInputPorts + numberOfOutputPorts;
        String nullRegion = "(\"" + intToBinary(0, numberOfCoordinates * bitPerCoordinate + totalOfPorts) + "\")";
        int numberOfRegions = regionsForVertex.get(router).size();
        while (numberOfRegions < maxOfRegions) {
            numberOfRegions++;
            bw.append(nullRegion);
            if (numberOfRegions < maxOfRegions)
                bw.append(",");
            bw.newLine();
        }
    }

    private String intToBinary(int value, int numberOfDigits) {
        String out = Integer.toBinaryString(value);
        while (out.length() < numberOfDigits)
            out = "0" + out;
        return out;
    }

    private String opToBinary(Set<Character> ports) {
        char[] outOp = { '0', '0', '0', '0', '0' };

        for (char pt : ports) {
            switch (pt) {
                case 'E':
                    outOp[4] = '1';
                    break;
                case 'W':
                    outOp[3] = '1';
                    break;
                case 'N':
                    outOp[2] = '1';
                    break;
                case 'S':
                    outOp[1] = '1';
                    break;
                case 'I':
                    outOp[0] = '1';
                    break;
            }
        }
        return String.valueOf(outOp);
    }
}

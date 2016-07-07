package rbr;

import util.Graph;
import util.Vertice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoutingTableGenerator {

    private Graph graph;

    public RoutingTableGenerator(Graph graph) {
        this.graph = graph;
    }

    public void doRoutingTable(String ext) {
        String routingTableFile = "Table_package_"+ext+".vhd";

        File routingTable = new File(routingTableFile);
        int size = (graph.dimX() >= graph.dimY()) ? graph.dimX() : graph.dimY();
        int nBits = (int) Math.ceil(Math.log(size) / Math.log(2));
        int maxOfRegions = maxOfRegions();

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(routingTable));
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
                    + nBits
                    + ";\n"
                    + "constant CELL_SIZE : integer := 2*NPORT+4*NBITS;\n\n"
                    + "subtype cell is std_logic_vector(CELL_SIZE-1 downto 0);\n"
                    + "subtype regAddr is std_logic_vector(2*NBITS-1 downto 0);\n"
                    + "type memory is array (0 to MEMORY_SIZE-1) of cell;\n"
                    + "type tables is array (0 to NROT-1) of memory;\n\n"
                    + "constant TAB: tables :=(");

            for (Vertice router : graph.getVertices()) {
                PrintRegions(router, maxOfRegions, bw, nBits);
                if (graph.getVertices().indexOf(router) != graph.getVertices()
                        .size() - 1)
                    bw.append(",");
            }

            bw.append("\n);\nend TablePackage;\n\npackage body TablePackage is\n"
                    + "end TablePackage;\n");
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int maxOfRegions()
    {
        int result = 0;
        for(Vertice v : graph.getVertices()) {
            result = Math.max(result, v.getRegions().size());
        }
        return result;
    }

    private void PrintRegions(Vertice router, int maxRegion, BufferedWriter bw, int nBits) {
        try {
            bw.append("\n -- Router " + router.getNome() + "\n");
            bw.append("(");

            for (int a = 0; a < router.getRegions().size(); a++) {
                int Xmin = Integer.parseInt(router.getRegions().get(a).getDownLeft()
                        .split("\\.")[0]);
                int Ymin = Integer.parseInt(router.getRegions().get(a).getDownLeft()
                        .split("\\.")[1]);
                int Xmax = Integer.parseInt(router.getRegions().get(a).getUpRight()
                        .split("\\.")[0]);
                int Ymax = Integer.parseInt(router.getRegions().get(a).getUpRight()
                        .split("\\.")[1]);

                // Write on file
                String outLine = "(\""
                        + opToBinary(router.getRegions().get(a).getIp())
                        + IntToBitsString(Xmin, nBits)
                        + IntToBitsString(Ymin, nBits)
                        + IntToBitsString(Xmax, nBits)
                        + IntToBitsString(Ymax, nBits)
                        + opToBinary(router.getRegions().get(a).getOp()) + "\")";

                bw.append(outLine);

                if (a != router.getRegions().size() - 1
                        || (a == router.getRegions().size() - 1)
                        && (router.getRegions().size() < maxRegion)) {
                    bw.append(",");
                }
                bw.newLine();
            }

            // If less then Max Region
            if (router.getRegions().size() < maxRegion) {
                int a = router.getRegions().size();
                while (a < maxRegion) {
                    a++;
                    String outLine = "(\"" + IntToBitsString(0, 4 * nBits + 10) + "\")";
                    bw.append(outLine);

                    if (a < maxRegion)
                        bw.append(",");

                    bw.newLine();
                }
            }

            bw.append(")");
            // bw.flush();
            // bw.close();
        } catch (IOException ex) {
            Logger.getLogger(Vertice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String IntToBitsString(int a, int size) {
        String out = Integer.toBinaryString(a);
        while (out.length() < size)
            out = "0" + out;

        return out;
    }

    private String opToBinary(String ports) {
        char[] outOp = { '0', '0', '0', '0', '0' };
        char[] port = ports.toCharArray();

        for (char pt : port) {
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

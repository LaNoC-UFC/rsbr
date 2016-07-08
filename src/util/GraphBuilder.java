package util;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GraphBuilder {

    static public Graph generateGraph(String topology_file_path) {
        File topology = new File(topology_file_path);

        Graph result = new Graph();
        try {
            Scanner sc = new Scanner(new FileReader(topology));

            String[] lines = null, columns = null;
            if (sc.hasNextLine())
                lines = sc.nextLine().split("; ");
            if (sc.hasNextLine())
                columns = sc.nextLine().split("; ");

            int dimX = lines[0].split(" ").length + 1;
            int dimY = lines.length;

            for (int i = 0; i < dimX; i++) {
                for (int j = 0; j < dimY; j++) {
                    String vertex = i + "." + j;
                    result.addVertex(vertex);
                }
            }

            for (int i = 0; i < lines.length; i++) {
                String[] line = lines[i].split(" ");
                for (int j = 0; j < line.length; j++) {
                    if (line[j].charAt(0) == '0') // there is a link
                    {
                        Vertex starting = result.vertex(j + "."
                                + (columns.length - i));
                        Vertex ending = result.vertex((j + 1) + "."
                                + (columns.length - i));
                        result.addEdge(starting, ending, "E");
                        result.addEdge(ending, starting, "W");
                    }
                }
            }

            for (int i = 0; i < columns.length; i++) {
                String[] column = columns[i].split(" ");
                for (int j = 0; j < column.length; j++) {
                    if (column[j].charAt(0) == '0') // there is a link
                    {
                        Vertex starting = result.vertex(j + "."
                                + (columns.length - i));
                        Vertex ending = result.vertex(j + "."
                                + (columns.length - 1 - i));
                        result.addEdge(starting, ending, "S");
                        result.addEdge(ending, starting, "N");
                    }
                }
            }

            sc.close();

        } catch (Exception ex) {
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
        }
    return result;
    }
}

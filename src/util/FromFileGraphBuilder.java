package util;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FromFileGraphBuilder {

    static public Graph generateGraph(String topology_file_path) {
        File topology = new File(topology_file_path);

        Graph result = null;
        try {
            Scanner sc = new Scanner(new FileReader(topology));

            String[] lines = sc.nextLine().split("; ");
            String[] columns = sc.nextLine().split("; ");

            int dimX = lines[0].split(" ").length + 1;
            int dimY = lines.length;

            result = new Graph(dimX, dimY);
            addVertices(result);
            addHorizontalLinks(result, lines, columns);
            addVerticalLinks(result, columns);
            sc.close();

        } catch (Exception ex) {
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
        }
    return result;
    }

    static private void addVertices(Graph graph) {
        for (int i = 0; i < graph.dimX(); i++) {
            for (int j = 0; j < graph.dimY(); j++) {
                String vertex = i + "." + j;
                graph.addVertex(vertex);
            }
        }
    }

    static private void addHorizontalLinks(Graph graph, String[] lines, String[] columns) {
        for (int i = 0; i < lines.length; i++) {
            String[] line = lines[i].split(" ");
            for (int j = 0; j < line.length; j++) {
                if (!linkIsFaulty(line[j])) {
                    Vertex starting = graph.vertex(j + "." + (columns.length - i));
                    Vertex ending = graph.vertex((j + 1) + "." + (columns.length - i));
                    graph.addEdge(starting, ending, EdgeColor.colorFromTo(starting.name(), ending.name()));
                    graph.addEdge(ending, starting, EdgeColor.colorFromTo(ending.name(), starting.name()));
                }
            }
        }
    }

    static private void addVerticalLinks(Graph graph, String[] columns) {
        for (int i = 0; i < columns.length; i++) {
            String[] column = columns[i].split(" ");
            for (int j = 0; j < column.length; j++) {
                if (!linkIsFaulty(column[j])) {
                    Vertex starting = graph.vertex(j + "." + (columns.length - i));
                    Vertex ending = graph.vertex(j + "." + (columns.length - 1 - i));
                    graph.addEdge(starting, ending, EdgeColor.colorFromTo(starting.name(), ending.name()));
                    graph.addEdge(ending, starting, EdgeColor.colorFromTo(ending.name(), starting.name()));
                }
            }
        }
    }

    static private boolean linkIsFaulty(String string) {
        return (string.charAt(0) == '1');
    }
}

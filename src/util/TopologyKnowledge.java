package util;

import java.util.Set;

public class TopologyKnowledge {
    static Character[] ports = {'N', 'S', 'E', 'W'};

    public static Character getInvColor(Character color) {
        switch (color) {
            case 'E':
                return 'W';
            case 'W':
                return 'E';
            case 'N':
                return 'S';
            case 'S':
                return 'N';
            case 'I':
                return 'I';
            default:
                throw new RuntimeException("Wrong port color " + color);
        }
    }

    public static Character colorFromTo(String src, String dst) {
        int srcX = Integer.valueOf(src.split("\\.")[0]);
        int srcY = Integer.valueOf(src.split("\\.")[1]);
        int dstX = Integer.valueOf(dst.split("\\.")[0]);
        int dstY = Integer.valueOf(dst.split("\\.")[1]);
        int deltaX = srcX - dstX;
        int deltaY = srcY - dstY;
        assert Math.abs(deltaX) + Math.abs(deltaY) == 1 : src + " and " + dst + " are not neighbors";
        if (1 == deltaX) {
            return 'W';
        } else if (-1 == deltaX) {
            return 'E';
        } else if (1 == deltaY) {
            return 'S';
        } else if (-1 == deltaY) {
            return 'N';
        }
        return 'I';
    }

    public static Range box(Set<Vertex> destinations) {
        int xMin = Integer.MAX_VALUE, yMin = Integer.MAX_VALUE;
        int xMax = 0, yMax = 0;
        for (Vertex vertex : destinations) {
            String[] xy = vertex.name().split("\\.");
            int x = Integer.valueOf(xy[0]);
            int y = Integer.valueOf(xy[1]);

            xMin = Math.min(xMin, x);
            yMin = Math.min(yMin, y);
            xMax = Math.max(xMax, x);
            yMax = Math.max(yMax, y);
        }
        return Range.TwoDimensionalRange(xMin, xMax, yMin, yMax);
    }
}

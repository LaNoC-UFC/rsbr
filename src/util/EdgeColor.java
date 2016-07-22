package util;

public class EdgeColor {
    static String[] ports = { "N", "S", "E", "W" };

    public static String getInvColor(String color) {
        switch (color) {
            case "E":
                return String.valueOf('W');
            case "W":
                return String.valueOf('E');
            case "N":
                return String.valueOf('S');
            case "S":
                return String.valueOf('N');
            case "I":
                return String.valueOf('I');
            default:
                System.out.println("ERROR : Wrong port Color.");
                return null;
        }
    }

    public static String colorFromTo(String src, String dst) {
        int srcX = Integer.valueOf(src.split("\\.")[0]);
        int srcY = Integer.valueOf(src.split("\\.")[1]);
        int dstX = Integer.valueOf(dst.split("\\.")[0]);
        int dstY = Integer.valueOf(dst.split("\\.")[1]);
        int deltaX = srcX - dstX;
        int deltaY = srcY - dstY;
        assert Math.abs(deltaX) + Math.abs(deltaY) == 1 : src + " and " + dst + " are not neighbors";
        if(1 == deltaX) {
            return "W";
        } else if(-1 == deltaX) {
            return "E";
        } else if(1 == deltaY) {
            return "S";
        } else if(-1 == deltaY) {
            return "N";
        }
        return "I";
    }
}

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
}

package util;

public class EdgeColor {
    static Character[] ports = { 'N', 'S', 'E', 'W' };

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
                System.out.println("ERROR : Wrong port Color.");
                return null;
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
        if(1 == deltaX) {
            return 'W';
        } else if(-1 == deltaX) {
            return 'E';
        } else if(1 == deltaY) {
            return 'S';
        } else if(-1 == deltaY) {
            return 'N';
        }
        return 'I';
    }
}

package util;

public final class Vertex {

    private final String name;

    public Vertex(String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    public boolean isIn(Range box) {
        int xMin = box.min(0);
        int yMin = box.min(1);
        int xMax = box.max(0);
        int yMax = box.max(1);

        int x = Integer.valueOf(name.split("\\.")[0]);
        int y = Integer.valueOf(name.split("\\.")[1]);

        return (x <= xMax && x >= xMin && y <= yMax && y >= yMin);
    }

    @Override
    public boolean equals(Object obj) {
        return ((obj instanceof Vertex) && (((Vertex) obj).name().equals(this.name)));
    }
}

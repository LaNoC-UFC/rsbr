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
        int x = Integer.valueOf(name.split("\\.")[0]);
        int y = Integer.valueOf(name.split("\\.")[1]);
        return box.contains(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        return ((obj instanceof Vertex) && (((Vertex) obj).name().equals(this.name)));
    }
}

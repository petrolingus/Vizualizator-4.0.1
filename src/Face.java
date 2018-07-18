import java.awt.*;

public class Face implements Comparable<Face> {

    Vertex v1;
    Vertex v2;
    Vertex v3;
    float averageZ;

    Color color;

    public Face(Vertex v1, Vertex v2, Vertex v3, float averageZ) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.averageZ = averageZ;
        this.color = new Color((int)(Math.random() * 0xFFFFFF));
    }

    @Override
    public int compareTo(Face o) {
        return Float.compare(o.averageZ, averageZ);
    }
}

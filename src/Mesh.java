import math.Matrix4f;
import math.Vector3f;
import math.Vector4f;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class Mesh {

    private float[] vertices;
    private float[] normals;
    private int[] indices;
    private int[] normalIndices;
    private float[] transformedVertices;

    static Matrix4f projectionMatrix;
    private Matrix4f transformationMatrix;

    ArrayList<Face> faces = new ArrayList<>();

    Color[] colors;

    Mesh(String pathname) {
        try {
            FileReader fileReader = new FileReader(new File(pathname));
            BufferedReader reader = new BufferedReader(fileReader);

            ArrayList<Float> vertexList = new ArrayList<>();
            ArrayList<Float> normalList = new ArrayList<>();
            ArrayList<Integer> indexList = new ArrayList<>();
            ArrayList<Integer> normalIndexList = new ArrayList<>();

            String string;
            while ((string = reader.readLine()) != null) {
                String[] s = string.split(" ");
                if (string.startsWith("v ")) {
                    vertexList.add(Float.parseFloat(s[1]));
                    vertexList.add(Float.parseFloat(s[2]));
                    vertexList.add(Float.parseFloat(s[3]));
                } else if (string.startsWith("vn")) {
                    normalList.add(Float.parseFloat(s[1]));
                    normalList.add(Float.parseFloat(s[2]));
                    normalList.add(Float.parseFloat(s[3]));
                } else if (string.startsWith("f ")) {
                    if (s.length > 4) throw new IOException("Данный тип модели не поддерживается!");
                    for (int i = 1; i < s.length; i++) {
                        String[] ids = s[i].split("/");
                        indexList.add(Integer.parseInt(ids[0]));
                        normalIndexList.add(Integer.parseInt(ids[2]));
                    }
                }
            }

            reader.close();

            vertices = new float[vertexList.size()];
            for (int i = 0; i < vertexList.size(); i++) {
                vertices[i] = vertexList.get(i);
            }
            System.out.println("Indices count: " + vertices.length / 3);

            normals = new float[normalList.size()];
            for (int i = 0; i < normalList.size(); i++) {
                normals[i] = normalList.get(i);
            }
            System.out.println("Normals count: " + normals.length / 3);

            indices = new int[indexList.size()];
            for (int i = 0; i < indexList.size(); i++) {
                indices[i] = indexList.get(i) - 1;
            }
            System.out.println("Indices count: " + indices.length);

            normalIndices = new int[normalIndexList.size()];
            for (int i = 0; i < normalIndexList.size(); i++) {
                normalIndices[i] = normalIndexList.get(i) - 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        colors = new Color[100];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = new Color((int)(Math.random() * 0xFFFFFF));
        }
        transformedVertices = new float[vertices.length];
        createTransformationMatrix(new Vector3f(), new Vector3f(), 1.0f);
    }

    void draw(Graphics2D g, int half_width, int half_height) {
        prepare();
        g.setColor(Color.BLACK);

//        for (int i = 0; i < indices.length; i += 3) {
//            int v1 = 3 * indices[i];
//            int v2 = 3 * indices[i + 1];
//            int v3 = 3 * indices[i + 2];
//
//            int x1 = Math.round(half_width  * transformedVertices[v1]);
//            int y1 = -Math.round(half_height * transformedVertices[v1 + 1]);
//            int x2 = Math.round(half_width  * transformedVertices[v2]);
//            int y2 = -Math.round(half_height * transformedVertices[v2 + 1]);
//            int x3 = Math.round(half_width  * transformedVertices[v3]);
//            int y3 = -Math.round(half_height * transformedVertices[v3 + 1]);
//
//            Polygon polygon = new Polygon();
//            polygon.addPoint(x1, y1);
//            polygon.addPoint(x2, y2);
//            polygon.addPoint(x3, y3);
//            g.drawPolygon(polygon);
//        }

        for (int i = 0; i < faces.size(); i++) {
            Face face = faces.get(i);

            int x1 = Math.round(half_width  * face.v1.x);
            int y1 = -Math.round(half_height * face.v1.y);
            int x2 = Math.round(half_width  * face.v2.x);
            int y2 = -Math.round(half_height * face.v2.y);
            int x3 = Math.round(half_width  * face.v3.x);
            int y3 = -Math.round(half_height * face.v3.y);

            Polygon polygon = new Polygon();
            polygon.addPoint(x1, y1);
            polygon.addPoint(x2, y2);
            polygon.addPoint(x3, y3);

            float x = face.v1.nx;
            float y = face.v1.ny;
            float z = face.v1.nz;

            float lx = 0.5f;
            float ly = 1;
            float lz = 0.7f;

            float numerator = x * lx + y * ly + z * lz;
            float denominator = (float) (Math.sqrt(x * x + y * y + z * z) * Math.sqrt(lx * lx + ly * ly + lz * lz));
            float res = numerator / denominator;

            int red = (int) Math.abs(255 * res);
            int green = (int) Math.abs(255 * res);
            int blue = (int) Math.abs(255 * res);
            Color color = new Color(red, green, blue);
            g.setColor(color);

            g.fillPolygon(polygon);
        }
    }

    private void prepare() {
        for (int i = 0; i < vertices.length; i += 3) {
            float x = vertices[i];
            float y = vertices[i + 1];
            float z = vertices[i + 2];

            Vector4f vector4f = new Vector4f(x, y, z, 1);
            Matrix4f.transform(transformationMatrix, vector4f, vector4f);
            Matrix4f.transform(Camera.viewMatrix, vector4f, vector4f);
            Matrix4f.transform(projectionMatrix, vector4f, vector4f);

            vector4f.x = vector4f.x / vector4f.w;
            vector4f.y = vector4f.y / vector4f.w;
            vector4f.z = vector4f.z / vector4f.w;

            transformedVertices[i] = vector4f.x;
            transformedVertices[i + 1] = vector4f.y;
            transformedVertices[i + 2] = vector4f.z;
        }

        faces = new ArrayList<>();
        for (int i = 0; i < indices.length; i += 3) {
            int i1 = indices[i];
            int i2 = indices[i + 1];
            int i3 = indices[i + 2];

            int normalIndex = 3 * normalIndices[i];
            float normalX = normals[normalIndex];
            float normalY = normals[normalIndex + 1];
            float normalZ = normals[normalIndex + 2];

            Vertex v1 = new Vertex(
                    transformedVertices[3 * i1], transformedVertices[3 * i1 + 1], transformedVertices[3 * i1] + 2,
                    normalX, normalY, normalZ);

            Vertex v2 = new Vertex(
                    transformedVertices[3 * i2], transformedVertices[3 * i2 + 1], transformedVertices[3 * i2] + 2,
                    normalX, normalY, normalZ);

            Vertex v3 = new Vertex(
                    transformedVertices[3 * i3], transformedVertices[3 * i3 + 1], transformedVertices[3 * i3] + 2,
                    normalX, normalY, normalZ);

            float z1 = transformedVertices[3 * i1 + 2];
            float z2 = transformedVertices[3 * i2 + 2];
            float z3 = transformedVertices[3 * i3 + 2];
            float average = (z1 + z2 + z3) / 3;

            faces.add(new Face(v1, v2, v3, average));
        }

        faces.sort(Face::compareTo);
    }

    private void createTransformationMatrix(Vector3f translation, Vector3f rotation, float scale) {
        Matrix4f matrix = new Matrix4f();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1), matrix, matrix);
        Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
        this.transformationMatrix = matrix;
    }
}

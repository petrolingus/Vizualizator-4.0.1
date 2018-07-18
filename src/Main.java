import math.Matrix4f;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

public class Main extends JFrame implements KeyListener {

    //Размеры окна
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int HALF_WIDTH = WIDTH / 2;
    private static final int HALF_HEIGHT = HEIGHT / 2;

    //Данные для проекционной матрицы
    private static final float FOV = 90;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000f;

    private boolean running;

    private Mesh plane;

    private Camera camera;

    private Main() {
        setSize(WIDTH + 6, HEIGHT + 29);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setIgnoreRepaint(true);
        setResizable(false);
        setVisible(true);
        initialize();
        paint();
    }

    private void initialize() {
        running = true;
        plane = new Mesh("cube.obj");
        camera = new Camera();
        addKeyListener(this);
        addKeyListener(camera);
        addMouseMotionListener(camera);
        Mesh.projectionMatrix = createProjectionMatrix();
        createBufferStrategy(2);
        try {
            new Robot().mouseMove(990, 540);
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
    }

    private void paint() {
        new Thread(() -> {
            BufferStrategy bufferStrategy = getBufferStrategy();
            while (running) {
                Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
                g.clearRect(0, 0, WIDTH + 6, HEIGHT + 29);
                g.translate(3 + HALF_WIDTH, 26 + HALF_HEIGHT);

                camera.move();
                plane.draw(g, HALF_WIDTH, HALF_HEIGHT);

                g.dispose();
                bufferStrategy.show();
                Toolkit.getDefaultToolkit().sync();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Matrix4f createProjectionMatrix() {
        float aspectRatio = (float) WIDTH / (float) HEIGHT;
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;
        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m32 = -1;
        projectionMatrix.m33 = 0;
        return projectionMatrix;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public static void main(String[] args) {
        Main main = new Main();
        System.out.println(main);
    }
}

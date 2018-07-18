import math.Matrix4f;
import math.Vector3f;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

class Camera implements KeyListener, MouseMotionListener {

    static Matrix4f viewMatrix;

    private static final float SPEED = 0.02f;
    private static final float ROTATING_SPEED = 0.1f;

    private float x;
    private float y;
    private float z;

    private float pitch;
    private float yaw;

    private boolean[] keys = new boolean[8];

    int oldX = 0;

    Camera() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 15.0f;
        this.pitch = 0.0f;
        this.yaw = 0.0f;
        createViewMatrix();
    }

    private void createViewMatrix() {
        viewMatrix = new Matrix4f();
        Matrix4f.rotate((float)Math.toRadians(pitch), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
        Matrix4f.rotate((float)Math.toRadians(yaw), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
        Vector3f cameraPos = new Vector3f(-x, -y, -z);
        Matrix4f.translate(cameraPos, viewMatrix, viewMatrix);
    }

    void move() {
        if (keys[0]) {
            y -= Math.sin(Math.toRadians(pitch)) * SPEED;
            x += Math.sin(Math.toRadians(yaw)) * SPEED * Math.cos(Math.toRadians(pitch));
            z -= Math.cos(Math.toRadians(yaw)) * SPEED * Math.cos(Math.toRadians(pitch));
        }
        if (keys[1]) {
            y += Math.sin(Math.toRadians(pitch)) * SPEED;
            x -= Math.sin(Math.toRadians(yaw)) * SPEED * Math.cos(Math.toRadians(pitch));
            z += Math.cos(Math.toRadians(yaw)) * SPEED * Math.cos(Math.toRadians(pitch));
        }
        if (keys[2]) {
            z += Math.cos(Math.toRadians(yaw + 90)) * SPEED;
            x -= Math.sin(Math.toRadians(yaw + 90)) * SPEED;
        }
        if (keys[3]) {
            z += Math.cos(Math.toRadians(yaw - 90)) * SPEED;
            x -= Math.sin(Math.toRadians(yaw - 90)) * SPEED;
        }
        if (keys[4]) {
            pitch -= ROTATING_SPEED;
        }
        if (keys[5]) {
            pitch += ROTATING_SPEED;
        }
        if (keys[6]) {
            yaw -= ROTATING_SPEED;
        }
        if (keys[7]) {
            yaw += ROTATING_SPEED;
        }
        createViewMatrix();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                keys[0] = true;
                break;
            case KeyEvent.VK_S:
                keys[1] = true;
                break;
            case KeyEvent.VK_A:
                keys[2] = true;
                break;
            case KeyEvent.VK_D:
                keys[3] = true;
                break;
            case KeyEvent.VK_UP:
                keys[4] = true;
                break;
            case KeyEvent.VK_DOWN:
                keys[5] = true;
                break;
            case KeyEvent.VK_LEFT:
                keys[6] = true;
                break;
            case KeyEvent.VK_RIGHT:
                keys[7] = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                keys[0] = false;
                break;
            case KeyEvent.VK_S:
                keys[1] = false;
                break;
            case KeyEvent.VK_A:
                keys[2] = false;
                break;
            case KeyEvent.VK_D:
                keys[3] = false;
                break;
            case KeyEvent.VK_UP:
                keys[4] = false;
                break;
            case KeyEvent.VK_DOWN:
                keys[5] = false;
                break;
            case KeyEvent.VK_LEFT:
                keys[6] = false;
                break;
            case KeyEvent.VK_RIGHT:
                keys[7] = false;
                break;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        yaw += ROTATING_SPEED * (e.getXOnScreen() - 990);
        pitch += ROTATING_SPEED * (e.getYOnScreen() - 540);
        try {
            new Robot().mouseMove(990, 540);
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
    }
}

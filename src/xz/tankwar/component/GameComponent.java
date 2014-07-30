package xz.tankwar.component;

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;
import java.util.*;

import xz.tankwar.module.MainWindow;

public abstract class GameComponent implements Drawable, Serializable {

    protected static final Random random = new Random();

    protected int x = 130;
    protected int y = 130;
    protected int fact;
    protected boolean alive = true;

    public int getFact() {
        return fact;
    }

    public boolean isAlive() {
        return alive;
    }

    public void abolish() {
        alive = false;
    }

    protected GameComponent() {
    }

    protected GameComponent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    public double distance(GameComponent g) {
        if (g == null)
            return 100000000000.0;
        return Math.sqrt(Math.pow(g.x - x, 2) + Math.pow(g.y - y, 2));
    }

    public double distance(Point g) {
        if (g == null)
            return 100000000000.0;
        return Math.sqrt(Math.pow(g.x - x, 2) + Math.pow(g.y - y, 2));
    }

    public boolean inScreen() {
        return !(x < -10 || x > MainWindow.WINDOW_WIDTH + 10 || y < -10 || y > MainWindow.WINDOW_HEIGHT + 10);
    }
    
}

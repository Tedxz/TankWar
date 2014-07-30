package xz.tankwar.component.weapon;

import java.awt.*;

import xz.tankwar.component.*;
import xz.tankwar.component.tank.Tank;
import xz.tankwar.module.MainWindow;
import static xz.tankwar.component.Direction.*;

public class Missile extends Weapon {
    static final int R = 4;
    static final int EXPLOSION_R[] = { 4, 6 };
    protected int STEP = 12;
    protected Direction dir;
    protected int power = 90;
    protected Color clr;
    
    public Missile(int _x, int _y, Direction _dir, int _pow, int _fact) {
        x = _x;
        y = _y;
        dir = _dir;
        power = _pow;
        fact = _fact;
        if (fact == 1)
            clr = Color.black;
        else
            clr = Color.gray;
    }
    public Missile(int _x, int _y, Direction _dir, int _pow, int _fact, int _step, Color _clr) {
        this(_x, _y, _dir, _pow, _fact);
        STEP = _step;
        clr = _clr;
    }
    
    public void draw(Graphics g) {
        g.setColor(clr);
        g.fillOval(x - R, y - R, R * 2, R * 2);
    }

    public int getAttackRadius() {
        return R;
    }

    public void autoAct() {
        if (!inScreen()) {
            abolish();
            return;
        }
        if (tryAttack()) {
            abolish();
            explode();
            return;
        }
        move(dir, STEP);
    }
    
    public void explode() {
        synchronized (MainWindow.explosions) {
            MainWindow.explosions.add(new Explosion(x, y, EXPLOSION_R, fact));
        }
    }

    public void move(Direction dir, int step) {
        x += (int)(step * unitVectorX(dir));
        y += (int)(step * unitVectorY(dir));
    }

    public void move() {
        move(dir, STEP);
    }

    public int getPower() {
        return power;
    }

    public boolean effect(Tank t) {
        t.makeDamage(power);
        return true;
    }

}

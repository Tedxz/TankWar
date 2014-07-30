package xz.tankwar.component.weapon;

import java.awt.*;

import xz.tankwar.component.tank.ComTank;
import xz.tankwar.component.tank.Tank;
import xz.tankwar.module.MainWindow;
import static xz.tankwar.component.tank.ComTank.ComTankType.*;

public class Mine extends Weapon {
    static final int R = 15, POWER = 3000;
    static final int EXPLOSION_R[] = { 15, 20, 27, 22 };
    int lastTime = 53;

    public Mine(int _x, int _y, int _fact) {
        super(_x, _y);
        fact = _fact;
        fact = -1;
    }

    public void draw(Graphics g) {
        g.setColor(Color.red);
        g.fillOval(x - R + 1, y - R + 1, R * 2, R * 2);
        g.setColor(Color.black);
        g.fillOval(x - R, y - R, R * 2, R * 2);
        g.fillRect(x - 5, y - R - 2, 10, R);
        g.setColor(Color.red);
        if (lastTime > 0)
            g.drawString("" + lastTime / 10, x - 3, y + 5);
        else
            g.drawString("ON", x - 9, y + 5);
    }

    @Override
    public void autoAct() {
        if (lastTime > 0) {
            --lastTime;
            return;
        }
        if (tryAttack()) {
            explode();
        }
    }

    public void explode() {
        abolish();
        synchronized (MainWindow.explosions) {
            MainWindow.explosions.add(new Explosion(x, y, EXPLOSION_R, fact));
        }
    }

    public int getAttackRadius() {
        return R;
    }

    public int getPower() {
        return POWER;
    }

    public boolean effect(Tank t) {
        if (t instanceof ComTank && ((ComTank)t).tag == ENGINEER)
            return false;
        t.makeDamage(POWER);
        return true;
    }

}

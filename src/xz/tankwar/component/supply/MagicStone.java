package xz.tankwar.component.supply;

import java.awt.*;
import static java.awt.Color.*;

import xz.tankwar.component.tank.PlayerTank;
import xz.tankwar.component.tank.Tank;
import xz.tankwar.component.weapon.Weapon;
import static xz.tankwar.module.PropertiesManager.*;

public class MagicStone extends Weapon {
    static final int R = 15, RC = 5;
    final int[] x1, x2, y1, y2;
    
    int lastTime = 10;

    public MagicStone(int _x, int _y) {
        x = _x;
        y = _y;
        fact = -1;
        x1 = new int[] { x - 15, x, x };
        x2 = new int[] { x + 15, x, x };
        y1 = new int[] { y, y - 15, y };
        y2 = new int[] { y, y + 23, y };
    }
    
    public void autoAct() {
        if (lastTime != 0) {
            --lastTime;
            return;
        }
        if (tryAttack()) {
            abolish();
        }
    }
    
    public void draw(Graphics g) {
        g.setColor(lightGemBlue);
        g.fillPolygon(x1, y1, 3);
        g.setColor(gemBlue);
        g.fillPolygon(x2, y1, 3);
        g.fillPolygon(x1, y2, 3);
        g.setColor(blue);
        g.fillPolygon(x2, y2, 3);
    }

    public int getAttackRadius() {
        return R;
    }

    public boolean effect(Tank t) {
        if (t instanceof PlayerTank) {
            ((PlayerTank)t).modifyMP(1000);
            ((PlayerTank)t).setEnergeticTime(400);
        }
        return true;
    }

}

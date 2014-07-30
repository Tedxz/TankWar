package xz.tankwar.component.supply;

import java.awt.Color;
import java.awt.Graphics;

import xz.tankwar.component.tank.*;
import xz.tankwar.component.weapon.*;

public class HealPack extends Weapon {
    static final int R = 15, RC = 5;
    int lastTime = 10;

    public HealPack(int _x, int _y) {
        x = _x;
        y = _y;
        fact = -1;
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
        g.setColor(Color.black);
        g.drawRoundRect(x - R, y - R, R * 2, R * 2, RC, RC);
        g.setColor(Color.white);
        g.fillRoundRect(x - R, y - R, R * 2, R * 2, RC, RC);
        g.setColor(Color.red);
        g.fillRect(x - 3, y - 9, 6, 18);
        g.fillRect(x - 9, y - 3, 18, 6);
    }

    public int getAttackRadius() {
        return R;
    }

    public boolean effect(Tank t) {
        t.modifyHP(t.getMaxHP());
        if (t instanceof PlayerTank)
            ((PlayerTank)t).setCrazyTime(400);
        return true;
    }

}

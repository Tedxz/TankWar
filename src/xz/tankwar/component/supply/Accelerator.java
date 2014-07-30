package xz.tankwar.component.supply;

import java.awt.Color;
import java.awt.Graphics;

import xz.tankwar.component.tank.*;
import xz.tankwar.component.weapon.*;

public class Accelerator extends Weapon {
    static final int R = 15;
    int lastTime = 10;

    public Accelerator(int _x, int _y) {
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
        g.setColor(Color.green);
        g.fillPolygon(new int[] { x, x - 10, x + 10 }, new int[] { y - 5, y + 5, y + 5 }, 3);
        g.fillPolygon(new int[] { x, x - 10, x + 10 }, new int[] { y - 15, y - 5, y - 5 }, 3);
        g.fillPolygon(new int[] { x, x - 10, x + 10 }, new int[] { y + 5, y + 15, y + 15 }, 3);
        
    }

    public int getAttackRadius() {
        return R;
    }

    @Override
    public boolean effect(Tank t) {
        if (t instanceof PlayerTank)
            ((PlayerTank)t).setAccTime(500);
        if (t instanceof ComTank) {
            ((ComTank)t).setStep(((ComTank)t).getStep() + 3);
            if (((ComTank)t).getStep() > 10)
                ((ComTank)t).setStep(10);
        }
        return true;
    }

}

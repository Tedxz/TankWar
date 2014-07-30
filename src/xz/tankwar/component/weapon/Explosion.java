package xz.tankwar.component.weapon;

import java.awt.*;

import xz.tankwar.component.GameComponent;
import xz.tankwar.component.tank.Tank;
import xz.tankwar.module.MainWindow;

public class Explosion extends Weapon {
    
    int[] expRadius = null;
    int curF = 0;
    int power = 0;
    Color clr = Color.orange;
    
    public Explosion(int _x, int _y, int[] er, int _fact) {
        x = _x;
        y = _y;
        expRadius = er;
        fact = _fact;
    }
    public Explosion(int _x, int _y, int[] er, int _fact, Color _clr) {
        x = _x;
        y = _y;
        expRadius = er;
        fact = _fact;
        clr = _clr;
    }
    public Explosion(int _x, int _y, int[] er, int _fact, Color _clr, int pow) {
        x = _x;
        y = _y;
        expRadius = er;
        fact = _fact;
        clr = _clr;
        power = pow;
    }
    
    public void draw(Graphics g) {
        g.setColor(clr);
        if (curF < expRadius.length)
            g.fillOval(x - expRadius[curF], y - expRadius[curF], expRadius[curF] * 2, expRadius[curF] * 2);
    }
    @Override
    public void autoAct() {
        if (curF >= expRadius.length) {
            abolish();
            return;
        }
        if (power != 0)
            tryAttack();
        ++curF;
    }
    @Override
    public int getAttackRadius() {
        return expRadius[curF];
    }
    @Override
    public boolean effect(Tank t) {
        if (power == -1) 
            t.makeDamage(t.getMaxHP());
        else
            t.makeDamage(power);
        return true;
    }

}

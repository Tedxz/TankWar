package xz.tankwar.component.weapon;

import java.awt.*;

import xz.tankwar.component.Automatic;
import xz.tankwar.component.GameComponent;
import xz.tankwar.component.tank.Tank;
import xz.tankwar.module.MainWindow;

public abstract class Weapon extends GameComponent implements Automatic {

    public abstract int getAttackRadius(); 
    public abstract boolean effect(Tank t);
    
    protected Weapon () {
    }
    
    protected Weapon(int _x, int _y) {
        super(_x, _y);
    }
    
    public boolean tryAttack() {
        int r = getAttackRadius();
        boolean flag = false;
        if (fact != 0) {
            synchronized (MainWindow.friends) {
                for (Tank t : MainWindow.friends)
                    if ((Math.abs(t.getX() - x) <= (r + t.HALF_WIDTH)) && 
                            (Math.abs(t.getY() - y) <= (r + t.HALF_WIDTH))) {
                        flag |= effect(t);
                    }
            }
            if ((Math.abs(MainWindow.myTank.getX() - x) <= (r + MainWindow.myTank.HALF_WIDTH)) 
                    && (Math.abs(MainWindow.myTank.getY() - y) <= (r + MainWindow.myTank.HALF_WIDTH))) {
                if (!MainWindow.myTank.isInvisible()) {
                    flag |= effect(MainWindow.myTank);
                }
            }
        } 
        if (fact != 1)
            synchronized (MainWindow.tanks) {
                for (Tank t : MainWindow.tanks)
                    if ((Math.abs(t.getX() - x) <= (r + t.HALF_WIDTH)) && 
                            (Math.abs(t.getY() - y) <= (r + t.HALF_WIDTH))) {
                        flag |= effect(t);
                    }
            }
        return flag;
    }

}

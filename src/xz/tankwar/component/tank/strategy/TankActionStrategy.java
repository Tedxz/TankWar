package xz.tankwar.component.tank.strategy;

import java.io.Serializable;
import java.util.*;

import xz.tankwar.component.*;
import xz.tankwar.component.tank.*;
import xz.tankwar.module.MainWindow;
import static xz.tankwar.component.Direction.*;

public abstract class TankActionStrategy implements Serializable {
    public static final double SQRT_2 = 1.414;
    protected static Random random = new Random();
    protected ComTank subject = null;
    protected boolean ignoreMoveLimit = false;
    protected int accuracy = 95;
    protected int coolDown;
    protected int lastCD;
    protected int turnProbability = 6;
    protected int unblockTime = 0;
    protected Direction unblockDir;

    protected Tank t;

    public TankActionStrategy(ComTank _s, int CD) {
        subject = _s;
        coolDown = CD;
        lastCD = random.nextInt(coolDown);
    }

    public TankActionStrategy(ComTank _s, int CD, boolean iml, int tp) {
        this(_s, CD);
        ignoreMoveLimit = iml;
        if (tp != -1)
            turnProbability = tp;
    }

    public void AIlaunch() {
        if (random.nextInt(100) < accuracy) {
            Tank target = findTarget();
            if (target != null)
                subject.setShootDir(findDirection(target));
            if (subject.getShootDir() != null && subject.getShootDir() != STOP)
                subject.setCannonDir(subject.getShootDir());
        }
        subject.launch();
    }

    protected Direction findDirection(GameComponent target) {
        Direction tDir = STOP;
        if (target != null) {
            if (target.getX() < subject.getX() - Tank.HALF_WIDTH)
                // tDir |= GameComponent.LEFT;
                tDir = compose(tDir, LEFT);
            if (target.getX() > subject.getX() + Tank.HALF_WIDTH)
                tDir = compose(tDir, RIGHT);
            if (target.getY() < subject.getY() - Tank.HALF_WIDTH)
                tDir = compose(tDir, UP);
            if (target.getY() > subject.getY() + Tank.HALF_WIDTH)
                tDir = compose(tDir, DOWN);
        }
        return tDir;
    }

    protected Tank findTarget() {
        Tank target = null;
        if (subject.getFact() == 1) {
            target = MainWindow.myTank;
        } else
            synchronized (MainWindow.tanks) {
                if (MainWindow.tanks.size() != 0)
                    target = MainWindow.tanks.get(0);
            }
        return target;
    }

    protected boolean inAttackArea(GameComponent t) {
        Direction tDir = findDirection(t);
        double x1, y1;
        double cross[] = new double[4];
        boolean bl = false, br = false;
        x1 = unitVectorX(tDir);
        y1 = unitVectorY(tDir);
        cross[0] = x1 * (t.getY() - Tank.HALF_WIDTH - subject.getY()) - y1
                * (t.getX() - Tank.HALF_WIDTH - subject.getX());
        cross[1] = x1 * (t.getY() + Tank.HALF_WIDTH - subject.getY()) - y1
                * (t.getX() - Tank.HALF_WIDTH - subject.getX());
        cross[2] = x1 * (t.getY() - Tank.HALF_WIDTH - subject.getY()) - y1
                * (t.getX() + Tank.HALF_WIDTH - subject.getX());
        cross[3] = x1 * (t.getY() + Tank.HALF_WIDTH - subject.getY()) - y1
                * (t.getX() + Tank.HALF_WIDTH - subject.getX());
        for (int i = 0; i < 4; ++i) {
            if (cross[i] > 0)
                br = true;
            if (cross[i] < 0)
                bl = true;
        }
        return bl && br;
    }

    protected void autoLaunch() {
        subject.setShootDir(subject.getMoveDir());
        subject.move();
        if (lastCD < 5 && random.nextInt(10) < lastCD)
            lastCD = 0;
        if (lastCD == 0) {
            AIlaunch();
            lastCD = coolDown;
        } else {
            --lastCD;
        }
    }

    abstract public void act();

}

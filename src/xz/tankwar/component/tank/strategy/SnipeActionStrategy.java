package xz.tankwar.component.tank.strategy;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import xz.tankwar.component.*;
import xz.tankwar.component.tank.*;
import xz.tankwar.component.weapon.*;
import static xz.tankwar.component.Direction.*;

public class SnipeActionStrategy extends TankActionStrategy implements Drawable {
    protected static final int lockSpeed = 30;

    protected int followTime;
    protected int lockOnTime;
    protected int disTime;

    public SnipeActionStrategy(ComTank _s, int CD) {
        super(_s, CD);
    }

    public void act() {
        if (lastCD > 0) {
            subject.move(STOP);
            --lastCD;
            return;
        }
        t = findTarget();
        if (followTime + lockOnTime == 0) {
            if (random.nextInt(100) < 4) {
                subject.setMoveDir(rotate(findDirection(t),
                        random.nextInt(7) + 1));
                subject.setCannonDir(subject.getMoveDir());
            }
            subject.move();
            subject.setCannonDir(subject.getMoveDir());
            if (inAttackArea(t)) {
                disTime = followTime =
                        (int)(subject.distance(t) / lockSpeed);
                lockOnTime = 0;
            }
            return;
        }
        if (followTime > 0) {
            if (random.nextInt(100) < 4) {
                subject.setMoveDir(rotate(findDirection(t),
                        random.nextInt(7) + 1));
                subject.setCannonDir(subject.getMoveDir());
            }
            subject.move();
            subject.setCannonDir(subject.getMoveDir());
            if (inAttackArea(t)) {
                --followTime;
                if (followTime == 0) {
                    lockOnTime = 35;
                    subject.setStep(((PlayerTank)t).step);
                }
            } else {
                followTime = 0;
            }
            return;
        }
        if (lockOnTime > 0) {
            --lockOnTime;
            subject.move(t.getMoveDir());
            subject.setCannonDir(findDirection(t));
            if (subject.blockTime > 60 && subject.getMoveDir().dimension() == 1
                    && subject.getCannonDir().dimension() == 2) {
                if (rotate(subject.getMoveDir(), 3) == subject.getCannonDir())
                    subject.move(rotate(subject.getMoveDir(), 2));
                else
                    subject.move(rotate(subject.getMoveDir(), 6));
                subject.setCannonDir(findDirection(t));
            }
            if (lockOnTime == 0) {
                Missile m = new Missile(subject.getX(), subject.getY(), subject
                        .getCannonDir(), subject.getPower(), subject.getFact(),
                        40, Color.red);
                subject.launch(m);
                lastCD = coolDown;
                subject.setStep(1);
            }
        }
    }

    public void draw(Graphics g) {
        if (followTime > 0) {
            int dlt = (disTime - followTime) * lockSpeed;
            Direction tDir = findDirection(findTarget());
            int nx = subject.getX(), ny = subject.getY();
            nx += (int)(dlt * unitVectorX(tDir));
            ny += (int)(dlt * unitVectorY(tDir));
            g.setColor(Color.red);
            g.drawOval(nx - 10, ny - 10, 20, 20);
            g.drawLine(nx, ny - 15, nx, ny + 15);
            g.drawLine(nx - 15, ny, nx + 15, ny);
        }
        if (lockOnTime > 0) {
            g.setColor(Color.red);
            g.drawOval(subject.getX() - lockOnTime,
                    subject.getY() - lockOnTime, lockOnTime * 2, lockOnTime * 2);
        }

    }
}

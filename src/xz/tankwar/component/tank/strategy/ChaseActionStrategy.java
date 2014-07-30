package xz.tankwar.component.tank.strategy;

import xz.tankwar.component.*;
import xz.tankwar.component.tank.*;
import static xz.tankwar.component.Direction.*;

public class ChaseActionStrategy extends TankActionStrategy {

    protected int getCloseProbability = 100;
    protected int dis = 0;
    protected int escTime;

    public ChaseActionStrategy(ComTank _s, int CD) {
        super(_s, CD);
    }

    public ChaseActionStrategy(ComTank _s, int CD, int dis) {
        super(_s, CD);
        this.dis = dis;
    }

    public ChaseActionStrategy(ComTank _s, int CD, boolean iml, int tp,
            int gcp, int dis) {
        super(_s, CD, iml, tp);
        getCloseProbability = gcp;
        this.dis = dis;
    }

    public void act() {
        t = findTarget();
        if (t == null) {
            if (random.nextInt(100) < turnProbability)
                subject.setMoveDir(rotate(UP, random.nextInt(8)));
        } else {
            if (escTime > 0)
                --escTime;
            if (random.nextInt(100) < getCloseProbability && escTime == 0) {
                subject.setMoveDir(findDirection(t));
                if (subject.distance(t) < dis) {
                    if (subject.distance(t) < dis - 20) {
                        subject.setMoveDir(rotate(subject.getMoveDir(), 3 + random.nextInt(3)));// 转向？
                        escTime = 10;
                    }
                    else
                        subject.setMoveDir(STOP);// 转向？
                }
            }
        }
        if (subject.blockTime > 0) {
            if (unblockTime <= 0)
                unblockTime = 5;
            unblockDir = rotate(subject.getMoveDir(), random.nextInt(4) + 1);
        }
        if (unblockTime > 0) {
            subject.setMoveDir(unblockDir);
            --unblockTime;
        }

        autoLaunch();
    }

}

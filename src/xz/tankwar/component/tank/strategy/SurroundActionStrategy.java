package xz.tankwar.component.tank.strategy;

import xz.tankwar.component.*;
import xz.tankwar.component.tank.*;
import static xz.tankwar.component.Direction.*;

public class SurroundActionStrategy extends TankActionStrategy {

    protected int getCloseProbability = 70;
    protected int dis = 90;
    protected int turnAngle = 2;

    public SurroundActionStrategy(ComTank _s, int CD) {
        super(_s, CD);
    }

    public SurroundActionStrategy(ComTank _s, int CD, boolean iml, int tp) {
        super(_s, CD, iml, tp);
    }

    public SurroundActionStrategy(ComTank _s, int CD, boolean iml, int tp,
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
            if (random.nextInt(100) < getCloseProbability) {
                subject.setMoveDir(findDirection(t));
                if (subject.distance(t) < dis)
                    subject.setMoveDir(rotate(subject.getMoveDir(), turnAngle));
                if (subject.distance(t) < dis - 20)
                    subject.setMoveDir(rotate(subject.getMoveDir(), turnAngle));
            }
        }

        if (subject.blockTime > 0 && unblockTime == 0) {
            if (unblockTime <= 0)
                unblockTime = 5;
            if (unblockTime == 5)
                turnAngle = 8 - turnAngle;
        }
        if (subject.blockTime > 0)
            --subject.blockTime;
        if (unblockTime > 0)
            --unblockTime;

        autoLaunch();
    }
}

package xz.tankwar.component.tank.strategy;

import xz.tankwar.component.*;
import xz.tankwar.component.tank.*;
import static xz.tankwar.component.Direction.*;

public class NormalActionStrategy extends TankActionStrategy {

    public NormalActionStrategy(ComTank _s, int CD) {
        super(_s, CD);
    }

    public NormalActionStrategy(ComTank _s, int CD, boolean iml, int tp) {
        super(_s, CD, iml, tp);
    }

    public void act() {
        t = findTarget();
        if (random.nextInt(100) < turnProbability)
            subject.setMoveDir(randomDirection());

        if (subject.blockTime > 0) {
            if (unblockTime <= 0)
                unblockTime = (int)subject.blockTime;
            unblockDir = rotate(subject.getMoveDir(), random.nextInt(4) + 1);
        }
        if (unblockTime > 0) {
            if (unblockTime % 20 == 0)
                unblockDir = rotate(subject.getMoveDir(), random.nextInt(4) + 1);
            subject.setMoveDir(unblockDir);
            --unblockTime;
        }

        autoLaunch();
    }

}

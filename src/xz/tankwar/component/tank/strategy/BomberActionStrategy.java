package xz.tankwar.component.tank.strategy;

import xz.tankwar.component.tank.*;

public class BomberActionStrategy extends ChaseActionStrategy {
    public BomberActionStrategy(ComTank _s, int CD) {
        super(_s, CD);
    }
    public BomberActionStrategy(ComTank _s, int CD, int dis) {
        super(_s, CD, dis);
    }
    public BomberActionStrategy(ComTank _s, int CD, boolean iml, int tp, int gcp, int dis) {
        super(_s, CD, iml, tp, gcp, dis);
    }
    
    public void act() {
        super.act();
        if (subject.distance(t) < 71)
            subject.makeDamage(subject.getMaxHP());
    }
}

package xz.tankwar.component.tank.strategy;

import xz.tankwar.component.tank.ComTank;
import xz.tankwar.component.weapon.Mine;
import xz.tankwar.module.MainWindow;

public class EngineerActionStrategy extends SurroundActionStrategy {
    public EngineerActionStrategy(ComTank _s, int CD) {
        super(_s, CD);
    }
    public EngineerActionStrategy(ComTank _s, int CD, boolean iml, int tp) {
        super(_s, CD, iml, tp);
    }
    public EngineerActionStrategy(ComTank _s, int CD, boolean iml, int tp, int gcp, int dis) {
        super(_s, CD, iml, tp, gcp, dis);
    }

    public void act() {
        super.act();
        if (lastCD == coolDown / 2 && subject.distance(t) < 250) {
            Mine m = null;
            for (int i = 0; i < MainWindow.weapons.size(); ++i) {
                if (MainWindow.weapons.get(i) instanceof Mine) {
                    m = (Mine)MainWindow.weapons.get(i);
                    if (m.distance(subject) <30)
                        return;
                }
            }
            synchronized (MainWindow.weapons){
                MainWindow.weapons.add(new Mine(subject.getX(), subject.getY(), subject.getFact()));
            }
        }

    }
}

package xz.tankwar.module.tankgenerator;

import xz.tankwar.component.tank.ComTank;
import static xz.tankwar.component.tank.ComTank.ComTankType.*;

public class BomberWaveGenerator extends AbstractWaveTankGenerator {

    public BomberWaveGenerator(int difficulty) {
        for (int i = 0; i < difficulty / 3 + 1; ++i) {
            tankList.add(new ComTank(BOMBER, i % 4));
        }
    }

}

package xz.tankwar.module.tankgenerator;

import xz.tankwar.component.tank.ComTank;
import static xz.tankwar.component.tank.ComTank.ComTankType.*;

public class NormalWaveGenerator extends AbstractWaveTankGenerator {

    public NormalWaveGenerator(int difficulty) {
        for (int i = 0; i < difficulty + 1; ++i)
            tankList.add(new ComTank(ENEMY, i % 4));
    }
    
}

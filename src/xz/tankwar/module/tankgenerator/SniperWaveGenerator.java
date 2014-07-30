package xz.tankwar.module.tankgenerator;

import xz.tankwar.component.tank.ComTank;
import static xz.tankwar.component.tank.ComTank.ComTankType.*;

public class SniperWaveGenerator extends AbstractWaveTankGenerator {
    
    public SniperWaveGenerator(int difficulty) {
        for (int i = 0; i < difficulty / 3 + 1; ++i) {
            tankList.add(new ComTank(SNIPER, i % 4));
        }
    }

}

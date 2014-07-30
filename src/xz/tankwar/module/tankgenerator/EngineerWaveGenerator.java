package xz.tankwar.module.tankgenerator;

import xz.tankwar.component.tank.ComTank;
import static xz.tankwar.component.tank.ComTank.ComTankType.*;

public class EngineerWaveGenerator extends AbstractWaveTankGenerator {

    public EngineerWaveGenerator(int difficulty) {
        for (int i = 0; i < difficulty / 2 + 1; ++i) {
            tankList.add(new ComTank(ENGINEER, i % 4));
        }
    }

}

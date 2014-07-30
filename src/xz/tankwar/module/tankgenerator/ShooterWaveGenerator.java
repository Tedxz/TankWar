package xz.tankwar.module.tankgenerator;

import xz.tankwar.component.tank.ComTank;
import static xz.tankwar.component.tank.ComTank.ComTankType.*;

public class ShooterWaveGenerator extends AbstractWaveTankGenerator {

    public ShooterWaveGenerator(int difficulty) {
        for (int i = 0; i < difficulty / 3 + 1; ++i) {
            tankList.add(new ComTank(SHOOTER, i % 4));
        }
    }

}

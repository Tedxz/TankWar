package xz.tankwar.module.tankgenerator;

import xz.tankwar.component.tank.ComTank;
import static xz.tankwar.component.tank.ComTank.ComTankType.*;
 
public class SoysauceWaveGenerator extends AbstractWaveTankGenerator {
    
    public SoysauceWaveGenerator(int difficulty) {
        for (int i = 0; i < 4; ++i) {
            tankList.add(new ComTank(SOY_SAUCE, difficulty % 4));
        }
    }

}

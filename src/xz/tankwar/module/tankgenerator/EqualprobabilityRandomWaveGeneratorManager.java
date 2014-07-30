package xz.tankwar.module.tankgenerator;

import java.util.Random;

public class EqualprobabilityRandomWaveGeneratorManager {
    private static Random random = new Random();
    public static AbstractWaveTankGenerator getWaveGen(int killed) {
        switch (random.nextInt(7)) {
            case 0:
                return new NormalWaveGenerator(5 + killed / 100 + random.nextInt(2));
            case 1:
                return new SniperWaveGenerator(5 + killed / 100 + random.nextInt(2));
            case 2:
                return new ShooterWaveGenerator(5 + killed / 100 + random.nextInt(2));
            case 3:
                return new EngineerWaveGenerator(5 + killed / 100 + random.nextInt(2));
            case 4:
                return new BomberWaveGenerator(5 + killed / 100 + random.nextInt(2));
            case 5:
                return new EngineerShooterWaveGenerator(5 + killed / 100 + random.nextInt(2));
            case 6:
                return new BomberSniperWaveGenerator(5 + killed / 100 + random.nextInt(2));
        }
        
        return new NormalWaveGenerator(5 + killed / 100 + random.nextInt(2));
    }
}

package xz.tankwar.module.tankgenerator;

import java.util.Random;

public class WaveGeneratorManager {
    private static Random random = new Random();
    public static AbstractWaveTankGenerator getWaveGen(int killed) {
        int rnd = random.nextInt(100);
        int diff = 5 + killed / 100 + random.nextInt(2);
        if (random.nextInt(1000) < 3)
            return new SoysauceWaveGenerator(diff);
        if (killed < 50) {
            return new NormalWaveGenerator(diff);
        }
        if (killed < 100) {
            if (rnd < 85)
                return new NormalWaveGenerator(diff);
            return new EngineerWaveGenerator(diff);
        }
        if (killed < 150) {
            if (rnd < 80)
                return new NormalWaveGenerator(diff);
            if (rnd < 90)
                return new BomberWaveGenerator(diff);
            return new EngineerWaveGenerator(diff);
        }
        if (killed < 220) {
            if (rnd < 79)
                return new NormalWaveGenerator(diff);
            if (rnd < 86)
                return new BomberWaveGenerator(diff);
            if (rnd < 93)
                return new EngineerWaveGenerator(diff);
            return new ShooterWaveGenerator(diff);
        }
        if (killed < 280) {
            if (rnd < 72)
                return new NormalWaveGenerator(diff);
            if (rnd < 79)
                return new BomberWaveGenerator(diff);
            if (rnd < 86)
                return new EngineerWaveGenerator(diff);
            if (rnd < 93)
                return new ShooterWaveGenerator(diff);
            return new EngineerShooterWaveGenerator(diff);
        }
        if (killed < 370) {
            if (rnd < 70)
                return new NormalWaveGenerator(diff);
            if (rnd < 76)
                return new BomberWaveGenerator(diff);
            if (rnd < 82)
                return new EngineerWaveGenerator(diff);
            if (rnd < 88)
                return new ShooterWaveGenerator(diff);
            if (rnd < 94)
                return new EngineerShooterWaveGenerator(diff);
            return new SniperWaveGenerator(diff);
        }
        if (killed < 450) {
            if (rnd < 70)
                return new AdvancedNormalWaveGenerator(diff);
            if (rnd < 75)
                return new BomberWaveGenerator(diff);
            if (rnd < 80)
                return new EngineerWaveGenerator(diff);
            if (rnd < 85)
                return new ShooterWaveGenerator(diff);
            if (rnd < 90)
                return new EngineerShooterWaveGenerator(diff);
            if (rnd < 95)
                return new SniperWaveGenerator(diff);
            return new BomberSniperWaveGenerator(diff);
        }
        if (killed < 500) {
            if (rnd < 65)
                return new AdvancedNormalWaveGenerator(diff);
            if (rnd < 70)
                return new BomberSniperWaveGenerator(diff);
            if (rnd < 75)
                return new BomberWaveGenerator(diff);
            if (rnd < 80)
                return new EngineerWaveGenerator(diff);
            if (rnd < 85)
                return new ShooterWaveGenerator(diff);
            if (rnd < 90)
                return new EngineerShooterWaveGenerator(diff);
            if (rnd < 95)
                return new SniperWaveGenerator(diff);
            return new BomberShooterWaveGenerator(diff);
        }
        if (killed < 550) {
            if (rnd < 60)
                return new AdvancedNormalWaveGenerator(diff);
            if (rnd < 65)
                return new BomberShooterWaveGenerator(diff);
            if (rnd < 70)
                return new BomberSniperWaveGenerator(diff);
            if (rnd < 75)
                return new BomberWaveGenerator(diff);
            if (rnd < 80)
                return new EngineerWaveGenerator(diff);
            if (rnd < 85)
                return new ShooterWaveGenerator(diff);
            if (rnd < 90)
                return new EngineerShooterWaveGenerator(diff);
            if (rnd < 95)
                return new SniperWaveGenerator(diff);
            return new EngineerSniperWaveGenerator(diff);
        }
        if (killed < 800) {
            if (rnd < 20)
                return new RandomWaveGenerator(diff);
            if (rnd < 60)
                return new AdvancedNormalWaveGenerator(diff);
            if (rnd < 65)
                return new BomberShooterWaveGenerator(diff);
            if (rnd < 70)
                return new BomberSniperWaveGenerator(diff);
            if (rnd < 75)
                return new BomberWaveGenerator(diff);
            if (rnd < 80)
                return new EngineerWaveGenerator(diff);
            if (rnd < 85)
                return new ShooterWaveGenerator(diff);
            if (rnd < 90)
                return new EngineerShooterWaveGenerator(diff);
            if (rnd < 95)
                return new SniperWaveGenerator(diff);
            return new EngineerSniperWaveGenerator(diff);
        }
        if (killed < 1100) {
            if (rnd < 30)
                return new RandomWaveGenerator(diff);
            if (rnd < 60)
                return new AdvancedNormalWaveGenerator(diff);
            if (rnd < 65)
                return new BomberShooterWaveGenerator(diff);
            if (rnd < 70)
                return new BomberSniperWaveGenerator(diff);
            if (rnd < 75)
                return new BomberWaveGenerator(diff);
            if (rnd < 80)
                return new EngineerWaveGenerator(diff);
            if (rnd < 85)
                return new ShooterWaveGenerator(diff);
            if (rnd < 90)
                return new EngineerShooterWaveGenerator(diff);
            if (rnd < 95)
                return new SniperWaveGenerator(diff);
            return new EngineerSniperWaveGenerator(diff);
        }
        if (killed < 1500) {
            if (rnd < 40)
                return new RandomWaveGenerator(diff);
            if (rnd < 60)
                return new AdvancedNormalWaveGenerator(diff);
            if (rnd < 65)
                return new BomberShooterWaveGenerator(diff);
            if (rnd < 70)
                return new BomberSniperWaveGenerator(diff);
            if (rnd < 75)
                return new BomberWaveGenerator(diff);
            if (rnd < 80)
                return new EngineerWaveGenerator(diff);
            if (rnd < 85)
                return new ShooterWaveGenerator(diff);
            if (rnd < 90)
                return new EngineerShooterWaveGenerator(diff);
            if (rnd < 95)
                return new SniperWaveGenerator(diff);
            return new EngineerSniperWaveGenerator(diff);
        }
        if (killed < 2000) {
            if (rnd < 50)
                return new RandomWaveGenerator(diff);
            if (rnd < 60)
                return new AdvancedNormalWaveGenerator(diff);
            if (rnd < 65)
                return new BomberShooterWaveGenerator(diff);
            if (rnd < 70)
                return new BomberSniperWaveGenerator(diff);
            if (rnd < 75)
                return new BomberWaveGenerator(diff);
            if (rnd < 80)
                return new EngineerWaveGenerator(diff);
            if (rnd < 85)
                return new ShooterWaveGenerator(diff);
            if (rnd < 90)
                return new EngineerShooterWaveGenerator(diff);
            if (rnd < 95)
                return new SniperWaveGenerator(diff);
            return new EngineerSniperWaveGenerator(diff);
        }
        {
            if (rnd < 55)
                return new RandomWaveGenerator(diff);
            if (rnd < 60)
                return new AdvancedNormalWaveGenerator(diff);
            if (rnd < 65)
                return new BomberShooterWaveGenerator(diff);
            if (rnd < 70)
                return new BomberSniperWaveGenerator(diff);
            if (rnd < 75)
                return new BomberWaveGenerator(diff);
            if (rnd < 80)
                return new EngineerWaveGenerator(diff);
            if (rnd < 85)
                return new ShooterWaveGenerator(diff);
            if (rnd < 90)
                return new EngineerShooterWaveGenerator(diff);
            if (rnd < 95)
                return new SniperWaveGenerator(diff);
            return new EngineerSniperWaveGenerator(diff);
        }
    }
}

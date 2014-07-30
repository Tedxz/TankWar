package xz.tankwar.module;

import java.io.*;
import java.util.List;

import xz.tankwar.component.tank.ComTank;
import xz.tankwar.component.tank.PlayerTank;
import xz.tankwar.component.weapon.Explosion;
import xz.tankwar.component.weapon.Weapon;
import xz.tankwar.module.tankgenerator.AbstractWaveTankGenerator;
import static xz.tankwar.module.PropertiesManager.*;

public class ArchiveManager {
    
    static ObjectOutputStream saveStream = null;
    static ObjectInputStream loadStream = null;
    static Archive archive = null;
    
    public static boolean readArchive() {
        boolean success = false;
//        ConsoleWindow.println("Reading archive from disk...");
        try {
            loadStream = new ObjectInputStream(
                    new BufferedInputStream(
                    new FileInputStream(SAVE_FILE_NAME)));
            archive = (Archive)loadStream.readObject();
            loadStream.close();
//            ConsoleWindow.println("Archive has been read.");
            success = true;
        } catch (FileNotFoundException e) {
            ConsoleWindow.println("(File not found)");
        } catch (InvalidClassException e) {
            ConsoleWindow.println("(Version error)");
        } catch (Exception e) {
            ExceptionManager.handleException(e);
//            ConsoleWindow.println("Failed.");
        } finally {
            try {
                if (loadStream != null)
                    loadStream.close();
            } catch (IOException e) {
                ExceptionManager.handleException(e);
            }
        }
        return success;
    }
    
    public static boolean writeArchive() {
//        ConsoleWindow.println("Writing archive to disk...");
        if (archive == null) {
            ConsoleWindow.println("Null archive.");
            return false;
        }
        try {
            saveStream = new ObjectOutputStream(
                    new BufferedOutputStream(
                    new FileOutputStream(SAVE_FILE_NAME)));
            saveStream.writeObject(archive);
            saveStream.close();
//            ConsoleWindow.println("Archive has been written.");
            return true;
        } catch (Exception e) {
            ExceptionManager.handleException(e);
            return false;
        } finally {
            try {
                saveStream.close();
            } catch (IOException e) {
                ExceptionManager.handleException(e);
            }
        }

    }
    
    public static void saveArchive() {
//        ConsoleWindow.println("Saving...");
        archive.save();
//        ConsoleWindow.println("Done.");
    }
    
    public static void loadArchive() {
//        ConsoleWindow.println("Loading...");
        archive.load();
//        ConsoleWindow.println("Done.");
    }
    
    public static void saveGame() {
        if (MainWindow.stat == MainWindow.STAT_GAME ||
                MainWindow.stat == MainWindow.STAT_PAUSE) {
            archive = new Archive();
            saveArchive();
            if (!writeArchive()) {
                archive = null;
                ConsoleWindow.println("Failed to save.");
            }
            archive = null;
            ConsoleWindow.println("Saved.");
        }
    }
    
    public static void loadGame() {
        MainWindow.stat = MainWindow.STAT_GAME;
        if (!readArchive()) {
            ConsoleWindow.println("Failed to load.");
            return;
        }
        loadArchive();
        archive = null;
        ConsoleWindow.println("Loaded.");
    }
    
    private static class Archive implements Serializable {
        private String ID;
        private boolean valid;
        private PlayerTank myTank = null;
        private List<ComTank> tanks = null;
        private List<ComTank> friends = null;
        private List<Weapon> weapons = null;
        private List<Weapon> supplies = null;
        private List<Weapon> explosions = null;
        private int killed = 0;
        private int waveNum = 0;
        private int freezed = 0;
        private int shake = 0;
        private AbstractWaveTankGenerator waveGen = null;

        public Archive() {
        }
        
        void save() {
            ID = MainWindow.gameID;
            myTank = MainWindow.myTank;
            tanks = MainWindow.tanks;
            friends = MainWindow.friends;
            weapons = MainWindow.weapons;
            supplies = MainWindow.supplies;
            explosions = MainWindow.explosions;
            killed = MainWindow.getKilled();
            waveNum = MainWindow.getWaveNum();
            freezed = MainWindow.freezed;
            shake = MainWindow.shake;
            waveGen = MainWindow.waveGen;
            valid = MainWindow.valid;
        }
        
        void load() {
            MainWindow.gameID = ID;
            MainWindow.myTank = myTank;
            MainWindow.tanks = tanks;
            MainWindow.friends = friends;
            MainWindow.weapons = weapons;
            MainWindow.supplies = supplies;
            MainWindow.explosions = explosions;
            MainWindow.setKilled(killed);
            MainWindow.setWaveNum(waveNum);
            MainWindow.freezed = freezed;
            MainWindow.shake = shake;
            MainWindow.waveGen = waveGen;
            MainWindow.stat = MainWindow.STAT_GAME;
            MainWindow.valid = valid;
        }
        
    }

}

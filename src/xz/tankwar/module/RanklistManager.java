package xz.tankwar.module;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import static xz.tankwar.module.PropertiesManager.*;

public class RanklistManager {
    public static Ranklist ranklist;
    static ObjectOutputStream saveStream = null;
    static ObjectInputStream loadStream = null;

    static {
        loadRanklist();
    }
    
    public static void loadRanklist() {
        try {
            loadStream = new ObjectInputStream(
                    new BufferedInputStream(
                    new FileInputStream(RANKLIST_FILE_NAME)));
            ranklist = (Ranklist)loadStream.readObject();
            loadStream.close();
        } catch (Exception e) {
            ranklist = new Ranklist();
//            ConsoleWindow.println("Cannot load ranklist.");
            return;
        }
//        ConsoleWindow.println("Ranklist loaded.");
    }

    public static void saveRanklist() {
        try {
            saveStream = new ObjectOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(RANKLIST_FILE_NAME)));
            saveStream.writeObject(ranklist);
            saveStream.close();
        } catch (Exception e) {
//            ConsoleWindow.println("Cannot save ranklist.");
            return;
        }
//        ConsoleWindow.println("Ranklist saved.");
    }

    public static void clearRanklist() {
        ranklist = new Ranklist();
        saveRanklist();
    }
    
    public static void insertRecord(String name, int k, String id) {
        ranklist.insert(name, k, id);
        saveRanklist();
    }

    public static class Ranklist implements Serializable {
        public static final int RANKLIST_SIZE = 11;
        private List<RanklistRecord> list = new ArrayList<RanklistRecord>();

        private Ranklist() {
        }

        public void insert(String name, int k,String id) {
            list.add(list.size(), new RanklistRecord(name, k, id));
            Collections.sort(list, RanklistReccordComparator.getInstance());
            while (list.size() > RANKLIST_SIZE)
                list.remove(RANKLIST_SIZE);
        }

        public RanklistRecord getRecord(int index) {
            return list.get(index);
        }
        
        public int size() {
            return list.size();
        }
        
        public static class RanklistReccordComparator implements
                Comparator<RanklistRecord> {
            private static RanklistReccordComparator ranklistReccordComparator =
                    new RanklistReccordComparator();

            private RanklistReccordComparator() {
            };

            public static RanklistReccordComparator getInstance() {
                return ranklistReccordComparator;
            }

            public int compare(RanklistRecord arg0, RanklistRecord arg1) {
                if (arg0.killed > arg1.killed)
                    return -1;
                if (arg0.killed < arg1.killed)
                    return 1;
                return 0;
            }
        }
    }

    public static class RanklistRecord implements Serializable {
        String name;
        long killed;
        Date time;
        String ID;

        RanklistRecord(String name, long killed, String id) {
            this.name = name;
            this.killed = killed;
            time = new Date(System.currentTimeMillis());
            ID = id;
        }
    }

}

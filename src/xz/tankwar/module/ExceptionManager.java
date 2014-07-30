package xz.tankwar.module;

import java.io.*;
import java.text.*;
import java.util.*;

import static xz.tankwar.module.PropertiesManager.*;

public class ExceptionManager {
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz") ;
    
    static {
        PrintStream stderr = null;
        try {
            stderr = new PrintStream(new FileOutputStream("./TankWar_error.log", false));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        System.setErr(stderr);
    }
    
    public static void sampleException() {
        try {
            throw new Exception("Sample Exception");
        } catch (Exception e) {
            ExceptionManager.handleException(e);
        }
    }
    
    public static void handleException(Exception e) {
        printException(e);
    }
    
    private static void printException(Exception e) {
        
        if (DEBUG)
            e.printStackTrace(System.out);
        
        Date time = new Date(System.currentTimeMillis());
        
        System.err.println(VERSION);
        System.err.println(DATE_FORMAT.format(time));
//        System.err.println(Calendar.getInstance());
        System.err.println();
        e.printStackTrace();
        System.err.println("-----------------------------------------------------");
        System.err.flush();
    }

}

package xz.tankwar.module;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import xz.tankwar.component.*;
import xz.tankwar.component.tank.*;
import static xz.tankwar.module.PropertiesManager.*;

public class ConsoleWindow extends Frame {
    public static final int X = 1020;
    public static final int Y = 70;
    public static final int CONSOLE_WIDTH = 250;
    public static final int CONSOLE_HEIGHT = 420;
    
    private static TextArea informationArea = new TextArea("", 0, 0, TextArea.SCROLLBARS_NONE);
    private static TextArea consoleArea = new TextArea("", 0, 0, TextArea.SCROLLBARS_NONE);
    
    
    public static final ConsoleWindow console = new ConsoleWindow();
    
    private ConsoleWindow() {
        super();
        setTitle(CONSOLE_TITLE);
        setBounds(X, Y, CONSOLE_WIDTH, CONSOLE_HEIGHT);
        setFont(CONSOLE_FONT);
        setResizable(false);
        setFocusable(false);
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                MainWindow.showConsole = false;
                setVisible(false);
            }
            
            @Override
            public void windowActivated(WindowEvent arg0) {
                MainWindow.MW.toFront();
            }

            public void windowDeiconified(WindowEvent e) {
                MainWindow.MW.setState(NORMAL);
            }

        });
        
        informationArea.setEditable(false);
        informationArea.setBackground(Color.white);
        consoleArea.setEditable(false);
        consoleArea.setBackground(Color.white);
        
        setLayout(new GridLayout(2, 1));
        add(informationArea);
        add(consoleArea);
        updateInformation();
        
        setVisible(MainWindow.showConsole);
        println(TITLE);
        println("\t" + VERSION);
        printsplit();

    }
    
    public static void updateInformation() {
        StringBuffer inform = new StringBuffer("--------Information Panel---------\n\n");
        inform.append(" Player Name : " + playerName + "\n");
        if (MainWindow.myTank != null && MainWindow.stat != MainWindow.STAT_START) {
            inform.append(String.format(" My HP : %4d     ", MainWindow.myTank.getHP()));
            inform.append(String.format("My MP : %4d\n", MainWindow.myTank.getMP()));
            
            inform.append(" Move   Direction :  " + MainWindow.myTank.getMoveDir() + "\n");
            inform.append(" Cannon Direction :  " + MainWindow.myTank.getCannonDir() + "\n");
            inform.append(" Shoot  Direction :  " + MainWindow.myTank.getShootDir() + "\n");
        } else {
            inform.append(" My HP : ----     My MP : ----\n");
            inform.append(" Move   Direction :  - - - - -\n");
            inform.append(" Cannon Direction :  - - - - -\n");
            inform.append(" Shoot  Direction :  - - - - -\n");
        }
        
        inform.append(String.format("\n Enemies Killed : %d", MainWindow.getKilled()));
        inform.append(String.format("\n Number of Wave : %d\n\n", MainWindow.getWaveNum()));
        long i;
        if ((i = MainWindow.getRefreshInterval()) < 1000) 
            inform.append(String.format(" Refresh Interval : %3d ms\n", i));
        else
            inform.append(String.format(" Refresh Interval : INF ms\n"));
        if (MainWindow.myTank != null && MainWindow.stat != MainWindow.STAT_START)
            inform.append(String.format("\n [I%2d] [M%2d] [A%2d] [S%2d] ",
                    (MainWindow.myTank.getCrazyTime()     + 49) / 50,
                    (MainWindow.myTank.getEnergeticTime() + 49) / 50,
                    (MainWindow.myTank.getAccTime()       + 49) / 50,
                    (MainWindow.myTank.getInvisibleTime() + 49) / 50));
        else
            inform.append("\n [I--] [M--] [A--] [S--] ");
        switch (MainWindow.stat){
            case MainWindow.STAT_GAME:
                inform.append(" [Game ]");
                break;
            case MainWindow.STAT_START:
                inform.append(" [Start]");
                break;
            case MainWindow.STAT_PAUSE:
                inform.append(" [Pause]");
                break;
            case MainWindow.STAT_HELP:
                inform.append(" [Help ]");
                break;
            case MainWindow.STAT_OVER:
                inform.append(" [Over ]");
                break;
            case MainWindow.STAT_RANKLIST:
                inform.append(" [Rank ]");
                break;
        }

        informationArea.setText(inform.toString());
    }
    
    public static void print(String s) {
        consoleArea.append(s);
    }
    public static void println(String s) {
        consoleArea.append(s + "\n");
    }
    public static void printsplit() {
        consoleArea.append("----------------------------------\n");
    }
    
}

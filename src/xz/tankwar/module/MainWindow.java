package xz.tankwar.module;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import static java.awt.Color.*;

import javax.xml.crypto.Data;

import xz.tankwar.component.*;
import xz.tankwar.component.supply.*;
import xz.tankwar.component.tank.*;
import xz.tankwar.component.weapon.*;
import xz.tankwar.module.RanklistManager.Ranklist;
import xz.tankwar.module.tankgenerator.AbstractWaveTankGenerator;
import xz.tankwar.module.tankgenerator.WaveGeneratorManager;
import static xz.tankwar.component.Direction.*;
import static xz.tankwar.module.PropertiesManager.*;
import static xz.tankwar.component.tank.ComTank.ComTankType.*;
import static java.awt.event.KeyEvent.*;

public class MainWindow extends Frame {

    /* Display */
    public static final int X = 200, Y = 70;
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    public static final int REFRESH_INTERVAL = 20;
    public static final int SHAKE_RANGE = 20;

    public static final int ENEMY_TANK_LIMIT = 12;
    public static final int FRIEND_TANK_LIMIT = 3;

    public static final int STAT_GAME = 0;
    public static final int STAT_START = 1;
    public static final int STAT_HELP = 2;
    public static final int STAT_PAUSE = 3;
    public static final int STAT_OVER = 4;
    public static final int STAT_RANKLIST = 5;
    

    /* Component Area */
    public static boolean archiveAvalible = false;
    public static boolean showConsole = true;

    public static int stat = 0;

    public static PlayerTank myTank = null;
    public static List<ComTank> tanks = null;
    public static List<ComTank> friends = null;
    public static List<Weapon> weapons = null;
    public static List<Weapon> supplies = null;
    public static List<Weapon> explosions = null;


    /* Threads */
    static Thread gameRunThread = null;
    public static AbstractWaveTankGenerator waveGen = null; 


    /* Statistics */
    private static int killed;
    private static int waveNum;
    private static Integer killedLock = 0;
    static String gameID;
    static boolean valid = true;

    /* Others */
    public static ConsoleWindow Console = ConsoleWindow.console;
    public static final MainWindow MW = new MainWindow();
    
    Random random = new Random();
    public static int freezed = 0;
    public static int shake = 0;
    static int HPreg = 2; /* 20 per second */
    static int MPreg = 3; /* 30 per second */

    /* Constructor */
    private MainWindow() {
        
        setTitle(TITLE + "   " + VERSION);
        setBounds(X, Y, WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);
        enableInputMethods(false);
        
        addWindowListener(new MainWindowAdapter());
        addKeyListener(new MainKeyAdapter());
        
        //------------------------------------------
        gameRestart();
        
        stat = STAT_START;
        synchronized (friends) {
            friends.add(new ComTank(FAKE_PLAYER, 2));
            friends.add(new ComTank(FRIEND));
            friends.add(new ComTank(FRIEND));
        }
        synchronized (tanks) {
            tanks.add(new ComTank(ENEMY));
            tanks.add(new ComTank(ENEMY));
            tanks.add(new ComTank(ENEMY));
        }
        stat = STAT_START;
        //------------------------------------------
        
        newThreads();
        startThreads();
        
        Console.setVisible(true);
        setVisible(true);
        archiveAvalible = ArchiveManager.readArchive();
//        try {
//            Runtime.getRuntime().exec("notepad xz/tankwar/component/Automatic.*");
//        } catch (IOException e) {
//            ExceptionManager.handleException(e);
//        }
    }

    /* Main */
    public static void main(String args[]) {
        if (args.length != 0)
            if (args[0].startsWith("-")) {
                if (args[0].contains("s"))
                    ENABLE_SAVE = true;
                if (args[0].contains("d"))
                    DEBUG = true;
                if (args[0].contains("c"));
                if (args[0].contains("r"))
                    RanklistManager.clearRanklist();
            }
    }

    /* Game Control Methods */
    public static void addKilled() {
        if (stat != STAT_GAME)
            return;
        synchronized (killedLock) {
            ++killed;
            if (killed == 1000)
                myTank.speak("Thousand Killed.");
            myTank.hintNewSkill(killed);
        }
    }

    public static int getKilled() {
        return killed;
    }
    public static void setKilled(int k) {
        killed = k;
    }

    public static int getWaveNum() {
        return waveNum;
    }

    public static void setWaveNum(int waveNum) {
        MainWindow.waveNum = waveNum;
    }

    public static void gameOver() {
        stat = STAT_OVER;
    }

    public static void gameRestart() {
        valid = !DEBUG;
        myTank = new PlayerTank();
        tanks = new ArrayList<ComTank>();
        friends = new ArrayList<ComTank>();
        weapons = new ArrayList<Weapon>();
        supplies = new ArrayList<Weapon>();
        explosions = new LinkedList<Weapon>();
        stat = STAT_GAME;
        freezed = 0;
        gameID = VERSION + 
                playerName + "," + 
                System.currentTimeMillis() + "," + 
                myTank.hashCode();
        if (!DEBUG) {
            killed = 0;
            waveNum = 0;
        }
    }

    void gamePause() {
        if (stat != STAT_GAME)
            return;
        gameRunThread = null;
        stat = STAT_PAUSE;
        repaint();
    }

    void gameResume() {
        stat = STAT_GAME;
        newThreads();
        startThreads();
    }
    
    private void newThreads() {
        gameRunThread = new GameRunThread();

    }
    
    private void startThreads() {
        gameRunThread.start();
    }
    
    /* Paint Methods */
    private Image offScreenImage = null;

    private void paintGame(Graphics g) throws NullPointerException {
        /* Paint Background */
        if (freezed == 0)
            g.setColor(lightYellow);
        else
            g.setColor(iceBlue);
        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        if (myTank.getSK() == 0 && myTank.getMP() > 20 && !myTank.isEnergetic()) {
            int x = myTank.getX(), y = myTank.getY();
            g.setColor(white);
            g.fillOval(x - 40, y - 40, 80, 80);
            g.setColor(lightYellow);
            g.fillOval(x - 36, y - 36, 72, 72);
            g.setColor(white);
            g.fillPolygon(new int[] { x, x - 31, x + 31 },
                    new int[] { y - 36, y + 18, y + 18 }, 3);
            g.fillPolygon(new int[] { x, x - 31, x + 31 },
                    new int[] { y + 36, y - 18, y - 18 }, 3);
        }

        /* Paint Weapons */
        synchronized (weapons) {
            for (int i = 0; i < weapons.size(); ++i)
                weapons.get(i).draw(g);
        }

        /* Paint Supplies */
        synchronized (supplies) {
            for (int i = 0; i < supplies.size(); ++i)
                supplies.get(i).draw(g);
        }

        /* Paint Tanks */
        synchronized (tanks) {
            for (int i = 0; i < tanks.size(); ++i)
                tanks.get(i).draw(g);
        }
        synchronized (friends) {
            for (int i = 0; i < friends.size(); ++i)
                friends.get(i).draw(g);
        }

        /* Paint Explosions */
        synchronized (explosions) {
            for (int i = 0; i < explosions.size(); ++i) {
                Weapon e = explosions.get(i);
                e.draw(g);
                if (!e.isAlive())
                    explosions.remove(e);
            }
        }

        if (stat == STAT_START)
            return;

        /* Paint My Tank */
        if (stat != STAT_START)
            myTank.draw(g);

        /* Paint Dialog */
        synchronized (tanks) {
            for (int i = 0; i < tanks.size(); ++i)
                tanks.get(i).drawDialog(g);
        }
        synchronized (friends) {
        for (int i = 0; i < friends.size(); ++i)
            friends.get(i).drawDialog(g);
        }

        myTank.drawDialog(g);

        /* Paint Energy Bar */
        if (stat != STAT_START)
            myTank.drawEnergyBar(g);

        /* Paint Debug */
        g.setFont(DIALOG_FONT);
        if (DEBUG)
            g.drawString("DEBUG MODE", 710, 590);
        
        
        /* Paint Information */
        if (!showConsole) {
            g.setFont(DIALOG_FONT);
            g.drawString("My Tank HP : " + myTank.getHP(), 7, 40);
            g.drawString("My Tank MP : " + myTank.getMP(), 7, 60);
        }

        g.drawString("Killed : " + killed, 720, 40);
    }

    private void paintHelp(Graphics g) {
        int x = 127, y = 460;
        g.setColor(blue);
        g.setFont(SUBTITLE_FONT);
        drawMiddleAlignedString(g, "HELP", WINDOW_WIDTH / 2, 120);
        g.setColor(black);
        g.setFont(HELP_FONT);
        g.drawString("  W ", x, y);
        g.drawString("A Q D", x, y + 20);
        g.drawString("  S", x, y + 40);
        g.setFont(HELP_FONT);
        g.drawString("Fire Direction", x - 60, y + 70);
        x = 623;
        y = 460;
        g.setFont(ARROW_FONT);
        g.drawString("¡¡¡ü", x, y);
        g.drawString("¡û¡¡¡ú", x, y + 20);
        g.drawString("¡¡¡ý", x, y + 40);
        g.setFont(HELP_FONT);
        g.drawString("Move Direction", x - 50, y + 70);

        g.setFont(HELP_FONT);

        x = 110;
        y = 160;
        g.drawString("   <ESC> Pause    <F1> Help     <F2> Restart", x, y);
        g.drawString("Skill 0  <KEY ~> : Healing           (??  MP)", x,
                y += 60);
        g.drawString("Skill 1  <KEY 1> : Summon Enemy      (0   MP)", x,
                y += 20);
        if (myTank == null || PlayerTank.SKILL_LIMIT[2] > killed)
            g.drawString("Skill 2  <KEY ?> :                   (--- MP)", x,
                    y += 20);
        else
            g.drawString("Skill 2  <KEY 2> : Set Mine          (50  MP)", x,
                    y += 20);
        if (myTank == null || PlayerTank.SKILL_LIMIT[3] > killed)
            g.drawString("Skill 3  <KEY ?> :                   (--- MP)", x,
                    y += 20);
        else
            g.drawString("Skill 3  <KEY 3> : Summon Friend     (400 MP)", x,
                    y += 20);
        if (myTank == null || PlayerTank.SKILL_LIMIT[4] > killed)
            g.drawString("Skill 4  <KEY ?> :                   (--- MP)", x,
                    y += 20);
        else
            g.drawString("Skill 4  <KEY E> : Octopus Cannon    (50  MP)", x,
                    y += 20);
        if (myTank == null || PlayerTank.SKILL_LIMIT[5] > killed)
            g.drawString("Skill 5  <KEY ?> :                   (--- MP)", x,
                    y += 20);
        else
            g.drawString("Skill 5  <KEY F> : Dash              (30  MP)", x,
                    y += 20);
        if (myTank == null || PlayerTank.SKILL_LIMIT[6] > killed)
            g.drawString("Skill 6  <KEY ?> :                   (--- MP)", x,
                    y += 20);
        else
            g.drawString("Skill 6  <KEY X> : Stealth           (400 MP)", x,
                    y += 20);
        if (myTank == null || PlayerTank.SKILL_LIMIT[7] > killed)
            g.drawString("Skill 7  <KEY ?> :                   (--- MP)", x,
                    y += 20);
        else
            g.drawString("Skill 7  <KEY R> : Line Shoot        (45  MP)", x,
                    y += 20);
        if (myTank == null || PlayerTank.SKILL_LIMIT[8] > killed)
            g.drawString("Skill 8  <KEY ?> :                   (--- HP)", x,
                    y += 20);
        else
            g.drawString("Skill 8  <KEY C> : Big Explosion     (200 HP)", x,
                    y += 20);
        if (myTank == null || PlayerTank.SKILL_LIMIT[9] > killed)
            g.drawString("Skill 9  <KEY ?> :                   (--- MP)", x,
                    y += 20);
        else
            g.drawString("Skill 9  <KEY Z> : Ice Age           (250 MP)", x,
                    y += 20);
        if (myTank == null || PlayerTank.SKILL_LIMIT[10] > killed)
            g.drawString("Skill 10 <KEY ?> :                   (--- MP)", x,
                    y += 20);
        else
            g.drawString("Skill 10 <KEY V> : Earthquake        (??? MP)", x,
                    y += 20);

    }

    private void paintOver(Graphics g) {
        g.setFont(SUBTITLE_FONT);
        g.setColor(black);
        drawMiddleAlignedString(g, "GAME OVER", WINDOW_WIDTH / 2, 270);
        g.setFont(HELP_FONT);
        drawMiddleAlignedString(g, "Press <ENTER> to continue", WINDOW_WIDTH / 2, 320);
        drawMiddleAlignedString(g, "Enemy Killed : " + killed, WINDOW_WIDTH / 2, 380);
        g.setFont(NAME_FONT);
        drawMiddleAlignedString(g, playerName, WINDOW_WIDTH / 2, 360);
    }

    private void paintRanklist(Graphics g) {
        g.setFont(SUBTITLE_FONT);
        g.setColor(green);
        drawMiddleAlignedString(g, "RANKLIST", WINDOW_WIDTH / 2, 110);
        g.setFont(SUBTITLE_FONT);
        g.setFont(HELP_FONT);
        drawMiddleAlignedString(g, VERSION, WINDOW_WIDTH / 2, 140);
        g.setColor(transparentWhite);
        for (int i = 0; i < Ranklist.RANKLIST_SIZE; ++i) {
            g.fillRoundRect(160, 170 + i * 33, 480, 23, 10, 10);
        }
        for (int i = 0; i < RanklistManager.ranklist.size(); ++i) {
            RanklistManager.RanklistRecord r = RanklistManager.ranklist.getRecord(i);
            if (r.ID == gameID)
                g.setColor(red);
            else
                g.setColor(black);
            g.setFont(NAME_FONT);
            drawMiddleAlignedString(g, r.name, 290, 190 + i * 33);
            g.setFont(HELP_FONT);
            drawMiddleAlignedString(g, "" + r.killed, 550, 190 + i * 33);

        }
        g.setColor(black);
        g.setFont(HELP_FONT);
        drawMiddleAlignedString(g, "Press <ENTER> to restart", WINDOW_WIDTH / 2, 565);
        g.setFont(DIALOG_FONT);
        g.drawString("Press <F3> to change name.", 20, WINDOW_HEIGHT - 25);
    }

    private void paintPause(Graphics g) {
        gameRunThread = new GameRunThread();
        g.setColor(blue);
        g.setFont(SUBTITLE_FONT);
        drawMiddleAlignedString(g, "PAUSED", WINDOW_WIDTH / 2, 290);
        g.setFont(HELP_FONT);
        drawMiddleAlignedString(g, "Press <ENTER> To Continue", WINDOW_WIDTH / 2, 335);
    }

    private void paintStart(Graphics g) {
        g.setFont(TITLE_FONT);
        g.setColor(white);
        g.fillRoundRect(20, 40, 630, 150, 20, 20);
        g.setColor(green);
        g.drawString("Tank", 40, 150);
        g.setColor(red);
        g.drawString("     War", 40, 150);
        g.setColor(blue);
        g.setFont(HELP_FONT);
        g.drawString(VERSION, 520, 220);
        g.setColor(black);
        g.drawString("Hello, ", 30, 270);
        g.setFont(NAME_FONT);
        g.drawString(playerName + "!", 110, 270);
        g.setFont(HELP_FONT);
        g.drawString("-- by XieZheng", 600, 570);
        drawMiddleAlignedString(g, "Press <ENTER> To Start Game", WINDOW_WIDTH / 2, 430);
        if (archiveAvalible)
            drawMiddleAlignedString(g, "Press <F12> To Load Game", WINDOW_WIDTH / 2, 460);
        g.setFont(DIALOG_FONT);
        g.drawString("(Press <F3> to change)", 115 + g.getFontMetrics(NAME_FONT).stringWidth(playerName + "!"), 270);
    }

    private void drawMiddleAlignedString(Graphics g, String s, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(s, x - fm.stringWidth(s) / 2, y);
    }
    
    public void paint(Graphics g) {
        ConsoleWindow.updateInformation();
        paintGame(g);
        switch (stat) {
            case STAT_HELP:
                paintHelp(g);
                break;
            case STAT_START:
                paintStart(g);
                break;
            case STAT_PAUSE:
                paintPause(g);
                break;
            case STAT_OVER:
                paintOver(g);
                break;
            case STAT_RANKLIST:
                paintRanklist(g);
                break;
        }
    }

    public void update(Graphics g) {
        if (offScreenImage == null)
            offScreenImage = createImage(WINDOW_WIDTH, WINDOW_HEIGHT);
        Graphics gOffScreenImage = offScreenImage.getGraphics();
        paint(gOffScreenImage);
        if (shake == 0)
            g.drawImage(offScreenImage, 0, 0, null);
        else {
            g.drawImage(offScreenImage, random.nextInt(SHAKE_RANGE * 2)
                    - SHAKE_RANGE,
                    random.nextInt(SHAKE_RANGE * 2) - SHAKE_RANGE, null);
            --shake;
        }
    }

    /* Monitor Function */
    
    public static long getRefreshInterval() {
        if (MW == null || MW.gameRunThread == null)
            return 0;
        return ((GameRunThread)MW.gameRunThread).getMillisRefreshInterval();
    }

    /* Threads */

    private class GameRunThread extends Thread {
        private long threadStartTime = 0;
        private Long millisRefreshTime = 0l;
        private Long millisRefreshInterval = 0l;
        private long loopCount = 0L;
        
        public long getMillisRefreshInterval() {
            return millisRefreshInterval;
        }
        
        private void actAll(List c) {
            for (int i = 0; i < c.size(); ++i) {
                if (!((Automatic)(c.get(i))).isAlive())
                    c.remove(i);
                else
                    ((Automatic)(c.get(i))).autoAct();
            }
        }
        
        public void run() {
            threadStartTime = System.currentTimeMillis();
            setPriority(MAX_PRIORITY);
            while (MainWindow.gameRunThread == this) {
                if (threadStartTime + loopCount * REFRESH_INTERVAL < System.currentTimeMillis()) {
                    ++loopCount;
                    millisRefreshInterval = System.currentTimeMillis() - millisRefreshTime;
                    millisRefreshTime = System.currentTimeMillis();
                    // GameRun
                    myTank.move();
                    if (freezed > 0)
                        --freezed;
                    actAll(friends);
                    actAll(tanks);
                    actAll(supplies);
                    actAll(weapons);
                    actAll(explosions);
                    // Launch
                    if (loopCount % 5 == 0) {
                        myTank.modifyHP(HPreg);
                        myTank.modifyMP(MPreg);
                        myTank.useSkill();
                        if (myTank.getShootDir() != STOP) {
                            myTank.launch();
                        }
                    }
                    // Gen
                    if (stat == STAT_GAME && loopCount % 50 == 7 && !DEBUG)
                        if (!((waveGen == null || !waveGen.hasNext()) && (tanks.size() > 0))) {
                            if (waveGen == null || !waveGen.hasNext()) {
                                waveGen = WaveGeneratorManager.getWaveGen(killed);
                                
                            }
                            if (tanks.size() <= ENEMY_TANK_LIMIT) {
                                synchronized (tanks) {
                                    tanks.add(waveGen.next());
                                }
                                if (random.nextInt(100) < 3 && friends.size() <= FRIEND_TANK_LIMIT) {
                                    synchronized (friends) {
                                        friends.add(new ComTank(ComTank.ComTankType.FRIEND));
                                    }
                                }
                            }
                            if (!waveGen.hasNext()) {
                                ++waveNum;
                                waveGen = null; 
                            }
                        }
    
                    // Repaint
                    repaint();
                } else {
                    try {
                        sleep(5);
                    } catch (InterruptedException e) {
                        ExceptionManager.handleException(e);
                    }
                }
            }
        }
    }

    /* Listeners */

    private class MainWindowAdapter extends WindowAdapter {
        
        public void windowClosing(WindowEvent e) {
            setVisible(false);
            Console.setVisible(false);
            System.exit(0);
        }

        public void windowDeactivated(WindowEvent arg0) {
            if (stat == STAT_GAME)
                gamePause();
        }

        public void windowIconified(WindowEvent arg0) {
            Console.setState(ICONIFIED);
            if (stat == STAT_GAME)
                gamePause();

        }

        public void windowActivated(WindowEvent arg0) {
            super.windowActivated(arg0);
        }

        public void windowDeiconified(WindowEvent e) {
            Console.setState(NORMAL);
        }
    }

    private class MainKeyAdapter extends KeyAdapter {

        private long pressedW = 0;
        private long pressedA = 0;
        private long pressedS = 0;
        private long pressedD = 0;
        private long pressedQ = 0;
        private long pressedUp = 0;
        private long pressedDown = 0;
        private long pressedLeft = 0;
        private long pressedRight = 0;
        
        private void locateMoveDirection() {
            Direction dir = STOP;
            if (pressedUp > pressedDown)
                dir = compose(dir, UP);
            if (pressedUp < pressedDown)
                dir = compose(dir, DOWN);
            if (pressedLeft > pressedRight)
                dir = compose(dir, LEFT);
            if (pressedLeft < pressedRight)
                dir = compose(dir, RIGHT);
            myTank.setMoveDir(dir);
        }
        private void locateShootDirection() {
            if (pressedQ > pressedW && pressedQ > pressedA
                    && pressedQ > pressedS && pressedQ > pressedD) {
                myTank.setShootDir(null);
                myTank.setCannonDir(myTank.getMoveDir());
                return;
            }
            Direction dir = STOP;
            if (pressedW > pressedS)
                dir = compose(dir, UP);
            if (pressedW < pressedS)
                dir = compose(dir, DOWN);
            if (pressedA > pressedD)
                dir = compose(dir, LEFT);
            if (pressedA < pressedD)
                dir = compose(dir, RIGHT);
            myTank.setShootDir(dir);
        }
        
        private void resetKey() {
            pressedW = 0;
            pressedA = 0;
            pressedS = 0;
            pressedD = 0;
            pressedQ = 0;
            pressedUp = 0;
            pressedDown = 0;
            pressedLeft = 0;
            pressedRight = 0;
            myTank.setShootDir(STOP);
            myTank.setMoveDir(STOP);
        }

        private void helpingKeyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case VK_SPACE:
                case VK_ENTER:
                case VK_F1:
                    stat = STAT_GAME;
                    resetKey();
                    gameResume();
                    break;
                case VK_F4:
                    showConsole = !showConsole;
                    ConsoleWindow.console.setVisible(showConsole);
                    break;
            }

        }

        private void pausingKeyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case VK_ENTER:
                case VK_ESCAPE:
                case VK_P:
                    resetKey();
                    gameResume();
                    break;
                case VK_F4:
                    showConsole = !showConsole;
                    ConsoleWindow.console.setVisible(showConsole);
                    break;
                case VK_F11:
                    if (ENABLE_SAVE)
                        ArchiveManager.saveGame();
                    break;
                case VK_F12:
                    if (ENABLE_SAVE) {
                        ArchiveManager.loadGame();
                        resetKey();
                        gameResume();
                    }
                    break;
            }
        }

        private void startingKeyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case VK_F1:
                    stat = STAT_HELP;
                    break;
                case VK_F2: /* Restart */
                    stat = STAT_GAME;
                    resetKey();
                    gameRestart();
                    break;
                case VK_F4:
                    showConsole = !showConsole;
                    ConsoleWindow.console.setVisible(showConsole);
                    break;
                case VK_ENTER:
                    stat = STAT_HELP;
                    gameRestart();
                    stat = STAT_HELP;
                    break;
                case VK_F12:
                    stat = STAT_GAME;
                    ArchiveManager.loadGame();
                    break;
                case VK_F3:
                    requestPlayerName();
                    break;
            }
        }

        private void debugKeyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case VK_4:
                    synchronized (MainWindow.tanks) {
                        MainWindow.tanks.add(new ComTank(ENEMY));
                    }
                    break;
                case VK_5:
                    synchronized (MainWindow.tanks) {
                        MainWindow.tanks.add(new ComTank(SHOOTER));
                    }
                    break;
                case VK_6:
                    synchronized (MainWindow.tanks) {
                        MainWindow.tanks.add(new ComTank(BOMBER));
                    }
                    break;
                case VK_7:
                    synchronized (MainWindow.friends) {
                        MainWindow.friends.add(new ComTank(IAMANORANGE));
                    }
                    break;
                case VK_8:
                    synchronized (MainWindow.tanks) {
                        MainWindow.tanks.add(new ComTank(ENGINEER));
                    }
                    break;
                case VK_9:
                    synchronized (MainWindow.tanks) {
                        MainWindow.tanks.add(new ComTank(SNIPER));
                    }
                    break;
                case VK_0:
                    synchronized (MainWindow.tanks) {
                        MainWindow.tanks.add(new ComTank(SOY_SAUCE));
                    }
                    break;
                case VK_MINUS:
                    killed += 99;
                    break;
                case VK_EQUALS:
                    myTank.modifyHP(1000);
                    myTank.modifyMP(1000);
                    break;

            }
        }

        private void gameKeyPressed(KeyEvent e) {
        	//ConsoleWindow.println("" + e.getKeyCode());
            switch (e.getKeyCode()) {
                case VK_F1: /* Help */
                    gamePause();
                    repaint();
                    stat = STAT_HELP;
                    break;
                case VK_F2: /* Restart */
                    //gameRestart();
                    break;
                case VK_F4:
                    showConsole = !showConsole;
                    ConsoleWindow.console.setVisible(showConsole);
                    break;
                case VK_ESCAPE:
                case VK_P:
                    gamePause();
                    break;
                case VK_UP:
                    pressedUp = System.currentTimeMillis();
                    break;
                case VK_DOWN:
                    pressedDown = System.currentTimeMillis();
                    break;
                case VK_LEFT:
                    pressedLeft = System.currentTimeMillis();
                    break;
                case VK_RIGHT:
                    pressedRight = System.currentTimeMillis();
                    break;
                case VK_W:
                    pressedW = System.currentTimeMillis();
                    break;
                case VK_S:
                    pressedS = System.currentTimeMillis();
                    break;
                case VK_A:
                    pressedA = System.currentTimeMillis();
                    break;
                case VK_D:
                    pressedD = System.currentTimeMillis();
                    break;
                case VK_Q:
                    pressedQ = System.currentTimeMillis();
                    break;
                case VK_BACK_QUOTE: /* Healing */
                    myTank.setSK(0);
                    break;
                case VK_1: /* Summon Enemy */
                    myTank.setSK(1);
                    break;
                case VK_2: /* Set Mine */
                    myTank.setSK(2);
                    break;
                case VK_3: /* Summon Friend */
                    myTank.setSK(3);
                    break;
                case VK_E: /* All Directions Shoot */
                    myTank.setSK(4);
                    break;
                case VK_F: /* Dash */
                    myTank.setSK(5);
                    break;
                case VK_X: /* Become Invisible */
                    myTank.setSK(6);
                    break;
                case VK_R: /* Line Shoot */
                    myTank.setSK(7);
                    break;
                case VK_C: /* Big Explosion */
                    myTank.setSK(8);
                    break;
                case VK_Z: /* Ice Age */
                    myTank.setSK(9);
                    break;
                case VK_V: /* Earthquake */
                    myTank.setSK(10);
                    break;
                case VK_SPACE: /* Long Distance Shoot */
                    if (DEBUG)
                        myTank.setSK(11);
                    break;
                case VK_F11:
                    if (ENABLE_SAVE)
                        ArchiveManager.saveGame();
                    break;
                case VK_F12:
                    if (ENABLE_SAVE)
                        ArchiveManager.loadGame();
                    break;
                default:
                    if (DEBUG)
                        debugKeyPressed(e);
                    break;
            }
            locateMoveDirection();
            locateShootDirection();

        }
        
        private void overKeyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case VK_ALT:
                case VK_CONTROL:
                    break;
                case VK_F4:
                    showConsole = !showConsole;
                    ConsoleWindow.console.setVisible(showConsole);
                    break;
                case VK_ENTER:
                    if (valid)
                        RanklistManager.insertRecord(playerName, killed, gameID);
                    stat = STAT_RANKLIST;
                    break;

            }
        }
        
        private void ranklistKeyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case VK_ALT:
                case VK_CONTROL:
                    break;
                case VK_F3:
                    requestPlayerName();
                    break;
                case VK_F4:
                    showConsole = !showConsole;
                    ConsoleWindow.console.setVisible(showConsole);
                    break;
                case VK_ENTER:
                    stat = STAT_GAME;
                    resetKey();
                    gameRestart();
                    break;
                default:
                    stat = STAT_GAME;
                    resetKey();
                    gameRestart();

            }
        }
        
        public void keyPressed(KeyEvent e) {
            switch (stat) {
                case STAT_GAME:
                    gameKeyPressed(e);
                    break;
                case STAT_HELP:
                    helpingKeyPressed(e);
                    break;
                case STAT_START:
                    startingKeyPressed(e);
                    break;
                case STAT_PAUSE:
                    pausingKeyPressed(e);
                    break;
                case STAT_OVER:
                    overKeyPressed(e);
                    break;
                case STAT_RANKLIST:
                    ranklistKeyPressed(e);
                    break;
            }
        }

        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case VK_UP:
                    pressedUp = 0;
                    break;
                case VK_DOWN:
                    pressedDown = 0;
                    break;
                case VK_LEFT:
                    pressedLeft = 0;
                    break;
                case VK_RIGHT:
                    pressedRight = 0;
                    break;
                case VK_W:
                    pressedW = 0;
                    break;
                case VK_S:
                    pressedS = 0;
                    break;
                case VK_A:
                    pressedA = 0;
                    break;
                case VK_D:
                    pressedD = 0;
                    break;
                case VK_Q:
                    pressedQ = 0;
                    break;
                case VK_BACK_QUOTE:
                case VK_1:
                case VK_2:
                case VK_3:
                case VK_E:
                case VK_R:
                case VK_F:
                case VK_C:
                case VK_X:
                case VK_Z:
                case VK_V:
                case VK_SPACE:
                    myTank.setSK(-1);
                    break;
            }
            locateMoveDirection();
            locateShootDirection();
        }
    }
}

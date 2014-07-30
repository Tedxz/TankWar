package xz.tankwar.module;

import java.awt.*;
import java.awt.event.*;
import java.util.Properties;

public class PropertiesManager {
    
    public static final String TITLE = "TankWar by XieZheng";
    public static final String VERSION = "Version 2.3.2";
    public static final String CONSOLE_TITLE = "TankWar Console";
    
    public static final Properties PROP = System.getProperties();
    public static final String OPERATING_SYSTEM = PROP.getProperty("os.name").toUpperCase();
    
    public static final String SAVE_FILE_NAME;
    public static final String RANKLIST_FILE_NAME;
    
    public static final int MAX_NAME_LENGTH = 15;
    public static String playerName = null;
    
    public static boolean DEBUG = false;
    public static boolean ENABLE_SAVE = true;
    
    public static final Font SUBTITLE_FONT;
    public static final Font TITLE_FONT;
    public static final Font ARROW_FONT;
    public static final Font DIALOG_FONT;
    public static final Font HELP_FONT;
    public static final Font CONSOLE_FONT;
    public static final Font NAME_FONT;
    public static final Font BUBBLE_FONT;
    
    public static final Color lightYellow       = new Color(255, 255, 150);
    public static final Color iceBlue           = new Color(200, 255, 255);
    public static final Color lightIceBlue      = new Color(230, 255, 255);
    public static final Color transparentWhite  = new Color(255, 255, 255, 90);
    public static final Color transparentGreen  = new Color(0, 255, 0, 20);
    public static final Color transGreen        = new Color(0, 255, 0, 80);
    public static final Color purple            = new Color(171, 157, 219);
    public static final Color darkPurple        = new Color(164, 73, 164);
    public static final Color lemon             = new Color(181, 230, 29);
    public static final Color blueGray          = new Color(112, 146, 190);
    public static final Color brown             = new Color(185, 122, 87);
    public static final Color darkBrown         = new Color(136, 0, 21);
    public static final Color lightGreen        = new Color(34, 177, 76);
    public static final Color transLightGreen   = new Color(34, 177, 76, 80);
    public static final Color whiteGreen        = new Color(180, 255, 180);
    public static final Color grayGreen         = new Color(34, 177, 76);
    public static final Color lightGrayGreen    = new Color(80, 190, 80);
    public static final Color darkWhiteGreen    = new Color(114, 255, 100);
    public static final Color gemBlue           = new Color(0, 162, 232);
    public static final Color lightGemBlue      = new Color(153, 217, 234);
    
    static {
        /* Player Name */
        playerName = PROP.getProperty("user.name");
            playerName = cut(playerName);

        /* Fonts */
        if (OPERATING_SYSTEM.contains("LINUX")) {
            NAME_FONT = new Font("Ubuntu Mono", Font.PLAIN, 24);
            SUBTITLE_FONT = new Font("Ubuntu Mono", Font.BOLD, 70);
            TITLE_FONT = new Font("Ubuntu Mono", Font.BOLD, 140);
            ARROW_FONT = new Font("Dialog", Font.PLAIN, 24);
            DIALOG_FONT = new Font("Dialog", Font.PLAIN, 12);
            HELP_FONT = new Font("Ubuntu Mono", Font.PLAIN, 24);
            CONSOLE_FONT = new Font("Ubuntu Mono", Font.PLAIN, 14);
            BUBBLE_FONT = new Font("Ubuntu Mono", Font.PLAIN, 14);
            SAVE_FILE_NAME = "./.tankwar_save.sav";
            RANKLIST_FILE_NAME = "./.tankwar_ranklist.bin";
        } else {
            if (OPERATING_SYSTEM.contains("WINDOWS")) {
                NAME_FONT = new Font("SansSerif", Font.PLAIN, 20);
                SUBTITLE_FONT = new Font("Courier New", Font.BOLD, 70);
                TITLE_FONT = new Font("Courier New", Font.BOLD, 120);
                ARROW_FONT = new Font("Dialog", Font.PLAIN, 20);
                DIALOG_FONT = new Font("Dialog", Font.PLAIN, 12);
                HELP_FONT = new Font("Courier New", Font.BOLD, 20);
                CONSOLE_FONT = new Font("Courier New", Font.PLAIN, 13);
                BUBBLE_FONT = new Font("Monospaced", Font.PLAIN, 12);
                SAVE_FILE_NAME = "./.tankwar_save.sav";
                RANKLIST_FILE_NAME = "./.tankwar_ranklist.bin";
            } else {
                NAME_FONT = new Font("Dialog", Font.PLAIN, 20);
                SUBTITLE_FONT = new Font("Monospaced", Font.BOLD, 70);
                TITLE_FONT = new Font("Monospaced", Font.BOLD, 120);
                ARROW_FONT = new Font("Dialog", Font.PLAIN, 20);
                DIALOG_FONT = new Font("Dialog", Font.PLAIN, 12);
                HELP_FONT = new Font("Monospaced", Font.BOLD, 20);
                CONSOLE_FONT = new Font("Monospaced", Font.PLAIN, 12);
                BUBBLE_FONT = new Font("Monospaced", Font.PLAIN, 12);
                SAVE_FILE_NAME = "./.tankwar_save.sav";
                RANKLIST_FILE_NAME = "./.tankwar_ranklist.bin";
            }
        }
    }
    
    private PropertiesManager() {}
    
    public static void requestPlayerName() {
        NameDialog.requestPlayerName();
    }
    
    private static String cut(String s) {
        while (s.getBytes().length > MAX_NAME_LENGTH) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
    
    public static class NameDialog extends Dialog {
        private static NameDialog uniqueNameDialog = new NameDialog();
        private TextField nameField = new TextField("", MAX_NAME_LENGTH);
        private Label hint = new Label("Input your name here:");
        private Button OKButton = new Button("OK!");

        private NameDialog() {
            super(MainWindow.MW, "Hello, what's your name?", true);
            setResizable(false);
            setLocation(MainWindow.MW.getX() + MainWindow.WINDOW_WIDTH / 2 - 125,
                    MainWindow.MW.getY() + MainWindow.WINDOW_HEIGHT / 2  - 50);
            setSize(250, 100);
            
            setLayout(new FlowLayout());
            add(hint);
            add(nameField);
            add(OKButton);
            
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    setVisible(false);
                }
            });
            
            nameField.addTextListener(new TextListener() {
                public void textValueChanged(TextEvent e) {
                    String ts = ((TextField)(e.getSource())).getText();
                    if (ts.getBytes().length > MAX_NAME_LENGTH) {
                        ts = cut(ts);
                        ((TextField)(e.getSource())).setText(ts);
                        ((TextField)(e.getSource())).setCaretPosition(ts.length());
                    }
                }
            });
            
            nameField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ((TextField)(e.getSource())).getParent().setVisible(false);
                    String ts = ((TextField)(e.getSource())).getText();
                    if (!ts.equals(""))
                        playerName = ts;
                }
            });
            OKButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ((Button)(e.getSource())).getParent().setVisible(false);

                    String ts = ((NameDialog)((Button)e.getSource()).getParent()).nameField.getText();
                    if (!ts.equals(""));
                        playerName = ts;
                }
            });
        }
        
        static void requestPlayerName() {
            uniqueNameDialog.nameField.setText(playerName);
            uniqueNameDialog.nameField.selectAll();
            uniqueNameDialog.setVisible(true);
        }
        
    }
    
}

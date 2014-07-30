package xz.tankwar.component.tank;

import java.awt.*;
import java.io.Serializable;

import static java.awt.Color.*;

import xz.tankwar.component.*;
import xz.tankwar.component.tank.Tank.*;
import xz.tankwar.component.weapon.*;
import xz.tankwar.module.ExceptionManager;
import xz.tankwar.module.MainWindow;
import xz.tankwar.module.tankgenerator.ShooterWaveGenerator;
import static xz.tankwar.component.Direction.*;
import static xz.tankwar.module.PropertiesManager.*;
import static xz.tankwar.component.tank.ComTank.ComTankType.*;

public class PlayerTank extends Tank {
    public int step = 6;
    private final Skill skillList[] = {
            new Healing(),
            new SummonEnemy(),
            new SetMine(),
            new SummonFriend(),
            new OctopusCannon(),
            new Dash(),
            new Stealth(),
            new LineShoot(),
            new BigExplosion(),
            new IceAge(),
            new Earthquake(),
            new LongDistanceShoot(),
    };
    public static final String[] SKILL_KEY =
    { "~", "1", "2", "3", "E", "F", "X", "R", "C", "Z", "V", " " };
    public static final int SKILL_LIMIT[];
    private final int lineShootUpdate1 = 1100;
    private final int lineShootUpdate2 = 2000;
    private final int dashUpdate = 1500;
    public long skillUse[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    private int sk = -1;
    protected int MP;
    protected int maxMP;
    protected int invisibleTime = 0;
    protected int energeticTime = 0;
    protected int crazyTime = 0;
    protected int accTime = 0;

    static {
        if (!DEBUG) {
            SKILL_LIMIT = new int[] { 0, 0, 20, 30, 60, 150, 300, 400, 550,
                    670, 900, 0 };
        } else {
            SKILL_LIMIT = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        }
    }

    /* Constructors */
    public PlayerTank() {
        super();
        clr1 = green;
        clr2 = grayGreen;
        maxHP = HP = 1000;
        maxMP = MP = 1000;
        x = 400;
        y = 633;
        setMoveLimit(UP, 30);
        dialog = new Dialog("Hello,\nI'm " + playerName + ".");

        if (MainWindow.stat == MainWindow.STAT_START) {
            x = -x;
            y = -y;
        }
        resetSkillUse();
    }

    public PlayerTank(int x, int y) {
        super();
        clr1 = green;
        clr2 = grayGreen;
        maxHP = HP = 1000;
        maxMP = MP = 1000;
        this.x = x;
        this.y = y;
        resetSkillUse();
    }

    public PlayerTank(PlayerTank t) {
        super();
        clr1 = green;
        clr2 = lightGreen;
        maxHP = HP = 1000;
        maxMP = MP = 1000;
        x = t.x;
        y = t.y;
        cannonDir = moveDir = t.cannonDir;
    }

    /* Skills */
    public void useSkill() {
        int s = sk;
        if (s == -1 || MP < skillList[s].getCost() || isLimited()
                || SKILL_LIMIT[s] > MainWindow.getKilled()) {
            stopSkill();
            return;
        }
        if (skillList[s].use()) {
            ++skillUse[s];
            if (!DEBUG)
                modifyMP(-skillList[s].getCost());
        }
    }

    /* Getters & Setters & ... */
    public boolean shootBlocked() {
        for (int i = 0; i < skillList.length; ++i)
            if (skillList[i] instanceof BlockShootSkill &&
                    ((BlockShootSkill)skillList[i]).isBlocking())
                return true;
        return false;
    }
    
    public boolean isInvisible() {
        return (invisibleTime != 0);
    }

    public boolean isCrazy() {
        return (crazyTime != 0 || sk == 0);
    }

    public boolean isAcc() {
        return (accTime != 0);
    }

    public boolean isEnergetic() {
        return (energeticTime != 0);
    }

    public void resetSkillUse() {
        skillUse = new long[SKILL_LIMIT.length];
    }

    public void hintNewSkill(int killed) {
        for (int i = 2; i < skillUse.length; ++i) {
            if (killed == SKILL_LIMIT[i])
                this.speak("I've learned a new skill:\n"
                        + skillList[i].toString()
                        + "!  Key <" + SKILL_KEY[i] + ">");
        }
    }

    public int getMP() {
        return MP;
    }

    public void modifyMP(int dlt) {
        if (isEnergetic() && dlt < 0)
            return;
        MP += dlt;
        if (MP > maxMP)
            MP = maxMP;
    }

    public int getInvisibleTime() {
        return invisibleTime;
    }

    public void setInvisibleTime(int invisibleTime) {
        this.invisibleTime = invisibleTime;
    }

    public int getEnergeticTime() {
        return energeticTime;
    }

    public void setEnergeticTime(int energeticTime) {
        this.energeticTime = energeticTime;
    }

    public int getCrazyTime() {
        return crazyTime;
    }

    public void setCrazyTime(int crazyTime) {
        this.crazyTime = crazyTime;
    }

    public int getAccTime() {
        return accTime;
    }

    public void setAccTime(int accTime) {
        this.accTime = accTime;
    }

    /* Draw */
    public void drawEnergyBar(Graphics g) {
        if (isCrazy() && random.nextInt(100) < 50)
            g.setColor(pink);
        else
            g.setColor(RED);
        if (HP > 0)
            g.fillRect(x - Tank.HALF_WIDTH, y + Tank.HALF_WIDTH + 25,
                    (int)((double)Tank.HALF_WIDTH * 2 * ((double)HP / maxHP)),
                    3);
        if (isEnergetic() && random.nextInt(100) < 50)
            g.setColor(blue);
        else
            g.setColor(CYAN);
        if (MP > 0)
            g.fillRect(x - Tank.HALF_WIDTH, y + Tank.HALF_WIDTH + 28,
                    (int)((double)Tank.HALF_WIDTH * 2 * ((double)MP / maxMP)),
                    3);

        g.setColor(BLACK);
        g.drawRect(x - Tank.HALF_WIDTH, y + Tank.HALF_WIDTH + 25,
                Tank.HALF_WIDTH * 2, 3);
        g.drawRect(x - Tank.HALF_WIDTH, y + Tank.HALF_WIDTH + 28,
                Tank.HALF_WIDTH * 2, 3);
    }

    private void drawSample(Graphics g) {
//        if (!isInvisible() || (Math.random() < 0.6))
//            super.draw(g);
        if (!isInvisible())
            super.draw(g);
        else {
            clr1 = transGreen;
            clr2 = transLightGreen;
            super.draw(g);
            clr1 = green;
            clr2 = lightGreen;
        }
    }

    public void draw(Graphics g) {
        if (sk != -1 && skillList[sk] instanceof Drawable &&
                SKILL_LIMIT[sk] <= MainWindow.getKilled() && getMP() >= skillList[sk].getCost())
            ((Drawable)skillList[sk]).draw(g);
        else
            drawSample(g);
    }

    /* Actions */

    private void decBuff() {
        if (invisibleTime > 0)
            --invisibleTime;
        if (crazyTime > 0)
            --crazyTime;
        if (energeticTime > 0) {
            --energeticTime;
            if (energeticTime == 0)
                sk = -1;
        }
        if (accTime > 0)
            --accTime;
    }

    public void move() {
        decBuff();
        if (moveTimeLimit == 1) {
            --moveTimeLimit;
            moveDir = STOP;
        }
        synchronized (this) {
            if (moveTimeLimit > 0) {
                --moveTimeLimit;
                move(moveDirLimit, Tank.STEP);
            } else {
                Direction tdir = cannonDir;
                if (isAcc())
                    move(moveDir, step + 4);
                else
                    move(moveDir, step);
                if (sk != -1 && skillList[sk] instanceof LongDistanceShoot)
                    cannonDir = tdir;
            }
        }
    }

    public void makeDamage(int dmg) {
        if (isCrazy())
            return;
        super.makeDamage(dmg);
        if (HP == 0) {
            clr1 = gray;
            clr2 = darkGray;
            MainWindow.gameOver();
        }
    }

    /* Skill */
    static final int[] SKILLBBO = { 20, 30, 40, 50, 60, 70, 80, 90, 100, 110,
            120, 130, 140, 150, 160, 170,
            180, 190, 200, 210, 220, 230, 240, 250, 250, 250,
            250, 250, 250, 250, 250, 250, 250, 250, 250, 250, 250, 250,
            250, 250 };
    static final int[] SKILLBBW = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 20, 30, 40, 50, 60, 70, 80, 90,
            100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200,
            210, 220, 230, 240, 250, 250, 250, 250, 250 };
    static final int[] SKILLIA = { 1000, 1000, 1000, 1000, 1000, 1000, 1000,
        1000, 1000, 1000, 1000, 1000 };

    public void setSK(int s) {
        if (sk != -1 && sk != s && skillList[sk] instanceof ReleaseKeySkill)
            if (((ReleaseKeySkill)skillList[sk]).useWhenRelease())
                modifyMP(-skillList[sk].getCost());
        sk = s;
    }
    public int getSK() {
        return sk;
    }
    
    void stopSkill() {
        sk = -1;
    }

    protected abstract class Skill implements Serializable {
        abstract int getCost();

        abstract boolean use();

    }

    protected interface ReleaseKeySkill {
        boolean useWhenRelease();
    }
    
    protected interface BlockShootSkill {
        boolean isBlocking();
    }
    
    protected class Healing extends Skill implements Drawable {

        public String toString() {
            return "Healing";
        }

        int getCost() {
            return 50;
        }

        boolean use() {
            if (isEnergetic()) {
                stopSkill();
                return false;
            }
            modifyHP(10 * (int)Math.min(5, MainWindow.getKilled() / 200 + 1));
            return true;
        }

        public void draw(Graphics g) {
            Color c1 = clr1, c2 = clr2;
            clr1 = darkWhiteGreen;
            clr2 = lightGrayGreen;
            drawSample(g);
            clr1 = c1;
            clr2 = c2;
        }

    }

    protected class SummonEnemy extends Skill {

        public String toString() {
            return "Summon Enemy";
        }

        int getCost() {
            return 0;
        }

        boolean use() {
            stopSkill();
            if (MainWindow.tanks.size() <= MainWindow.ENEMY_TANK_LIMIT) {
                synchronized (MainWindow.tanks) {
                    if (MainWindow.getKilled() < 450)
                        MainWindow.tanks.add(new ComTank(ENEMY));
                    else {
                        int t = random.nextInt(6);
                        switch (t) {
                            case 0:
                            case 1:
                                MainWindow.tanks.add(new ComTank(ENEMY));
                                break;
                            case 2:
                                MainWindow.tanks.add(new ComTank(SHOOTER));
                                break;
                            case 3:
                                MainWindow.tanks.add(new ComTank(SNIPER));
                                break;
                            case 4:
                                MainWindow.tanks.add(new ComTank(BOMBER));
                                break;
                            case 5:
                                MainWindow.tanks.add(new ComTank(ENGINEER));
                                break;
                        }

                    }
                }
                return true;
            } else
                return false;
        }
    }

    protected class SetMine extends Skill {

        public String toString() {
            return "Set Mine";
        }

        int getCost() {
            return 50;
        }

        boolean use() {
            Mine m = null;
            synchronized (MainWindow.weapons) {
                for (int i = 0; i < MainWindow.weapons.size(); ++i) {
                    if (MainWindow.weapons.get(i) instanceof Mine) {
                        m = (Mine)MainWindow.weapons.get(i);
                        if (m.distance(PlayerTank.this) < 30)
                            return false;
                    }
                }
            }
            synchronized (MainWindow.weapons) {
                MainWindow.weapons.add(new Mine(x, y, fact));
            }
            stopSkill();
            return true;
        }
    }

    protected class SummonFriend extends Skill {

        public String toString() {
            return "Summon Friend";
        }

        int getCost() {
            return 400;
        }

        boolean use() {
            stopSkill();
            if (MainWindow.friends.size() < 3) {
                synchronized (MainWindow.friends) {
                    if (Math.random() < 0.98 || ComTank.existOrange)
                        MainWindow.friends.add(new ComTank(FRIEND));
                    else
                        MainWindow.friends.add(new ComTank(IAMANORANGE));
                }
                return true;
            }
            return false;
        }
    }

    protected class OctopusCannon extends Skill implements Drawable {

        public String toString() {
            return "Octopus Cannon";
        }

        int getCost() {
            return 50;
        }

        boolean use() {
            for (int i = 0; i < 8; ++i) {
                cannonDir = rotate(cannonDir, 1);
                launch();
            }
            return true;
        }

        public void draw(Graphics g) {
            for (int i = 0; i < 8; ++i) {
                cannonDir = rotate(cannonDir, 1);
                if (Math.random() < 0.3)
                    drawSample(g);
            }
        }

    }

    protected class Dash extends Skill {

        public String toString() {
            return "Dash";
        }

        int getCost() {
            return 30;
        }

        boolean use() {
            stopSkill();
            Direction tDir = moveDir;
            if (MainWindow.getKilled() < dashUpdate)
                forceMove(cannonDir, 30 * step);
            else {
                crazyTime += 5;
                int dx, dy;
                for (int i = 0; i < 30; ++i) {
                    forceMove(cannonDir, step);
                    synchronized (MainWindow.explosions) {
                        MainWindow.explosions.add(new Explosion(x, y,
                                EXPLOSION_R,
                                fact, transparentGreen));
                    }
                    synchronized (MainWindow.tanks) {
                        for (Tank t : MainWindow.tanks) {
                            dx = (x - t.getX());
                            dy = (y - t.getY());
                            if (Math.abs(dx) < Tank.HALF_WIDTH * 2
                                    && Math.abs(dy) < Tank.HALF_WIDTH * 2) {
                                t.HP = 0;
                                t.makeDamage(1);
                            }
                        }
                    }
                    synchronized (MainWindow.friends) {
                        for (Tank t : MainWindow.friends) {
                            dx = (x - t.getX());
                            dy = (y - t.getY());
                            if (Math.abs(dx) < Tank.HALF_WIDTH * 2
                                    && Math.abs(dy) < Tank.HALF_WIDTH * 2) {
                                t.HP = 0;
                                t.makeDamage(1);
                            }
                        }
                    }
                }
            }
            moveLimit();
            moveDir = tDir;
            return true;
        }
    }

    protected class Stealth extends Skill {

        public String toString() {
            return "Stealth";
        }

        int getCost() {
            return 400;
        }

        boolean use() {
            invisibleTime = 500;
            return true;
        }
    }

    protected class LineShoot extends Skill implements Drawable {

        public String toString() {
            return "Line Shoot";
        }

        int getCost() {
            return 45;
        }

        boolean use() {
            PlayerTank tpt = new PlayerTank(PlayerTank.this);
            tpt.shift(rotate(cannonDir, 2), 30 * step);
            synchronized (MainWindow.weapons) {
                tpt.launch();
                for (int i = 0; i < 12; ++i) {
                    tpt.shift(rotate(cannonDir, 6), 5 * step);
                    tpt.launch();
                }

                if (MainWindow.getKilled() >= lineShootUpdate1) {
                    tpt.shift(rotate(cannonDir, 4), 15 * step);
                    tpt.launch();
                    for (int i = 0; i < 12; ++i) {
                        tpt.shift(rotate(cannonDir, 2), 5 * step);
                        tpt.launch();
                    }
                }
                if (MainWindow.getKilled() >= lineShootUpdate2) {
                    tpt.shift(cannonDir, 30 * step);
                    tpt.launch();
                    for (int i = 0; i < 12; ++i) {
                        tpt.shift(rotate(cannonDir, 6), 5 * step);
                        tpt.launch();
                    }
                }
            }
            return true;
        }

        public void draw(Graphics g) {
            PlayerTank tpt = new PlayerTank(PlayerTank.this);
            tpt.shift(rotate(cannonDir, 2), 30 * step);
            if (Math.random() < 0.3)
                tpt.drawSample(g);
            for (int i = 0; i < 12; ++i) {
                tpt.shift(rotate(cannonDir, 6), 5 * step);
                if (Math.random() < 0.3)
                    tpt.drawSample(g);
            }
            if (MainWindow.getKilled() >= lineShootUpdate1) {
                tpt.shift(rotate(cannonDir, 4), 15 * step);
                if (Math.random() < 0.3)
                    tpt.drawSample(g);
                for (int i = 0; i < 12; ++i) {
                    tpt.shift(rotate(cannonDir, 2), 5 * step);
                    if (Math.random() < 0.3)
                        tpt.drawSample(g);
                }
            }
            if (MainWindow.getKilled() >= lineShootUpdate2) {
                tpt.shift(cannonDir, 30 * step);
                if (Math.random() < 0.3)
                    tpt.drawSample(g);
                for (int i = 0; i < 12; ++i) {
                    tpt.shift(rotate(cannonDir, 6), 5 * step);
                    if (Math.random() < 0.3)
                        tpt.drawSample(g);
                }
            }
        }

    }

    protected class BigExplosion extends Skill {

        public String toString() {
            return "Big Explosion";
        }

        int getCost() {
            return 0;
        }

        boolean use() {
            stopSkill();
            if (HP < 200)
                return false;
            HP -= Math.min(HP - 10, 200);
            Explosion ep = new Explosion(x, y, SKILLBBO, fact);
            synchronized (MainWindow.explosions) {
                MainWindow.explosions.add(ep);
                MainWindow.explosions.add(new Explosion(x, y, SKILLBBW, fact,
                        lightYellow, -1));
            }
            moveDir = STOP;
            return true;
        }
    }

    protected class IceAge extends Skill {

        public String toString() {
            return "Ice Age";
        }

        int getCost() {
            return 250;
        }

        boolean use() {
            stopSkill();
            MainWindow.freezed = 350;
            MainWindow.explosions.add(new Explosion(400, 300, SKILLIA, fact,
                    lightIceBlue));
            synchronized (MainWindow.tanks) {
                for (Tank t : MainWindow.tanks) {
                    t.setMoveLimit(STOP, 350 + random.nextInt(20));
                }
            }
            synchronized (MainWindow.friends) {
                for (Tank t : MainWindow.friends) {
                    t.setMoveLimit(STOP, 350 + random.nextInt(20));
                }
            }
            synchronized (MainWindow.weapons) {
                for (Weapon w : MainWindow.weapons) {
                    if (w instanceof Missile) {
                        w.abolish();
                        ((Missile)w).explode();
                    }
                    if (w instanceof Mine) {
                        w.abolish();
                        ((Mine)w).explode();
                    }
                }
            }
            return true;
        }
    }

    protected class Earthquake extends Skill {

        public String toString() {
            return "Earthquake";
        }

        int getCost() {
            return 50;
        }

        boolean use() {
            setMoveLimit(STOP, 5);
            makeDamage(10);
            MainWindow.shake = 5;
            synchronized (MainWindow.tanks) {
                for (Tank t : MainWindow.tanks)
                    t.makeDamage(60);
            }
            synchronized (MainWindow.friends) {
                for (Tank t : MainWindow.friends)
                    t.makeDamage(60);
            }
            synchronized (MainWindow.weapons) {
                for (Weapon w : MainWindow.weapons) {
                    if (w instanceof Mine) {
                        w.abolish();
                        ((Mine)w).explode();
                    }
                }
            }
            return true;
        }
    }

    protected class LongDistanceShoot extends Skill implements Drawable, ReleaseKeySkill, BlockShootSkill {

        private int proc = 0;
        private long lastUseTime;
        private long lastLaunchTime;
        
        public String toString() {
            return "Long Direction Shoot";
        }
        
        int getCost() {
            return 0;
        }

        boolean use() {
            lastUseTime = System.currentTimeMillis();
            if (proc > 0) {
                --proc;
                stopSkill();
            }
            return false;
        }
        
        public boolean useWhenRelease() {
            lastLaunchTime = System.currentTimeMillis();
            Missile m = new Missile(x, y, cannonDir, 
                    (int)distance(locateTarget()), fact, 25, darkPurple);
            launch(m);
            System.out.println(m.getPower());
            return true;
        }
        
        public boolean isBlocking() {
            return System.currentTimeMillis() - Math.max(lastUseTime, lastLaunchTime) < 1500;
        }
        
        public void draw(Graphics g) {
            drawSample(g);
            Point p = locateTarget();
            g.setColor(Color.red);
            g.drawOval(p.x - 10, p.y - 10, 20, 20);
            g.drawLine(p.x, p.y - 15, p.x, p.y + 15);
            g.drawLine(p.x - 15, p.y, p.x + 15, p.y);
        }
        
        private Point locateTarget() {
            Missile m = new Missile(x, y, getCannonDir(), 0, fact);
            while (m.inScreen()) {
                m.move();
                if (m.tryAttack())
                    return new Point(m.getX(), m.getY());
            }
            return new Point(-100, -100);
        }
        
    }
}

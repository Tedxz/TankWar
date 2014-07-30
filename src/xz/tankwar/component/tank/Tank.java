package xz.tankwar.component.tank;

import java.awt.*;
import java.io.Serializable;
import static java.awt.Color.*;

import xz.tankwar.component.*;
import xz.tankwar.component.supply.*;
import xz.tankwar.component.weapon.*;
import xz.tankwar.module.ConsoleWindow;
import xz.tankwar.module.MainWindow;
import static xz.tankwar.component.Direction.*;
import static xz.tankwar.component.tank.ComTank.ComTankType.*;
import static xz.tankwar.module.PropertiesManager.*;

public class Tank extends GameComponent {
    public static final int R = 18, HALF_WIDTH = 22, STEP = 4, CANNON_R = 5,
            CANNON_LEN = 35;
    public static final int SHOOT_CD = 70;
    static final int EXPLOSION_R[] = { 15, 25, 30, 45, 35 };

    public static final int DIALOG_BORDER_WIDTH = 5;
    public static final int DIALOG_ARROW_WIDTH = 7;
    public static final Color DIALOG_BACKGROUND_COLOR = new Color(255, 255,
            255, 150);
    public static final Color DIALOG_FOREGROUND_COLOR = black;
    
    protected Direction moveDir = STOP;
    protected Direction shootDir = STOP;
    protected Direction cannonDir = STOP;
    protected int power = 50;
    protected int HP;
    protected int maxHP;
    public long blockTime = 0;
    protected boolean ignoreMoveLimit = false;
    protected int energyBarLastTime = 0;
    protected Color clr1 = gray;
    protected Color clr2 = black;
    protected Color clr3 = null;
    protected Dialog dialog = null;

    protected Direction moveDirLimit = STOP;
    protected int moveTimeLimit = 0;

    public void setMoveDir(Direction moveDir) {
        this.moveDir = moveDir;
    }

    public void setShootDir(Direction dir) {
        if (isLimited())
            return;
        if (dir == null) {
            shootDir = null;
            return;
        }
        this.shootDir = dir;
        if (shootDir == null)
            shootDir = STOP;
        if (shootDir != null && shootDir != STOP)
            this.cannonDir = shootDir;
    }

    public void setCannonDir(Direction cannonDir) {
        if (cannonDir != STOP)
            this.cannonDir = cannonDir;
    }

    public void resetMoveDir(Direction dir) {
        if (isLimited())
            return;
        moveDir = erase(moveDir, dir);
        if ((shootDir == null || shootDir == STOP) && moveDir != STOP)
            cannonDir = moveDir;
    }

    public void resetShootDir(Direction dir) {
        if (isLimited())
            return;
        if (dir == null) {
            shootDir = STOP;
            return;
        }
        if (shootDir != null && shootDir != STOP)
            shootDir = erase(shootDir, dir);
        if (shootDir != null && shootDir != STOP)
            cannonDir = shootDir;
    }

    public Direction getShootDir() {
        return shootDir;
    }

    public Direction getMoveDir() {
        return moveDir;
    }

    public Direction getCannonDir() {
        return cannonDir;
    }

    public void setMoveLimit(Direction dir, int time) {
        moveDirLimit = dir;
        moveTimeLimit = time;
    }

    public boolean isLimited() {
        return (moveTimeLimit != 0);
    }

    public int getPower() {
        return power;
    }

    void moveLimit() {
        if (ignoreMoveLimit) {
            if (x < -25 || x > MainWindow.WINDOW_WIDTH + 25
                    || y < -25 || y > MainWindow.WINDOW_HEIGHT + 25)
                abolish();
            return;
        }
        if (x < 25) {
            x = 25;
            blockTime = 70;
        }
        if (y < 45) {
            y = 45;
            blockTime = 70;
        }
        if (x > MainWindow.WINDOW_WIDTH - 25) {
            x = MainWindow.WINDOW_WIDTH - 25;
            blockTime = 70;
        }
        if (y > MainWindow.WINDOW_HEIGHT - 25) {
            y = MainWindow.WINDOW_HEIGHT - 25;
            blockTime = 70;
        }
    }

    private boolean block(int x, int y) {
        int dx, dy;
        blockTime = 1;
        if (MainWindow.myTank != this && !MainWindow.myTank.isInvisible()) {
            Tank t = MainWindow.myTank;
            dx = (x - t.x);
            dy = (y - t.y);
            if (Math.abs(dx) < Tank.HALF_WIDTH * 2
                    && Math.abs(dy) < Tank.HALF_WIDTH * 2) {
                return true;
            }
        }

        synchronized (MainWindow.tanks) {
            for (Tank t : MainWindow.tanks) {
                if (t == this)
                    continue;
                dx = (x - t.x);
                dy = (y - t.y);
                if (Math.abs(dx) < Tank.HALF_WIDTH * 2
                        && Math.abs(dy) < Tank.HALF_WIDTH * 2) {
                    return true;
                }
            }
        }
        synchronized (MainWindow.friends) {
            for (Tank t : MainWindow.friends) {
                if (t == this)
                    continue;
                dx = (x - t.x);
                dy = (y - t.y);
                if (Math.abs(dx) < Tank.HALF_WIDTH * 2
                        && Math.abs(dy) < Tank.HALF_WIDTH * 2) {
                    return true;
                }
            }
        }
        blockTime = 0;
        return false;
    }

    void shift(Direction dir, int step) {
    x += (int)(step * unitVectorX(dir));
    y += (int)(step * unitVectorY(dir));
    
    }

    public void forceMove(Direction dir, int step) {
        moveDir = dir;
        if (moveDir == STOP)
            return;
        if (shootDir == STOP)
            cannonDir = moveDir;
        x += (int)(step * unitVectorX(dir));
        y += (int)(step * unitVectorY(dir));
    }

    public void move(Direction dir, int step) {
        if (this instanceof PlayerTank
                && (((PlayerTank)this).isInvisible()
                || ((PlayerTank)this).isCrazy())) {
            forceMove(dir, step);
            if (moveTimeLimit == 0) {
                moveLimit();
            }
            return;
        }
        if (block(x, y) && !(this instanceof PlayerTank)) {
            HP = 0;
            makeDamage(1);
        }
        moveDir = dir;
        if (moveDir == STOP)
            return;
        if (shootDir == STOP)
            cannonDir = moveDir;
        int tx = x, ty = y;
        tx += (int)(step * unitVectorX(dir));
        ty += (int)(step * unitVectorY(dir));
        if (block(tx, ty))
            return;
        x = tx;
        y = ty;
        if (moveTimeLimit == 0) {
            moveLimit();
        }
    }

    public void move() {
        if (moveTimeLimit > 0) {
            --moveTimeLimit;
            move(moveDirLimit, STEP);
        } else
            move(moveDir, STEP);
    }

    public void move(Direction dir) {
        moveDir = dir;
        move();
    }

    public void makeDamage(int dmg) {
        if (!alive)
            return;
        modifyHP(-dmg);
        if (HP == 0) {
            abolish();
            explode();
        }
        energyBarLastTime = 100;
    }

    void dropItem(int prob) {
        int rnd = random.nextInt(100);
        if (rnd < prob) {
            synchronized (MainWindow.supplies) {
                MainWindow.supplies.add(new HealPack(x, y));
            }
            return;
        }
        if (rnd < prob * 2) {
            synchronized (MainWindow.supplies) {
                MainWindow.supplies.add(new MagicStone(x, y));
            }
            return;
        }
        if (rnd < prob * 3) {
            synchronized (MainWindow.supplies) {
                MainWindow.supplies.add(new Accelerator(x, y));
            }
            return;
        }

    }

    void explode() {
        if (fact == 1) {
            MainWindow.addKilled();
            if (((ComTank)this).tag == IAMANORANGE)
                ComTank.existOrange = false;
        }
        synchronized (MainWindow.explosions) {
            MainWindow.explosions.add(new Explosion(x, y, EXPLOSION_R, fact));
        }
    }

    public int getHP() {
        return HP;
    }
    public int getMaxHP() {
        return maxHP;
    }

    public void modifyHP(int dlt) {
        HP += dlt;
        if (HP > maxHP)
            HP = maxHP;
        if (HP < 0)
            HP = 0;
    }
    
    public boolean shootBlocked() {
        return false;
    }
    
    public void launch() {
        if (isLimited())
            return;
        if (shootBlocked())
            return;
        int nx = x, ny = y, cl = CANNON_LEN + 4;
        
        nx += (int)(cl * unitVectorX(cannonDir));
        ny += (int)(cl * unitVectorY(cannonDir));
        
        Missile m = new Missile(nx, ny, cannonDir, power, this.fact);
        synchronized (MainWindow.weapons) {
            MainWindow.weapons.add(m);
        }
        m.explode();

    }

    public void launch(Missile m) {
        if (isLimited())
            return;
        int nx = x, ny = y, cl = CANNON_LEN + 4;
        nx += (int)(cl * unitVectorX(cannonDir));
        ny += (int)(cl * unitVectorY(cannonDir));
        m.setX(nx);
        m.setY(ny);
        synchronized (MainWindow.weapons) {
            MainWindow.weapons.add(m);
        }
        m.explode();

    }

    protected void drawChassis(Graphics g) {
        g.setColor(clr2);
        if (this instanceof PlayerTank && ((PlayerTank)this).getSK() != 0) {
            if (((PlayerTank)this).crazyTime > 0 && random.nextInt(100) < 50)
                g.setColor(whiteGreen);
        }
        g.fillRoundRect(x - HALF_WIDTH, y - HALF_WIDTH, HALF_WIDTH * 2,
                HALF_WIDTH * 2, 10, 10);
    }

    protected void drawCannon(Graphics g) {
        int rnd = random.nextInt(100);
        if (clr3 != null)
            g.setColor(clr3);
        else
            g.setColor(clr1);

        if (this instanceof PlayerTank && ((PlayerTank)this).getSK() != 0) {
            if (((PlayerTank)this).energeticTime > 0 && rnd < 50)
                g.setColor(whiteGreen);
        }

        switch (cannonDir) {
            case UP:
                g.fillRect(x - CANNON_R, y - CANNON_LEN,
                        CANNON_R * 2, CANNON_LEN);
                break;
            case DOWN:
                g.fillRect(x - CANNON_R, y,
                        CANNON_R * 2, CANNON_LEN);
                break;
            case LEFT:
                g.fillRect(x - CANNON_LEN, y - CANNON_R,
                        CANNON_LEN, CANNON_R * 2);
                break;
            case RIGHT:
                g.fillRect(x, y - CANNON_R,
                        CANNON_LEN, CANNON_R * 2);
                break;
            case UP_LEFT: {
                int[] xpoint = { x - 32, x - 25, x, x - 7 }, ypoint = { y - 25,
                        y - 32, y - 7, y };
                g.fillPolygon(xpoint, ypoint, 4);
                break;
            }
            case UP_RIGHT: {
                int[] xpoint = { x + 25, x + 32, x + 7, x }, ypoint = { y - 32,
                        y - 25, y, y - 7 };
                g.fillPolygon(xpoint, ypoint, 4);
                break;
            }
            case DOWN_LEFT: {
                int[] xpoint = { x - 7, x, x - 25, x - 32 }, ypoint = { y,
                        y + 7, y + 32, y + 25 };
                g.fillPolygon(xpoint, ypoint, 4);
                break;
            }
            case DOWN_RIGHT: {
                int[] xpoint = { x, x + 7, x + 32, x + 25 }, ypoint = { y + 7,
                        y, y + 25, y + 32 };
                g.fillPolygon(xpoint, ypoint, 4);
                break;
            }
        }
        g.setColor(clr1);
        if (this instanceof PlayerTank && ((PlayerTank)this).getSK() != 0) {
            if (((PlayerTank)this).energeticTime > 0 && rnd < 50)
                g.setColor(whiteGreen);
        }
        g.fillOval(x - R, y - R, R * 2, R * 2);
    }

    public void drawDialog(Graphics g) {
        if (dialog != null)
            dialog.draw(g);
    }

    public void draw(Graphics g) {
        drawChassis(g);
        drawCannon(g);
    }

    public void speak(String s) {
        dialog = new Dialog(s);
    }

    class Dialog implements Serializable {

        protected String[] content = null;
        protected int textHeight;
        protected int textWidth = 0;
        protected Direction dir = DOWN_LEFT;
        protected int lastTime = 2000;

        public Dialog(String _content) {
            FontMetrics fm = ConsoleWindow.console.getFontMetrics(BUBBLE_FONT);
            content = _content.split("\n");
            textHeight = content.length * BUBBLE_FONT.getSize()
                    + DIALOG_BORDER_WIDTH * 2;
            for (int i = 0; i < content.length; ++i)
                textWidth = Math.max(textWidth, fm.stringWidth(content[i]));
            textWidth += DIALOG_BORDER_WIDTH * 2;
    
        }

        private Point locateRect() {
            int nx = x, ny = y;
            int sumOffsetX = Tank.HALF_WIDTH + DIALOG_ARROW_WIDTH + 3;
            int sumOffsetY = Tank.HALF_WIDTH + DIALOG_ARROW_WIDTH + 3;
            if (dir.includes(UP))
                ny -= sumOffsetY + textHeight;
            if (dir.includes(LEFT))
                nx -= sumOffsetX + textWidth;
            if (dir.includes(DOWN))
                ny += sumOffsetY;
            if (dir.includes(RIGHT))
                nx += sumOffsetX;
            return new Point(nx, ny);
        }

        private Polygon getArrow() {
            Polygon pol = new Polygon();
            int px = x, py = y;
            px += unitVectorX(dir) * Math.sqrt(2.0) * (Tank.HALF_WIDTH + 5);
            py += unitVectorY(dir) * Math.sqrt(2.0) * (Tank.HALF_WIDTH + 5);
            pol.addPoint(px, py);
            px += unitVectorX(dir) * Math.sqrt(2.0) * DIALOG_ARROW_WIDTH;
            py += unitVectorY(dir) * Math.sqrt(2.0) * DIALOG_ARROW_WIDTH;
            pol.addPoint(px, py + 3);
            pol.addPoint(px, py - 3);
            return pol;
        }
        
        private void refreshDirection() {
            if (x < textWidth + 40)
                dir = compose(dir, RIGHT);
            if (y < textHeight + 60)
                dir = compose(dir, DOWN);
            if (x > MainWindow.WINDOW_WIDTH - textWidth - 40)
                dir = compose(dir, LEFT);
            if (y > MainWindow.WINDOW_HEIGHT -textHeight - 40)
                dir = compose(dir, UP);
            
        }
        
        public void draw(Graphics g) {
            if (lastTime > 0 && MainWindow.stat != MainWindow.STAT_PAUSE)
                --lastTime;
            if (lastTime == 0)
                Tank.this.dialog = null;
            refreshDirection();
            g.setColor(DIALOG_BACKGROUND_COLOR);
            Point p = locateRect();
            g.fillRoundRect(p.x, p.y, textWidth, textHeight,
                    DIALOG_BORDER_WIDTH, DIALOG_BORDER_WIDTH);
            g.fillPolygon(getArrow());
            g.setColor(DIALOG_FOREGROUND_COLOR);
            g.setFont(BUBBLE_FONT);
            for (int i = 0; i < content.length; ++i)
                g.drawString(content[i], p.x + DIALOG_BORDER_WIDTH,
                        p.y + DIALOG_BORDER_WIDTH + BUBBLE_FONT.getSize() * (i + 1));
        }
    }

}

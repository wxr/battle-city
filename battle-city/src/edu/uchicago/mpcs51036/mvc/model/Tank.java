package edu.uchicago.mpcs51036.mvc.model;

import edu.uchicago.mpcs51036.mvc.controller.Game;
import edu.uchicago.mpcs51036.mvc.controller.Game.Orientation;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Random;

public class Tank extends Sprite {

    private static final int BULLET_ON_SCREEN_MAX = 2;
    private static final int BULLET_COOLING_TIME_MIN = 8;

    private static final int PROBABILITY_RIGHT = 15;  // Out of 100
    private static final int PROBABILITY_DOWN = 60;   // Out of 100
    private static final int PROBABILITY_LEFT = 15;   // Out of 100

    private int mBulletOnScreen = 0;
    private int mBulletCoolingTime = 0;
    private boolean mBulletCool = true;
    private int mID;

    private int mEnemyTankLevel;

    private boolean mProtected;
    private int mProtectExpire;

    private int mAutoKeepCurrentMoveCount = 0;
    private int mAutoCount = 0;

    public Tank(Team team, int id) {
        mID = id;
        if (team == Team.PLAYER) {
            initPlayerTank();
        } else if (team == Team.ENEMY) {
            initEnemyTank();
        }
    }

    private void initPlayerTank() {
        setTeam(Team.PLAYER);
        setOrientation(Orientation.TO_UP);
        setProtectedExpire(50);
        if (mID == 1) {
            setCenter(new Point(5 * 48 - 24, 13 * 48 - 24));  // Left
        } else if (mID == 2) {
            setCenter(new Point(9 * 48 - 24, 13 * 48 - 24));  // Right
        }
        initTank();
    }

    private void initEnemyTank() {
        setTeam(Team.ENEMY);
        setOrientation(randomOrientation());
        setEnemyTankLevel(randomIntBetween(1, 4));
        if (mID % 3 == 1) {
            setCenter(new Point(6 * 48 + 24, 24));   // Middle
        } else if (mID % 3 == 2) {
            setCenter(new Point(12 * 48 + 24, 24));  // Right
        } else {
            setCenter(new Point(0 * 48 + 24, 24));   // Left
        }
        initTank();
    }

    private void initTank() {
        setModel(Model.TANK);
        setSideLength(48);
        setSpeedValue(6);
        setElevation(50);
        updateSubimage();
        updateRectangle();
    }

    private void updateSubimage() {
        if (getTeam() == Team.PLAYER) {
            updateSubimagePlayer();
        } else if (getTeam() == Team.ENEMY) {
            updateSubimageEnemy();
        }
    }

    private void updateSubimagePlayer() {
        if (mID == 1) {
            switch (getOrientation()) {
                case TO_RIGHT:
                    setSubimage(Game.getImage().getSubimage(96, 0, 16, 16));
                    break;
                case TO_DOWN:
                    setSubimage(Game.getImage().getSubimage(64, 0, 16, 16));
                    break;
                case TO_LEFT:
                    setSubimage(Game.getImage().getSubimage(32, 0, 16, 16));
                    break;
                case TO_UP:
                    setSubimage(Game.getImage().getSubimage(0, 0, 16, 16));
                    break;
                default:
                    break;
            }
        } else if (mID == 2) {
            switch (getOrientation()) {
                case TO_RIGHT:
                    setSubimage(Game.getImage().getSubimage(96, 8 * 16, 16, 16));
                    break;
                case TO_DOWN:
                    setSubimage(Game.getImage().getSubimage(64, 8 * 16, 16, 16));
                    break;
                case TO_LEFT:
                    setSubimage(Game.getImage().getSubimage(32, 8 * 16, 16, 16));
                    break;
                case TO_UP:
                    setSubimage(Game.getImage().getSubimage(0, 8 * 16, 16, 16));
                    break;
                default:
                    break;
            }
        }
    }

    private void updateSubimageEnemy() {
        switch (getOrientation()) {
            case TO_RIGHT:
                setSubimage(Game.getImage().getSubimage(14 * 16, (getEnemyTankLevel() - 1) * 16, 16, 16));
                break;
            case TO_DOWN:
                setSubimage(Game.getImage().getSubimage(12 * 16, (getEnemyTankLevel() - 1) * 16, 16, 16));
                break;
            case TO_LEFT:
                setSubimage(Game.getImage().getSubimage(10 * 16, (getEnemyTankLevel() - 1) * 16, 16, 16));
                break;
            case TO_UP:
                setSubimage(Game.getImage().getSubimage(8 * 16, (getEnemyTankLevel() - 1) * 16, 16, 16));
                break;
            default:
                break;
        }
    }

    public void tryFireBullet() {
        if (mBulletOnScreen < BULLET_ON_SCREEN_MAX && mBulletCool) {
            Cc.getInstance().getOpsList().enqueue(new Bullet(this), CollisionOp.Operation.ADD);
            ++mBulletOnScreen;
            mBulletCool = false;
            mBulletCoolingTime = 0;
        }
    }

    public void reduceOneBulletOnScreen() {
        --mBulletOnScreen;
    }

    @Override
    public void move() {
        if (getTeam() == Team.ENEMY) {
            automaticMove();
        } else if (getTeam() == Team.PLAYER) {
            adjustProtected();
        }

        adjustBulletCooling();

        if (isTurning()) {
            handleTurning();
        } else {
            handleStraight();
        }
    }

    private void automaticMove() {
        // Fire or not
        if (randomIntBetween(1, 100) > 90) {
            tryFireBullet();
        }

        // Keep current move
        if (mAutoCount < mAutoKeepCurrentMoveCount) {
            ++mAutoCount;
            return;
        }

        // New move
        mAutoKeepCurrentMoveCount = randomIntBetween(1, 20);
        mAutoCount = 0;

        int value = randomIntBetween(1, 100);
        if (isBetween(value, 1, PROBABILITY_RIGHT)) {
            makeMove(Orientation.TO_RIGHT);
        } else if (isBetween(value, PROBABILITY_RIGHT + 1, PROBABILITY_RIGHT + PROBABILITY_DOWN)) {
            makeMove(Orientation.TO_DOWN);
        } else if (isBetween(value, PROBABILITY_RIGHT + PROBABILITY_DOWN + 1,
                                    PROBABILITY_RIGHT + PROBABILITY_DOWN + PROBABILITY_LEFT)) {
            makeMove(Orientation.TO_LEFT);
        } else {
            makeMove(Orientation.TO_UP);
        }
    }

    private void adjustProtected() {
        if (!isProtected()) {
            return;
        }
        --mProtectExpire;
        if (mProtectExpire == 0) {
            setProtected(false);
        }
    }

    private void adjustBulletCooling() {
        if (mBulletCool) {
            return;
        }
        ++mBulletCoolingTime;
        if (mBulletCoolingTime == BULLET_COOLING_TIME_MIN) {
            mBulletCool = true;
        }
    }

    private void handleTurning() {
        setTurning(false);
        int newX;
        int newY;
        if (getOrientation() == Orientation.TO_RIGHT || getOrientation() == Orientation.TO_LEFT) {
            newX = getCenter().x;
            newY = (int) Math.round(getCenter().y / 24.0) * 24;
        } else {
            newX = (int) Math.round(getCenter().x / 24.0) * 24;
            newY = getCenter().y;
        }
        setCenter(new Point(newX, newY));
        updateRectangle();
    }

    private void handleStraight() {
        boolean[] done = new boolean[] { false };
        int[] originalX = new int[] { getCenter().x };
        int[] originalY = new int[] { getCenter().y };
        int[] newX = new int[] { getCenter().x + getSpeedX() };
        int[] newY = new int[] { getCenter().y + getSpeedY() };

        checkMapBorder(done, originalX, originalY, newX, newY);
        checkOtherTank(done, originalX, originalY, newX, newY);
        checkTerrain(done, originalX, originalY, newX, newY);

        setCenter(new Point(newX[0], newY[0]));
        updateRectangle();
    }

    private void checkMapBorder(boolean[] done, int[] originalX, int[] originalY, int[] newX, int[] newY) {
        int sideLength = getSideLength();

        if (newX[0] < sideLength / 2) {
            newX[0] = sideLength / 2;
            done[0] = true;
        } else if (newX[0] > 48 * 13 - sideLength / 2) {
            newX[0] = 48 * 13 - sideLength / 2;
            done[0] = true;
        } else if (newY[0] < sideLength / 2) {
            newY[0] = sideLength / 2;
            done[0] = true;
        } else if (newY[0] > 48 * 13 - sideLength / 2) {
            newY[0] = 48 * 13 - sideLength / 2;
            done[0] = true;
        }
    }

    private void checkOtherTank(boolean[] done, int[] originalX, int[] originalY, int[] newX, int[] newY) {
        if (done[0]) {
            return;
        }

        int sideLength = getSideLength();
        Rectangle newTankRect = new Rectangle(newX[0] - sideLength / 2, newY[0] - sideLength / 2, sideLength, sideLength);

        List<Movable> movTanks = Cc.getInstance().getMovTanks();
        for (Movable otherTank : movTanks) {
            if (otherTank != this && otherTank.getRectangle().intersects(newTankRect)) {
                newX[0] = originalX[0];
                newY[0] = originalY[0];
                done[0] = true;
                return;
            }
        }
    }

    private void checkTerrain(boolean[] done, int[] originalX, int[] originalY, int[] newX, int[] newY) {
        if (done[0]) {
            return;
        }

        int sideLength = getSideLength();
        Rectangle newTankRect = new Rectangle(newX[0] - sideLength / 2, newY[0] - sideLength / 2, sideLength, sideLength);

        List<Movable> movTerrains = Cc.getInstance().getMovTerrains();
        for (Movable terrain : movTerrains) {
            if (terrain.getRectangle().intersects(newTankRect)) {
                Model terrModel = terrain.getModel();
                if (terrModel == Model.BRICK || terrModel == Model.STONE || terrModel == Model.GROUND
                        || terrModel == Model.WATER || terrModel == Model.BASE) {
                    newX[0] = originalX[0];
                    newY[0] = originalY[0];
                    done[0] = true;
                    return;
                }
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        updateSubimage();
        g.drawImage(getSubimage(), getRectangle().x, getRectangle().y, getSideLength(), getSideLength(), null);
    }

    public int getID() {
        return mID;
    }

    public void setProtected(boolean protect) {
        mProtected = protect;
    }

    public boolean isProtected() {
        return mProtected;
    }

    public void setProtectedExpire(int expire) {
        setProtected(true);
        mProtectExpire = expire;
    }

    private int randomIntBetween(int nStart, int nEnd) {
        Random randGenerator = new Random();
        int nRange = nEnd - nStart + 1;
        return randGenerator.nextInt(nRange) + nStart;
    }

    private Orientation randomOrientation() {
        switch (randomIntBetween(0, 3)) {
            case 0:
                return Orientation.TO_RIGHT;
            case 1:
                return Orientation.TO_DOWN;
            case 2:
                return Orientation.TO_LEFT;
            case 3:
                return Orientation.TO_UP;
            default:
                return null;
        }
    }

    public void setEnemyTankLevel(int level) {
        mEnemyTankLevel = level;
    }

    public int getEnemyTankLevel() {
        return mEnemyTankLevel;
    }

    private boolean isBetween(int value, int start, int end) {
        return value >= start && value <= end;
    }

}



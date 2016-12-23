package edu.uchicago.mpcs51036.mvc.model;

import edu.uchicago.mpcs51036.mvc.model.Cc.Direction;
import edu.uchicago.mpcs51036.mvc.controller.Game;
import edu.uchicago.mpcs51036.sound.Sound;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

public class Bullet extends Sprite {

    private static final int OFFSET = 3;

    private Tank mParentTank;
    private boolean mDestroyedByAnotherBullet = false;

    public Bullet(Tank tank) {

        setParentTank(tank);

        setTeam(tank.getTeam());
        setModel(Model.BULLET);
        setSideLength(12);
        setOrientation(tank.getOrientation());
        setSpeedValue(10);
        setElevation(49);
        updateSubimage();

        switch (getOrientation()) {
            case TO_RIGHT:
                setCenter(new Point(tank.getCenter().x + OFFSET, tank.getCenter().y));
                break;
            case TO_DOWN:
                setCenter(new Point(tank.getCenter().x, tank.getCenter().y + OFFSET));
                break;
            case TO_LEFT:
                setCenter(new Point(tank.getCenter().x - OFFSET, tank.getCenter().y));
                break;
            case TO_UP:
                setCenter(new Point(tank.getCenter().x, tank.getCenter().y - OFFSET));
                break;
            default:
                break;
        }

        updateRectangle();
        makeMove(getOrientation());

        Sound.playSound("BattleCityBulletFire.wav");

    }

    private void updateSubimage() {
        switch (getOrientation()) {
            case TO_RIGHT:
                setSubimage(Game.getImage().getSubimage(21 * 16 + 10, 6 * 16 + 6, 4, 4));
                break;
            case TO_DOWN:
                setSubimage(Game.getImage().getSubimage(21 * 16 + 3, 6 * 16 + 6, 4, 4));
                break;
            case TO_LEFT:
                setSubimage(Game.getImage().getSubimage(20 * 16 + 10, 6 * 16 + 6, 4, 4));
                break;
            case TO_UP:
                setSubimage(Game.getImage().getSubimage(20 * 16 + 3, 6 * 16 + 6, 4, 4));
                break;
            default:
                break;
        }
    }

    @Override
    public void move() {
        if (mDestroyedByAnotherBullet) {
            destroyThisBullet();
            return;
        }

        int newX = getCenter().x + getSpeedX();
        int newY = getCenter().y + getSpeedY();

        if (newX < 0 || newX > 48 * 13 || newY < 0 || newY > 48 * 13) {
            destroyThisBullet();
            return;
        }

        int sideLength = getSideLength();
        Rectangle newBulletRectangle = new Rectangle(newX - sideLength / 2, newY - sideLength / 2, sideLength, sideLength);

        checkHitTank(newBulletRectangle);
        checkHitAnotherBullet(newBulletRectangle);
        checkHitTerrain();

        setCenter(new Point(newX, newY));
        updateRectangle();
    }

    private void checkHitTank(Rectangle newBulletRectangle) {
        List<Movable> movTanks = Cc.getInstance().getMovTanks();
        for (Movable otherTank : movTanks) {
            if (otherTank.getRectangle().intersects(newBulletRectangle)) {
                if (otherTank.getTeam() == this.getTeam() && otherTank != getParentTank()) {
                    destroyThisBullet();
                    break;
                } else if (otherTank.getTeam() != this.getTeam()) {
                    destroyThisBullet();
                    destroyHitTank((Tank) otherTank);
                    break;
                }
            }
        }
    }

    private void checkHitAnotherBullet(Rectangle newBulletRectangle) {
        List<Movable> movBullets = Cc.getInstance().getMovBullets();
        for (Movable otherBullet : movBullets) {
            if (otherBullet != this && otherBullet.getRectangle().intersects(newBulletRectangle)) {
                destroyThisBullet();
                ((Bullet) otherBullet).setDestroyedByAnotherBullet();
                break;
            }
        }
    }

    private void checkHitTerrain() {
        int[] indices = null;
        Direction direction = null;

        int column, row, columnSmall, columnBig, rowSmall, rowBig;
        switch (getOrientation()) {
            case TO_RIGHT:
                column = (int) Math.ceil(getCenter().x / 12.0) + 1;
                rowSmall = (getCenter().y / 24) * 2;
                rowBig = (getCenter().y / 24) * 2 + 1;
                indices = new int[] { rowSmall * 100 + column, rowBig * 100 + column };
                direction = Direction.UP_OR_DOWN;
                break;

            case TO_DOWN:
                row = (int) Math.ceil(getCenter().y / 12.0) + 1;
                columnSmall = (getCenter().x / 24) * 2;
                columnBig = (getCenter().x / 24) * 2 + 1;
                indices = new int[] { row * 100 + columnSmall, row * 100 + columnBig };
                direction = Direction.LEFT_OR_RIGHT;
                break;

            case TO_LEFT:
                column = (int) Math.floor(getCenter().x / 12.0) + 1;
                rowSmall = (getCenter().y / 24) * 2;
                rowBig = (getCenter().y / 24) * 2 + 1;
                indices = new int[] { rowSmall * 100 + column, rowBig * 100 + column };
                direction = Direction.UP_OR_DOWN;
                break;

            case TO_UP:
                row = (int) Math.floor(getCenter().y / 12.0) + 1;
                columnSmall = (getCenter().x / 24) * 2;
                columnBig = (getCenter().x / 24) * 2 + 1;
                indices = new int[] { row * 100 + columnSmall, row * 100 + columnBig };
                direction = Direction.LEFT_OR_RIGHT;
                break;

            default:
                break;
        }

        checkHitTerrainCommon(indices, direction);
    }

    private void checkHitTerrainCommon(int[] indices, Direction direction) {
        boolean needRemoveBullet = false;

        for (int index : indices) {
            Movable terr = Cc.getInstance().getTerrainOutOfMatrix(index);
            if (terr == null) {
                continue;
            }
            switch (terr.getModel()) {
                case BRICK:
                    ((Terrain) terr).beGround();
                    int pairID = Cc.getInstance().findMyPairedMemberInSameGroup(index, direction);
                    Movable pairTerr = Cc.getInstance().getTerrainOutOfMatrix(pairID);
                    if (pairTerr != null) {
                        ((Terrain) pairTerr).beGround();
                    }
                    needRemoveBullet = true;

                    int[] id_list = new int[4];
                    boolean condition = true;
                    for (int i = 1; i <= 4; ++i) {
                        int id = Cc.getInstance().findAnotherMemberInSameGroup(index, i);
                        id_list[i-1] = id;
                        condition = condition && (Cc.getInstance().getTerrainOutOfMatrix(id).getModel() == Model.GROUND);
                    }
                    if (condition) {
                        for (int id : id_list) {
                            Cc.getInstance().getOpsList().enqueue(Cc.getInstance().getTerrainOutOfMatrix(id), CollisionOp.Operation.REMOVE);
                            Cc.getInstance().removeTerrainFromMatrix(id);
                        }
                    }
                    break;

                case STONE:
                    needRemoveBullet = true;
                    break;

                case BASE:
                    Game.setStatusCount(0);
                    Game.setGlobalStatus(Game.GlobalStatus.DISPLAYING_GAME_OVER);
                    Cc.getInstance().setAppearTime(-999);
                    break;

                default:
                    break;
            }
        }

        if (needRemoveBullet) {
            destroyThisBullet();
        }
    }

    @Override
    public void draw(Graphics g) {
        updateSubimage();
        g.drawImage(getSubimage(), getRectangle().x, getRectangle().y, getSideLength(), getSideLength(), null);
    }

    private void destroyThisBullet() {
        Cc.getInstance().getOpsList().enqueue(this, CollisionOp.Operation.REMOVE);
        Sound.playSound("BattleCityBulletDestroy.wav");
        getParentTank().reduceOneBulletOnScreen();
    }

    private void destroyHitTank(Tank hitTank) {
        if (hitTank.getTeam() == Team.PLAYER && !hitTank.isProtected()) {
            Cc.getInstance().getOpsList().enqueue(hitTank, CollisionOp.Operation.REMOVE);
        } else if (hitTank.getTeam() == Team.ENEMY) {
            Cc.getInstance().getOpsList().enqueue(hitTank, CollisionOp.Operation.REMOVE);
            Cc.getInstance().updateKillEnemyRecord(getParentTank().getID(), hitTank.getEnemyTankLevel());
        }
    }

    private Tank getParentTank() {
        return mParentTank;
    }

    private void setParentTank(Tank parent) {
        mParentTank = parent;
    }

    private void setDestroyedByAnotherBullet() {
        mDestroyedByAnotherBullet = true;
    }

}



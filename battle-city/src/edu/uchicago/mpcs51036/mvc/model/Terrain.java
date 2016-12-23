package edu.uchicago.mpcs51036.mvc.model;

import edu.uchicago.mpcs51036.mvc.controller.Game;

import java.awt.Graphics;
import java.awt.Point;

public class Terrain extends Sprite {

    private enum BasePart {
        UPPER_LEFT, UPPER_RIGHT, LOWER_LEFT, LOWER_RIGHT
    }

    private int mConvRow;
    private int mConvColumn;
    private int mConvID;

    private BasePart mBasePart;

    public Terrain(String type, int convRow, int convColumn, int convID) {

        mConvRow = convRow;
        mConvColumn = convColumn;
        mConvID = convID;

        setTeam(Team.TERRAIN);

        switch (type) {
            case "brick":
                setModel(Model.BRICK);
                setElevation(50);
                break;
            case "stone":
                setModel(Model.STONE);
                setElevation(50);
                break;
            case "forest":
                setModel(Model.FOREST);
                setElevation(90);
                break;
            case "water":
                setModel(Model.WATER);
                setElevation(0);
                break;
            case "ice":
                setModel(Model.ICE);
                setElevation(0);
                break;
            case "baseUpperLeft":
                setModel(Model.BASE);
                setElevation(50);
                mBasePart = BasePart.UPPER_LEFT;
                break;
            case "baseUpperRight":
                setModel(Model.BASE);
                setElevation(50);
                mBasePart = BasePart.UPPER_RIGHT;
                break;
            case "baseLowerLeft":
                setModel(Model.BASE);
                setElevation(50);
                mBasePart = BasePart.LOWER_LEFT;
                break;
            case "baseLowerRight":
                setModel(Model.BASE);
                setElevation(50);
                mBasePart = BasePart.LOWER_RIGHT;
                break;
            default:
                break;
        }

        int y = mConvRow * 24 - 12;
        int yMinus = y - 6;
        int yPlus = y + 6;

        int x = mConvColumn * 24 - 12;
        int xMinus = x - 6;
        int xPlus = x + 6;

        switch (mConvID) {
            case 1:
                setCenter(new Point(xMinus, yMinus));
                Cc.getInstance().putTerrainIntoMatrix((mConvRow * 2 - 1) * 100 + (mConvColumn * 2 - 1), this);
                break;
            case 2:
                setCenter(new Point(xPlus, yMinus));
                Cc.getInstance().putTerrainIntoMatrix((mConvRow * 2 - 1) * 100 + (mConvColumn * 2), this);
                break;
            case 3:
                setCenter(new Point(xMinus, yPlus));
                Cc.getInstance().putTerrainIntoMatrix((mConvRow * 2) * 100 + (mConvColumn * 2 - 1), this);
                break;
            case 4:
                setCenter(new Point(xPlus, yPlus));
                Cc.getInstance().putTerrainIntoMatrix((mConvRow * 2) * 100 + (mConvColumn * 2), this);
                break;
            default:
                break;
        }

        setSideLength(12);
        updateRectangle();
        updateSubimage();

    }

    @Override
    public void move() {

    }

    @Override
    public void draw(Graphics g) {
        updateSubimage();
        g.drawImage(getSubimage(), getRectangle().x, getRectangle().y, getSideLength(), getSideLength(), null);
    }

    private void updateSubimage() {
        switch (getModel()) {
            case BRICK:
                updateBrick();
                break;
            case STONE:
                updateStone();
                break;
            case FOREST:
                updateForest();
                break;
            case WATER:
                updateWater();
                break;
            case ICE:
                updateIce();
                break;
            case BASE:
                updateBase();
                break;
            default:
                break;
        }
    }

    private void updateBrick() {
        switch (mConvID) {
            case 1:
                setSubimage(Game.getImage().getSubimage(16 * 16, 0, 4, 4));
                break;
            case 2:
                setSubimage(Game.getImage().getSubimage(16 * 16 + 4, 0, 4, 4));
                break;
            case 3:
                setSubimage(Game.getImage().getSubimage(16 * 16, 4, 4, 4));
                break;
            case 4:
                setSubimage(Game.getImage().getSubimage(16 * 16 + 4, 4, 4, 4));
                break;
            default:
                break;
        }
    }

    private void updateStone() {
        switch (mConvID) {
            case 1:
                setSubimage(Game.getImage().getSubimage(16 * 16, 16, 4, 4));
                break;
            case 2:
                setSubimage(Game.getImage().getSubimage(16 * 16 + 4, 16, 4, 4));
                break;
            case 3:
                setSubimage(Game.getImage().getSubimage(16 * 16, 4 + 16, 4, 4));
                break;
            case 4:
                setSubimage(Game.getImage().getSubimage(16 * 16 + 4, 4 + 16, 4, 4));
                break;
            default:
                break;
        }
    }

    private void updateForest() {
        switch (mConvID) {
            case 1:
                setSubimage(Game.getImage().getSubimage(17 * 16, 32, 4, 4));
                break;
            case 2:
                setSubimage(Game.getImage().getSubimage(17 * 16 + 4, 32, 4, 4));
                break;
            case 3:
                setSubimage(Game.getImage().getSubimage(17 * 16, 4 + 32, 4, 4));
                break;
            case 4:
                setSubimage(Game.getImage().getSubimage(17 * 16 + 4, 4 + 32, 4, 4));
                break;
            default:
                break;
        }
    }

    private void updateWater() {
        switch (mConvID) {
            case 1:
                setSubimage(Game.getImage().getSubimage(16 * 16, 32, 4, 4));
                break;
            case 2:
                setSubimage(Game.getImage().getSubimage(16 * 16 + 4, 32, 4, 4));
                break;
            case 3:
                setSubimage(Game.getImage().getSubimage(16 * 16, 4 + 32, 4, 4));
                break;
            case 4:
                setSubimage(Game.getImage().getSubimage(16 * 16 + 4, 4 + 32, 4, 4));
                break;
            default:
                break;
        }
    }

    private void updateIce() {
        switch (mConvID) {
            case 1:
                setSubimage(Game.getImage().getSubimage(18 * 16, 32, 4, 4));
                break;
            case 2:
                setSubimage(Game.getImage().getSubimage(18 * 16 + 4, 32, 4, 4));
                break;
            case 3:
                setSubimage(Game.getImage().getSubimage(18 * 16, 4 + 32, 4, 4));
                break;
            case 4:
                setSubimage(Game.getImage().getSubimage(18 * 16 + 4, 4 + 32, 4, 4));
                break;
            default:
                break;
        }
    }

    private void updateBase() {
        switch (mBasePart) {
            case UPPER_LEFT:
                switch (mConvID) {
                    case 1:
                        setSubimage(Game.getImage().getSubimage(19 * 16, 32, 4, 4));
                        break;
                    case 2:
                        setSubimage(Game.getImage().getSubimage(19 * 16 + 4, 32, 4, 4));
                        break;
                    case 3:
                        setSubimage(Game.getImage().getSubimage(19 * 16, 4 + 32, 4, 4));
                        break;
                    case 4:
                        setSubimage(Game.getImage().getSubimage(19 * 16 + 4, 4 + 32, 4, 4));
                        break;
                    default:
                        break;
                }
                break;

            case UPPER_RIGHT:
                switch (mConvID) {
                    case 1:
                        setSubimage(Game.getImage().getSubimage(19 * 16 + 8, 32, 4, 4));
                        break;
                    case 2:
                        setSubimage(Game.getImage().getSubimage(19 * 16 + 4 + 8, 32, 4, 4));
                        break;
                    case 3:
                        setSubimage(Game.getImage().getSubimage(19 * 16 + 8, 4 + 32, 4, 4));
                        break;
                    case 4:
                        setSubimage(Game.getImage().getSubimage(19 * 16 + 4 + 8, 4 + 32, 4, 4));
                        break;
                    default:
                        break;
                }
                break;

            case LOWER_LEFT:
                switch (mConvID) {
                    case 1:
                        setSubimage(Game.getImage().getSubimage(19 * 16, 32 + 8, 4, 4));
                        break;
                    case 2:
                        setSubimage(Game.getImage().getSubimage(19 * 16 + 4, 32 + 8, 4, 4));
                        break;
                    case 3:
                        setSubimage(Game.getImage().getSubimage(19 * 16, 4 + 32 + 8, 4, 4));
                        break;
                    case 4:
                        setSubimage(Game.getImage().getSubimage(19 * 16 + 4, 4 + 32 + 8, 4, 4));
                        break;
                    default:
                        break;
                }
                break;

            case LOWER_RIGHT:
                switch (mConvID) {
                    case 1:
                        setSubimage(Game.getImage().getSubimage(19 * 16 + 8, 32 + 8, 4, 4));
                        break;
                    case 2:
                        setSubimage(Game.getImage().getSubimage(19 * 16 + 4 + 8, 32 + 8, 4, 4));
                        break;
                    case 3:
                        setSubimage(Game.getImage().getSubimage(19 * 16 + 8, 4 + 32 + 8, 4, 4));
                        break;
                    case 4:
                        setSubimage(Game.getImage().getSubimage(19 * 16 + 4 + 8, 4 + 32 + 8, 4, 4));
                        break;
                    default:
                        break;
                }
                break;

            default:
                break;
        }
    }

    public void beGround() {
        setModel(Model.GROUND);
        setSubimage(Game.getImage().getSubimage(0, 0, 1, 1));
    }

}



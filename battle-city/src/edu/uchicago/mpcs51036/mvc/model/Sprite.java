package edu.uchicago.mpcs51036.mvc.model;

import edu.uchicago.mpcs51036.mvc.controller.Game.Orientation;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public abstract class Sprite implements Movable {

    private Team mTeam;
    private Model mModel;

    private Point mCenter;
    private int mSideLength;
    private Rectangle mRectangle;
    private int mSpeedValue;
    private int mSpeedX;
    private int mSpeedY;
    private Orientation mOrientation;
    private BufferedImage mSubimage;
    private int mElevation;

    private boolean mTurning;

    @Override
    public void move() {

    }

    @Override
    public void draw(Graphics g) {

    }

    @Override
    public Team getTeam() {
        return mTeam;
    }

    @Override
    public void setTeam(Team team) {
        mTeam = team;
    }

    @Override
    public Model getModel() {
        return mModel;
    }

    @Override
    public void setModel(Model model) {
        mModel = model;
    }

    @Override
    public Point getCenter() {
        return mCenter;
    }

    @Override
    public void setCenter(Point pntCenter) {
        mCenter = pntCenter;
    }

    @Override
    public int getSideLength() {
        return mSideLength;
    }

    @Override
    public void setSideLength(int sideLength) {
        mSideLength = sideLength;
    }

    @Override
    public Rectangle getRectangle() {
        return mRectangle;
    }

    @Override
    public void setRectangle(Rectangle rect) {
        mRectangle = rect;
    }

    @Override
    public int getSpeedValue() {
        return mSpeedValue;
    }

    @Override
    public void setSpeedValue(int speedValue) {
        mSpeedValue = speedValue;
    }

    @Override
    public int getSpeedX() {
        return mSpeedX;
    }

    @Override
    public void setSpeedX(int speedX) {
        mSpeedX = speedX;
    }

    @Override
    public int getSpeedY() {
        return mSpeedY;
    }

    @Override
    public void setSpeedY(int speedY) {
        mSpeedY = speedY;
    }

    @Override
    public Orientation getOrientation() {
        return mOrientation;
    }

    @Override
    public void setOrientation(Orientation orientation) {
        mOrientation = orientation;
    }

    @Override
    public BufferedImage getSubimage() {
        return mSubimage;
    }

    @Override
    public void setSubimage(BufferedImage bufSubimage) {
        mSubimage = bufSubimage;
    }

    @Override
    public int getElevation() {
        return mElevation;
    }

    @Override
    public void setElevation(int elevation) {
        mElevation = elevation;
    }

    @Override
    public void updateCenter() {
        setCenter(new Point(getCenter().x + getSpeedX(), getCenter().y + getSpeedY()));
    }

    @Override
    public void updateRectangle() {
        setRectangle(new Rectangle(getCenter().x - getSideLength() / 2, getCenter().y - getSideLength() / 2, getSideLength(), getSideLength()));
    }

    @Override
    public void makeMove(Orientation orientation) {

        setTurning(getOrientation() != orientation);

        setOrientation(orientation);
        switch (orientation) {
            case TO_RIGHT:
                setSpeedX(getSpeedValue());
                setSpeedY(0);
                break;
            case TO_DOWN:
                setSpeedX(0);
                setSpeedY(getSpeedValue());
                break;
            case TO_LEFT:
                setSpeedX(-getSpeedValue());
                setSpeedY(0);
                break;
            case TO_UP:
                setSpeedX(0);
                setSpeedY(-getSpeedValue());
                break;
            default:
                break;
        }

    }

    @Override
    public void makeStop() {
        setSpeedX(0);
        setSpeedY(0);
    }

    public boolean isTurning() {
        return mTurning;
    }

    public void setTurning(boolean turning) {
        mTurning = turning;
    }

}



package edu.uchicago.mpcs51036.mvc.model;

import edu.uchicago.mpcs51036.mvc.controller.Game.Orientation;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public interface Movable {

    enum Team {
        PLAYER, ENEMY, TERRAIN
    }

    enum Model {
        TANK, BULLET, BRICK, STONE, FOREST, WATER, ICE, BASE, GROUND
    }

    void move();
    void draw(Graphics g);

    Team getTeam();
    Model getModel();
    void setTeam(Team team);
    void setModel(Model model);

    Point getCenter();
    int getSideLength();
    Rectangle getRectangle();
    int getSpeedValue();
    int getSpeedX();
    int getSpeedY();
    Orientation getOrientation();
    BufferedImage getSubimage();
    int getElevation();

    void setCenter(Point pntCenter);
    void setSideLength(int sideLength);
    void setRectangle(Rectangle rect);
    void setSpeedValue(int speedValue);
    void setSpeedX(int speedX);
    void setSpeedY(int speedY);
    void setOrientation(Orientation orientation);
    void setSubimage(BufferedImage bufSubimage);
    void setElevation(int elevation);

    void updateCenter();
    void updateRectangle();

    void makeMove(Orientation orientation);
    void makeStop();

}



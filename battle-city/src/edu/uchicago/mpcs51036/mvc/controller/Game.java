package edu.uchicago.mpcs51036.mvc.controller;

import edu.uchicago.mpcs51036.mvc.model.Cc;
import edu.uchicago.mpcs51036.mvc.model.CollisionOp;
import edu.uchicago.mpcs51036.mvc.model.Movable;
import edu.uchicago.mpcs51036.mvc.model.Tank;
import edu.uchicago.mpcs51036.mvc.view.GamePanel;
import edu.uchicago.mpcs51036.sound.Sound;

import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class Game implements Runnable, KeyListener {

    public enum GlobalStatus {
        CHOOSING_ONE_OR_TWO_PLAYERS,
        DISPLAYING_LEVEL_NUMBER,
        BATTLING,
        BATTLING_AND_PAUSED,
        DISPLAYING_LEVEL_RESULT,
        DISPLAYING_GAME_OVER
    }

    public enum Orientation {
        TO_UP, TO_DOWN, TO_LEFT, TO_RIGHT
    }

    private static final int PAUSE = 80, // p key
                              QUIT = 81, // q key
                         PLAYER1UP = 87, // w key
                       PLAYER1DOWN = 83, // s key
                       PLAYER1LEFT = 65, // a key
                      PLAYER1RIGHT = 68, // d key
                       PLAYER1FIRE = 32, // space key
                         PLAYER2UP = 38, // up arrow
                       PLAYER2DOWN = 40, // down arrow
                       PLAYER2LEFT = 37, // left arrow
                      PLAYER2RIGHT = 39, // right arrow
                       PLAYER2FIRE = 10; // enter key

    public static final Dimension DIM = new Dimension(850, 48 * 13);  // The dimension of the game
    private static final int ANIMATION_DELAY = 50; // The time between frames in milliseconds
    private static final String PREFIX = "src/edu/uchicago/mpcs51036/";

    private static BufferedImage image;
    private static BufferedImage imageWelcome;
    private static Font font;
    private static Clip clipMusicBackground;

    private static GlobalStatus globalStatus;
    private static int statusCount;
    private static boolean singlePlayer;

    private GamePanel gmpPanel;
    private Thread threadAnimation;
    private int nTick;
    private int enemyTankID;

    public static void main(String args[]) {
        EventQueue.invokeLater(() -> {
            try {
                Game game = new Game();
                game.fireUpAnimationThread();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private Game() {
        try {
            image = ImageIO.read(new File(PREFIX + "image/BattleCityImage.png"));
            imageWelcome = ImageIO.read(new File(PREFIX + "image/BattleCityWelcome.png"));
            font = Font.createFont(Font.TRUETYPE_FONT, new File(PREFIX + "font/BattleCityFont.ttf"));
            clipMusicBackground = Sound.clipForLoopFactory("BattleCityWhiteNoise.wav");
        } catch (Exception e) {
            e.printStackTrace();
        }

        gmpPanel = new GamePanel();
        gmpPanel.addKeyListener(this);

        setGlobalStatus(GlobalStatus.CHOOSING_ONE_OR_TWO_PLAYERS);
        setStatusCount(0);
        singlePlayer = true;
        nTick = 0;
        enemyTankID = 1;
    }

    private void fireUpAnimationThread() {
        if (threadAnimation == null) {
            threadAnimation = new Thread(this);
            threadAnimation.start();  // Start the new thread and call the run() method
        }
    }

    public void run() {
        threadAnimation.setPriority(Thread.MIN_PRIORITY);
        long lStartTime = System.currentTimeMillis();
        while (Thread.currentThread() == threadAnimation) {
            tick();
            gmpPanel.update(gmpPanel.getGraphics());
            executeGameOpsList();
            lStartTime += ANIMATION_DELAY;  // Continuously increment lStartTime for every frame
            try {
                Thread.sleep(Math.max(0, lStartTime - System.currentTimeMillis()));
            } catch (InterruptedException e) {
                // Ignore this frame
            }
        }
    }

    private void tick() {
        if (Game.getGlobalStatus() == GlobalStatus.BATTLING_AND_PAUSED) {
            return;
        }
        if (++nTick == Cc.getInstance().getAppearTime()) {
            Cc.getInstance().getOpsList().enqueue(new Tank(Movable.Team.ENEMY, enemyTankID++), CollisionOp.Operation.ADD);
            Cc.getInstance().updateAppearTime();
        }
    }

    private void executeGameOpsList() {
        while (!Cc.getInstance().getOpsList().isEmpty()) {
            CollisionOp cop = Cc.getInstance().getOpsList().dequeue();
            Movable mov = cop.getMovable();
            CollisionOp.Operation operation = cop.getOperation();

            if (mov.getModel() == Movable.Model.TANK) {
                if (operation == CollisionOp.Operation.ADD) {
                    Cc.getInstance().getMovTanks().add(mov);
                } else {
                    Cc.getInstance().getMovTanks().remove(mov);
                }
            } else if (mov.getModel() == Movable.Model.BULLET) {
                if (operation == CollisionOp.Operation.ADD) {
                    Cc.getInstance().getMovBullets().add(mov);
                } else {
                    Cc.getInstance().getMovBullets().remove(mov);
                }
            } else {  // Terrain
                if (operation == CollisionOp.Operation.ADD) {
                    Cc.getInstance().getMovTerrains().add(mov);
                } else {
                    Cc.getInstance().getMovTerrains().remove(mov);
                }
            }
        }
        System.gc();
    }

    public static GlobalStatus getGlobalStatus() {
        return globalStatus;
    }

    public static void setGlobalStatus(GlobalStatus status) {
        globalStatus = status;
    }

    public static int getStatusCount() {
        return statusCount;
    }

    public static void setStatusCount(int count) {
        statusCount = count;
    }

    public static boolean isSinglePlayer() {
        return singlePlayer;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int nKey = e.getKeyCode();
        if (getGlobalStatus() == GlobalStatus.CHOOSING_ONE_OR_TWO_PLAYERS) {
            switch (nKey) {
                case PLAYER2UP:
                    singlePlayer = true;
                    break;
                case PLAYER2DOWN:
                    singlePlayer = false;
                    break;
                case PLAYER2FIRE:
                    setGlobalStatus(GlobalStatus.DISPLAYING_LEVEL_NUMBER);
                    setStatusCount(0);
                    nTick = 0;
                    Cc.getInstance().initBattle();
                    break;
                default:
                    break;
            }
            return;
        }
        if (getGlobalStatus() != GlobalStatus.BATTLING) {
            return;
        }
        Tank tankPlayer1 = Cc.getInstance().getTankPlayerOne();
        Tank tankPlayer2 = Cc.getInstance().getTankPlayerTwo();
        switch (nKey) {
            case QUIT:
                System.exit(0);
                break;
            case PLAYER1UP:
                if (tankPlayer1 != null) {
                    tankPlayer1.makeMove(Orientation.TO_UP);
                }
                break;
            case PLAYER1DOWN:
                if (tankPlayer1 != null) {
                    tankPlayer1.makeMove(Orientation.TO_DOWN);
                }
                break;
            case PLAYER1LEFT:
                if (tankPlayer1 != null) {
                    tankPlayer1.makeMove(Orientation.TO_LEFT);
                }
                break;
            case PLAYER1RIGHT:
                if (tankPlayer1 != null) {
                    tankPlayer1.makeMove(Orientation.TO_RIGHT);
                }
                break;
            case PLAYER1FIRE:
                if (tankPlayer1 != null) {
                    tankPlayer1.tryFireBullet();
                }
                break;
            case PLAYER2UP:
                if (tankPlayer2 != null) {
                    tankPlayer2.makeMove(Orientation.TO_UP);
                }
                break;
            case PLAYER2DOWN:
                if (tankPlayer2 != null) {
                    tankPlayer2.makeMove(Orientation.TO_DOWN);
                }
                break;
            case PLAYER2LEFT:
                if (tankPlayer2 != null) {
                    tankPlayer2.makeMove(Orientation.TO_LEFT);
                }
                break;
            case PLAYER2RIGHT:
                if (tankPlayer2 != null) {
                    tankPlayer2.makeMove(Orientation.TO_RIGHT);
                }
                break;
            case PLAYER2FIRE:
                if (tankPlayer2 != null) {
                    tankPlayer2.tryFireBullet();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased (KeyEvent e) {
        int nKey = e.getKeyCode();
        if (getGlobalStatus() == GlobalStatus.BATTLING_AND_PAUSED && nKey == PAUSE) {
            setGlobalStatus(GlobalStatus.BATTLING);
            return;
        }
        if (getGlobalStatus() != GlobalStatus.BATTLING) {
            return;
        }
        Tank tankPlayer1 = Cc.getInstance().getTankPlayerOne();
        Tank tankPlayer2 = Cc.getInstance().getTankPlayerTwo();
        switch (nKey) {
            case PAUSE:
                setGlobalStatus(GlobalStatus.BATTLING_AND_PAUSED);
                break;
            case PLAYER1UP:
                if (tankPlayer1 != null && tankPlayer1.getOrientation() == Orientation.TO_UP) {
                    tankPlayer1.makeStop();
                }
                break;
            case PLAYER1DOWN:
                if (tankPlayer1 != null && tankPlayer1.getOrientation() == Orientation.TO_DOWN) {
                    tankPlayer1.makeStop();
                }
                break;
            case PLAYER1LEFT:
                if (tankPlayer1 != null && tankPlayer1.getOrientation() == Orientation.TO_LEFT) {
                    tankPlayer1.makeStop();
                }
                break;
            case PLAYER1RIGHT:
                if (tankPlayer1 != null && tankPlayer1.getOrientation() == Orientation.TO_RIGHT) {
                    tankPlayer1.makeStop();
                }
                break;
            case PLAYER2UP:
                if (tankPlayer2 != null && tankPlayer2.getOrientation() == Orientation.TO_UP) {
                    tankPlayer2.makeStop();
                }
                break;
            case PLAYER2DOWN:
                if (tankPlayer2 != null && tankPlayer2.getOrientation() == Orientation.TO_DOWN) {
                    tankPlayer2.makeStop();
                }
                break;
            case PLAYER2LEFT:
                if (tankPlayer2 != null && tankPlayer2.getOrientation() == Orientation.TO_LEFT) {
                    tankPlayer2.makeStop();
                }
                break;
            case PLAYER2RIGHT:
                if (tankPlayer2 != null && tankPlayer2.getOrientation() == Orientation.TO_RIGHT) {
                    tankPlayer2.makeStop();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public static BufferedImage getImage() {
        return image;
    }
    public static BufferedImage getImageWelcome() {
        return imageWelcome;
    }

    public static Font getFont(float fontSize) {
        return font.deriveFont(fontSize);
    }

    public static Clip getClipMusicBackground() {
        return clipMusicBackground;
    }

}



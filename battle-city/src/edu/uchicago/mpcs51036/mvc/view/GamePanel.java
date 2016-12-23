package edu.uchicago.mpcs51036.mvc.view;

import edu.uchicago.mpcs51036.mvc.model.Cc;
import edu.uchicago.mpcs51036.mvc.model.Movable;
import edu.uchicago.mpcs51036.mvc.controller.Game;
import edu.uchicago.mpcs51036.sound.Sound;

import javax.sound.sampled.Clip;
import java.awt.Panel;
import java.awt.FontMetrics;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;

public class GamePanel extends Panel {

    private static final int X_LEFT = 130;
    private static final int X_RIGHT = 490;
    private static final int Y_PLAYER = 170;
    private static final int Y_DELTA = 45;

    private static final BufferedImage IMAGE_WELCOME = Game.getImageWelcome();
    private static final BufferedImage IMAGE_TANK_ARROW = Game.getImage().getSubimage(96, 0, 16, 16);
    private static final BufferedImage IMAGE_ENEMY_TANK = Game.getImage().getSubimage(8 * 16, 2 * 16, 16, 16);
    private static final BufferedImage IMAGE_PLAYER_1 = Game.getImage().getSubimage(0, 0, 16, 16);
    private static final BufferedImage IMAGE_PLAYER_2 = Game.getImage().getSubimage(0, 8 * 16, 16, 16);
    private static final BufferedImage IMAGE_BASE_DESTROYED = Game.getImage().getSubimage(20 * 16, 32, 16, 16);

    private static final int CHOOSE_PLAYER_NUMBER_POS = 100;

    private Dimension dimOff;
    private Image imgOff;
    private Graphics grpOff;

    private FontMetrics fmt;

    private int delayCount = 0;

    public GamePanel(){

        this.setPreferredSize(Game.DIM);

        GameFrame gmf = new GameFrame();
        gmf.getContentPane().add(this);
        gmf.pack();
        gmf.setTitle("Battle City");
        gmf.setResizable(false);
        gmf.setVisible(true);

        this.setFocusable(true);

        fmt = getGraphics().getFontMetrics();

    }

    public void update(Graphics g) {
        if (grpOff == null || Game.DIM.width != dimOff.width || Game.DIM.height != dimOff.height) {
            dimOff = Game.DIM;
            imgOff = createImage(Game.DIM.width, Game.DIM.height);
            grpOff = imgOff.getGraphics();
        }

        displayBackground(grpOff, Color.BLACK);  // Clean up the screen

        switch (Game.getGlobalStatus()) {
            case CHOOSING_ONE_OR_TWO_PLAYERS:
                choosingOneOrTwoPlayers(g);
                break;
            case DISPLAYING_LEVEL_NUMBER:
                displayingLevelNumber(g);
                break;
            case BATTLING:
                battling(g);
                break;
            case BATTLING_AND_PAUSED:
                battlingAndPaused(g);
                break;
            case DISPLAYING_LEVEL_RESULT:
                displayingLevelResult(g);
                break;
            case DISPLAYING_GAME_OVER:
                displayingGameOver(g);
                break;
            default:
                break;
        }
    }

    private void choosingOneOrTwoPlayers(Graphics g) {
        if (Game.getStatusCount() <= CHOOSE_PLAYER_NUMBER_POS) {
            displayChoosingPlayerAnimation(g);
            Game.setStatusCount(Game.getStatusCount() + 1);
        } else {
            displayChoosingPlayerOption(g);
        }
    }

    private void displayChoosingPlayerAnimation(Graphics g) {
        displayBackground(g, Color.BLACK);
        int offset = Game.DIM.height * (CHOOSE_PLAYER_NUMBER_POS - Game.getStatusCount()) / CHOOSE_PLAYER_NUMBER_POS;
        g.drawImage(IMAGE_WELCOME, 0, offset, Game.DIM.width, Game.DIM.height, null);
    }

    private void displayChoosingPlayerOption(Graphics g) {
        g.drawImage(IMAGE_WELCOME, 0, 0, Game.DIM.width, Game.DIM.height, null);
        if (Game.isSinglePlayer()) {
            g.drawImage(IMAGE_TANK_ARROW, 240, 425, 48, 48, null);
        } else {
            g.drawImage(IMAGE_TANK_ARROW, 240, 475, 48, 48, null);
        }
    }

    private void displayingLevelNumber(Graphics g) {
        if (Game.getStatusCount() < 100) {
            displayLevelNumberShow(g);
        } else {
            displayLevelNumberChange();
        }
    }

    private void displayLevelNumberShow(Graphics g) {
        if (Game.getStatusCount() == 0) {
            Sound.playSound("BattleCityStageStart.wav");
        }
        displayBackground(g, Color.LIGHT_GRAY);
        g.setColor(Color.BLACK);
        g.setFont(Game.getFont(20f));
        String str = "STAGE " + Cc.getInstance().getLevel();
        g.drawString(str, (Game.DIM.width - fmt.stringWidth(str)) / 2 - 40, Game.DIM.height / 4 + 50);
        Game.setStatusCount(Game.getStatusCount() + 1);
    }

    private void displayLevelNumberChange() {
        Game.setStatusCount(0);
        Game.setGlobalStatus(Game.GlobalStatus.BATTLING);
        Game.getClipMusicBackground().loop(Clip.LOOP_CONTINUOUSLY);
    }

    private void battling(Graphics g) {
        if (Game.getStatusCount() > 0 && isAllEnemyClear()) {
            displayBattlingAllClearDelay();
        } else {
            displayBattlingOngoing();
        }
        displayBattlingCommon(g);
    }

    private void displayBattlingAllClearDelay() {
        ++delayCount;
        if (delayCount == 15) {
            Game.setStatusCount(0);
            Game.setGlobalStatus(Game.GlobalStatus.DISPLAYING_LEVEL_RESULT);
        }
    }

    private void displayBattlingOngoing() {
        Game.setStatusCount(Game.getStatusCount() + 1);
        if (Cc.getInstance().getTankPlayerOne() == null) {
            Cc.getInstance().tryMakePlayerTankOnScreen(1);
        }
        if (Game.isSinglePlayer()) {
            return;
        }
        if (Cc.getInstance().getTankPlayerTwo() == null) {
            Cc.getInstance().tryMakePlayerTankOnScreen(2);
        }
    }

    private void displayBattlingCommon(Graphics g) {
        iterateMovables(grpOff,
                (ArrayList<Movable>) Cc.getInstance().getMovTerrains(),
                (ArrayList<Movable>) Cc.getInstance().getMovTanks(),
                (ArrayList<Movable>) Cc.getInstance().getMovBullets());

        displayRightSideInformation(grpOff);

        g.drawImage(imgOff, 0, 0, this);
    }

    private void battlingAndPaused(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(Game.getFont(20f));
        String str = "Paused";
        g.drawString(str, (Game.DIM.width - fmt.stringWidth(str)) / 2 - 40, 80);
    }

    private void displayingLevelResult(Graphics g) {

        int count = Game.getStatusCount() + 1;
        Game.setStatusCount(count);

        if (count > 15) {
            displayBackground(g, Color.BLACK);
            displayResultStage(g);
            displayResultPlayer(g);
        }

        if (count > 40) {
            displayResultDetail(g, 1);
        }

        if (count > 60) {
            displayResultDetail(g, 2);
        }

        if (count > 80) {
            displayResultDetail(g, 3);
        }

        if (count > 100) {
            displayResultDetail(g, 4);
        }

        if (count > 120) {
            displayResultLine(g);
            displayResultTotal(g);
        }

        if (count > 140) {
            displayResultScore(g);
        }

    }

    private void displayBackground(Graphics g, Color color) {
        g.setColor(color);
        g.fillRect(0, 0, Game.DIM.width, Game.DIM.height);
    }

    private void displayResultStage(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(Game.getFont(20f));
        String str = "STAGE " + Cc.getInstance().getLevel();
        g.drawString(str, (Game.DIM.width - fmt.stringWidth(str)) / 2 - 40, 80);
    }

    private void displayResultCommon(Graphics g, String str, Color color, int column, int line) {
        g.setColor(color);
        g.drawString(str, column, Y_PLAYER + Y_DELTA * line);
    }

    private void displayResultPlayer(Graphics g) {
        String str = "PLAYER I";
        displayResultCommon(g, str, Color.ORANGE, X_LEFT, 0);
        if (Game.isSinglePlayer()) {
            return;
        }
        str = "PLAYER II";
        displayResultCommon(g, str, Color.ORANGE, X_RIGHT, 0);
    }

    private void displayResultDetail(Graphics g, int line) {
        String str = line + "00 PTS * " + Cc.getInstance().getKillEnemyRecord(1, line);
        displayResultCommon(g, str, Color.WHITE, X_LEFT, line);
        if (Game.isSinglePlayer()) {
            return;
        }
        str = line + "00 PTS * " + Cc.getInstance().getKillEnemyRecord(2, line);
        displayResultCommon(g, str, Color.WHITE, X_RIGHT, line);
    }

    private void displayResultLine(Graphics g) {
        String str = "--------------";
        displayResultCommon(g, str, Color.WHITE, X_LEFT, 5);
        if (Game.isSinglePlayer()) {
            return;
        }
        displayResultCommon(g, str, Color.WHITE, X_RIGHT, 5);
    }

    private void displayResultTotal(Graphics g) {
        String str = "TOTAL     " + calculateTotal(1);
        displayResultCommon(g, str, Color.ORANGE, X_LEFT, 6);
        if (Game.isSinglePlayer()) {
            return;
        }
        str = "TOTAL     " + calculateTotal(2);
        displayResultCommon(g, str, Color.ORANGE, X_RIGHT, 6);
    }

    private void displayResultScore(Graphics g) {
        String str = "SCORE " + calculateScore(1) + " PTS";
        displayResultCommon(g, str, Color.RED, X_LEFT, 7);
        if (Game.isSinglePlayer()) {
            return;
        }
        str = "SCORE " + calculateScore(2) + " PTS";
        displayResultCommon(g, str, Color.RED, X_RIGHT, 7);
    }

    private int calculateTotal(int playerID) {
        int sum = 0;
        for (int line = 1; line <= 4; ++line) {
            sum += Cc.getInstance().getKillEnemyRecord(playerID, line);
        }
        return sum;
    }

    private int calculateScore(int playerID) {
        int sum = 0;
        for (int line = 1; line <= 4; ++line) {
            sum += line * 100 * Cc.getInstance().getKillEnemyRecord(playerID, line);
        }
        return sum;
    }

    private void displayingGameOver(Graphics g) {
        if (Game.getStatusCount() < 40) {
            displayGameOverBaseDestroyed(g);
        } else {
            displayGameOverStatic(g);
        }
    }

    private void displayGameOverBaseDestroyed(Graphics g) {
        if (Game.getStatusCount() == 0) {
            Sound.playSound("BattleCityGameOver.wav");
        }

        List<Movable> movTerrains = Cc.getInstance().getMovTerrains();
        for (Movable mov : movTerrains) {
            mov.draw(grpOff);
        }
        List<Movable> movTanks = Cc.getInstance().getMovTanks();
        for (Movable mov : movTanks) {
            mov.draw(grpOff);
        }
        List<Movable> movBullets = Cc.getInstance().getMovBullets();
        for (Movable mov : movBullets) {
            mov.draw(grpOff);
        }

        grpOff.drawImage(IMAGE_BASE_DESTROYED, 6 * 48, 12 * 48, 48, 48, null);

        displayRightSideInformation(grpOff);

        g.drawImage(imgOff, 0, 0, this);

        Game.setStatusCount(Game.getStatusCount() + 1);
    }

    private void displayGameOverStatic(Graphics g) {
        displayBackground(g, Color.LIGHT_GRAY);
        g.setColor(Color.RED);
        String str = "GAME OVER";
        g.setFont(Game.getFont(20f));
        g.drawString(str, (Game.DIM.width - fmt.stringWidth(str)) / 2 - 50, Game.DIM.height / 4 + 50);
    }

    private void iterateMovables(Graphics g, ArrayList<Movable>... movMovz){
        for (ArrayList<Movable> movMovs : movMovz) {
            for (Movable mov : movMovs) {
                mov.move();
                mov.draw(g);
            }
        }
    }

    private void displayRightSideInformation(Graphics g) {

        g.setColor(Color.DARK_GRAY);
        g.fillRect(13 * 48, 0, 40, Game.DIM.height);

        int startX = 48 * 13 + 100;
        int startY = 40;
        int side = 32;
        int deltaX = 10;
        int deltaY = 10;

        int enemy = Cc.getInstance().getNumberOfEnemyNotOnScreen();
        if (enemy > 0) {
            int row = 0;
            for ( ; row < enemy / 2; ++row) {
                g.drawImage(IMAGE_ENEMY_TANK, startX, startY + row * (side + deltaY), side, side, null);
                g.drawImage(IMAGE_ENEMY_TANK, startX + side + deltaX, startY + row * (side + deltaY), side, side, null);
            }
            if (enemy % 2 == 1) {
                g.drawImage(IMAGE_ENEMY_TANK, startX, startY + row * (side + deltaY), side, side, null);
            }
        }

        displayRightSideInfoHelper(g, 1, "PLAYER I", IMAGE_PLAYER_1, startX, 450, 470, 495);
        if (Game.isSinglePlayer()) {
            return;
        }
        displayRightSideInfoHelper(g, 2, "PLAYER II", IMAGE_PLAYER_2, startX, 550, 570, 595);

    }

    private void displayRightSideInfoHelper(Graphics g, int playerID, String str, BufferedImage image, int startX, int y1, int y2, int y3) {
        g.setColor(Color.WHITE);
        g.setFont(Game.getFont(14f));
        g.drawString(str, startX - 20, y1);

        g.drawImage(image, startX - 10, y2, 32, 32, null);

        str = Integer.toString(Cc.getInstance().getPlayerLife(playerID));
        g.drawString(str, startX + 40, y3);
    }

    private boolean isAllEnemyClear() {
        boolean allEnemyClear = true;
        List<Movable> movTanks = Cc.getInstance().getMovTanks();
        for (Movable movTank : movTanks) {
            if (movTank.getTeam() == Movable.Team.ENEMY) {
                allEnemyClear = false;
                break;
            }
        }
        if (Cc.getInstance().getAppearTime() != -999) {
            allEnemyClear = false;
        }
        return allEnemyClear;
    }

}



package edu.uchicago.mpcs51036.mvc.model;

import edu.uchicago.mpcs51036.map.BattleCityMap;
import edu.uchicago.mpcs51036.mvc.controller.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cc {

    enum Direction {
        LEFT_OR_RIGHT, UP_OR_DOWN
    }

    private static final int PLAYER_LIFE_INITIAL_VALUE = 3;
    private static final int TIME_FACTOR_APPEAR_ENEMY_TANK = 90;

    private int mLifeOfPlayerOne;
    private int mLifeOfPlayerTwo;
    private int mLevel;
    private int[][] mKillEnemyRecord;
    private boolean mPlayerOneIsOver = true;
    private boolean mPlayerTwoIsOver = true;

    private List<Movable> mMovTanks = new ArrayList<>(200);
    private List<Movable> mMovBullets = new ArrayList<>(500);
    private List<Movable> mMovTerrains = new ArrayList<>(3000);

    private GameOpsList mOpsList = new GameOpsList();

    private List<Integer> mAppearTimeList = new ArrayList<>();
    private int mAppearTime;

    private Map<Integer, Movable> mTerrainMatrix = new HashMap<>();

    private static final Cc INSTANCE = new Cc();

    private Cc() {}

    public static Cc getInstance() {
        return INSTANCE;
    }

    public void initBattle() {

        clearAll();
        mKillEnemyRecord = new int[2][4];

        setLevel(1);

        setPlayerLife(1, PLAYER_LIFE_INITIAL_VALUE);
        tryMakePlayerTankOnScreen(1);
        mPlayerOneIsOver = false;

        if (!Game.isSinglePlayer()) {
            setPlayerLife(2, PLAYER_LIFE_INITIAL_VALUE);
            tryMakePlayerTankOnScreen(2);
            mPlayerTwoIsOver = false;
        }

        BattleCityMap.loadTerrain(getLevel());
        BattleCityMap.loadEnemyTank(getLevel());

    }

    private void setLevel(int level) {
        mLevel = level;
    }

    public int getLevel() {
        return mLevel;
    }

    private void setPlayerLife(int player, int life) {
        if (player == 1) {
            mLifeOfPlayerOne = life;
        } else {
            mLifeOfPlayerTwo = life;
        }
    }

    public int getPlayerLife(int player) {
        if (player == 1) {
            return mLifeOfPlayerOne;
        } else {
            return mLifeOfPlayerTwo;
        }
    }

    public void tryMakePlayerTankOnScreen(int player) {
        if (player == 1) {
            if (getPlayerLife(1) > 0) {
                Tank tankPlayerOne = new Tank(Movable.Team.PLAYER, 1);
                mOpsList.enqueue(tankPlayerOne, CollisionOp.Operation.ADD);
                setPlayerLife(1, getPlayerLife(1) - 1);
            } else {
                mPlayerOneIsOver = true;
                if (mPlayerTwoIsOver) {
                    Game.setGlobalStatus(Game.GlobalStatus.DISPLAYING_GAME_OVER);
                }
            }
        } else {
            if (getPlayerLife(2) > 0) {
                Tank tankPlayerTwo = new Tank(Movable.Team.PLAYER, 2);
                mOpsList.enqueue(tankPlayerTwo, CollisionOp.Operation.ADD);
                setPlayerLife(2, getPlayerLife(2) - 1);
            } else {
                mPlayerTwoIsOver = true;
                if (mPlayerOneIsOver) {
                    Game.setGlobalStatus(Game.GlobalStatus.DISPLAYING_GAME_OVER);
                }
            }
        }
    }

    public GameOpsList getOpsList() {
        return mOpsList;
    }

    private void clearAll() {
        mMovTanks.clear();
        mMovBullets.clear();
        mMovTerrains.clear();
    }

    public Tank getTankPlayerOne() {
        List<Movable> movTanks = Cc.getInstance().getMovTanks();
        Tank resultTank = null;
        for (Movable movTank : movTanks) {
            if (movTank.getTeam() == Movable.Team.PLAYER && ((Tank) movTank).getID() == 1) {
                resultTank = (Tank) movTank;
            }
        }
        return resultTank;
    }

    public Tank getTankPlayerTwo() {
        List<Movable> movTanks = Cc.getInstance().getMovTanks();
        Tank resultTank = null;
        for (Movable movTank : movTanks) {
            if (movTank.getTeam() == Movable.Team.PLAYER && ((Tank) movTank).getID() == 2) {
                resultTank = (Tank) movTank;
            }
        }
        return resultTank;
    }

    public List<Movable> getMovTanks() {
        return mMovTanks;
    }

    public List<Movable> getMovBullets() {
        return mMovBullets;
    }

    public List<Movable> getMovTerrains() {
        return mMovTerrains;
    }

    public void putTerrainIntoMatrix(int index, Movable terrain) {
        mTerrainMatrix.put(index, terrain);
    }

    public Movable getTerrainOutOfMatrix(int index) {
        return mTerrainMatrix.get(index);
    }

    public void removeTerrainFromMatrix(int index) {
        mTerrainMatrix.remove(index);
    }

    public int findAnotherMemberInSameGroup(int myID, int lookforID) {
        int row = myID / 100;
        int rowSmall;
        int rowBig;
        if (row % 2 == 1) {
            rowSmall = row;
            rowBig = row + 1;
        } else {
            rowSmall = row - 1;
            rowBig = row;
        }

        int column = myID - row * 100;
        int columnSmall;
        int columnBig;
        if (column % 2 == 1) {
            columnSmall = column;
            columnBig = column + 1;
        } else {
            columnSmall = column - 1;
            columnBig = column;
        }

        switch (lookforID) {
            case 1:
                return rowSmall * 100 + columnSmall;
            case 2:
                return rowSmall * 100 + columnBig;
            case 3:
                return rowBig * 100 + columnSmall;
            case 4:
                return rowBig * 100 + columnBig;
            default:
                return 0;
        }
    }

    public int findMyPairedMemberInSameGroup(int myID, Direction direction) {
        int row = myID / 100;
        int column = myID - row * 100;

        int rowResult = 0;
        int columnResult = 0;

        switch (direction) {
            case LEFT_OR_RIGHT:
                rowResult = row;
                if (column % 2 == 1) {
                    columnResult = column + 1;
                } else {
                    columnResult = column - 1;
                }
                break;
            case UP_OR_DOWN:
                columnResult = column;
                if (row % 2 == 1) {
                    rowResult = row + 1;
                } else {
                    rowResult = row - 1;
                }
                break;
            default:
                break;
        }

        return rowResult * 100 + columnResult;
    }

    public void updateAppearTime() {
        if (mAppearTimeList.size() == 0) {
            mAppearTime = -999;
        } else {
            mAppearTime = mAppearTimeList.get(0) * TIME_FACTOR_APPEAR_ENEMY_TANK;
            mAppearTimeList.remove(0);
        }
    }

    public int getAppearTime() {
        return mAppearTime;
    }

    public void setAppearTime(List<Integer> list) {
        mAppearTimeList = list;
    }

    public void setAppearTime(int time) {
        mAppearTime = time;
    }

    public int getNumberOfEnemyNotOnScreen() {
        return mAppearTimeList.size();
    }

    public void updateKillEnemyRecord(int player, int enemyTankLevel) {
        mKillEnemyRecord[player-1][enemyTankLevel-1]++;
    }

    public int getKillEnemyRecord(int player, int enemyTankLevel) {
        return mKillEnemyRecord[player-1][enemyTankLevel-1];
    }

}



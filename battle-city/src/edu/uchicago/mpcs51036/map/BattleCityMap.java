package edu.uchicago.mpcs51036.map;

import edu.uchicago.mpcs51036.mvc.model.Cc;
import edu.uchicago.mpcs51036.mvc.model.CollisionOp;
import edu.uchicago.mpcs51036.mvc.model.Terrain;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BattleCityMap {

    private static final String PREFIX = "src/edu/uchicago/mpcs51036/map/";

    public static void loadTerrain(int stageLevel) {
        String fileName = PREFIX + "BattleCityTerrain" + format(stageLevel) + ".txt";
        try {
            Scanner in = new Scanner(new FileReader(fileName));
            while (in.hasNextLine()) {
                String str = in.nextLine();
                if (str.length() > 0) {
                    String[] paras = str.split(",");
                    for (int convID = 1; convID <= 4; ++convID) {
                        Cc.getInstance().getOpsList().enqueue(new Terrain(paras[2], Integer.parseInt(paras[0]), Integer.parseInt(paras[1]), convID), CollisionOp.Operation.ADD);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Something wrong during loading the terrain: " + e.getMessage());
            System.exit(-1);
        }
    }

    public static void loadEnemyTank(int stageLevel) {
        String fileName = PREFIX + "BattleCityEnemyTank" + format(stageLevel) + ".txt";
        try {
            List<Integer> list = new ArrayList<>();
            Scanner in = new Scanner(new FileReader(fileName));
            while (in.hasNextLine()) {
                String str = in.nextLine();
                if (str.length() > 0) {
                    list.add(Integer.parseInt(str));
                }
            }
            Cc.getInstance().setAppearTime(list);
            Cc.getInstance().updateAppearTime();
        } catch (IOException e) {
            System.err.println("Something wrong during loading the enemy tank: " + e.getMessage());
            System.exit(-1);
        }
    }

    private static String format(int stageLevel) {
        if (stageLevel <= 9) {
            return "0" + stageLevel;
        }
        return "" + stageLevel;
    }

}



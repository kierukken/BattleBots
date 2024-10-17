package bots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class MarcusBot extends Bot {
    Bullet[] bulletsOut;
    ArrayList<int[]> dangerSquares = new ArrayList<>();
    ArrayList<int[]> borderDangerSquares = new ArrayList<>();
    ArrayList<int[]> mySquares = new ArrayList<>();
    ArrayList<int[]> testSquares = new ArrayList<>();
    ArrayList<int[]> intersectSquares = new ArrayList<>();
    BotHelper botHelper = new BotHelper();
    int safeSquareArray = -1;
    int currentSquareTest = -1;
    private Image botImage;

    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        try {
            // for (int i = 0; i < bullets.length; i++) { //Basic, lazer based, bullet
            // dodging code, has issue of jittering when encountered with 2 bullets on
            // either side of it
            // if (bullets[i].getXSpeed() != 0) {
            // if (bullets[i].getX() > me.getX()) {if (bullets[i].getXSpeed() < 0) {if
            // (bullets[i].getY() >= me.getY() && bullets[i].getY() <= me.getY() +
            // Bot.RADIUS * 2) {
            // if (bullets[i].getY() >= me.getY() && bullets[i].getY() <= me.getY() +
            // Bot.RADIUS) {
            // return BattleBotArena.DOWN;
            // }
            // if (bullets[i].getY() >= me.getY() + Bot.RADIUS
            // && bullets[i].getY() <= me.getY() + Bot.RADIUS * 2) {
            // return BattleBotArena.UP;
            // }
            // }}}
            // if (bullets[i].getX() < me.getX()) {if (bullets[i].getXSpeed() > 0) {if
            // (bullets[i].getY() >= me.getY() && bullets[i].getY() <= me.getY() +
            // Bot.RADIUS * 2) {
            // if (bullets[i].getY() >= me.getY() && bullets[i].getY() <= me.getY() +
            // Bot.RADIUS) {
            // return BattleBotArena.DOWN;
            // }
            // if (bullets[i].getY() >= me.getY() + Bot.RADIUS
            // && bullets[i].getY() <= me.getY() + Bot.RADIUS * 2) {
            // return BattleBotArena.UP;
            // }
            // }}}

            // }
            // if (bullets[i].getYSpeed() != 0) {
            // if (bullets[i].getY() > me.getY()) {
            // if (bullets[i].getYSpeed() < 0) {
            // if (bullets[i].getX() >= me.getX() && bullets[i].getX() <= me.getX() +
            // Bot.RADIUS * 2) {
            // if (bullets[i].getX() >= me.getX() && bullets[i].getX() <= me.getX() +
            // Bot.RADIUS) {
            // return BattleBotArena.RIGHT;
            // }
            // if (bullets[i].getX() >= me.getX() + Bot.RADIUS
            // && bullets[i].getX() <= me.getX() + Bot.RADIUS * 2) {
            // return BattleBotArena.LEFT;
            // }
            // }
            // }
            // }
            // if (bullets[i].getY() < me.getY()) {
            // if (bullets[i].getYSpeed() > 0) {
            // if (bullets[i].getX() >= me.getX() && bullets[i].getX() <= me.getX() +
            // Bot.RADIUS * 2) {
            // if (bullets[i].getX() >= me.getX() && bullets[i].getX() <= me.getX() +
            // Bot.RADIUS) {
            // return BattleBotArena.RIGHT;
            // }
            // if (bullets[i].getX() >= me.getX() + Bot.RADIUS
            // && bullets[i].getX() <= me.getX() + Bot.RADIUS * 2) {
            // return BattleBotArena.LEFT;
            // }
            // }
            // }

            // }
            // }
            // }

            // attempt at making a grid
            bulletsOut = bullets;
            mySquares.clear();
            for (int i = -1; i < 6; i++) {
                for (int j = -1; j < 6; j++) {
                    mySquares.add(
                            new int[] { (int) Math.round((me.getX() / 5)) + i, (int) Math.round((me.getY() / 5)) + j });
                }
            }

            int[] xArray = new int[(BattleBotArena.RIGHT_EDGE / 5)];
            for (int i = 0; i < xArray.length; i++) {
                xArray[i] = (i * 5);
            }
            int[] yArray = new int[(BattleBotArena.BOTTOM_EDGE / 5)];
            for (int i = 0; i < yArray.length; i++) {
                yArray[i] = (i * 5);
            }
            dangerSquares.clear();
            dangerSquares.addAll(borderDangerSquares);
            for (int i = 0; i < bulletsOut.length; i++) {
                if (bullets[i].getX() < me.getX() + Bot.RADIUS * 2) {
                    if (bullets[i].getXSpeed() > 0) {
                        for (int j = -4; j < 12; j++) {
                            for (int k = -2; k < 3; k++) {
                                dangerSquares.add(new int[] { (int) Math.round((bullets[i].getX() / 5)) + j,
                                        (int) Math.round((bullets[i].getY() / 5)) + k });
                            }
                        }
                    }
                }
                if (bullets[i].getX() > me.getX()) {
                    if (bullets[i].getXSpeed() < 0) {
                        for (int j = -4; j < 12; j++) {
                            for (int k = -2; k < 3; k++) {
                                dangerSquares.add(new int[] { (int) Math.round((bullets[i].getX() / 5)) - j,
                                        (int) Math.round((bullets[i].getY() / 5)) + k });
                            }
                        }
                    }
                }
                if (bullets[i].getY() < me.getY() + Bot.RADIUS * 2) {
                    if (bullets[i].getYSpeed() > 0) {
                        for (int j = -4; j < 12; j++) {
                            for (int k = -2; k < 3; k++) {
                                dangerSquares.add(new int[] { (int) Math.round((bullets[i].getX() / 5)) + k,
                                        (int) Math.round((bullets[i].getY() / 5)) + j });
                            }
                        }
                    }
                }

                if (bullets[i].getY() > me.getY()) {
                    if (bullets[i].getYSpeed() < 0) {
                        for (int j = -4; j < 12; j++) {
                            for (int k = -2; k < 3; k++) {
                                dangerSquares.add(new int[] { (int) Math.round((bullets[i].getX() / 5)) + k,
                                        (int) Math.round((bullets[i].getY() / 5)) - j });
                            }
                        }
                    }
                }
            }
            
            for (int i = 0; i < liveBots.length ; i++){
                if (liveBots[i].getX() < 950-Bot.RADIUS*2 && liveBots[i].getX() > 50+Bot.RADIUS*2 && liveBots[i].getY() < 650-Bot.RADIUS*2 && liveBots[i].getY() > 50+Bot.RADIUS*2){
                for (int j = -3; j < 8; j++) {
                    for (int k = -3; k < 8; k++) {
                        dangerSquares.add(new int[] { (int) Math.round((liveBots[i].getX() / 5)) + j, (int) Math.round((liveBots[i].getY() / 5)) + k});
                        }
                    }
                }
            }
            for (int i = 0; i < deadBots.length ; i++){
                if (deadBots[i].getX() < 950 && deadBots[i].getX() > 50 && deadBots[i].getY() < 650 && deadBots[i].getY() > 50){
                for (int j = -1; j < 6; j++) {
                    for (int k = -1; k < 6; k++) {
                        dangerSquares.add(new int[] { (int) Math.round((deadBots[i].getX() / 5)) + j, (int) Math.round((deadBots[i].getY() / 5)) + k});
                        }
                    }
                }
            }
            
            intersectSquares.clear();
            testSquares.clear();
            boolean flag = false;
            for (int i = 0; i < dangerSquares.size(); i++) {
                for (int j = 0; j < mySquares.size(); j++) {
                    if (dangerSquares.get(i)[0] == mySquares.get(j)[0]
                            && dangerSquares.get(i)[1] == mySquares.get(j)[1]) {
                        intersectSquares.add(new int[] { dangerSquares.get(i)[0], dangerSquares.get(i)[1] });
                        // just exists for efficiency when drawing
                        // WHEN NOT DRAWING, ADD A BREAK THE SECOND THIS BECOMES VALID
                        flag = true;
                        // break;
                    }

                }
            }
            if (flag) {
                {
                    for (int k = -7; k < 8; k++) {
                        for (int l = -1; l < 6; l++) {
                            for (int m = -1; m < 6; m++) {
                                testSquares.add(
                                        new int[] { (int) Math.round((me.getX() / 5)) + l + k,
                                                (int) Math.round((me.getY() / 5)) + m });
                            }
                        }
                    }
                    for (int k = -7; k < 8; k++) {
                        for (int l = -1; l < 6; l++) {
                            for (int m = -1; m < 6; m++) {
                                testSquares.add(
                                        new int[] { (int) Math.round((me.getX() / 5)) + l,
                                                (int) Math.round((me.getY() / 5)) + m + k });
                            }
                        }
                    }
                }
            }

            // System.out.println(testSquares.size() / 49 + " " + testSquares.size());

            boolean isSquareArraySafe = true;
            safeSquareArray = -1;
            if (testSquares.size() > 0) {
                // System.out.println("Yes, testSquares is > 0");
                    // System.out.println("starts looping through the danger squares on the field");
                    for (int j = 0; j < testSquares.size(); j += 49) {
                        isSquareArraySafe = true;
                        // System.out.println("starts looping through all of the test squares in 49 chunks");
                        // 0, 1, 2, 3, 4, 5, 6,
                        // 7,                13,
                        // 14,               20,
                        // 21,               27,
                        // 28,               34,
                        // 35,               41,
                        // 42,43,44,45,46,47,48
                        ktest: for (int k = 0; k < 49; k++) {
                            currentSquareTest = j+k; 
                            //System.out.println("Current square being tested "+currentSquareTest+ ", Within array " + j);   
                            // System.out.println(j);
                            
                            for (int i = 0; i < dangerSquares.size(); i++){
                                
                                if (dangerSquares.get(i)[0] == testSquares.get(j + k)[0] && dangerSquares.get(i)[1] == testSquares.get(j + k)[1]) {
                                    intersectSquares.add(new int[] {testSquares.get(j + k)[0], testSquares.get(j + k)[1] });
                                    
                                
                                    
                                // System.out.println("a test square landed on a danger square within the 49 chunk");
                                    isSquareArraySafe = false;
                                    break ktest;
                                }
                            }
                        }
                        if (isSquareArraySafe) {
                            if (safeSquareArray == -1
                                    || botHelper.calcDistance(xArray[testSquares.get(safeSquareArray)[0]],
                                            yArray[testSquares.get(safeSquareArray)[1]], me.getX(),
                                            me.getY()) < botHelper.calcDistance(xArray[testSquares.get(j)[0]],
                                                    yArray[testSquares.get(j)[1]], me.getX(), me.getY()))
                                //System.out.println("Chosen safe Array was: "+ j);
                                safeSquareArray = j;
                        }

                    }
                    //System.out.println("Final Chosen Array was: "+ safeSquareArray);
                
            }
            if (testSquares.size() > 0) {
                if (safeSquareArray != -1) {
                    if (yArray[mySquares.get(0)[1]] != yArray[testSquares.get(safeSquareArray)[1]] 
                    && (yArray[mySquares.get(0)[1]] - yArray[testSquares.get(safeSquareArray)[1]]) > (xArray[mySquares.get(0)[0]] - xArray[testSquares.get(safeSquareArray)[0]])) {
                        if (mySquares.get(0)[1] > yArray[testSquares.get(safeSquareArray)[1]]) {
                            return BattleBotArena.DOWN;
                        }
                        if (mySquares.get(0)[1] < yArray[testSquares.get(safeSquareArray)[1]]) {
                            return BattleBotArena.UP;
                        }
                    }
                
                    if (xArray[mySquares.get(0)[0]] != xArray[testSquares.get(safeSquareArray)[0]] 
                    && (yArray[mySquares.get(0)[1]] - yArray[testSquares.get(safeSquareArray)[1]]) < (xArray[mySquares.get(0)[0]] - xArray[testSquares.get(safeSquareArray)[0]])) {

                        if (xArray[mySquares.get(0)[0]] > xArray[testSquares.get(safeSquareArray)[0]]) {
                            return BattleBotArena.LEFT;
                        }
                        if (xArray[mySquares.get(0)[0]] < xArray[testSquares.get(safeSquareArray)[0]]) {
                            return BattleBotArena.RIGHT;
                        }
                    }
                    
                }

                // yArray [safeSquareArray], , me.getY()
            }
            for (int i = 0; i < liveBots.length; i++) { 
                if (liveBots[i].getY() <= me.getY() && liveBots[i].getY()+Bot.RADIUS >= me.getY()){
                    if (liveBots[i].getX() > me.getX()){
                        return BattleBotArena.FIRERIGHT;
                    }
                    else if (liveBots[i].getX() < me.getX()){
                        return BattleBotArena.FIRELEFT;
                    }
                }
                if (liveBots[i].getX() <= me.getX() && liveBots[i].getX()+Bot.RADIUS >= me.getX()){
                    if (liveBots[i].getY() > me.getY()){
                        return BattleBotArena.FIREDOWN;
                    }
                    else if (liveBots[i].getY() < me.getY()){
                        return BattleBotArena.FIREUP;
                    }
                }
            }

            return BattleBotArena.STAY;
        } catch (Exception e) {
            //e.printStackTrace();

        }
        return BattleBotArena.STAY;
    }

    @Override
    public void newRound() {
        // resetting variables ASK BROOKS WHY SO INNEFFICIENT
        // for (int x = 0; x < 200; x++){
        //     borderDangerSquares.add(new int[] { x,10 });
        //     borderDangerSquares.add(new int[] { x,130 });
        // }
        // for (int y = 0; y < 140; y++){
        //     borderDangerSquares.add(new int[] { 10,y });
        //     borderDangerSquares.add(new int[] { 190,y });
        // }
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        // try {
        //     g.setColor(Color.RED);
        //     int[] xArray = new int[(BattleBotArena.RIGHT_EDGE / 5)];
        //     for (int i = 0; i < xArray.length; i++) {
        //         xArray[i] = (i * 5);
        //     }
        //     int[] yArray = new int[(BattleBotArena.BOTTOM_EDGE / 5)];
        //     for (int i = 0; i < yArray.length; i++) {
        //         yArray[i] = (i * 5);
        //     }
        //     if (dangerSquares.size() > 0){
        //         for (int j = 0; j < dangerSquares.size(); j++) {
        //             g.drawRect(xArray[dangerSquares.get(j)[0]], yArray[dangerSquares.get(j)[1]], 5, 5);
        //         }
        //     }
        //     g.setColor(Color.GREEN);
        //     for (int j = 0; j < mySquares.size(); j++) {
        //         g.drawRect(xArray[mySquares.get(j)[0]], yArray[mySquares.get(j)[1]], 5, 5);
        //     }
        //     if (testSquares.size() > 0) {
        //         g.setColor(Color.ORANGE);
        //         for (int i = 0; i < testSquares.size(); i++) {
        //             g.drawRect(xArray[testSquares.get(i)[0]], yArray[testSquares.get(i)[1]], 5, 5);
        //         }
        //     }
        //     g.setColor(Color.CYAN);
        //     for (int i = 0; i < intersectSquares.size(); i++) {
        //         g.drawRect(xArray[intersectSquares.get(i)[0]], yArray[intersectSquares.get(i)[1]], 5, 5);
        //     }
        //     g.setColor(Color.BLUE);
        //     if (testSquares.size() > 0) {
        //         if (safeSquareArray != -1) {
        //             g.drawRect(xArray[testSquares.get(safeSquareArray)[0]], yArray[testSquares.get(safeSquareArray)[1]],
        //                     5, 5);
        //                     //System.out.println(safeSquareArray);
        //         }
        //     }
        //     g.setColor(Color.GREEN);
        //     g.drawRect(xArray[mySquares.get(0)[0]], yArray[mySquares.get(0)[1]], 5, 5);
            // g.setColor(Color.MAGENTA);
            // if (testSquares.size() > 0) {
            //     if (currentSquareTest != -1) {
            //         g.drawRect(xArray[testSquares.get(currentSquareTest)[0]], yArray[testSquares.get(currentSquareTest)[1]],
            //                 5, 5);
            //     }
            // }
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        g.drawImage(botImage,x+3,y+3,null);

        // g.drawLine(x, 0, x, 1000);
        // g.drawLine(x + Bot.RADIUS, 0, x + Bot.RADIUS, 1000);
        // g.drawLine(x + Bot.RADIUS * 2, 0, x + Bot.RADIUS * 2, 1000);

    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'getName'");
        return "EyeBot";
    }

    @Override
    public String getTeamName() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method
        // 'getTeamName'");
        return "Team";
    }

    @Override
    public String outgoingMessage() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method
        // 'outgoingMessage'");
        return null;
    }

    @Override
    public void incomingMessage(int botNum, String msg) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method
        // 'incomingMessage'");
    }

    @Override
    public String[] imageNames() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'imageNames'");
        String[] imageName = {"eyecon.png"};
        return imageName;
    }
    
    @Override
    public void loadedImages(Image[] images) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method
        // 'loadedImages'");
        botImage = images[0];

    }
}
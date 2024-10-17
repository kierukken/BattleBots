package bots;

import java.util.ArrayList;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class RickyBot extends Bot{

    private int saveRange = (int) (Bot.RADIUS * BattleBotArena.BULLET_SPEED / (int)(BattleBotArena.BOT_SPEED)) + Bot.RADIUS;
    private int Tx = 250;
    private int Ty = 175;
    private int lengthOfBMap = (BattleBotArena.RIGHT_EDGE-BattleBotArena.LEFT_EDGE)/5;
    private int widthOfBMap = (BattleBotArena.BOTTOM_EDGE-BattleBotArena.TOP_EDGE)/5;
    private int dontFire = 0;
    private boolean dontMove = false;
    private double tan;
    private double oriTan;
    private double[] array = new double[5];
    private int[][] bmap = new int[5][5];
    private ArrayList<Integer> list = new ArrayList<Integer>();
    private Image image;

    @Override
    public void newRound() {
        // TODO Auto-generated method stub
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        // TODO Auto-generated method stub

        int midX = (int)(me.getX()) + Bot.RADIUS; 
        int midY = (int)(me.getY()) + Bot.RADIUS; 
        dontMove = false;


        if (bullets != null)
        {
            for (int i = 0; i < bullets.length; i++)
            {
                Bullet currentBullet = bullets[i];
                if (Math.sqrt(Math.pow(Math.abs(midX - currentBullet.getX()), 2) + Math.pow(Math.abs(midY - currentBullet.getY()), 2)) <= Bot.RADIUS) System.out.println("get hitted");

                if (Math.abs(midX - currentBullet.getX()) <= saveRange && Math.abs(midY - currentBullet.getY()) <= saveRange) dontMove = true;


                if (currentBullet.getXSpeed() > 0 && midX - currentBullet.getX() < saveRange && midX > currentBullet.getX() && Math.abs(midY - currentBullet.getY()) < Bot.RADIUS)
                {
                    if (midY >= currentBullet.getY()) return BattleBotArena.DOWN;
                    else return BattleBotArena.UP;
                }
                else if (currentBullet.getXSpeed() < 0 && currentBullet.getX() - midX < saveRange && midX < currentBullet.getX() && Math.abs(midY - currentBullet.getY()) < Bot.RADIUS)
                {
                    if (midY >= currentBullet.getY()) return BattleBotArena.DOWN;
                    else return BattleBotArena.UP;
                }
                else if (currentBullet.getYSpeed() > 0 && midY - currentBullet.getY() < saveRange && midY > currentBullet.getY() && Math.abs(midX - currentBullet.getX()) < Bot.RADIUS)
                {
                    if (midX >= currentBullet.getX()) return BattleBotArena.RIGHT;
                    else return BattleBotArena.LEFT;
                }
                else if (currentBullet.getYSpeed() < 0 && currentBullet.getY() - midY < saveRange && midY < currentBullet.getY() && Math.abs(midX - currentBullet.getX()) < Bot.RADIUS)
                {
                    if (midX >= currentBullet.getX()) return BattleBotArena.RIGHT;
                    else return BattleBotArena.LEFT;
                }


            }
        }

        if (!dontMove && liveBots != null && deadBots != null)
        {
            for (int i = 0; i < 5; i++) array[i] = 0;

            for (int i = 0; i < liveBots.length; i++)
            {
                BotInfo currentBot = liveBots[i];
                int currentBotX = (int)(currentBot.getX()) + Bot.RADIUS;
                int currentBotY = (int)(currentBot.getY()) + Bot.RADIUS;

                double t = Math.sqrt(Math.pow(Math.abs(midX - currentBotX), 2) + Math.pow(Math.abs(midY - currentBotY), 2));
                if (t <= 50)
                {
                    double l = Math.sqrt(Math.pow(Math.abs(midX-1 - currentBotX), 2) + Math.pow(Math.abs(midY - currentBotY), 2));
                    double u = Math.sqrt(Math.pow(Math.abs(midX - currentBotX), 2) + Math.pow(Math.abs(midY-1 - currentBotY), 2));
                    double d = Math.sqrt(Math.pow(Math.abs(midX - currentBotX), 2) + Math.pow(Math.abs(midY+1 - currentBotY), 2));
                    double r = Math.sqrt(Math.pow(Math.abs(midX+1 - currentBotX), 2) + Math.pow(Math.abs(midY - currentBotY), 2));
                    array[0] += t;
                    array[1] += u;
                    array[2] += d;
                    array[3] += l;
                    array[4] += r;
                }
            }

            for (int i = 0; i < deadBots.length; i++)
            {
                BotInfo currentBot = deadBots[i];
                int currentBotX = (int)(currentBot.getX()) + Bot.RADIUS;
                int currentBotY = (int)(currentBot.getY()) + Bot.RADIUS;

                double t = Math.sqrt(Math.pow(Math.abs(midX - currentBotX), 2) + Math.pow(Math.abs(midY - currentBotY), 2));
                if (t <= 50)
                {
                    double l = Math.sqrt(Math.pow(Math.abs(midX-1 - currentBotX), 2) + Math.pow(Math.abs(midY - currentBotY), 2));
                    double u = Math.sqrt(Math.pow(Math.abs(midX - currentBotX), 2) + Math.pow(Math.abs(midY-1 - currentBotY), 2));
                    double d = Math.sqrt(Math.pow(Math.abs(midX - currentBotX), 2) + Math.pow(Math.abs(midY+1 - currentBotY), 2));
                    double r = Math.sqrt(Math.pow(Math.abs(midX+1 - currentBotX), 2) + Math.pow(Math.abs(midY - currentBotY), 2));
                    array[0] += t;
                    array[1] += u;
                    array[2] += d;
                    array[3] += l;
                    array[4] += r;
                }
            }

            int index = 0;
            double smallest = array[0];
            for(int i = 1; i < 5; i++)
            {
                if (array[i] > smallest)
                {
                    smallest = array[i];
                    index = i;
                }
            }
            
            if (index != 0 && !dontMove) return index;
        }


        if (liveBots != null && deadBots != null)
        {
            for (int i = 0; i < 5; i++) for (int j = 0; j < 5; j++) bmap[i][j] = 0;

            for (int i = 0; i < 5; i++)
            {
                for (int j = 0; j < 5; j++)
                {
                    if (i == 0 || j == 0  || i == 4 || j == 4) bmap[i][j]++;                    
                    for (int k = 0;k < liveBots.length; k++)
                    {
                        BotInfo currentBot = liveBots[k];
                        int currentBotX = (int)(currentBot.getX()) + Bot.RADIUS;
                        int currentBotY = (int)(currentBot.getY()) + Bot.RADIUS;

                        if (currentBotX >= i*lengthOfBMap - 2*Bot.RADIUS && currentBotX <= (i+1)*lengthOfBMap + 2*Bot.RADIUS && currentBotY >= j*widthOfBMap - 2*Bot.RADIUS && currentBotY <= (j+1)*widthOfBMap + 2*Bot.RADIUS)
                        {
                            bmap[i][j]++;
                        }
                    }   
                    for (int k = 0;k < deadBots.length; k++)
                    {
                        BotInfo currentBot = deadBots[k];
                        int currentBotX = (int)(currentBot.getX()) + Bot.RADIUS;
                        int currentBotY = (int)(currentBot.getY()) + Bot.RADIUS;

                        if (currentBotX >= i*lengthOfBMap - 2*Bot.RADIUS && currentBotX <= (i+1)*lengthOfBMap + 2*Bot.RADIUS && currentBotY >= j*widthOfBMap - 2*Bot.RADIUS && currentBotY <= (j+1)*widthOfBMap + 2*Bot.RADIUS)
                        {
                            bmap[i][j]++;
                        }
                    }     
                }
            } 

            int smallest = bmap[0][0];
            for (int i = 0; i < 5; i++) 
            {
                for (int j = 0; j < 5; j++)
                {
                    if (bmap[i][j] < smallest)
                    {
                        smallest = bmap[i][j];
                    }
                }
            }

            list.clear();
            for (int i = 0; i < 5; i++) 
            {
                for (int j = 0; j < 5; j++)
                {
                    if (bmap[i][j] == smallest)
                    {
                        list.add(i);
                        list.add(j);
                    }
                }
            }

            Tx = ((list.get(0))*lengthOfBMap + lengthOfBMap/2);
            Ty = ((list.get(1))*widthOfBMap + widthOfBMap/2);
            for (int i = 2; i < list.size(); i += 2)
            {
                if (Math.abs(Tx - midX) + Math.abs(Ty - midY) > (Math.abs(list.get(i)*lengthOfBMap + lengthOfBMap/2 - midX) + Math.abs(list.get(i+1)*widthOfBMap + widthOfBMap/2 - midY)))
                {
                    Tx = ((list.get(i))*lengthOfBMap + lengthOfBMap/2);
                    Ty = ((list.get(i+1))*widthOfBMap + widthOfBMap/2);
                }
            }

       
        }


        if (Tx - midX != 0) tan = (Math.abs((Ty-midY)/(Tx-midX)));
        
        if (!dontMove && Tx - midX != 0)
        {
            if (Math.abs(Tx - midX) > 3 && Math.abs(Ty - midY) > 3)
            {
                if (midX >= Tx && midY >= Ty)
                {
                    if (tan <= oriTan) return BattleBotArena.LEFT;
                    else return BattleBotArena.UP;
                }
                else if (midX <= Tx && midY >= Ty)
                {
                    if (tan <= oriTan) return BattleBotArena.RIGHT;
                    else return BattleBotArena.UP;
                }
                else if (midX >= Tx && midY <= Ty)
                {
                    if (tan <= oriTan) return BattleBotArena.LEFT;
                    else return BattleBotArena.DOWN;
                }
                else if (midX <= Tx && midY <= Ty)
                {
                    if (tan <= oriTan) return BattleBotArena.RIGHT;
                    else return BattleBotArena.DOWN;
                }
            }
        }

        if (shotOK)
        {
            for (int i = 0; i < liveBots.length; i++)
            {
                BotInfo currentBot = liveBots[i];
                int currentBotX = (int)(currentBot.getX()) + Bot.RADIUS;
                int currentBotY = (int)(currentBot.getY()) + Bot.RADIUS;
                int shotRange = 100;



                if (midX - currentBotX <= shotRange && midX > currentBotX && Math.abs(midY - currentBotY) <= Bot.RADIUS)
                {
                    if (dontFire >= 5)
                    {
                        dontFire = 0;
                        return BattleBotArena.FIRELEFT;
                    }
                    dontFire++;
                }
                else if (currentBotX - midX <= shotRange && midX < currentBotX && Math.abs(midY - currentBotY) <= Bot.RADIUS)
                {
                    if (dontFire >= 5)
                    {
                        dontFire = 0;
                        return BattleBotArena.FIRERIGHT;
                    }
                    dontFire++;
                }
                else if (midY - currentBotY <= shotRange && midY > currentBotY && Math.abs(midX - currentBotX) <= Bot.RADIUS)
                {
                    if (dontFire >= 5)
                    {
                        dontFire = 0;
                        return BattleBotArena.FIREUP;
                    }
                    dontFire++;
                }
                else if (currentBotY - midY <= shotRange && midY < currentBotY && Math.abs(midX - currentBotX) <= Bot.RADIUS)
                {
                    if (dontFire >= 5)
                    {
                        dontFire = 0;
                        return BattleBotArena.FIREDOWN;
                    }
                    dontFire++;
                }

                if (midX > currentBotX && Math.abs(midY - currentBotY) <= Bot.RADIUS)
                {
                    if (dontFire >= 5)
                    {
                        dontFire = 0;
                        return BattleBotArena.FIRELEFT;
                    }
                    dontFire++;
                }
                else if (midX < currentBotX && Math.abs(midY - currentBotY) <= Bot.RADIUS)
                {
                    if (dontFire >= 5)
                    {
                        dontFire = 0;
                        return BattleBotArena.FIRERIGHT;
                    }
                    dontFire++;
                }
                else if (midY > currentBotY && Math.abs(midX - currentBotX) <= Bot.RADIUS)
                {
                    if (dontFire >= 5)
                    {
                        dontFire = 0;
                        return BattleBotArena.FIREUP;
                    }
                    dontFire++;
                }
                else if (midY < currentBotY && Math.abs(midX - currentBotX) <= Bot.RADIUS)
                {
                    if (dontFire >= 5)
                    {
                        dontFire = 0;
                        return BattleBotArena.FIREDOWN;
                    }
                    dontFire++;
                }
            }
        }
       
        return BattleBotArena.STAY;
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        // TODO Auto-generated method stub
        g.drawImage(image, x+1, y, Bot.RADIUS*2, Bot.RADIUS*2, null);
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "RickyBot";
    }

    @Override
    public String getTeamName() {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String outgoingMessage() {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public void incomingMessage(int botNum, String msg) {
        // TODO Auto-generated method stub
    }

    @Override
    public String[] imageNames() {
        // TODO Auto-generated method stub
        String[] images = {"rolling stones.png"};
        return images;
    }

    @Override
    public void loadedImages(Image[] images) {
        // TODO Auto-generated method stub
        if (images != null)
		{
            image = images[0];
        }
    }
    
}

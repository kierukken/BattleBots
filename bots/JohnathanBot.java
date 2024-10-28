package bots;

import arena.BotInfo;
import arena.Bullet;
import java.awt.Graphics;
import java.awt.Image;


public class JohnathanBot extends Bot {

    @Override
    public void newRound() {
    }
    private static final double DODGE_DISTANCE = 5.0;
    
    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        BotHelper helper = new BotHelper();
    
        if (liveBots == null || liveBots.length == 0) {
            return arena.BattleBotArena.STAY; 
        }
    
        double closestDistance = Double.MAX_VALUE;
        BotInfo closestBot = null;
    
        for (BotInfo bot : liveBots) {
            double distance = helper.calcDistance(me.getX(), me.getY(), bot.getX(), bot.getY());
            if (distance < closestDistance) {
                closestDistance = distance;
                closestBot = bot;
            }
        }

        for (Bullet bullet : bullets) {
            if (bullet != null) {
                double bulletX = bullet.getX();
                double bulletY = bullet.getY();
                double bulletDistance = helper.calcDistance(me.getX(), me.getY(), bulletX, bulletY);

                if (bulletDistance < DODGE_DISTANCE) {
                    double dodgeX = me.getX() - bulletX;
                    double dodgeY = me.getY() - bulletY;
    
                    if (Math.abs(dodgeX) > Math.abs(dodgeY)) {
                        return (dodgeX > 0) ? arena.BattleBotArena.RIGHT : arena.BattleBotArena.LEFT;
                    } else {
                        return (dodgeY > 0) ? arena.BattleBotArena.DOWN : arena.BattleBotArena.UP;
                    }
                }
            }
        }
    
        if (closestBot != null) {
            double dx = closestBot.getX() - me.getX();
            double dy = closestBot.getY() - me.getY();
    
            double safeDistance = RADIUS * 2.5; 
            
            if (closestDistance <= safeDistance) {
                if (shotOK) {
                    if (Math.abs(dx) > Math.abs(dy)) {
                        return (dx > 0) ? arena.BattleBotArena.FIRERIGHT : arena.BattleBotArena.FIRELEFT;
                    } else {
                        return (dy > 0) ? arena.BattleBotArena.FIREDOWN : arena.BattleBotArena.FIREUP;
                    }
                } else {
                    return arena.BattleBotArena.STAY;
                }
            }

            if (closestDistance > safeDistance) {
                if (Math.abs(dx) > Math.abs(dy)) {
                    return (dx > 0) ? arena.BattleBotArena.RIGHT : arena.BattleBotArena.LEFT;
                } else {
                    return (dy > 0) ? arena.BattleBotArena.DOWN : arena.BattleBotArena.UP;
                }
            }
        }
    
        return arena.BattleBotArena.STAY; 
    }
    
    @Override
    public void draw(Graphics g, int x, int y) {
        // Code to draw the bot
        g.fillOval(x, y, RADIUS * 2, RADIUS * 2); // Draw a simple circle
    }

    @Override
    public String getName() {
        return "JohnathanBot"; // Name of the bot
    }

    @Override
    public String getTeamName() {
        return ""; // Team name
    }

    @Override
    public String outgoingMessage() {
        return ""; // Message to broadcast
    }

    @Override
    public void incomingMessage(int botNum, String msg) {
        // Handle incoming messages
    }

    @Override
    public String[] imageNames() {
        return new String[] {}; // Image names, if any
    }

    @Override
    public void loadedImages(Image[] images) {
        // Store loaded images if needed
    }
}
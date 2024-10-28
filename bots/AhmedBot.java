package bots;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Random;
public class AhmedBot extends Bot{
    private Random random;
    private static final int RADIUS = 10; //Radius for drawing

    public AhmedBot(){
        random = new Random();
    }

    @Override
    public void newRound() {
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        // TODO Auto-generated method stub
       // throw new UnsupportedOperationException("Unimplemented method 'getMove'");
       for (Bullet bullet : bullets) {
        if (isBulletDangerous(me, bullet) )
            return moveAwayFromBullet(me, bullet);
       }
    
       BotInfo closestBot = findClosestBot (me, liveBots);
       if (closestBot != null) {
        if (shotOK && isBotInRange(me, closestBot)) {
            return fireAtBot(me, closestBot); // Fire in the direction of the bot
        } else {
            return moveTowardsBot(me, closestBot);  // Move towards bot
        }

       }
       return getRandomMove();

    }

    @Override
    public void draw(Graphics g, int x, int y) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, RADIUS * 2, RADIUS * 2);
        
        
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'getName'");
        return "cyborg";
    }

    @Override
    public String getTeamName() {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'getTeamName'");
        return "Bot";

    }

    @Override
    public String outgoingMessage() {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'outgoingMessage'");
        return " ";
    }

    @Override
    public void incomingMessage(int botNum, String msg) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'incomingMessage'");


    }

    @Override
    public String[] imageNames() {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'imageNames'");
        return new String[] {"roomba_up.png"};
		

    }

    @Override
    public void loadedImages(Image[] images) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'loadedImages'");
    }
    //Are bullets coming for me
    private boolean isBulletDangerous(BotInfo me, Bullet bullet) { 
        return Math.abs(bullet.getX() - me.getX()) < 20 && Math.abs(bullet.getY() - me.getY()) < 20;
    }
    //method to move away from bullets
    private int moveAwayFromBullet(BotInfo me, Bullet bullet) {
        if (bullet.getX() > me.getX()) { 
            return BattleBotArena.LEFT; // move left
        } else if (bullet.getX() < me.getX()) {
            return BattleBotArena.RIGHT; // move right
        } else if (bullet.getY() > me.getY()) {
            return BattleBotArena.UP; // move up
        } else {
            return BattleBotArena.DOWN; // move down
        }
    }
    //Helper method to find the closest bot
    private BotInfo findClosestBot(BotInfo me, BotInfo[] liveBots) {
        BotInfo closestBot = null;
        double closestDistance = Double.MAX_VALUE;

        for (BotInfo bot : liveBots) {
            double distance = Math.hypot(bot.getX() - me.getX(), bot.getY() - me.getY());
            if (distance < closestDistance) {
                closestDistance = distance;
                closestBot = bot;
        }
    } 
    return closestBot;
    }
    //Check if bot is in shooting range
    private boolean isBotInRange(BotInfo me, BotInfo bot) {
        return Math.abs(me.getX() - bot.getX()) < 50 || Math.abs(me.getY() - bot.getY()) < 50; 
    }
    //Method for firing
    private int fireAtBot(BotInfo me, BotInfo bot) {
        if (bot.getX() > me.getX()) {
            return BattleBotArena.FIRERIGHT;
        } else if (bot.getX() < me.getX()) {
            return BattleBotArena.FIRELEFT;
        } else if (bot.getY() > me.getY()) {
            return BattleBotArena.FIREDOWN;
        } else {
            return BattleBotArena.FIREUP;
        }
    }
    //Method to move towards bot
    private int moveTowardsBot(BotInfo me, BotInfo bot) {
        if (Math.abs(bot.getX() - me.getX()) > Math.abs(bot.getY() - me.getY())) {
            return bot.getX() > me.getX() ? BattleBotArena.RIGHT : BattleBotArena.LEFT;
        } else {
            return bot.getY() > me.getY() ? BattleBotArena.DOWN : BattleBotArena.UP;
        }
    }
    private int getRandomMove() {
        int[] moves = { BattleBotArena.UP, BattleBotArena.DOWN, BattleBotArena.LEFT, BattleBotArena.RIGHT };
        return moves[random.nextInt(moves.length)];
    }
}

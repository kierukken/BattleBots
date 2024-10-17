package bots;

import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class IsmailBot extends Bot{
    private int move = BattleBotArena.STAY;
    Image up, down, left, right, current; 
    BotHelper helper = new BotHelper();
    public Bullet closestBullet;
    @Override
    public void newRound() {
        
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {     
        if(bullets.length > 0){
            closestBullet = helper.findClosest(me, bullets);
        }
        BotInfo closestBot = helper.findClosest(me, liveBots);
        double distfromBotX = closestBot.getX() - me.getX();
        double distfromBotY = closestBot.getY() - me.getY();
        double dispfromBulletX = helper.calcDisplacement( me.getY(), closestBullet.getY());
        double dispfromBulletY = helper.calcDisplacement( me.getX(), closestBullet.getX());
        
        
        /*
         * My strategy for the first round involves my bot responding to bullets by either moving up,down,left or right depending on which axis
         * the bullet is coming from and whether there is a bot already above my bot.
         * My bot will also shoot up,down,left or right depending on if there is a bot that is in shooting range.
         **/
        if ((Math.abs(dispfromBulletX) <= 26)&&(dispfromBulletX>0)&&(closestBullet.getY() >= me.getY())&&(closestBullet.getY() <= me.getY()+26)) {
            if (distfromBotY < 0) { 
                move = BattleBotArena.DOWN;
            }
            else if (distfromBotY > 0) { 
                move = BattleBotArena.UP;
            }
        
        }
        if ((Math.abs(dispfromBulletX) <= 26)&&(dispfromBulletX>0)&&(closestBullet.getY() >= me.getY())&&(closestBullet.getY() <= me.getY()+26)) {
            if (distfromBotY < 0) { 
                move = BattleBotArena.DOWN;
            }
            else if (distfromBotY > 0) { 
                move = BattleBotArena.UP;
            }
        
        }
        if ((Math.abs(dispfromBulletY) <= 26)&&(dispfromBulletY<0)&&(me.getX() <= closestBullet.getX())&&(me.getX()+26 >= closestBullet.getX())) {
            if (distfromBotX < 0) { 
                move = BattleBotArena.LEFT;
            }
            else if (distfromBotX > 0) { 
                move = BattleBotArena.RIGHT;
            }
        }
        if ((Math.abs(dispfromBulletY) <= 26)&&(dispfromBulletY>0)&&(me.getX() <= closestBullet.getX())&&(me.getX()+26 >= closestBullet.getX())) {
            if (distfromBotX < 0) { 
                move = BattleBotArena.LEFT;
            }
            else if (distfromBotX > 0) { 
                move = BattleBotArena.RIGHT;
            }
        }
        if ((distfromBotX <= 26)&&(distfromBotX>=0)) {
            if (distfromBotY>0) {
                move = BattleBotArena.FIREDOWN;
            }
            else {
                move = BattleBotArena.FIREUP;
            }
        }
        if ((Math.abs(distfromBotY) <= 26)&&(distfromBotY>=0)) {
            if (distfromBotX>0) {
                move = BattleBotArena.FIRERIGHT;
            }
            else {
                move = BattleBotArena.FIRELEFT;
            }
        }

        return move;
        
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        if (current != null)
			g.drawImage(current, x, y, Bot.RADIUS*2, Bot.RADIUS*2, null);
    }

    @Override
    public String getName() {
        return "IsmailBot";
    }

    @Override
    public String getTeamName() {
        return "TeamIsmail";
    }

    @Override
    public String outgoingMessage() {
       return "";
    }

    @Override
    public void incomingMessage(int botNum, String msg) {
        
    }

    @Override
    public String[] imageNames() {
        String[] images = {"pikachu_up.png","pikachu_up.down","pikachu_right.png","pikachu_left.png"};
		return images;
    }

    @Override
    public void loadedImages(Image[] images) {
        if (images != null)
		{
			current = up = images[0];
			down = images[1];			
			right = images[2];
            left = images[3];
		}
    }
    
}
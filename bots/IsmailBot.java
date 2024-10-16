package bots;

import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class IsmailBot extends Bot{
    Image up, down, left, right, current; 

    @Override
    public void newRound() {
        
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {     
        for (int i = 0;i<bullets.length;i++) {
            if (Math.abs(bullets[i].getX()-me.getX()) <= 100 ) {
                
            }
        }
        for (int i = 0;i<bullets.length;i++) {
            
        }
        return BattleBotArena.STAY;
        // throw new UnsupportedOperationException("Unimplemented method 'getMove'");
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
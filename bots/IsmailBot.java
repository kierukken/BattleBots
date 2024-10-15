package bots;

import java.awt.Graphics;
import java.awt.Image;

import arena.BotInfo;
import arena.Bullet;

public class IsmailBot extends Bot{
    Image up, down, left, right, current; 

    @Override
    public void newRound() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'newRound'");
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {     
        for (int i = 0;i<bullets.length;i++) {

        }
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException(        "Unimplemented method 'draw'");
    }

    @Override
    public String getName() {
        return "IsmailBot";
    }

    @Override
    public String getTeamName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTeamName'");
    }

    @Override
    public String outgoingMessage() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'outgoingMessage'");
    }

    @Override
    public void incomingMessage(int botNum, String msg) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'incomingMessage'");
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
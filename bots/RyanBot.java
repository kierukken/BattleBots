package bots;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class RyanBot extends Bot{

    @Override
    public void newRound() {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'newRound'");
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        for (Bullet bullet : bullets) {
            double distance = Math.sqrt(Math.pow(bullet.getX() - me.getX(), 2) + Math.pow(bullet.getY() - me.getY(), 2));
            if (distance < Bot.RADIUS * 2) {
                if (bullet.getY() < me.getY()) return BattleBotArena.DOWN;
                else if (bullet.getY() > me.getY()) return BattleBotArena.UP;
                else if (bullet.getX() < me.getX()) return BattleBotArena.RIGHT;
                else if (bullet.getX() > me.getX()) return BattleBotArena.LEFT;
            }
        }
        int choice = (int) (Math.random() * 9);
        switch (choice) {
            case 0: return BattleBotArena.UP;
            case 1: return BattleBotArena.DOWN;
            case 2: return BattleBotArena.LEFT;
            case 3: return BattleBotArena.RIGHT;
            case 4: return shotOK ? BattleBotArena.FIREUP : BattleBotArena.STAY;
            case 5: return shotOK ? BattleBotArena.FIREDOWN : BattleBotArena.STAY;
            case 6: return shotOK ? BattleBotArena.FIRELEFT : BattleBotArena.STAY;
            case 7: return shotOK ? BattleBotArena.FIRERIGHT : BattleBotArena.STAY;
            default: return BattleBotArena.STAY;
        }
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        // TODO Auto-generated method stub
        g.setColor(Color.orange);
		g.fillRect(x+2, y+2, RADIUS*2-4, RADIUS*2-4);
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "RyanBot";
    }

    @Override
    public String getTeamName() {
        // TODO Auto-generated method stub
        return "Team Ryan";
    }

    @Override
    public String outgoingMessage() {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public void incomingMessage(int botNum, String msg) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'incomingMessage'");
    }

    @Override
    public String[] imageNames() {
        // TODO Auto-generated method stub
        String[] paths = {"drone_up.png"};
		return paths;
    }

    @Override
    public void loadedImages(Image[] images) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'loadedImages'");
    }
    
}

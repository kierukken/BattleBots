package bots;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;


public class HannanBot extends Bot {

    /**
    * My name (set when getName() first called)
    */
    private String name = null;

    /**
    * Bot image
    */
    Image current, up, down, right, left;

    //initiating BotHelper
    BotHelper helper = new BotHelper();

    /**
    * Defining the corners of the arena so that it goes to one of them right away 
    * top left, bottom left, top right, bottom right RESPECTIVELY
    */
    int[][] corners = { {0, 0}, {0, BattleBotArena.BOTTOM_EDGE}, {BattleBotArena.RIGHT_EDGE, 0}, {BattleBotArena.RIGHT_EDGE, BattleBotArena.BOTTOM_EDGE} 
    };

    @Override
    public void newRound() {
        current = up; //Set initial direction to up at the start of each round
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {

        /** 
        * PART 1: 
        * Find the closest corner using the helper class to calculate distance
        * at first assuming the corner is the top left so I can loop through and find the correct one 
        */
        int targetX = corners[0][0], targetY = corners[0][1];
        double minDistance = helper.calcDistance(me.getX(), me.getY(), targetX, targetY);

        /**
        * Corners is a 2D array of corner coordinates 
        * Each element in corners is a 1D array containing two values: an x-coordinate and a y-coordinate
        * In each iteration, the variable corner represents one of the corner coordinate arrays 
        * if the distance between that and that is less than minDistance then min distance gets reassigned type stuff to get the new closest corner
        */

        for (int[] corner : corners) {
            double loopDist = helper.calcDistance(me.getX(), me.getY(), corner[0], corner[1]);
            if (loopDist < minDistance) {
                minDistance = loopDist;
                targetX = corner[0];
                targetY = corner[1];
            }
        }

        /** 
        * PART 2:
        * Move towards the closest corner
        */
        if (me.getX() < targetX) {
            current = right; //Update current image direction
            return BattleBotArena.RIGHT; //Move right
        }
        if (me.getX() > targetX) {
            current = left; //Update current image direction
            return BattleBotArena.LEFT;  //Move left
        }
        if (me.getY() < targetY) {
            current = down; //Update current image direction
            return BattleBotArena.DOWN;  //Move down
        }
        if (me.getY() > targetY) {
            current = up; //Update current image direction
            return BattleBotArena.UP;    //Move up
        }

        /**
        * PART 3:
        * Shoot if any bots are at the edges and within range
        */
        if (shotOK) {
            //go through all the living bots
            for (BotInfo bot : liveBots) {
                //Checking if the bot is on any edge
                if (bot.getX() <= 0 || bot.getX() >= BattleBotArena.RIGHT_EDGE || 
                    bot.getY() <= 0 || bot.getY() >= BattleBotArena.BOTTOM_EDGE) {

                    //If we're on the same x, shoot up or down depending on position 
                    if (me.getX() == bot.getX()) {

                        //Shoot up if I'm below and shoot down if I'm above and hope it hits and they don't move out the way 
                        if (me.getY() > bot.getY()) {
                            return BattleBotArena.FIREUP; 
                        } else {
                            return BattleBotArena.FIREDOWN; 
                        }
                    }

                    //if we're on the same y, shoot left or right depending on position
                    if (me.getY() == bot.getY()) {

                        //Shoot left or right, same logic as up or down just with left and right    
                        if (me.getX() > bot.getX()) { 
                            return BattleBotArena.FIRELEFT; 
                        } else {
                            return BattleBotArena.FIRERIGHT; 
                        } 
                    }
                }
            }
        }

        //If the movement tasks have been fulfilled and it's in the corner and there's nothing to shoot, just stay in the corner
        return BattleBotArena.STAY;
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        if (current != null) {
            g.drawImage(current, x, y, Bot.RADIUS * 2, Bot.RADIUS * 2, null); //Draw bot with updated current image
        } else {
            g.setColor(Color.lightGray);
            g.fillOval(x, y, Bot.RADIUS * 2, Bot.RADIUS * 2); //Fallback if current image is null
        }
    }

    /**
    * Construct and return my name
    */
    @Override
    public String getName() {
        if (name == null)
            name = "HannanBot";
        return name;
    }

    /**
    * Team "Arena"
    */
    @Override
    public String getTeamName() {
        return "Arena";
    }

    /**
    * Returning image names to load them in
    */
    @Override
    public String[] imageNames() {
        String[] paths = {"drone_up.png", "drone_down.png", "drone_right.png", "drone_left.png"};
        return paths;
    }

    /**
    * Store the images loaded by the arena.
    */
    @Override
    public void loadedImages(Image[] images) {
        if (images != null) {
            if (images.length > 0)
                up = images[0];
            if (images.length > 1)
                down = images[1];
            if (images.length > 2)
                right = images[2];
            if (images.length > 3)
                left = images[3];
            current = up; //Initialize current image to up
        }
    }

    @Override
    public String outgoingMessage() {
        return null;
    }

    @Override
    public void incomingMessage(int botNum, String msg) {

    }
}

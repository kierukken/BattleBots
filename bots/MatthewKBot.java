package bots;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.util.ArrayList;
import arena.BotInfo;
import arena.Bullet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
//Writing and reading files for Neurons
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class MatthewKBot extends Bot {
    private String msg = null;
    
    private int[] options;
    private int minMoveIn;
    private int maxMoveIn;
    private int move;
    private int lastMove;
    /*
     * If true and there's ammo, the bot will automatically dump all its ammo
     * 
     * This is done in a desperate attempt to kill a threatening bot
     */
    private boolean burstFire = false;
    private double[][] threats;
    private boolean dodge;

    BotHelper helper = new BotHelper();
    Image current, up, down, right, left;
    public MatthewKBot(){

    }
    @Override
    public void newRound() {
        burstFire = false;
        move = 9;
        lastMove = 9;
        //set a neuron to control a judgemental danger radius
        maxMoveIn = 30;
        minMoveIn = 20;
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        /**
         * First lets determine our legal options
         * 
         * Remove options during logic then do random if length is more than 1.
         * 
         * 
         */
        options = (shotOK) ? new int[] {1,2,3,4,5,6,7,8,9} : new int[] {1,2,3,4,5};
        //grab info on closest bot and bullet
        if(bullets.length > 0){
            Bullet closestBullet = helper.findClosest(me, bullets);
        }
        BotInfo closestBot = helper.findClosest(me, liveBots);

        //If we can't shoot then play defense
        if (shotOK) {
            //will auto fire if burstFire is true
            if(burstFire == true){
                return(lastMove);
            }
            //Defensive calculations with ammo
            if(bullets.length > 0){
                //put a neuron in this function later on
                threats = getThreatsInRadius(100,me, bullets);
            }
            //play aggressively if you dont need to dodge
            double deltaXfromBot = closestBot.getX() - me.getX();
            double deltaYfromBot = closestBot.getY() - me.getY();
            double xDistFromBot = Math.abs(deltaXfromBot);
            double yDistFromBot = Math.abs(deltaYfromBot);
            if((xDistFromBot>minMoveIn && xDistFromBot<maxMoveIn&&yDistFromBot<13)||(yDistFromBot>minMoveIn && yDistFromBot<maxMoveIn&&xDistFromBot<13)){
                System.out.println("BOOM");
                //This section inside the if-statement fires a bullet at another robot
                options = removeOptions(1,2,3,4,9);
                
                if(xDistFromBot>minMoveIn && xDistFromBot<maxMoveIn&& yDistFromBot<13){
                    //Fire horizontally
                    options = (deltaXfromBot > 0)? removeOptions(7) : removeOptions(8);
                }else if(yDistFromBot>minMoveIn && yDistFromBot<maxMoveIn && xDistFromBot<13){
                    //Fire vertically
                    options = (deltaYfromBot > 0)? removeOptions(6) : removeOptions(5);
                }
                move = options[(int)(System.currentTimeMillis() % options.length)];
                lastMove = move;
                return(move);

            }else if((xDistFromBot>minMoveIn && xDistFromBot<maxMoveIn)||(yDistFromBot>minMoveIn&&yDistFromBot<maxMoveIn)){    
                if (xDistFromBot>minMoveIn && xDistFromBot<maxMoveIn) {
                    //line up the y variable.
                    options = removeOptions(3,4,5,6,7,8,9);
                    options = (deltaYfromBot > 0)? removeOptions(1):removeOptions(2);
                } else if(yDistFromBot>minMoveIn&&yDistFromBot<maxMoveIn) {
                    //line up the x variable.
                    options = removeOptions(1,2,5,6,7,8,9);
                    options = (deltaXfromBot > 0)? removeOptions(3):removeOptions(4);
                }
                System.out.println("Lining you up");
                move = options[(int)(System.currentTimeMillis() % options.length)];
                lastMove = move;
                return(move);
            }else{
                /**
                 * Determine if we should go for vetrical or horizontal on the offense to match escape distance
                 * 
                 * Go horizontal if dx<=dy
                 *
                 * Vertical if dx > dy
                 */
                //first check for which x or y postiion is closer to firing range
                //check this after making sure no bullets are going to hit you
                System.out.println("Getting closer to you");
                if(Math.abs(xDistFromBot-25) <= Math.abs(yDistFromBot-25)){
                    //change x position and get rid of y options up and down
                    //only 3 and 4 (up and down) remain
                    options = removeOptions(1,2,5,6,7,8,9);
                    printArr(options);
                    options = (deltaXfromBot < 0)
                        ?((deltaXfromBot < -25)?removeOptions(4):removeOptions(3))
                        :((deltaXfromBot < 25)?removeOptions(4):removeOptions(3));
                    
                    
                }else{
                    //change y position and get rid of x options left and right
                    //only 1 and 2 (up and down) remain
                    options = removeOptions(3,4,5,6,7,8,9);
                    printArr(options);
                    //above if dy > 0
                    options = (deltaYfromBot > 0)
                        ?((deltaYfromBot < 25)?removeOptions(2):removeOptions(1))
                        :((deltaXfromBot < -25)?removeOptions(2):removeOptions(1));
                    
                }
                move = options[(int)(System.currentTimeMillis() % options.length)];
                lastMove = move;
                return(move);
            }

            
        } else {
            //play defensively
            System.out.println("DEFENSE");
            //RESUME FROM HERE****************************************************************
            /*if(bullets.length > 0){
                //put a neuron in this function later on
                threats = getThreatsInRadius(100,me, bullets);

            }*/
        }

        // TODO Auto-generated method stub 
        lastMove = move;

        return(move);
    }
    private int[] removeOptions(int... optionsToRemove) {
        //Use hashset for O(1) lookups to check if option should be removed
        HashSet<Integer> toRemoveSet = new HashSet<>();
        for(int opt:optionsToRemove){
            toRemoveSet.add(opt);
        }
        // Create a list to hold the new options
        ArrayList<Integer> newArr = new ArrayList<>(options.length);
        //add those not in the hashset to arraylist
        for(int opt: options){
            if(!toRemoveSet.contains(opt)){
                newArr.add(opt);
            }
        }
        //convert to int array
        int[] retArr = new int[newArr.size()];
        for(int i=0;i<newArr.size();i++){
            retArr[i] = newArr.get(i);
        }
        return(retArr);
    }

    private void printArr(int[] arr){
        for(int i=0;i<arr.length;i++){
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }
    /*
     * Returns a multidimensional array of all threatening bullets within a certain radius
     * 
     * Start by getting all bullets in a radius. Then filter for if they are coming towards the bot and are near or not 
     * Keep them in the list if they are. Get rid of them if not
     * 
     * Order the list based on which bullet is closer
     */
    private double[][] getThreatsInRadius(int radius, BotInfo me, Bullet[] bullets){
        ArrayList<double[]> bulletsInRadius = new ArrayList<>();
        //ArrayList<double[]> dangers = new ArrayList<>();
        for(Bullet bullet: bullets){
            double buX = bullet.getX();
            double buY = bullet.getY();
            if(Math.sqrt((buX-me.getX())*(buX-me.getX())+(buY-me.getY())*(buY-me.getY())) <= radius){
                //dx & dy is the position relative to the bot
                double dx = bullet.getX() - me.getX();
                double dy = bullet.getY() - me.getY();
                double sx = bullet.getXSpeed();
                double sy = bullet.getYSpeed();
                //checks if it is coming for the bot or not
                if(Math.abs(dx + sx)<Math.abs(dx)||Math.abs(dy + sy)<Math.abs(dy)){
                    //format: x,y,dx,dy,sx,sy
                    bulletsInRadius.add(new double[]{buX,buY,dx,dy,sx,sy});
                }
            }
        }
        Collections.sort(bulletsInRadius, new Comparator<double[]>() {
            @Override
            public int compare(double[] bullet1, double[] bullet2){
                //use pythagorean theorum to order bullets from smallest distance to greatest distance
                double dist1 = Math.sqrt(bullet1[2]*bullet1[2]+bullet1[3]*bullet1[3]);
                double dist2 = Math.sqrt(bullet2[2]*bullet2[2]+bullet2[3]*bullet2[3]);
                //-1 if dist1<dist2 0 if equal, 1 if dist1>dist2
                return Double.compare(dist1, dist2);//compare based on distance
            }
        });
        //convert back to 2d array for future uses
        double[][]sortedThreats = new double[bulletsInRadius.size()][6];
        for(int i=0;i<bulletsInRadius.size();i++){
            sortedThreats[i] = bulletsInRadius.get(i);
        }
        return(sortedThreats);
    }

    

    /*private double[][] getDeadsInRadius(int radius, BotInfo[] deadBots){
        ArrayList<double[]> inRadius = new ArrayList<>();
        //Check if in radius and add if true
        for(BotInfo deadBot: deadBots){
            double dbX = deadBot.getX();

        }
    }*/
    /*
     * Fixes the collision between bot and object.
     * Sends the bot in the opposite direction.
     * 
     
    private int fixColision(int givenMove){
        int retMove = (givenMove%2==0)? givenMove-1: givenMove+1;
        return(retMove);
    }*/















    @Override
    public void draw(Graphics g, int x, int y) {
        if (current != null)
			g.drawImage(current, x, y, Bot.RADIUS*2, Bot.RADIUS*2, null);
		else
		{
			g.setColor(Color.lightGray);
			g.fillOval(x, y, Bot.RADIUS*2, Bot.RADIUS*2);
		}
        
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        String name = "Mattomizer";
        return name;
    }

    @Override
    public String getTeamName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String outgoingMessage() {
        String x = msg;
		msg = null;
		return x;
    }

    @Override
    public void incomingMessage(int botNum, String msg) {
        // TODO Auto-generated method stub
    }

    @Override
    public String[] imageNames() {
        // TODO Auto-generated method stub
        String[] paths = {"roomba_up.png", "roomba_down.png", "roomba_right.png", "roomba_left.png"};
		return paths;
    }

    @Override
    public void loadedImages(Image[] images) {
        if (images != null)
		{
			if (images.length > 0)
				up = images[0];
			if (images.length > 1)
				down = images[1];
			if (images.length > 2)
				right = images[2];
			if (images.length > 3)
				left = images[3];
			current = up;
		}
    }
    
}
package bots;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import arena.BotInfo;
import arena.Bullet;

public class MatthewKBot extends Bot {
    private String msg = null;
    
    private int[] options;

    private int move;
    private int lastMove;
    /*
     * If true and there's ammo, the bot will automatically dump all its ammo
     * 
     * This is done in a desperate attempt to kill a threatening bot
     */
    private boolean burstFire = false;

    BotHelper helper = new BotHelper();
    public MatthewKBot(){

    }
    @Override
    public void newRound() {
        burstFire = false;
        move = 9;
        lastMove = 9;

    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        System.out.println("Hello");
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
        System.out.println(closestBot.getY());
        //If we can't shoot, run / defense
        System.out.println(shotOK);
        if (shotOK) {
            //will auto fire if burstFire is true
            if(burstFire == true){
                return(lastMove);
            }
            //play aggressively unless you need to dodge
            double deltaXfromBot = closestBot.getX() - me.getX();
            double deltaYfromBot = closestBot.getY() - me.getY();
            double xDistFromBot = Math.abs(deltaXfromBot);
            double yDistFromBot = Math.abs(deltaYfromBot);
            if((xDistFromBot>20 && xDistFromBot<30)||(yDistFromBot>20&&yDistFromBot<30)){    
                /*if(true){
                
                }else{
                    /**
                     * Line up with bot to fire at it.
                     * 
                     * First figure out the 
                     *
                }
                */
                System.out.println("Lining up one of my variables");
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
                    if(deltaXfromBot < 0){//                            go left                               go right
                        options = (deltaXfromBot < -25)?removeOptions(4):removeOptions(3);
                    }else{//                                            go left                               go right
                        options = (deltaXfromBot < 25)?removeOptions(4):removeOptions(3);
                    }
                    
                }else{
                    //change y position and get rid of x options left and right
                    //only 1 and 2 (up and down) remain
                    options = removeOptions(3,4,5,6,7,8,9);
                    printArr(options);
                    //above if dy > 0
                    if(deltaYfromBot > 0){//                            go up                               go down
                        options = (deltaYfromBot < 25)?removeOptions(2):removeOptions(1);
                    }else{//                                            go up                              go down
                        options = (deltaXfromBot < -25)?removeOptions(2):removeOptions(1);
                    }
                }
                move = options[(int)(System.currentTimeMillis() % options.length)];
                lastMove = move;
                return(move);
            }

            
        } else {
            //play defensively
            System.out.println("DEFENSE");
        }

        // TODO Auto-generated method stub 
        lastMove = move;

        return(move);
    }
    private int[] removeOptions(int... optionsToRemove) {
    // Create a list to hold the new options
    ArrayList<Integer> newArr = new ArrayList<>();

    // Iterate through the current options
    for (int opt : options) {
        // Check if the current option is in the optionsToRemove array
        boolean toRemove = false;
        for (int removeOpt : optionsToRemove) {
            if (opt == removeOpt) {
                toRemove = true;
                break;
            }
        }
        // If the current option is not to be removed, add it to the new array
        if (!toRemove) {
            newArr.add(opt);
        }
    }

    // Convert ArrayList back to int array
    int[] retArr = new int[newArr.size()];
    for (int i = 0; i < retArr.length; i++) {
        retArr[i] = newArr.get(i);
    }

    return retArr;
}

private void printArr(int[] arr){
    for(int i=0;i<arr.length;i++){
        System.out.print(arr[i] + " ");
    }
    System.out.println();
}


    @Override
    public void draw(Graphics g, int x, int y) {
        // TODO Auto-generated method stub
       
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
        return(null);
    }

    @Override
    public void loadedImages(Image[] images) {
    }
    
}

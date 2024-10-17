package bots;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;

import arena.BotInfo;
import java.util.ArrayList;
import arena.Bullet;
import java.util.Random;
import arena.BattleBotArena;
//import arena.BotInfo;
//import arena.Bullet;

public class User3689bot extends Bot {
    ArrayList<Bullet> closestBullets = new ArrayList<Bullet>();
    ArrayList<Integer> possibleDirections = new ArrayList<Integer>();
    boolean targetMove;
    Random random = new Random();
    boolean[] botClose = {false,false,false,false};
    int action = 3;
    String priority = "Vertical"; //default 
    BotInfo targetBot;
    double closest;
    int waitTime = 0;
    boolean[] run = {true,true,true,true}; //tracks each direction to see if there is a dead bot in the way of the targetBot (and therefore we shouldn't shoot)
    //boolean priorityChanged = false;
    boolean canShoot; //whether the turn must be used for moving or it can be used to shoot instead
    double distance;
	/**
	 * Next message to send, or null if nothing to send.
	 */
	private String nextMessage = null;
	/**
	 * An array of trash talk messages.
	 */
	private String[] killMessages = {"Woohoo!!!", "In your face!", "Pwned", "Take that.", "Gotcha!", "Too easy.", "Hahahahahahahahahaha :-)"};
	/**
	 * Bot image
	 */
	Image current, up, down, right, left;
	/**
	 * My name (set when getName() first called)
	 */
	private String name = null;
	/**
	 * Counter for timing moves in different directions
	 */
	
	private int msgCounter = 0;
	
	private int sleep = (int)(Math.random()*5+1);
	/**
	 * Set to True if we are trying to overheat
	 */
	private boolean overheat = false;

	/**
	 * Return image names to load
	 */
	public String[] imageNames()
	{
		String[] paths = {"drone_up.png", "drone_down.png", "drone_right.png", "drone_left.png"};
		return paths;
	}

	/**
	 * Store the images loaded by the arena
	 */
	public void loadedImages(Image[] images)
	{
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
    
    public BotInfo findClosestBot(BotInfo _me, BotInfo[] _bots) {
		BotInfo closest;
		double distance, closestDist;
		closest = _bots[0];
		closestDist = Math.abs(_me.getX() - closest.getX()) + Math.abs(_me.getY() - closest.getY());
		for (int i = 1; i < _bots.length; i++) {
			distance = Math.abs(_me.getX() - _bots[i].getX()) + Math.abs(_me.getY() - _bots[i].getY());
			if (distance < closestDist) {
				closest = _bots[i];
				closestDist = distance;
			}
		}
		return closest;
	}

    public double findClosestInt(BotInfo _me, BotInfo[] _bots) {
		BotInfo closest;
		double distance;
        double closestDist = 0;
        if (_bots.length > 0){
            closest = _bots[0];
            closestDist = Math.abs(_me.getX() - closest.getX()) + Math.abs(_me.getY() - closest.getY());
            for (int i = 1; i < _bots.length; i++) {
                distance = Math.abs(_me.getX() - _bots[i].getX()) + Math.abs(_me.getY() - _bots[i].getY());
                if (distance < closestDist) {
                    closest = _bots[i];
                    closestDist = distance;
                }
            }
        }
		return closestDist;
	}
    
	public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets)
	{
        // for overheating
		if (overheat){try{Thread.sleep(sleep);}catch (Exception e){}}
        
        
        if (waitTime != 0){
            canShoot = false;
            waitTime--;
        } else {
            canShoot = true;
        }
        
        targetMove = true;
        //priorityChanged = false;
        possibleDirections.clear();
        possibleDirections.add(1);
        possibleDirections.add(2);
        possibleDirections.add(3);
        possibleDirections.add(4);
        
        if (me.getX() <= BattleBotArena.LEFT_EDGE+10) {
            botClose[2] = true;
            possibleDirections.remove(Integer.valueOf(3));
        } else if (me.getX()+Bot.RADIUS*2 >= BattleBotArena.RIGHT_EDGE) {
            botClose[3] = true;
            possibleDirections.remove(Integer.valueOf(4));
        }
        if (me.getY()+Bot.RADIUS*2 >= BattleBotArena.BOTTOM_EDGE-10){
            botClose[1] = true;
            possibleDirections.remove(Integer.valueOf(2));
        }else if (me.getY() <= 10){
            botClose[0] = true;
            possibleDirections.remove(Integer.valueOf(1));
        }

        if (bullets.length > 0){
            for (int i = 0; i < bullets.length; i++) {
                if (Math.abs(me.getX() - bullets[i].getX()) + Math.abs(me.getY() - bullets[i].getY()) <= Bot.RADIUS+100){//if the bot is within a 100 radius of the bullet (allows for actions to be taken with more foresight)
                    canShoot = false; //should not be shooting, should instead be moving away from the bullet
                    //waitTime = 3;
                    targetMove = false; //should not be moving towards target, should be moving away from bullet
                    if (bullets[i].getX() < me.getX()){//to the left
                        if (bullets[i].getY() < me.getY()){ //above bot
                            if (getDirection(bullets[i]) == 2){//up doesnt matter, down does, right does and left doesnt
                                possibleDirections.remove(Integer.valueOf(3));
                            }
                            if (getDirection(bullets[i])==4){
                                possibleDirections.remove(Integer.valueOf(1));
                            }
                        }
                        if (bullets[i].getY() > me.getY() && bullets[i].getY() < me.getY()+Bot.RADIUS*2){ //within y of bot
                            if (getDirection(bullets[i]) == 1 || getDirection(bullets[i]) == 2){
                                possibleDirections.remove(Integer.valueOf(3));
                            }
                            if (getDirection(bullets[i]) == 4){
                      
                                possibleDirections.remove(Integer.valueOf(3));
                                possibleDirections.remove(Integer.valueOf(4));
                            }
                        }
                        if (bullets[i].getY() > me.getY()+Bot.RADIUS*2){ //below bot
                            if (getDirection(bullets[i]) == 1){
                                possibleDirections.remove(Integer.valueOf(3));
                            }
                            if (getDirection(bullets[i]) == 4){
                                possibleDirections.remove(Integer.valueOf(2));
                            }
                        }
                    } else if (bullets[i].getX() > me.getX() && bullets[i].getX() < me.getX()+Bot.RADIUS*2){ //within the x of the bot
                        if (bullets[i].getY() <= me.getY()){ //above bot
                            if (getDirection(bullets[i]) == 2){
                              
                                possibleDirections.remove(Integer.valueOf(2));
                                possibleDirections.remove(Integer.valueOf(1));
                            }
                            if (getDirection(bullets[i]) == 3 || getDirection(bullets[i]) == 4){
                                possibleDirections.remove(Integer.valueOf(1));
                            }
                        }
                        if (bullets[i].getY() >= me.getY()+Bot.RADIUS*2){ //below bot
                            if (getDirection(bullets[i]) == 3 || getDirection(bullets[i]) == 4){
                                possibleDirections.remove(Integer.valueOf(2));
                            }
                            if (getDirection(bullets[i])==1){
                                possibleDirections.remove(Integer.valueOf(1));
                                possibleDirections.remove(Integer.valueOf(2));
                            }
                        }
                    }else if (bullets[i].getX() > me.getX()){ //to the right of bot
                        if (bullets[i].getY() < me.getY()){ //above bot
                            if (getDirection(bullets[i])==2){
                                possibleDirections.remove(Integer.valueOf(4));
                            }
                            if (getDirection(bullets[i])==3){
                                possibleDirections.remove(Integer.valueOf(1));
                            }
                        }
                        if (bullets[i].getY() > me.getY() && bullets[i].getY() < me.getY()+Bot.RADIUS*2){ //within y of bot
                            if (getDirection(bullets[i]) == 3){
                                
                                possibleDirections.remove(Integer.valueOf(3));
                                possibleDirections.remove(Integer.valueOf(4));
                            }
                            if (getDirection(bullets[i]) == 1 || getDirection(bullets[i])==2){
                                possibleDirections.remove(Integer.valueOf(4));
                            }
                        }
                        if (bullets[i].getY() > me.getY()+Bot.RADIUS*2){ //below bot
                            if (getDirection(bullets[i]) == 1){
                                possibleDirections.remove(Integer.valueOf(4));
                            }
                            if (getDirection(bullets[i]) == 3){
                                possibleDirections.remove(Integer.valueOf(2));
                            }
                        }
                    }
                }
            }
	    }
        
        targetBot = findClosestBot(me, liveBots);
        closest = findClosestInt(me,deadBots);
        run[0] = true;
        run[1] = true;
        run[2] = true;
        run[3] = true;
        botClose[0] = false;
        botClose[1] = false;
        botClose[2] = false;
        botClose[3] = false;
        
         
        for (int i = 0;i<deadBots.length;i++){ //loop through first to find if there are dead bots too close to go horizontal/vertical
            distance = Math.sqrt(Math.pow(deadBots[i].getX()-me.getX(),2)+Math.pow(deadBots[i].getY()-me.getY(),2));
            if (distance <= (Bot.RADIUS*2)+3){ //check if bots are too close
                if (me.getX() + Bot.RADIUS*2 >= deadBots[i].getX() && me.getX() <= deadBots[i].getX() + Bot.RADIUS*2 && me.getY() + Bot.RADIUS*2 >= deadBots[i].getY() && me.getY() <= deadBots[i].getY() + Bot.RADIUS*2){ //deadbot is colliding with the bot
                    if (me.getX() < deadBots[i].getX()){
                        botClose[3] = false;
                        
                    } else if (me.getX() > deadBots[i].getX()){
                        botClose[2] = false;
                    }
                    if (me.getY() < deadBots[i].getY()){
                        botClose[1] = false;
                    } else if (me.getY() > deadBots[i].getY()){
                        botClose[0] = false;
                    }
                }
                if (deadBots[i].getX() >= me.getX()+Bot.RADIUS*2 && deadBots[i].getY() <= me.getY()+Bot.RADIUS*2 && deadBots[i].getY()+Bot.RADIUS*2 >= me.getY()+Bot.RADIUS*2){ //x is to the right and y is within the y of the bot
                    botClose[3] = false;
                }
                if (deadBots[i].getX()+Bot.RADIUS*2 >= me.getX() && deadBots[i].getX() <= me.getX()+Bot.RADIUS*2){ //x is within x of bot
                    if (deadBots[i].getY() <= me.getY()){ //dead bot is above bot
                        botClose[0] = false;
                    }
                    if (deadBots[i].getY()+Bot.RADIUS*2 >= me.getY()+Bot.RADIUS*2){ //dead bot is below bot
                        botClose[1] = false;
                    }
                }
                if (deadBots[i].getX()+RADIUS*2 <= me.getX() && (deadBots[i].getY() <= me.getY()+Bot.RADIUS*2 && deadBots[i].getY()+Bot.RADIUS*2 >= me.getY())){//x is to the left and dead bot's y is within y of bot
                    botClose[2] = false;
                }
                /* 
                if (deadBots[i].getX() < me.getX() && ((deadBots[i].getY() <= me.getY()+Bot.RADIUS*2 && deadBots[i].getY()+Bot.RADIUS*2 >= me.getY()+Bot.RADIUS*2))){ //x to the left and y within y of bot
                    botClose[2] = false;
                }
                if (deadBots[i].getX() >= me.getX() && deadBots[i].getY() <= me.getY()+Bot.RADIUS*2 && deadBots[i].getY() >= me.getY()+Bot.RADIUS*2){
                    botClose[3] = false;
                }
                if (deadBots[i].getY() < me.getY() && deadBots[i].getX()+Bot.RADIUS*2 >= me.getX() && deadBots[i].getX() <= me.getX()+Bot.RADIUS*2){
                    botClose[0] = false;
                }
                if (deadBots[i].getY()+Bot.RADIUS*2 > me.getY()+Bot.RADIUS*2 && deadBots[i].getX()+Bot.RADIUS*2 >= me.getX() && deadBots[i].getX() <= me.getX()+Bot.RADIUS*2){
                    botClose[1] = false;
                }
                    */

            }
        }
            

        //System.out.println(possibleDirections+"one");
        for (int i = 0; i<deadBots.length;i++){
            if (me.getX() >= targetBot.getX() && deadBots[i].getY()+(Bot.RADIUS*2) >= me.getY()+ Bot.RADIUS && deadBots[i].getY() < me.getY()+ Bot.RADIUS && deadBots[i].getX() >= targetBot.getX() && deadBots[i].getX()<= me.getX()){
                run[0] = false;
            } else if (me.getX() <= targetBot.getX() && deadBots[i].getY()+(Bot.RADIUS*2) >= me.getY()+ Bot.RADIUS && deadBots[i].getY() < me.getY()+ Bot.RADIUS && deadBots[i].getX() <= targetBot.getX() && deadBots[i].getX() >= me.getX()){
                run[1] = false;
            } else if (me.getY() >= targetBot.getY() && deadBots[i].getX() <= me.getX()+Bot.RADIUS && deadBots[i].getX()+(Bot.RADIUS*2) > me.getX()+Bot.RADIUS && deadBots[i].getY() >= targetBot.getY() && deadBots[i].getY() <= me.getY()){
                run[2] = false;
            } else if (me.getY() <= +targetBot.getY() && deadBots[i].getX() <= me.getX()+Bot.RADIUS && deadBots[i].getX()+(Bot.RADIUS*2) > me.getX()+Bot.RADIUS && deadBots[i].getY() <= targetBot.getY() && deadBots[i].getY() >= me.getY()){
                run[3] = false;
            }
            distance = Math.sqrt(Math.pow(deadBots[i].getX()-me.getX(),2)+Math.pow(deadBots[i].getY()-me.getY(),2));
            if (distance <= (Bot.RADIUS*2)+20){ //within 20 radius of a dead bot
                targetMove = false;
                canShoot = false;
                if (me.getX() + Bot.RADIUS*2 >= deadBots[i].getX() && me.getX() <= deadBots[i].getX() + Bot.RADIUS*2 && me.getY() + Bot.RADIUS*2 >= deadBots[i].getY() && me.getY() <= deadBots[i].getY() + Bot.RADIUS*2){ //deadbot is colliding with the bot
                    if (me.getX() < deadBots[i].getX()){
                        possibleDirections.remove(Integer.valueOf(4));
                        if (distance == closest){
                            //if (!priority.equals("Horizontal")){
                            //    priorityChanged = true;
                            //}
                            if (!(botClose[0] && botClose[1])){
                                priority = "Horizontal";
                            }
                            
                        }
                        
                    } else if (me.getX() > deadBots[i].getX()){
                        possibleDirections.remove(Integer.valueOf(3));
                        if (distance == closest){
                            //if (!priority.equals("Horizontal")){
                            //    priorityChanged = true;
                            //}
                            if (!(botClose[0] && botClose[1])){
                                priority = "Horizontal";
                            }
                        }
                    }
                    if (me.getY() < deadBots[i].getY()){
                        possibleDirections.remove(Integer.valueOf(2));
                        if (distance == closest || action == 2){
                            //if (!priority.equals("Vertical")){
                            //    priorityChanged = true;
                            //}
                            if (!(botClose[2] && botClose[3])){
                                priority = "Vertical";
                            }
                        }
                    } else if (me.getY() > deadBots[i].getY()){
                        possibleDirections.remove(Integer.valueOf(1));
                        if (distance == closest){
                            //if (!priority.equals("Vertical")){
                            //    priorityChanged = true;
                            //}
                            if (!(botClose[2] && botClose[3])){
                                priority = "Vertical";
                            }
                        }
                    }
                }
                if (deadBots[i].getX() >= me.getX()+Bot.RADIUS*2 && deadBots[i].getY() <= me.getY()+Bot.RADIUS*2 && deadBots[i].getY()+Bot.RADIUS*2 >= me.getY()+Bot.RADIUS*2){ //x is to the right and y is within the y of the bot
                    //&& ((deadBots[i].getY() <= me.getY() && deadBots[i].getY()+Bot.RADIUS*2 >= me.getY())||(deadBots[i].getY() <= me.getY()+Bot.RADIUS*2 && deadBots[i].getY()+Bot.RADIUS*2 >= me.getY()+Bot.RADIUS*2))
                    possibleDirections.remove(Integer.valueOf(4));
                    if (distance == closest){
                        //if (!priority.equals("Horizontal")){
                        //    priorityChanged = true;
                        //}
                        if (!(botClose[0] && botClose[1])){
                            priority = "Horizontal";
                        }
                        
                    }
                }
                if (deadBots[i].getX()+Bot.RADIUS*2 >= me.getX() && deadBots[i].getX() <= me.getX()+Bot.RADIUS*2){ //x is within x of bot
                    if (deadBots[i].getY() <= me.getY()){ //dead bot is above bot
                        possibleDirections.remove(Integer.valueOf(1));
                        if (distance == closest){
                            //if (!priority.equals("Vertical")){
                            //    priorityChanged = true;
                            //}
                            if (!(botClose[2] && botClose[3])){
                                priority = "Vertical";
                            }
                        }
                    }
                    if (deadBots[i].getY()+Bot.RADIUS*2 >= me.getY()+Bot.RADIUS*2){ //dead bot is below bot
                        possibleDirections.remove(Integer.valueOf(2));
                        if (distance == closest){
                            //if (!priority.equals("Vertical")){
                            //    priorityChanged = true;
                            //}
                            if (!(botClose[2] && botClose[3])){
                                priority = "Vertical";
                            }
                        }
                    }
                }
                if (deadBots[i].getX()+RADIUS*2 <= me.getX() && (deadBots[i].getY() <= me.getY()+Bot.RADIUS*2 && deadBots[i].getY()+Bot.RADIUS*2 >= me.getY())){//x is to the left and dead bot's y is within y of bot
                    possibleDirections.remove(Integer.valueOf(3));
                    if (distance == closest){
                        //if (!priority.equals("Horizontal")){
                        //    priorityChanged = true;
                        //}
                        if (!(botClose[0] && botClose[1])){
                            priority = "Horizontal";
                        }
                    }
                }
            }
        }

         
        for (int i = 0; i<liveBots.length;i++){
            if (Math.sqrt(Math.pow(liveBots[i].getX()-me.getX(),2)+Math.pow(liveBots[i].getY()-me.getY(),2)) <= (Bot.RADIUS*2)+Bot.RADIUS*2){
                if (me.getX() + Bot.RADIUS*2 >= liveBots[i].getX() && me.getX() <= liveBots[i].getX() + Bot.RADIUS*2 && me.getY() + Bot.RADIUS*2 >= liveBots[i].getY() && me.getY() <= liveBots[i].getY() + Bot.RADIUS*2){ //deadbot is colliding with the bot
                    if (me.getX() < liveBots[i].getX()){
                        possibleDirections.remove(Integer.valueOf(4));
                        priority = "Horizontal";
                    } else if (me.getX() > liveBots[i].getX()){
                        possibleDirections.remove(Integer.valueOf(3));
                        priority = "Horizontal";
                    }
                    if (me.getY() < liveBots[i].getY()){
                        possibleDirections.remove(Integer.valueOf(2));
                        priority = "Vertical";
                    } else if (me.getY() > liveBots[i].getY()){
                        possibleDirections.remove(Integer.valueOf(1));
                        priority = "Vertical";
                    }
                }
                if (liveBots[i].getX() >= me.getX()+Bot.RADIUS*2 && ((liveBots[i].getY() <= me.getY() && liveBots[i].getY()+Bot.RADIUS*2 >= me.getY())||(liveBots[i].getY() <= me.getY()+Bot.RADIUS*2 && liveBots[i].getY()+Bot.RADIUS*2 >= me.getY()+Bot.RADIUS*2))){ //x is to the right and y is within the y of the bot
                    possibleDirections.remove(Integer.valueOf(4));
                }
                if (liveBots[i].getX()+Bot.RADIUS*2 >= me.getX() && liveBots[i].getX() <= me.getX()+Bot.RADIUS*2){ //x is within x of bot
                    if (liveBots[i].getY() <= me.getY()){ //dead bot is above bot
                        possibleDirections.remove(Integer.valueOf(1));
                    }
                    if (liveBots[i].getY()+Bot.RADIUS*2 >= me.getY()+Bot.RADIUS*2){ //dead bot is below bot
                        possibleDirections.remove(Integer.valueOf(2));
                    }
                }
                if (liveBots[i].getX()+RADIUS*2 <= me.getX() && (liveBots[i].getY() <= me.getY()+Bot.RADIUS*2 && liveBots[i].getY()+Bot.RADIUS*2 >= me.getX())){//x is to the left and dead bot's y is within y of bot
                    possibleDirections.remove(Integer.valueOf(3));
                }
            }
            if (Math.sqrt(Math.pow(liveBots[i].getX()-me.getX(),2)+Math.pow(liveBots[i].getY()-me.getY(),2)) <= (Bot.RADIUS*2)+20){
                if (waitTime == 0){
                    canShoot = true;
                    waitTime = 3;
                }
            }
        }
        //}
        

        
        if (!shotOK){
            canShoot = false;
            targetMove = false; //should not be moving towards the target if the bot cant even shoot 
        }

        //System.out.print("["+run[0]+", "+run[1]+", "+ run[2]+", "+run[3]+"]");
        //System.out.print(canShoot);
        //System.out.print("Wait time: "+waitTime);
        if (canShoot){
            //need to find the direction of both bots
            if (targetBot.getY()+(Bot.RADIUS*2) > me.getY()+ Bot.RADIUS && targetBot.getY() < me.getY()+ Bot.RADIUS){ //shooting right or left
                
                if (me.getX() > targetBot.getX()){
                    if (run[0]){
                        //System.out.println("SHoot!");
                        waitTime = 3;
                        return 7;
                    }
                } else if (me.getX() < targetBot.getX()){
                    if (run[1]){
                        waitTime = 3;
                        return 8;
                    }
                }
            } else if (targetBot.getX() < me.getX()+Bot.RADIUS && targetBot.getX()+(Bot.RADIUS*2) > me.getX()+Bot.RADIUS){
                if (me.getY() > targetBot.getY()){
                    if (run[2]){
                        waitTime = 3;
                        return 5;
                    }
                } else if (me.getY() < +targetBot.getY()){
                    if (run[3]){
                        waitTime = 3;
                        return 6;
                    }
                }
            }
        }

        

        
        if (targetBot.getY()+(Bot.RADIUS*2) > me.getY() && targetBot.getY() < me.getY()+ Bot.RADIUS*2){ 
            priority = "Horizontal";
        } else if (targetBot.getX()+(Bot.RADIUS*2) > me.getX() && targetBot.getX() < me.getX()+ Bot.RADIUS*2){
            priority = "Vertical";
        }

        //need to move the bot to align with the closest 
        if (targetMove){
            if (me.getX() < targetBot.getX() && possibleDirections.contains(4) && priority.equals("Horizontal")){ //x position - (try to travel x position first)
                action = 4;
            } else if (me.getX() > targetBot.getX() && possibleDirections.contains(3)&& priority.equals("Horizontal")){
                action = 3;
            }
            if (me.getY() < targetBot.getY() && possibleDirections.contains(2)&& priority.equals("Vertical")){ //x position - (try to travel x position first)
                action = 2;
            } else if (me.getY() > targetBot.getY() && possibleDirections.contains(1)&& priority.equals("Vertical")){
                action = 1;
            }
        }
        
                    
        //System.out.println("Possible directions: "+possibleDirections);
        //System.out.println(priority);
        //System.out.println("Direction: "+action);
        if (!possibleDirections.contains(action) && possibleDirections.size() > 0){
            
            action = possibleDirections.get(random.nextInt(possibleDirections.size()));
             
            if (priority.equals("Horizontal")){
                if (possibleDirections.contains(1)){
                    action = 1;
                } else if (possibleDirections.contains(2)){
                    action = 2;
                }
            } else if (priority.equals("Vertical")) {
                if (possibleDirections.contains(3)){
                    action = 3;
                } else if (possibleDirections.contains(4)){
                    action = 4;
                }
            }
            
           
        }

        return action;
    }
    public int getDirection(Bullet bullet){
        if (bullet.getXSpeed() > 0) {
            return 4; //right
        } else if (bullet.getXSpeed() < 0){
            return 3; //left
        } else if (bullet.getYSpeed() > 0){
            return 2; //down
        } else {
            return 1; //up
        }
        //throw new UnsupportedOperationException("Unimplemented method 'getMove'");
    }
	/**
	 * Decide whether we are overheating this round or not
	 */
	public void newRound()
	{
		possibleDirections.add(1);
        possibleDirections.add(2);
        possibleDirections.add(3);
        possibleDirections.add(4);
	}

	/**
	 * Send the message and then blank out the message string
	 */
	public String outgoingMessage()
	{
		String msg = nextMessage;
		nextMessage = null;
		return msg;
	}

	/**
	 * Construct and return my name
	 */
	public String getName()
	{
		if (name == null)
			name = "User3689";
		return name;
	}

	/**
	 * Team "Arena"
	 */
	public String getTeamName()
	{
		return "Arena";
	}

	/**
	 * Draws the bot at x, y
	 * @param g The Graphics object to draw on
	 * @param x Left coord
	 * @param y Top coord
	 */
	public void draw (Graphics g, int x, int y)
	{
		if (current != null)
			g.drawImage(current, x, y, Bot.RADIUS*2, Bot.RADIUS*2, null);
		else
		{
			g.setColor(Color.lightGray);
			g.fillOval(x, y, Bot.RADIUS*2, Bot.RADIUS*2);
		}
	}

	/**
	 * If the message is announcing a kill for me, schedule a trash talk message.
	 * @param botNum ID of sender
	 * @param msg Text of incoming message
	 */
	public void incomingMessage(int botNum, String msg)
	{
		if (botNum == BattleBotArena.SYSTEM_MSG && msg.matches(".*destroyed by "+getName()+".*"))
		{
			int msgNum = (int)(Math.random()*killMessages.length);
			nextMessage = killMessages[msgNum];
			msgCounter = (int)(Math.random()*30 + 30);
		}
	}

}
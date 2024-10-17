package bots;
import java.util.Random;

import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class DylanBot extends Bot{
    
	/**
	 * The radius of a Bot. Each Bot should fit into a circle inscribed into a
	 * square with height and width equal to RADIUS * 2.
	 */
	public static final int RADIUS = 13;//ROWBOTTOM was 10

	/**
	 * This is your Bot's number, a unique identifier assigned at the beginning of each round.
	 */
	protected int botNumber;

    	/**
	 * Bot image
	 */
	Image current, up, down, right, left;

	/**
	 * This method is called at the beginning of each round. Use it to perform
	 * any initialization that you require when starting a new round.
	 */
	public void newRound(){

    }

	/**
	 * This method is called at every time step to find out what you want your
	 * Bot to do. The legal moves are defined in constants in the BattleBotArena
	 * class (UP, DOWN, LEFT, RIGHT, FIREUP, FIREDOWN, FIRELEFT, FIRERIGHT, STAY,
	 * SEND_MESSAGE). <br><br>
	 *
	 * The <b>FIRE</b> moves cause a bullet to be created (if there are
	 * not too many of your bullets on the screen at the moment). Each bullet
	 * moves at speed set by the BULLET_SPEED constant in BattleBotArena. <br><br>
	 *
	 * The <b>UP</b>, <b>DOWN</b>, <b>LEFT</b>, and <b>RIGHT</b> moves cause the
	 * bot to move BOT_SPEED
	 * pixels in the requested direction (BOT_SPEED is a constant in
	 * BattleBotArena). However, if this would cause a
	 * collision with any live or dead bot, or would move the Bot outside the
	 * playing area defined by TOP_EDGE, BOTTOM_EDGE, LEFT_EDGE, and RIGHT_EDGE,
	 * the move will not be allowed by the Arena.<br><Br>
	 *
	 * The <b>SEND_MESSAGE</b> move (if allowed by the Arena) will cause a call-back
	 * to this Bot's <i>outgoingMessage()</i> method, which should return the message
	 * you want the Bot to broadcast. This will be followed with a call to
	 * <i>incomingMessage(String)</i> which will be the echo of the broadcast message
	 * coming back to the Bot.
	 *
	 * @param me		A BotInfo object with all publicly available info about this Bot
	 * @param shotOK	True if a FIRE move is currently allowed
	 * @param liveBots	An array of BotInfo objects for the other Bots currently in play
	 * @param deadBots	An array of BotInfo objects for the dead Bots littering the arena
	 * @param bullets	An array of all Bullet objects currently in play
	 * @return			A legal move (use the constants defined in BattleBotArena)
	 */
    @Override
	public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets){
        int currentY = (int)me.getY();
		int currentX = (int)me.getX();
        double distanceToTop = currentY; // Distance to the top wall
        double distanceToBottom = BattleBotArena.BOTTOM_EDGE - currentY; // Distance to the bottom wall
        boolean atTop = currentY<=RADIUS;
        boolean atBottom = currentY>=BattleBotArena.BOTTOM_EDGE - RADIUS * 2;
		boolean atLeft = currentX<=RADIUS;
		boolean atRight = currentX>=BattleBotArena.RIGHT_EDGE-RADIUS*2;
		boolean defend = false;
		boolean dangerUp = false;
        boolean dangerDown = false;
        boolean dangerLeft = false;
        boolean dangerRight = false;
		if ((atTop)||(atBottom)){
		for (int i = 0;i<bullets.length;i++){
			Bullet currentBullet = bullets[i];
			int bulletX = (int) currentBullet.getX();
            int bulletY = (int) currentBullet.getY();
			int bulletXSpeed = (int)currentBullet.getXSpeed();
            int bulletYSpeed = (int)currentBullet.getYSpeed();
			if (bulletYSpeed > 0 && bulletY < currentY) {
                dangerUp = true;  // Bullet is coming from below
            }
            if (bulletYSpeed < 0 && bulletY > currentY) {
                dangerDown = true;  // Bullet is coming from above
            }
            if (bulletXSpeed > 0 && bulletX < currentX) {
                dangerLeft = true;  // Bullet is coming from the right
            }
            if (bulletXSpeed < 0 && bulletX > currentX) {
                dangerRight = true;  // Bullet is coming from the left
            }
        

        // Decide movement based on threats
        if ((dangerUp)&&(!atLeft)) {
            return BattleBotArena.LEFT;   // Move up if there's danger from below
        }
        if ((dangerDown)&&(!atRight)) {
            return BattleBotArena.RIGHT; // Move down if there's danger from above
        }
        if (dangerLeft) {
			if (!atTop){
            return BattleBotArena.UP;  // Move left if there's danger from the right
			}else{
				return BattleBotArena.DOWN;
			}
        }
        if (dangerRight) {
			if (!atBottom){
            return BattleBotArena.DOWN; // Move right if there's danger from the left
			}else{
				return BattleBotArena.UP;
			}
        }
		}
	}

		for (int i = 0;i<liveBots.length;i++){
			BotInfo currentBot = liveBots[i];
			int currentBotX = (int)(currentBot.getX())+ Bot.RADIUS;
			int currentBotY = (int)(currentBot.getY())+ Bot.RADIUS;
			if (Math.abs(currentX - currentBotX) < RADIUS * 2 && Math.abs(currentY - currentBotY) < RADIUS * 2) {
                Random random = new Random();
				int moveCount = random.nextInt(2);
				if (moveCount == 0){
				return BattleBotArena.UP;
				}else{
					return BattleBotArena.DOWN;
				}
            }

		}
        if ((!atTop)&&(!atBottom)){
            if (!defend){
                if (distanceToTop < distanceToBottom) {
                    // If the top wall is closer, move up
                    return BattleBotArena.UP;
                }else{
                    return BattleBotArena.DOWN;
                } 
            }else{
                if (distanceToTop<distanceToBottom){
					//Fire down if moving up
                return BattleBotArena.FIREDOWN;
                }else{
                    return BattleBotArena.FIREUP;
                }
            }
        }else{
        Random random = new Random();
        int moveCount = random.nextInt(5); // Generates 0 to 4
        if (moveCount == 0){
            return BattleBotArena.FIREUP;
        }else if (moveCount == 1){
            return BattleBotArena.FIREDOWN;
        }else if (moveCount == 2){
            return BattleBotArena.FIRELEFT;
        }else{
            return BattleBotArena.FIRERIGHT;
        }
    }
    }

	/**
	 * Called when it is time to draw the Bot. Your Bot should be (mostly)
	 * within a circle inscribed inside a square with top left coordinates
	 * <i>(x,y)</i> and a size of <i>RADIUS * 2</i>. If you are using an image,
	 * just put <i>null</i> for the ImageObserver - the arena has some special features
	 * to make sure your images are loaded before you will use them.
	 *
	 * @param g The Graphics object to draw yourself on.
	 * @param x The x location of the top left corner of the drawing area
	 * @param y The y location of the top left corner of the drawing area
	 */

	public void draw (Graphics g, int x, int y){
        if (current != null) {
            g.drawImage(current, x, y, RADIUS * 2, RADIUS * 2, null);
        }
    }

	/**
	 * This method will only be called once, just after your Bot is created,
	 * to set your name permanently for the entire match.
	 *
	 * @return The Bot's name
	 */
	public String getName(){
        return "DylanBot";
    }

	/**
	 * This method is called at every time step to find out what team you are
	 * currently on. Of course, there can only be one winner, but you can
	 * declare and change team allegiances throughout the match if you think
	 * anybody will care. Perhaps you can send coded broadcast message or
	 * invitation to other Bots to set up a temporary team...
	 *
	 * @return The Bot's current team name
	 */
	public String getTeamName(){
        return "Arena";
    }

	/**
	 * This is only called after you have requested a SEND_MESSAGE move (see
	 * the documentation for <i>getMove()</i>). However if you are already over
	 * your messaging cap, this method will not be called. Messages longer than
	 * 200 characters will be truncated by the arena before being broadcast, and
	 * messages will be further truncated to fit on the message area of the screen.
	 *
	 * @return The message you want to broadcast
	 */
	public String outgoingMessage(){
        return "Hi";
    }

	/**
	 * This is called whenever the referee or a Bot sends a broadcast message.
	 *
	 * @param botNum The ID of the Bot who sent the message, or <i>BattleBotArena.SYSTEM_MSG</i> if the message is from the referee.
	 * @param msg The text of the message that was broadcast.
	 */
	public void incomingMessage(int botNum, String msg){

    }

	/**
	 * This is called by the arena at startup to find out what image names you
	 * want it to load for you. All images must be stored in the <i>images</i>
	 * folder of the project, but you only have to return their names (not
	 * their paths).<br><br>
	 *
	 * PLEASE resize your images in an image manipulation
	 * program. They should be squares of size RADIUS * 2 so that they don't
	 * take up much memory.
	 *
	 * @return An array of image names you want the arena to load.
	 */
	public String[] imageNames(){
        String[] paths = {"drone_up.png", "drone_down.png", "drone_right.png", "drone_left.png"};
        return paths;
        
    }

	/**
	 * Once the arena has loaded your images (see <i>imageNames()</i>), it
	 * calls this method to pass you the images it has loaded for you. Store
	 * them and use them in your draw method.<br><br>
	 *
	 * PLEASE resize your images in an
	 * image manipulation program. They should be squares of size RADIUS * 2 so
	 * that they don't take up much memory.<br><br>
	 *
	 * CAREFUL: If you got the file names wrong, the image array might be null
	 * or contain null elements.
	 *
	 * @param images The array of images (or null if there was a problem)
	 */
	public void loadedImages(Image[] images){
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

	/**
	 * Called by the arena to assign your unique id number at the start of each round.
	 * There is probably no need to override this method.
	 *
	 * @param botNum Your ID number
	 */
	public void assignNumber(int botNum)
	{
		this.botNumber = botNum;
	}

	/**
	 * Stops Bot developers from cheating by spawning a Thread. The human referee
	 * should also check to make sure they are only using a single class and no
	 * inner classes (check to make sure there is only one .class file per Bot).
	 */
	//final public void run()
	{

	}

	/**
	 * Stops Bot developers from cheating by using a Timer. The human referee
	 * should also check to make sure they are only using a single class and no
	 * inner classes (check to make sure there is only one .class file per Bot).
	 */
	//final public void actionPerformed(ActionEvent e)
	{
		
	}
}
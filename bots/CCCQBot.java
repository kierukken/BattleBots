package bots;

import java.awt.Graphics;
import java.awt.Image;
import java.util.Arrays;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class CCCQBot extends Bot {
	Image picture;
	//method to check if bot needs to move to dodge incoming bullets
	boolean bulletTowards(double mePos, double bulletPos, double bulletSpeed) {
		return ((bulletPos < mePos) && (bulletPos + 10 * bulletSpeed > mePos - 13)) || ((bulletPos > mePos) && (bulletPos + 10 * bulletSpeed < mePos + 13));
	}
	//method to check if there is a dead bot to block a bullet
	boolean deadBotInBetween(double sourcePosX, double sourcePosY, double targetPos, double deadPosX, double deadPosY, boolean isHorizontal) {
		if (isHorizontal) {
			if (sourcePosY > deadPosY - 13 && sourcePosY < deadPosY + 13) {
				if (deadPosX > sourcePosX && deadPosX < targetPos) {
					return true;
				} else if (deadPosX < sourcePosX && deadPosX > targetPos) {
					return true;
				}
			}
		} else {
			if (sourcePosX > deadPosX - 13 && sourcePosX < deadPosX + 13) {
				if (deadPosY > sourcePosY && deadPosY < targetPos) {
					return true;
				} else if (deadPosY < sourcePosY && deadPosY > targetPos) {
					return true;
				}
			}
		}
		return false;
	}

    /**
	 * The radius of a Bot. Each Bot should fit into a circle inscribed into a
	 * square with height and width equal to RADIUS * 2.
	 */
	public static final int RADIUS = 13;//TODO: Add this back in S2
	//public static final int RADIUS = (int)(BattleBotArena.RIGHT_EDGE/105);//changed from 13//changed from 10

	/**
	 * This is your Bot's number, a unique identifier assigned at the beginning of each round.
	 */
	protected int botNumber;

	/**
	 * This method is called at the beginning of each round. Use it to perform
	 * any initialization that you require when starting a new round.
	 */
	public void newRound() {

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
	 * @param shotOK	True iff a FIRE move is currently allowed
	 * @param liveBots	An array of BotInfo objects for the other Bots currently in play
	 * @param deadBots	An array of BotInfo objects for the dead Bots littering the arena
	 * @param bullets	An array of all Bullet objects currently in play
	 * @return			A legal move (use the constants defined in BattleBotArena)
	 */
	public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
		double[] botPos = {me.getX() + 13, me.getY() + 13}; //coordinates of me
		double[] nearestDisabled = null; //coordinates of the nearest overheated bot or out of ammo bot
		//defense
		for (Bullet bullet: bullets) {
			double[] bulletPos = {bullet.getX(), bullet.getY()};
			double[] speed = {bullet.getXSpeed(), bullet.getYSpeed()};
			if (bulletPos[0] > botPos[0] - 13 && bulletPos[0] < botPos[0] + 13 && speed[1] != 0) { //detect bullet above or below bot
				if (bulletTowards(botPos[1], bulletPos[1], speed[1])) { //dodge incoming bullet
					boolean bulletBlockedByBot = false;
					for (BotInfo deadBot: deadBots) {
						double[] deadPos = {deadBot.getX() + 13, deadBot.getY() + 13};
						if (!deadBotInBetween(bulletPos[0], bulletPos[1], botPos[1], deadPos[0],deadPos[1], false)) {
							if (botPos[1] + 13 > deadPos[1] - 13 && botPos[1] - 13 < deadPos[1] + 13) {
								if (deadPos[0] + 39 > bulletPos[0] && deadPos[0] < botPos[0]) {
									return BattleBotArena.RIGHT;
								} else if (deadPos[0] - 39 < bulletPos[0] && deadPos[0] > botPos[0]) {
									return BattleBotArena.LEFT;
								}
							}
						} else {
							bulletBlockedByBot = true;
							break;
						}
					}
					if (!bulletBlockedByBot) {
						if (bulletPos[0] < 26) {
							return BattleBotArena.RIGHT;
						} else if (bulletPos[0] > 974) {
							return BattleBotArena.LEFT;
						} else if (bulletPos[0] > botPos[0]) {
							return BattleBotArena.LEFT;
						} else {
							return BattleBotArena.RIGHT;
						}
					}
				}
			} else if (bulletPos[1] > botPos[1] - 13 && bulletPos[1] < botPos[1] + 13 && speed[0] != 0) { //detect bullet left or right of bot
				if (bulletTowards(botPos[0], bulletPos[0], speed[0])) { //dodge incoming bullet
					boolean bulletBlockedByBot = false;
					for (BotInfo deadBot: deadBots) {
						double[] deadPos = {deadBot.getX()+13, deadBot.getY()+13};
						if (!deadBotInBetween(bulletPos[0], bulletPos[1], botPos[0], deadPos[0], deadPos[1], true)) {
							if (botPos[0] + 13 > deadPos[0] - 13 && botPos[0] - 13 < deadPos[0] + 13) {
								if (deadPos[1] + 39 > bulletPos[1] && deadPos[1] < botPos[1]) {
									return BattleBotArena.DOWN;
								} else if (deadPos[1] - 39 < bulletPos[1] && deadPos[1] > botPos[1]) {
									return BattleBotArena.UP;
								}
							}
						} else {
							bulletBlockedByBot = true;
							break;
						}
					}
					if (!bulletBlockedByBot) {
						if (bulletPos[1] < 36) {
							return BattleBotArena.DOWN;
						} else if (bulletPos[1] > 674) {
							return BattleBotArena.UP;
						} else if (bulletPos[1] > botPos[1]) {
							return BattleBotArena.UP;
						} else {
							return BattleBotArena.DOWN;
						}
					}
				}
			}
		}
		//offense
		if (shotOK) {
			for (BotInfo liveBot: liveBots) {
				double[] livePos = {liveBot.getX() + 13, liveBot.getY() + 13};
				//the following code is used to check live bots that are lined up and close enough to guarantee hit
				if (livePos[0] > botPos[0] - 13 && livePos[0] < botPos[0] + 13) { //check live bot above or below
					for (BotInfo deadBot: deadBots){
						double[] deadPos = {deadBot.getX() + 13, deadBot.getY() + 13};
						if (!deadBotInBetween(botPos[0], botPos[1], livePos[1], deadPos[0], deadPos[1], false)){
							if (livePos[1] > botPos[1] && livePos[1] - botPos[1] < 56) { //check live bot in range below
								return BattleBotArena.FIREDOWN;
							} else if (livePos[1] < botPos[1] && botPos[1] - livePos[1] < 56) { //check live bot in range above
								return BattleBotArena.FIREUP;
							}
						}
					}
				} else if (livePos[1] > botPos[1] - 13 && livePos[1] < botPos[1] + 13) { //check live bot on the left or right
					for (BotInfo deadBot: deadBots){
						double[] deadPos = {deadBot.getX() + 13, deadBot.getY() + 13};
						if (!deadBotInBetween(botPos[0], botPos[1], livePos[0], deadPos[0], deadPos[1], true)){
							if (livePos[0] > botPos[0] && livePos[0] - botPos[0] < 56) { //cehck live bot in range on the right
								return BattleBotArena.FIRERIGHT;
							} else if (livePos[0] < botPos[0] && botPos[0] - livePos[0] < 56) { //check live bot in range on the left
								return BattleBotArena.FIRELEFT;
							}
						}
					}
				}
				if (liveBot.isOverheated() || liveBot.getBulletsLeft() == 0) { //check overheated or out of ammo live bot
					if (livePos[0] > botPos[0] - 13 && livePos[0] < botPos[0] + 13) { //check if overheated bot is above or below
						if (livePos[1] > botPos[1]) { //overheated bot is below
							return BattleBotArena.FIREDOWN;
						} else { //above
							return BattleBotArena.FIREUP;
						}
					} else if (livePos[1] > botPos[1] - 13 && livePos[1] < botPos[1] + 13) { //check if overheated bot is on the left or right
						if (livePos[0] > botPos[0]) { //overheated bot is on the right
							return BattleBotArena.FIRERIGHT;
						} else { //left
							return BattleBotArena.FIRELEFT;
						}
					} else { //update the location of the nearest overheated bot if needed
						if (nearestDisabled == null) { //there is no other overheated bot
							nearestDisabled = livePos;
						} else { //there is already an overheated bot
							if (Math.min(Math.abs(botPos[0] - livePos[0]), Math.abs(botPos[1] - livePos[1])) < Math.min(Math.abs(botPos[0] - nearestDisabled[0]), Math.abs(botPos[1] - nearestDisabled[1]))) {
								nearestDisabled = livePos;
							}
						}
					}
				}
			}
			if (nearestDisabled != null) { //move towards the nearest overheated bot
				//check for dead bots blocking the path to overheated bot
				for (BotInfo deadBot: deadBots) {
					double[] deadPos = {deadBot.getX() + 13, deadBot.getY() + 13};
					//check for dead bots blocking above or below overheated bot
					if (botPos[0] < nearestDisabled[0]) { //overheated bot is to the right
						if (deadBotInBetween(nearestDisabled[0] - 13, botPos[1], nearestDisabled[1], deadPos[0], deadPos[1], false)) { //dead bot blocking vertical path
							if (botPos[1] < nearestDisabled[1]) { //overheated bot is to the bottom
								return BattleBotArena.DOWN;
							} else { //top
								return BattleBotArena.UP;
							}
						}
					} else { //left
						if (deadBotInBetween(nearestDisabled[0] + 13, botPos[1], nearestDisabled[1], deadPos[0], deadPos[1], false)) { //dead bot blocking vertical path
							if (botPos[1] < nearestDisabled[1]) { //overheated bot is to the bottom
								return BattleBotArena.DOWN;
							} else { //top
								return BattleBotArena.UP;
							}
						}
					}
					//check for dead bots blocking left or right of overheated bot
					if (botPos[1] < nearestDisabled[1]) { //overheated bot is to the bottom
						if (deadBotInBetween(botPos[0], nearestDisabled[1] - 13, nearestDisabled[0], deadPos[0], deadPos[1], true)) { //dead bot blocking vertical path
							if (botPos[0] < nearestDisabled[0]) { //overheated bot is to the right
								return BattleBotArena.RIGHT;
							} else { //left
								return BattleBotArena.LEFT;
							}
						}
					} else { //top
						if (deadBotInBetween(botPos[0], nearestDisabled[1] + 13, nearestDisabled[0], deadPos[0], deadPos[1], true)) { //dead bot blocking vertical path
							if (botPos[0] < nearestDisabled[0]) { //overheated bot is to the right
								return BattleBotArena.RIGHT;
							} else { //left
								return BattleBotArena.LEFT;
							}
						}
					}
				}
				//take the shorter of horizontal or vertical distance and move towards overheated
				if (Math.abs(botPos[0] - nearestDisabled[0]) < Math.abs(botPos[1] - nearestDisabled[1])) { //horizontal distance is shorter
					if (botPos[0] < nearestDisabled[0]) { //overheated bot is on the right
						return BattleBotArena.RIGHT;
					} else { //left
						return BattleBotArena.LEFT;
					}
				} else { //vertical distance is shorter
					if (botPos[1] < nearestDisabled[1]) { //overheated bot is below
						return BattleBotArena.DOWN;
					} else { //above
						return BattleBotArena.UP;
					}
				}
			}
		}
		return BattleBotArena.STAY;
	};

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
	public void draw (Graphics g, int x, int y) {
		g.drawImage(picture , x, y , 25 , 25 , null);
	}

	/**
	 * This method will only be called once, just after your Bot is created,
	 * to set your name permanently for the entire match.
	 *
	 * @return The Bot's name
	 */
	public String getName(){
        return "";
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
        return "";
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
        return "L";
    }

	/**
	 * This is called whenever the referee or a Bot sends a broadcast message.
	 *
	 * @param botNum The ID of the Bot who sent the message, or <i>BattleBotArena.SYSTEM_MSG</i> if the message is from the referee.
	 * @param msg The text of the message that was broadcast.
	 */
	public void incomingMessage(int botNum, String msg) {

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
	public String[] imageNames() {
		String [] images = {"gpt.jpg"};
		return images;
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
	public void loadedImages(Image[] images) {
		picture = images[0];
	}
}
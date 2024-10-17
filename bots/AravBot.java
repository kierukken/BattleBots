package bots;

import java.awt.Graphics;
import java.awt.Image;

import arena.BotInfo;
import arena.Bullet;
import arena.BattleBotArena;

public class AravBot extends Bot {
    Image image;
    /**
     * direction to shoot in when spraying bullets
     *  5 = fire up
     * 6 = fire down
     * 7 = fire left 
     * 8 = fire right */
    int shootDirection = 5;
    /**
     * counts the current frame
     */
    int counter = 0;

    @Override
    public void newRound() {
    }
    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        counter++; // increment the counter every time called
        Bullet closest = findClosestBullet(me, bullets);
        if (closest == null) return secondaryAction(me, shotOK, liveBots); // if no bullets to dodge, go to secondary action
        String direction = getBulletDirection(closest, me); 
        double innderDistance = getInnerDist(closest, me, direction);
        int timeToDodge = 20; // a safe time to dodge
        if (direction == "top") {
            // if the distance to the bullet is small enough, check if by the edges and return direction accordingly
            if (Math.abs(closest.getY() - (me.getY())) / 6 - Math.abs(innderDistance / 3) < timeToDodge) return canGo(innderDistance, me) ? BattleBotArena.RIGHT : BattleBotArena.LEFT;
        } else if (direction == "bottom") { 
            // if the distance to the bullet is small enough, check if by the edges and return direction accordingly
            if (Math.abs(closest.getY() - (me.getY() + (Bot.RADIUS * 2))) / 6 - (innderDistance / 3) < timeToDodge)  return canGo(innderDistance, me) ? BattleBotArena.RIGHT : BattleBotArena.LEFT;
        } else if (direction == "left") {
            // if the distance to the bullet is small enough, check if by the edges and return direction accordingly
            if (Math.abs(closest.getX() - (me.getX())) / 6 - (innderDistance / 3) < timeToDodge) return canGo(innderDistance, me) ? BattleBotArena.DOWN : BattleBotArena.UP;
        } else if (direction == "right") {
            // if the distance to the bullet is small enough, check if by the edges and return direction accordingly
            if (Math.abs(closest.getX() - (me.getX() + (Bot.RADIUS * 2))) / 6 - (innderDistance / 3) < timeToDodge) return canGo(innderDistance, me) ? BattleBotArena.DOWN : BattleBotArena.UP;
        } // if no dodging is requiredm then do secondary action
        return secondaryAction(me, shotOK, liveBots);
    }
    /**
     * Finds the closest bullet to the bot that may harm the bot.
     * @param me A BotInfo object with all publicly available info about this Bot
     * @param bullets An array of all Bullet objects currently in play
     * @return A Bullet object that is the closest to the bot
     */
    public Bullet findClosestBullet(BotInfo me, Bullet[] bullets) {
        if (bullets.length < 1) return null; // if no bullets in action, leave
        Bullet closestX = bullets[0]; // initialize closest bullet in the x direction
        Bullet closestY = bullets[0]; // initialize closest bullet in the y direction
        double minDistX = BattleBotArena.RIGHT_EDGE; // initialize distance to closest bullet in the x direction
        double minDistY = BattleBotArena.BOTTOM_EDGE; // initialize distance to closest bullet in the y direction
        String direction;
        int extraBorder = 20; // an offset value so dodging isnt too close 
        for (Bullet bullet : bullets) {
            if (bullet.getX() > (me.getX() - extraBorder) && bullet.getX() < (me.getX() + Bot.RADIUS * 2 + extraBorder)) { // check if in your box range 
                direction = getBulletDirection(bullet, me); // must be in the top / bottom range
                if ((direction == "top" && bullet.getYSpeed() > 0)) { // check if coming towards you from top
                    if (Math.abs((me.getY()) - bullet.getY()) < minDistX) { // check if distance is smallest
                        minDistX = Math.abs((me.getY()) - bullet.getY()); // set to distance
                        closestX = bullet; // set to closest bullet
                    }
                } else if (direction == "bottom" && bullet.getYSpeed() < 0) { // check if coming towards you from bottom
                    if (Math.abs(bullet.getY() - (me.getY() + Bot.RADIUS * 2)) < minDistX) {
                        minDistX = Math.abs(bullet.getY() - (me.getY() + Bot.RADIUS * 2)); // set to distance
                        closestX = bullet; // set to closest bullet
                    }
                }
            } else if (bullet.getY() > (me.getY() - extraBorder) && bullet.getY() < (me.getY() + Bot.RADIUS * 2 + extraBorder)) { // check if in your box range
                direction = getBulletDirection(bullet, me); // must be in the left / right range
                if ((direction == "left" && bullet.getXSpeed() > 0)) { // check if coming towards you from left
                    if (Math.abs((me.getX()) - bullet.getX()) < minDistY) { // check if distance is smallest
                        minDistY = Math.abs(bullet.getX() - (me.getX())); // set to distance
                        closestY = bullet; // set to closest bullet
                    }
                } else if (direction == "right" && bullet.getXSpeed() < 0) { // check if coming towards you from right
                    if (Math.abs(bullet.getX() - (me.getX() + Bot.RADIUS * 2)) < minDistY) { // check if distance is smallest
                        minDistY = Math.abs(bullet.getX() - (me.getX() + Bot.RADIUS * 2)); // set to distance
                        closestY = bullet; // set to closest bullet
                    }
                }
            }
        }
        // System.out.println((distX<distY?"distX":"distY") + ": "+ Math.min(distX,distY) +" "+ (distX<distY?closestX:closestY));
        if (Math.min(minDistX, minDistY) == 700) return null; //if the closest bullet is not in our range or doesnt satisy our conditions, leave
        return (minDistX < minDistY ? closestX : closestY); // return bullet with smallest distance
    }
    /**
     * Finds the relative direction between a bot and a bullet. 
     * @param bullet A Bullet object that is currently getting checked
     * @param me A BotInfo object with all publicly available info about this Bot
     * @return String that can be "top", "bottom", "left", "right"
     */
    public String getBulletDirection(Bullet bullet, BotInfo me) {
        if (bullet.getYSpeed() != 0) return bullet.getY() > (me.getY() + Bot.RADIUS * 2) ? "bottom" : "top"; // if ypos of bullet is greater, then it is under the bot.
        return bullet.getX() > (me.getX() + Bot.RADIUS * 2) ? "right" : "left"; // if xpos of bullet is greater, then it is right of the bot
    }
    /**
     * Finds the distance between the center of a bot and a bullet
     * @param bullet A Bullet object that is currently getting checked
     * @param me A BotInfo object with all publicly available info about this Bot
     * @param direction String that can be "top", "bottom", "left", "right" and signifies the direction the bullet is coming from
     * @return double, if negative, and coming from the vertical direction, then left side of bot, if coming from horizontal direction, then top side of bot
     */
    public double getInnerDist(Bullet bullet, BotInfo me, String direction) {
        if (direction == "top" || direction == "bottom") return (bullet.getX() - (me.getX() + Bot.RADIUS)); // if negative, then left side of bot,
        return bullet.getY() - (me.getY() + Bot.RADIUS); // if negative, then top side of bot,
    }
    /**
     * Checks to see if the bot is on the very edge and decides direction accordingly
     * @param innderDistance the distance between the center of a bot and a bullet
     * @param me A BotInfo object with all publicly available info about this Bot
     * @return boolean, true means move right / down
     */
    public boolean canGo(double innderDistance, BotInfo me) {
        // true = right / down
        boolean go = innderDistance < 0;
        if ((me.getX() <= BattleBotArena.LEFT_EDGE || (me.getX() + Bot.RADIUS * 2) >= (BattleBotArena.RIGHT_EDGE)) || (me.getY() <= BattleBotArena.TOP_EDGE || (me.getY() + Bot.RADIUS * 2) >= (BattleBotArena.BOTTOM_EDGE ))) 
            go = !go; // flip direction of movement if your are too close to the edges, therefore move away from edges
        return go; // direction of movement
    }
    /**
     * Determines the secondary course of action after dodging is not required
     * @param me A BotInfo object with all publicly available info about this Bot
     * @param shotOK True iff a FIRE move is currently allowed 
     * @param liveBots An array of BotInfo objects for the other Bots currently in play
     * @return int, action to complete
     */
    public int secondaryAction(BotInfo me, boolean shotOK, BotInfo[] liveBots) {
        int edgeDodge = leaveEdges(me, 100, 25);
        BotInfo closest = findClosestBot(me, liveBots);
        if (edgeDodge < 25) return edgeDodge; // needs to dodge edge, then dodge edge, priority 1
        if (shotOK){
            if(closest!=null){ // if there is a bot we can aim at, then shoot it, priority 2
                int shoot = getShootDirection(closest, me, 10); 
                if(shoot < 26) return shoot; // check failsafe before returning the shooting direction
            } else { // if there is nothing to shoot at, then spray the entire area with bullets, priority 3
                if(shootDirection>8){ // loop between all the shooting directions 5-8 and spray fire in all directions
                    shootDirection=5;
                } return shootDirection++;
            }
        } // if no actions are required, taunt enemy, priority 4
        return BattleBotArena.SEND_MESSAGE;
    }
    /**
     * Determines the direction to move so that the bot is away from the edges and cannot get cornered easily
     * @param me A BotInfo object with all publicly available info about this Bot
     * @param offset int, distance from the edge that is considered safe
     * @param failValue int, value returned when no need to leave edge
     * @return int, direction to move
     * @return int, return failValue if no action is required
     */
    public int leaveEdges(BotInfo me, int offset, int failValue) {
        // check each border to see if you are far away enough 
        if (me.getX() < offset) return BattleBotArena.RIGHT;
        if (me.getY() < offset) return BattleBotArena.DOWN; 
        if (me.getY() + (Bot.RADIUS * 2) > (BattleBotArena.BOTTOM_EDGE - offset)) return BattleBotArena.UP;
        if (me.getX() + (Bot.RADIUS * 2) > (BattleBotArena.RIGHT_EDGE - offset)) return BattleBotArena.LEFT;
        return failValue; // if you are in the range, then return no movement required
    }
    /**
     * Find the closest bot that is in the range of the bullet to shoot at. 
     * @param me A BotInfo object with all publicly available info about this Bot
     * @param bots An array of BotInfo objects for the other Bots currently in play
     * @return A BotInfo object with all publicly available info about the closest Bot
     * @return return null if no bot is detected 
     */
    public BotInfo findClosestBot(BotInfo me, BotInfo[] bots) {
        if (bots.length < 1) return null; // leave if no bots in field
        BotInfo closestX = bots[0]; // initialize current closest bot in the x direction
        BotInfo closestY = bots[0]; // initialize current closest bot in the y direction
        double minDistX = BattleBotArena.RIGHT_EDGE; // initialize current distance closest bot in the x direction
        double minDistY = BattleBotArena.BOTTOM_EDGE; // initialize current distance closest bot in the y direction
        int extraBorder = 20; // an offset value so you can shoot enemies before they are directly in range 
        for (BotInfo bot : bots) {
            if (checkBotInRange(me.getX(), bot.getX(), extraBorder)) { // bottom or top
                if (Math.abs((me.getY()+Bot.RADIUS) - (bot.getY()+Bot.RADIUS)) < minDistX) { // check if distance is smallest
                    minDistX = Math.abs((me.getY()+Bot.RADIUS) - (bot.getY()+Bot.RADIUS)); // set to distance
                    closestX = bot; // set to closest bot
                }
            } else if (checkBotInRange(me.getY(), bot.getY(), extraBorder)) { // left or right
                if (Math.abs((me.getX()+Bot.RADIUS) - (bot.getX()+Bot.RADIUS)) < minDistY) {
                    minDistY = Math.abs((me.getX()+Bot.RADIUS) - (bot.getX()+Bot.RADIUS)); // set to distance
                    closestY = bot; // set to closest bot
                }
            }
        }
        if (Math.min(minDistX, minDistY) == 700) return null; // if no bots in range, or any other conditions unsatisified, then return
        return (minDistX < minDistY ? closestX : closestY); // return bot with closest distance
    }
    /**
     * checks if bot is in range, that is directly left, right, up or down of the bot
     * @param me double, first value to check with
     * @param bot double, second value to check if in range 
     * @param extraBorder int, an added border to include in the range of checking
     * @return boolean, true if in range of bot
     */
    public boolean checkBotInRange(double me, double bot, int extraBorder){
        return me+Bot.RADIUS > bot - extraBorder && me+Bot.RADIUS < (bot + Bot.RADIUS * 2 + extraBorder); // checks if in the range to shoot. helps with repitition.
    }
    /**
     * figures out which direction to shoot in.
     * @param bot A BotInfo object with all publicly available info about bot to get direction against
     * @param me A BotInfo object with all publicly available info about this Bot
     * @param delay int, the delay (in ticks) to run the shooting
     * @return action to shoot in a certain direction
     * @return a failsafe value of 26 in the case delay is true
     */
    public int getShootDirection(BotInfo bot, BotInfo me, int delay) {
        if(counter % delay ==0){
            // top, bottom
            if (checkBotInRange(me.getX(), bot.getX(), 20)) return bot.getY() > (me.getY() + Bot.RADIUS) ? BattleBotArena.FIREDOWN : BattleBotArena.FIREUP;
            // right, left
            return bot.getX() > (me.getX() + Bot.RADIUS * 2) ? BattleBotArena.FIRERIGHT : BattleBotArena.FIRELEFT;
        } return 26;
    }
    @Override
    public void draw(Graphics g, int x, int y) {
        g.drawImage(image, x, y, Bot.RADIUS * 2, Bot.RADIUS * 2, null);
    }
    @Override
    public String getName() {
        return "Aravi";
    }
    @Override
    public String getTeamName() {
        return "number 1";
    }
    @Override
    public String outgoingMessage() {
        return "Nah Id win";
    }
    @Override
    public void incomingMessage(int botNum, String msg) {
    }
    @Override
    public String[] imageNames() {
        String[] images = {"gojo.jpg"};
        return images;
    }
    @Override
    public void loadedImages(Image[] images) {
        image = images[0];
    }
}
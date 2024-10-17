package bots;

import java.awt.Graphics;
import java.awt.Image;

import arena.BotInfo;
import arena.Bullet;
import arena.BattleBotArena;

public class SyedBot extends Bot {

    // Frame counter to track when to shoot
    private int frameCounter = 0;
    private static final double SAFE_DISTANCE = 150; // Safe distance to maintain from other bots
    private static final double BULLET_DODGE_DISTANCE = 60; // Distance to dodge bullets
    private Image samBotImage; // Image for SamBot

    @Override
    public void newRound() {
        // Reset frame counter at the start of a new round
        frameCounter = 0;
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        BotHelper helper = new BotHelper(); // helper to calculate distances
        int move = BattleBotArena.STAY; // default move
        int fireDirection = BattleBotArena.STAY; // default fire direction
        BotInfo closestBot = null;
        Bullet closestBullet = null;

        // Detect if my bot is stuck at the edge or corner and force movement so it leaves the spot
        if (me.getX() <= BattleBotArena.LEFT_EDGE + RADIUS) {
            move = BattleBotArena.RIGHT; // Move right if stuck on the left edge
        } else if (me.getX() >= BattleBotArena.RIGHT_EDGE - RADIUS) {
            move = BattleBotArena.LEFT; // Move left if stuck on the right edge
        } else if (me.getY() <= BattleBotArena.TOP_EDGE + RADIUS) {
            move = BattleBotArena.DOWN; // Move down if stuck on the top edge
        } else if (me.getY() >= BattleBotArena.BOTTOM_EDGE - RADIUS) {
            move = BattleBotArena.UP; // Move up if stuck on the bottom edge
        }

        // Find the closest live bot and move toward it
        if (liveBots != null && liveBots.length > 0) {
            closestBot = helper.findClosest(me, liveBots);
            if (closestBot != null) {
                double distanceToBot = helper.calcDistance(me.getX(), me.getY(), closestBot.getX(), closestBot.getY());

                // Move towards the closest bot to engage
                if (distanceToBot > SAFE_DISTANCE) {
                    if (Math.abs(closestBot.getX() - me.getX()) > Math.abs(closestBot.getY() - me.getY())) {
                        move = (closestBot.getX() > me.getX()) ? BattleBotArena.RIGHT : BattleBotArena.LEFT;
                    } else {
                        move = (closestBot.getY() > me.getY()) ? BattleBotArena.DOWN : BattleBotArena.UP;
                    }
                }
            }
        }

        // Find the closest bullet and dodge if necessary
        if (bullets != null && bullets.length > 0) {
            closestBullet = helper.findClosest(me, bullets);
            if (closestBullet != null) {
                double bulletDistance = helper.calcDistance(me.getX(), me.getY(), closestBullet.getX(), closestBullet.getY());

                // Dodge if a bullet is within dodging distance and heading toward my bot
                if (bulletDistance < BULLET_DODGE_DISTANCE) {
                    if (closestBullet.getY() < me.getY() && closestBullet.getYSpeed() > 0 || closestBullet.getY() > me.getY() && closestBullet.getYSpeed() < 0) {
                        move = (me.getX() < BattleBotArena.RIGHT_EDGE / 2) ? BattleBotArena.RIGHT : BattleBotArena.LEFT;
                    } else if (closestBullet.getX() < me.getX() && closestBullet.getXSpeed() > 0 || closestBullet.getX() > me.getX() && closestBullet.getXSpeed() < 0) {
                        move = (me.getY() < BattleBotArena.BOTTOM_EDGE / 2) ? BattleBotArena.DOWN : BattleBotArena.UP;
                    }
                    return move; // Dodging takes priority
                }
            }
        }

        // fire at the closest bot
        if (closestBot != null && shotOK) {
            // Aim and shoot at the closest bot
            if (Math.abs(closestBot.getX() - me.getX()) > Math.abs(closestBot.getY() - me.getY())) {
                fireDirection = (closestBot.getX() > me.getX()) ? BattleBotArena.FIRERIGHT : BattleBotArena.FIRELEFT;
            } else {
                fireDirection = (closestBot.getY() > me.getY()) ? BattleBotArena.FIREDOWN : BattleBotArena.FIREUP;
            }

            // Fire every 30 frames
            if (frameCounter % 30 == 0) {
                return fireDirection;
            }
        }

        frameCounter++; // increase the frame counter for firing
        return move; // Return the chosen move
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        //sambot image
        if (samBotImage != null) {
            g.drawImage(samBotImage, x, y, RADIUS * 2, RADIUS * 2, null);
        }
    }

    @Override
    public String getName() {
        return "SYEDBOT";
    }

    @Override
    public String getTeamName() {
        return "TeamSyed";
    }

    @Override
    public String outgoingMessage() {
       return"";
    }

    @Override
    public void incomingMessage(int botNum, String msg) {
        
    }

    @Override
    public String[] imageNames() {
        // Load SamBot image
        return new String[]{"SamBot.png"};
    }

    @Override
    public void loadedImages(Image[] images) {
        if (images != null && images.length > 0) {
            samBotImage = images[0];
        }
    }
}

package bots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Random;
import arena.BotInfo;
import arena.Bullet;
import arena.BattleBotArena;

/**
 * The SaremBot class is a custom bot for the BattleBots Arena. 
 * It implements basic behaviors like moving, shooting, dodging bullets, and handling messages.
 * 
 * @author Your Name
 */
public class SaremBot extends Bot {

    // Bot image (can add your own images later)
    Image current, up, down, right, left;

    // Name of the bot
    private String name = null;

    // Counter for movement timing
    private int moveCount = 99;

    // The current move to perform
    private int move = BattleBotArena.UP;

    // Next message to send
    private String nextMessage = null;

    // Random object to randomize movement
    private Random random = new Random();

    // Radius within which the bot will try to dodge bullets
    private final double dodgeRadius = 100;

    @Override
    public String[] imageNames() {
        // Provide image names for loading if any
        return new String[]{"goofy-face.png", "down.png", "right.png", "left.png"};
    }

    @Override
    public void loadedImages(Image[] images) {
        // Store the loaded images
        if (images != null) {
            if (images.length > 0) up = images[0];
            if (images.length > 1) down = images[1];
            if (images.length > 2) right = images[2];
            if (images.length > 3) left = images[3];
            current = up; // Default image
        }
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        // Increment move counter
        moveCount++;

        // Check if any bullets are nearby
        for (Bullet bullet : bullets) {
            double distance = Math.sqrt(Math.pow(bullet.getX() - me.getX(), 2) + Math.pow(bullet.getY() - me.getY(), 2));

            // If a bullet is within the dodge radius
            if (distance < dodgeRadius) {
                // Determine the bullet's direction
                if (bullet.getXSpeed() != 0) {
                    // Bullet is moving horizontally, so dodge vertically
                    if (bullet.getX() > me.getX()) {
                        move = BattleBotArena.UP; // Dodge up if the bullet is coming from the right
                        current = up;
                    } else {
                        move = BattleBotArena.DOWN; // Dodge down if the bullet is coming from the left
                        current = down;
                    }
                } else if (bullet.getYSpeed() != 0) {
                    // Bullet is moving vertically, so dodge horizontally
                    if (bullet.getY() > me.getY()) {
                        move = BattleBotArena.LEFT; // Dodge left if the bullet is coming from below
                        current = left;
                    } else {
                        move = BattleBotArena.RIGHT; // Dodge right if the bullet is coming from above
                        current = right;
                    }
                }
                return move; // Immediately dodge if a bullet is nearby
            }
        }

        // Time to choose a new move if no bullets are close
        if (moveCount >= 30 + (int)(Math.random() * 60)) {
            moveCount = 0;
            int choice = (int)(Math.random() * 8); // Random choice for movement or firing

            switch (choice) {
                case 0:
                    move = BattleBotArena.UP;
                    current = up;
                    break;
                case 1:
                    move = BattleBotArena.DOWN;
                    current = down;
                    break;
                case 2:
                    move = BattleBotArena.LEFT;
                    current = left;
                    break;
                case 3:
                    move = BattleBotArena.RIGHT;
                    current = right;
                    break;
                case 4:
                    move = BattleBotArena.FIREUP;
                    moveCount = 99; // Ensure a new move is chosen after firing
                    break;
                case 5:
                    move = BattleBotArena.FIREDOWN;
                    moveCount = 99;
                    break;
                case 6:
                    move = BattleBotArena.FIRELEFT;
                    moveCount = 99;
                    break;
                case 7:
                    move = BattleBotArena.FIRERIGHT;
                    moveCount = 99;
                    break;
            }
        }

        // Return the chosen move
        return move;
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        if (current != null) {
            g.drawImage(current, x, y, Bot.RADIUS * 2, Bot.RADIUS * 2, null);
        } else {
            g.setColor(Color.blue);
            g.fillOval(x, y, Bot.RADIUS * 2, Bot.RADIUS * 2);
        }
    }

    @Override
    public void newRound() {
        // Called at the start of each round to initialize variables if needed
    }

    @Override
    public String getName() {
        if (name == null) {
            name = "Sarem" + (botNumber < 10 ? "0" : "") + botNumber;
        }
        return name;
    }

    @Override
    public String getTeamName() {
        return "SaremTeam";
    }

    @Override
    public String outgoingMessage() {
        // Returns any message the bot wants to send
        return nextMessage;
    }

    @Override
    public void incomingMessage(int botNum, String msg) {
        // Handle incoming messages
    }
}



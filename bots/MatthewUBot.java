package bots;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class MatthewUBot extends Bot {
    private Image up, down, right, left, current;
    private Random random = new Random();
    private int patrolLimit = 800; 
    private int arenaHeight = 600; 
    private String lastMessage = ""; 
    private int currentMove = BattleBotArena.STAY; 
    private long lastMoveTime = System.currentTimeMillis(); 
    private int movementDuration = 50; 
    private int score = 0; // Score counter
    private Set<Integer> killedBots = new HashSet<>(); 

    public MatthewUBot() {
        loadImages(); // Load images when the bot is created
    }

    private void loadImages() {
        try {
            up = ImageIO.read(new File("images/pikachu_up.png"));
            down = ImageIO.read(new File("images/pikachu_down.png"));
            right = ImageIO.read(new File("images/pikachu_right.png"));
            left = ImageIO.read(new File("images/pikachu_left.png"));
            current = up; 
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }

    @Override
    public void newRound() {
        score = 0; 
        killedBots.clear(); 
        shout("I'm ready for battle!"); 
    }

    private void shout(String message) {
        lastMessage = message; // Store the message to send
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        long currentTime = System.currentTimeMillis();

        // Bullet avoidance
        for (Bullet bullet : bullets) {
            if (isBulletHeadingTowardsMe(me, bullet)) {
                if (bullet.getY() < me.getY()) {
                    currentMove = BattleBotArena.DOWN; 
                    current = down;
                } else {
                    currentMove = BattleBotArena.UP;
                    current = up;
                }
                return currentMove; // Move to avoid the bullet
            }
        }

        // Check if it's time to change direction
        if (currentTime - lastMoveTime >= movementDuration) {
            int randomDirection = random.nextInt(5);
            switch (randomDirection) {
                case 0:
                    currentMove = BattleBotArena.STAY; 
                    break;
                case 1:
                    currentMove = BattleBotArena.UP; 
                    current = up;
                    break;
                case 2:
                    currentMove = BattleBotArena.DOWN; 
                    current = down;
                    break;
                case 3:
                    currentMove = BattleBotArena.LEFT; 
                    current = left;
                    break;
                case 4:
                    currentMove = BattleBotArena.RIGHT; 
                    current = right;
                    break;
            }
            lastMoveTime = currentTime; 
        }

        // Wall avoidance
        if (me.getX() < 50) {
            currentMove = BattleBotArena.RIGHT; 
        } else if (me.getX() > patrolLimit - 50) {
            currentMove = BattleBotArena.LEFT; 
        }
        if (me.getY() < 50) {
            currentMove = BattleBotArena.DOWN; 
        } else if (me.getY() > arenaHeight - 50) {
            currentMove = BattleBotArena.UP; 
        }

        // Update score for new dead bots
        for (BotInfo deadBot : deadBots) {
            if (!killedBots.contains(deadBot.getBotNumber())) {
                killedBots.add(deadBot.getBotNumber());
                score++;
                shout("I got one! Score: " + score);
            }
        }

        // Improved shooting logic: target nearest bot
        BotInfo nearestBot = findNearestBot(me, liveBots);
        if (shotOK && nearestBot != null) {
            if (Math.abs(nearestBot.getX() - me.getX()) < 10) {
                if (nearestBot.getY() < me.getY()) {
                    return BattleBotArena.FIREUP;
                } else {
                    return BattleBotArena.FIREDOWN;
                }
            } else if (Math.abs(nearestBot.getY() - me.getY()) < 10) {
                if (nearestBot.getX() < me.getX()) {
                    return BattleBotArena.FIRELEFT;
                } else {
                    return BattleBotArena.FIRERIGHT;
                }
            }
        }

        return currentMove;
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        g.drawImage(current, x, y, null);
    }

    @Override
    public String getName() {
        return "MatthewBot"; 
    }

    @Override
    public String getTeamName() {
        return "Team Name"; 
    }

    @Override
    public String outgoingMessage() {
        return lastMessage;
    }

    @Override
    public void incomingMessage(int botNum, String msg) {
        // Handle incoming messages, e.g., "Target spotted at (x, y)"
    }

    @Override
    public String[] imageNames() {
        return new String[]{"pikachu_up.png", "pikachu_down.png", "pikachu_left.png", "pikachu_right.png"};
    }

    @Override
    public void loadedImages(Image[] images) {
        // Unused
    }

    private boolean isBulletHeadingTowardsMe(BotInfo me, Bullet bullet) {
        // Simple bullet avoidance logic
        return (bullet.getX() > me.getX() - 20 && bullet.getX() < me.getX() + 20) ||
               (bullet.getY() > me.getY() - 20 && bullet.getY() < me.getY() + 20);
    }

    private BotInfo findNearestBot(BotInfo me, BotInfo[] liveBots) {
        BotInfo nearest = null;
        double minDistance = Double.MAX_VALUE;
        for (BotInfo bot : liveBots) {
            double distance = Math.hypot(bot.getX() - me.getX(), bot.getY() - me.getY());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = bot;
            }
        }
        return nearest;
    }

    private boolean nearbyBots(BotInfo me, BotInfo[] liveBots) {
        for (BotInfo bot : liveBots) {
            if (Math.hypot(bot.getX() - me.getX(), bot.getY() - me.getY()) < 100) {
                return true;
            }
        }
        return false;
    }
}

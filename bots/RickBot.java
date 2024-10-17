package bots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

import java.util.*;

class Node implements Comparable<Node> {
    int x, y;
    double gCost, hCost;
    Node parent;

    Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.gCost = 0;
        this.hCost = 0;
        this.parent = null;
    }

    double getFCost() {
        return gCost + hCost;
    }

    public int compareTo(Node other) {
        return Double.compare(this.getFCost(), other.getFCost());
    }
}

/**
 * The bot is written by Rick Liu.
 *
 * @author Rick Liu
 * @version 1.3 (Oct 14, 2024)
 */
public class RickBot extends Bot {

	/**
	 * My name
	 */
	String name;
	/**
	 * My next message or null if nothing to say
	 */
	String nextMessage = null;
	/**
	 * Array of happy drone messages
	 */
	private String[] messages = {"You're all done.", "I love Mr. Brooks."};
	/**
	 * Image for drawing
	 */
	Image up, down, left, right, current;
	/**
	 * Current move
	 */
	private int move = BattleBotArena.UP;
	/**
	 * My last location - used for detecting when I am stuck
	 */
	private double x, y;
    /**
     * The width of arena
     */
    private final int ARENA_WIDTH = BattleBotArena.RIGHT_EDGE-BattleBotArena.LEFT_EDGE;
    /**
     * The height of arena
     */
    private final int ARENA_HEIGHT = BattleBotArena.BOTTOM_EDGE-BattleBotArena.TOP_EDGE;
    /**
     * Width of detection
     */
    private final int LENGTH_OF_DETECTION = ARENA_WIDTH/5;
    /**
     * Fake Infinite Number
     */
    private final int INF = 0x3f3f3f3f;
    /**
     * Avoid consecutive shots
     */
    private final int MAX_SHOOT_CNT = 50;
    private int shootCnt = MAX_SHOOT_CNT;
    /**
     * threaten range
     */
    private final int THREATEN_RANGE = 3;
    private boolean[][] grid = new boolean[100][70];
    // corresponding to BattleBotArena's UP, DOWN, LEFT, RIGHT
    private final int[] dx = new int[]{0,0,-1,1};
    private final int[] dy = new int[]{-1,1,0,0};
    private BotHelper helper = new BotHelper();
    private List<Node> myPath;
    private String targetName;
    private int spawnTime = 0;
	/**
	 * Draw the current Drone image
	 */
	public void draw(Graphics g, int x, int y) {
		g.drawImage(current, x, y, Bot.RADIUS*2, Bot.RADIUS*2, null);
	}
    
    public void initGrid(BotInfo me, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        for(int i = 0; i < grid.length; i++)
            for(int j = 0; j < grid[0].length; j++)
                grid[i][j] = false;
        // bots
        BotInfo[] allBots = new BotInfo[liveBots.length+deadBots.length];
        System.arraycopy(liveBots, 0, allBots, 0, liveBots.length);
        System.arraycopy(deadBots, 0, allBots, liveBots.length, deadBots.length);
        for(BotInfo bot : allBots) {
            int nx = (int)(bot.getX()/10);
            int ny = (int)(bot.getY()/10);
            for(int i = nx-1; i <= nx+1; i++) {
                for(int j = ny-1; j <= ny+1; j++) {
                    if(i < 0 || i >= grid.length || j < 0 || j >= grid[0].length) continue;
                    grid[i][j] = true;
                }
            }
        }
        int mx = (int)(me.getX()/10);
        int my = (int)(me.getY()/10);
        for(int i = mx-1; i <= mx+1; i++) {
            for(int j = my-1; j <= my+1; j++) {
                if(i < 0 || i >= grid.length || j < 0 || j >= grid[0].length) continue;
                grid[i][j] = false;
            }
        }
        // grid[mx][my] = true;
        // bullets
        for(Bullet newBullet : bullets) {
            int nx = (int)(newBullet.getX()/10);
            int ny = (int)(newBullet.getY()/10);
            // threatening area has a length of 3
            for(int i = 0; i < 3; i++) {
                if(nx < 0 || nx >= grid.length || ny < 0 || ny >= grid[0].length) continue;
                grid[nx][ny] = true;
                if(newBullet.getXSpeed() > 0) nx++;
                else if(newBullet.getXSpeed() < 0) nx--;
                else if(newBullet.getYSpeed() > 0) ny++;
                else ny--;
            }
        }
    }
    
    public List<Node> findFinalPath(Node nd) {
        List<Node> path = new ArrayList<>();
        while(nd != null) {
            path.add(nd);
            nd = nd.parent;
        }
        // change to start to end
        Collections.reverse(path);
        if(!path.isEmpty())
            path.remove(0);
        // System.out.println("list size:" + path.size());
        return path;
    }
    // returns only the grid location, needs to be converted to actual location
    public List<Node> AStarFindPath(int startX, int startY, int endX, int endY) {
        int loopTime = 0;
        PriorityQueue<Node> pq = new PriorityQueue<>();
        // initialize
        boolean[][] vis = new boolean[grid.length][grid[0].length];
        Node startNode = new Node(startX, startY);
        pq.add(startNode);
        // for(int i = 0; i < grid.length; i++) {
        //     for(int j = 0; j < grid[0].length; j++) {
        //         if(i == startX && j == startY)
        //             System.out.print("2 ");
        //         if(i == endX && j == endY)
        //             System.out.print("3 ");
        //         System.out.print(grid[i][j]?1+" ":"  ");
        //     }
        //     System.out.println();
        // }
        // System.out.println("Start: "+startX+" "+startY);
        // System.out.println("End: "+endX+" "+endY);
        
        while(!pq.isEmpty()) {
            loopTime++;
            if(loopTime >= 10000) {
                List<Node> returnList = new ArrayList<Node>();
                return returnList;
            }
            // System.out.println("loop against "+targetName);
            // System.out.println("new pq");
            // for(Node a : pq) {
            //     System.out.println(a.x+" "+a.y);
            // }
            Node cur = pq.poll();
            // if get to target
            // TODO: Find correct ending logic
            if((Math.abs(cur.x-endX) <= 3 && cur.y == endY) || (Math.abs(cur.y-endY) <= 3 && cur.x == endX)) {
                return findFinalPath(cur);
            }
            vis[cur.x][cur.y] = true;
            // for(int i = 0; i < grid.length; i++) {
            //     for(int j = 0; j < grid[0].length; j++) {
            //         System.out.print(vis[i][j]? 1:0);
            //     }
            //     System.out.println();
            // }
            // traverse 4 directions
            for(int i = 0; i < 4; i++) {
                int nx = cur.x+dx[i];
                int ny = cur.y+dy[i];
                // in grid, not visited, not a obstacle
                boolean hasObstacle = false, outOfBound = false;
                for(int ii = nx-1; ii <= nx+1; ii++)
                    for(int jj = ny-1; jj <= ny+1; jj++) {
                        if(ii < 0 || ii >= grid.length || jj < 0 || jj >= grid[0].length) {
                            outOfBound = true;
                            continue;
                        }
                        if(grid[ii][jj])
                            hasObstacle = true;
                    }
                // if(nx >= 0 && nx < grid.length && ny >=0 && ny < grid[0].length && grid[nx][ny]) hasObstacle = true;
                // if(nx < 0 || nx >= grid.length || ny < 0 || ny >= grid[0].length) outOfBound = true;

                // System.out.println(hasObstacle);
                // System.out.println("nxny: "+nx+" "+ny+" "+outOfBound+" "+vis[nx][ny]+" "+hasObstacle);
                if(!outOfBound && !vis[nx][ny] && !hasObstacle) {
                    Node nxt = new Node(nx,ny);
                    double newGCost = cur.gCost+1;
                    if(!pq.contains(nxt) || newGCost < nxt.gCost) {
                        nxt.gCost = newGCost;
                        nxt.hCost = helper.manhattanDist(nx, ny, endX, endY);
                        nxt.parent = cur;
                        if(!pq.contains(nxt)) pq.add(nxt);
                    }
                }
            }
        }
        List<Node> returnList = new ArrayList<Node>();
        return returnList;
    }
	
    public String findEnemy(BotInfo me, BotInfo[] liveBots) {
        BotInfo[] newBotInfo = Arrays.copyOf(liveBots, liveBots.length);
        BotInfo targetBot = helper.findClosest(me, newBotInfo);
        while(true) {
            double d = Math.sqrt(Math.pow(me.getX()-targetBot.getX(),2)+Math.pow(me.getY()-targetBot.getY(),2));
            if (d < Bot.RADIUS*3) {
                // find next closest one
                // TODO: Possible error
                BotInfo[] newBotInfo2 = new BotInfo[newBotInfo.length-1];
                int index = 0;
                for(BotInfo bot : newBotInfo) {
                    if(Math.abs(bot.getX()-targetBot.getX()) < 1e-4 && Math.abs(bot.getY()-targetBot.getY()) < 1e-4) {
                        continue;
                    }
                    newBotInfo2[index] = bot;
                    index++;
                }
                newBotInfo = Arrays.copyOf(newBotInfo2, newBotInfo2.length);
                targetBot = helper.findClosest(me, newBotInfo);
            } else {
                break;
            }
        }
        return targetBot.getName();
    }
    
	/**
	 * Move in squares and fire every now and then.
	 */
	public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        spawnTime++;
        try {
            if(shootCnt != 0) shootCnt--;
            // System.out.println(me.getLastMove());
            boolean hasToMove = false;
            String threatenDirection = "";
            // 2 blocks for necessary turn to avoid bullets, check length/width
            int len = (int)Math.ceil(1.0*LENGTH_OF_DETECTION/(Bot.RADIUS*2)); // divide to bot's diameter's square blocks.
            int leftBound = (int)(me.getX()-Bot.RADIUS-(len/2)*(Bot.RADIUS*2));
            int topBound = (int)(me.getY()-Bot.RADIUS-(len/2)*(Bot.RADIUS*2));
            if(len%2 == 0) len++;
            boolean[][] detection = new boolean[len][len];
            // initialize to false
            for(int i = 0; i < len; i++)
                Arrays.fill(detection[i], false);
            for(Bullet bullet : bullets) {
                // not concerning (out of threatening area)
                if(bullet.getX() < me.getX()-len/2*(Bot.RADIUS*2) || 
                    bullet.getX() > me.getX()+len/2*(Bot.RADIUS*2) || 
                    bullet.getY() < me.getY()-len/2*(Bot.RADIUS*2) || 
                    bullet.getY() > me.getY()+len/2*(Bot.RADIUS*2))
                    continue;
                // not concerning (in threatening area but opposite direction)
                // only same horizontal/vertical line are left
                if(bullet.getX() < me.getX()-Bot.RADIUS && bullet.getXSpeed() <= 0) continue;
                if(bullet.getX() > me.getX()+Bot.RADIUS && bullet.getXSpeed() >= 0) continue;
                if(bullet.getY() < me.getY()-Bot.RADIUS && bullet.getYSpeed() <= 0) continue;
                if(bullet.getY() > me.getY()+Bot.RADIUS && bullet.getYSpeed() >= 0) continue;
                // convert to block no.
                int blockX = (int)(bullet.getX()-leftBound)/(Bot.RADIUS*2);
                int blockY = (int)(bullet.getY()-topBound)/(Bot.RADIUS*2);
                if(blockX < 0 || blockX >= len || blockY < 0 || blockY >= len) continue;
                // make notice on potential dangers
                detection[blockX][blockY] = true;
            }
            // horizontal
            int closestDistH = INF;
            int closestLocH = -1;
            for(int i = 0; i < len; i++) {
                int j = len/2;
                if(detection[i][j]) {
                    if(Math.abs(len/2-i) < closestDistH) {
                        closestDistH = Math.abs(len/2-i);
                        closestLocH = i;
                    }
                }
            }
            if(closestDistH <= 2) {
                hasToMove = true;
                if(closestLocH < len/2) {
                    threatenDirection = "left";
                } else {
                    threatenDirection = "right";
                }
            }
            // vertical
            int closestDistV = INF;
            int closestLocV = -1;
            for(int j = 0; j < len; j++) {
                int i = len/2;
                if(detection[i][j]) {
                    if(Math.abs(len/2-j) < closestDistV) {
                        closestDistV = Math.abs(len/2-j);
                        closestLocV = j;
                    }
                }
            }
            if(closestDistV <= THREATEN_RANGE) {
                hasToMove = true;
                if(closestLocV < len/2) {
                    threatenDirection = "up";
                } else {
                    threatenDirection = "down";
                }
            }
            // process movement
            if(hasToMove) {
                if(threatenDirection.equals("left") || threatenDirection.equals("right")) {
                    if(closestLocV < len/2) return BattleBotArena.DOWN;
                    else return BattleBotArena.UP;
                }
                else {
                    if(closestLocH < len/2) return BattleBotArena.RIGHT;
                    else return BattleBotArena.LEFT;
                }
            }
            // shoot if okay, else move
            boolean shoot = true;
            if(shotOK && shootCnt == 0) {
                shootCnt = MAX_SHOOT_CNT;
                BotHelper newHelper = new BotHelper();
                BotInfo targetBot = newHelper.findClosest(me, liveBots);
                double xDifference = targetBot.getX()-me.getX();
                double yDifference = targetBot.getY()-me.getY();
                if(xDifference > 20 && yDifference > 20) shoot = false;
                if(shoot) {
                    if(xDifference > 0) {
                        if(Math.abs(yDifference) > Math.abs(xDifference)) {
                            // shoot vertically
                            if(yDifference < 0) return BattleBotArena.FIREUP;
                            else return BattleBotArena.FIREDOWN;
                        } else {
                            // shoot horizontally
                            return BattleBotArena.FIRERIGHT;
                        }
                    } else {
                        if(Math.abs(yDifference) > Math.abs(xDifference)) {
                            // shoot vertically
                            if(yDifference < 0) return BattleBotArena.FIREUP;
                            else return BattleBotArena.FIREDOWN;
                        } else {
                            // shoot horizontally
                            return BattleBotArena.FIRELEFT;
                        }
                    }
                }
            }
            // else, find path to go for enemy
            initGrid(me, liveBots, deadBots, bullets);
            // find target
            int targetX = -1, targetY = -1;
            while(true) {
                if(targetName == null) {
                    targetName = findEnemy(me, liveBots);
                }
                for(BotInfo bot : liveBots) {
                    if(bot.getName().equals(targetName)) {
                        targetX = (int)(bot.getX()/10);
                        targetY = (int)(bot.getY()/10);
                        break;
                    }
                }
                if(targetX != -1) break;
                targetName = null;
            }
            // find path
            // TODO: find out if needed to update path every frame
            int mx = (int)(me.getX()/10);
            int my = (int)(me.getY()/10);
            // shoot if close
            if(mx == targetX || my == targetY) {
                if(mx == targetX) {
                    if(my < targetY) return BattleBotArena.FIREDOWN;
                    return BattleBotArena.FIREUP;
                } else {
                    if(mx < targetX) return BattleBotArena.FIRERIGHT;
                    return BattleBotArena.FIRELEFT;
                }
            }
            myPath = AStarFindPath(mx,my,targetX,targetY);
            // System.out.println("new path targeting "+targetName);
            // for (Node node : myPath) {
            //     System.out.println("Step: (" + node.x + ", " + node.y + ")");
            // }
            if(!myPath.isEmpty()) {
                int nxtX = myPath.get(0).x;
                int nxtY = myPath.get(0).y;
                if(mx < nxtX) return BattleBotArena.RIGHT;
                else if(mx > nxtX) return BattleBotArena.LEFT;
                else if(my < nxtY) return BattleBotArena.DOWN;
                else if(my > nxtY) return BattleBotArena.UP;
                else return BattleBotArena.SEND_MESSAGE;
            }
            // System.out.println("NOTHING TO DO!");
            return BattleBotArena.SEND_MESSAGE;
        } catch(Exception e) {
            // hide errors
            StackTraceElement[] stackTrace = e.getStackTrace();
            System.out.println("An error occurred: " + e.getMessage());
            if (stackTrace.length > 0) {
                StackTraceElement element = stackTrace[0];
                System.out.println("Error occurred in class: " + element.getClassName());
                System.out.println("Error occurred in method: " + element.getMethodName());
                System.out.println("Error occurred in file: " + element.getFileName());
                System.out.println("Error occurred at line number: " + element.getLineNumber());
            }
            System.out.println();
            return BattleBotArena.SEND_MESSAGE;
        }
	}

	/**
	 * Construct and return my name
	 */
	public String getName()
	{
		return "BBBIUUUU";
	}

	/**
	 * Team Arena!
	 */
	public String getTeamName() {
		return "BRUH";
	}

	/**
	 * Pick a random starting direction
	 */
	public void newRound() {
		spawnTime = 0;
        myPath = null;
        targetName = null;
	}

	/**
	 * Image names
	 */
	public String[] imageNames()
	{
		// String[] images = {"pikachu_up.png","pikachu_down.png","pikachu_left.png","pikachu_right.png"};
        String[] images = {"gpt.jpg", "gpt.jpg", "gpt.jpg", "gpt.jpg"};
 		return images;
	}

	/**
	 * Store the loaded images
	 */
	public void loadedImages(Image[] images)
	{
		if (images != null)
		{
			current = up = images[0];
			down = images[1];
			left = images[2];
			right = images[3];
		}
	}

	/**
	 * Send my next message and clear out my message buffer
	 */
	public String outgoingMessage()
	{
		int index = (int)(Math.random()*messages.length);
		return messages[index];
	}

	/**
	 * Required abstract method
	 */
	public void incomingMessage(int botNum, String msg)
	{

	}

}
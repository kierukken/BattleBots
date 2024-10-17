package bots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class AlexanderBot extends Bot {

    ArrayList<Pos2d> myList = new ArrayList<>();
    ArrayList<Pos2d> closedList = new ArrayList<>();
    ArrayList<Pos2d> bulletList = new ArrayList<>();
    Pos2d goalPos = new Pos2d(400, 400);

    Pos2d nextPos;
    private int move;
    private int shootTimer = 0;

    private boolean[] safeX = new boolean[BattleBotArena.RIGHT_EDGE];
    private boolean[] safeY = new boolean[BattleBotArena.BOTTOM_EDGE];

    private int drawTimer;
    private int shootAngle;

    private boolean isThrowingError;

    @Override
    public void newRound() {
        shootTimer = 0;
        drawTimer = 0;
        isThrowingError = false;
    }

    private Pos2d getMiddle(BotInfo bot) {
        return new Pos2d(bot.getX() + Bot.RADIUS / 2.0, bot.getY() + Bot.RADIUS / 2.0);
    }

    private BotInfo getClosest(Pos2d me, BotInfo[] bots) {
        double minDist = 9999;
        int index = 0;

        for (int i = 0; i < bots.length; i++) {
            if (bots[i].getName() == "RickyBot" && bots.length > 1) continue;

            Pos2d botPos = getMiddle(bots[i]);

            double distance = Math.abs(me.getX() - botPos.getX()) + Math.abs(me.getY() - botPos.getY());
            if (distance < minDist) {
                index = i;
                minDist = distance;
            }
        }

        return bots[index];
    }

    private boolean botInLine(Pos2d mePos, Pos2d botPos) {
        if (mePos.getX() + Bot.RADIUS / 1.90 > botPos.getX() - Bot.RADIUS / 1.90 && mePos.getX() - Bot.RADIUS / 1.90 < botPos.getX() + Bot.RADIUS / 1.90)
            return true;
        
        if (mePos.getY() + Bot.RADIUS / 1.90 > botPos.getY() - Bot.RADIUS / 1.90 && mePos.getY() - Bot.RADIUS / 1.90 < botPos.getY() + Bot.RADIUS / 1.90)
            return true;

        return false;
    }

    private boolean checkIfSafe(Pos2d absMePos) {
        for (int i = -(int) (Bot.RADIUS * 0.9); i <= Bot.RADIUS * 2.9; i++) {
            int tx = (int) absMePos.getX() + i;

            if (tx >= 0 && tx < safeX.length)
                if (!safeX[tx]) return false;

            int ty = (int) absMePos.getY() + i;

            if (ty >= 0 && ty < safeY.length)
                if (!safeY[ty]) return false;
        }

        return true;
    }

    private BotInfo me;

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        try {
            this.me = me;

            move = BattleBotArena.STAY;

            shootTimer--;

            Pos2d absPos = new Pos2d(me.getX(), me.getY());

            Pos2d mePos = getMiddle(me);
            BotInfo closestEnemy = getClosest(mePos, liveBots);
            Pos2d closestEnemyPos = getMiddle(closestEnemy);

            double deltaX = mePos.getX() - closestEnemyPos.getX();
            double deltaY = mePos.getY() - closestEnemyPos.getY();

            if (shotOK && Math.abs(shootTimer) % 3 == 0 && botInLine(mePos, closestEnemyPos) && Math.min(Math.abs(deltaX), Math.abs(deltaY)) <= Bot.RADIUS * 4.0 && Math.max(Math.abs(deltaX), Math.abs(deltaY)) <= Bot.RADIUS * 4.5) {
                // SHOOT
                if (Math.abs(deltaY) > Math.abs(deltaX)) {
                    shootTimer = 7;
                    return mePos.getY() > closestEnemyPos.getY() ? BattleBotArena.FIREUP : BattleBotArena.FIREDOWN;
                } else {
                    shootTimer = 7;
                    return mePos.getX() > closestEnemyPos.getX() ? BattleBotArena.FIRELEFT : BattleBotArena.FIRERIGHT;
                }
            }

            Arrays.fill(safeX, true);
            Arrays.fill(safeY, true);

            for (Bullet bullet : bullets) {
                if (bullet.getXSpeed() == 0.0) {
                    if (mePos.getY() - Bot.RADIUS * 1.2 > bullet.getY() && bullet.getYSpeed() < 0) continue;
                    if (mePos.getY() + Bot.RADIUS * 1.2 < bullet.getY() && bullet.getYSpeed() > 0) continue;

                    for (int d = -1; d <= 1; d++)
                        safeX[(int) bullet.getX() + d] = false;
                } else {
                    if (mePos.getX() - Bot.RADIUS * 1.2 > bullet.getX() && bullet.getXSpeed() < 0) continue;
                    if (mePos.getX() + Bot.RADIUS * 1.2 < bullet.getX() && bullet.getXSpeed() > 0) continue;

                    for (int d = -1; d <= 1; d++)
                        safeY[(int) bullet.getY() + d] = false;
                }
            }

            boolean botMoving = closestEnemy.getLastMove() == BattleBotArena.RIGHT || closestEnemy.getLastMove() == BattleBotArena.LEFT || closestEnemy.getLastMove() == BattleBotArena.DOWN || closestEnemy.getLastMove() == BattleBotArena.UP || !closestEnemy.isOverheated();

            deltaX = mePos.getX() - closestEnemyPos.getX();
            deltaY = mePos.getY() - closestEnemyPos.getY();

            if (Math.abs(deltaX) > Math.abs(deltaY))
                goalPos = closestEnemyPos.copy().plus(Math.copySign(botMoving ? 80 : 65, deltaX), 0);
            else
                goalPos = closestEnemyPos.copy().plus(0, Math.copySign(botMoving ? 80 : 65, deltaY));
        
            Pos2d difference = goalPos.copy().minus(absPos);

            nextPos = null;

            if (Math.max(Math.abs(mePos.getX() - goalPos.getX()), Math.abs(mePos.getY() - goalPos.getY())) > BattleBotArena.BOT_SPEED * 3.0) {
                if (Math.atan2(Math.abs(difference.getY()), Math.abs(difference.getX())) < Math.PI / 4.0) {
                    move = difference.getX() > 0 ? BattleBotArena.RIGHT : BattleBotArena.LEFT;

                    nextPos = absPos.copy().plus(Math.copySign(BattleBotArena.BOT_SPEED, difference.getX()), 0);

                    updateList(nextPos, deadBots, bullets);

                    if (!checkIfSafe(nextPos) || anyContain(myList, closedList)) {
                        move = difference.getY() > 0 ? BattleBotArena.DOWN : BattleBotArena.UP;
                    }
                } else {
                    move = difference.getY() > 0 ? BattleBotArena.DOWN : BattleBotArena.UP;

                    nextPos = absPos.copy().plus(0, Math.copySign(BattleBotArena.BOT_SPEED, difference.getY()));

                    updateList(nextPos, deadBots, bullets);

                    if (!checkIfSafe(nextPos) || anyContain(myList, closedList)) {
                        move = difference.getX() > 0 ? BattleBotArena.RIGHT : BattleBotArena.LEFT;
                    }
                }
            };

            updateList(absPos, deadBots, bullets);
            // System.out.println("Lazer: " + !checkIfSafe(absPos) + " Bullets: " + anyContain(myList, bulletList));

            if (!checkIfSafe(absPos) || (nextPos != null && !checkIfSafe(nextPos))) {
                Pos2d bulletPos = findContain(myList, bulletList);

                if (bulletPos == null && nextPos != null) {
                    updateList(nextPos, deadBots, bullets);
                    bulletPos = findContain(myList, bulletList);
                }

                if (bulletPos != null) {
                    // NOT SAFE, DODGE
                    Bullet activeBullet = bulletPos.bullet;

                    deltaX = mePos.getX() - bulletPos.getX();
                    deltaY = mePos.getY() - bulletPos.getY();

                    if (Math.abs(activeBullet.getXSpeed()) < 0.1)
                        return deltaX > 0 ? BattleBotArena.RIGHT : BattleBotArena.LEFT;
                    else
                        return deltaY > 0 ? BattleBotArena.DOWN : BattleBotArena.UP;
                }
            }

            if (move == BattleBotArena.STAY || (shotOK && Math.abs(shootTimer) % 7 == 0 && botInLine(mePos, closestEnemyPos))) {
                if (shotOK && (Math.abs(shootTimer) % 7 == 0 || shootTimer < 0)) {
                    if (botInLine(mePos, closestEnemyPos)) {
                        if (shootTimer <= 0 && (Math.max(Math.abs(deltaX), Math.abs(deltaY)) <= 240 || closestEnemy.isOverheated() || !botMoving)) {
                            // SHOOT
                            if (Math.abs(deltaY) > Math.abs(deltaX)) {
                                shootTimer = 7;
                                move = mePos.getY() > closestEnemyPos.getY() ? BattleBotArena.FIREUP : BattleBotArena.FIREDOWN;
                            } else {
                                shootTimer = 7;
                                move = mePos.getX() > closestEnemyPos.getX() ? BattleBotArena.FIRELEFT : BattleBotArena.FIRERIGHT;
                            }
                        }
                    }
                }
            }
            
            return move;
        } catch (Exception e) {
            isThrowingError = true;

            System.out.println("Error (AlexanderBot): " + e + " Line: " + e.getStackTrace()[0].getLineNumber());

            return BattleBotArena.SEND_MESSAGE;
        }
    }

    private boolean anyContain(ArrayList<Pos2d> list1, ArrayList<Pos2d> list2) {
        return findContain(list1, list2) != null;
    }

    private Pos2d findContain(ArrayList<Pos2d> list1, ArrayList<Pos2d> list2) {
        for (Pos2d pos1 : list1) {
            for (Pos2d pos2 : list2) {
                if (pos1.equals(pos2)) return pos2;
            }
        }

        return null;
    }

    private void updateList(Pos2d me, BotInfo[] graves, Bullet[] bullets) {
        closedList.clear();
        for (BotInfo bot : graves) {
            for (int x = 0; x < Bot.RADIUS * 2; x++) {
                for (int y = 0; y < Bot.RADIUS * 2; y++) {
                    if (x == 0 || x == Bot.RADIUS * 2 - 1 || y == 0 || y == Bot.RADIUS * 2 - 1)
                        closedList.add(new Pos2d(bot.getX() + x, bot.getY() + y));
                }
            }
        }

        myList.clear();
        for (int x = 0; x < Bot.RADIUS * 2; x++) {
            for (int y = 0; y < Bot.RADIUS * 2; y++) {
                if (x <= 2 || x >= Bot.RADIUS * 2 - 3 || y <= 2 || y >= Bot.RADIUS * 2 - 3)
                    myList.add(new Pos2d(me.getX() + x, me.getY() + y));
            }
        }

        bulletList.clear();
        for (Bullet bullet : bullets) {
            if (Math.hypot(me.getX() - bullet.getX(), me.getY() - bullet.getY()) <= Bot.RADIUS * 10) {
                int mx = bullet.getXSpeed() > 0 ? 1 : -1;
                if (bullet.getXSpeed() == 0.0) {
                    mx = 0;
                    if (me.getY() - Bot.RADIUS * 1.2 > bullet.getY() && bullet.getYSpeed() < 0) continue;
                    if (me.getY() + Bot.RADIUS * 1.2 < bullet.getY() && bullet.getYSpeed() > 0) continue;
                }

                int my = bullet.getYSpeed() > 0 ? 1 : -1;
                if (bullet.getYSpeed() == 0.0) {
                    my = 0;
                    if (me.getX() - Bot.RADIUS * 1.2 > bullet.getX() && bullet.getXSpeed() < 0) continue;
                    if (me.getX() + Bot.RADIUS * 1.2 < bullet.getX() && bullet.getXSpeed() > 0) continue;
                }

                for (int d = -5; d <= 5; d++) {
                    for (int i = 0; i < BattleBotArena.BULLET_SPEED * 20; i++) {
                        double dx = d * copySign(bullet.getXSpeed());
                        double dy = d * copySign(bullet.getYSpeed());
                        bulletList.add(new Pos2d(bullet.getX() + i * mx + dy, bullet.getY() + i * my + dx, bullet));
                    }
                }
            }
        }
    }

    private double copySign(double value) {
        if (value > 0)
            return 1;
        else if (value < 0)
            return -1;
        
        return 0;
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        try {
            if (me.getThinkTime() > BattleBotArena.PROCESSOR_LIMIT * 0.95) return;

            if (isThrowingError)
                g.drawImage(errorImage, x + 3, y + 2, null);
            else
                g.drawImage(botImage, x + 3, y + 2, null);


            if (me.getThinkTime() < BattleBotArena.PROCESSOR_LIMIT * 0.9) {
                int lastMove = me.getLastMove();
                if (lastMove == BattleBotArena.FIREDOWN) {
                    shootAngle = 90;
                    drawTimer = 1;
                } else if (lastMove == BattleBotArena.FIRELEFT) {
                    shootAngle = 180;
                    drawTimer = 1;
                } else if (lastMove == BattleBotArena.FIREUP) {
                    shootAngle = -90;
                    drawTimer = 1;
                } else if (lastMove == BattleBotArena.FIRERIGHT) {
                    shootAngle = 0;
                    drawTimer = 1;
                }

                if (drawTimer % 4 != 0) {
                    Image img = null;
                    int xDiff = 0;
                    int yDiff = 0;

                    if (shootAngle == 0) {
                        img = shootImages.get("right")[drawTimer++];
                        xDiff = Bot.RADIUS * 2;
                    } else if (shootAngle == 90) {
                        img = shootImages.get("down")[drawTimer++];
                        yDiff = Bot.RADIUS * 2;
                    } else if (shootAngle == 180) {
                        img = shootImages.get("left")[drawTimer++];
                        xDiff = -Bot.RADIUS * 4;
                    } else if (shootAngle == -90) {
                        img = shootImages.get("up")[drawTimer++];
                        yDiff = -Bot.RADIUS * 4;
                    }

                    g.drawImage(img, x + xDiff, y + yDiff, null);
                }
            }

            // g.setColor(Color.GREEN);
            // for (Pos2d pos : myList) {
            //     g.drawRect((int) pos.getX(), (int) pos.getY(), 1, 1);
            // }

            // if (nextPos != null) {
            //     g.setColor(Color.GREEN);
            //     g.drawRect((int) nextPos.getX(), (int) nextPos.getY(), 1, 1);
            // }

            // g.setColor(Color.RED);
            // for (Pos2d pos : bulletList) {
            //     g.drawRect((int) pos.getX(), (int) pos.getY(), 1, 1);
            // }

            // g.setColor(Color.RED);
            // for (int i = 0; i < safeX.length; i++) {s
            //     if (!safeX[i])
            //         g.drawLine(i, BattleBotArena.TOP_EDGE, i, BattleBotArena.BOTTOM_EDGE);
            // }

            // for (int i = 0; i < safeY.length; i++) {
            //     if (!safeY[i])
            //         g.drawLine(BattleBotArena.LEFT_EDGE, i, BattleBotArena.RIGHT_EDGE, i);
            // }

            // Pos2d pos = findContain(myList, bulletList);
            // g.setColor(Color.BLUE);
            // if (pos != null)
            //     g.drawRect((int) pos.getX(), (int) pos.getY(), 20, 20);
        } catch (Exception e) {
            isThrowingError = true;

            System.out.println("Error (Alexanderbot): " + e + " Line: " + e.getStackTrace()[0].getLineNumber());
        }
    }

    @Override
    public String getName() {
        // Return a random name so that people can't target my bot
        try {
            String candidateChars  = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
            String str = "";
            for (int i = 0; i < (int) Math.floor(Math.random() * 5) + 5; i++) {
                String add = "" + candidateChars.charAt((int) Math.floor(Math.random() * candidateChars.length()));
                str += Math.random() < 0.5 ? add : add.toLowerCase();
            }

            return str;
        } catch (Exception e) {
            isThrowingError = true;

            System.out.println("Error (AlexanderBot): " + e + " Line: " + e.getStackTrace()[0].getLineNumber());
            return "Driver.java";
        }
    }

    @Override
    public String getTeamName() {
        return null;
    }

    @Override
    public String outgoingMessage() {
        return "Uh oh (ERROR)";
    }

    @Override
    public void incomingMessage(int botNum, String msg) {}

    @Override
    public String[] imageNames() {
        try {
            String s = "shootAnimation/";
            String[] shoot = new String[4 * 4];
            for (int i = 0; i < 4; i++)
                shoot[i] = s + "right/" + i % 4 + ".gif";
            for (int i = 4; i < 8; i++)
                shoot[i] = s + "down/" + i % 4 + ".gif";
            for (int i = 8; i < 12; i++)
                shoot[i] = s + "left/" + i % 4 + ".gif";
            for (int i = 12; i < 16; i++)
                shoot[i] = s + "up/" + i % 4 + ".gif";

            String[] strs = new String[2 + shoot.length];
            strs[0] = "starfish4.png";
            strs[1] = "error.png";
            for (int i = 0; i < shoot.length; i++)
                strs[i + 2] = shoot[i];

            return strs;
        } catch (Exception e) {
            isThrowingError = true;

            System.out.println("Error (AlexanderBot): " + e + " Line: " + e.getStackTrace()[0].getLineNumber());
            return new String[0];
        }
    }

    private Image botImage;
    private Image errorImage;
    private HashMap<String, Image[]> shootImages = new HashMap<>();

    @Override
    public void loadedImages(Image[] images) {
        try {
            botImage = images[0];
            errorImage = images[1];

            shootImages.put("right", new Image[4]);
            shootImages.put("down", new Image[4]);
            shootImages.put("left", new Image[4]);
            shootImages.put("up", new Image[4]);

            int k;

            int width = 50;
            int height = 20;

            k = 0;
            for (int i = 2; i < 6; i++)
                shootImages.get("right")[k++] = images[i].getScaledInstance(width, height, Image.SCALE_FAST);

            k = 0;
            for (int i = 6; i < 10; i++)
                shootImages.get("down")[k++] = images[i].getScaledInstance(height, width, Image.SCALE_FAST);

            k = 0;
            for (int i = 10; i < 14; i++)
                shootImages.get("left")[k++] = images[i].getScaledInstance(width, height, Image.SCALE_FAST);

            k = 0;
            for (int i = 14; i < 18; i++)
                shootImages.get("up")[k++] = images[i].getScaledInstance(height, width, Image.SCALE_FAST);
        } catch (Exception e) {
            isThrowingError = true;
            
            System.out.println("Error (AlexanderBot): " + e + " Line: " + e.getStackTrace()[0].getLineNumber());
        }
    }

    private static class Pos2d {
        private double posX;
        private double posY;
        private Bullet bullet;

        /**
         * Creates a new Pos2d that stores an x and y position
         * @param x the x position
         * @param y the y position
         */
        public Pos2d(double x, double y)
        {
            this.posX = x;
            this.posY = y;
        }

        public Pos2d(double x, double y, Bullet bullet) {
            this(x, y);
            this.bullet = bullet;
        }

        /**
         * set the x and y position to the pos2d
         * @param x the new x position
         * @param y the new y position
         * @return itself
         */
        public Pos2d set(double x, double y) {
            this.posX = x;
            this.posY = y;

            return this;
        }

        /**
         * Sets the x position of the pos2d
         * @param x the new x position
         * @return itself
         */
        public Pos2d setX(double x) {
            this.posX = x;

            return this;
        }

         /**
         * Sets the y position of the pos2d
         * @param y the new y position
         * @return itself
         */
        public Pos2d setY(double y) {
            this.posY = y;

            return this;
        }

        /**
         * pluss an x and y component to the current pos2d
         * @param x plusition x
         * @param y plusition y
         * @return itself
         */
        public Pos2d plus(double x, double y) {
            this.posX += x;
            this.posY += y;

            return this;
        }

        /**
         * pluss a pos2d to another pos2d
         * @param otherPos the pos to plus onto this pos
         * @return itself
         */
        public Pos2d plus(Pos2d otherPos) {
            plus(otherPos.getX(), otherPos.getY());

            return this;
        }

        /**
         * Subtracts an x and y component to the current pos2d
         * @param x sub x
         * @param y sub y
         * @return itself
         */
        public Pos2d minus(double x, double y) {
            plus(-x, -y);

            return this;
        }

        /**
         * Subtracts a pos2d to another pos2d
         * @param otherPos the pos to subtract onto this pos
         * @return itself
         */
        public Pos2d minus(Pos2d otherPos) {
            minus(otherPos.getX(), otherPos.getY());

            return this;
        }

        /**
         * Multipies both the x and y component by a multiplier
         * @param mul the multipier
         * @return itself
         */
        public Pos2d mult(double mul) {
            this.posX *= mul;
            this.posY *= mul;

            return this;
        }

        /**
         * Multipies the x by a multiplier
         * @param mul the multipier
         * @return itself
         */
        public Pos2d multX(double mul) {
            this.posX *= mul;

            return this;
        }


        /**
         * Multipies the y by a multiplier
         * @param mul the multipier
         * @return itself
         */
        public Pos2d multY(double mul) {
            this.posY *= mul;

            return this;
        }

        /**
         * Divides both the x and y component by a divisor
         * @param mul the divisor
         * @return itself
         */
        public Pos2d div(double div) {
            this.posX /= div;
            this.posY /= div;

            return this;
        }

        /**
         * Divides the x component by a divisor
         * @param mul the divisor
         * @return itself
         */
        public Pos2d divX(double div) {
            this.posX /= div;

            return this;
        }

        /**
         * Divides the y component by a divisor
         * @param mul the divisor
         * @return itself
         */
        public Pos2d divY(double div) {
            this.posY /= div;

            return this;
        }

        /**
         * @return the x position
         */
        public double getX() {
            return posX;
        }

        /**
         * @return the y position
         */
        public double getY() {
            return posY;
        }

        public double getMag() {
            return Math.sqrt(posX * posX + posY * posY);
        }

        public double getManhatan() {
            return Math.abs(posX) + Math.abs(posY);
        }

        /**
         * Creates a new copy of the current pos2d
         * @return the copy
         */
        public Pos2d copy() {
            return new Pos2d(posX, posY);
        }

        @Override
        public String toString() {
            return "Pos2d (X: " + posX + ", Y: " + posY + ")";
        }

        @Override
        public boolean equals(Object other) {
            if (other == null) return false;
            Pos2d otherPos = (Pos2d) other;

            return this.posX == otherPos.posX && this.posY == otherPos.posY;
        }
    }
    
}
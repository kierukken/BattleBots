package bots;

import java.awt.*;
import java.util.ArrayList;

import arena.*;

public class CareyBot extends Bot {

    private Image image;
    private int r = Bot.RADIUS;
    private int cd = 0;

    @Override
    public void newRound() {
        cd = 0;
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        cd--;
        double meX = me.getX() + r, meY = me.getY() + r; boolean up = false, down = false, left = false, right = false;
        ArrayList<BotInfo> overheated = new ArrayList<>();
        for (Bullet bullet: bullets) {
            double x = bullet.getX(); double y = bullet.getY(); double xs = bullet.getXSpeed(); double ys = bullet.getYSpeed();

        }
        for (Bullet bullet: bullets) {
            double x = bullet.getX(); double y = bullet.getY(); double xs = bullet.getXSpeed(); double ys = bullet.getYSpeed();
            if ((y < meY && ys > 0) || (y > meY && ys < 0)) {
                if (x > meX - 3 * r && x < meX - r) left = true;
                if (x > meX + r && x < meX + 3 * r) right = true;
            }
            if ((x < meX && xs > 0) || (x > meX && xs < 0)) {
                if (y > meY - 3 * r && y < meY - r) up = true;
                if (y > meY + r && y < meY + 3 * r) down = true;
            }
            if (x > meX - r && x < meX + r) if ((y < meY && y + 10 * ys > meY - r) || (y > meY && y + 10 * ys < meY + r)) {
                for (BotInfo deadBot: deadBots) {
                    double deadX = deadBot.getX() + r; double deadY = deadBot.getY() + r;
                    if (meY + r > deadY - r && meY - r < deadY + r) {
                        if (deadX < meX && deadX + 3 * r > x) return BattleBotArena.RIGHT;
                        else if (deadX > meX && deadX - 3 * r < x) return BattleBotArena.LEFT;
                    }
                }
                if (x < 2 * r) return BattleBotArena.RIGHT;
                else if (x > 1000 - 2 * r) return BattleBotArena.LEFT;
                if (x > meX) {
                    if (left) return BattleBotArena.RIGHT;
                    else return BattleBotArena.LEFT;
                } else {
                    if (right) return BattleBotArena.LEFT;
                    else return BattleBotArena.RIGHT;
                }
            }
            if (y > meY - r && y < meY + r) if ((x < meX && x + 10 * xs > meX - r) || (x > meX && x + 10 * xs < meX + r)) {
                for (BotInfo deadBot: deadBots) {
                    double deadX = deadBot.getX() + r, deadY = deadBot.getY() + r;
                    if (meX + r > deadX - r && meX - r < deadX + r) {
                        if (deadY < meY && deadY + 3 * r > y) return BattleBotArena.DOWN;
                        else if (deadY > meY && deadY - 3 * r < y) return BattleBotArena.UP;
                    }
                }
                if (y < 2 * r) return BattleBotArena.DOWN;
                else if (y > 700 - 2 * r) return BattleBotArena.UP;
                if (y > meY) {
                    if (up) return BattleBotArena.DOWN;
                    else return BattleBotArena.UP;
                } else {
                    if (down) return BattleBotArena.UP;
                    else return BattleBotArena.DOWN;
                }
            }
        }
        for (BotInfo liveBot: liveBots) {
            double x = liveBot.getX() + r; double y = liveBot.getY() + r;
            if (liveBot.getName().equals("Human") || liveBot.isOverheated()) overheated.add(liveBot);
            if (shotOK && cd <= 0) {
                if (x > meX - r && x < meX + r) {
                    cd = 0;
                    if (y > meY && y - meY < 86) return BattleBotArena.FIREDOWN;
                    else if (y < meY && meY - y < 86) return BattleBotArena.FIREUP;
                }
                if (y > meY - r && y < meY + r) {
                    cd = 0;
                    if (x > meX && x - meX < 86) return BattleBotArena.FIRERIGHT;
                    else if (x < meX && meX - x < 86) return BattleBotArena.FIRELEFT;
                }
            }
        }
        if (!overheated.isEmpty()) {
            double x = overheated.get(0).getX() + r, y = overheated.get(0).getY() + r;
            if (shotOK && cd <= 0) {
                int move = fire(meX, meY, x, y);
                if (move != 0) return move;
            }
            return move(meX, meY, x, y, up, down, left, right);
        }
        return BattleBotArena.STAY;
    }

    private int fire(double meX, double meY, double botX, double botY) {
        if (botX > meX - r && botX < meX + r) {
            cd = 10;
            if (botY > meY) return BattleBotArena.FIREDOWN;
            else return BattleBotArena.FIREUP;
        }
        if (botY > meY - r && botY < meY + r) {
            cd = 10;
            if (botX > meX) return BattleBotArena.FIRERIGHT;
            else return BattleBotArena.FIRELEFT;
        }
        return 0;
    }

    private int move(double meX, double meY, double botX, double botY, boolean up, boolean down, boolean left, boolean right) {
        if (botX > meX) {
            if (botY > meY) {
                if (down && right) return BattleBotArena.STAY;
                else if (down) return BattleBotArena.RIGHT;
                else if (right) return BattleBotArena.DOWN;
                else if (Math.abs(botX - meX) > Math.abs(botY - meY)) return BattleBotArena.DOWN;
                else return BattleBotArena.RIGHT;
            } else {
                if (up && right) return BattleBotArena.STAY;
                else if (up) return BattleBotArena.RIGHT;
                else if (right) return BattleBotArena.UP;
                else if (Math.abs(botX - meX) > Math.abs(botY - meY)) return BattleBotArena.UP;
                else return BattleBotArena.RIGHT;
            }
        } else {
            if (botY > meY) {
                if (down && left) return BattleBotArena.STAY;
                else if (down) return BattleBotArena.LEFT;
                else if (left) return BattleBotArena.DOWN;
                else if (Math.abs(botX - meX) > Math.abs(botY - meY)) return BattleBotArena.DOWN;
                else return BattleBotArena.LEFT;
            } else {
                if (up && left) return BattleBotArena.STAY;
                else if (up) return BattleBotArena.LEFT;
                else if (left) return BattleBotArena.UP;
                else if (Math.abs(botX - meX) > Math.abs(botY - meY)) return BattleBotArena.UP;
                else return BattleBotArena.LEFT;
            }
        }
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        g.drawImage(image, x, y, Bot.RADIUS * 2, Bot.RADIUS * 2, null);
    }

    @Override
    public String getName() {
        return "Carey";
    }

    @Override
    public String getTeamName() {
        return "";
    }

    @Override
    public String outgoingMessage() {
        return "";
    }

    @Override
    public void incomingMessage(int botNum, String msg) {}

    @Override
    public String[] imageNames() {
        return new String[] {"dead.png"};
    }

    @Override
    public void loadedImages(Image[] images) {
        image = images[0];
    }
    
}

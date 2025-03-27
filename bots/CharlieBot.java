package bots;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import arena.*;
import java.util.Random;

public class CharlieQBot extends Bot {
    Image picture;
    private int shootDelay = 0;

    @Override
    public void newRound() {
        shootDelay = 0;
    }

    private boolean deadBotInBetween(double sourcePosX, double sourcePosY, double targetPos, double deadPosX,
            double deadPosY, boolean isHorizontal) {
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

    private int shoot(int direction) {
        shootDelay = 10;
        if (direction == 0)
            return BattleBotArena.FIREDOWN;
        if (direction == 1)
            return BattleBotArena.FIRELEFT;
        if (direction == 2)
            return BattleBotArena.FIREUP;
        if (direction == 3)
            return BattleBotArena.FIRERIGHT;
        return BattleBotArena.STAY;
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        shootDelay--;

        Random random = new Random();

        double myX = me.getX() + Bot.RADIUS;
        double myY = me.getY() + Bot.RADIUS;

        ArrayList<BotInfo> overHeatedBots = new ArrayList<BotInfo>();
        ArrayList<BotInfo> botsInLine = new ArrayList<BotInfo>();
        BotInfo closestBot = null;
        int closestBotLastMove;
        BotInfo closestOverheated = null;

        double distance = 10000.0;
        double overHeatDistance = 10000.0;
        for (BotInfo bot : liveBots) {
            double botX = bot.getX() + Bot.RADIUS;
            double botY = bot.getY() + Bot.RADIUS;
            if (botX > myX - 13 && botX < myX + 13 || botY > myY - 13 && botY < myY + 13) {
                botsInLine.add(bot);
            }
            if (bot.isOverheated() || bot.getName().equals("Human")) {
                overHeatedBots.add(bot);
                if (overHeatDistance > Math.sqrt(Math.pow(botX - myX, 2) + Math.pow(botY - myY, 2))) {
                    overHeatDistance = Math.sqrt(Math.pow(botX - myX, 2) + Math.pow(botY - myY, 2));
                    closestOverheated = bot;
                }
            }
            if (distance > Math.sqrt(Math.pow(botX - myX, 2) + Math.pow(botY - myY, 2))) {
                if (bot.getName().equals("Albert") && liveBots.length > 1) continue;
                distance = Math.sqrt(Math.pow(botX - myX, 2) + Math.pow(botY - myY, 2));
                closestBot = bot;
                closestBotLastMove = bot.getLastMove();
            }
        }
        int closestDeadDistance = 10000;
        BotInfo closestDeadBot = null;
        for(BotInfo bot : deadBots){
            double botX = bot.getX() + Bot.RADIUS;
            double botY = bot.getY() + Bot.RADIUS;
            if (closestDeadDistance > Math.sqrt(Math.pow(botX - myX, 2) + Math.pow(botY - myY, 2)) && bot.getBulletsLeft() > 0) {
                closestDeadDistance = (int) Math.sqrt(Math.pow(botX - myX, 2) + Math.pow(botY - myY, 2));
                closestDeadBot = bot;
            }
        }

        boolean canMoveRight = true;
        boolean canMoveLeft = true;
        boolean canMoveUp = true;
        boolean canMoveDown = true;

        for (Bullet bullet : bullets) {
            double bulletX = bullet.getX();
            double bulletY = bullet.getY();
            double xSpeed = bullet.getXSpeed();
            double ySpeed = bullet.getYSpeed();
            
            double nextBulletX = bulletX + xSpeed;
            double nextBulletY = bulletY + ySpeed;
            
            if (Math.abs((myX + 13) - bulletX) <= 13 && Math.abs(myY - bulletY) <= 13) {
                if ((bulletX > myX && xSpeed < 0) || (bulletY == myY && Math.abs(ySpeed) > 0)) {
                    canMoveRight = false;
                }
            }
            if (Math.abs((myX + 26) - nextBulletX) <= 13 && Math.abs(myY - nextBulletY) <= 13) {
                canMoveRight = false;
            }
            if (Math.abs((myX - 13) - bulletX) <= 13 && Math.abs(myY - bulletY) <= 13) {
                if ((bulletX < myX && xSpeed > 0) || (bulletY == myY && Math.abs(ySpeed) > 0)) {
                    canMoveLeft = false;
                }
            }
            if (Math.abs((myX - 26) - nextBulletX) <= 13 && Math.abs(myY - nextBulletY) <= 13) {
                canMoveLeft = false;
            }
            if (Math.abs(myX - bulletX) <= 13 && Math.abs((myY - 13) - bulletY) <= 13) {
                if ((bulletY < myY && ySpeed > 0) || (bulletX == myX && Math.abs(xSpeed) > 0)) {
                    canMoveUp = false;
                }
            }
            if (Math.abs(myX - nextBulletX) <= 13 && Math.abs((myY - 26) - nextBulletY) <= 13) {
                canMoveUp = false;
            }
            if (Math.abs(myX - bulletX) <= 13 && Math.abs((myY + 13) - bulletY) <= 13) {
                if ((bulletY > myY && ySpeed < 0) || (bulletX == myX && Math.abs(xSpeed) > 0)) {
                    canMoveDown = false;
                }
            }
            if (Math.abs(myX - nextBulletX) <= 13 && Math.abs((myY + 26) - nextBulletY) <= 13) {
                canMoveDown = false;
            }
        }
        
        ArrayList<Integer> validMoves = new ArrayList<>();
        if (canMoveRight)
            validMoves.add(BattleBotArena.RIGHT);
        if (canMoveLeft)
            validMoves.add(BattleBotArena.LEFT);
        if (canMoveUp)
            validMoves.add(BattleBotArena.UP);
        if (canMoveDown)
            validMoves.add(BattleBotArena.DOWN);

        /* defense code */
        for (Bullet bullet : bullets) {
            double bulletX = bullet.getX();
            double bulletY = bullet.getY();
            double xSpeed = bullet.getXSpeed();
            double ySpeed = bullet.getYSpeed();
            if (bulletY + 13 >= myY && bulletY - 13 <= myY) {
                if (Math.abs(bulletX - myX) < 90) {
                    if ((bulletX < myX && xSpeed > 0) || (bulletX > myX && xSpeed < 0)) {
                        /* emergency shooting code */
                        if (shotOK && Math.abs(bulletY - myY) / 3 > Math.abs(bulletX - myX) / 6) {
                            if (xSpeed < 0)
                                if (shootDelay <= 3)
                                    return shoot(3);
                                else if (shootDelay <= 3)
                                    return shoot(1);
                        }
                        boolean bulletBlocked = false;
                        boolean lesser = false;
                        boolean greater = false;
                        for (BotInfo deadBot : deadBots) {
                            double deadX = deadBot.getX() + Bot.RADIUS;
                            double deadY = deadBot.getY() + Bot.RADIUS;
                            if (Math.abs(deadX - myX) <= 39) {
                                if (Math.abs(deadY - bulletY) <= 39) {
                                    if (deadY <= myY)
                                        lesser = true;
                                    if (deadY > myY)
                                        greater = true;
                                }
                            }
                            if (deadBotInBetween(myX, myY, bulletX, deadX, deadY, true)) {
                                bulletBlocked = true;
                                break;
                            }
                        }
                        if (!bulletBlocked) {
                            if (bulletY < 78 && canMoveDown) {
                                return BattleBotArena.DOWN;
                            } else if (bulletY > 622 && canMoveUp) {
                                return BattleBotArena.UP;
                            }
                            if (bulletY <= myY && !greater && canMoveDown) {
                                return BattleBotArena.DOWN;
                            } else if (bulletY > myY && !lesser && canMoveUp) {
                                return BattleBotArena.UP;
                            } else {
                                if (xSpeed < 0)
                                    return BattleBotArena.LEFT;
                                else
                                    return BattleBotArena.RIGHT;
                            }
                        }
                    }
                }
            }
            if (bulletX + 13 >= myX && bulletX - 13 <= myX) {
                if (Math.abs(bulletY - myY) < 90) {
                    if ((bulletY < myY && ySpeed > 0) || (bulletY > myY && ySpeed < 0)) {
                        /* emergency shooting code */
                        if (shotOK && Math.abs(bulletX - myX) / 3 > Math.abs(bulletY - myY) / 6) {
                            if (ySpeed < 0)
                                if (shootDelay <= 3)
                                    return shoot(0);
                                else if (shootDelay <= 3)
                                    return shoot(2);
                        }
                        boolean bulletBlocked = false;
                        boolean lesser = false;
                        boolean greater = false;
                        for (BotInfo deadBot : deadBots) {
                            double deadX = deadBot.getX() + Bot.RADIUS;
                            double deadY = deadBot.getY() + Bot.RADIUS;
                            if (Math.abs(deadY - myY) <= 39) {
                                if (Math.abs(deadX - bulletX) <= 39) {
                                    if (deadX <= myX)
                                        lesser = true;
                                    if (deadX > myX)
                                        greater = true;
                                }
                            }
                            if (deadBotInBetween(myX, myY, bulletY, deadX, deadY, false)) {
                                bulletBlocked = true;
                                break;
                            }
                        }
                        if (!bulletBlocked) {
                            if (bulletX < 78 && canMoveRight) {
                                return BattleBotArena.RIGHT;
                            } else if (bulletX > 922 && canMoveLeft) {
                                return BattleBotArena.LEFT;
                            }
                            if (bulletX <= myX && !greater && canMoveRight) {
                                return BattleBotArena.RIGHT;
                            } else if (bulletX > myX && !lesser && canMoveLeft) {
                                return BattleBotArena.LEFT;
                            } else {
                                if (ySpeed < 0)
                                    return BattleBotArena.UP;
                                else
                                    return BattleBotArena.DOWN;
                            }
                        }
                    }
                }
            }
        }
        /* offense code */
        for (BotInfo bot : botsInLine) {
            double botX = bot.getX() + Bot.RADIUS;
            double botY = bot.getY() + Bot.RADIUS;
            if ((botY > myY - 13 && botY < myY + 13)) {
                boolean blocked = false;
                for (BotInfo deadBot : deadBots) {
                    if (deadBotInBetween(myX, myY, botX, deadBot.getX() + Bot.RADIUS, deadBot.getY() + Bot.RADIUS,
                            true)) {
                        blocked = true;
                        continue;
                    }
                }
                if (!blocked && Math.abs(myX - botX) < 80) {
                    if (botX < myX) {
                        if (shotOK && shootDelay <= 0)
                            return shoot(1);
                    } else {
                        if (shotOK && shootDelay <= 0)
                            return shoot(3);
                    }
                }
            } else if ((botX > myX - 13 && botX < myX + 13)) {
                boolean blocked = false;
                for (BotInfo deadBot : deadBots) {
                    if (deadBotInBetween(myX, myY, botY, deadBot.getX() + Bot.RADIUS, deadBot.getY() + Bot.RADIUS,
                            false)) {
                        blocked = true;
                        break;
                    }
                }
                if (!blocked && Math.abs(myY - botY) < 80) {
                    if (botY < myY) {
                        if (shotOK && shootDelay <= 0)
                            return shoot(2);
                    } else {
                        if (shotOK && shootDelay <= 0)
                            return shoot(0);
                    }
                }
            }
        }
        /*Pick up bullet */
        if (closestDeadBot != null&& me.getBulletsLeft() == 0) {
            double closestBotX = closestDeadBot.getX() + Bot.RADIUS;
            double closestBotY = closestDeadBot.getY() + Bot.RADIUS;
            if (Math.abs(closestBotX-myX) >= Math.abs(closestBotY)){
                closestBotX = closestBotX >= 500 ? closestBotX-13:closestBotX+13;
            }
            else{
                closestBotY = closestBotY >= 350 ? closestBotY-13:closestBotY+13; 
            }
        
            if (Math.abs(closestBotX-myX) >= Math.abs(closestBotY-myY)) {
                if (closestBotX > myX) {
                    if (canMoveRight) {
                        return BattleBotArena.RIGHT;
                    }
                } 
                else {
                    if (canMoveLeft) {
                        return BattleBotArena.LEFT;
                    }
                }
            }
            else {
                if (closestBotY > myY) {
                    if (canMoveDown) {
                        return BattleBotArena.DOWN;
                    }
                } 
                else {
                    if (canMoveUp) {
                        return BattleBotArena.UP;
                    }
                }
            }
        }
        /* move away from close bots*/
        if ((closestBot != null && distance < 38) || (closestDeadBot != null && closestDeadDistance < 38)) {
            BotInfo nearBot;
            double nearBotX, nearBotY;
            
            if (closestBot != null && distance < 38) {
                nearBot = closestBot;
                nearBotX = nearBot.getX() + Bot.RADIUS;
                nearBotY = nearBot.getY() + Bot.RADIUS;
            } else {
                nearBot = closestDeadBot;
                nearBotX = nearBot.getX() + Bot.RADIUS;
                nearBotY = nearBot.getY() + Bot.RADIUS;
            }
            
            // Move AWAY from the bot, not toward it
            if (nearBotX > myX && canMoveLeft) return BattleBotArena.LEFT;
            if (nearBotX < myX && canMoveRight) return BattleBotArena.RIGHT;
            if (nearBotY > myY && canMoveUp) return BattleBotArena.UP;
            if (nearBotY < myY && canMoveDown) return BattleBotArena.DOWN;
        }
        /* overheated bot */
        if (distance < 50){
            if (closestOverheated != null) {
                double overheatedX = closestOverheated.getX() + Bot.RADIUS;
                double overheatedY = closestOverheated.getY() + Bot.RADIUS;
                if (overheatedY > myY - 13 && overheatedY < myY + 13) {
                    boolean blocked = false;
                    for (BotInfo deadBot : deadBots) {
                        if (deadBotInBetween(myX, myY, overheatedX, deadBot.getX() + Bot.RADIUS,
                                deadBot.getY() + Bot.RADIUS, true)) {
                            blocked = true;
                            break;
                        }
                    }
                    if (!blocked && shotOK && shootDelay <= 0) {
                        return shoot(overheatedX < myX ? 1 : 3);
                    }
                } else if (overheatedX > myX - 13 && overheatedX < myX + 13) {
                    boolean blocked = false;
                    for (BotInfo deadBot : deadBots) {
                        if (deadBotInBetween(myX, myY, overheatedY, deadBot.getX() + Bot.RADIUS,
                                deadBot.getY() + Bot.RADIUS, false)) {
                            blocked = true;
                            break;
                        }
                    }
                    if (!blocked && shotOK && shootDelay <= 0) {
                        return shoot(overheatedY < myY ? 2 : 0);
                    }
                }
                double xDist = Math.abs(overheatedX - myX);
                double yDist = Math.abs(overheatedY - myY);
                if (xDist <= yDist) {
                    if (myX < overheatedX && canMoveRight) {
                        return BattleBotArena.RIGHT;
                    } else if (myX > overheatedX && canMoveLeft) {
                        return BattleBotArena.LEFT;
                    } else if (myY < overheatedY && canMoveDown) {
                        return BattleBotArena.DOWN;
                    } else if (myY > overheatedY && canMoveUp) {
                        return BattleBotArena.UP;
                    }
                } else {
                    if (myY < overheatedY && canMoveDown) {
                        return BattleBotArena.DOWN;
                    } else if (myY > overheatedY && canMoveUp) {
                        return BattleBotArena.UP;
                    } else if (myX < overheatedX && canMoveRight) {
                        return BattleBotArena.RIGHT;
                    } else if (myX > overheatedX && canMoveLeft) {
                        return BattleBotArena.LEFT;
                    }
                }
            }
        }
        /*Move to target bot */
        if (closestBot != null) {
            double closestBotX = closestBot.getX() + Bot.RADIUS;
            double closestBotY = closestBot.getY() + Bot.RADIUS;
            if (Math.abs(closestBotX-myX) >= Math.abs(closestBotY)){
                closestBotX = closestBotX >= 500 ? closestBotX-40:closestBotX+40;
            }
            else{
                closestBotY = closestBotY >= 350 ? closestBotY-40:closestBotY+40; 
            }
            /*
            for (BotInfo bot: deadBots){
                double deadBotX = bot.getX() + Bot.RADIUS;
                double deadBotY = bot.getY() + Bot.RADIUS;
                if (Math.abs(deadBotX-closestBotX) <= 18 && Math.abs(deadBotY-closestBotY) <= 18){
                    closestBotX = closestBotX >= 500 ? closestBotX-26:closestBotX+26;
                    closestBotY = closestBotY >= 350 ? closestBotY-26:closestBotY+26; 
                }
            }
            */
            if (Math.abs(closestBotX-myX) >= Math.abs(closestBotY-myY)) {
                if (closestBotX > myX) {
                    if (canMoveRight) {
                        return BattleBotArena.RIGHT;
                    }
                } 
                else {
                    if (canMoveLeft) {
                        return BattleBotArena.LEFT;
                    }
                }
            }
            else {
                if (closestBotY > myY) {
                    if (canMoveDown) {
                        return BattleBotArena.DOWN;
                    }
                } 
                else {
                    if (canMoveUp) {
                        return BattleBotArena.UP;
                    }
                }
            }
        }
        /*Move to optimal position */
        if (!validMoves.isEmpty()) {
            int arenaWidth = BattleBotArena.RIGHT_EDGE - BattleBotArena.LEFT_EDGE;
            int arenaHeight = BattleBotArena.BOTTOM_EDGE - BattleBotArena.TOP_EDGE;

            int optimalX = arenaWidth * 3 / 5 + BattleBotArena.LEFT_EDGE;
            int optimalY = arenaHeight * 2 / 5 + BattleBotArena.TOP_EDGE;

            double dangerFromRight = 1000;
            double dangerFromLeft = 1000;
            double dangerFromUp = 1000;
            double dangerFromDown = 1000;

            for (BotInfo bot : liveBots) {
                if (bot.getName().equals(getName()))
                    continue;

                double botX = bot.getX() + Bot.RADIUS;
                double botY = bot.getY() + Bot.RADIUS;

                if (Math.abs(botY - myY) < 50) {
                    if (botX > myX) {
                        dangerFromRight = Math.min(dangerFromRight, botX - myX);
                    } else {
                        dangerFromLeft = Math.min(dangerFromLeft, myX - botX);
                    }
                }

                if (Math.abs(botX - myX) < 50) {
                    if (botY > myY) {
                        dangerFromDown = Math.min(dangerFromDown, botY - myY);
                    } else {
                        dangerFromUp = Math.min(dangerFromUp, myY - botY);
                    }
                }
            }

            double rightScore = dangerFromRight / 5 - Math.abs(optimalX - (myX + 13));
            double leftScore = dangerFromLeft / 5 - Math.abs(optimalX - (myX - 13));
            double upScore = dangerFromUp / 5 - Math.abs(optimalY - (myY - 13));
            double downScore = dangerFromDown / 5 - Math.abs(optimalY - (myY + 13));

            if (myX < BattleBotArena.LEFT_EDGE + 50)
                leftScore -= 50;
            if (myX > BattleBotArena.RIGHT_EDGE - 50)
                rightScore -= 50;
            if (myY < BattleBotArena.TOP_EDGE + 50)
                upScore -= 50;
            if (myY > BattleBotArena.BOTTOM_EDGE - 50)
                downScore -= 50;

            int bestMove = BattleBotArena.STAY;
            double bestScore = -Double.MAX_VALUE;

            if (canMoveRight && validMoves.contains(BattleBotArena.RIGHT) && rightScore > bestScore) {
                bestScore = rightScore;
                bestMove = BattleBotArena.RIGHT;
            }
            if (canMoveLeft && validMoves.contains(BattleBotArena.LEFT) && leftScore > bestScore) {
                bestScore = leftScore;
                bestMove = BattleBotArena.LEFT;
            }
            if (canMoveUp && validMoves.contains(BattleBotArena.UP) && upScore > bestScore) {
                bestScore = upScore;
                bestMove = BattleBotArena.UP;
            }
            if (canMoveDown && validMoves.contains(BattleBotArena.DOWN) && downScore > bestScore) {
                bestScore = downScore;
                bestMove = BattleBotArena.DOWN;
            }

            if (random.nextInt(5) == 0 && !validMoves.isEmpty()) {
                return validMoves.get(random.nextInt(validMoves.size()));
            }

            return bestMove;
        }
        return BattleBotArena.STAY;
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        g.drawImage(picture , x, y , 26 , 26 , null);
    }

    @Override
    public String getName() {
        return "Charlie";
    }

    @Override
    public String getTeamName() {
        return "";
    }

    @Override
    public String outgoingMessage() {
        return "I am a bot";
    }

    @Override
    public void incomingMessage(int botNum, String msg) {

    }

    @Override
    public String[] imageNames() {
        String[] images = { "tank.png" };
        return images;
    }

    @Override
    public void loadedImages(Image[] images) {
        picture = images[0];
    }

}

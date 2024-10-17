package bots;

import java.util.Random;
import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class JasonBot extends Bot {

    BotHelper helper = new BotHelper();

    Image creeper;

    private String name = null;

    private String message = null;

    private boolean center = false;

    private Random rand = new Random();
    private int randomizer;
    private int fireDirection;

    @Override
    public void newRound() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'newRound'");
        randomizer = rand.nextInt(2);
        fireDirection = 0;
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        // Try to exxclude bullets that are not shooting towards the bot
        // Currently still detecting bullets that are not shooting towards the bot,
        // which makes the bot moves weirdly
        if (bullets.length > 0) {
            Bullet closestBullet = helper.findClosest(me, bullets);
            // BotInfo closestBot = helper.findClosest(me, liveBots);
            // System.out.println(center);
            // System.out.println(me.getY());
            if (closestBullet.getX() >= (me.getX() - 20) && closestBullet.getX() <= (me.getX() + 30)
                    && closestBullet.getYSpeed() != 0) {
                if (center == true) {
                    if (me.getX() < 50 || me.getX() > 950) {
                        center = false;
                        return BattleBotArena.STAY;
                    } else {
                        if (randomizer == 0) {
                            return BattleBotArena.LEFT;
                        } else {
                            return BattleBotArena.RIGHT;
                        }
                    }
                } else if (center == false) {
                    if (me.getX() < 490) {
                        // System.out.println("MOVE LEFT");
                        return BattleBotArena.RIGHT;
                    } else if (me.getX() > 510) {
                        // System.out.println("MOVE RIGHT");
                        return BattleBotArena.LEFT;
                    } else {
                        center = true;
                        return BattleBotArena.STAY;
                    }
                } else {
                    return BattleBotArena.STAY;
                }
            } else if (closestBullet.getY() >= me.getY() && closestBullet.getY() <= (me.getY() + 40)
                    && closestBullet.getXSpeed() != 0) {
                {
                    if (center == true) {
                        if (me.getY() < 50 || me.getY() > 650) {
                            center = false;
                            return BattleBotArena.STAY;
                        } else {
                            if (randomizer == 0) {
                                return BattleBotArena.UP;
                            } else {
                                return BattleBotArena.DOWN;
                            }
                        }
                    } else if (center == false) {
                        if (me.getY() < 340) {
                            // System.out.println("MOVE DOWN");
                            return BattleBotArena.DOWN;
                        } else if (me.getY() > 360) {
                            // System.out.println("MOVE UP");
                            return BattleBotArena.UP;
                        } else {
                            center = true;
                            return BattleBotArena.STAY;
                        }
                    } else {
                        return BattleBotArena.STAY;
                    }
                }
            } else {
                return secondaryAction(me, shotOK);
            }
        } else {
            return secondaryAction(me, shotOK);
        }
    }

    public int shootAround(boolean shotOK) {
        if (shotOK == true) {
            if (fireDirection == 0) {
                fireDirection += 1;
                return BattleBotArena.FIREUP;
            } else if (fireDirection == 1) {
                fireDirection += 1;
                return BattleBotArena.FIRERIGHT;
            } else if (fireDirection == 2) {
                fireDirection += 1;
                return BattleBotArena.FIREDOWN;
            } else if (fireDirection == 3) {
                fireDirection = 0;
                return BattleBotArena.FIRELEFT;
            } else {
                return BattleBotArena.STAY;
            }
        } else {
            return BattleBotArena.STAY;
        }
    }

    public int secondaryAction(BotInfo me, boolean shotOK) {
        if (me.getX() < 100) {
            return BattleBotArena.RIGHT;
        } else if (me.getX() > 900) {
            return BattleBotArena.LEFT;
        } else if (me.getY() < 50) {
            return BattleBotArena.DOWN;
        } else if (me.getY() > 650) {
            return BattleBotArena.UP;
        } else {
            return shootAround(shotOK);
        }
    }

    // public Bullet findClosest(BotInfo me, Bullet[] _bullets) {
    //     Bullet closest;
    //     double distance, closestDist;
    //     // TODO: Fix the error here before the next semester - In the mean time be sure
    //     // to error check as this will crash if there are no bullets
    //     closest = _bullets[0];
    //     closestDist = Math.abs(me.getX() - closest.getX()) + Math.abs(me.getY() - closest.getY());
    //     for (int i = 1; i < _bullets.length; i++) {
    //         distance = Math.abs(me.getX() - _bullets[i].getX()) + Math.abs(me.getY() - _bullets[i].getY());
    //         if (distance < closestDist) {
    //             closest = _bullets[i];
    //             closestDist = distance;
    //         }
    //     }
    //     return closest;
    // }

    public boolean bulletDirection(BotInfo me, Bullet bullet) {
        if (helper.calcDisplacement(me.getX(), bullet.getX()) > 0) { //if the bullet is at the right of the bot
            if (bullet.getXSpeed() < 0) {// if the bullet is moving to the left
                return true; 
            } else {
                return false;
            }
        } else if (helper.calcDisplacement(me.getX(), bullet.getX()) < 0) { //if the bullet is at the left of the bot
            if (bullet.getXSpeed() > 0) {// if the bullet is moving to the right
                return true;
            } else {
                return false;
            }
        } else if (helper.calcDisplacement(me.getY(), bullet.getY()) > 0) { //if the bullet is 
            if (bullet.getYSpeed() < 0) {
                return true;
            } else {
                return false;
            }
        } else if (helper.calcDisplacement(me.getY(), bullet.getY()) < 0) {
            if (bullet.getYSpeed() > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        g.drawImage(creeper, x, y, Bot.RADIUS * 2, Bot.RADIUS * 2, null);
        // throw new UnsupportedOperationException("Unimplemented method 'draw'");
    }

    @Override
    public String getName() {
        name = "JasonBot";
        return name;
        // throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }

    @Override
    public String getTeamName() {
        return "CHINA";
        // throw new UnsupportedOperationException("Unimplemented method
        // 'getTeamName'");
    }

    @Override
    public String outgoingMessage() {
        // throw new UnsupportedOperationException("Unimplemented
        // method'outgoingMessage'");
        message = null;
        return message;
    }

    @Override
    public void incomingMessage(int botNum, String msg) {
        // throw new UnsupportedOperationException("Unimplemented
        // method'incomingMessage'");
    }

    @Override
    public String[] imageNames() {
        String[] images = { "creeper.jpg" };
        return images;
        // throw new UnsupportedOperationException("Unimplemented method 'imageNames'");
    }

    @Override
    public void loadedImages(Image[] images) {
        creeper = images[0];
        // throw new UnsupportedOperationException("Unimplemented
        // method'loadedImages'");
    }

}

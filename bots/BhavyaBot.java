package bots;
import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap ;
import java.util.List  ;
import java.util.Map ;
public class BhavyaBot extends Bot {
    Image picture ; 
    BotHelper bothelper = new BotHelper() ; 
    private Map<Bullet , double[]> bulletPreviousPositions = new HashMap<>() ; 
    private double dangerBotDistance = 30 ; 
    @Override
    public void newRound() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'newRound'");
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
      
      // Finding the closest Bullets 
      Bullet[] closestBullets = findTwoClosestBullets(me, bullets);
      BotInfo threatBot = isBotNearby(me, liveBots);
      if(threatBot != null){
        String shootDirection = dangerBotShootDirection(me, threatBot);
        System.out.println(shootDirection);
        if(shotOK){
            
            return stringToCommand(shootDirection) ; 
            
        }
      }
      String moveDecision = "STAY STILL" ;
      for(Bullet bullet : closestBullets){
        if( bullet != null){
            double[] prevPosition = bulletPreviousPositions.get(bullet) ;
            if(prevPosition != null){
                String direction = getBulletDirection(bullet, prevPosition[0], prevPosition[1]);
                String reaction = reactToBullet(bullet, direction, me);
                if(!reaction.equals("STAY STILL")){
                    moveDecision = reaction ;

                }
            }
        }
      }
   
      updateBulletTracking(closestBullets);
      
       return stringToCommand(moveDecision) ; 
    }
       
       
        //throw new UnsupportedOperationException("Unimplemented method 'getMove'");
    

    @Override
    public void draw(Graphics g, int x, int y) {
        g.drawImage(picture , x, y , 25 , 25 , null);
        //throw new UnsupportedOperationException("Unimplemented method 'draw'");
    }

    @Override
    public String getName() {
        return "Monte" ; 
        //throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }

    @Override
    public String getTeamName() {
        return "Ka'ah";
        //throw new UnsupportedOperationException("Unimplemented method 'getTeamName'");
    }

    @Override
    public String outgoingMessage() {
        return "Die bot Die" ; 
        //throw new UnsupportedOperationException("Unimplemented method 'outgoingMessage'");
    }

    @Override
    public void incomingMessage(int botNum, String msg) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'incomingMessage'");
    }

    @Override
    public String[] imageNames() {
        String [] images = {"mango2.png" , "starfish4.png" , "SamBot.png","drone_down.png"};
        return images ; 
        //throw new UnsupportedOperationException("Unimplemented method 'imageNames'");
    }

    @Override
    public void loadedImages(Image[] images) {
        picture = images[0] ; 
        throw new UnsupportedOperationException("Unimplemented method 'loadedImages'");
    }
    public Bullet[] findTwoClosestBullets(BotInfo me , Bullet[] bullets){
        // Finding the closest Bullet 
        Bullet closest = bothelper.findClosest(me, bullets) ;
        // if no Bullet on screen return empty array 
        if(closest == null){
            return new Bullet[]{null , null} ; 
        }
        List<Bullet> remainingBullets = new ArrayList<>(Arrays.asList(bullets));
        remainingBullets.remove(closest) ; 

        // Finding the second closest bullet 
         Bullet secondClosest = bothelper.findClosest(me, remainingBullets.toArray(new Bullet[0]));
         return new Bullet[]{closest , secondClosest} ; 
    } 
    public void updateBulletTracking(Bullet[] closestBullets){
        if(closestBullets == null || closestBullets.length == 0 ){
            bulletPreviousPositions.clear() ;
            return;
        }
        for(Bullet bullet : closestBullets){
            if(bullet != null){
                bulletPreviousPositions.put(bullet , new double[]{bullet.getX(), bullet.getY()});
            }    
        }
        cleanUpBullets(closestBullets);
     }
    public String getBulletDirection(Bullet bullet , double prevX , double prevY){
        double currentX = bullet.getX() ; 
        double currentY = bullet.getY() ; 
        double deltaX = currentX - prevX ; 
        double deltaY = currentY - prevY ; 
        if(deltaX == 0 && deltaY < 0 ){
            return "UP" ; 
        }
        else if(deltaX == 0 && deltaY > 0 ){
            return "DOWN" ;
        }
        else if (deltaY == 0 && deltaX < 0 ){
            return "LEFT" ; 
        }
        else if(deltaY == 0 && deltaX > 0 ){
            return "RIGHT" ;
        }
        else{
            return "STAY" ;
        }
    }   
    public String reactToBullet(Bullet bullet , String direction , BotInfo me){
        double bulletX = bullet.getX() ; 
        double bulletY = bullet.getY() ;
        if((direction.equals("RIGHT") || direction.equals("LEFT"))){
            return (bulletY - me.getY() > 0 ) ? "MOVE UP" : "MOVE DOWN" ; 
        } else if ((direction.equals("UP") || direction.equals("DOWN"))){
            return(bulletX - me.getX() > 0 ) ? "MOVE LEFT" : "MOVE RIGHT" ; 
        }
        return "STAY STILL" ; 
    }
    public void cleanUpBullets(Bullet[] currentBullets){
        if(currentBullets == null || currentBullets.length == 0 ){
            bulletPreviousPositions.clear() ; 
            return ; 
        }
        for(Bullet bullet : new ArrayList<>(bulletPreviousPositions.keySet())){
            boolean isActive = false ;
            for(Bullet currentbullet : currentBullets){
                if(bullet.equals(currentbullet)){
                    isActive = true ; 
                    break ; 
                }
            }
            if(!isActive){
                bulletPreviousPositions.remove(bullet) ; 
            }
        }
        }
    public int stringToCommand(String reaction){
             switch (reaction) {
                 case "MOVE UP" -> {
                     return BattleBotArena.UP;
            }
                 case  "MOVE DOWN" -> {
                     return BattleBotArena.DOWN;
            }
                 case "MOVE RIGHT" -> {
                     return BattleBotArena.RIGHT ;    
            }
                  case "MOVE LEFT" -> {
                      return BattleBotArena.LEFT ;
            }
                  case  "SHOOT DOWN" ->{
                    return BattleBotArena.FIREDOWN ;
                  }
                  case "SHOOT UP" ->{
                    return BattleBotArena.FIREUP;
                  }
                  case "SHOOT RIGHT" -> {
                    return BattleBotArena.FIRERIGHT;
                  }
                  case "SHOOT LEFT" -> {
                    return BattleBotArena.FIRELEFT ;
                  }
             }
             return BattleBotArena.STAY ; 
        }
    public BotInfo isBotNearby(BotInfo me , BotInfo[] liveBots){
        for(BotInfo otherBots : liveBots){
            if(otherBots != null && !otherBots.equals(me)){
                double distance = bothelper.calcDistance(otherBots.getX(), otherBots.getY(), me.getX(), me.getY()) ;
                if(distance < dangerBotDistance){
                    if(Math.abs(otherBots.getX() - me.getX()) < 10 || Math.abs(otherBots.getY() - me.getY()) < 10){
                        return otherBots ; 
                    }
                }
            }
        }
        return null ; 
    }
    public String dangerBotShootDirection(BotInfo me , BotInfo targetBot){
        if(Math.abs(targetBot.getX() - me.getX()) < 10 ){
            return (targetBot.getY() > me.getY()) ? "SHOOT DOWN" : "SHOOT UP" ;
        }
        else if (Math.abs(targetBot.getY() - me.getY()) < 10){
            return (targetBot.getX() > me.getX()) ? "SHOOT RIGHT" : "SHOOT LEFT" ;
        }
        return "DON'T SHOOT" ; 
    }   
}
   



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
    private Map<Integer , double[]> botPreviousPositions = new HashMap<>() ; 
    private final  double dangerBotDistance = 300 ; 
    private final  double edgeDistanceX = 100 ; 
    private final double edgeDistanceY = 50 ;  
    private  final int edgeEscape = 5 ; 
    private  int edgeEscapeStep = 0 ; 
    private boolean escapeEdge = false ; 
    private String Edge = "" ; 
    private final int maxBullet = 2 ; 
    private int currentBullet =  0 ; 
    private String lastMove = "" ; 
    private int timeStamp = 0 ; 
    @Override
    public void newRound() {
        
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
      
      // Finding the closest Bullets 
     
      Bullet[] closestBullets = findTwoClosestBullets(me, bullets);
      double botCurrentX = me.getX() ;
      double botCurrentY = me.getY() ; 
      double[] botPositions = {botCurrentX , botCurrentY} ;
      timeStamp++ ; 
      updateBotPosition(botPositions, timeStamp);
      /*if(me.getX() < edgeDistanceX){
          Edge = "MOVE RIGHT" ; 
         escapeEdge = true ; 
      }else if (me.getX() > 1300 - edgeDistanceX){
        Edge = "MOVE LEFT" ;
        escapeEdge = true ; 
      }else if (me.getY() < edgeDistanceY){
        Edge = "MOVE DOWN" ;
        escapeEdge = true ; 
      }else if (me.getY() > 700 - edgeDistanceY){
        Edge = "MOVE UP" ; 
        escapeEdge = true ; 
      }
      if(escapeEdge){
            if(edgeEscapeStep == edgeEscape){
                escapeEdge = false ; 
            }else{
                edgeEscapeStep++ ; 
            }
            return stringToCommand(Edge) ;
      }*/

      
          BotInfo threatBot = isBotNearby(me, liveBots);
      if(threatBot != null){
        String shootDirection = dangerBotShootDirection(me, threatBot);
        
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
      System.out.println("MOVE DECISION IS : " + moveDecision);
       return stringToCommand(moveDecision);
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        g.drawImage(picture , x, y , 25 , 25 , null);
       
    }

    @Override
    public String getName() {
        return "Monte" ; 
    
    }

    @Override
    public String getTeamName() {
        return "Ka'ah";
      
    }

    @Override
    public String outgoingMessage() {
        return "Die bot Die" ; 
     
    }

    @Override
    public void incomingMessage(int botNum, String msg) {
       
        
    }

    @Override
    public String[] imageNames() {
        String [] images = {"mango2.png" , "starfish4.png" , "SamBot.png","drone_down.png"};
        return images ; 

    }

    @Override
    public void loadedImages(Image[] images) {
        picture = images[0] ; 
        
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
        }
        else if((direction.equals("RIGHT") || direction.equals("LEFT")) && bulletY - me.getY() == 0 ){
                return (me.getY() - 350 > 0 ) ? "MOVE UP" : "MOVE DOWN" ; 
        }
         else if ((direction.equals("UP") || direction.equals("DOWN"))){
            return(bulletX - me.getX() > 0 ) ? "MOVE LEFT" : "MOVE RIGHT" ; 
        }
        else if ((direction.equals("UP") || direction.equals("DOWN")) && bulletX - me.getX() == 0 ){
            return (me.getX() - 650 > 0 ) ? "MOVE LEFT" : "MOVE RIGHT" ; 
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
                    System.out.println("GOING UP");
                     return BattleBotArena.UP;
            }
                 case  "MOVE DOWN" -> {
                    System.out.println("GOING DOWN");
                     return BattleBotArena.DOWN;
            }
                 case "MOVE RIGHT" -> {
                    System.out.println("GOING RIGHT");
                     return BattleBotArena.RIGHT ;    
            }
                  case "MOVE LEFT" -> {
                    System.out.println("GOING LEFT");
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
                    if(Math.abs(otherBots.getX() - me.getX()) < 10 || Math.abs(otherBots.getY() - me.getY()) < 20){
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
    public String isAtEdges(BotInfo me){
       double currentBotX = me.getX() ; 
       double currentBotY = me.getY() ;
       if(currentBotX > 1300 - edgeDistanceX){
        return "MOVE LEFT" ; 
       }
       else if(currentBotX < edgeDistanceX){
        return "MOVE RIGHT" ; 
       }
       else if (currentBotY > 700 - edgeDistanceY){
        return "MOVE UP" ; 
       }
       else if(currentBotY < edgeDistanceY){
        return "MOVE DOWN" ;
       }
       else{
        return "ALL GOOD" ; 
       }
    }
    public void updateBotPosition(double[] botPrevPosition , int timeStamp){
       botPreviousPositions.put(timeStamp , botPrevPosition) ;
    }
    //public boolean isMoving(String lastMove , BotInfo me){
     
   // }
}
   



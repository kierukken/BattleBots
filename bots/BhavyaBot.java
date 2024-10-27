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
    Image picture ; // Image of the bot
    BotHelper bothelper = new BotHelper() ; // Initializing BotHelper class to use its methods
    private Map<Bullet , double[]> bulletPreviousPositions = new HashMap<>() ; // A hasp map to store bullet's previous positions
    double [] botPreviousPositions = new double[2] ; // A Double array to store bot's previous positions
    double[] botPositions = new double[2] ; // A double array to store the bot's current positions
    BotInfo[] closestBots = new BotInfo[3] ; 

    private final  double dangerBotDistance = 400 ; // Distance variable to figure out when a bot danger and when not
    private int timeStamp = 0 ; // Time Stamp to take care of certain things in the program
    private int bulletsShot = 0 ;  // Variable to store how many bullets have been shot 
    private int totalBulletsShot = 0;
    private String lastDecision = "" ; // String to store the last expected move of the bot
    private int count = 0 ; // Counter to implement the opening stratergy of the bot
    private boolean startShoot = true ; // Boolean To implement the opening stratergy of the bot
    private String moveToBot = "" ;  // String to store where to move to reach a bot
    private BotInfo nearestBot ; // BotInfo object to store the closest Bot
    private BotInfo dangerBot ;  
    private boolean danger = false  ; 
    private boolean shootDanger = false ; 
    private boolean bulletsShort = false ; 
    private String startDirection = "" ; 
    private boolean atEdge ;
    private String edge ;  
    private String shootDirection ;
    private int moveCounter = 0 ; 
    @Override
    public void newRound() {
        count = 0 ; 
        startShoot = true ; 
        totalBulletsShot =0 ;
        timeStamp = 0 ;
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        System.out.println(timeStamp);
        // This function implements if the bot is at the edge
        if(atEdge){
            if(moveCounter == 5 ){
                atEdge = false ; 
            }
            String lastShoot = shootDirection ; 
            double meX = me.getX() ; 
            if(edge.equals("AT TOP EDGE") && (meX < 5 || meX > 695) && (lastShoot.equals("SHOOT RIGHT") || lastShoot.equals("SHOOT LEFT")) ){
                moveCounter++ ;
                return BattleBotArena.DOWN ; 
            }
            if(edge.equals("AT BOTTOM EDGE") && (meX < 5 || meX > 695) && (lastShoot.equals("SHOOT RIGHT") || lastShoot.equals("SHOOT LEFT")) ){
                moveCounter++ ;
                return BattleBotArena.UP ; 
            }
        }
        /* CODE FOR NOT SHOOTING OUR TEAM BOTS NEXT THREE LINES */
        
           List<BotInfo> oppBots = new ArrayList<>(Arrays.asList(liveBots));
           oppBots.removeIf(bot -> bot.getTeamName().equals("Warriors"));
           liveBots = oppBots.toArray(new BotInfo[0]);
           
       // This block is used for making sure the bot never runs out of bullets
       if(totalBulletsShot > 25 && bulletsShort == false  ){
        shotOK = false ; 
        BotInfo closestDeadBot = bothelper.findClosest(me, deadBots);
        if(Math.abs(me.getX() - closestDeadBot.getX()) > 5 || Math.abs(me.getY() - closestDeadBot.getY()) > 5){
               String move = goToBot(me, closestDeadBot);
               return stringToCommand(move) ; 
        }
        else{
            bulletsShort = true ; 
        }
       }
       // This implements if there is a danger bot near me
       if(danger){
        danger = false ; 
        String direction  = getBotDirection(me, dangerBot) ; 
        if(!direction.equals("DON'T SHOOT")){
            shootDanger = true ; 
            return stringToCommand(direction) ; 
        }
       }
       if(shootDanger){
        shootDanger = false ; 
        if(lastDecision != null){
            return stringToCommand(lastDecision) ; 
        }
       }
      // Finding the two closest Bullets
      Bullet[] closestBullets = findTwoClosestBullets(me, bullets) ;
      if(closestBullets == null){
        return BattleBotArena.DOWN ; 
      }
     // Adding bot's current Position to the botPosition
      double botCurrentX = me.getX() ; 
      double botCurrentY = me.getY() ; 
      botPositions[0] = botCurrentX ;
      botPositions[1] = botCurrentY;
      // Chekcking if less than 6 bots left on the screen
      if(liveBots.length < 6){
        // Checks if the bot was supposed move in the last move
        if(!moveToBot.isEmpty())
            if(checkStuck(botPreviousPositions, botPositions, moveToBot)){
                return handleStuck(lastDecision) ; 
            }
            // Finds the nearest Bot
        if(liveBots != null){
            nearestBot = bothelper.findClosest(me, liveBots);
        }
        if(nearestBot != null){
            String killBot = killBot(me, nearestBot);
            if(killBot != null){
                totalBulletsShot++ ; 
                return stringToCommand(killBot);
            }
            moveToBot = goToBot(me, nearestBot);
            return stringToCommand(moveToBot);
        }
      }
     // Using Count and startShoot variable to implement the opening stratergy
    //   if(count < 4 ){
    //     count++ ;
    //     return BattleBotArena.DOWN;
      //} 
       if (startShoot){
        count++ ;
        switch(count) {
            case 1 -> {
                System.out.println("START DIRECTION : " + startDirection);
                return (me.getX() > 650 ) ? BattleBotArena.UP : BattleBotArena.DOWN ; 
            }
            case 2 -> {
                totalBulletsShot++ ;
                return BattleBotArena.FIREDOWN ;
              }
            case 3-> {
                totalBulletsShot++ ; 
                return BattleBotArena.FIREUP ;
              }
            case 4 -> {
                totalBulletsShot++ ; 
                return BattleBotArena.FIRERIGHT ;
              }
            case 5 -> {
                totalBulletsShot++ ;
                startShoot = false ; 
                return BattleBotArena.FIRELEFT ;
              }
        
        }
      }
      
      // Checking if the bot is stuck somewhere
      if(timeStamp > 300 && botPreviousPositions != null && botPositions != null && !lastDecision.isEmpty()){
        if(checkStuck(botPreviousPositions, botPositions, lastDecision)){
            return handleStuck(lastDecision) ; 
        }
      }
      // Checking if the bot can shoot 
      if(shotOK && bulletsShot < 2 ){
        // Finding a threatBot if there
        BotInfo threatBot = isBotNearby(me, liveBots);
        if(threatBot != null){
            // Determining in which direction the danger Bot is 
            shootDirection = dangerBotShootDirection(me, threatBot);
            if(shotOK){
                totalBulletsShot++ ; 
                bulletsShot++ ;
                edge = shotAtEdge(me) ;
                if (!edge.equals("NOT AT EDGE")) {
                    atEdge = true ; 
                }
                return stringToCommand(shootDirection) ;
            }
        }
      }
      String moveDecision = "STAY STILL" ; 
      // Looping through the array of closestBullets to escape from them 
      if(closestBullets != null){
        for(Bullet bullet : closestBullets){
                if(bullet != null){
                    double [] prevPosition = bulletPreviousPositions.get(bullet) ;
                    if(prevPosition != null){
                        String direction = getBulletDirection(bullet, prevPosition[0], prevPosition[1]);
                        String reaction = reactToBullet(bullet, direction, me) ; 
                        if(!reaction.equals("STAY STILL")){
                             dangerBot = checkMoveSafety(reaction, me, liveBots);
                            if(dangerBot != null){
                               danger = true ;   
                            }
                            bulletsShot = 0 ; 
                            moveDecision = reaction ; 
                            lastDecision = reaction ; 
                            break ; 
                        }
                    }
                }
                else{
                   // System.out.println("Error found at line 133");
                }
        }
    }
    else{
        System.out.println("Error found at line 135");
    }
      // Updating Bot positions
      if(botPositions != null){
        updateBotPosition(botPositions, timeStamp);
      }
      // Update Bullet Positions
      if(closestBullets != null){
      updateBulletTracking(closestBullets);
    }
      // Returning actions
      return stringToCommand(moveDecision) ; 
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
        return "Warriors";
      
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
    /*
     * This functions finds the two closest bullets from the bot on the play screen
     * @params - BotInfo me(All the information available for my bot), Bullet[] bullets(All the information available for all the bullets on the screen)
     * @returns - Returns a Array of Bullets which has the positions of the closest and second closest bullet.
     */
    public Bullet[] findTwoClosestBullets(BotInfo me , Bullet[] bullets){
        if(bullets.length == 0 ){
            return null ; 
        }
        // Finding the closest Bullet 
        Bullet closest = bothelper.findClosest(me, bullets) ;
        // if no Bullet on screen return empty array 
        if(closest == null){
            return new Bullet[]{null , null} ; 
        }
        List<Bullet> remainingBullets = new ArrayList<>(Arrays.asList(bullets));
        remainingBullets.remove(closest) ; 

        // Finding the second closest bullet 
        Bullet secondClosest = null ;
            if(!remainingBullets.isEmpty()){
                secondClosest = bothelper.findClosest(me, remainingBullets.toArray(new Bullet[0]));

            }
         return new Bullet[]{closest , secondClosest} ; 
    } 
    /*
     * This function updates the closestBullet Array frame as the closest Bullets change
     * @params - Bullet[] closestBullets(Contains the information about current closest bullets)
     * @returns - None 
     */
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
    /* 
    * This functions is used to find out the direction of a bullet 
    * @params- Bullet bullet(The bullet whose direction is to be find) , double prevX(Bullet's previous X-pos) , double prevY(Bullet's previous Y-Pos)
    * @returns - String containing the direction of the bullet
    */ 
    public String getBulletDirection(Bullet bullet , double prevX , double prevY){
        if(bullet != null){
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
        return "STAY" ;
    }   
    /*
     * This function is used to react to a bullet coming close
     * @params - Bullet bullet(Bullet against which action is to be taken) , String direction(Direction of the bullet) , BotInfo me(All information about my bot)
     */
    public String reactToBullet(Bullet bullet , String direction , BotInfo me){
        if(bullet != null){
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
    else{
        return "STAY STILL" ; 
    }
}
    /*
     * This function is used to clean the bulletPreviousPositions map in case any bullet is no more active on the screen
     * @params - Bullet[] currentBullets ( Information about all the bullets present on the screen)
     * @returns - None 
     */
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
    /* 
    * This function is used to make the bot act based on the input
    * @params - String reaction( String containing information on what to do)
    * @returns - Returns an int which is corresponded with a bot action on the screen
    */    
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
    /* 
    * This function is called when there more than 5 live bots on screen to find any bot nearby 
    * @params- BotInfo me(All information about my bot) , BotInfo liveBots(All information about all other bots on the screen)
    * @returns - BotInfo , information about the which is dangerly close to my bot.
    */    
    public BotInfo isBotNearby(BotInfo me , BotInfo[] liveBots){
        for(BotInfo otherBots : liveBots){
            if(otherBots != null && !otherBots.equals(me)){
              if(otherBots.getX() == me.getX() || otherBots.getY() == me.getY()){
                return otherBots ; 
              }
            }
        }
        return null ; 
    }
    /* 
     * This function is used to find in what direction the dangerBot is 
     * @params- BotInfo me(All info about my bot) , BotInfo targetBot(All info about the bot which is to be shot)
     * @returns - String , containing the direction in which the dangerBot is
     */
    public String dangerBotShootDirection(BotInfo me , BotInfo targetBot){
        if(Math.abs(targetBot.getX() - me.getX()) < 10 ){
            return (targetBot.getY() > me.getY()) ? "SHOOT DOWN" : "SHOOT UP" ;
        }
        else if (Math.abs(targetBot.getY() - me.getY()) < 10){
            return (targetBot.getX() > me.getX()) ? "SHOOT RIGHT" : "SHOOT LEFT" ;
        }
        return "DON'T SHOOT" ; 
    }  
    /*
     * This function is used to update the bot's position after every frame
     * @params- double[] botPositions , int timeStamp 
     * @returns - None 
     */
    public void updateBotPosition(double[] botPositions , int timeStamp){
       botPreviousPositions[0] = botPositions[0] ;
       botPreviousPositions[1] = botPositions[1] ; 
    }
    /*
     * This method checks if my bot is stuck somewhere
     * @params- double[] botPreviousPositions , doublep[] botPositions , String lastDecision(What was the bot supposed to do according to the last move)
     * @returns - Booelean , telling if the bot is stuck or not
     */
    public boolean checkStuck(double[] botPreviousPositions , double[] botPositions , String lastDecision){
        if(lastDecision.equals("STAY STILL")){
            return false ; 
        }
        else{
            if(lastDecision.equals("MOVE RIGHT") || lastDecision.equals("MOVE LEFT")){
                if(botPreviousPositions[0] == botPositions[0]){
                    return true ; 
                }
            }
            else if(lastDecision.equals("MOVE UP") || lastDecision.equals("MOVE DOWN")){
                if(botPreviousPositions[1] == botPositions[1]){
                    return true ; 
                }
            }
        }
        return false ;    
         
}
    /* 
    * This bot perfrom actions to handle a stuck bot
    * @params - String lastDecision ( What was the bot supposed to do in last move)
    * @returns - Int which corresponds to a action for the bot on the screen
    */
    public int handleStuck(String lastDecision){
        switch (lastDecision){
            case "MOVE UP" -> {
                return BattleBotArena.DOWN ;
            }
            case "MOVE DOWN" -> {
                return BattleBotArena.UP ;
            }
            case "MOVE LEFT" -> {
                return BattleBotArena.RIGHT ; 
            }
            case "MOVE RIGHT" -> {
                return BattleBotArena.LEFT ;
            } 
        }
        return BattleBotArena.FIREDOWN ;
    }
    /*
     * This function is used when there are less than 5 bots on the screen to find other live bots
     * @params - BotInfo me , BotInfo nearestBot
     * @returns - String ( where to go to find the bot)
     */
    public String goToBot(BotInfo me , BotInfo nearestBot){
      if(Math.abs(me.getX() - nearestBot.getX()) > Math.abs(me.getY() - nearestBot.getY())){
         if(me.getX() < nearestBot.getX()){
            return "MOVE RIGHT" ; 
         } else{
            return "MOVE RIGHT" ;
         }
      } else{
        if(me.getY() < nearestBot.getY()){
            return "MOVE DOWN" ;
        } else{
            return "MOVE UP" ;
        }
      }
    }
    /* 
    *This function is only used when there are less than 5 bots on the screen to find which direction to shoot to kill a bot
    * @params - BotInfo me , BotInfo nearestBot
    * @returns - String , containing in what direction to shoot on the screen 
    */
    public String killBot(BotInfo me , BotInfo nearestBot){
        if(me.getX() == nearestBot.getX()){
            if(me.getY() < nearestBot.getY()){
                return "SHOOT DOWN" ;
            } else{
                return "SHOOT UP" ;
            }
        } else if (me.getY() == nearestBot.getY()){
            if(me.getX() < nearestBot.getX()){
                return "SHOOT RIGHT" ;
            } else{
                return "SHOOT LEFT" ; 
            }
        }
        return null ; 
    }
    /* 
    *This function is used to check if the move that the bot is supposed to make safe or not
    * @params - String moveDecision , BotInfo me , BotInfo livebots[]
    * @returns - BotInfo object if there's a danger bot after the expected move is made 
    */
    public BotInfo checkMoveSafety (String moveDecision , BotInfo me , BotInfo livebots[]){
        double newX = me.getX() ; 
        double newY = me.getY() ; 
        switch(moveDecision){
            case "MOVE UP" -> newY += 3.0 ;
            case "MOVE DOWN" -> newY -= 3.0 ;
            case "MOVE RIGHT" -> newX += 3.0 ;
            case "MOVE LEFT" -> newX -= 3.0 ;        
        }
        for(BotInfo bots : livebots){
            double botX = bots.getX() ; 
            double botY = bots.getY() ; 
            if(Math.abs(botX - newX) < 1 || Math.abs(botY - newY) < 1 ){
                return bots ; 
            }
        }
        return null ; 
    }
    /* 
    *This function is used to find the direction of a bot
    * @params - BotInfo me , BotInfo targetBot
    * @returns - String , direction of the bot
    */
    public String getBotDirection(BotInfo me , BotInfo targetBot){
        if(Math.abs(targetBot.getX() - me.getX()) < 5 ){
            return (targetBot.getY() > me.getY()) ? "SHOOT DOWN" : "SHOOT UP" ;
        }
        else if (Math.abs(targetBot.getY() - me.getY()) < 5){
            return (targetBot.getX() > me.getX()) ? "SHOOT RIGHT" : "SHOOT LEFT" ;
        }
        return "DON'T SHOOT" ; 
    }
    /* 
     *This function is used to check if the bot has recently shot and if it is at an edge
    * @params - BotInfo me 
    * @returns - String , if it at any edge then that edge else return not at edge
    */
    public String shotAtEdge(BotInfo me){
       double meX = me.getX() ; 
       double meY = me.getY() ; 
       double diffX = BattleBotArena.RIGHT_EDGE - meX;
       double diffY = BattleBotArena.BOTTOM_EDGE - meY  ; 
       if(diffX > 695){return "AT LEFT EDGE" ; }
       else if(diffX < 5){return "AT RIGHT EDGE" ; }
       else if(diffY > 1295){return "AT TOP EDGE";}
       else if(diffY < 5 ){return "AT BOTTOM EDGE" ;}
       else{return "NOT AT EDGE" ;}
    }


}
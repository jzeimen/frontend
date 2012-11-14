package edu.mines.csci598.recycler.frontend;

import edu.mines.csci598.recycler.frontend.graphics.Line;
import edu.mines.csci598.recycler.frontend.graphics.Path;
import edu.mines.csci598.recycler.frontend.graphics.Sprite;
import edu.mines.csci598.recycler.frontend.utils.ComputerConstants;
import edu.mines.csci598.recycler.frontend.utils.GameConstants;
import edu.mines.csci598.recycler.frontend.utils.Log;

import java.util.Random;


/**
 * Created with IntelliJ IDEA.
 * User: Marshall
 * Date: 11/13/12
 * Time: 6:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ComputerPlayer {
    public ComputerHand primary;
    public Path p;
    private Random random;
    private double lastStrikeTime;
    private double lastStrikeDelay;
    public int targetRecyclable;


    public ComputerPlayer(){
        primary = new ComputerHand();
        random = new Random(System.currentTimeMillis());
        lastStrikeTime=0;
        lastStrikeDelay=0.25;
        targetRecyclable = 0;
    }
    public void updateAI(Recyclable r,double currentTimeSec){
        //Follow target recyclable
        followRecyclable(r,currentTimeSec);
        //Strike target recyclable
        strike(r,currentTimeSec);

    }
    public void followRecyclable(Recyclable r, double currentTimeSec){
        primary.getSprite().setY(r.getSprite().getY());
    }
    public boolean hasCollisionWithRecyclable(Recyclable r,double currentTime){
        boolean ret = false;
        return ret;
    }
    private void strike(Recyclable r, double currentTimeSec){
        if(r.getSprite().isTouchable()){
            if(currentTimeSec > lastStrikeTime + lastStrikeDelay){
                //Log.logInfo("hx="+primary.getSprite().getX()+",hy"+primary.getSprite().getY()+
                //            ",hsx="+primary.getSprite().getScaledX()+",hsy"+primary.getSprite().getScaledY()+
                //            ",rx"+r.getSprite().getX()+",ry"+r.getSprite().getY()+
                //            ",rsx="+r.getSprite().getScaledX()+",rsy="+r.getSprite().getScaledY());
                if(ICanStrike()){
                    Log.logInfo("Strike");
                    strikeRecyclable(r,currentTimeSec);
                    lastStrikeDelay=ComputerConstants.LAST_STRIKE_UPDATE;
                    lastStrikeTime = currentTimeSec;
                }else {
                    lastStrikeDelay+=ComputerConstants.LAST_STRIKE_UPDATE;
                }
            }
        }
    }
    public boolean ICanStrike(){
        boolean ret = false;
        int rand = random.nextInt(ComputerConstants.MAX_GENERATION_NUMBER)+1;
        if(rand > ComputerConstants.MIN_GENERATION_THRESHOLD){
            ret = true;
        }
        return ret;
    }
    public void strikeRecyclable(Recyclable r,double currentTimeSec){
        int newX = r.getSprite().getX();
        int newY = r.getSprite().getY();

        if(newX<primary.getSprite().getX()){
            handleCollision(r,currentTimeSec,newX,-1 * ComputerConstants.HAND_X_OFFSET_FROM_CONVEYER);
        } else {
            handleCollision(r, currentTimeSec, newX, ComputerConstants.HAND_X_OFFSET_FROM_CONVEYER);
        }
    }
    private void handleCollision(Recyclable r,double currentTimeSec,int newX, int pathOffset){
        Path path = new Path();
        Line collideLine;
        primary.getSprite().setX(newX + pathOffset);
        if(pathOffset>0){
            Log.logInfo("INFO: Pushed Right");
            r.setMotionState(Recyclable.MotionState.FALL_RIGHT);
            collideLine = new Line(r.getSprite().getX(), r.getSprite().getY(),
                    r.getSprite().getX() + GameConstants.ITEM_PATH_END, r.getSprite().getY(),
                    GameConstants.ITEM_PATH_TIME);
        } else {
            Log.logInfo("INFO: Pushed Left");
            r.setMotionState(Recyclable.MotionState.FALL_LEFT);
            collideLine = new Line(r.getSprite().getX(), r.getSprite().getY(),
                    r.getSprite().getX() - GameConstants.ITEM_PATH_END, r.getSprite().getY(),
                    GameConstants.ITEM_PATH_TIME);
        }
        path.addLine(collideLine);
        r.getSprite().setPath(path);
        r.getSprite().setStartTime(currentTimeSec);
        r.getSprite().setState(Sprite.TouchState.UNTOUCHABLE);
    }
}
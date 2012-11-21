package edu.mines.csci598.recycler.frontend.motion;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.mines.csci598.recycler.frontend.Recyclable;

/**
 * Manages a list of items and causes them to move on demand.
 * @author Oliver
 *
 */
public abstract class ItemMover {
	
	protected List<Recyclable> recyclables;
	private double lastMotionTimeSec;
	protected double speedPixPerSecond;
	
	public ItemMover(double initialSpeed){
		recyclables = new ArrayList<Recyclable>();
		lastMotionTimeSec = 0;
		speedPixPerSecond = initialSpeed;
	}
	
	/**
	 * Given the current time, moves the items under the ItemMover's control
	 * @param currentTimeSec
	 */
	//public abstract void moveItems(double currentTimeSec);
	public void moveItems(double currentTimeSec){
		// Figure out how much time has passed since we last moved
		double timePassedSec = currentTimeSec - lastMotionTimeSec;
		
		for(Recyclable r : recyclables){
			Point2D newPosition = r.getPath().getLocation(r.getPosition(), speedPixPerSecond, timePassedSec); 
			r.setPosition(newPosition);
		}
		
		lastMotionTimeSec = currentTimeSec;
	}

	/**
	 * @return the list of items this ItemMover moves
	 */
    public final List<Recyclable> getRecyclables() {
        return recyclables;
    }
    
    /**
     * Registers the given recyclable with this ItemMover.  It will now be moved with each call to moveItems
     * @param r
     */
    public final void takeControlOfRecyclable(Recyclable r){
    	recyclables.add(r);
    }
    
    /**
     * Transfers ownership of any items managed by this ItemMover that are at the end of their path.
     * The ItemMover will no longer be aware of these items after calling this method.
     * @return The items no longer owned by this ItemMover
     */
    public final List<Recyclable> releaseControlOfRecyclablesAtEndOfPath(){
    	List<Recyclable> atEnd = new ArrayList<Recyclable>();
    	for(Recyclable r : recyclables){
    		if(r.getPosition().equals(r.getPath().finalPosition())){
    			atEnd.add(r);
    		}
    	}
    	for(Recyclable r : atEnd){
    		recyclables.remove(r);
    	}
    	return atEnd;
    }
}

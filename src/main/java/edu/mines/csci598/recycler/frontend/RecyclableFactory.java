package edu.mines.csci598.recycler.frontend;

import java.util.Random;

import org.apache.log4j.Logger;

import edu.mines.csci598.recycler.frontend.graphics.Path;
import edu.mines.csci598.recycler.frontend.utils.GameConstants;

/**
 * A factory to create Recyclables
 * Created with IntelliJ IDEA.
 * User: jzeimen
 * Date: 10/21/12
 * Time: 12:22 PM
 * To change this template use File | Settings | File Templates.
 */
public final class RecyclableFactory {    
    private static final Logger logger = Logger.getLogger(RecyclableFactory.class);
    
    private static final Random rand = new Random();
	private static double nextTimeToGenerate = 0;
    private static double meanTimeBetweenItemGeneration = GameConstants.MIN_TIME_BETWEEN_GENERATIONS;
    private static Path outputPath;
    private static int numberOfItemTypes = 1;

    /**
     * Returns a random RecyclableType of the first <em>numberOfItemTypesInUse</em> types.
     * If <em>numberOfItemTypesInUse</em> is greater than the total number of items in the game,
     * the total number is used instead.
     *
     * @param numberOfItemTypesInUse
     * @return
     */
    // TODO this shouldn't really depend on the order we typed items above
    private static Recyclable generateRandom(Path path, int numberOfItemTypesInUse) {
        int numRecyclableTypes = RecyclableType.values().length;

        if (numberOfItemTypesInUse > numRecyclableTypes) { // error checking, just use the number of recyclables instead
            numberOfItemTypesInUse = numRecyclableTypes;
        }

        // Generates a choice between 0 and the number of items in use passed in, or the total number available
        int randomChoiceIndex = rand.nextInt(numberOfItemTypesInUse);
        RecyclableType type = RecyclableType.values()[randomChoiceIndex];
		String[] possibleImages = type.getImagePaths();
        return new Recyclable(type, path, possibleImages[(int)(Math.floor(Math.random() * possibleImages.length))]);
    }

    private static Recyclable generateRandomPowerUp(Path path) {
        int randomChoiceIndex = rand.nextInt(3) + 5;
        RecyclableType type = RecyclableType.values()[randomChoiceIndex];
        String[] possibleImages = type.getImagePaths();
        return new Recyclable(type, path, possibleImages[0]);
    }
    


	/**
	 * Generates new item if necessary
	 * @return A new recyclable if it's been long enough and we get lucky, null otherwise
	 */
	public static Recyclable possiblyGenerateItem(double currentTimeSec) {
		Recyclable returned = null;
		if (needsItemGeneration(currentTimeSec)) {
            // a very simple way to have powerups not generated frequently
            Random randomGenerator = new Random();
            int ranNum = randomGenerator.nextInt(100);
            if (ranNum < GameConstants.POWERUP_FREQUENCY_PERCENTAGE) {
            	returned = RecyclableFactory.generateRandomPowerUp(outputPath);
                logger.debug("Powerup generated: " + returned);
            }
            else {
                // Recyclables initially take the path of the conveyor belt
            	returned = RecyclableFactory.generateRandom(outputPath, numberOfItemTypes);
                logger.debug("Item generated: " + returned);
            }
		}
		return returned;
    }
	
    /**
     * Determines if item should be generated.  If true, sets the time for the next object to be generated
     *
     * @return true if item should be generated, false otherwise
     */
    private static boolean needsItemGeneration(double currentTimeSec) {
		if (currentTimeSec > nextTimeToGenerate) {
			double timeToAdd = rand.nextGaussian()
					+ meanTimeBetweenItemGeneration;
			nextTimeToGenerate = currentTimeSec + Math.max(timeToAdd, GameConstants.MIN_TIME_BETWEEN_GENERATIONS);
			
			logger.debug("Current time: " + currentTimeSec);
			logger.debug("Time for next item generation: " + nextTimeToGenerate);
			
			return true;
		}
		return false;
    }
    
    /**
     * Sets the path that any items generated will follow
     * @param p the path items will follow
     */
    public static void setOutputPath(Path p){
    	outputPath = p;
    }

	public static void setNumItemTypesInUse(int numItemTypesInUse) {
		numberOfItemTypes = numItemTypesInUse;
	}
}

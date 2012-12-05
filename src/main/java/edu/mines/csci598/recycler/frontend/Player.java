package edu.mines.csci598.recycler.frontend;


import org.apache.log4j.Logger;

import edu.mines.csci598.recycler.backend.GameManager;
import edu.mines.csci598.recycler.frontend.hands.Hand;

/**
 * A player class contains the players hands and in the future will keep track of
 * other player specific things.
 * <p/>
 * Created with IntelliJ IDEA.
 * User: jzeimen
 * Date: 10/20/12
 * Time: 9:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class Player {

    private static final Logger logger = Logger.getLogger(Player.class);
    
    Hand primary;
    Hand auxiliary;

    public Player(GameManager manager) {

    }

}

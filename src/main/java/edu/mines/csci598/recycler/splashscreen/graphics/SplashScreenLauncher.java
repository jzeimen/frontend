package edu.mines.csci598.recycler.splashscreen.graphics;

import edu.mines.csci598.recycler.backend.GameManager;
import edu.mines.csci598.recycler.backend.GameState;
import edu.mines.csci598.recycler.backend.ModalMouseMotionInputDriver;
import edu.mines.csci598.recycler.bettyCrocker.Song;
import edu.mines.csci598.recycler.bettyCrocker.Track;
import edu.mines.csci598.recycler.frontend.GameLauncher;
import edu.mines.csci598.recycler.splashscreen.footers.TwitterFooter;
import edu.mines.csci598.recycler.splashscreen.footers.WeatherFooter;
import edu.mines.csci598.recycler.splashscreen.headers.InstructionHeader;
import edu.mines.csci598.recycler.splashscreen.playerdetector.DetectHand;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SplashScreenLauncher extends GameState {

    private GameManager gameManager;
    private List<SplashScreenSection> staticSections;
    private List<SplashScreenSection> cyclingSections;
    private boolean refreshScreen;
    private int currentCyclingSectionIndex;
    private Song song;
    private DetectHand detector;

    private static final int HEADER_HEIGHT = 150;
    private static final int FOOTER_HEIGHT = 100;
    private static final int MILLISECOND_TO_SECOND = 1000;

    public static void main(String[] args) {
        SplashScreenLauncher launcher = new SplashScreenLauncher();
        launcher.getGameManager().setState(launcher);
        launcher.getGameManager().run();
        launcher.getGameManager().destroy();
    }

    public SplashScreenLauncher() {
        gameManager = new GameManager("SplashScreen", false);
        detector = new DetectHand( MILLISECOND_TO_SECOND, gameManager );

        song = new Song();
        song.addTrack(new Track("src/main/resources/Sounds/root_beer_float.mp3"));
        song.startPlaying(true);

        Component canvas = gameManager.getCanvas();
        int screenWidth = canvas.getWidth();
        int screenHeight = canvas.getHeight();

        staticSections = new ArrayList<SplashScreenSection>();
        SplashScreenSection instructionsSection = new InstructionHeader(gameManager.getCanvas());
        instructionsSection.initialize(new Point(0, 0), new Point(screenWidth, HEADER_HEIGHT), updateScreenCallback, cycleScreenCallback);
        staticSections.add(instructionsSection);

        SplashScreenSection twitterSection = new TwitterFooter();
        twitterSection.initialize(new Point(0, screenHeight - FOOTER_HEIGHT), new Point(screenWidth / 2, screenHeight), updateScreenCallback, cycleScreenCallback);
        staticSections.add(twitterSection);

        SplashScreenSection weatherSection = new WeatherFooter();
        weatherSection.initialize(new Point(screenWidth / 2, screenHeight - FOOTER_HEIGHT), new Point(screenWidth, screenHeight), updateScreenCallback, cycleScreenCallback);
        staticSections.add(weatherSection);

        cyclingSections = new ArrayList<SplashScreenSection>();
        SplashScreenSection highScoreSection = new HighScoreScreen();
        highScoreSection.initialize(new Point(0, HEADER_HEIGHT), new Point(screenWidth, screenHeight - FOOTER_HEIGHT), updateScreenCallback, cycleScreenCallback);
        cyclingSections.add(highScoreSection);

        currentCyclingSectionIndex = 0;
        refreshScreen = true;
    }

    UpdateScreenCallback updateScreenCallback = new UpdateScreenCallback() {
        @Override
        public void updateScreen() {
            refreshScreen = true;
        }
    };

    CycleScreenCallback cycleScreenCallback = new CycleScreenCallback() {
        @Override
        public void cycleScreen(SplashScreenSection currentSection) {
            if (cyclingSections.contains(currentSection)) {
                int nextSectionIndex = (cyclingSections.indexOf(currentSection) + 1) % cyclingSections.size();
                SplashScreenSection nextSection = cyclingSections.get(nextSectionIndex);

                currentSection.stop();

                if( detector.startGame() ){
                    song.stopPlaying();
                    GameLauncher gm = new GameLauncher();
                    ModalMouseMotionInputDriver mouse = new ModalMouseMotionInputDriver();
                    gm.getGameManager().installInputDriver(mouse);
                    gm.getGameManager().setState(gm);
                    gm.getGameManager().run();
                    gm.getGameManager().destroy();

                    //gm = new GameManager( "SplashScreen", false );
                }

                // CODE USED TO CYCLE TO NEXT SCREEN
                // nextSection.initialize(new Point(...), new Point(...), updateScreenCallback, cycleScreenCallback);

                // currentCyclingSectionIndex = nextSectionIndex;
                // refreshScreen = true;
            }
        }
    };

    @Override
    protected GameState updateThis(float elapsedTime) {
        return this;
    }

    @Override
    protected void drawThis(Graphics2D g) {
        if (refreshScreen) {
            refreshScreen = false;

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setBackground(new Color(0, 0, 0, 0));
            g.clearRect(0, 0, gameManager.getCanvas().getWidth(), gameManager.getCanvas().getHeight());

            for (SplashScreenSection section : staticSections) {
                section.draw(g);
            }

            cyclingSections.get(currentCyclingSectionIndex).draw(g);
        }
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}

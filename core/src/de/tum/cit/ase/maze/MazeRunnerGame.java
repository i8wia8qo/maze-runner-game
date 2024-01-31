package de.tum.cit.ase.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

import java.util.HashMap;
import java.util.Map;

/**
 * The MazeRunnerGame class represents the core of the Maze Runner game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class MazeRunnerGame extends Game {
    // Screens
    private MenuScreen menuScreen;
    private GameScreen gameScreen;

    private GameOverScreen gameOverScreen;

    private VictoryScreen victoryScreen;

    // public static final int V_WIDTH = 400;
    public static final int V_WIDTH = 800;
    // public static final int V_HEIGHT = 208;
    public static final int V_HEIGHT = 416;



    Stage uiStage;
    Viewport uiViewport;
    OrthographicCamera uiCamera;

    // Sprite Batch for rendering
    private SpriteBatch spriteBatch;

    private Music backgroundMusic;
    private Music excitingMusic;
    private Music chillMusic;

    private Sound collectKeySound;
    private Sound receiveDamageSound;
    private Sound gameOverSound;
    private Sound victorySound;




    // UI Skin
    private Skin skin;

    private NativeFileChooser fileChooser;

    // Character animation downwards
    private Animation<TextureRegion> characterDownAnimation;
    private Animation<TextureRegion> characterRightAnimation;
    private Animation<TextureRegion> characterUpAnimation;
    private Animation<TextureRegion> characterLeftAnimation;

    private Animation<TextureRegion> characterFightDownAnimation;
    private Animation<TextureRegion> characterFightRightAnimation;
    private Animation<TextureRegion> characterFightUpAnimation;
    private Animation<TextureRegion> characterFightLeftAnimation;

    private Animation<TextureRegion> ghostUpAnimation;
    private Animation<TextureRegion> ghostDownAnimation;
    private Animation<TextureRegion> ghostLeftAnimation;
    private Animation<TextureRegion> ghostRightAnimation;

    private TextureRegion heartFull;
    private TextureRegion heartThreeQuarterFull;
    private TextureRegion heartHalfFull;
    private TextureRegion heartOneQuarterFull;
    private TextureRegion heartEmpty;

    private Animation<TextureRegion> trapStraightGrayAnimation;
    private Animation<TextureRegion> trapStraightGoldAnimation;
    private Animation<TextureRegion> trapSkewedGrayAnimation;
    private Animation<TextureRegion> trapSkewedGoldAnimation;


    private Animation<TextureRegion> doorOpen;
    private Animation<TextureRegion> keyChestOpen;
    private Animation<TextureRegion> ghostPoof;

    private int levelCounter;

    private int numberOfLevelsInCampaign;

    private boolean costomMapLoaded = false;

    private Map<Integer, String> levelPaths = new HashMap<>();


    // Sound effect constants
    public static final int SOUND_COLLECT_KEY = 1;
    public static final int SOUND_RECEIVE_DAMAGE = 2;
    public static final int SOUND_GAME_OVER = 3;
    public static final int SOUND_VICTORY = 4;

    private boolean isPaused = false;

    /**
     * Constructor for MazeRunnerGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
        this.fileChooser = fileChooser;
    }

    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     */
    @Override
    public void create() {
        /*uiCamera = new OrthographicCamera();
        uiViewport = new ScreenViewport(uiCamera);
        uiStage = new Stage(uiViewport);*/

        spriteBatch = new SpriteBatch(); // Create SpriteBatch
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json")); // Load UI skin
        this.loadCharacterAnimation(); // Load character animation

        // Play some background music
        // Background sound
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background.mp3"));
        excitingMusic = Gdx.audio.newMusic(Gdx.files.internal("excitingBackgroundMusic.mp3"));
        chillMusic = Gdx.audio.newMusic(Gdx.files.internal("spookyChillBackgroundMusic.mp3"));

        collectKeySound = Gdx.audio.newSound(Gdx.files.internal("collextKeySFX.mp3"));
        receiveDamageSound = Gdx.audio.newSound(Gdx.files.internal("damageSFX.mp3"));
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("gameOverSFX.mp3"));
        victorySound = Gdx.audio.newSound(Gdx.files.internal("victorySFX.mp3"));

        backgroundMusic.setLooping(true);
        // Uncomment this if you want music to play:
        backgroundMusic.play();


        levelCounter = 1;
        numberOfLevelsInCampaign = 5;
        levelPaths.put(1, "maps/level-1.properties");
        levelPaths.put(2, "maps/level-2.properties");
        levelPaths.put(3, "maps/level-3.properties");
        levelPaths.put(4, "maps/level-4.properties");
        levelPaths.put(5, "maps/level-5.properties");

        goToMenu(); // Navigate to the menu screen
    }

    /**
     * Plays a specific sound effect based on the provided sound type.
     * This method manages the audio feedback for different game events,
     * such as collecting a key, receiving damage, game over, and victory.
     *
     * @param sound The integer identifier for the sound effect to be played.
     *              This should be one of the predefined constants (e.g., SOUND_COLLECT_KEY).
     */
    public void playSoundEffect(int sound) {
        switch (sound) {
            case SOUND_COLLECT_KEY:
                collectKeySound.play();
                break;
            case SOUND_RECEIVE_DAMAGE:
                receiveDamageSound.play();
                break;
            case SOUND_GAME_OVER:
                gameOverSound.play();
                break;
            case SOUND_VICTORY:
                victorySound.play();
                break;
            default:
                System.out.println("Sound effect not recognized: " + sound);
                break;
        }
    }

    /**
     * Changes the background music to a new track specified by the file path.
     * If music is currently playing, it stops and disposes of the current track before
     * loading and playing the new one. The new track will loop continuously.
     *
     * @param newMusicFilePath The path to the new music file to be played.
     */
    public void changeMusic(String newMusicFilePath) {
        if (backgroundMusic != null) {
            backgroundMusic.stop(); // Stop current music
            backgroundMusic.dispose(); // Release resources
        }
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(newMusicFilePath));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();
    }

    /**
     * Transitions the game to the menu screen. This method is typically called
     * when the player navigates back to the main menu, either from the game or from
     * another screen. It ensures the music is set appropriately for the menu.
     */
    public void goToMenu() {
        changeMusic("spookyChillBackgroundMusic.mp3");
        this.menuScreen = new MenuScreen(this);
        this.setScreen(menuScreen); // Set the current screen to MenuScreen

        /*if(!isPaused){
            if (gameScreen != null) {
                gameScreen.dispose(); // Dispose the game screen if it exists
                gameScreen = null;
            }
        }*/


        if (gameOverScreen != null) {
            gameOverScreen.dispose(); // Dispose the game screen if it exists
            gameOverScreen = null;
        }
    }

    /**
     * Displays the pause menu. This method is called when the game is paused,
     * showing a menu that allows the player to resume the game or navigate to other options.
     */
    public void showPauseMenu() {
        setScreen(new MenuScreen(this));  // Assuming MenuScreen is your pause menu
    }

    /**
     * Switches to the game screen, loading the next level or restarting the current one.
     * This method disposes of the current game screen if it exists and initializes a new
     * game screen with the specified level or map.
     */
    public void goToGame() {

        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the current game screen
            gameScreen = null;
        }

        if(costomMapLoaded){
            levelCounter = levelCounter -1;
            costomMapLoaded = false;
            goToMenu();
        } else {
            if(levelCounter <1){
                levelCounter = 1;
            } else if (levelCounter > numberOfLevelsInCampaign + 1) {
                levelCounter = 1;
            } else if (levelCounter == numberOfLevelsInCampaign + 1){
                levelCounter = 1;
                goToVictory();
            } else {

                String filePath = levelPaths.get(levelCounter);
                Gdx.app.log("Game", "About to load map: " + true);
                this.gameScreen = new GameScreen(this, filePath);
                this.setScreen(gameScreen); // Set the current screen to GameScreen
                if (menuScreen != null) {
                    menuScreen.dispose(); // Dispose the menu screen if it exists
                    menuScreen = null;
                }
            }
        }


    }

    /**
     * Loads a game map from the specified file path and initializes a new game screen
     * with this map. This method is used when loading custom maps or transitioning between levels.
     *
     * @param filePath The path to the map file to be loaded.
     */

    public void loadGameMap(String filePath) {
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the current game screen
            gameScreen = null;
        }

        costomMapLoaded = true;

        Gdx.app.log("Game", "About to load map: " + true);
        this.gameScreen = new GameScreen(this, filePath);
        this.setScreen(gameScreen); // Set the current screen to GameScreen
        if (menuScreen != null) {
            menuScreen.dispose(); // Dispose the menu screen if it exists
            menuScreen = null;

        }
    }

    /**
     * Transitions the game to the game over screen. This method is called when the player
     * loses all lives or fails a level, and it plays the appropriate sound effect for game over.
     */
    public void goToGameOver() {

        playSoundEffect(3);
        levelCounter = 1;
        Gdx.app.log("Game", "goToGameOver(): " + true);

        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the current game screen
            gameScreen = null;
        }

        // Create and set the game over screen
        this.gameOverScreen = new GameOverScreen(this);
        this.setScreen(gameOverScreen);
    }

    /**
     * Transitions the game to the victory screen. This method is called when the player
     * completes the final level or achieves a significant milestone, playing a victory sound effect.
     */
    public void goToVictory() {

        playSoundEffect(4);
        levelCounter = 1;
        Gdx.app.log("Game", "goToGameOver(): " + true);

        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the current game screen
            gameScreen = null;
        }

        // Create and set the game over screen
        this.victoryScreen = new VictoryScreen(this);
        this.setScreen(victoryScreen);
    }

    /**
     * Loads the character animation from the character.png file.
     */
    private void loadCharacterAnimation() {
        Texture walkSheet = new Texture(Gdx.files.internal("character.png"));
        Texture fightSheet = new Texture(Gdx.files.internal("character.png"));
        Texture mobSheet = new Texture(Gdx.files.internal("mobs.png"));
        Texture thingsSheet = new Texture(Gdx.files.internal("things.png"));
        Texture objectsSheet = new Texture(Gdx.files.internal("objects.png"));
        TextureRegion[][] objects;

        int frameWidth = 16;
        int frameHeight = 32;
        int frameHeightMobsAndThings = 16;
        int initialOffsetX = 8;
        int initialGostOffsetX = 6*16;
        int initialTrapOffsetX = 6*16;
        int frameSpacingX = 16;
        int animationFrames = 4;
        int animationFramesMobsAndThings = 3;

        objects = TextureRegion.split(objectsSheet, 16, 16);
        this.heartFull = objects[0][4];
        this.heartThreeQuarterFull = objects[0][5];
        this.heartHalfFull = objects[0][6];
        this.heartOneQuarterFull = objects[0][7];
        this.heartEmpty = objects[0][8];

        // libGDX internal Array instead of ArrayList because of performance
        Array<TextureRegion> walkFramesDown = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkFramesRight = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkFramesUp = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkFramesLeft = new Array<>(TextureRegion.class);

        Array<TextureRegion> fightFramesDown = new Array<>(TextureRegion.class);
        Array<TextureRegion> fightFramesRight = new Array<>(TextureRegion.class);
        Array<TextureRegion> fightFramesUp = new Array<>(TextureRegion.class);
        Array<TextureRegion> fightFramesLeft = new Array<>(TextureRegion.class);

        Array<TextureRegion> ghostFramesDown = new Array<>(TextureRegion.class);
        Array<TextureRegion> ghostFramesRight = new Array<>(TextureRegion.class);
        Array<TextureRegion> ghostFramesUp = new Array<>(TextureRegion.class);
        Array<TextureRegion> ghostFramesLeft = new Array<>(TextureRegion.class);

        Array<TextureRegion> trapStraightGrayFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> trapStraightGoldFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> trapSkewedGrayFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> trapSkewedGoldFrames = new Array<>(TextureRegion.class);

        Array<TextureRegion> doorOpenFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> keyChestOpenFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> ghostPoofFrames = new Array<>(TextureRegion.class);


        // Add all frames to the animation

        // PLAYER WALKING
        for (int col = 0; col < animationFrames; col++) {
            walkFramesDown.add(new TextureRegion(walkSheet, col * frameWidth, 0*frameHeight, frameWidth, frameHeight));
        }

        for (int col = 0; col < animationFrames; col++) {
            walkFramesRight.add(new TextureRegion(walkSheet, col * frameWidth, 1*frameHeight, frameWidth, frameHeight));
        }

        for (int col = 0; col < animationFrames; col++) {
            walkFramesUp.add(new TextureRegion(walkSheet, col * frameWidth, 2*frameHeight, frameWidth, frameHeight));
        }

        for (int col = 0; col < animationFrames; col++) {
            walkFramesLeft.add(new TextureRegion(walkSheet, col * frameWidth, 3*frameHeight, frameWidth, frameHeight));
        }

        // PLAYER FIGHTING
        for (int col = 0; col < animationFrames; col++) {
            fightFramesDown.add(new TextureRegion(fightSheet, initialOffsetX + col * (frameWidth + frameSpacingX), 4*frameHeight, frameWidth, frameHeight));
        }

        for (int col = 0; col < animationFrames; col++) {
            fightFramesUp.add(new TextureRegion(fightSheet, initialOffsetX + col * (frameWidth + frameSpacingX), 5*frameHeight, frameWidth, frameHeight));
        }

        for (int col = 0; col < animationFrames; col++) {
            fightFramesRight.add(new TextureRegion(fightSheet, initialOffsetX + col * (frameWidth + frameSpacingX), 6*frameHeight, frameWidth, frameHeight));
        }

        for (int col = 0; col < animationFrames; col++) {
            fightFramesLeft.add(new TextureRegion(fightSheet, initialOffsetX + col * (frameWidth + frameSpacingX), 7*frameHeight, frameWidth, frameHeight));
        }

        // GHOST FLOATING
        for (int col = 0; col < animationFramesMobsAndThings; col++) {
            ghostFramesUp.add(new TextureRegion(mobSheet, initialGostOffsetX + col * frameWidth, 7*frameHeightMobsAndThings, frameWidth, frameHeightMobsAndThings));
        }

        for (int col = 0; col < animationFramesMobsAndThings; col++) {
            ghostFramesDown.add(new TextureRegion(mobSheet, initialGostOffsetX + col * frameWidth, 4*frameHeightMobsAndThings, frameWidth, frameHeightMobsAndThings));
        }

        for (int col = 0; col < animationFramesMobsAndThings; col++) {
            ghostFramesLeft.add(new TextureRegion(mobSheet, initialGostOffsetX + col * frameWidth, 5*frameHeightMobsAndThings, frameWidth, frameHeightMobsAndThings));
        }

        for (int col = 0; col < animationFramesMobsAndThings; col++) {
            ghostFramesRight.add(new TextureRegion(mobSheet, initialGostOffsetX + col * frameWidth, 6*frameHeightMobsAndThings, frameWidth, frameHeightMobsAndThings));
        }


        // TRAP TRAPPING
        for (int col = 0; col < animationFramesMobsAndThings; col++) {
            trapStraightGrayFrames.add(new TextureRegion(thingsSheet, initialTrapOffsetX + col * frameWidth, 4*frameHeightMobsAndThings, frameWidth, frameHeightMobsAndThings));
        }

        for (int col = 0; col < animationFramesMobsAndThings; col++) {
            trapStraightGoldFrames.add(new TextureRegion(thingsSheet, initialTrapOffsetX + col * frameWidth, 5*frameHeightMobsAndThings, frameWidth, frameHeightMobsAndThings));
        }

        for (int col = 0; col < animationFramesMobsAndThings; col++) {
            trapSkewedGrayFrames.add(new TextureRegion(thingsSheet, initialTrapOffsetX + col * frameWidth, 6*frameHeightMobsAndThings, frameWidth, frameHeightMobsAndThings));
        }

        for (int col = 0; col < animationFramesMobsAndThings; col++) {
            trapSkewedGoldFrames.add(new TextureRegion(thingsSheet, initialTrapOffsetX + col * frameWidth, 7*frameHeightMobsAndThings, frameWidth, frameHeightMobsAndThings));
        }

        // DOOR OPENING
        for (int col = 0; col < 4; col++) {
            doorOpenFrames.add(new TextureRegion(thingsSheet, 0 * frameWidth, col*frameHeightMobsAndThings, frameWidth, frameHeightMobsAndThings));
        }

        // KEY CHEST OPENING
        for (int col = 0; col < 4; col++) {
            keyChestOpenFrames.add(new TextureRegion(thingsSheet, 6 * frameWidth, col*frameHeightMobsAndThings, frameWidth, frameHeightMobsAndThings));
        }

        // GHOST POOF // 7 Frames // W:32px, H:32px
        for (int col = 0; col < 8; col++) {
            ghostPoofFrames.add(new TextureRegion(objectsSheet, 64+col*64, 64, 32, 32));
        }


        characterDownAnimation = new Animation<>(0.1f, walkFramesDown);
        characterRightAnimation = new Animation<>(0.1f, walkFramesRight);
        characterUpAnimation = new Animation<>(0.1f, walkFramesUp);
        characterLeftAnimation = new Animation<>(0.1f, walkFramesLeft);

        characterFightDownAnimation = new Animation<>(0.1f, fightFramesDown);
        characterFightRightAnimation = new Animation<>(0.1f, fightFramesRight);
        characterFightUpAnimation = new Animation<>(0.1f, fightFramesUp);
        characterFightLeftAnimation = new Animation<>(0.1f, fightFramesLeft);

        ghostUpAnimation = new Animation<>(0.1f, ghostFramesUp);
        ghostDownAnimation = new Animation<>(0.1f, ghostFramesDown);
        ghostLeftAnimation = new Animation<>(0.1f, ghostFramesLeft);
        ghostRightAnimation = new Animation<>(0.1f, ghostFramesRight);

        trapStraightGrayAnimation = new Animation<>(0.1f, trapStraightGrayFrames);
        trapStraightGoldAnimation = new Animation<>(0.1f, trapStraightGoldFrames);
        trapSkewedGrayAnimation = new Animation<>(0.1f, trapSkewedGrayFrames);
        trapSkewedGoldAnimation = new Animation<>(0.1f, trapSkewedGoldFrames);

        doorOpen = new Animation<>(0.1f, doorOpenFrames);
        keyChestOpen = new Animation<>(0.1f, keyChestOpenFrames);
        ghostPoof = new Animation<>(0.1f, ghostPoofFrames);
    }

    /**
     * Updates the game's viewport size based on the screen size changes.
     * This method ensures that the game's UI elements and graphics scale correctly
     * when the window size is adjusted.
     *
     * @param width The new width of the screen.
     * @param height The new height of the screen.
     */
    public void resize(int width, int height) {
        // Update the viewport when the screen size changes
        //uiViewport.update(width, height, true);
    }

    /**
     * Cleans up resources when the game is disposed.
     */
    @Override
    public void dispose() {
        getScreen().hide(); // Hide the current screen
        getScreen().dispose(); // Dispose the current screen
        spriteBatch.dispose(); // Dispose the spriteBatch
        skin.dispose(); // Dispose the skin
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
        if (excitingMusic != null) {
            excitingMusic.dispose();
        }
        if (chillMusic != null) {
            chillMusic.dispose();
        }

        if (collectKeySound != null) {
            collectKeySound.dispose();
        }
        if (receiveDamageSound != null) {
            receiveDamageSound.dispose();
        }
        if (gameOverSound != null) {
            gameOverSound.dispose();
        }
        if (victorySound != null) {
            victorySound.dispose();
        }
    }

    // Getter methods
    public Skin getSkin() {
        return skin;
    }



    public Animation<TextureRegion> getCharacterDownAnimation() {
        return characterDownAnimation;
    }

    public Animation<TextureRegion> getCharacterRightAnimation() {
        return characterRightAnimation;
    }

    public Animation<TextureRegion> getCharacterUpAnimation() {
        return characterUpAnimation;
    }

    public Animation<TextureRegion> getCharacterLeftAnimation() {
        return characterLeftAnimation;
    }

    public Animation<TextureRegion> getCharacterFightDownAnimation() {
        return characterFightDownAnimation;
    }

    public Animation<TextureRegion> getCharacterFightRightAnimation() {
        return characterFightRightAnimation;
    }

    public Animation<TextureRegion> getCharacterFightUpAnimation() {
        return characterFightUpAnimation;
    }

    public Animation<TextureRegion> getCharacterFightLeftAnimation() {
        return characterFightLeftAnimation;
    }

    public Animation<TextureRegion> getGhostUpAnimation() {
        return ghostUpAnimation;
    }

    public Animation<TextureRegion> getGhostDownAnimation() {
        return ghostDownAnimation;
    }

    public Animation<TextureRegion> getGhostLeftAnimation() {
        return ghostLeftAnimation;
    }

    public Animation<TextureRegion> getGhostRightAnimation() {
        return ghostRightAnimation;
    }

    public Animation<TextureRegion> getTrapStraightGrayAnimation() {
        return trapStraightGrayAnimation;
    }

    public Animation<TextureRegion> getTrapStraightGoldAnimation() {
        return trapStraightGoldAnimation;
    }

    public Animation<TextureRegion> getTrapSkewedGrayAnimation() {
        return trapSkewedGrayAnimation;
    }

    public Animation<TextureRegion> getTrapSkewedGoldAnimation() {
        return trapSkewedGoldAnimation;
    }

    public TextureRegion getHeartFull() {
        return heartFull;
    }

    public TextureRegion getHeartThreeQuarterFull() {
        return heartThreeQuarterFull;
    }

    public TextureRegion getHeartHalfFull() {
        return heartHalfFull;
    }

    public TextureRegion getHeartOneQuarterFull() {
        return heartOneQuarterFull;
    }

    public TextureRegion getHeartEmpty() {
        return heartEmpty;
    }

    public Animation<TextureRegion> getDoorOpen() {
        return doorOpen;
    }

    public Animation<TextureRegion> getKeyChestOpen() {
        return keyChestOpen;
    }

    public Animation<TextureRegion> getGhostPoof() {
        return ghostPoof;
    }

    public int getLevelCounter() {
        return levelCounter;
    }

    public void setLevelCounter(int levelCounter) {
        this.levelCounter = levelCounter;
    }

    public int getNumberOfLevelsInCampaign() {
        return numberOfLevelsInCampaign;
    }

    public void setNumberOfLevelsInCampaign(int numberOfLevelsInCampaign) {
        this.numberOfLevelsInCampaign = numberOfLevelsInCampaign;
    }

    /*public Stage getUiStage() {
        return uiStage;
    }

    public void setUiStage(Stage uiStage) {
        this.uiStage = uiStage;
    }

    public Viewport getUiViewport() {
        return uiViewport;
    }

    public void setUiViewport(Viewport uiViewport) {
        this.uiViewport = uiViewport;
    }

    public OrthographicCamera getUiCamera() {
        return uiCamera;
    }

    public void setUiCamera(OrthographicCamera uiCamera) {
        this.uiCamera = uiCamera;
    }*/

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public int getScreenWidth() {
        return Gdx.graphics.getWidth();
    }

    public int getScreenHeight() {
        return Gdx.graphics.getHeight()
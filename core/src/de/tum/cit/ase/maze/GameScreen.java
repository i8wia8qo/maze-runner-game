package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.ase.maze.entities.*;
import de.tum.cit.ase.maze.helpers.Tuple;
import de.tum.cit.ase.maze.scenes.Hud;
import com.badlogic.gdx.InputMultiplexer;

import java.util.*;

/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {

    // First we create the map
    private TiledMap map;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private ShapeRenderer shapeRenderer;

    //private GameMap gameMap;  // GameMap class to handle map-related logic
    private GameMap2 gameMap;  // GameMap class to handle map-related logic

    private Player player;
    private Lives firstLife, secondLife, thirdLife;
    private Image firstLifeImage, secondLifeImage, thirdLifeImage;

    private Ghost ghost1;

    private List<Entity> traps = new ArrayList<>();

    private List<Enemy> enemies = new ArrayList<>();

    private List<Entity> keyChests = new ArrayList<>();

    private List<Entity> doors = new ArrayList<>();

    private List<Poof> ghostPoof = new ArrayList<>();
    private Animation<TextureRegion> ghostPoofAnimation;


    private final MazeRunnerGame game;

    private Stage uiStage;
    private Viewport gameViewport;
    private final OrthographicCamera camera;
    private final BitmapFont font;

    private float sinusInput = 0f;

    private float cameraFollowThresholdX; // = game.V_WIDTH * 0.3f; // Adjust the threshold as needed
    private float cameraFollowThresholdY; // = game.V_HEIGHT * 0.3f;
    private float cameraSpeed; // = 5.0f; // Adjust the camera movement speed as needed
    private Vector3 targetPosition; // = new Vector3();

    private Hud hud;

    private boolean goToGameOver = false;
    private boolean goToNextLevel = false;

    private boolean escapeKeyPressed = false;

    private boolean switchToExcitingMusic = false;
    private boolean switchedToExcitingMusic = true;

    private float timeSinceEscapePressed = 0f;

    private boolean disposed = false;

    private boolean switchingScreen = false;   // prevents use-after-switch
    private boolean initialized = false;       // render guard until show() ran

    // textures owned by THIS screen (created in show())
    private Texture characterSheet;            // replaces local walkSheet
    private Texture objectsSheet;              // used for poof (no more per-kill Texture())
    private TextureRegion poofFrame;           // cached region from objectsSheet


    /**
     * Constructs a new GameScreen, setting up the game environment, initializing the map,
     * the player, and other game entities. This screen is responsible for rendering the game world,
     * processing game logic, and handling user input.
     *
     * @param game The instance of the game controller, providing access to global game resources and methods.
     * @param filePath The file path to the level map to be loaded, allowing for dynamic level loading.
     */
    /* public GameScreen(MazeRunnerGame game, String filePath) {





        this.game = game;
        hud = new Hud(game.getSpriteBatch(), game);
        cameraFollowThresholdX = game.V_WIDTH * 0.1f; // Adjust the threshold as needed
        cameraFollowThresholdY = game.V_HEIGHT * 0.1f;
        cameraSpeed = 2.0f; // Adjust the camera movement speed as needed
        targetPosition = new Vector3();

        game.changeMusic("hauntedMusic.mp3");

        ghostPoofAnimation = game.getGhostPoof();


        //this.gameMap = new GameMap("maps/level-1.properties");
        //this.gameMap = new GameMap2("maps/level-2.properties");
        this.gameMap = new GameMap2(filePath);

        this.map = gameMap.getTiledMap();
        tiledMapRenderer = new OrthogonalTiledMapRenderer(map);

        shapeRenderer = new ShapeRenderer();

        // For displaying the HUD
        OrthographicCamera uiCamera = new OrthographicCamera();
        Viewport uiViewport = new ScreenViewport(uiCamera);
        this.uiStage = new Stage(uiViewport);


        // Create and configure the camera for the game view
        camera = new OrthographicCamera();
        gameViewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);
        gameViewport.apply(true);
        camera.update();

        // Adjust the camera position to center on the map
        //camera = new OrthographicCamera();
        //camera.setToOrtho(false);
        camera.position.set(gameMap.getMapWidth()*16 / 2f, gameMap.getMapHeight()*16 / 2f, 0); // Center the camera on the map
        //camera.translate(-gameMap.getMapWidth()*16 / 2f, -gameMap.getMapHeight()*16 / 2f); // Translate the camera to place (0,0) at the bottom-left
        camera.update();
        uiCamera.update();
        Gdx.app.log("GameMap", "mapWidth: " + gameMap.getMapWidth());
        Gdx.app.log("GameMap", "mapHeight: " + gameMap.getMapHeight());

        camera.zoom = 0.7f;
        uiCamera.zoom = 0.7f;



        // Get the font from the game's skin
        font = game.getSkin().getFont("font");

    } */

    public GameScreen(MazeRunnerGame game, String filePath) {
        this.game = game;

        // Reset state flags for this screen instance
        disposed = false;
        switchingScreen = false;
        initialized = false;
        goToGameOver = false;
        goToNextLevel = false;
        escapeKeyPressed = false;
        switchToExcitingMusic = false;
        switchedToExcitingMusic = true;

        hud = new Hud(game.getSpriteBatch(), game);

        cameraFollowThresholdX = game.V_WIDTH * 0.1f;
        cameraFollowThresholdY = game.V_HEIGHT * 0.1f;
        cameraSpeed = 2.0f;
        targetPosition = new Vector3();

        game.changeMusic("hauntedMusic.mp3");

        ghostPoofAnimation = game.getGhostPoof();

        this.gameMap = new GameMap2(filePath);
        this.map = gameMap.getTiledMap();

        // Only ONE renderer for the tiled map
        tiledMapRenderer = new OrthogonalTiledMapRenderer(map);

        shapeRenderer = new ShapeRenderer();

        // UI stage
        OrthographicCamera uiCamera = new OrthographicCamera();
        Viewport uiViewport = new ScreenViewport(uiCamera);
        this.uiStage = new Stage(uiViewport);

        // Game camera + viewport
        camera = new OrthographicCamera();
        gameViewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);
        gameViewport.apply(true);

        camera.position.set(gameMap.getMapWidth() * 16 / 2f, gameMap.getMapHeight() * 16 / 2f, 0);
        camera.zoom = 0.7f;
        camera.update();

        uiCamera.zoom = 1f;
        uiCamera.update();

        font = game.getSkin().getFont("font");
    }


    // Screen interface methods with necessary functionality

    /**
     * Renders the game world, including the map, player, enemies, and HUD elements. This method
     * is called every frame and is responsible for drawing the game state to the screen.
     * It also handles game logic updates such as player movement and collision detection.
     *
     * @param delta The time in seconds since the last render call. Used for frame-independent movement.
     */
    @Override
    public void render(float delta) {

        if (disposed || switchingScreen) return;
        if (!initialized || player == null) return;

        if (game.isPaused()) {
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            escapeKeyPressed = true;
        }

        // If the game is paused, update the timer
        /*if (game.isPaused()) {
            timeSinceEscapePressed += delta;
        }

        // Check for escape key press
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (!game.isPaused()) {
                // First time pressing ESCAPE, pause the game
                timeSinceEscapePressed = 0f; // Reset the timer
                //game.pauseGame(); // Implement this method to pause your game
                escapeKeyPressed = true;
            } else if (timeSinceEscapePressed >= 1f) {
                // Pressing ESCAPE again after 1 second, resume the game
                //game.resumeGame(); // Implement this method to resume your game

            }
        }*/

        if(game.isPaused()){

        }else {

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                escapeKeyPressed = true;
            }

            if (hud.getLives() <= 0) {
                goToGameOver = true;
                Gdx.app.log("GameScreen", "Player has no lives left. Switching to GameOverScreen.");

            }

            if (hud.getLives() <= 1.5) {
                switchToExcitingMusic = true;
            }

            if (switchToExcitingMusic && switchedToExcitingMusic) {
                game.changeMusic("excitingBackgroundMusic.mp3");
                switchedToExcitingMusic = false;
            }


            // Update the camera's position to follow the player
            //camera.position.set(player.getX(), player.getY(), 0);
            //camera.update();

            // Calculate the difference between the player's position and the camera's position
            float deltaX = player.getX() - camera.position.x;
            float deltaY = player.getY() - camera.position.y;

            // Check if the player has moved out of the center region
            // Check if the player has moved out of the center region
            if (Math.abs(deltaX) > cameraFollowThresholdX || Math.abs(deltaY) > cameraFollowThresholdY) {
                // Set the target position for the camera to smoothly pan towards
                targetPosition.set(player.getX(), player.getY(), 0);
            }

            // Use interpolation to smoothly move the camera towards the target position
            camera.position.lerp(targetPosition, cameraSpeed * delta);
            camera.update();

            shapeRenderer.setProjectionMatrix(camera.combined);


            ScreenUtils.clear(0, 0, 0, 1); // Clear the screen

            game.getSpriteBatch().setProjectionMatrix(hud.stage.getCamera().combined);


            camera.update(); // Update the camera
            uiStage.getCamera().update();
            uiStage.act(delta);
            uiStage.draw();


            // Set up and begin drawing with the sprite batch
            game.getSpriteBatch().setProjectionMatrix(camera.combined);


            //Gdx.gl.glClearColor(0, 0, 0, 1);
            //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            //renderer.setView(camera);
            //renderer.render();

            //Sprite sprite;
            //renderer.getBatch().begin();
            game.getSpriteBatch().begin(); // Important to call this before drawing anything

            // Translate the sprite batch to the camera's position
            //game.getSpriteBatch().setTransformMatrix(new Matrix4().idt().translate(-camera.position.x, -camera.position.y, 0));

            // Render the TiledMap
            tiledMapRenderer.setView(camera);
            tiledMapRenderer.render();

            // Render the player


            for (Poof poof : ghostPoof) {
                poof.update(delta);
                poof.draw(game.getSpriteBatch());
            }

            Iterator<Poof> poof_iterator = ghostPoof.iterator();
            while (poof_iterator.hasNext()) {
                Poof poof = poof_iterator.next();

                if (poof.isAnimationFinished()) {
                    poof_iterator.remove(); // Remove the ghost safely
                }

            }


//        ghost1.update(delta);
//        ghost1.draw(game.getSpriteBatch());

            for (Entity entity : traps) {
                if (entity instanceof Trap) {
                    Trap spikeTrap = (Trap) entity;
                    spikeTrap.update(delta);
                    spikeTrap.draw(game.getSpriteBatch());
                }
            }

            for (Enemy enemy : enemies) {
                if (enemy instanceof Ghost) {
                    Ghost ghostEnemy = (Ghost) enemy;
                    ghostEnemy.update(delta);
                    ghostEnemy.draw(game.getSpriteBatch());
                }
            }

            for (Entity entity : keyChests) {
                if (entity instanceof KeyChest) {
                    KeyChest keyChest = (KeyChest) entity;
                    keyChest.update(delta);
                    keyChest.draw(game.getSpriteBatch());
                }
            }

            for (Entity entity : doors) {
                if (entity instanceof Door) {
                    Door door = (Door) entity;
                    door.update(delta);
                    door.draw(game.getSpriteBatch());
                }
            }

            float oldPlayerX = player.getX();
            float oldPlayerY = player.getY();

            player.update(delta);
            player.draw(game.getSpriteBatch());

            /*firstLife.update(delta);
            secondLife.update(delta);
            thirdLife.update(delta);
            firstLife.draw(game.getSpriteBatch());
            secondLife.draw(game.getSpriteBatch());
            thirdLife.draw(game.getSpriteBatch());*/


            game.getSpriteBatch().end(); // Important to call this after drawing everything*/

            hud.stage.draw();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            // Calculate the starting point to draw the static rectangle
            /*int rectX = 112 - 16; // Center X - half the width of the rectangle
            int rectY = 112 - 16; // Center Y - half the height of the rectangle
            shapeRenderer.rect(rectX, rectY, 32, 32); // Draw a 32x32 rectangle*/

            Rectangle playerBounds = player.getEntityBounds();
            Rectangle playerSword = player.getSwordHitZone();

            shapeRenderer.setColor(Color.RED); // Set the color for the rectangle
            shapeRenderer.rect(player.getEntityBounds().x, player.getEntityBounds().y, player.getEntityBounds().width, player.getEntityBounds().height);

            if (playerSword != null) {
                shapeRenderer.rect(playerSword.x, playerSword.y, playerSword.width, playerSword.height);
            }



        /*for(Enemy enemy : enemies) {

            shapeRenderer.rect(enemy.getEntityBounds().x, enemy.getEntityBounds().y, enemy.getEntityBounds().width, enemy.getEntityBounds().height);

            if(enemy instanceof Ghost){
                Ghost ghost = (Ghost) enemy;
                Rectangle ghostBounds = ghost.getEntityBounds();

                if (Intersector.overlaps(playerBounds, ghostBounds)) {
                    // Collision occurred between player and ghost
                    // Handle the collision logic here

                    player.setTookDamage(true);
                    player.setDamageTaken(ghost.damageGiven);
                    hud.setLives(player.getLives());
                    hud.update();

                }

                if(playerSword != null){
                    if (Intersector.overlaps(playerSword, ghostBounds)) {
                        // Collision occurred between player and ghost
                        // Handle the collision logic here

                        Gdx.app.log("GameScreen", "render() ghost hit" + true);
                        ghost.setTookDamage(true);
                        ghost.setDamageTaken(player.getDamageGiven());

                        if(ghost.isDead()){
                            enemies.remove(enemy);
                        }

                        *//*if(ghost.getLives()-player.getDamageGiven()>0){
                            ghost.setTookDamage(true);
                            ghost.setDamageTaken(player.getDamageGiven());
                        } else{

                        }

                        player.setDamageTaken(ghost.damageGiven);
                        hud.setLives(player.getLives());
                        hud.update();*//*

                    }
                }


            }

        }*/

            Iterator<Enemy> iterator = enemies.iterator();
            while (iterator.hasNext()) {
                Enemy enemy = iterator.next();
                shapeRenderer.rect(enemy.getEntityBounds().x, enemy.getEntityBounds().y, enemy.getEntityBounds().width, enemy.getEntityBounds().height);
                if (enemy instanceof Ghost) {
                    Ghost ghost = (Ghost) enemy;
                    Rectangle ghostBounds = ghost.getEntityBounds();

                    if (Intersector.overlaps(playerBounds, ghostBounds)) {
                        // Handle the collision logic here
                        game.playSoundEffect(2);
                        player.setTookDamage(true);
                        player.setDamageTaken(ghost.damageGiven);
                        hud.setLives(player.getLives());

                    }

                    if (playerSword != null) {
                        if (Intersector.overlaps(playerSword, ghostBounds)) {
                            // Handle the collision logic here
                            // Gdx.app.log("GameScreen", "render() ghost hit" + true);
                            ghost.setTookDamage(true);
                            ghost.setDamageTaken(player.getDamageGiven());

                            if (ghost.isDead()) {
                                hud.setScore(hud.getScore() + 100);

                                /*Texture objectsSheet = new Texture(Gdx.files.internal("objects.png"));
                                TextureRegion poofTexture = new TextureRegion(objectsSheet, 32, 32, 32, 32);
                                Poof poof = new Poof(new Sprite(poofTexture), gameMap, ghostPoofAnimation);
                                poof.setX(enemy.getX() - enemy.getWidth() / 2);
                                poof.setY(enemy.getY() - enemy.getHeight() / 2);
                                ghostPoof.add(poof);*/
                                Poof poof = new Poof(new Sprite(poofFrame), gameMap, ghostPoofAnimation);
                                poof.setX(enemy.getX() - enemy.getWidth() / 2);
                                poof.setY(enemy.getY() - enemy.getHeight() / 2);
                                ghostPoof.add(poof);
                                iterator.remove(); // Remove the ghost safely
                            }
                        }
                    }
                }
            }

        /*for(Entity entity : traps) {

            shapeRenderer.rect(entity.getEntityBounds().x, entity.getEntityBounds().y, entity.getEntityBounds().width, entity.getEntityBounds().height);

            if(entity instanceof Trap){
                Trap trap = (Trap) entity;
                Rectangle trapBounds = trap.getEntityBounds();

                if (Intersector.overlaps(playerBounds, trapBounds)) {
                    // Collision occurred between player and trap
                    // Handle the collision logic here

                    player.setTookDamage(true);
                    player.setDamageTaken(trap.damageGiven);
                    hud.setLives(player.getLives());
                    hud.update();

                }
            }

        }*/


            Iterator<Entity> trap_iterator = traps.iterator();
            while (trap_iterator.hasNext()) {
                Entity entity = trap_iterator.next();
                shapeRenderer.rect(entity.getEntityBounds().x, entity.getEntityBounds().y, entity.getEntityBounds().width, entity.getEntityBounds().height);
                if (entity instanceof Trap) {
                    Trap trap = (Trap) entity;
                    Rectangle trapBounds = trap.getEntityBounds();

                    if (Intersector.overlaps(playerBounds, trapBounds)) {
                        // Handle the collision logic here
                        game.playSoundEffect(2);
                        player.setTookDamage(true);
                        player.setDamageTaken(trap.damageGiven);
                        hud.setLives(player.getLives());

                    }

                    if (playerSword != null) {
                        if (Intersector.overlaps(playerSword, trapBounds)) {
                            // Handle the collision logic here
                            // Gdx.app.log("GameScreen", "render() ghost hit" + true);
                            trap.setTookDamage(true);
                            trap.setDamageTaken(player.getDamageGiven());

                            if (trap.isDead()) {
                                hud.setScore(hud.getScore() + 250);

                                trap_iterator.remove(); // Remove the ghost safely
                            }
                        }
                    }
                }
            }

            Iterator<Entity> keyChest_iterator = keyChests.iterator();
            while (keyChest_iterator.hasNext()) {
                Entity entity = keyChest_iterator.next();
                shapeRenderer.rect(entity.getEntityBounds().x, entity.getEntityBounds().y, entity.getEntityBounds().width, entity.getEntityBounds().height);
                if (entity instanceof KeyChest) {
                    KeyChest keyChest = (KeyChest) entity;
                    Rectangle keyChestBounds = keyChest.getEntityBounds();

                    if (Intersector.overlaps(playerBounds, keyChestBounds)) {
                        boolean animationInProgress = keyChest.isAnimationInProgress();
                        if (!animationInProgress) {
                            keyChest.setAnimationStarted(true);
                        }

                        keyChest.setAnimationInProgress(true);

                        // Handle the collision logic here

                        game.playSoundEffect(1);
                        player.setKey(true);
                        hud.setKeyAcquired(true);
                        gameMap.getBasicTilesSet().getTile(96).getProperties().clear();

                    }

                    if (playerSword != null) {
                        if (Intersector.overlaps(playerSword, keyChestBounds)) {
                            // Handle the collision logic here
                            // Gdx.app.log("GameScreen", "render() ghost hit" + true);
                            keyChest.setTookDamage(true);
                            keyChest.setDamageTaken(player.getDamageGiven());

                            if (keyChest.isDead()) {
                                hud.setScore(0);

                                keyChest_iterator.remove(); // Remove the keyChest safely
                            }
                        }
                    }
                }
            }


            Iterator<Entity> door_iterator = doors.iterator();
            while (door_iterator.hasNext()) {
                Entity entity = door_iterator.next();
                shapeRenderer.rect(entity.getEntityBounds().x, entity.getEntityBounds().y, entity.getEntityBounds().width, entity.getEntityBounds().height);

                if (entity instanceof Door) {
                    Door door = (Door) entity;
                    Rectangle doorBounds = door.getEntityBounds();
                    shapeRenderer.rect(door.getMoveToNextLevel().x, door.getMoveToNextLevel().y, door.getMoveToNextLevel().width, door.getMoveToNextLevel().height);

                    if (Intersector.overlaps(playerBounds, door.getMoveToNextLevel())) {

                        goToNextLevel = true;
                        Gdx.app.log("GameScreen", "Player is going to the next level.");


                    }

                    if (Intersector.overlaps(playerBounds, doorBounds)) {
                        // Handle the collision logic here
                        // player.setPosition(oldPlayerX, oldPlayerY);
                        boolean animationInProgress = door.isAnimationInProgress();
                        if (!animationInProgress) {
                            door.setAnimationStarted(true);
                        }

                        door.setAnimationInProgress(true);

                    }

                    if (playerSword != null) {
                        if (Intersector.overlaps(playerSword, doorBounds)) {
                            // Handle the collision logic here
                            // Gdx.app.log("GameScreen", "render() ghost hit" + true);
                            door.setTookDamage(true);
                            door.setDamageTaken(player.getDamageGiven());

                            if (door.isDead()) {
                                hud.setScore(0);
                                door_iterator.remove(); // Remove the keyChest safely
                            }
                        }
                    }
                }
            }

            hud.update(delta);


            shapeRenderer.end();

            //renderer.getBatch().end();

            /*if (goToGameOver) {
                Gdx.app.log("GameScreen", "Player has no lives left. Switching to GameOverScreen.");
                game.goToGameOver();

                return; // Ensure no further rendering logic is processed after this call
            }

            if (goToNextLevel) {
                Gdx.app.log("GameScreen", "Player has no lives left. Switching to GameOverScreen.");

                game.setLevelCounter(game.getLevelCounter() + 1);

                game.goToGame();

                return; // Ensure no further rendering logic is processed after this call
            }*/


            if (goToGameOver) {
                switchingScreen = true;
                Gdx.app.log("GameScreen", "Switching to GameOverScreen.");
                game.goToGameOver();
                return;
            }

            if (goToNextLevel) {
                switchingScreen = true;
                Gdx.app.log("GameScreen", "Switching to next level.");
                game.setLevelCounter(game.getLevelCounter() + 1);
                game.goToGame();
                return;
            }

            if (escapeKeyPressed) {
                switchingScreen = true;
                escapeKeyPressed = false;
                Gdx.app.log("GameScreen", "Switching to MenuScreen.");
                game.goToMenu();
                return;
            }


        }

        /*if (escapeKeyPressed) {
            game.goToMenu();
        }*/
        /*


        // Move text in a circular path to have an example of a moving object
        sinusInput += delta;
        float textX = (float) (camera.position.x + Math.sin(sinusInput) * 100);
        float textY = (float) (camera.position.y + Math.cos(sinusInput) * 100);





        //gameMap.loadMapFromProperties(game, "maps/level-1.properties");  // Replace with the actual properties file

        // Render the text
        font.draw(game.getSpriteBatch(), "Press ESC to go to menu", textX, textY);

        // Draw the character next to the text :) / We can reuse sinusInput here
        game.getSpriteBatch().draw(
                //game.getCharacterDownAnimation().getKeyFrame(sinusInput, true),
                game.getCharacterUpAnimation().getKeyFrame(sinusInput, true),
                textX - 96,
                textY - 64,
                64,
                128
        );

        game.getSpriteBatch().end(); // Important to call this after drawing everything*/

    }

    /**
     * Resizes the game viewport. This method is called whenever the window size changes,
     * ensuring that the game's visuals adjust correctly to the new size.
     *
     * @param width The new width of the window.
     * @param height The new height of the window.
     */
    /*@Override
    public void resize(int width, int height) {
        // original code camera.setToOrtho(false);
        //camera.viewportWidth = width;
        //camera.viewportHeight = height;
        //gameViewport.update(width, height, true); // Update the viewport dimensions
        //gameViewport.apply(true);
        //camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0); // Center the camera
        //uiStage.getViewport().update(width, height, true);
        //camera.update();

        if (disposed) return;

        // Update world viewport. Keeps aspect ratio, avoids squishing.
        gameViewport.update(width, height, true);

        // Update UI viewport. Keeps UI coordinates in screen pixels.
        if (uiStage != null) {
            uiStage.getViewport().update(width, height, true);
        }

        // Update HUD stage
        if (hud != null && hud.stage != null) {
            hud.stage.getViewport().update(width, height, true);
        }

    }*/

    @Override
    public void resize(int width, int height) {
        if (disposed) return;

        // WORLD viewport prevents squishing.
        if (gameViewport != null) {
            gameViewport.update(width, height, true);
        }

        // UI stage viewport. Fixes button hit areas after resize.
        if (uiStage != null) {
            uiStage.getViewport().update(width, height, true);
        }

        // HUD stage viewport
        if (hud != null && hud.stage != null) {
            hud.stage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    /**
     * Called when this screen becomes the current screen for the game. Used to initialize
     * resources specific to this screen.
     */
    /*@Override
    public void show() {

        TmxMapLoader loader = new TmxMapLoader();
        //map = loader.load("maps/maze2.tmx");

        renderer = new OrthogonalTiledMapRenderer(map);


        Texture walkSheet = new Texture(Gdx.files.internal("character.png"));
        int frameWidth = 16;
        int frameHeight = 32;
        int animationFrames = 4;
        Sprite sprite;



        TextureRegion textureRegion = new TextureRegion(walkSheet, 0 * frameWidth, 0*frameHeight, frameWidth, frameHeight);
        Texture texture = textureRegion.getTexture();
        TextureRegion texregFacingDown= new TextureRegion(walkSheet, 0 * frameWidth, 0*frameHeight, frameWidth, frameHeight);
        TextureRegion texregFacingRight= new TextureRegion(walkSheet, 0 * frameWidth, 1*frameHeight, frameWidth, frameHeight);
        TextureRegion texregFacingUp= new TextureRegion(walkSheet, 0 * frameWidth, 2*frameHeight, frameWidth, frameHeight);
        TextureRegion texregFacingLeft= new TextureRegion(walkSheet, 0 * frameWidth, 3*frameHeight, frameWidth, frameHeight);
        Texture stillframeFacingDown = texregFacingDown.getTexture();
        Texture stillframeFacingRight = texregFacingRight.getTexture();
        Texture stillframeFacingUp = texregFacingUp.getTexture();
        Texture stillframeFacingLeft = texregFacingLeft.getTexture();

        player = new Player(new Sprite(textureRegion), gameMap,
                game.getCharacterUpAnimation(),
                game.getCharacterDownAnimation(),
                game.getCharacterLeftAnimation(),
                game.getCharacterRightAnimation(),
                game.getCharacterFightUpAnimation(),
                game.getCharacterFightDownAnimation(),
                game.getCharacterFightLeftAnimation(),
                game.getCharacterFightRightAnimation(),
                texregFacingDown,
                texregFacingRight,
                texregFacingUp,
                texregFacingLeft);

        player.setPosition(gameMap.getSpawn().getX() * player.getCollisionLayer().getTileWidth(), gameMap.getSpawn().getY() * player.getCollisionLayer().getTileHeight());

        firstLife = new Lives(new Sprite(game.getHeartFull()), player);
        secondLife = new Lives(new Sprite(game.getHeartFull()), player);
        thirdLife = new Lives(new Sprite(game.getHeartFull()), player);

        int windowWidth = (int) camera.viewportHeight; // Get the width of the game window
        int windowHeight = (int) camera.viewportHeight; // Get the height of the game window
        Gdx.app.log("GameScreen", "windowWidth: " + windowWidth);
        Gdx.app.log("GameScreen", "windowHeight: " + windowHeight);
        int heartSpriteWidth = 16; // Width of each sprite
        int heartSpriteHeight = 16; // Width of each sprite

// Position for secondLife (center)
        int secondLifeX = (windowWidth / 2) - (heartSpriteWidth / 2);
        int secondLifeY = (100-heartSpriteHeight); // Y-position at the top of the game window
        secondLife.setPosition(secondLifeX, secondLifeY);
// Position for firstLife (left of secondLife)
        int firstLifeX = secondLifeX - heartSpriteWidth - 2; // Offset by 2px and width of the sprite
        int firstLifeY = secondLifeY; // Same Y-position as secondLife
        firstLife.setPosition(firstLifeX, firstLifeY);

// Position for thirdLife (right of secondLife)
        int thirdLifeX = secondLifeX + heartSpriteWidth + 2; // Offset by 2px and width of the sprite
        int thirdLifeY = secondLifeY; // Same Y-position as secondLife
        thirdLife.setPosition(thirdLifeX, thirdLifeY);

        // Initialize lives display
        initLivesDisplay();

        TextureRegion stillFrameUpGhost = new TextureRegion(game.getGhostUpAnimation().getKeyFrame(0));
        TextureRegion stillFrameDownGhost = new TextureRegion(game.getGhostDownAnimation().getKeyFrame(0));
        TextureRegion stillFrameLeftGhost = new TextureRegion(game.getGhostLeftAnimation().getKeyFrame(0));
        TextureRegion stillFrameRightGhost = new TextureRegion(game.getGhostRightAnimation().getKeyFrame(0));

        TextureRegion stillFrameTrapStraightGray = new TextureRegion(game.getTrapStraightGrayAnimation().getKeyFrame(0));
        TextureRegion stillFrameTrapStraightGold = new TextureRegion(game.getTrapStraightGoldAnimation().getKeyFrame(0));
        TextureRegion stillFrameTrapSkewedGray = new TextureRegion(game.getTrapSkewedGrayAnimation().getKeyFrame(0));
        TextureRegion stillFrameTrapSkewedGold = new TextureRegion(game.getTrapSkewedGoldAnimation().getKeyFrame(0));

        *//*ghost1 = new Ghost(new Sprite(stillFrameDownGhost), gameMap,
                game.getGhostUpAnimation(),
                game.getGhostDownAnimation(),
                game.getGhostLeftAnimation(),
                game.getGhostRightAnimation(),

                game.getGhostUpAnimation(),
                game.getGhostDownAnimation(),
                game.getGhostLeftAnimation(),
                game.getGhostRightAnimation(),

                stillFrameUpGhost,
                stillFrameDownGhost,
                stillFrameLeftGhost,
                stillFrameRightGhost);

        ghost1.setPosition(8 * ghost1.getCollisionLayer().getTileWidth(), 5 * ghost1.getCollisionLayer().getTileHeight());*//*

        TextureRegion[] stillFrames = new TextureRegion[4]; // Assuming you have 4 still frames

        stillFrames[0] = new TextureRegion(game.getTrapStraightGrayAnimation().getKeyFrame(0));
        stillFrames[1] = new TextureRegion(game.getTrapStraightGoldAnimation().getKeyFrame(0));
        stillFrames[2] = new TextureRegion(game.getTrapSkewedGrayAnimation().getKeyFrame(0));
        stillFrames[3] = new TextureRegion(game.getTrapSkewedGoldAnimation().getKeyFrame(0));

        Animation<TextureRegion>[] animations = new Animation[4]; // Assuming you have 4 animations

        animations[0] = game.getTrapStraightGrayAnimation();
        animations[1] = game.getTrapStraightGoldAnimation();
        animations[2] = game.getTrapSkewedGrayAnimation();
        animations[3] = game.getTrapSkewedGoldAnimation();



        for(Tuple spawn : gameMap.getTrapSpawns()) {

            int seed = 42;
            //Random random = new Random(seed);
            Random random = new Random();

            int randomInt = random.nextInt(4); // Generates a random integer between 0 (inclusive) and 4 (exclusive)

            Trap trap = new Trap(new Sprite(stillFrames[randomInt]), gameMap, animations[randomInt]);

            trap.setPosition(spawn.getX() * trap.getCollisionLayer().getTileWidth(), spawn.getY() * trap.getCollisionLayer().getTileHeight());

            traps.add(trap);
        }

        for(Tuple spawn : gameMap.getEnemySpawns()) {

            Ghost ghost = new Ghost(new Sprite(stillFrameDownGhost), gameMap,
                    game.getGhostUpAnimation(),
                    game.getGhostDownAnimation(),
                    game.getGhostLeftAnimation(),
                    game.getGhostRightAnimation(),

                    game.getGhostUpAnimation(),
                    game.getGhostDownAnimation(),
                    game.getGhostLeftAnimation(),
                    game.getGhostRightAnimation(),

                    stillFrameUpGhost,
                    stillFrameDownGhost,
                    stillFrameLeftGhost,
                    stillFrameRightGhost);

            ghost.setPosition(spawn.getX() * ghost.getCollisionLayer().getTileWidth(), spawn.getY() * ghost.getCollisionLayer().getTileHeight());

            enemies.add(ghost);
        }

        for(Tuple spawn : gameMap.getKeyChestSpawns()) {

            KeyChest keyChest = new KeyChest(new Sprite(game.getKeyChestOpen().getKeyFrame(0)), gameMap, game.getKeyChestOpen());

            keyChest.setPosition(spawn.getX() * keyChest.getCollisionLayer().getTileWidth(), spawn.getY() * keyChest.getCollisionLayer().getTileHeight());

            keyChests.add(keyChest);
        }

        for(Tuple spawn : gameMap.getDoorSpawns()) {

            Door door = new Door(new Sprite(game.getDoorOpen().getKeyFrame(0)), gameMap, game.getDoorOpen());

            door.setPosition(spawn.getX() * door.getCollisionLayer().getTileWidth(), spawn.getY() * door.getCollisionLayer().getTileHeight());

            doors.add(door);
        }




    }*/


    @Override
    public void show() {
        // If show() gets called again on the same screen instance, reset safe state
        traps.clear();
        enemies.clear();
        keyChests.clear();
        doors.clear();
        ghostPoof.clear();

        // --- Textures owned by this screen ---
        // Character sheet used ONLY to build Player sprite/regions here
        characterSheet = new Texture(Gdx.files.internal("character.png"));

        // Objects sheet used for poof (avoid allocating textures inside render!)
        objectsSheet = new Texture(Gdx.files.internal("objects.png"));
        poofFrame = new TextureRegion(objectsSheet, 32, 32, 32, 32);

        int frameWidth = 16;
        int frameHeight = 32;

        TextureRegion textureRegion = new TextureRegion(characterSheet, 0 * frameWidth, 0 * frameHeight, frameWidth, frameHeight);

        TextureRegion texregFacingDown  = new TextureRegion(characterSheet, 0 * frameWidth, 0 * frameHeight, frameWidth, frameHeight);
        TextureRegion texregFacingRight = new TextureRegion(characterSheet, 0 * frameWidth, 1 * frameHeight, frameWidth, frameHeight);
        TextureRegion texregFacingUp    = new TextureRegion(characterSheet, 0 * frameWidth, 2 * frameHeight, frameWidth, frameHeight);
        TextureRegion texregFacingLeft  = new TextureRegion(characterSheet, 0 * frameWidth, 3 * frameHeight, frameWidth, frameHeight);

        player = new Player(
                new Sprite(textureRegion),
                gameMap,
                game.getCharacterUpAnimation(),
                game.getCharacterDownAnimation(),
                game.getCharacterLeftAnimation(),
                game.getCharacterRightAnimation(),
                game.getCharacterFightUpAnimation(),
                game.getCharacterFightDownAnimation(),
                game.getCharacterFightLeftAnimation(),
                game.getCharacterFightRightAnimation(),
                texregFacingDown,
                texregFacingRight,
                texregFacingUp,
                texregFacingLeft
        );

        player.setPosition(
                gameMap.getSpawn().getX() * player.getCollisionLayer().getTileWidth(),
                gameMap.getSpawn().getY() * player.getCollisionLayer().getTileHeight()
        );

        // Lives sprites use TextureRegions from MazeRunnerGame (shared); do NOT dispose those here
        firstLife = new Lives(new Sprite(game.getHeartFull()), player);
        secondLife = new Lives(new Sprite(game.getHeartFull()), player);
        thirdLife = new Lives(new Sprite(game.getHeartFull()), player);

        initLivesDisplay();

        // Build enemies/traps/etc. (unchanged logic, but keep it here)
        TextureRegion stillFrameUpGhost    = new TextureRegion(game.getGhostUpAnimation().getKeyFrame(0));
        TextureRegion stillFrameDownGhost  = new TextureRegion(game.getGhostDownAnimation().getKeyFrame(0));
        TextureRegion stillFrameLeftGhost  = new TextureRegion(game.getGhostLeftAnimation().getKeyFrame(0));
        TextureRegion stillFrameRightGhost = new TextureRegion(game.getGhostRightAnimation().getKeyFrame(0));

        TextureRegion[] stillFrames = new TextureRegion[4];
        stillFrames[0] = new TextureRegion(game.getTrapStraightGrayAnimation().getKeyFrame(0));
        stillFrames[1] = new TextureRegion(game.getTrapStraightGoldAnimation().getKeyFrame(0));
        stillFrames[2] = new TextureRegion(game.getTrapSkewedGrayAnimation().getKeyFrame(0));
        stillFrames[3] = new TextureRegion(game.getTrapSkewedGoldAnimation().getKeyFrame(0));

        @SuppressWarnings("unchecked")
        Animation<TextureRegion>[] animations = new Animation[4];
        animations[0] = game.getTrapStraightGrayAnimation();
        animations[1] = game.getTrapStraightGoldAnimation();
        animations[2] = game.getTrapSkewedGrayAnimation();
        animations[3] = game.getTrapSkewedGoldAnimation();

        Random random = new Random();

        for (Tuple spawn : gameMap.getTrapSpawns()) {
            int randomInt = random.nextInt(4);
            Trap trap = new Trap(new Sprite(stillFrames[randomInt]), gameMap, animations[randomInt]);
            trap.setPosition(spawn.getX() * trap.getCollisionLayer().getTileWidth(),
                    spawn.getY() * trap.getCollisionLayer().getTileHeight());
            traps.add(trap);
        }

        for (Tuple spawn : gameMap.getEnemySpawns()) {
            Ghost ghost = new Ghost(
                    new Sprite(stillFrameDownGhost), gameMap,
                    game.getGhostUpAnimation(), game.getGhostDownAnimation(),
                    game.getGhostLeftAnimation(), game.getGhostRightAnimation(),
                    game.getGhostUpAnimation(), game.getGhostDownAnimation(),
                    game.getGhostLeftAnimation(), game.getGhostRightAnimation(),
                    stillFrameUpGhost, stillFrameDownGhost, stillFrameLeftGhost, stillFrameRightGhost
            );
            ghost.setPosition(spawn.getX() * ghost.getCollisionLayer().getTileWidth(),
                    spawn.getY() * ghost.getCollisionLayer().getTileHeight());
            enemies.add(ghost);
        }

        for (Tuple spawn : gameMap.getKeyChestSpawns()) {
            KeyChest keyChest = new KeyChest(new Sprite(game.getKeyChestOpen().getKeyFrame(0)), gameMap, game.getKeyChestOpen());
            keyChest.setPosition(spawn.getX() * keyChest.getCollisionLayer().getTileWidth(),
                    spawn.getY() * keyChest.getCollisionLayer().getTileHeight());
            keyChests.add(keyChest);
        }

        for (Tuple spawn : gameMap.getDoorSpawns()) {
            Door door = new Door(new Sprite(game.getDoorOpen().getKeyFrame(0)), gameMap, game.getDoorOpen());
            door.setPosition(spawn.getX() * door.getCollisionLayer().getTileWidth(),
                    spawn.getY() * door.getCollisionLayer().getTileHeight());
            doors.add(door);
        }

        initialized = true;


        // While resizing the window, hitboxes of buttons should stay aligned with the buttons.
        // The following code does this.
        InputMultiplexer mux = new InputMultiplexer();

        // UI stage should usually get input first
        if (uiStage != null) mux.addProcessor(uiStage);

        // HUD stage too if it has clickable actors
        if (hud != null && hud.stage != null) mux.addProcessor(hud.stage);

        Gdx.input.setInputProcessor(mux);

    }

    @Override
    /* public void hide() {
        dispose();
    } */

    public void hide() {
        // stop timers, unregister input, pause, etc.
        // DO NOT dispose here.
    }


    /*@Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        player.getTexture().dispose();
        //ghost1.getTexture().dispose();
        shapeRenderer.dispose();
        for(Enemy enemy : enemies) {
            if(enemy instanceof Ghost) {
                Ghost ghostEnemy = (Ghost) enemy;
                ghostEnemy.getTexture().dispose();
            }
        }
        firstLife.getTexture().dispose();
        secondLife.getTexture().dispose();
        thirdLife.getTexture().dispose();

        for(Entity entity : traps) {
            if(entity instanceof Trap) {
                Trap spikeTrap = (Trap) entity;
                spikeTrap.getTexture().dispose();
            }
        }

        for(Entity entity : keyChests) {
            if(entity instanceof KeyChest) {
                KeyChest keyChest = (KeyChest) entity;
                keyChest.getTexture().dispose();
            }
        }

        for(Entity entity : doors) {
            if(entity instanceof Door) {
                Door door = (Door) entity;
                door.getTexture().dispose();
            }
        }
    }*/

    /*@Override
    public void dispose() {
        try {
            map.dispose();
        } catch (Exception e) {
            Gdx.app.error("DisposeError", "Failed to dispose map", e);
        }

        try {
            renderer.dispose();
        } catch (Exception e) {
            Gdx.app.error("DisposeError", "Failed to dispose renderer", e);
        }

        try {
            player.getTexture().dispose();
        } catch (Exception e) {
            Gdx.app.error("DisposeError", "Failed to dispose player texture", e);
        }

        // No need for ghost1.getTexture().dispose(); as it's commented out

        try {
            //shapeRenderer.dispose();
        } catch (Exception e) {
            Gdx.app.error("DisposeError", "Failed to dispose shapeRenderer", e);
        }

        for (Enemy enemy : enemies) {
            try {
                if (enemy instanceof Ghost) {
                    Ghost ghostEnemy = (Ghost) enemy;
                    ghostEnemy.getTexture().dispose();
                }
            } catch (Exception e) {
                Gdx.app.error("DisposeError", "Failed to dispose ghost texture", e);
            }
        }

        try {
            firstLife.getTexture().dispose();
        } catch (Exception e) {
            Gdx.app.error("DisposeError", "Failed to dispose firstLife texture", e);
        }

        try {
            secondLife.getTexture().dispose();
        } catch (Exception e) {
            Gdx.app.error("DisposeError", "Failed to dispose secondLife texture", e);
        }

        try {
            thirdLife.getTexture().dispose();
        } catch (Exception e) {
            Gdx.app.error("DisposeError", "Failed to dispose thirdLife texture", e);
        }

        for (Entity entity : traps) {
            try {
                if (entity instanceof Trap) {
                    Trap spikeTrap = (Trap) entity;
                    spikeTrap.getTexture().dispose();
                }
            } catch (Exception e) {
                Gdx.app.error("DisposeError", "Failed to dispose trap texture", e);
            }
        }

        for (Entity entity : keyChests) {
            try {
                if (entity instanceof KeyChest) {
                    KeyChest keyChest = (KeyChest) entity;
                    keyChest.getTexture().dispose();
                }
            } catch (Exception e) {
                Gdx.app.error("DisposeError", "Failed to dispose keyChest texture", e);
            }
        }

        for (Entity entity : doors) {
            try {
                if (entity instanceof Door) {
                    Door door = (Door) entity;
                    door.getTexture().dispose();
                }
            } catch (Exception e) {
                Gdx.app.error("DisposeError", "Failed to dispose door texture", e);
            }
        }
    }*/

    /**
     * Called when this screen becomes the current screen for the game. Used to initialize
     * resources specific to this screen.
     */

    /*@Override
    public void dispose() {
        // Dispose of the map resource if it's exclusively used within this screen.
        safelyDispose("map", map);

        // The renderer might be shared; ensure it's disposed of here only if this screen owns it.
        safelyDispose("tiledMapRenderer", tiledMapRenderer);

        // ShapeRenderer is likely exclusive to this screen.
        safelyDispose("shapeRenderer", shapeRenderer);

        // Disposing of textures loaded specifically for this screen
        // You might need to check if textures are shared or managed globally.
        safelyDisposeTexture("player texture", player.getTexture());
        // If you have specific textures for lives, traps, etc., dispose of them here if they're not managed by AssetManager

        // Disposing of the stage if it's not shared across screens.
        safelyDispose("uiStage", uiStage);

        // It's critical to ensure that shared resources (like global SpriteBatch instances) are not disposed of here.
    }*/

    /*@Override
    public void dispose() {
        if (disposed) return;
        disposed = true;

        safelyDispose("uiStage", uiStage);
        uiStage = null;

        safelyDispose("shapeRenderer", shapeRenderer);
        shapeRenderer = null;

        safelyDispose("tiledMapRenderer", tiledMapRenderer);
        tiledMapRenderer = null;

        safelyDispose("renderer", tiledMapRenderer);
        tiledMapRenderer = null;

        // Map ownership: ONLY dispose it if GameMap2 doesn't also dispose it
        safelyDispose("map", map);
        map = null;

        // IMPORTANT: do NOT dispose player.getTexture() here (explained below)
    }*/



    @Override
    public void dispose() {
        if (disposed) return;
        disposed = true;
        switchingScreen = true;

        safelyDispose("uiStage", uiStage);
        uiStage = null;

        safelyDispose("shapeRenderer", shapeRenderer);
        shapeRenderer = null;

        safelyDispose("tiledMapRenderer", tiledMapRenderer);
        tiledMapRenderer = null;

        // Map ownership: ONLY dispose if GameMap2 does NOT dispose it elsewhere.
        safelyDispose("map", map);
        map = null;

        // Dispose textures that THIS screen created in show()
        safelyDisposeTexture("characterSheet", characterSheet);
        characterSheet = null;

        safelyDisposeTexture("objectsSheet", objectsSheet);
        objectsSheet = null;

        poofFrame = null;


    }

    // Helper method to safely dispose of disposable resources.
    private void safelyDispose(String resourceName, Disposable resource) {
        try {
            if (resource != null) {
                resource.dispose();
                Gdx.app.log("ResourceDispose", resourceName + " disposed successfully.");
            }
        } catch (Exception e) {
            Gdx.app.error("DisposeError", "Failed to dispose " + resourceName, e);
        }
    }

    // Helper method for textures, considering their unique handling.
    private void safelyDisposeTexture(String resourceName, Texture texture) {
        try {
            if (texture != null) {
                texture.dispose();
                Gdx.app.log("ResourceDispose", resourceName + " disposed successfully.");
            }
        } catch (Exception e) {
            Gdx.app.error("DisposeError", "Failed to dispose " + resourceName, e);
        }
    }

    // Additional methods and logic can be added as needed for the game screen
    private void initLivesDisplay() {

        /*

        firstLifeImage = new Image(new SpriteDrawable(firstLife));
        secondLifeImage = new Image(new SpriteDrawable(secondLife));
        thirdLifeImage = new Image(new SpriteDrawable(thirdLife));

        //int screenWidth = uiStage.getViewport().getScreenWidth()/uiStage.getWidth();
        int screenWidth = (int) uiStage.getViewport().getScreenWidth();
        int screenHeight = uiStage.getViewport().getScreenHeight();
        Gdx.app.log("uiStage.getViewport()", ".getScreenWidth(): " + screenWidth);
        Gdx.app.log("uiStage.getViewport()", ".getScreenWidth(): " + screenHeight);
        //secondLifeImage.setX((screenWidth - secondLifeImage.getWidth()) / 2);
        //secondLifeImage.setY(screenHeight/2.0f);
        secondLifeImage.setX(0);
        secondLifeImage.setY(0);
        firstLifeImage.setX(secondLifeImage.getX() - firstLifeImage.getWidth() - 2);
        firstLifeImage.setY(secondLifeImage.getY());
        thirdLifeImage.setX(secondLifeImage.getX() + secondLifeImage.getWidth() + 2);
        thirdLifeImage.setY(secondLifeImage.getY());

        uiStage.addActor(firstLifeImage);
        uiStage.addActor(secondLifeImage);
        uiStage.addActor(thirdLifeImage);

        */

    }

    public void updateLives() {

        firstLifeImage.setDrawable(new SpriteDrawable(firstLife));
        secondLifeImage.setDrawable(new SpriteDrawable(secondLife));
        thirdLifeImage.setDrawable(new SpriteDrawable(thirdLife));
    }

    public boolean isEscapeKeyPressed() {
        return escapeKeyPressed;
    }

    public void setEscapeKeyPressed(boolean escapeKeyPressed) {
        this.escapeKeyPressed = escapeKeyPressed;
    }
}

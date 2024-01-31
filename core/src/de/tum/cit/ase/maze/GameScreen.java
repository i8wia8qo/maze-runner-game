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

import java.util.*;

/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {

    // First we create the map
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
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
    //private boolean escapeKeyPressed = false;

    /**
     * Constructs a new GameScreen, setting up the game environment, initializing the map,
     * the player, and other game entities. This screen is responsible for rendering the game world,
     * processing game logic, and handling user input.
     *
     * @param game The instance of the game controller, providing access to global game resources and methods.
     * @param filePath The file path to the level map to be loaded, allowing for dynamic level loading.
     */
    public GameScreen(MazeRunnerGame game, String filePath) {





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

            firstLife.update(delta);
            secondLife.update(delta);
            thirdLife.update(delta);
            firstLife.draw(game.getSpriteBatch());
            secondLife.draw(game.getSpriteBatch());
            thirdLife.draw(game.getSpriteBatch());


            game.getSpriteBatch().end(); // Important to call this after drawing everything*/

            hud.stage.draw();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            // Calculate the starting point to draw the static rectangle
            int rectX = 112 - 16; // Center X - half the width of the rectangle
            int rectY = 112 - 16; // Center Y - half the height of the rectangle

            shapeRenderer.rect(rectX, rectY, 32, 32); // Draw a 32x32 rectangle

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

                                Texture objectsSheet = new Texture(Gdx.files.internal("objects.png"));
                                TextureRegion poofTexture = new TextureRegion(objectsSheet, 32, 32, 32, 32);
                                Poof poof = new Poof(new Sprite(poofTexture), gameMap, ghostPoofAnimation);
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

            if (goToGameOver) {
                Gdx.app.log("GameScreen", "Player has no lives left. Switching to GameOverScreen.");
                game.goToGameOver();

                return; // Ensure no further rendering logic is processed after this call
            }

            if (goToNextLevel) {
                Gdx.app.log("GameScreen", "Player has no lives left. Switching to GameOverScreen.");

                game.setLevelCounter(game.getLevelCounter() + 1);

                game.goToGame();

                return; // Ensure no further rendering logic is processed after this call
            }



        }

        if (escapeKeyPressed) {
            game.goToMenu();
        }
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
    @Override
    public void resize(int width, int height) {
        // original code camera.setToOrtho(false);
        //camera.viewportWidth = width;
        //camera.viewportHeight = height;
        gameViewport.update(width, height, true); // Update the viewport dimensions
        //gameViewport.apply(true);
        //camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0); // Center the camera
        //uiStage.getViewport().update(width, height, true);
        //camera.update();



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
    @Override
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

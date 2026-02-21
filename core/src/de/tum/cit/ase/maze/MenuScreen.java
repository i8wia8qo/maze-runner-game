package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;
import com.badlogic.gdx.utils.Align;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileFilter;

/**
 * The MenuScreen class is responsible for displaying the main menu of the game.
 * It extends the LibGDX Screen class and sets up the UI components for the menu.
 */
public class MenuScreen implements Screen {

    private final Stage stage;

    private boolean backToGameFromPause = false;

    MazeRunnerGame game;

    /**
     * Constructor for MenuScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public MenuScreen(MazeRunnerGame game) {

        this.game = game;

        var camera = new OrthographicCamera();
        camera.zoom = 1.5f; // Set camera zoom for a closer view

        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements
        //ExtendViewport viewport = new ExtendViewport(MazeRunnerGame.V_WIDTH, MazeRunnerGame.V_HEIGHT, camera);
        //stage = new Stage(viewport, game.getSpriteBatch());


        Table table = new Table();
        table.setFillParent(true);
        table.defaults().pad(6);
        stage.addActor(table);

        // Title: Maze Runner!
        Label title = new Label("Maze Runner!", game.getSkin(), "title");
        title.setAlignment(Align.center);
        table.add(title).padBottom(10).row();

        // Game description: How to play the game and achieve victory!
        String descriptionText =
                "A spooky pixel-art maze adventure!\n\n" +
                        "Find the key, dodge ghostly enemies, and unlock the door to escape.\n\n" +
                        "Survive through five haunted levels… or load your own custom map.";

        Label description = new Label(descriptionText, game.getSkin());
        description.setAlignment(Align.center);
        description.setWrap(true);

        table.add(description).width(650).padBottom(24).row();

        // Subtitle
        Label subtitle = new Label("— Menu —", game.getSkin(), "title");
        subtitle.setAlignment(Align.center);

        table.add(subtitle).padBottom(18).row();



        // Buttons: Start the game in campaign mode, or load a custom map!

        TextButton goToGameButton = new TextButton("Start the Game", game.getSkin());
        table.add(goToGameButton).width(330).padBottom(12).row();

        goToGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToGame();
            }
        });

        TextButton loadGameMapButton = new TextButton("Load Custom Map", game.getSkin());
        table.add(loadGameMapButton).width(330).padBottom(28).row();

        loadGameMapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                NativeFileChooserConfiguration conf = new NativeFileChooserConfiguration();
                conf.mimeFilter = "text/plain";
                conf.directory = Gdx.files.absolute(System.getProperty("user.home"));

                game.getFileChooser().chooseFile(conf, new NativeFileChooserCallback() {
                    @Override
                    public void onFileChosen(FileHandle file) {
                        String filePath = file.path();
                        Gdx.app.log("File Chooser", "Selected file path: " + filePath);
                        game.loadGameMap(filePath);
                    }

                    @Override
                    public void onCancellation() {
                        Gdx.app.log("File Chooser", "File selection cancelled.");
                    }

                    @Override
                    public void onError(Exception exception) {
                        Gdx.app.error("File Chooser", "Error occurred while selecting file: " + exception.getMessage());
                    }
                });
            }
        });

        // Game controls: instructions how to move and attack!

        table.add(new Label("Controls", game.getSkin(), "title")).padTop(10).padBottom(10).row();

        Label c1 = new Label("Move: Arrow Keys", game.getSkin());
        Label c2 = new Label("Attack: A", game.getSkin());
        Label c3 = new Label("Pause / Menu: ESC", game.getSkin());

        c1.setAlignment(Align.center);
        c2.setAlignment(Align.center);
        c3.setAlignment(Align.center);

        table.add(c1).padBottom(6).row();
        table.add(c2).padBottom(6).row();
        table.add(c3).padBottom(0).row();


        /*Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        // Add a label as a title
        table.add(new Label("Hello World from the Menu!", game.getSkin(), "title")).padBottom(80).row();

        // Create and add a button to go to the game screen
        TextButton goToGameButton = new TextButton("Go To Game", game.getSkin());
        table.add(goToGameButton).width(300).row();

        goToGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToGame(); // Change to the game screen when button is pressed
            }
        });

        // Create and add a button to go to the file chooser
        TextButton loadGameMapButton = new TextButton("Load New Map", game.getSkin());
        table.add(loadGameMapButton).width(300).padBottom(80).row();
        loadGameMapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Create a NativeFileChooserConfiguration with your file filter
                NativeFileChooserConfiguration conf = new NativeFileChooserConfiguration();
                conf.mimeFilter = "text/plain"; // Filter to .properties files

                // Set the starting directory
                conf.directory = Gdx.files.absolute(System.getProperty("user.home"));

                // Open the file chooser
                game.getFileChooser().chooseFile(conf, new NativeFileChooserCallback() {
                    @Override
                    public void onFileChosen(FileHandle file) {
                        // Get the selected file path as a string
                        String filePath = file.path();

                        // Now you have the file path in the 'filePath' variable
                        Gdx.app.log("File Chooser", "Selected file path: " + filePath);

                        // Handle the selected file, e.g., load your game map
                        game.loadGameMap(filePath);
                    }

                    @Override
                    public void onCancellation() {
                        // Handle cancellation
                        Gdx.app.log("File Chooser", "File selection cancelled.");
                    }

                    @Override
                    public void onError(Exception exception) {
                        // Handle error
                        Gdx.app.error("File Chooser", "Error occurred while selecting file: " + exception.getMessage());
                    }
                });
            }
        });


        table.row();
        // Controls
        table.add(new Label("Controls:", game.getSkin(), "title")).padBottom(40).row();

        // Pause the Game
        table.add(new Label("Move: Arrow Pad", game.getSkin())).padBottom(25).row();

        // Pause the Game
        table.add(new Label("Attack: Press A", game.getSkin())).padBottom(25).row();

        // Pause the Game
        table.add(new Label("Pause / Return to Menu: ESC", game.getSkin())).padBottom(25).row();*/


    }


    /**
     * Renders the game or UI elements for the current frame. This method clears the screen, updates the stage with any actor movements or changes,
     * and then draws the updated stage to the screen. It's called every frame to ensure the UI is responsive and visually up-to-date.
     *
     * @param delta The time in seconds since the last render call. This is used to ensure animations and transitions are smooth,
     *              regardless of the frame rate.
     */
    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update the stage
        stage.draw(); // Draw the stage


    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // Update the stage viewport on resize
    }

    @Override
    public void dispose() {
        // Dispose of the stage when screen is disposed
        stage.dispose();
    }

    @Override
    public void show() {
        // Set the input processor so the stage can receive input events
        Gdx.input.setInputProcessor(stage);
    }

    // The following methods are part of the Screen interface but are not used in this screen.
    @Override
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
    }
}

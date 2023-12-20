package com.example.basicgameapp;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.*;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.ui.FXGLButton;
import com.almasb.fxgl.ui.Position;
import com.almasb.fxgl.ui.ProgressBar;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.text.Font;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;
import static com.almasb.fxgl.dsl.FXGL.*;

public class BasicGameApp extends GameApplication{

    private Entity player;
    //    private Text coinText;
    private Text timerText;

    private ProgressBar hpBar;

    private ProgressBar coins;
    @Override
    protected void initSettings(GameSettings settings){
        //sa Windows nung game 600 X2 600 na siya
        settings.setWidth(800);
        settings.setHeight(800);
        settings.setTitle("WorldHELL 0 ");
        settings.setVersion("0.1");
        settings.setSceneFactory(new SceneFactory(){
            @Override
            public FXGLMenu newMainMenu(){
                return new WorldHellMenu(MenuType.MAIN_MENU);
            }

            @Override
            public FXGLMenu newGameMenu(){
                return new WorldHellMenu(MenuType.GAME_MENU);
            }
        });
        //settings.setGameMenuEnabled(true);
        settings.setMainMenuEnabled(true);
    }

    public static void main(String[] args){
        launch(args);                            //Lol nabasa ko na in a nutshell FXGL is a javafx application with game development features
    }
    public enum EntityType{                     //forda adding the collision detection
        PLAYER,
        EAGLE,
        ZOMBIE,
        SWIPE,
        BIRD,
        COIN,
        YOULOSE,
        BOSSJOSHUA
    }
    @Override
    protected void onPreInit() {
        loopBGM("Rip & Tear Doom OST.mp3");
        getSettings().setGlobalMusicVolume(0.2);
        getSettings().setGlobalSoundVolume(0.5);
    }
    @Override
    protected void initGame(){
        loopBGM("Rip & Tear Doom OST.mp3");

        // reset time
        set("time", 0);

        Node background = FXGL.getAssetLoader().loadTexture("bloody_paper2.jpg");
        background.setScaleX(4.0);
        background.setScaleY(4.0);

        GameView backgroundView = new GameView(background, 0);

        getGameScene().addGameView(backgroundView);

        //this is our player entity creation lolololol
        player = FXGL.entityBuilder()
                .type(EntityType.PLAYER)
                .viewWithBBox("girl_colored.png")
                .at(FXGL.getAppWidth() / 2.0, FXGL.getAppHeight() / 2.0) //positioning
                .with(new CollidableComponent(true))
                .buildAndAttach();

        player.translate(new Point2D(player.getWidth() / 2.0,player.getHeight() / 2.0 ));

//        getGameScene().getViewport().bindToEntity(player, player.getX() - player.getWidth(), player.getY() - player.getHeight());
//        getGameScene().getViewport().setBounds(-1266, -1865, 1266, 1865);

        Viewport viewport = getGameScene().getViewport();
        viewport.setBounds(-1266, -1865, 1266, 1865);
        viewport.bindToEntity(player, getAppWidth() / 2.0 - player.getWidth() / 2, getAppHeight() / 2.0 - player.getHeight() / 2);

        getGameWorld().addEntityFactory(new Factory());



        //spawning xombies at any random points and making them chase the player
        run(() -> {

            Entity zombie = spawn("zombie", new Point2D(-1,-1));
            Entity eagle = spawn("eagle", new Point2D(-1,-1));
            Entity bird = spawn("bird", new Point2D(-1,-1));

            // spawn boss joshua if time is 4 mins
            if (geti("time") >= 120) {
                Entity bossJoshua = spawn("bossJoshua", new Point2D(-1,-1));
                initPosition(bossJoshua);
                bossJoshua.setRotation(8d);
                run(() -> {
                    Point2D playerCenter = player.getCenter().subtract(player.getWidth(), player.getHeight());

                    bossJoshua.translateTowards(player.getCenter().subtract(zombie.getWidth()/2.0, zombie.getHeight()/2.0), 3);

                }, Duration.seconds(0));

                run(() -> {
                    bossJoshua.setRotation(bossJoshua.getRotation() * -1);
                }, Duration.millis(100));
            }

            initPosition(zombie);
            initPosition(eagle);
            initPosition(bird);

            zombie.setRotation(8d);
            bird.setRotation(8d);

            run(() -> {
                Point2D playerCenter = player.getCenter().subtract(player.getWidth(), player.getHeight());

                zombie.translateTowards(player.getCenter().subtract(zombie.getWidth()/2.0, zombie.getHeight()/2.0), 1);
                eagle.translateTowards(playerCenter.add(eagle.getWidth() /2.0, eagle.getHeight() / 2.0), 1);
                bird.translateTowards(player.getCenter().subtract(bird.getWidth()/2.0, bird.getHeight()/2.0), 1);

            }, Duration.seconds(0));

            run(() -> {
                zombie.setRotation(zombie.getRotation() * -1);
                bird.setRotation(bird.getRotation() * -1);
            }, Duration.millis(400));



        }, Duration.seconds(0.5));
        // player swipes
        run(() -> {
            Entity swipe = spawn("swipe", new Point2D(player.getX() + 128, player.getY()));
            play("swipe.wav");
            if (geti("hangerLevel") >= 2){
                Entity swipe2 = spawn("swipe", new Point2D(player.getX() - 128, player.getY()));
                swipe2.setRotation(180);
                despawnWithDelay(swipe2, Duration.millis(100));
            }
            despawnWithDelay(swipe, Duration.millis(100));
        }, Duration.seconds(1.0 / geti("hangerLevel")));

        run(() -> inc("time", + 1), Duration.seconds(1));

    }


    @Override
    protected void onUpdate(double tpf) {
        coins.setCurrentValue(geti ("coins"));
        hpBar.setCurrentValue(geti("hp"));
//        hpText.setText(String.valueOf(geti("hp")));
        timerText.setText("Time: " + geti("time"));

        // game ends in 5 mins
        if (geti("time") >= 180) {
            play("napakagaling_ni_sir.wav");
            youWin();
        }

        if (geti("coins") >= geti("coin_trigger")) {
            levelUp();
            set("coins", 0);
        }
        // player death
        if (geti("hp") <= 0){
            // score = coins / time + time
            set("score", geti("coins") / (int) getGameTimer().getNow() * 100 + (int) getGameTimer().getNow() / 10);
            gameOver();
        }

        if (geti("time") == 120 && geti("joshua_flag") == 1){
            play("joshuas_coming.wav");
            set("joshua_flag", 0);
        }
    }

    void levelUp() {
        getGameController().pauseEngine();
        upgradeMenu();
    }

    @Override
    protected void initInput() {
        FXGL.onKey(KeyCode.D, () -> {
            player.translateX(4);
        });

        FXGL.onKey(KeyCode.A, () -> {
            player.translateX(-5);
        });

        FXGL.onKey(KeyCode.W, () -> {
            player.translateY(-5);
        });

        FXGL.onKey(KeyCode.S, () -> {
            player.translateY(5);
        });
    }

    @Override
    protected void initUI(){

        timerText = FXGL.getUIFactoryService().newText("",Color.BLACK, 24.0);
        timerText.setTranslateX(50);
        timerText.setTranslateY(50);

        coins = new ProgressBar();
        coins.setMaxValue(10);
        coins.setMinValue(0);
        coins.setWidth(getAppWidth());
        coins.setHeight(20);
        coins.setFill(Color.GOLD);

        hpBar = new ProgressBar();
        hpBar.setMaxValue(100);
        hpBar.setMinValue(0);
        hpBar.setWidth(player.getWidth());
        hpBar.setHeight(10);
        hpBar.setFill(Color.RED);

        FXGL.getGameScene().addUINode(timerText);
        addUINode(coins, 0,0 );
        addUINode(hpBar, player.getX() - hpBar.getBackgroundBar().getWidth(), player.getY() + 10);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {// creating a global variable named vars
        vars.put("score", 0);
        vars.put("coins", 0);
        vars.put("hp", 100);
        vars.put("time", 0);
        vars.put("hangerLevel", 1);
        vars.put("hangerSpeed", 1);
        vars.put("coin_trigger", 10);
        vars.put("joshua_flag", 1);
    }

    @Override
    protected void initPhysics(){
        //Collision Handler can only handle two types of entities

        //Collision handler for the zombies
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.ZOMBIE) {
            @Override
            protected void onCollisionBegin(Entity player, Entity zombie){
                // TODO: make a damage component of zombie
                inc("hp", -10);
                getGameScene().getViewport().shakeTranslational(20);
                play("player_pain.wav");
            }
        });

        //Collision handler for the eagles
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.EAGLE) {
            @Override
            protected void onCollisionBegin(Entity player, Entity eagle){
                // damage player
                inc("hp", -10);
                getGameScene().getViewport().shakeTranslational(20);
                play("player_pain.wav");
            }
        });

        // Collision handler for bossJoshua
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.BOSSJOSHUA) {
            @Override
            protected void onCollision(Entity player, Entity bossJoshua){
                // damage player
                inc("hp", -1);
                getGameScene().getViewport().shakeTranslational(20);
                play("player_pain.wav");
            }
        });
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.SWIPE, EntityType.EAGLE) {
            @Override
            protected void onCollisionBegin(Entity swipe, Entity eagle){
                FXGL.play("eagle_death.wav");
                FXGL.getGameWorld().removeEntity(eagle);
                if (FXGLMath.randomBoolean())
                    spawn("coin", eagle.getPosition());

            }
        });

        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.COIN) {
            @Override
            protected void onCollisionBegin(Entity player, Entity coin){
                FXGL.play("coin_get.wav");
                FXGL.getGameWorld().removeEntity(coin);
                inc("coins", +1);
            }
        });
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.SWIPE, EntityType.ZOMBIE) {
            @Override
            protected void onCollisionBegin(Entity swipe, Entity zombie){
                FXGL.play("zombie_pain.wav");
                FXGL.getGameWorld().removeEntity(zombie);
                if (FXGLMath.randomBoolean())
                    spawn("coin", zombie.getPosition());

            }
        });
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.SWIPE, EntityType.BIRD) {
            @Override
            protected void onCollisionBegin(Entity swipe, Entity bird){
                FXGL.play("bird_death.wav");
                FXGL.getGameWorld().removeEntity(bird);
                if (FXGLMath.randomBoolean())
                    spawn("coin", bird.getPosition());
            }
        });

        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.SWIPE, EntityType.BOSSJOSHUA) {
            @Override
            protected void onCollisionBegin(Entity swipe, Entity bossJoshua){
                FXGL.play("joshua_laugh.wav");
                // 50% chance to kill boss joshua
                if (FXGLMath.randomBoolean()){
                    FXGL.play("joshua_death.wav");
                    FXGL.getGameWorld().removeEntity(bossJoshua);
                    spawn("coin", bossJoshua.getPosition());
                }
            }
        });
    }

    void initPosition(Entity entity) {
        entity.setPosition(FXGLMath.randomPoint(new Rectangle2D(-getAppWidth() * .9 - entity.getWidth() * .5,-getAppHeight() * .9 - entity.getHeight() * .8,getAppWidth() * 3, getAppHeight() * 3)));
        // if enemy is in screen, remove that enemy
        if (entity.getX() < getAppWidth() + player.getRightX() && entity.getX() > player.getX() - getAppWidth() / 2.0
                && entity.getY() > player.getBottomY() - getAppHeight() / 2.0 && entity.getY() < getAppHeight() + player.getY()
        ){
            getGameWorld().removeEntity(entity);
        }
    }

    void gameOver() {
        // Display an image in the dialog
        var image = FXGL.texture("skull.png");
        image.setFitWidth(300);
        image.setFitHeight(300);
        // Create a VBox to hold the image and a message
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        Label showMess=  new Label("   You Lose!\nYour Score: " + geti("score"));
        showMess.setTextFill(Color.WHITE);
        showMess.setFont(Font.font(35));
        vbox.getChildren().addAll(image, showMess);

        Button goMain= FXGL.getUIFactoryService().newButton("Main Menu");


        // Show the dialog with the image and message
        FXGL.getDialogService().showBox("Game Over", vbox, goMain);
        goMain.setOnAction(event -> {
            // Go back to the WorldHellMenu
            getGameController().gotoMainMenu();
        });
        getAudioPlayer().stopAllMusic();
    }
    void youWin() {

        // Display an image in the dialog
        var image = FXGL.texture("skull.png");
        image.setFitWidth(300);
        image.setFitHeight(300);
        // Create a VBox to hold the image and a message
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        Label showMess=  new Label("   You WIN!\nYour Score: " + geti("score"));
        showMess.setTextFill(Color.WHITE);
        showMess.setFont(Font.font(35));
        vbox.getChildren().addAll(image, showMess);

        Button goMain= FXGL.getUIFactoryService().newButton("Main Menu");


        // Show the dialog with the image and message
        FXGL.getDialogService().showBox("YOU WIN!!!", vbox, goMain);
        goMain.setOnAction(event -> {
            // Go back to the WorldHellMenu
            getGameController().gotoMainMenu();
        });
        //getAudioPlayer().stopAllMusic();


//        getAudioPlayer().stopAllMusic();
//        showMessage("You WIN!\nYour Score: " + geti("score"), () -> {
//            getGameController().gotoMainMenu();
            loopBGM("Rip & Tear Doom OST.mp3");
//        });
    }

    void upgradeMenu() {
        // Create a VBox to hold the image and a message
        VBox vbox = new VBox(10);
        Button upgradeHangerButton = new Button("Upgrade Hanger", new ImageView(FXGL.image("hanger.png")));
        Button healButton = new Button("Heal", new ImageView(FXGL.image("redHeart.jpg")));
        upgradeHangerButton.setStyle("-fx-background-color: transparent;");
        healButton.setStyle("-fx-background-color: transparent;");
        vbox.getChildren().addAll(upgradeHangerButton, healButton);
        upgradeHangerButton.setOnAction(event -> {
            inc("hangerLevel", +1);
            inc("coin_trigger", +10);
            coins.setMaxValue(geti("coin_trigger"));
            getGameController().resumeEngine();
        });
        healButton.setOnAction(event -> {
            var healthHeal = 30;
            if (geti("hp") < 100 - healthHeal)
                inc("hp", + healthHeal);
            else
                set("hp", 100);
            inc("coin_trigger", +10);
            coins.setMaxValue(geti("coin_trigger"));
            getGameController().resumeEngine();
        });
        getAudioPlayer().stopAllMusic();

        getDialogService().showBox("UPGRADE", new VBox(), upgradeHangerButton, healButton);
    }
}
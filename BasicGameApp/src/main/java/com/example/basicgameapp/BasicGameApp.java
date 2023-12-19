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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

public class BasicGameApp extends GameApplication{

    private Entity player;
//    private Text coinText;
    private Text timerText;

    private ProgressBar hpBar;

    private ProgressBar coins;
    private Boolean isPaused = false;
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
        COIN
    }
    @Override
    protected void onPreInit() {
        getSettings().setGlobalMusicVolume(0.2);
        getSettings().setGlobalSoundVolume(0.5);
    }
    @Override
    protected void initGame(){

        loopBGM("Doom Eternal OST - The Only Thing They Fear Is You (Mick Gordon) [Doom Eternal Theme].mp3");
        // lower bgm volume


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


        run(() -> {

            Entity zombie = spawn("zombie", new Point2D(-1,-1));
            Entity eagle = spawn("eagle", new Point2D(-1,-1));
            Entity bird = spawn("bird", new Point2D(-1,-1));

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
            if (geti("hangerLevel") >= 2){
                Entity swipe2 = spawn("swipe", new Point2D(player.getX() - 128, player.getY()));
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
        if (geti("time") >= 600) {
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
    }

    void levelUp() {
        // TODO: FIX THIS SHIT
        if (isPaused) {
            return;
        }
        isPaused = true;
        getGameController().pauseEngine();
        var vbox = new VBox(3);

        // TODO: make a level up menu
        var choice1 = new FXGLButton("Upgrade Hanger");
        var choice2 = new FXGLButton("Heal");


        choice1.setTextFill(Color.BLACK);
        choice1.setText("Upgrade Hanger");
        choice1.setOnMouseClicked(e -> {
            inc("hangerLevel", +1);
            vbox.setVisible(false);
            isPaused = false;
            inc("coin_trigger", +10);
            coins.setMaxValue(geti("coin_trigger"));
            getGameController().resumeEngine();
        });

        choice2.setTextFill(Color.BLACK);
        choice2.setOnMouseClicked(e -> {
            var healthHeal = 30;
            if (geti("hp") < 100 - healthHeal)
                inc("hp", + healthHeal);
            else
                set("hp", 100);
            vbox.setVisible(false);
            isPaused = false;
            inc("coin_trigger", +10);
            coins.setMaxValue(geti("coin_trigger"));
            getGameController().resumeEngine();
        });


        vbox.getChildren().addAll(choice1, choice2);

        vbox.setTranslateX(getAppWidth() / 2.0 - vbox.getWidth() * 2.0);
        vbox.setTranslateY(getAppHeight() / 2.0 - vbox.getHeight() / 2.0);

        getGameScene().addChild(vbox);
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
       //For the display of the coins count
//       coinText = FXGL.getUIFactoryService().newText("",Color.BLACK, 20.0);
//       coinText.setTranslateX(50);
//       coinText.setTranslateY(70);

       timerText = FXGL.getUIFactoryService().newText("",Color.BLACK, 24.0);
       timerText.setTranslateX(50);
       timerText.setTranslateY(50);

//       hpText = FXGL.getUIFactoryService().newText("",Color.BLACK, 24.0);
//       hpText.setTranslateX(50);
//       hpText.setTranslateY(90);
//
//       hpText.setText(String.valueOf(geti("hp")));

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
//       FXGL.getGameScene().addUINode(coinText);
//       FXGL.getGameScene().addUINode(hpText);
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
                getGameScene().getViewport().shakeTranslational(5);
                play("ai.wav");
            }
        });

        //Collision handler for the eagles
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.EAGLE) {
            @Override
            protected void onCollisionBegin(Entity player, Entity eagle){
                // damage player
                inc("hp", -10);
                getGameScene().getViewport().shakeTranslational(5);
                play("ai.wav");
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
//                coinText.setText("Coins: " + geti("coins"));


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
        showMessage("You Lose!\nYour Score: " + geti("score"), () -> getGameController().gotoMainMenu());
        getAudioPlayer().stopAllMusic();
    }
    void youWin() {
        showMessage("You Win!\nYour Score: " + geti("score"), () -> getGameController().gotoMainMenu());
        getAudioPlayer().stopAllMusic();
    }
}
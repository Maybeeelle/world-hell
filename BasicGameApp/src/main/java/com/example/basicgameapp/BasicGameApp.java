package com.example.basicgameapp;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.*;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.ui.Position;
import com.almasb.fxgl.ui.ProgressBar;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;

import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

public class BasicGameApp extends GameApplication{

    private Entity player;
    private Text coinText;
    private Text timerText;
    private Text hpText;
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
    }
    @Override
    protected void initGame(){

        loopBGM("Doom Eternal OST - The Only Thing They Fear Is You (Mick Gordon) [Doom Eternal Theme].mp3");

        // reset time
        set("time", 0);

        Node background = FXGL.getAssetLoader().loadTexture("background.jpg");
        background.setScaleX(5.0);
        background.setScaleY(5.0);

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
            // player death
            if (geti("hp") <= 0){
                // score = coins / time + time
                set("score", geti("coins") / (int) getGameTimer().getNow() * 100 + (int) getGameTimer().getNow());
                gameOver();
            }

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
            despawnWithDelay(swipe, Duration.millis(100));
        }, Duration.seconds(1));

        run(() -> inc("time", + 1), Duration.seconds(1));

    }


    @Override
    protected void onUpdate(double tpf) {
        hpText.setText(String.valueOf(geti("hp")));
        timerText.setText("Time: " + geti("time"));

        // game ends in 5 mins
        if (geti("time") >= 600) {
            youWin();
        }
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
       coinText = FXGL.getUIFactoryService().newText("",Color.BLACK, 20.0);
       coinText.setTranslateX(50);
       coinText.setTranslateY(70);

       timerText = FXGL.getUIFactoryService().newText("",Color.BLACK, 24.0);
       timerText.setTranslateX(50);
       timerText.setTranslateY(50);

       hpText = FXGL.getUIFactoryService().newText("",Color.BLACK, 24.0);
       hpText.setTranslateX(50);
       hpText.setTranslateY(90);

       hpText.setText(String.valueOf(geti("hp")));

       FXGL.getGameScene().addUINode(timerText);
       FXGL.getGameScene().addUINode(coinText);
       FXGL.getGameScene().addUINode(hpText);
   }

    @Override
    protected void initGameVars(Map<String, Object> vars) {// creating a global variable named vars
        vars.put("score", 0);
        vars.put("coins", 0);
        vars.put("hp", 100);
        vars.put("time", 0);
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
                play("ai.wav");
            }
        });

        //Collision handler for the eagles
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.EAGLE) {
            @Override
            protected void onCollisionBegin(Entity player, Entity eagle){
                // damage player
                inc("hp", -10);
                play("ai.wav");
            }
        });
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.SWIPE, EntityType.EAGLE) {
            @Override
            protected void onCollisionBegin(Entity swipe, Entity eagle){
                FXGL.play("eagle_death.wav");
                FXGL.getGameWorld().removeEntity(eagle);
                Entity coin = spawn("coin", eagle.getPosition());

            }
        });

        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.COIN) {
            @Override
            protected void onCollisionBegin(Entity player, Entity coin){
                //FXGL.play("eagle_death.wav");
                FXGL.getGameWorld().removeEntity(coin);
                inc("coins", +1);
                coinText.setText("Coins: " + geti("coins"));


            }
        });
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.SWIPE, EntityType.ZOMBIE) {
            @Override
            protected void onCollisionBegin(Entity swipe, Entity zombie){
                FXGL.play("slayy.wav");
                FXGL.getGameWorld().removeEntity(zombie);
                Entity coin = spawn("coin", zombie.getPosition());

            }
        });
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.SWIPE, EntityType.BIRD) {
            @Override
            protected void onCollisionBegin(Entity swipe, Entity bird){
                FXGL.play("eagle_death.wav");
                FXGL.getGameWorld().removeEntity(bird);
                Entity coin = spawn("coin", bird.getPosition());
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
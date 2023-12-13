package com.example.basicgameapp;
import java.math.*;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.GameView;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.Scene;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Map;
import java.util.Timer;

import static com.almasb.fxgl.dsl.FXGL.*;

public class BasicGameApp extends GameApplication{

//    SubScene gameOver = new SubScene(new Group(new Text("Game Over")),800,800);
    private GameSettings settings;//pagdeclare kay player na may datatype na Entity
    private Entity player;
    private Text coinText;
    private Text timerText;
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
        PLAYER,EAGLE, ZOMBIE, SWIPE, BIRD, WIN, COIN
    }


    @Override
    protected void onPreInit() {
        loopBGM("Doom Eternal OST - The Only Thing They Fear Is You (Mick Gordon) [Doom Eternal Theme].mp3");
    }

    @Override
    protected void initGame(){

//        getGameScene().setBackgroundColor(Paint.valueOf("gray"));



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
        getGameScene().getViewport().bindToEntity(player, player.getX() - player.getWidth(), player.getY() - player.getHeight());

        getGameScene().getViewport().setBounds(-1266, -1865, 1266, 1865);

        player.addComponent(new HealthComponent());

        player.getComponent(HealthComponent.class).setHealth(100);
        getGameWorld().addEntityFactory(new Factory());

        run(() -> {

            if (player.getComponent(HealthComponent.class).getHealth() <= 0){
                set("score", geti("coins") / (int) getGameTimer().getNow() * 100);
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

            getGameTimer().runAtInterval(() -> {
                zombie.setRotation(zombie.getRotation() * -1);
                bird.setRotation(bird.getRotation() * -1);
            }, Duration.millis(400));



        }, Duration.seconds(0.5));
        // player swipes
        getGameTimer().runAtInterval(() -> {
            Entity swipe = spawn("swipe", new Point2D(player.getX() + 128, player.getY()));
            despawnWithDelay(swipe, Duration.millis(100));
        }, Duration.millis(1000));

    }


    @Override
    protected void onUpdate(double tpf) {
        timerText.setText("Time: " + (int) getGameTimer().getNow());

        if (getGameTimer().getNow() > 600) {
            getGameController().gotoMainMenu();
            // win
            Entity win = spawn("win", new Point2D(player.getX(), player.getY()));
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


       FXGL.getGameScene().addUINode(timerText);
       FXGL.getGameScene().addUINode(coinText);
   }

    @Override
    protected void initGameVars(Map<String, Object> vars) {// creating a global variable named vars
        vars.put("score", 0);
        vars.put("coins", 0);
    }

    @Override
    protected void initPhysics(){
        //Collision Handler can only handle two types of entities
        //Collision handler for the zombies
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.ZOMBIE) {
            @Override
            protected void onCollisionBegin(Entity player, Entity zombie){
                // TODO: make a damage component of zombie
                player.getComponent(HealthComponent.class).setHealth(player.getComponent(HealthComponent.class).getHealth() - 10); // zombie damages player by 10 points
                play("ai.wav");
            }
        });

        //Collision handler for the eagles

        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.EAGLE) {
            @Override
            protected void onCollisionBegin(Entity player, Entity eagle){
                // damage player
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
        if (entity.getX() < getAppWidth() && entity.getX() > 0
                && entity.getY() > 0 && entity.getY() < getAppHeight()
        ){
            getGameWorld().removeEntity(entity);
        }
    }

    void gameOver() {
        showMessage("Your Score: " + geti("score"), () -> getGameController().gotoMainMenu());
    }
}
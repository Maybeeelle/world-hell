package com.example.basicgameapp;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
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
import javafx.scene.input.KeyCode;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Map;
import static com.almasb.fxgl.dsl.FXGL.*;

public class BasicGameApp extends GameApplication{

    private GameSettings settings;//pagdeclare kay player na may datatype na Entity
    private Entity player;
    @Override
    protected void initSettings(GameSettings settings){
                                                //sa Windows nung game 600 X2 600 na siya
        settings.setWidth(800);
        settings.setHeight(800);
        settings.setTitle("WorldHELL 0 ");
        settings.setVersion("0.1");//Lol anong version?
        settings.setSceneFactory(new sceneFactory());
    }

    public static void main(String[] args){
        launch(args);                            //Lol nabasa ko na in a nutshell FXGL is a javafx application with game development features
    }


    public enum EntityType{                     //forda adding the collision detection
        PLAYER,EAGLE, ZOMBIE
    }
    @Override
    protected void initGame(){


        //this is our player entity creation lolololol
        player = FXGL.entityBuilder()
                .type(EntityType.PLAYER)
                .scale(0.25, 0.25)//for da adding the collision detection
                .at(FXGL.getAppWidth() / 2.0, FXGL.getAppHeight() / 2.0) //positioning
                                                //.view(new Rectangle(25,25, Color.BLUE))
                .view("girl_colored.png")
                .viewWithBBox("girl_bbox.png")
                //.viewWithBBox(new Rectangle(30,60))

                .with(new CollidableComponent(true))
                .buildAndAttach();              //Anong ibigsabihin nito ni Build and Attach?? nvm gets ko na


        getGameScene().getViewport().bindToEntity(player, player.getX() - player.getWidth() * .75, player.getY() - player.getHeight());

        player.addComponent(new HealthComponent());

        player.getComponent(HealthComponent.class).setHealth(100);
        getGameWorld().addEntityFactory(new Factory());

        run(() -> {

            if (player.getComponent(HealthComponent.class).getHealth() <= 0){
                FXGL.getGameWorld().removeEntity(player);
            }
//            Entity zombie = spawn("zombie", FXGLMath.randomPoint(new Rectangle2D(0, 0, getAppWidth(), getAppHeight())));
//            Entity eagle = spawn("eagle", FXGLMath.randomPoint(new Rectangle2D(0, 0, getAppWidth(), getAppHeight())));
            Entity zombie = spawn("zombie", new Point2D(-1,-1));
            Entity eagle = spawn("eagle", new Point2D(-1,-1));
                                                 //yung mga sizes ng eagle at zombies
            zombie.setScaleX(0.08);
            zombie.setScaleY(0.08);

            zombie.setPosition(FXGLMath.randomPoint(new Rectangle2D(-getAppWidth() * .9 - zombie.getWidth() * .5,-getAppHeight() * .9 - zombie.getHeight() * .8,getAppWidth() * 3, getAppHeight() * 3)));
            if (zombie.getX() < getAppWidth() && zombie.getX() > 0
            && zombie.getY() > 0 && zombie.getY() < getAppHeight()
            ){
                FXGL.getGameWorld().removeEntity(zombie);
            }
            eagle.setScaleX(0.06);
            eagle.setScaleY(0.06);
            eagle.setPosition(FXGLMath.randomPoint(new Rectangle2D(-getAppWidth() * .5 - eagle.getWidth() * .2,-getAppHeight() * .5 - eagle.getHeight() * .2,getAppWidth() * 2, getAppHeight() * 2)));
            if (eagle.getX() < getAppWidth() && eagle.getX() > 0
                    && eagle.getY() > 0 && eagle.getY() < getAppHeight()
            ){
                FXGL.getGameWorld().removeEntity(eagle);
            }
            run(() -> {
                //Hindi ko masyadong gets itonf getCenter huehue TmT
                zombie.translateTowards(player.getCenter().subtract(zombie.getCenter()).multiply(4), 1);
                zombie.rotateBy(50);
                eagle.translateTowards(player.getCenter().subtract(eagle.getCenter()).multiply(4), 1);
//                eagle.translateTowards(player.getCenter().multiply(2), 1.5);

            }, Duration.seconds(0));
        }, Duration.seconds(0.5));

    }

    @Override
    protected void initInput() {
        FXGL.onKey(KeyCode.D, () -> {
            player.translateX(4);
            FXGL.inc("pixelsMoved", +5);
        });

        FXGL.onKey(KeyCode.A, () -> {
            player.translateX(-5);
            FXGL.inc("pixelsMoved", +5);
        });

        FXGL.onKey(KeyCode.W, () -> {
            player.translateY(-5);
            FXGL.inc("pixelsMoved", +5);
        });

        FXGL.onKey(KeyCode.S, () -> {
            player.translateY(5);
            FXGL.inc("pixelsMoved", +5);
        });
    }

   @Override
    protected void initUI(){
//        var backgroundPicture= getAssetLoader().loadTexture("paper.jpg");
//        backgroundPicture.setTranslateX(0);
//        backgroundPicture.setTranslateY(0);
//       backgroundPicture.setScaleX(2);
//       backgroundPicture.setScaleY(2);
        Text textPixels= new Text();
        textPixels.setTranslateX(50);
        textPixels.setTranslateY(100);
//       getGameScene().addUINode(backgroundPicture);
        Text label= new Text("Pixels Moved: ");
       label.setTranslateX(50);
       label.setTranslateY(85);
       textPixels.textProperty().bind(FXGL.getWorldProperties().intProperty("pixelsMoved").asString());
//       textPixels.textProperty().bind(FXGL.getWorldProperties().intProperty("playerPosition").asString());
       FXGL.getGameScene().addUINode(label);
       FXGL.getGameScene().addUINode(textPixels); //add to the scene graph
   }

    @Override
    protected void initGameVars(Map<String, Object> vars) {// creating a global variable named vars
        vars.put("pixelsMoved", 0);
//        vars.put("playerPosition", player.getCenter());
    }

    @Override
    protected void initPhysics(){
        //Collision Handler can only handle two types of entities
        //Collision handler for the zombies
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.ZOMBIE) {
            @Override
            protected void onCollisionBegin(Entity player, Entity zombie){
                // TODO: make a damage component of zombie
                FXGL.play("slayy.wav");
                //player.getComponent(HealthComponent.class).setHealth(player.getComponent(HealthComponent.class).getHealth() - 10); // zombie damages player by 10 points
                FXGL.getGameWorld().removeEntity(zombie);
            }
        });

        //Collision handler for the eagles

        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.EAGLE) {
            @Override
            protected void onCollisionBegin(Entity player, Entity eagle){
                FXGL.play("eagle_death.wav");
                FXGL.getGameWorld().removeEntity(eagle);

            }
        });
    }
}

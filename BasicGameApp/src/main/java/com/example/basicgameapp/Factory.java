package com.example.basicgameapp;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;

public class Factory implements EntityFactory {
    @Spawns("zombie")
    public Entity newZombie(SpawnData data){
        return FXGL.entityBuilder(data)
                .type(BasicGameApp.EntityType.ZOMBIE)
                //.view("zombie_final.png")
                .viewWithBBox("zombie_colored.png")
                .with(new ProjectileComponent(new Point2D(0,0), 500))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("eagle") //para kay eagle
    public Entity newEagle(SpawnData data){
        return FXGL.entityBuilder(data)
                .type(BasicGameApp.EntityType.EAGLE)
                //.view("zombie_eagle.png")
                .viewWithBBox("eagle_colored.png")
                .with(new ProjectileComponent(new Point2D(0,0), 500))
                .with(new CollidableComponent(true))
                .build();
    }
}
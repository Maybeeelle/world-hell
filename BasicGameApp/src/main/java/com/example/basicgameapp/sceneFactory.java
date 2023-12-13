package com.example.basicgameapp;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.app.scene.SceneFactory;

public class sceneFactory extends SceneFactory {
    @Override
    public FXGLMenu newMainMenu(){
        return new WorldHellMenu(MenuType.MAIN_MENU);
    }

    @Override
    public FXGLMenu newGameMenu(){
        return new WorldHellMenu(MenuType.GAME_MENU);
    }
}

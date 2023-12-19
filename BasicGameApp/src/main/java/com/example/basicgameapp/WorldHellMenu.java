package com.example.basicgameapp;


import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

public class WorldHellMenu extends FXGLMenu {
        public WorldHellMenu(MenuType type) {
            super(type);

            getAudioPlayer().resumeAllMusic();
            //customizing menu,adding the background image of the menu here
//            setBackgroundImage(System.getProperty("user.dir") + "/src/main/resources/assets/textures/BG.png");
            //dito sana yung mga buttons chuchu

            ImageView iv = new ImageView(FXGL.image("BG.png"));
            var play = new VBox(3,
                    new MenuButton(".          .", () -> fireNewGame())
            );

            var quit = new VBox(3,
                    new MenuButton(".          .", () -> fireExit())
            );

            var credits = new VBox(
                    new MenuButton(".             .", () -> showCredits())
            );
            //menuBox.setAlignment(Pos.TOP_CENTER);
            play.setTranslateY(getAppWidth() / 2.0 + 140);
            play.setTranslateX(getAppHeight() - 470);

            quit.setTranslateY(getAppWidth() / 2.0 + 290);
            quit.setTranslateX(getAppHeight() - 470);

            credits.setTranslateY(610);
            credits.setTranslateX(475);

            getContentRoot().getChildren().addAll(iv, play, quit, credits);

        }

        private void setBackgroundImage(String imagePath){
            Image backgroundImage= new Image(imagePath);

            BackgroundImage background = new BackgroundImage(
                    backgroundImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT
            );
            Background backgroundWithImage= new Background(background);

            getContentRoot().setBackground(backgroundWithImage);

        }

        private void showCredits() {
            getDialogService().showMessageBox("Sprite assets: Maybelle Patetico\n" +
                    "\n" +
                    "Background assets: Google Stock Images\n" +
                    "\n" +
                    "BGM: Doom Eternal OST - The Only Thing They Fear Is You (Mick Gordon) [Doom Eternal Theme]\n" +
                    "     \n" +
                    "     https://www.youtube.com/watch?v=kpnW68Q8ltc\n" +
                    "Made with FXGL by AlmasB");
        }


        private static class MenuButton extends Parent {
            MenuButton(String name, Runnable action) {
                var text = getUIFactoryService().newText(name, Color.BLACK, 36.0);
                text.setStrokeWidth(1.5);
                text.strokeProperty().bind(text.fillProperty());

                text.fillProperty().bind(
                        Bindings.when(hoverProperty())
                                .then(Color.RED)
                                .otherwise(Color.BLACK)
                );

                setOnMouseClicked(e ->{
                    action.run();
                });
                setPickOnBounds(true);
                getChildren().add(text);
            }
        }
    }

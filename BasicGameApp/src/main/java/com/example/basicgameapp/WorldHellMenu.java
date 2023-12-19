package com.example.basicgameapp;


import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getDialogService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameController;

public class WorldHellMenu extends FXGLMenu {
        public WorldHellMenu(MenuType type) {
            super(type);
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

            var help= new VBox(
                    new MenuButton(".             .", () -> showHelp())
            );
            //menuBox.setAlignment(Pos.TOP_CENTER);
            play.setTranslateY(getAppWidth() / 2.0 + 140);
            play.setTranslateX(getAppHeight() - 470);

            quit.setTranslateY(getAppWidth() / 2.0 + 290);
            quit.setTranslateX(getAppHeight() - 470);

            credits.setTranslateY(610);
            credits.setTranslateX(475);

            help.setTranslateY(610);
            help.setTranslateX(171);

            getContentRoot().getChildren().addAll(iv, play, quit, credits,help);

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
        Stage creditsStage = new Stage();
        creditsStage.initStyle(StageStyle.UTILITY);
        creditsStage.setTitle("Credits");

        // Load and set the background image for the credits stage
        ImageView creditsBackground = new ImageView(FXGL.image("theDevelopers.jpg"));
        creditsBackground.setFitWidth(800); // Set the desired width
        creditsBackground.setFitHeight(800);

        // Create a VBox to hold the credits text
        VBox creditsBox = new VBox(
                new Label("    "),
                createBackToMenuButton()
        );

        creditsBox.setAlignment(Pos.CENTER);
        creditsBox.setSpacing(10);

        // Add the VBox to the scene
        StackPane creditsRoot = new StackPane(creditsBackground, creditsBox);
        creditsStage.setScene(new Scene(creditsRoot, 800, 800));

        // Show the credits stage
        creditsStage.show();

        // Adjust alignment and translation for the backButton directly
        StackPane.setAlignment(creditsBox, Pos.BOTTOM_LEFT);
        creditsBox.setTranslateX(10);
        creditsBox.setTranslateY(-10);
        StackPane.setAlignment(createBackToMenuButton(), Pos.BOTTOM_RIGHT);
    }


        private void showHelp(){
            Stage creditsStage = new Stage();
            creditsStage.initStyle(StageStyle.UTILITY);
            creditsStage.setTitle("Credits");

            // Load and set the background image for the credits stage
            ImageView creditsBackground = new ImageView(FXGL.image("rulesOfGames.png"));
            creditsBackground.setFitWidth(800); // Set the desired width
            creditsBackground.setFitHeight(800);

            // Create a VBox to hold the credits text
            VBox creditsBox = new VBox(
                    new Label("    "),
                    createBackToMenuButton()
            );

            creditsBox.setAlignment(Pos.CENTER);
            creditsBox.setSpacing(10);

            // Add the VBox to the scene
            StackPane creditsRoot = new StackPane(creditsBackground, creditsBox);
            creditsStage.setScene(new Scene(creditsRoot, 800, 800));

            // Show the credits stage
            creditsStage.show();

            // Adjust alignment and translation for the backButton directly
            StackPane.setAlignment(creditsBox, Pos.BOTTOM_LEFT);
            creditsBox.setTranslateX(10);
            creditsBox.setTranslateY(-10);
            StackPane.setAlignment(createBackToMenuButton(), Pos.BOTTOM_RIGHT);
        }




    private Button createBackToMenuButton() {
        Button backButton = new Button("Back to Menu");

        backButton.setTranslateX(330);
        backButton.setTranslateY(350);
        backButton.setOnAction(event -> {
            // Close the credits stage
            Stage creditsStage = (Stage) backButton.getScene().getWindow();
            creditsStage.close();

            // Go back to the WorldHellMenu
            FXGL.getGameController().gotoMainMenu();
        });

        StackPane.setAlignment(backButton, Pos.BOTTOM_RIGHT);



        return backButton;
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

                setOnMouseClicked(e -> action.run());
                setPickOnBounds(true);
                getChildren().add(text);
            }
        }
    }

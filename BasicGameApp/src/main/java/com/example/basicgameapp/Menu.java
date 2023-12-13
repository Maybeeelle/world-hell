package com.example.basicgameapp;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;

public class Menu  extends Application {
    @Override
    public void start(Stage primaryStage)throws Exception{
        Pane root= new Pane();
        root.setPrefSize(800,800);

        InputStream is= Files.newInputStream(Paths.get("C:/Users/majah/OneDrive/Desktop/world-hell/BasicGameApp/src/main/resources/assets/textures/BG.png"));
        Image img=  new Image(is);
        is.close();

        ImageView imgView= new ImageView(img);
        imgView.setFitWidth(800);
        imgView.setFitHeight(800);

        root.getChildren().addAll(imgView);

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //Designing the button in the menu
    private static class MenuButton extends StackPane {
        private Text text;

        public MenuButton(String name){
            text= new Text(name);
            text.setFont(text.getFont().font(20));
        }

    }
    public static void main(String[] args){
        launch(args);
    }
}

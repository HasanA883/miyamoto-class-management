package com.btm495.miyamoto;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

public class SceneManager {

    public static void switchScene(Window window, String fxmlPath, String title) throws Exception {
        Parent root = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
        Stage stage = (Stage) window;
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.sizeToScene();
    }
}

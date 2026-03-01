import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;public class Miyamoto extends Application {

    @Override
    public void start(Stage primaryStage) {
        TextArea textArea = new TextArea();
        textArea.setPrefHeight(100);

        Button helloButton = new Button("Hello");
        Button goodbyeButton = new Button("Goodbye");

        helloButton.setOnAction(e -> textArea.setText("hello"));
        goodbyeButton.setOnAction(e -> textArea.clear());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(textArea, helloButton, goodbyeButton);

        Scene scene = new Scene(layout, 300, 200);

        primaryStage.setTitle("Starter Project");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Database.initializeDatabase();
        launch(args); // Triggers start()
    }

}

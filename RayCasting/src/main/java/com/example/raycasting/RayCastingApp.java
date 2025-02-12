package com.example.raycasting;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;

public class RayCastingApp extends Application {
    private Circle circle;
    private Parent createContent() {
        Pane root = new Pane();
        circle = new Circle(150.0f, 150.0f, 50.f, Color.YELLOW);

        root.getChildren().add(circle);

        root.setOnMouseDragged(new MouseDragEventHandler());

        return root;
    }

    private class MouseDragEventHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent e) {
            circle.setCenterX(e.getX());
            circle.setCenterY(e.getY());
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Ray Casting");
        stage.setScene(new Scene(createContent(), 800, 800, Color.BLACK));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
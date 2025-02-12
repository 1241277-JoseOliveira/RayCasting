package com.example.raycasting;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RayCastingApp extends Application {
    private Circle light_circle;
    private Circle shadow_circle;
    private List<Line> rays = new ArrayList<>();

    private Parent createContent() {
        Pane root = new Pane();
        light_circle = new Circle(150.0f, 150.0f, 50.f, Color.YELLOW);
        shadow_circle = new Circle(400.0f, 400.0f, 80.f, Color.WHITE);

        root.getChildren().add(light_circle);
        root.getChildren().add(shadow_circle);

        generateLightRays(root);

        root.setOnMouseDragged(new MouseDragEventHandler());

        return root;
    }

    private void generateLightRays(Pane root) {
        double radius = 800;

        double centerX = light_circle.getCenterX();
        double centerY = light_circle.getCenterY();

        root.getChildren().removeAll(rays);
        rays.clear();

        for (int i = 0; i <= 360; i++) {
            double angle = Math.toRadians(i);
            double rayEndX = centerX + radius * Math.cos(angle);
            double rayEndY = centerY + radius * Math.sin(angle);

            Line ray = new Line(centerX, centerY, rayEndX, rayEndY);
            ray.setStroke(Color.YELLOW);
            root.getChildren().add(ray);
            rays.add(ray);
        }
    }

    private class MouseDragEventHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent e) {
            light_circle.setCenterX(e.getX());
            light_circle.setCenterY(e.getY());

            checkAndMoveLightCircle();

            generateLightRays((Pane) e.getSource());
        }
    }

    private void checkAndMoveLightCircle() {
        double centerX1 = light_circle.getCenterX();
        double centerY1 = light_circle.getCenterY();
        double centerX2 = shadow_circle.getCenterX();
        double centerY2 = shadow_circle.getCenterY();

        double radius1 = light_circle.getRadius();
        double radius2 = shadow_circle.getRadius();

        double distance = Math.sqrt(Math.pow(centerX2 - centerX1, 2) + Math.pow(centerY2 - centerY1, 2));

        if (distance < (radius1 + radius2)) {
            double angle = Math.atan2(centerY2 - centerY1, centerX2 - centerX1);

            double newCenterX = centerX2 - (radius1 + radius2) * Math.cos(angle);
            double newCenterY = centerY2 - (radius1 + radius2) * Math.sin(angle);

            light_circle.setCenterX(newCenterX);
            light_circle.setCenterY(newCenterY);
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

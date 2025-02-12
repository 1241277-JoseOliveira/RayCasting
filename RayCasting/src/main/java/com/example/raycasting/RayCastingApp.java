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
        root.getChildren().removeAll(rays);
        rays.clear();

        for (int i = 0; i < 360; i++) {
            double angle = Math.toRadians(i);

            generateLine(root, angle);
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

        double sceneWidth = light_circle.getParent().getLayoutBounds().getWidth();
        double sceneHeight = light_circle.getParent().getLayoutBounds().getHeight();
        
        if (centerX1 - radius1 < 0) centerX1 = radius1;
        if (centerX1 + radius1 > sceneWidth) centerX1 = sceneWidth - radius1;
        if (centerY1 - radius1 < 0) centerY1 = radius1;
        if (centerY1 + radius1 > sceneHeight) centerY1 = sceneHeight - radius1;

        light_circle.setCenterX(centerX1);
        light_circle.setCenterY(centerY1);

        double distance = Math.sqrt(Math.pow(centerX2 - centerX1, 2) + Math.pow(centerY2 - centerY1, 2));

        if (distance < (radius1 + radius2)) {
            double angle = Math.atan2(centerY2 - centerY1, centerX2 - centerX1);

            double newCenterX = centerX2 - (radius1 + radius2) * Math.cos(angle);
            double newCenterY = centerY2 - (radius1 + radius2) * Math.sin(angle);

            if (newCenterX - radius1 < 0) newCenterX = radius1;
            if (newCenterX + radius1 > sceneWidth) newCenterX = sceneWidth - radius1;
            if (newCenterY - radius1 < 0) newCenterY = radius1;
            if (newCenterY + radius1 > sceneHeight) newCenterY = sceneHeight - radius1;

            light_circle.setCenterX(newCenterX);
            light_circle.setCenterY(newCenterY);
        }
    }


    private void generateLine(Pane root, double angle) {
        double centerX1 = light_circle.getCenterX();
        double centerY1 = light_circle.getCenterY();
        double centerX2 = shadow_circle.getCenterX();
        double centerY2 = shadow_circle.getCenterY();

        double cosTheta = Math.cos(angle);
        double sinTheta = Math.sin(angle);

        double sceneWidth = root.getWidth();
        double sceneHeight = root.getHeight();

        double lineLength = Math.max(sceneWidth, sceneHeight) + 100;;
        double radius2 = shadow_circle.getRadius();

        double rayEndX = centerX1 + lineLength * cosTheta;
        double rayEndY = centerY1 + lineLength * sinTheta;

        double dx = rayEndX - centerX1;
        double dy = rayEndY - centerY1;

        double a = dx * dx + dy * dy;
        double b = 2 * ((centerX1 - centerX2) * dx + (centerY1 - centerY2) * dy);
        double c = (centerX1 - centerX2) * (centerX1 - centerX2) +
                (centerY1 - centerY2) * (centerY1 - centerY2) - radius2 * radius2;

        double discriminant = b * b - 4 * a * c;

        if (discriminant >= 0) {
            double sqrtD = Math.sqrt(discriminant);
            double t1 = (-b + sqrtD) / (2 * a);
            double t2 = (-b - sqrtD) / (2 * a);

            double t = Math.min(t1, t2);
            if (t < 0) {
                t = Math.max(t1, t2);
            }

            if (t > 0 && t < 1) {
                rayEndX = centerX1 + t * dx;
                rayEndY = centerY1 + t * dy;
            }
        }

        Line ray = new Line(centerX1, centerY1, rayEndX, rayEndY);
        ray.setStroke(Color.YELLOW);
        root.getChildren().add(ray);
        rays.add(ray);
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

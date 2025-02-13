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
        light_circle = new Circle(150.0f, 150.0f, 50.f, Color.YELLOW); //fonte de luz
        shadow_circle = new Circle(400.0f, 400.0f, 80.f, Color.WHITE); //círculo normal

        //adicionar elementos à root node
        root.getChildren().add(light_circle);
        root.getChildren().add(shadow_circle);

        //Gerar Raior de Luz
        generateLightRays(root);

        //Permitir arrastar a fonte de luz com o rato
        root.setOnMouseDragged(new MouseDragEventHandler());

        return root;
    }

    private void generateLightRays(Pane root) {
        //Apagar raios antigos antes de gerar novos
        root.getChildren().removeAll(rays);
        rays.clear();

        for (int i = 0; i < 360; i++) { //dividir circunferencia em 360 partes e aplicar um raio a cada parte
            //converter angulos para radianos
            double angle = Math.toRadians(i);

            //Gerar Raio
            generateLine(root, angle);
        }
    }

    private class MouseDragEventHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent e) {
            //Mover a fonte de luz com o rato
            light_circle.setCenterX(e.getX());
            light_circle.setCenterY(e.getY());

            //Verificar colisões (com a borda ou com o círculo normal, por exemplo)
            checkAndMoveLightCircle();

            //Gerar raios a cada movimentação
            generateLightRays((Pane) e.getSource());
        }
    }

    private void checkAndMoveLightCircle() {
        double centerX1 = light_circle.getCenterX(); //Cordenada x do centro da fonte de luz
        double centerY1 = light_circle.getCenterY(); //Cordenada y do centro da fonte de luz
        double centerX2 = shadow_circle.getCenterX(); //Cordenada x do centro do círculo normal
        double centerY2 = shadow_circle.getCenterY(); //Cordenada y do centro do círculo normal

        double radius1 = light_circle.getRadius(); // raio da fonte de luz
        double radius2 = shadow_circle.getRadius(); // raio do círculo normal

        double sceneWidth = light_circle.getParent().getLayoutBounds().getWidth(); // largura da cena
        double sceneHeight = light_circle.getParent().getLayoutBounds().getHeight(); // comprimento da cena

        if (centerX1 - radius1 < 0) centerX1 = radius1; // se a fonte de luz ultrapassar a borda esquerda, o centro é reposicionado
        if (centerX1 + radius1 > sceneWidth) centerX1 = sceneWidth - radius1; // se a fonte de luz ultrapassar a borda direita, o centro é reposicionado
        if (centerY1 - radius1 < 0) centerY1 = radius1; // se a fonte de luz ultrapassar a borda superior, o centro é reposicionado
        if (centerY1 + radius1 > sceneHeight) centerY1 = sceneHeight - radius1; // se a fonte de luz ultrapassar a borda inferior, o centro é reposicionado

        light_circle.setCenterX(centerX1); //reposição da fonte de luz
        light_circle.setCenterY(centerY1); //reposição da fonte de luz

        double distance = Math.sqrt(Math.pow(centerX2 - centerX1, 2) + Math.pow(centerY2 - centerY1, 2)); //Cálculo de distãncia entre circulos

        // se a distância for menor do que a soma dos raios existe uma colisão
        if (distance < (radius1 + radius2)) {
            double angle = Math.atan2(centerY2 - centerY1, centerX2 - centerX1); //cálculo do ângulo da linha entre os centros dos círculos

            //Novas coordenadas são calculadas para afastar os círculos
            double newCenterX = centerX2 - (radius1 + radius2) * Math.cos(angle);
            double newCenterY = centerY2 - (radius1 + radius2) * Math.sin(angle);

            //Após o ajuste, a posição final ainda é verificada para garantir que a fonte de luz não ultrapasse os limites da cena.
            if (newCenterX - radius1 < 0) newCenterX = radius1;
            if (newCenterX + radius1 > sceneWidth) newCenterX = sceneWidth - radius1;
            if (newCenterY - radius1 < 0) newCenterY = radius1;
            if (newCenterY + radius1 > sceneHeight) newCenterY = sceneHeight - radius1;


            light_circle.setCenterX(newCenterX); //reposição da fonte de luz
            light_circle.setCenterY(newCenterY); //reposição da fonte de luz
        }
    }


    private void generateLine(Pane root, double angle) {
        double centerX1 = light_circle.getCenterX(); //Cordenada x do centro da fonte de luz
        double centerY1 = light_circle.getCenterY(); //Cordenada y do centro da fonte de luz
        double centerX2 = shadow_circle.getCenterX(); //Cordenada x do centro do círculo normal
        double centerY2 = shadow_circle.getCenterY(); //Cordenada y do centro do círculo normal

        double cosTheta = Math.cos(angle); //cálculo do cosseno do ângulo que o raio faz com o raio da circunferência
        double sinTheta = Math.sin(angle); //cálculo do seno do ângulo que o raio faz com o raio da circunferência

        double sceneWidth = root.getWidth(); //Largura da cena
        double sceneHeight = root.getHeight(); //Comprimento da cena

        double lineLength = Math.max(sceneWidth, sceneHeight) + 100;; //Comprimento do raio
        double radius2 = shadow_circle.getRadius(); //raio do círculo normal

        double rayEndX = centerX1 + lineLength * cosTheta; //coordenada x final do raio
        double rayEndY = centerY1 + lineLength * sinTheta; //coordenada y final do raio

        double dx = rayEndX - centerX1; //subtração da coordenada x final pela coordenada x inicial do raio (centro da fonte de luz)
        double dy = rayEndY - centerY1; //subtração da coordenada y final pela coordenada y inicial do raio (centro da fonte de luz)

        double a = dx * dx + dy * dy; //coeficiente A da interseção de reta com circunferência
        double b = 2 * ((centerX1 - centerX2) * dx + (centerY1 - centerY2) * dy); //coeficiente B da interseção de reta com circunferência
        double c = (centerX1 - centerX2) * (centerX1 - centerX2) +
                (centerY1 - centerY2) * (centerY1 - centerY2) - radius2 * radius2; //coeficiente C da interseção de reta com circunferência

        double discriminant = b * b - 4 * a * c; //Binómio Discriminante da interseção

        //Se o discriminante for maior do que a 0 a reta interseta o círculo normal
        if (discriminant > 0) {
            double sqrtD = Math.sqrt(discriminant); //Raiz do discriminante
            double t1 = (-b + sqrtD) / (2 * a); //Coordenada 1 da interseção
            double t2 = (-b - sqrtD) / (2 * a); //Coordenada 2 da interseção

            double t = Math.min(t1, t2); //Encontrar o menor valor

            // Se negativo, utiliza-se o valor positivo
            if (t < 0) {
                t = Math.max(t1, t2);
            }

            //limitar o raio apenas até a interseção dentro do segmento analisado
            if (t > 0 && t < 1) {
                rayEndX = centerX1 + t * dx;
                rayEndY = centerY1 + t * dy;
            }
        }

        //Escrever raio na cena causando o efeito de sombra no círculo normal
        Line ray = new Line(centerX1, centerY1, rayEndX, rayEndY);
        ray.setStroke(Color.YELLOW);
        root.getChildren().add(ray);
        rays.add(ray);
    }



    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Ray Casting"); // Nome do programa que será escrito na janela de visualização
        stage.setScene(new Scene(createContent(), 800, 800, Color.BLACK)); //Criação da cena
        stage.setResizable(false); // Impede que a janela seja redimensionada
        
        stage.show(); //Mostrar janela
    }

    public static void main(String[] args) {
        launch();
    }
}

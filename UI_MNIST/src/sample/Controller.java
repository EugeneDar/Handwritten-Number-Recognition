package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller {

    @FXML
    public Canvas canvas;
    @FXML
    public TextField textField;
    @FXML
    public TextField testField;

    public Affine affine;

    public Simulation simulation;


    public Controller() {
        canvas = new Canvas(560, 560);
        affine = new Affine();
        affine.appendScale(20, 20);
        simulation = new Simulation();
        simulation.train();
    }

    public void showResult () {
        textField.setText(simulation.getResult());
    }

    public void draw() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setTransform(affine);

        g.setFill(Color.WHITE);
        g.fillRect(0, 0, 28, 28);

        for (int i = 0;i < simulation.fieldSize;i++) {
            for (int j = 0;j < simulation.fieldSize;j++) {
                int color = 255 - simulation.field[i][j];
                g.setFill(Color.rgb(color, color, color));
                g.fillRect(i, j, 1, 1);
            }
        }
    }

    public void mouseDraw (MouseEvent event) {
        try {
            Point2D point2D = affine.inverseTransform(event.getX(), event.getY());
            int x = (int) point2D.getX();
            int y = (int) point2D.getY();
            simulation.setDote(x, y);
            draw();
        } catch (Exception e) {
            System.out.println(event.getX() + " : " + event.getY());
            e.printStackTrace();
        }
        showResult();
    }

    public void clear () {
        simulation.field = new int[28][28];
        draw();
    }

    public void select () {
        String text = testField.getText().toString();
        if (text.length() == 0) {
            return;
        }
        int number = Integer.parseInt(text);
        int pos = 0;
        for (int i = 0;i < simulation.fieldSize;i++) {
            for (int j = 0;j < simulation.fieldSize;j++) {
                simulation.field[j][i] = (int)(simulation.inputs[number][pos++] * 255.0);
            }
        }
        showResult();
        Simulation.print(simulation.inputs[number], Integer.parseInt(textField.getText()));
        draw();
    }
}

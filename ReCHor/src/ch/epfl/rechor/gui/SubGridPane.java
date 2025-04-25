package ch.epfl.rechor.gui;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class SubGridPane extends GridPane {

    private List<Bounds> listOfBounds;

    public SubGridPane (List <Bounds> listBounds) {
        this.listOfBounds = listBounds;
    }

    @Override
    protected void layoutChildren() {

        super.layoutChildren();


        for (int i = 1; i < listOfBounds.size(); i++) {
            Bounds firstBound = listOfBounds.get(i-1);
            Bounds secondBound = listOfBounds.get(i);

            Line line = new Line(firstBound.getCenterX(), firstBound.getCenterY(),
                    secondBound.getCenterX(), secondBound.getCenterY());
            line.setStrokeWidth(2);
            line.setStroke(Color.RED);
        }
    }
}

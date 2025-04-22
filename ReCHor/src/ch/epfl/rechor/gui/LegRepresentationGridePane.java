package ch.epfl.rechor.gui;

import javafx.geometry.Bounds;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

public class LegRepresentationGridePane extends GridPane {

    List<Bounds> pairsCircle = new ArrayList<>();

    @Override
    protected void layoutChildren() {

        //stocker la représentation des étapes :
        super.layoutChildren();
        for (int i = 1; i < pairsCircle.size(); i++) {
            Bounds firstBound = pairsCircle.get(i-1);
            Bounds secondBound = pairsCircle.get(i);
            Line line = new Line(firstBound.getCenterX(), firstBound.getCenterY(),
                    secondBound.getCenterX(), secondBound.getCenterY());
            line.setStrokeWidth(2);
            line.setStroke(Color.RED);
        }
    }
}

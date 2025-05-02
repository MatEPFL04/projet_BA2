package ch.epfl.rechor.gui;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.JourneyExtractor;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Record representing the detail of a journey.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public record SummaryUI(Node rootNode, ObservableValue<Journey> selectedJourneyO) {

    public static SummaryUI create(ObservableValue<List<Journey>> listOfJourneys,
                                   ObservableValue<LocalTime> journeyTime){

        Text text = new Text();
        ImageView imageView = new ImageView();
        HBox box1 = new HBox(imageView, text);

        Circle circle = new Circle();
        Group group = new Group(circle);
        Line line = new Line();
        Pane pane = new Pane(line, group);

        HBox box2 = new HBox();

        Text depTime = new Text();

        Text arrTime = new Text();

        BorderPane borderPane = new BorderPane(box1, pane, box2, depTime, arrTime);

        return new SummaryUI(borderPane, null);
    }
}

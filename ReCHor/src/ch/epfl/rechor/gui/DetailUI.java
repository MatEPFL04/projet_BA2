package ch.epfl.rechor.gui;

import ch.epfl.rechor.journey.Journey;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Record representing the detail of a journey.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public record DetailUI(Node rootNode) {

    /**
     *
     * @param observableValue
     * @return
     */
    public static DetailUI create(ObservableValue<Journey> observableValue) {

        // 1. Construction du graphe de scène principal
        Text text = new Text("Aucun voyage");
        VBox box = new VBox(text);
        StackPane stackPane = new StackPane(box);
        ScrollPane scrollPane = new ScrollPane(stackPane);

        return new DetailUI(scrollPane);
    }
}

package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public final class ErrorManager {

    //pane containing the annotated map
    private final Pane pane;

    //text node containing the error message
    private final Text text;

    //sequential transition for the error display
    private final SequentialTransition paneErrorTransition;

    private static final double DURATION_FIRST_FADE_TRANSITION_IN_SECS = 0.2;
    private static final int DURATION_PAUSE_TRANSITION_IN_SECS = 2;
    private static final double DURATION_SECOND_FADE_TRANSITION_IN_SECS = 0.5;

    private static final double INVISIBLE_VALUE_FADE_TRANSITION = 0.0;
    private static final double VISIBLE_VALUE_FADE_TRANSITION = 0.8;

    private static final String PANE_CSS_CLASS = "error.css";

    /**
     * Constructor of ErrorManager
     * Initialize the Pane, the Text node of the Pane and the sequential transition for the error display
     */

    public ErrorManager() {
        pane = new VBox();
        text = new Text();

        pane.getStylesheets().add(PANE_CSS_CLASS);
        pane.getChildren().add(text);
        pane.setMouseTransparent(true);

        paneErrorTransition = createSequentialTransitions();
    }

    /**
     * Return the pane where errors are displayed
     *
     * @return the pane where errors are displayed
     */

    public Pane pane() {
        return pane;
    }

    /**
     * Display an error with message 'messageError' and play a sound
     *
     * @param messageError message of the error
     */

    public void displayError(String messageError) {
        text.setText(messageError);

        java.awt.Toolkit.getDefaultToolkit().beep();

        paneErrorTransition.stop();
        paneErrorTransition.play();
    }

    //create the sequential transition
    private SequentialTransition createSequentialTransitions() {
        FadeTransition firstFadeTransition = new FadeTransition(Duration.seconds(DURATION_FIRST_FADE_TRANSITION_IN_SECS), pane);
        firstFadeTransition.setFromValue(INVISIBLE_VALUE_FADE_TRANSITION);
        firstFadeTransition.setToValue(VISIBLE_VALUE_FADE_TRANSITION);

        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(DURATION_PAUSE_TRANSITION_IN_SECS));

        FadeTransition secondFadeTransition = new FadeTransition(Duration.seconds(DURATION_SECOND_FADE_TRANSITION_IN_SECS), pane);
        secondFadeTransition.setFromValue(VISIBLE_VALUE_FADE_TRANSITION);
        secondFadeTransition.setToValue(INVISIBLE_VALUE_FADE_TRANSITION);

        return new SequentialTransition(firstFadeTransition,
                pauseTransition, secondFadeTransition);
    }

}

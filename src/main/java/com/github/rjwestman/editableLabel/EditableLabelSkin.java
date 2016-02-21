package com.github.rjwestman.editableLabel;

import com.sun.javafx.scene.control.skin.TextFieldSkin;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.collections.SetChangeListener;
import javafx.css.PseudoClass;

public class EditableLabelSkin extends TextFieldSkin {

    private EditableLabel editableLabel;
    private Boolean editableState;

    /************************************************************************
     *                                                                      *
     * Constructors                                                         *
     *                                                                      *
     ***********************************************************************/
    // Constructors and helper methods for constructors

    public EditableLabelSkin(final EditableLabel editableLabel) {
        this(editableLabel, new EditableLabelBehavior(editableLabel));
    }
    public EditableLabelSkin(final EditableLabel editableLabel, final EditableLabelBehavior editableLabelBehavior) {
        super(editableLabel, editableLabelBehavior);
        this.editableLabel = editableLabel;
        init();
    }

    private void init() {
        editableState = false;

        Platform.runLater(this::updateVisibleText);
        editableLabel.getPseudoClassStates().addListener( (SetChangeListener<PseudoClass>) e -> {
            if (e.getSet().contains(PseudoClass.getPseudoClass("editable"))) {
                if ( !editableState ) {
                    // editableState change to editable
                    editableState = true;
                    updateVisibleText();
                }
            } else {
                if ( editableState ) {
                    // editableState change to not editable
                    editableState = false;
                    updateVisibleText();
                }
            }
        });
        editableLabel.widthProperty().addListener( observable -> updateVisibleText() );
        editableLabel.baseTextProperty().addListener( observable -> updateVisibleText() );
    }

    /************************************************************************
     *                                                                      *
     * Control State Changes                                                *
     *                                                                      *
     ***********************************************************************/
    // Handles visual changes on state change that are not or cannot be handled via css

    private void updateVisibleText() {
        String baseText = editableLabel.getBaseText();
        if ( !editableState ) {
            editableLabel.setText(calculateClipString(baseText));
        } else {
            editableLabel.setText(baseText);
            editableLabel.positionCaret(baseText.length());
        }
    }

    private String calculateClipString(String text) {
        TextLayout layout = Toolkit.getToolkit().getTextLayoutFactory().createLayout();
        Object font = editableLabel.getFont().impl_getNativeFont();
        double labelWidth = editableLabel.getWidth();

        // If no need to truncate
        layout.setContent(text, font);
        if (layout.getBounds().getWidth() < labelWidth) {
            return text;
        } else {
            layout.setContent(text+"...", font);
            while ( layout.getBounds().getWidth() > labelWidth ) {
                text = text.substring(0, text.length()-1);
                layout.setContent(text+"...", font);
            }

            return text+"...";
        }
    }

    /************************************************************************
     *                                                                      *
     * Skin Layout                                                          *
     *                                                                      *
     ***********************************************************************/
    // Lays out the elements of the control (e.g. calculating and setting sizes and bounds)


}
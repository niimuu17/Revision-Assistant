package com.example.revision_assistant;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.List;
import java.util.function.Consumer;

/**
 * Dedicated floating modal window for adding or editing a class duration.
 * Validates against duration conflicts and invalid formats in real-time,
 * keeping the dialog open with clear error messages until the user fixes the input.
 */
public class TimeSlotDialog {

    public static void show(Window owner, String dialogTitle, String headerText, String initialValue,
                            List<String> existingSlots, String ignoredSlot, Consumer<String> onConfirm) {

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle(dialogTitle);
        dialog.setResizable(false);

        VBox root = new VBox(14);
        root.setPadding(new Insets(22));
        root.setPrefWidth(460);
        root.setStyle("-fx-background-color: #fcf9f2; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Header
        Label titleLabel = new Label(dialogTitle);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1c2a18;");

        Label subtitleLabel = new Label(headerText);
        subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #71816c;");
        subtitleLabel.setWrapText(true);

        // 2. Input Field
        Label fieldLabel = new Label("Class Duration / Time Slot:");
        fieldLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c3f26;");

        TextField durationField = new TextField(initialValue != null ? initialValue : "");
        durationField.setPromptText("e.g. 08:30 - 09:50 or 01:30 PM - 02:50 PM");
        durationField.setStyle("-fx-background-color: #ffffff; -fx-padding: 9px 12px; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: #d8e2d4; -fx-font-size: 13px; -fx-text-fill: #1c2a18;");

        Label hintLabel = new Label("💡 Examples: 08:30 - 09:50, 10:00 AM - 11:20 AM, 1:30 - 2:50, 14:00 - 15:30");
        hintLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #71816c;");

        // 3. Error Feedback Label (initially hidden)
        Label errorLabel = new Label();
        errorLabel.setWrapText(true);
        errorLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px; -fx-font-weight: bold;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // 4. Action Buttons
        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(10, 0, 0, 0));

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: #f0e8dc; -fx-text-fill: #2c3f26; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8px 16px; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> dialog.close());

        Button submitBtn = new Button("Confirm Duration");
        submitBtn.setStyle("-fx-background-color: #445a3c; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8px 18px; -fx-cursor: hand;");

        Runnable validateAndSubmit = () -> {
            String input = durationField.getText() != null ? durationField.getText().trim() : "";

            // Validate against format and existing slot conflicts
            String error = TimeSlotHelper.validateSlot(input, existingSlots, ignoredSlot);

            if (error != null) {
                // Show error in red and KEEP DIALOG OPEN
                errorLabel.setText("⚠️ " + error);
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                durationField.setStyle("-fx-background-color: #ffffff; -fx-padding: 9px 12px; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: #ef4444; -fx-font-size: 13px; -fx-text-fill: #1c2a18;");
                durationField.requestFocus();
            } else {
                // Success: invoke callback and close
                onConfirm.accept(input);
                dialog.close();
            }
        };

        submitBtn.setOnAction(e -> validateAndSubmit.run());
        durationField.setOnAction(e -> validateAndSubmit.run());

        // Clear error styling when user types
        durationField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (errorLabel.isVisible()) {
                errorLabel.setVisible(false);
                errorLabel.setManaged(false);
                durationField.setStyle("-fx-background-color: #ffffff; -fx-padding: 9px 12px; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: #445a3c; -fx-font-size: 13px; -fx-text-fill: #1c2a18;");
            }
        });

        buttonBar.getChildren().addAll(cancelBtn, submitBtn);

        root.getChildren().addAll(
                titleLabel, subtitleLabel,
                fieldLabel, durationField, hintLabel,
                errorLabel,
                buttonBar
        );

        Scene scene = new Scene(root);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}

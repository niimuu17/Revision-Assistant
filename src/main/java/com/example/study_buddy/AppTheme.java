package com.example.study_buddy;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Pure JavaFX Theme Manager for Study Buddy.
 * Implements an Earthy Forest Green, Warm Sand Cream, and Sage aesthetic
 * entirely in programmatic JavaFX without relying on external CSS files.
 */
public final class AppTheme {

    // ==========================================
    // Design Tokens (Earthy Forest & Warm Cream)
    // ==========================================
    public static final String COLOR_OUTER_SHELL      = "#526749"; // Earthy moss/forest green
    public static final String COLOR_PANEL_CREAM      = "#f0e8dc"; // Warm light parchment cream
    public static final String COLOR_CANVAS_CREAM     = "#f1eae0"; // Warm sand parchment canvas
    public static final String COLOR_CARD_IVORY       = "#fcf9f2"; // Warm light ivory card
    public static final String COLOR_DEEP_OLIVE       = "#445a3c"; // Primary olive/forest green
    public static final String COLOR_DEEP_OLIVE_HOVER = "#4e6645"; // Hover state for primary buttons
    public static final String COLOR_CREAM_TEXT       = "#f0e8dc"; // Text fill for primary dark buttons
    public static final String COLOR_OUTLINE_BORDER   = "#3d5236"; // Outline button border
    public static final String COLOR_OUTLINE_TEXT     = "#3d5236"; // Outline button text
    public static final String COLOR_DARK_OLIVE       = "#1c2a18"; // High-contrast dark charcoal olive
    public static final String COLOR_SUB_OLIVE        = "#4b5c46"; // Medium olive for secondary titles
    public static final String COLOR_MUTED_OLIVE      = "#71816c"; // Muted olive-gray for descriptions
    public static final String COLOR_SAGE_PILL        = "#d8e2d4"; // Soft sage background for badges
    public static final String COLOR_FOREST_TEXT      = "#2c3f26"; // Deep forest text on sage badges
    public static final String COLOR_HOVER_TINT       = "#e8dfd2"; // Soft parchment hover tint
    public static final String COLOR_BORDER_CREAM     = "#e5dcce"; // Soft canvas/card border

    private AppTheme() {}

    /**
     * Applies outer application container styling.
     */
    public static void applyOuterShell(Region root) {
        if (root == null) return;
        root.setStyle("-fx-background-color: " + COLOR_OUTER_SHELL + "; -fx-padding: 14px 20px 16px 20px;");
    }

    /**
     * Applies top header bar styling.
     */
    public static void applyHeaderBar(HBox topBar) {
        if (topBar == null) return;
        topBar.setStyle("-fx-background-color: " + COLOR_PANEL_CREAM + "; "
                + "-fx-padding: 12px 18px; "
                + "-fx-background-radius: 16px; "
                + "-fx-border-color: #e4d8c8; "
                + "-fx-border-radius: 16px; "
                + "-fx-border-width: 1px; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(30, 45, 25, 0.06), 10, 0, 0, 2);");
    }

    /**
     * Applies styling to the left sidebar hamburger toggle button.
     */
    public static void applyHamburgerButton(Button btn) {
        if (btn == null) return;
        String normal = "-fx-background-color: " + COLOR_DEEP_OLIVE + "; "
                + "-fx-text-fill: " + COLOR_CREAM_TEXT + "; "
                + "-fx-font-size: 15px; -fx-font-weight: bold; "
                + "-fx-background-radius: 8px; -fx-padding: 6px 12px; -fx-cursor: hand;";
        String hover = "-fx-background-color: " + COLOR_DEEP_OLIVE_HOVER + "; "
                + "-fx-text-fill: " + COLOR_CREAM_TEXT + "; "
                + "-fx-font-size: 15px; -fx-font-weight: bold; "
                + "-fx-background-radius: 8px; -fx-padding: 6px 12px; -fx-cursor: hand;";
        btn.setStyle(normal);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(normal));
    }

    /**
     * Applies styling to the right sidebar toggle button.
     */
    public static void applyRightToggleButton(Button btn) {
        if (btn == null) return;
        String normal = "-fx-background-color: transparent; "
                + "-fx-text-fill: " + COLOR_DEEP_OLIVE + "; "
                + "-fx-font-size: 14px; -fx-font-weight: bold; "
                + "-fx-background-radius: 8px; -fx-padding: 6px 10px; -fx-cursor: hand;";
        String hover = "-fx-background-color: " + COLOR_HOVER_TINT + "; "
                + "-fx-text-fill: " + COLOR_DARK_OLIVE + "; "
                + "-fx-font-size: 14px; -fx-font-weight: bold; "
                + "-fx-background-radius: 8px; -fx-padding: 6px 10px; -fx-cursor: hand;";
        btn.setStyle(normal);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(normal));
    }

    /**
     * Styles any workspace canvas (Main Menu, Calendar, Routine, Quiz, Progress, Notebooks).
     */
    public static void applyMainCanvas(Region canvas) {
        if (canvas == null) return;
        canvas.setStyle("-fx-background-color: " + COLOR_CANVAS_CREAM + "; "
                + "-fx-padding: 24px 28px; "
                + "-fx-background-radius: 18px; "
                + "-fx-border-color: " + COLOR_BORDER_CREAM + "; "
                + "-fx-border-radius: 18px; "
                + "-fx-border-width: 1px; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(30, 45, 25, 0.05), 10, 0, 0, 2);");
    }

    /**
     * Styles sidebars (left and right).
     */
    public static void applySidebar(VBox sidebar) {
        if (sidebar == null) return;
        sidebar.setStyle("-fx-background-color: " + COLOR_PANEL_CREAM + "; "
                + "-fx-background-radius: 16px; "
                + "-fx-border-color: #e4d8c8; "
                + "-fx-border-radius: 16px; "
                + "-fx-border-width: 1px; "
                + "-fx-padding: 16px; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(30, 45, 25, 0.06), 10, 0, 0, 2);");
    }

    /**
     * Styles a primary solid action button (e.g. "+ New Notebook", "Save", "Submit").
     */
    public static void applyPrimaryButton(Button btn) {
        if (btn == null) return;
        String normal = "-fx-background-color: " + COLOR_DEEP_OLIVE + "; "
                + "-fx-text-fill: " + COLOR_CREAM_TEXT + "; "
                + "-fx-font-weight: bold; -fx-font-size: 12px; "
                + "-fx-background-radius: 10px; -fx-padding: 8px 18px; -fx-cursor: hand;";
        String hover = "-fx-background-color: " + COLOR_DEEP_OLIVE_HOVER + "; "
                + "-fx-text-fill: " + COLOR_CREAM_TEXT + "; "
                + "-fx-font-weight: bold; -fx-font-size: 12px; "
                + "-fx-background-radius: 10px; -fx-padding: 8px 18px; -fx-cursor: hand; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(68, 90, 60, 0.25), 6, 0, 0, 2);";
        btn.setStyle(normal);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(normal));
    }

    /**
     * Styles an outlined pill action button (e.g. "📖 My Notebooks").
     */
    public static void applyOutlineButton(Button btn) {
        if (btn == null) return;
        String normal = "-fx-background-color: transparent; "
                + "-fx-text-fill: " + COLOR_OUTLINE_TEXT + "; "
                + "-fx-font-weight: bold; -fx-font-size: 12px; "
                + "-fx-border-color: " + COLOR_OUTLINE_BORDER + "; "
                + "-fx-border-radius: 10px; -fx-border-width: 1.5px; "
                + "-fx-background-radius: 10px; -fx-padding: 7px 16px; -fx-cursor: hand;";
        String hover = "-fx-background-color: " + COLOR_HOVER_TINT + "; "
                + "-fx-text-fill: " + COLOR_DARK_OLIVE + "; "
                + "-fx-font-weight: bold; -fx-font-size: 12px; "
                + "-fx-border-color: " + COLOR_DARK_OLIVE + "; "
                + "-fx-border-radius: 10px; -fx-border-width: 1.5px; "
                + "-fx-background-radius: 10px; -fx-padding: 7px 16px; -fx-cursor: hand;";
        btn.setStyle(normal);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(normal));
    }

    /**
     * Styles secondary transparent or subtle action buttons.
     */
    public static void applySecondaryButton(Button btn) {
        if (btn == null) return;
        String normal = "-fx-background-color: " + COLOR_CARD_IVORY + "; "
                + "-fx-text-fill: " + COLOR_DARK_OLIVE + "; "
                + "-fx-font-weight: bold; -fx-font-size: 12px; "
                + "-fx-border-color: #d8cebe; "
                + "-fx-border-radius: 8px; -fx-border-width: 1px; "
                + "-fx-background-radius: 8px; -fx-padding: 6px 12px; -fx-cursor: hand;";
        String hover = "-fx-background-color: " + COLOR_HOVER_TINT + "; "
                + "-fx-text-fill: " + COLOR_DARK_OLIVE + "; "
                + "-fx-font-weight: bold; -fx-font-size: 12px; "
                + "-fx-border-color: #c4b6a2; "
                + "-fx-border-radius: 8px; -fx-border-width: 1px; "
                + "-fx-background-radius: 8px; -fx-padding: 6px 12px; -fx-cursor: hand;";
        btn.setStyle(normal);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(normal));
    }

    /**
     * Styles navigation items in the left sidebar.
     */
    public static void applyNavItem(Button btn, boolean active) {
        if (btn == null) return;
        String normal = active
                ? "-fx-background-color: " + COLOR_SAGE_PILL + "; "
                  + "-fx-text-fill: " + COLOR_DARK_OLIVE + "; "
                  + "-fx-font-size: 13px; -fx-font-weight: bold; "
                  + "-fx-background-radius: 10px; -fx-padding: 10px 14px; -fx-cursor: hand;"
                : "-fx-background-color: transparent; "
                  + "-fx-text-fill: " + COLOR_SUB_OLIVE + "; "
                  + "-fx-font-size: 13px; -fx-font-weight: 500; "
                  + "-fx-background-radius: 10px; -fx-padding: 10px 14px; -fx-cursor: hand;";

        String hover = "-fx-background-color: " + COLOR_HOVER_TINT + "; "
                + "-fx-text-fill: " + COLOR_DARK_OLIVE + "; "
                + "-fx-font-size: 13px; -fx-font-weight: bold; "
                + "-fx-background-radius: 10px; -fx-padding: 10px 14px; -fx-cursor: hand;";

        btn.setStyle(normal);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(normal));
    }

    /**
     * Styles the logout button in the navigation drawer.
     */
    public static void applyLogoutButton(Button btn) {
        if (btn == null) return;
        String normal = "-fx-background-color: #faece8; -fx-text-fill: #993b2a; "
                + "-fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 8px; "
                + "-fx-padding: 8px 14px; -fx-cursor: hand;";
        String hover = "-fx-background-color: #f7ddd7; -fx-text-fill: #802e1f; "
                + "-fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 8px; "
                + "-fx-padding: 8px 14px; -fx-cursor: hand;";
        btn.setStyle(normal);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(normal));
    }

    /**
     * Styles a single notebook card to match the warm ivory/sage card aesthetic.
     */
    public static void applyNotebookCard(
            StackPane card,
            Label dot,
            Label titleLabel,
            Label descLabel,
            Label statsLabel,
            Label openArrow,
            Button optionsBtn) {

        if (card == null) return;

        String normalCardStyle = "-fx-background-color: " + COLOR_CARD_IVORY + "; "
                + "-fx-background-radius: 14px; "
                + "-fx-border-color: transparent; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(50, 70, 45, 0.08), 8, 0, 0, 2);";

        String hoverCardStyle = "-fx-background-color: #ffffff; "
                + "-fx-background-radius: 14px; "
                + "-fx-border-color: #e2dbce; -fx-border-radius: 14px; -fx-border-width: 1px; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(50, 70, 45, 0.14), 12, 0, 0, 3);";

        card.setStyle(normalCardStyle);
        card.setOnMouseEntered(e -> card.setStyle(hoverCardStyle));
        card.setOnMouseExited(e -> card.setStyle(normalCardStyle));

        if (dot != null) {
            dot.setStyle("-fx-font-size: 12px; -fx-text-fill: #879981;");
        }
        if (titleLabel != null) {
            titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_DARK_OLIVE + ";");
        }
        if (descLabel != null) {
            descLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + COLOR_MUTED_OLIVE + ";");
        }
        if (statsLabel != null) {
            statsLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; "
                    + "-fx-text-fill: " + COLOR_FOREST_TEXT + "; "
                    + "-fx-background-color: " + COLOR_SAGE_PILL + "; "
                    + "-fx-padding: 2px 7px; -fx-background-radius: 6px;");
        }
        if (openArrow != null) {
            openArrow.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_DARK_OLIVE + "; -fx-cursor: hand;");
        }
        if (optionsBtn != null) {
            optionsBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_MUTED_OLIVE + "; "
                    + "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 0 4px; -fx-cursor: hand;");
        }
    }

    /**
     * Styles an academic course card to match the warm ivory/sage card aesthetic.
     */
    public static void applyCourseCard(VBox card, Label codeLabel, Label titleLabel, Button dotsBtn) {
        if (card == null) return;
        String normal = "-fx-background-color: " + COLOR_CARD_IVORY + "; -fx-padding: 16px; "
                + "-fx-background-radius: 14px; -fx-border-color: " + COLOR_BORDER_CREAM + "; -fx-border-radius: 14px; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(50, 70, 45, 0.08), 8, 0, 0, 2); -fx-cursor: hand;";
        String hover = "-fx-background-color: #ffffff; -fx-padding: 16px; "
                + "-fx-background-radius: 14px; -fx-border-color: " + COLOR_DEEP_OLIVE + "; -fx-border-radius: 14px; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(50, 70, 45, 0.14), 12, 0, 0, 3); -fx-cursor: hand;";
        card.setStyle(normal);
        card.setOnMouseEntered(e -> card.setStyle(hover));
        card.setOnMouseExited(e -> card.setStyle(normal));

        if (codeLabel != null) {
            codeLabel.setStyle("-fx-background-color: " + COLOR_SAGE_PILL + "; -fx-text-fill: " + COLOR_FOREST_TEXT + "; "
                    + "-fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 3px 8px; -fx-background-radius: 6px;");
        }
        if (titleLabel != null) {
            titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_DARK_OLIVE + ";");
        }
        if (dotsBtn != null) {
            dotsBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_MUTED_OLIVE + "; -fx-font-size: 14px; -fx-cursor: hand;");
        }
    }

    /**
     * Styles a monthly calendar day cell.
     */
    public static void applyCalendarDayCell(VBox cell, Label dayNumLabel, boolean isToday, boolean isCurrentMonth) {
        if (cell == null) return;
        if (isCurrentMonth) {
            cell.setStyle("-fx-background-color: " + COLOR_CARD_IVORY + "; -fx-background-radius: 8px; "
                    + "-fx-border-color: " + COLOR_BORDER_CREAM + "; -fx-border-radius: 8px; -fx-padding: 4px;");
        } else {
            cell.setStyle("-fx-background-color: #f6efe4; -fx-background-radius: 8px; "
                    + "-fx-border-color: #e8e0d4; -fx-border-radius: 8px; -fx-padding: 4px; -fx-opacity: 0.65;");
        }
        if (dayNumLabel != null) {
            if (isToday) {
                dayNumLabel.setStyle("-fx-background-color: " + COLOR_DEEP_OLIVE + "; -fx-text-fill: " + COLOR_CREAM_TEXT + "; "
                        + "-fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 10px; -fx-padding: 2px 7px;");
            } else if (!isCurrentMonth) {
                dayNumLabel.setStyle("-fx-text-fill: #a0ad9b; -fx-font-size: 11px;");
            } else {
                dayNumLabel.setStyle("-fx-text-fill: " + COLOR_DARK_OLIVE + "; -fx-font-weight: bold; -fx-font-size: 11px;");
            }
        }
    }

    /**
     * Styles routine schedule cell cards.
     */
    public static void applyRoutineCell(VBox cell, boolean hasClass) {
        if (cell == null) return;
        if (hasClass) {
            String normal = "-fx-background-color: " + COLOR_CARD_IVORY + "; -fx-background-radius: 8px; "
                    + "-fx-border-color: " + COLOR_BORDER_CREAM + "; -fx-border-radius: 8px; -fx-cursor: hand;";
            String hover = "-fx-background-color: #ffffff; -fx-background-radius: 8px; "
                    + "-fx-border-color: " + COLOR_OUTLINE_BORDER + "; -fx-border-radius: 8px; -fx-cursor: hand;";
            cell.setStyle(normal);
            cell.setOnMouseEntered(e -> cell.setStyle(hover));
            cell.setOnMouseExited(e -> cell.setStyle(normal));
        } else {
            String normal = "-fx-background-color: " + COLOR_CANVAS_CREAM + "; -fx-background-radius: 8px; "
                    + "-fx-border-color: " + COLOR_BORDER_CREAM + "; -fx-border-radius: 8px; -fx-border-style: dashed; -fx-cursor: hand;";
            String hover = "-fx-background-color: " + COLOR_CARD_IVORY + "; -fx-background-radius: 8px; "
                    + "-fx-border-color: " + COLOR_MUTED_OLIVE + "; -fx-border-radius: 8px; -fx-cursor: hand;";
            cell.setStyle(normal);
            cell.setOnMouseEntered(e -> cell.setStyle(hover));
            cell.setOnMouseExited(e -> cell.setStyle(normal));
        }
    }

    /**
     * Styles standard modal dialog boxes.
     */
    public static void applyModalDialog(VBox root, Label headerTitle, Label headerSub, Button cancelBtn, Button saveBtn) {
        if (root != null) {
            root.setStyle("-fx-background-color: " + COLOR_CARD_IVORY + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");
        }
        if (headerTitle != null) {
            headerTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_DARK_OLIVE + ";");
        }
        if (headerSub != null) {
            headerSub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + COLOR_MUTED_OLIVE + ";");
        }
        if (cancelBtn != null) {
            cancelBtn.setStyle("-fx-background-color: " + COLOR_PANEL_CREAM + "; -fx-text-fill: " + COLOR_SUB_OLIVE + "; "
                    + "-fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8px 16px; -fx-cursor: hand;");
            cancelBtn.setOnMouseEntered(e -> cancelBtn.setStyle("-fx-background-color: " + COLOR_HOVER_TINT + "; -fx-text-fill: " + COLOR_DARK_OLIVE + "; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8px 16px; -fx-cursor: hand;"));
            cancelBtn.setOnMouseExited(e -> cancelBtn.setStyle("-fx-background-color: " + COLOR_PANEL_CREAM + "; -fx-text-fill: " + COLOR_SUB_OLIVE + "; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8px 16px; -fx-cursor: hand;"));
        }
        if (saveBtn != null) {
            applyPrimaryButton(saveBtn);
        }
    }

    /**
     * Applies the overall Earthy theme to the main dashboard views.
     */
    public static void applyDashboardTheme(
            Region mainRootContainer,
            HBox appTopBar,
            Button leftToggleBtn,
            Button rightToggleBtn,
            Label formTitle,
            Label welcomeText,
            Label userDetailText,
            Region mainMenuView,
            Label recentNotebooksLabel,
            Button myNotebooksBtn,
            Button newNotebookBtn,
            VBox leftSidebar,
            VBox rightSidebar,
            Button navHomeBtn,
            Button navRoutineBtn,
            Button navCalendarBtn,
            Button navQuizBtn,
            Button navProgressBtn,
            Button logoutButton) {

        applyOuterShell(mainRootContainer);
        applyHeaderBar(appTopBar);
        applyHamburgerButton(leftToggleBtn);
        applyRightToggleButton(rightToggleBtn);

        if (formTitle != null) {
            formTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_DARK_OLIVE + ";");
        }
        if (welcomeText != null) {
            welcomeText.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_SUB_OLIVE + ";");
        }
        if (userDetailText != null) {
            userDetailText.setStyle("-fx-font-size: 11px; -fx-text-fill: " + COLOR_MUTED_OLIVE + ";");
        }

        applyMainCanvas(mainMenuView);

        if (recentNotebooksLabel != null) {
            recentNotebooksLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_DARK_OLIVE + ";");
        }
        applyOutlineButton(myNotebooksBtn);
        applyPrimaryButton(newNotebookBtn);

        applySidebar(leftSidebar);
        applySidebar(rightSidebar);

        applyNavItem(navHomeBtn, true);
        applyNavItem(navRoutineBtn, false);
        applyNavItem(navCalendarBtn, false);
        applyNavItem(navQuizBtn, false);
        applyNavItem(navProgressBtn, false);

        applyLogoutButton(logoutButton);
    }
}

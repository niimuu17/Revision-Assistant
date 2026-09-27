# Implementation Plan: Complete Migration from `styles.css` to Pure JavaFX & Plain-Page Notebook

## 1. Executive Summary & Objective

The user requested:
1. **Complete Removal of `styles.css`**: Remove all CSS stylesheets from the application and replace all styling across the entire project (Login, Main Menu / Dashboard, Courses, Quizzes, Calendar, Tasks, Notebook) with **pure JavaFX code**.
2. **Implementation of Plain-Page Notebook Layout**:
   - 4 unboxed buttons at top right (`+ Text`, `+ Code`, `+ Image`, `+ Screenshot`).
   - Single plain white continuous page below.
   - Default borderless text writing mode.
   - Simple white code box with faint/less visible border (`#e2e8f0`), monospace text, and top `✏️ Edit`, `📋 Copy`, `🗑️ Remove` options.
   - Frameless inline images and pasted screenshots with zero outer border/card wrapping, and top `✏️ Edit`, `🔍 Open Full`, `🗑️ Remove` options.

---

## 2. Architecture: Centralized JavaFX Styling Engine (`UITheme.java`)

To ensure clean, maintainable, and robust styling without duplicating style strings across controllers, we will create a dedicated JavaFX styling engine:
`com.example.study_buddy.UITheme`

### Why `UITheme.java`?
- **Zero CSS Files**: Eliminates external `.css` files and class-lookup overhead.
- **Type-Safe Design Tokens**: Centralizes all colors, fonts, insets, borders, and shadows as strongly-typed Java constants:
  - `PRIMARY_COLOR = "#4f46e5"`
  - `BORDER_COLOR = "#e2e8f0"`
  - `TEXT_DARK = "#1e293b"`
  - `BG_CANVAS = "#ffffff"`
  - `BG_MUTED = "#f8fafc"`
- **Programmatic State Handling**: Attaches hover, focus, and press effects dynamically using JavaFX properties (`node.hoverProperty()`, `node.focusedProperty()`, `node.pressedProperty()`).

```
+-----------------------------------------------------------------------------------+
|                              UITheme.java (Pure JavaFX)                          |
+-----------------------------------------------------------------------------------+
|  [Design Tokens]                                                                  |
|   • Colors: PRIMARY (#4f46e5), BORDER (#e2e8f0), MUTED_TEXT (#64748b)            |
|   • Fonts: Segoe UI, Consolas Monospace                                           |
|   • Shadows: DropShadow three-pass blur                                           |
+-----------------------------------------------------------------------------------+
|  [Component Stylers]                                                              |
|   • applyPrimaryButton(Button)         • applyToggleButton(Button)                |
|   • applySecondaryButton(Button)       • applyLogoutButton(Button)                |
|   • applyNavItem(Button, boolean)      • applyTextInput(TextInputControl)         |
|   • applyCourseCard(VBox)              • applyNotebookCard(VBox)                  |
|   • applyQuizCard(VBox)                • applyMcqOption(VBox, boolean selected)   |
|   • applyCalendarCell(VBox, boolean)   • makeBorderlessTextArea(TextArea)         |
|   • applyCodeContainer(VBox)           • applyTopActionButton(Button)             |
+-----------------------------------------------------------------------------------+
```

---

## 3. Mapping: CSS Selectors to JavaFX Programmatic Methods

Every class in `styles.css` maps directly to a clean JavaFX method in `UITheme`:

| Original CSS Class in `styles.css` | `UITheme` Method in Pure JavaFX | Behavior & Interactions |
|---|---|---|
| `.root`, `.main-container` | `UITheme.applyBackground(Region)` | Subtle linear gradient background (`#f8fafc` to `#e2e8f0`). |
| `.brand-panel`, `.brand-title`, `.brand-badge` | `UITheme.applyBrandPanel(...)` | Gradient background (`#312e81` to `#4f46e5`), bold labels. |
| `.btn-primary` | `UITheme.applyPrimaryButton(Button)` | Indigo gradient, bold white text, hover & pressed animations. |
| `.btn-toggle` | `UITheme.applyToggleButton(Button)` | Clean square toggle with hover highlight. |
| `.btn-logout` | `UITheme.applyLogoutButton(Button)` | Soft red background (`#fee2e2`), bold red text, hover highlight. |
| `.nav-item`, `.nav-item-active` | `UITheme.applyNavItem(Button, boolean)` | Transparent resting state, lavender highlight when active, hover feedback. |
| `.text-input` | `UITheme.applyTextInput(TextInputControl)` | Rounded border, focus shadow & indigo border on focus. |
| `.notebook-card`, `.course-card`, `.chapter-card` | `UITheme.applyCard(...)` | White card, rounded corners, subtle dropshadow, hover elevation. |
| `.quiz-mcq-card`, `.quiz-mcq-card-selected` | `UITheme.applyMcqOption(...)` | Dynamic border color and background for normal, selected, correct, and incorrect. |
| `.calendar-day-cell`, `.calendar-day-header` | `UITheme.applyCalendarCell(...)` | Day cell borders, current-day indicator badge, hover states. |
| `.doc-text-area` | `UITheme.makeBorderlessTextArea(TextArea)` | **Zero borders**, zero inset, transparent background, auto-expanding row count. |
| `.block-code-container`, `.code-text-area` | `UITheme.applyCodeSnippetBox(VBox, TextArea)` | **White box**, faint `#e2e8f0` border, dark monospace text. |
| `.doc-tool-btn` | `UITheme.applyUnboxedActionButton(Button)` | **Frameless button**, clean typography, soft hover background (`#f1f5f9`). |

---

## 4. Notebook Plain-Page Refactoring (In Pure JavaFX)

Incorporating the user's specific notebook requirements using pure JavaFX:

1. **Top Action Buttons (`+ Text`, `+ Code`, `+ Image`, `+ Screenshot`)**:
   - Placed in the top right header row without any enclosing grey box.
   - Styled via `UITheme.applyUnboxedActionButton(button)`:
     - No border, no permanent background box, smooth hover effect (`#f1f5f9`).
2. **Single Continuous White Page**:
   - `pagePlaygroundContainer` and `blocksContainer` set to pure white (`#ffffff`), with transparent scrollpane.
3. **Default Text Mode**:
   - Text blocks configured via `UITheme.makeBorderlessTextArea(textArea)`:
     - No borders, no inset shadow, no focus outlines.
     - Text renders seamlessly like typing directly on paper.
     - Hover bar with `▲`, `▼`, `🗑️` reorder/remove controls.
4. **Subtle Light Code Snippet Box**:
   - White background (`#ffffff`), 1px faint border (`#e2e8f0`), rounded corners (`8px`).
   - Top action bar right above the code box:
     - `✏️ Edit`: Language selection dropdown.
     - `📋 Copy`: Quick copy button with feedback transition.
     - `▲` / `▼`: Move up / down.
     - `🗑️ Remove`: Delete block.
5. **Pure Frameless Image & Screenshot Insertion**:
   - Raw `ImageView` placed directly on the white canvas with zero card padding or outline border.
   - Top action bar right above the image:
     - `✏️ Edit`: Replace image or update caption.
     - `🔍 Open Full`: View full-size image in default desktop viewer.
     - `▲` / `▼`: Move up / down.
     - `🗑️ Remove`: Delete image.

---

## 5. Migration Execution Steps

### Step 1: Create `com.example.study_buddy.UITheme.java`
- Implement all styling methods, colors, and dynamic hover/focus handlers in pure JavaFX.

### Step 2: Update `LoginController.java` & `login-view.fxml`
- Remove `stylesheets="@styles.css"` and `styleClass` references from `login-view.fxml`.
- Call `UITheme` helper methods during `initialize()` to style brand panel, inputs, and buttons.

### Step 3: Update `HelloController.java` & `hello-view.fxml`
- Remove `stylesheets="@styles.css"` and `styleClass` references from `hello-view.fxml`.
- Replace all `getStyleClass().add(...)` calls with corresponding `UITheme` methods for:
  - Navigation bar, top toolbar, and toggle buttons.
  - Courses & Progress view (cards, badges, 3-dot menus, chapter cards).
  - Quizzes view (question cards, MCQ option cards, result banner).
  - Calendar view (day cells, headers, badges, task pills).
  - Tasks & Deadlines sidebar.
- Implement the refined **plain-page notebook layout** in `HelloController.java` using `UITheme`.

### Step 4: Delete `src/main/resources/com/example/study_buddy/styles.css`
- Safely remove the external CSS file from the project.

### Step 5: Verification & Testing
- Run `$env:JAVA_HOME = "C:\Users\User\.jdks\ms-21.0.12"; .\mvnw.cmd test` to ensure all 29 tests pass.
- Verify UI flows (Login, Main Menu, Notebook, Courses, Quizzes, Calendar).

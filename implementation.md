# Implementation Plan: Earthy Forest & Warm Cream Theme (Pure JavaFX)

## 1. Overview & Visual Analysis

> **User's Request**:  
> *"recolor the app like this . dont use css, use only javafx. give me the implementation plan before coding"*

Based on the provided screenshot, the application is being transformed from the default cool slate/indigo palette into a warm, organic, earthy palette (Forest Green / Warm Sand / Ivory Cream / Sage).

### The Color Palette (Extracted from Screenshot)

| Token Name | Hex Code | Visual Role & Appearance |
|---|:---:|---|
| `BG_OUTER_SHELL` | `#526749` | **Outer Window Shell**: Earthy forest / moss green visible around the entire application window margins. |
| `BG_PANEL_CREAM` | `#f0e8dc` | **Top Header & Sidebars**: Warm light parchment / cream with soft rounded corners (`16px`). |
| `BG_CANVAS_CREAM` | `#f1eae0` | **Center Workspace Canvas**: Warm sand / parchment background with rounded corners (`18px`). |
| `BG_CARD_IVORY` | `#fcf9f2` | **Notebook & Content Cards**: Clean, warm ivory / milk card background with soft elevation. |
| `BTN_PRIMARY_BG` | `#445a3c` | **Primary Action Buttons & Hamburger Menu**: Deep olive / forest green (`+ New Notebook`, `☰` toggle). |
| `BTN_PRIMARY_FG` | `#f0e8dc` | **Primary Button Text / Icons**: Warm cream text on dark olive backgrounds. |
| `BTN_OUTLINE_BORDER` | `#3d5236` | **Outlined Action Buttons**: 1.5px solid dark olive border (`📖 My Notebooks`). |
| `BTN_OUTLINE_FG` | `#3d5236` | **Outlined Button Text**: Dark olive text for outlined buttons. |
| `TEXT_DARK_OLIVE` | `#1c2a18` | **Headings & Card Titles**: Deep forest charcoal for high contrast, crisp typography. |
| `TEXT_SUB_OLIVE` | `#4b5c46` | **Subtitles & Active Navigation**: Medium olive for subtitles and active links. |
| `TEXT_MUTED_OLIVE` | `#71816c` | **Muted Descriptions**: Olive-gray for secondary text and placeholders. |
| `PILL_SAGE_BG` | `#d8e2d4` | **Stats & Badges**: Soft sage green background for counts (`0 Topics · 0 Pages`). |
| `PILL_SAGE_FG` | `#2c3f26` | **Badge Text**: Deep forest text on sage badges. |

---

## 2. "Use Only JavaFX, Don't Use CSS" Architecture

To strictly fulfill the instruction **"dont use css, use only javafx"**:
1. **Zero External CSS Files**: We will NOT add rules to `styles.css`.
2. **Centralized Pure JavaFX Theme Class (`AppTheme.java`)**:
   - Creates a dedicated JavaFX class: `com.example.study_buddy.AppTheme`.
   - Defines all color tokens, fonts, insets, borders, and effects as static Java constants.
   - Provides programmatic styling methods using JavaFX APIs (`node.setStyle(...)`, `setBackground(...)`, `setTextFill(...)`, `setFont(...)`, `setEffect(...)`).
   - Automatically handles dynamic hover, pressed, and focus interactions programmatically via JavaFX event listeners (`setOnMouseEntered`, `setOnMouseExited`, `focusedProperty()`).
3. **Application Lifecycle Integration**:
   - In `hello-view.fxml`: Apply initial root background `-fx-background-color: #526749;` directly to avoid initial rendering flicker.
   - In `HelloController.java`: Inside `initialize()`, call `AppTheme.applyTheme(this)` to color all static containers, headers, toolbars, buttons, and navigation elements.
   - In dynamic generators (`createNotebookCard`, `loadTopicsExplorer`, etc.), apply `AppTheme.applyNotebookCard(...)` so dynamically generated cards strictly match the ivory/sage aesthetic shown in the screenshot.

---

## 3. Component-by-Component Redesign

### A. Window Shell & Outer Container
- Outer container: `VBox` root in `hello-view.fxml`.
- Style: `-fx-background-color: #526749; -fx-padding: 14px 20px 16px 20px;`.
- Spacing: `14px` between header and workspace.

### B. Top Header Bar (`appTopBar`)
- Background: `#f0e8dc` (Warm parchment cream).
- Corner radius: `16px`.
- Padding: `12px 18px`.
- Border: `none` or subtle `-fx-border-color: #e4d8c8; -fx-border-radius: 16px;`.
- **Hamburger Toggle Button (`leftToggleBtn`)**:
  - Background: `#445a3c` (Deep Olive).
  - Text fill: `#f0e8dc` (Cream).
  - Size: ~38x38px, corner radius `8px`.
  - Hover: slightly lighter olive `#4e6544`.
- **Title Block**:
  - `Study Buddy 🎓`: `-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1c2a18;`.
  - `welcomeText`: `-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #4b5c46;`.
  - `userDetailText`: `-fx-font-size: 11px; -fx-text-fill: #71816c;`.
- **Right Action Button (`rightToggleBtn`)**:
  - Warm cream background, rounded rectangle, dark olive icon fill.

### C. Main Menu / Recent Notebooks Canvas (`mainMenuView`)
- Background: `#f1eae0` (Warm Sand / Parchment).
- Corner radius: `18px`.
- Padding: `24px 28px`.
- **Header Row**:
  - Title: `Recent notebooks` (`-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1c2a18;`).
  - **`📖 My Notebooks` Button**:
    - Pill shape (corner radius `10px`).
    - Background: `transparent`.
    - Border: `1.5px solid #3d5236`.
    - Text: `#3d5236` (bold 12px).
    - Hover: soft olive cream highlight (`#e8dfd2`).
  - **`+ New Notebook` Button**:
    - Pill shape (corner radius `10px`).
    - Background: `#445a3c` (Deep Olive).
    - Text: `#f0e8dc` (Cream, bold 12px).
    - Hover: `#4e6544`.

### D. Notebook Cards (`createNotebookCard`)
- Dimensions: `224px x 128px`.
- Background: `#fcf9f2` (Light Ivory Cream).
- Corner radius: `14px`.
- Shadow: `dropshadow(three-pass-box, rgba(50, 70, 45, 0.08), 8, 0, 0, 2)`.
- Hover animation: subtle lift + slightly deeper shadow `dropshadow(three-pass-box, rgba(50, 70, 45, 0.14), 10, 0, 0, 3)`.
- Accent stripe: removed or integrated seamlessly into the ivory card.
- **Card Content**:
  - Dot: `•` in soft sage `#879981`.
  - Title: `-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1c2a18;`.
  - 3-Dot Options Button: `⋮` in `#7a8a75` pinned to top right.
  - Description: `-fx-font-size: 11px; -fx-text-fill: #71816c;`.
  - **Stats Badge**:
    - Pill: `-fx-background-color: #d8e2d4; -fx-text-fill: #2c3f26; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 2px 7px;`.
  - **Open Link**:
    - `-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #1c2a18; -fx-cursor: hand;`.

### E. Sidebars & Navigation Items
- Background: `#f0e8dc` with rounded corners `16px`.
- Nav items:
  - Inactive: transparent background, `#4b5c46` text.
  - Active: `#d8e2d4` sage background, `#1c2a18` dark olive bold text.
  - Hover: `#e6decb` soft parchment tint.
- Logout Button:
  - Soft coral-tinted parchment `-fx-background-color: #f7e6e2; -fx-text-fill: #993b2a; -fx-font-weight: bold; -fx-background-radius: 8px;`.

---

## 4. Files to Create & Modify

| File | Type of Change | Description |
|---|:---:|---|
| `src/main/java/com/example/study_buddy/AppTheme.java` | **New File** | Pure JavaFX theme manager containing all design tokens, programmatic styling methods, and dynamic hover/focus handlers. |
| `src/main/java/com/example/study_buddy/HelloController.java` | **Modify** | Call `AppTheme.applyTheme(this)` in `initialize()`, apply card styling in `createNotebookCard()`, and update view switchers to maintain consistent colors. |
| `src/main/resources/com/example/study_buddy/hello-view.fxml` | **Modify** | Add `fx:id="mainRootContainer"` and initial inline JavaFX styles matching the `#526749` outer shell and `#f0e8dc` header. |

---

## 5. Verification Plan

1. **Automated Unit Tests**:
   - Run `$env:JAVA_HOME = "C:\Users\User\.jdks\ms-21.0.12"; .\mvnw.cmd test` to ensure all 29 tests continue passing.
2. **Visual & Interactive Verification**:
   - Launch application and compare directly against the user's screenshot:
     - Outer shell background is Earthy Forest Green (`#526749`).
     - Top bar is rounded Warm Cream (`#f0e8dc`).
     - Hamburger toggle is Deep Olive (`#445a3c`) with cream icon.
     - Canvas is Warm Sand (`#f1eae0`) with rounded corners.
     - `Recent notebooks` header and buttons (`📖 My Notebooks` outline, `+ New Notebook` solid olive).
     - Notebook cards are Light Ivory (`#fcf9f2`) with sage pills (`#d8e2d4`) and dark olive text.

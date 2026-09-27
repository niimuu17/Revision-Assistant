# Implementation Plan: OneNote Clean Page with Single `+ Image` Button & Clickable Media Actions

## 1. User Requirements Breakdown

> **User's Request**:  
> *"Inline title will be page name. And after clicking the screenshot show remove and copy option, just keep a +image button. And set image functionality as screenshot. Show me implementation plan."*

### Key Specifications:
1. **Inline Title as Page Name**:
   - The page name (`page.getTitle()`, e.g. "Class") serves as the large inline editable title (26px, bold, borderless).
   - Editing the title auto-updates the page name in SQLite database and in the topics explorer breadcrumb.
   - Thin horizontal divider line underneath the title.
   - Formatted date and time line below the divider (`Tuesday, September 22, 2026      7:41 PM`).
2. **Just ONE Button: `+ Image`**:
   - Remove `+ Code`, `+ Text`, and `+ Screenshot` buttons.
   - Keep only a single clean, unboxed `🖼 + Image` button at the top right header (next to the discreet `Saved ✓` indicator).
3. **Unified Image & Screenshot Functionality**:
   - **`Ctrl + V` Typing Shortcut**: While typing in any text area, pressing `Ctrl + V` with an image in the clipboard automatically splits the text, inserts the screenshot directly below, creates a new text line underneath, and shifts focus there so writing continues uninterrupted.
   - **Clicking `+ Image` Button**: Checks the clipboard first—if a screenshot/image is in the clipboard, it inserts it instantly; if no image is on the clipboard, it opens a file chooser dialog to select an image from disk.
4. **Clickable Media Actions (`📋 Copy` & `🗑 Remove`)**:
   - When viewing the page, images/screenshots render **completely clean and frameless** with zero borders and zero extra boxes (matching the reference screenshot).
   - **When the user clicks on the image/screenshot**:
     - A sleek floating action bar appears on top of the image containing:
       - `📋 Copy`: Copies the image back to the system clipboard (shows "✓ Copied!").
       - `🗑 Remove`: Deletes the image block from the page.
     - Clicking again or clicking elsewhere deselects the image and hides the toolbar.

---

## 2. Visual Layout & Interaction Mockup

```
+-----------------------------------------------------------------------------------------------+
|  Class                                                     [ Saved ✓ ]        [ 🖼 + Image ]  |
|  -------------------------------------------------------------------------------------------  |
|  Tuesday, September 22, 2026      7:41 PM                                                     |
|                                                                                               |
|  I love my country                                                                            |
|                                                                                               |
|  +-- [ When user clicks image: Action bar appears ] -----------------[ 📋 Copy ] [ 🗑 Remove ]+  |
|  |                                                                                         |  |
|  |                 [ Pure Inline Image / Screenshot - Frameless Display ]                   |  |
|  |                                                                                         |  |
|  +-----------------------------------------------------------------------------------------+  |
|                                                                                               |
|  I love my country too|                                                                       |
|  (cursor is blinking here, ready to continue typing...)                                       |
|                                                                                               |
+-----------------------------------------------------------------------------------------------+
```

---

## 3. Technical Implementation Details

### A. OneNote-Style Header with Single `+ Image` Button (`HelloController.java`)
In `renderPageCanvas(Page page)`:
- Left:
  - `pageTitleField`: 26px bold text field, borderless, transparent, bound to `page.getTitle()`.
  - On focus lost or Enter: saves the new title to SQLite and updates the notebook topics explorer.
- Right:
  - `saveBadge`: "Saved ✓"
  - `addImageBtn`: Single unboxed button labeled `🖼 + Image`.
- Divider:
  - 1px thin border line (`#e2e8f0`).
- Subtitle:
  - Date and time formatted dynamically from `page.getUpdatedAt()` or current time (e.g. `Tuesday, September 22, 2026      7:41 PM`).

### B. Smart Image Insertion Logic (`handleSmartImageOrScreenshot`)
Unified handler for both clicking `+ Image` and pressing `Ctrl + V`:
1. **Clipboard Check**:
   - If `Clipboard.getSystemClipboard().hasImage()`:
     - Extract `Image fxImage = clipboard.getImage()`.
     - Save to `study_buddy_data/images/<notebookId>/`.
     - Insert `PageBlock(TYPE_IMAGE, targetFile.getAbsolutePath(), "")`.
   - If no clipboard image (when clicking `+ Image`):
     - Open `FileChooser` dialog to let the user select PNG/JPG/GIF.
2. **Seamless Text Splitting (for `Ctrl + V`)**:
   - If user is typing in paragraph `i`:
     - Text before cursor stays in paragraph `i`.
     - Image block inserted at `i + 1`.
     - Text after cursor (or new empty paragraph) created at `i + 2`.
     - Auto-focus set on text block `i + 2`.

### C. Click-to-Action on Image Blocks (`createImageBlockNode`)
- Media container starts with `actionBar.setVisible(false)` and `actionBar.setManaged(false)`.
- When user clicks on the `ImageView`:
  - Toggle `actionBar`:
    - `📋 Copy`:
      - Copies the image file / FX image to system clipboard.
      - Temporarily changes text to `✓ Copied!`.
    - `🗑 Remove`:
      - Deletes the block from `currentPageBlocks`, saves, and refreshes the canvas.
    - `🔍 Open Full`:
      - Opens in default system image viewer.
- When clicking on text or another area, the action bar deselects and hides.

---

## 4. Modified Files & Components

| Target File | Changes |
|---|---|
| [HelloController.java](file:///c:/Users/User/IdeaProjects/Study_Buddy/src/main/java/com/example/study_buddy/HelloController.java) | - Re-architect header: inline page name title + divider + date/time + single `+ Image` button.<br>- Implement `handleSmartImageOrScreenshot()` supporting clipboard screenshot paste and file dialog fallback.<br>- Implement `Ctrl + V` key interceptor on text areas.<br>- Implement click-to-show `📋 Copy` and `🗑 Remove` action bar on images.<br>- Auto-focus text writing area on page open. |
| [styles.css](file:///c:/Users/User/IdeaProjects/Study_Buddy/src/main/resources/com/example/study_buddy/styles.css) | - Add styles for the floating image action bar and clean OneNote header styling. |
| [implementation.md](file:///c:/Users/User/IdeaProjects/Study_Buddy/implementation.md) | - Document the OneNote document design, single `+ Image` button, and click-to-action media behavior. |

---

## 5. Verification Plan

1. **Automated Unit Tests**:
   - Run `$env:JAVA_HOME = "C:\Users\User\.jdks\ms-21.0.12"; .\mvnw.cmd test` to ensure all 29 tests pass.
2. **Interactive UI Verification**:
   - Open a page: Confirm the inline title displays the page name with a thin underline and date/time beneath.
   - Confirm only the `🖼 + Image` button is present at the top (no code, no separate screenshot button).
   - Type text, press `Ctrl + V` with a copied screenshot: verify it embeds inline and creates a new focused text area below.
   - Click the image: verify the `📋 Copy` and `🗑 Remove` options appear.
   - Click `📋 Copy`: verify image is copied back to clipboard.
   - Click `🗑 Remove`: verify image is deleted cleanly.

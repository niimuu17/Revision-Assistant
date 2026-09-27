# Implementation Plan: PC Image Upload with Immediate Auto-Return to Text Mode

## 1. User Intent & Workflow

> **User's Request**:  
> *"+image will allow to upload picture from pc"*  
> *"after insert a image or paste screenshot again go to text mode"*

### Unified Flow:
1. **Upload Picture from PC (`+ Image`)**:
   - Clicking `🖼 + Image` opens the PC file explorer dialog (`FileChooser`).
   - After selecting an image file, the image is embedded cleanly into the plain white page.
   - A new text paragraph is automatically created directly underneath the image.
   - **Immediate Text Mode**: Focus is automatically placed inside this new text area with a blinking cursor (`Platform.runLater(textArea::requestFocus)`), allowing the user to start typing immediately with zero extra clicks.
2. **Paste Screenshot (`Ctrl + V`)**:
   - Pressing `Ctrl + V` while writing automatically splits the text at the cursor, embeds the screenshot, creates a text paragraph beneath, and immediately returns to text mode by focusing the cursor in the continuation text area.

---

## 2. Technical Modifications

### In `HelloController.java`:
1. **Wire `+ Image` Button to PC File Upload**:
   - In `renderPageCanvas()`:
     ```java
     Button addImageBtn = createUnboxedButton("🖼 + Image", this::promptUploadImage);
     ```
2. **Auto-Return to Text Mode in `promptUploadImage()`**:
   ```java
   PageBlock newBlock = new PageBlock(PageBlock.TYPE_IMAGE, targetFile.getAbsolutePath(), "");
   currentPageBlocks.add(newBlock);
   
   // Automatically append text block beneath the image
   PageBlock nextTextBlock = new PageBlock(PageBlock.TYPE_TEXT, "", "");
   currentPageBlocks.add(nextTextBlock);
   
   saveCurrentPageBlocks();
   targetFocusBlockIndex = currentPageBlocks.size() - 1; // Target text block for immediate focus
   refreshBlocksView();
   ```
3. **Auto-Return to Text Mode in `handleSmartInlinePaste()`**:
   - After inserting a pasted screenshot, sets `targetFocusBlockIndex = blockIndex + 2;` and schedules `requestFocus()` on the text area below the image.

---

## 3. Files to Modify

| Target File | Changes |
|---|---|
| [HelloController.java](file:///c:/Users/User/IdeaProjects/Study_Buddy/src/main/java/com/example/study_buddy/HelloController.java) | - Connect `+ Image` button to `promptUploadImage()`.<br>- Update `promptUploadImage()` to automatically append and focus an empty text area directly below the newly inserted image. |
| [implementation.md](file:///c:/Users/User/IdeaProjects/Study_Buddy/implementation.md) | - Document the PC image upload and immediate auto-return to text mode. |

---

## 4. Verification Plan

1. **Automated Unit Tests**:
   - Run `$env:JAVA_HOME = "C:\Users\User\.jdks\ms-21.0.12"; .\mvnw.cmd test` to ensure all 29 tests pass.
2. **Interactive Flow Check**:
   - Click `🖼 + Image`, select an image from PC: verify the image is inserted and the blinking cursor immediately appears in the text area below it. Type characters to confirm instant text mode.
   - Copy a screenshot and press `Ctrl + V` while typing: verify the screenshot embeds and the blinking cursor immediately appears in the text area below it.

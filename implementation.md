# Implementation Plan: Unified "Plain Window" Notebook Document Layout

> **User Feedback**:  
> *"In notebook, I want a plain window. Now notes, code snippet, screenshot, picture are saving in their separate portion, so it seems less organized. Give me an implementation plan of more organized layout."*
>
> **Core Objective**:  
> Transform the notebook workspace from a fragmented series of disconnected card boxes into a **clean, unified, and organized "Plain Window" Document Canvas** (similar to Notion, Apple Notes, OneNote, and GitHub markdown).

---

## 1. Problem Analysis & UX Transformation

| Current Experience (Fragmented Cards) | Proposed Experience (Unified Plain Window) |
|---|---|
| **Separated Portions**: Every note, code snippet, and screenshot is wrapped in a heavy bordered box (`.block-card`) with redundant headers (`📝 Note`, `🖼 Image`). | **Seamless Document Canvas**: A clean, distraction-free white document workspace (`#ffffff`) where notes, code snippets, and pictures flow naturally as one unified page. |
| **Awkward Bottom Insertion Bar**: Buttons to add notes, code, or images are placed at the bottom, far below the content. | **Top Sticky Action Toolbar**: A modern document formatting bar at the top with quick one-click insertion tools. |
| **Visual Clutter**: Every block has permanent delete buttons, borders, and separate scrollable text areas. | **Clean Inline Embeds with Hover Controls**: Clean typography with seamless auto-resizing text; code snippets and images display as sleek embedded elements with subtle controls appearing on hover. |
| **No Organization Filtering**: All types are mixed in one long stack. | **Organized View Tabs**: `[ 📄 All ]  [ 📝 Notes ]  [ 💻 Code ]  [ 🖼 Media ]` allowing the user to view the complete document flow or instantly filter by content type. |

---

## 2. Layout & Visual Mockup

```
+-------------------------------------------------------------------------------------------------------+
| ◀ Back to Notebooks   |  📂 Topics   |   📘 Data Structures › 📂 Trees › 📄 Binary Search Trees     |
+-------------------------------------------------------------------------------------------------------+
|                                                                                                       |
|  DOCUMENT CANVAS (Plain Window)                                                                       |
|  +-------------------------------------------------------------------------------------------------+  |
|  | [ + Add Text ]  [ + Code Snippet ]  [ + Image ]  [ 📷 Paste Screenshot (Ctrl+V) ]               |  |
|  | Filter View: (•) All Document   ( ) Notes Only   ( ) Code Only   ( ) Media Only                 |  |
|  +-------------------------------------------------------------------------------------------------+  |
|                                                                                                       |
|  Binary Search Trees - Implementation & Traversal                                      [ Saved ✓ ]   |
|  Last edited: Today at 16:15                                                                          |
|  ---------------------------------------------------------------------------------------------------  |
|                                                                                                       |
|  A Binary Search Tree (BST) is a node-based binary tree data structure with the following            |
|  properties: The left subtree of a node contains only nodes with keys lesser than the node’s key,     |
|  and the right subtree contains only nodes with keys greater than the node’s key.                     |
|                                                                                                       |
|  +---[ Java: BST Node Definition ]--------------------------------------------------[ 📋 Copy Code ]+  |
|  | class Node {                                                                                     |  |
|  |     int key;                                                                                     |  |
|  |     Node left, right;                                                                            |  |
|  |     public Node(int item) { key = item; left = right = null; }                                   |  |
|  | }                                                                                                |  |
|  +--------------------------------------------------------------------------------------------------+  |
|                                                                                                       |
|  Here is the visualization diagram for standard in-order traversal:                                  |
|                                                                                                       |
|  [                     🖼 BST Traversal Diagram (Click to Zoom)                                    ]  |
|                        Caption: In-order traversal visits nodes in ascending sorted order             |
|                                                                                                       |
|  When implementing the delete operation, we have three cases to consider:                            |
|  1. Node to be deleted is a leaf.                                                                     |
|  2. Node to be deleted has only one child.                                                            |
|  3. Node to be deleted has two children (find in-order successor).                                    |
|                                                                                                       |
+-------------------------------------------------------------------------------------------------------+
```

---

## 3. Technical Implementation Details

### A. Seamless Document Canvas Architecture
In `HelloController.java` (`renderPageCanvas(Page page)`):
1. **Document Wrapper**:
   - Host the canvas inside a clean, centered document sheet with responsive max-width (e.g. 840px–900px, like a real document) or full-width with generous padding (32px).
   - Background is clean white (`#ffffff`), border-radius 10px, subtle shadow, mimicking a clean digital notepad.

2. **Top Document Action & Filter Bar**:
   - Relocate insertion buttons from the bottom to the top header right beneath the page title:
     - `📝 + Text`
     - `💻 + Code`
     - `🖼 + Image`
     - `📷 Paste Screenshot (Ctrl+V)`
   - Add filter toggle chips:
     - `📄 All` (shows full continuous document)
     - `📝 Notes` (shows only text notes)
     - `💻 Code` (shows only code snippets)
     - `🖼 Media` (shows only screenshots and pictures)

3. **Borderless Text Blocks**:
   - Remove `.block-card` borders, headers, and backgrounds around text.
   - Text areas render directly on the white canvas with transparent background and no focus borders.
   - Dynamic height expansion: text expands naturally as lines are typed, without awkward internal scrollbars.

4. **Embedded Code Snippets**:
   - Sleek dark card (`#0f172a`), language pill dropdown (Java, Python, C++, etc.), and a 1-click `📋 Copy Code` button.
   - Minimal action toolbar on hover (Move Up, Move Down, Delete).

5. **Inline Media & Screenshots**:
   - Centered with automatic aspect ratio scaling (`fitWidth = 720px`).
   - Clean caption field underneath that looks like real document typography.
   - Quick action bar on hover (Zoom, Open in Default App, Move, Delete).

6. **Full Backward Compatibility**:
   - Zero database schema changes required. Existing `PageBlock` JSON storage (`[{"type":"TEXT", ...}, {"type":"CODE", ...}, {"type":"IMAGE", ...}]`) works identically, ensuring all existing notes open with the new layout without data loss.

---

## 4. Files to Modify

| File | Changes |
|---|---|
| [HelloController.java](file:///c:/Users/User/IdeaProjects/Study_Buddy/src/main/java/com/example/study_buddy/HelloController.java) | - Re-architect `renderPageCanvas()` to use a top document toolbar and unified plain document canvas.<br>- Streamline `createTextBlockNode()`, `createCodeBlockNode()`, and `createImageBlockNode()` into borderless inline document sections.<br>- Add view filtering (`All`, `Notes`, `Code`, `Media`).<br>- Add hover controls for delete and reordering. |
| [styles.css](file:///c:/Users/User/IdeaProjects/Study_Buddy/src/main/resources/com/example/study_buddy/styles.css) | - Add styles for `.plain-document-canvas`, `.doc-top-toolbar`, `.doc-filter-chip`, `.doc-text-area`, and `.doc-inline-media`.<br>- Refine `.block-code-container` for seamless inline integration. |
| [hello-view.fxml](file:///c:/Users/User/IdeaProjects/Study_Buddy/src/main/resources/com/example/study_buddy/hello-view.fxml) | Ensure `pagePlaygroundContainer` has clean transparent styling to host the document canvas smoothly. |

---

## 5. Verification Plan

1. **Automated Unit Tests**:
   - Run `$env:JAVA_HOME = "C:\Users\User\.jdks\ms-21.0.12"; .\mvnw.cmd test` to ensure all 29 tests pass.
2. **Plain Document Canvas Test**:
   - Open a notebook &rarr; open a topic &rarr; open a page.
   - Verify the page displays as a clean, unified white document without fragmented card boxes.
3. **Multi-Content Flow Test**:
   - Add notes, insert code snippet, paste screenshot, and upload image.
   - Verify they render inline as a cohesive single document.
4. **Top Toolbar Insertion Test**:
   - Test `+ Text`, `+ Code`, `+ Image`, and `Paste Screenshot (Ctrl+V)` from the top toolbar.
5. **Filter View Test**:
   - Switch between `All`, `Notes`, `Code`, and `Media` to verify instant organization.
6. **Data Persistence Test**:
   - Edit text, change code language, copy code &rarr; reopen page &rarr; verify all content is saved correctly in SQLite.

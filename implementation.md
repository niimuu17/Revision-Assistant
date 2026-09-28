# Implementation Plan: Quiz UI Simplification, Multi-Format File Upload, and Bypass System Removal

> **User Request**:  
> 1. Remove the text "AI study quiz, and its below line test you knowledge...., and bottom of the page ''Ready to create quiz"  
> 2. Change "Generate quiz with AI" to "Generate quiz"  
> 3. Upload file in this section should be taken pdf, doc, pptx, image, code  
> 4. Remove bypass entry system.  
> *Show me implementation plan*

---

## 1. Requirement Breakdown & Scope

### 1.1 Remove Header Title, Subtitle, and Bottom Status in Quiz View
- **Top Header Bar**: Remove the `VBox` containing:
  - `quizHeaderTitle` Label (`"🧠 AI Study Quiz"`)
  - `quizHeaderSubtitle` Label (`"Test your knowledge with AI-generated MCQs and short answer evaluations"`)
  - The top bar will now cleanly show: `[◀ Back to Main Menu]`, flexible space, `[⚙️ API Key]`, and `[🔄 New Quiz]`.
- **Bottom Status Area**:
  - In `hello-view.fxml`, set initial `quizProgressLabel` text to `""` (empty string) instead of `"Ready to create quiz"`.
  - In `HelloController.java` (`handleResetQuiz()`), set `quizProgressLabel.setText("")` so no `"Ready to create quiz"` text appears upon reset. During active quizzes, it will continue to show actual question progress (e.g., `"Answered 2 of 5 questions"`).

### 1.2 Change "Generate quiz with AI" to "Generate quiz"
- In `hello-view.fxml`, update the primary generation button (`generateQuizBtn`):
  - Current: `text="🚀 Generate Quiz with AI"`
  - Updated: `text="Generate quiz"`

### 1.3 Support PDF, DOC, PPTX, Image, and Code in Source Upload
- **Button Text**: Change button from `"📎 Upload File (.txt, .md, code)"` to `"📎 Upload File (PDF, DOC, PPTX, Image, Code)"`.
- **FileChooser Filters**: Update `handleUploadSourceFile()` to include:
  - All Supported Files (`*.pdf`, `*.doc`, `*.docx`, `*.pptx`, `*.ppt`, `*.png`, `*.jpg`, `*.jpeg`, `*.webp`, `*.bmp`, `*.gif`, `*.txt`, `*.md`, `*.java`, `*.py`, `*.c`, `*.cpp`, `*.cs`, `*.js`, `*.ts`, `*.html`, `*.css`, `*.json`, `*.sql`, `*.sh`)
  - PDF Documents (`*.pdf`)
  - Word Documents (`*.docx`, `*.doc`)
  - PowerPoint Presentations (`*.pptx`, `*.ppt`)
  - Image Files (`*.png`, `*.jpg`, `*.jpeg`, `*.webp`, `*.bmp`, `*.gif`)
  - Code & Text Files (`*.txt`, `*.md`, `*.java`, `*.py`, `*.c`, `*.cpp`, `*.js`, `*.ts`, `*.html`, `*.css`, `*.json`, `*.sql`)
  - All Files (`*.*`)
- **Handling Images & Binary Documents**:
  - **Text / Document / Code**: Read content via `QuizSourceHelper.readFileContent(file)` (already uses PDFBox for PDF and OpenXML Zip parsing for `.docx` and `.pptx`; add string extraction fallback for legacy `.doc`).
  - **Images**: When an image file (`.png`, `.jpg`, `.jpeg`, `.webp`, etc.) is chosen, do not read it as text. Display `"🖼️ " + chosen.getName() + " (" + (chosen.length() / 1024) + " KB Image)"` in `sourceFileLabel`.
  - **AI Generation**: In `GeminiApiService.java`, add `generateQuizFromImageAsync(...)` using `buildGeminiImageRequestBody()` to send multimodal image data directly to Google Gemini, allowing users to generate quizzes from handwritten notes, whiteboard photos, diagrams, and textbook screenshots!

### 1.4 Remove Bypass Entry System
- In `login-view.fxml`:
  - Remove the `⚡ Enter (Bypass Login)` button from the **Log In** card.
  - Remove the `⚡ Enter (Bypass Login)` button from the **Sign Up** card.
- In `LoginController.java`:
  - Remove the `@FXML public void handleBypassLogin()` method.
- In `DatabaseHelper.java`:
  - Deprecate / remove `getFirstOrCreateDevUser()`.

---

## 2. Detailed Technical Changes

### File 1: `src/main/resources/com/example/study_buddy/hello-view.fxml`
1. **Remove Quiz Header Labels**:
   ```xml
   <!-- Remove:
   <VBox spacing="2.0">
       <Label fx:id="quizHeaderTitle" text="🧠 AI Study Quiz" ... />
       <Label fx:id="quizHeaderSubtitle" text="Test your knowledge..." ... />
   </VBox>
   -->
   ```
2. **Update Upload Button Text**:
   ```xml
   <!-- From: text="📎 Upload File (.txt, .md, code)" -->
   <!-- To: -->
   <Button onAction="#handleUploadSourceFile" styleClass="btn-secondary-action" text="📎 Upload File (PDF, DOC, PPTX, Image, Code)" />
   ```
3. **Update Generate Quiz Button Text**:
   ```xml
   <!-- From: text="🚀 Generate Quiz with AI" -->
   <!-- To: -->
   <Button fx:id="generateQuizBtn" onAction="#handleGenerateQuiz" ... text="Generate quiz" />
   ```
4. **Remove "Ready to create quiz" text from Bottom Bar**:
   ```xml
   <!-- From: text="Ready to create quiz" -->
   <!-- To: text="" -->
   <Label fx:id="quizProgressLabel" text="" style="-fx-font-size: 12px; -fx-text-fill: #71816c;" />
   ```

---

### File 2: `src/main/java/com/example/study_buddy/HelloController.java`
1. In `handleResetQuiz()`:
   - Change `quizProgressLabel.setText("Ready to create quiz");` to `quizProgressLabel.setText("");`.
2. In `handleUploadSourceFile()`:
   - Add comprehensive `FileChooser.ExtensionFilter` entries for PDF, Word (.doc, .docx), PowerPoint (.pptx, .ppt), Images (.png, .jpg, .jpeg, .webp, .bmp, .gif), and Code/Text files.
   - Detect image files via `QuizSourceHelper.isImageFile(chosen)`. For images, set `uploadedQuizFileContent = ""` and display `sourceFileLabel.setText("🖼️ " + chosen.getName() + " (" + (chosen.length() / 1024) + " KB Image)")`.
   - For document/code files, extract text via `QuizSourceHelper.readFileContent(chosen)` and display summary.
3. In `handleGenerateQuiz()`:
   - Check if `uploadedQuizFile != null && QuizSourceHelper.isImageFile(uploadedQuizFile)`.
   - If true, dispatch to `geminiApiService.generateQuizFromImageAsync(prompt, uploadedQuizFile, numQuestions, difficulty, typeMode)`.
   - If false, continue dispatching text-based `geminiApiService.generateQuizAsync(...)`.

---

### File 3: `src/main/java/com/example/study_buddy/QuizSourceHelper.java`
1. Update `isImageFile(File file)` to also recognize `.bmp` and `.gif` alongside `.png`, `.jpg`, `.jpeg`, and `.webp`.
2. Update `readFileContent(File file)`:
   - Add fallback text extractor for `.doc` files (scanning printable ASCII/UTF-8 strings from binary compound streams).

---

### File 4: `src/main/java/com/example/study_buddy/GeminiApiService.java`
1. Add `generateQuizFromImageAsync(String topicPrompt, File imageFile, int numQuestions, String difficulty, String questionTypeMode)`.
2. Implement `generateQuizFromImage(...)`:
   - Builds prompt requesting JSON quiz format matching `generateQuiz`.
   - Reads image bytes and calls `buildGeminiImageRequestBody(promptBuilder.toString(), imageBytes, mimeType)`.
   - Executes request via `sendGeminiRequest` and parses JSON into `QuizSession`.

---

### File 5: `src/main/resources/com/example/study_buddy/login-view.fxml`
1. Remove `⚡ Enter (Bypass Login)` `<Button>` from the Login form.
2. Remove `⚡ Enter (Bypass Login)` `<Button>` from the Sign Up form.

---

### File 6: `src/main/java/com/example/study_buddy/LoginController.java`
1. Remove `handleBypassLogin()` method.

---

## 3. Verification & Validation Plan

1. **Build & Unit Test Verification**:
   - Run `$env:JAVA_HOME = "C:\Users\User\.jdks\ms-21.0.12"; .\mvnw.cmd test`
   - Confirm all 29 tests pass with zero regressions.
2. **Visual & UI Verification**:
   - Run `$env:JAVA_HOME = "C:\Users\User\.jdks\ms-21.0.12"; .\mvnw.cmd javafx:run`
   - Verify Login screen shows only "Log In", "Sign Up", and credential inputs (bypass button completely gone).
   - Navigate to Quiz view:
     - Verify header has NO "AI Study Quiz" and NO "Test your knowledge..." subtitle.
     - Verify bottom status has NO "Ready to create quiz" text.
     - Verify button says `"Generate quiz"`.
     - Click "📎 Upload File (PDF, DOC, PPTX, Image, Code)":
       - Verify file dialog filter dropdown includes all categories (PDF, DOC, PPTX, Images, Code).
       - Select an image (PNG/JPG) or document (PDF/DOCX/PPTX) and confirm source chip displays properly.
       - Click "Generate quiz" and verify questions are generated from the uploaded file.

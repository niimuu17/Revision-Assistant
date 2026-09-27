# Implementation Plan: Full Quiz Explanation with See More/See Less & Unsubmitted Answers

## 1. Overview & Context

> **User's Request**:  
> *"want to see full explanation by see more/see less and for unsubmitter answer after the line ''no response was submitted ...." simple give explanation of the answer with see more/see less . give me implemetation plan"*

### The Issue Identified:
1. **Explanation Cutoff (Ellipsis `...`)**:
   - In the review mode, `💡 Explanation:` currently uses a basic JavaFX `Label` without `setMinHeight(Region.USE_PREF_SIZE)` or width binding.
   - When the explanation exceeds the horizontal boundary, JavaFX clips the label with an ellipsis (e.g. `using only vertice...`), with no button or mechanism for the user to view the rest of the text.
2. **Unsubmitted Short Answer Handling**:
   - When a student submits a quiz with unanswered short-answer questions, the card should clearly state `"No response was submitted."` and immediately below that line, provide a clean `💡 Explanation:` box containing the expected answer / rubric with a **"See More ▾" / "See Less ▴"** toggle to expand and read the full explanation.

---

## 2. Detailed UX & Layout Specification

### A. MCQ Explanation with Expandable Toggle
- **Trigger**: Displayed below the options grid when reviewing the quiz.
- **Header**: `💡 Explanation:` (bold indigo/purple `#4338ca`).
- **Content**: `q.getExplanation()`.
- **Expansion Behavior**:
  - `explText` is configured with `setWrapText(true)`, `setMinHeight(Region.USE_PREF_SIZE)`, and `prefWidthProperty().bind(card.widthProperty().subtract(60))` so text wraps properly and is never accidentally clipped by JavaFX.
  - If `q.getExplanation().length() > 140`:
    - Shows an excerpt (~130 chars ending at nearest word boundary) + `...`.
    - Shows a `See More ▾` button styled with `.quiz-see-more-btn`.
    - Clicking `See More ▾` reveals the full, untruncated explanation and flips the button to `See Less ▴`.
    - Clicking `See Less ▴` collapses back to the excerpt and flips the button to `See More ▾`.
  - If `<= 140`: displays full explanation directly without the button.

### B. Unsubmitted / Blank Short Answer Questions
- **Detection**: `q.getStudentAnswer() == null || q.getStudentAnswer().trim().isEmpty()`.
- **Display**:
  - Score badge: `Score: 0 / 5 pts`.
  - Unsubmitted notice line:
    ```
    No response was submitted.
    ```
  - Directly underneath the `"No response was submitted."` line:
    - Dedicated `💡 Explanation:` box displaying the expected answer / key concepts from `q.getRubric()`.
    - Expandable toggle:
      - If `q.getRubric().length() > 140`: shows excerpt + `See More ▾` / `See Less ▴` toggle.
      - If `<= 140`: shows full rubric directly.

### C. Submitted but Wrong/Partial Short Answer Questions
- **Detection**: `!isUnsubmitted && q.getAwardedScore() < q.getMaxScore()`.
- **Display**:
  - Score badge: `Score: [awarded] / 5 pts`.
  - Qualitative AI feedback: `q.getAiFeedback()`.
  - Directly underneath:
    - Dedicated `💡 Explanation:` box displaying `q.getRubric()` with the **See More ▾ / See Less ▴** toggle.

---

## 3. UI Styling & CSS Design

Existing `.quiz-explanation-box` and `.quiz-see-more-btn` will be enhanced in `src/main/resources/com/example/study_buddy/styles.css`:

```css
.quiz-explanation-box {
    -fx-background-color: #f8fafc;
    -fx-background-radius: 8px;
    -fx-border-color: #e2e8f0;
    -fx-border-radius: 8px;
    -fx-border-width: 1.5px;
    -fx-padding: 12px 14px;
}

.quiz-see-more-btn {
    -fx-background-color: #eef2ff;
    -fx-text-fill: #4338ca;
    -fx-font-size: 11px;
    -fx-font-weight: bold;
    -fx-background-radius: 6px;
    -fx-padding: 3px 10px;
    -fx-cursor: hand;
}

.quiz-see-more-btn:hover {
    -fx-background-color: #e0e7ff;
    -fx-text-fill: #3730a3;
}
```

---

## 4. Technical Implementation Steps

### 1. In `HelloController.java`:
- Add a clean, reusable helper method:
  ```java
  private VBox createExpandableExplanationBox(String titleText, String fullContent, int threshold, Node parentCard)
  ```
  - Initializes `VBox explBox` with `.quiz-explanation-box`.
  - Adds title label `💡 Explanation:`.
  - Configures `explText` with `wrapText = true`, `setMinHeight(Region.USE_PREF_SIZE)`, and binds width to `card.widthProperty().subtract(60)`.
  - Implements word-boundary truncation, `See More ▾`, and `See Less ▴` toggle action.
- Update `revealQuizResults(QuizSession session)`:
  - **MCQ Questions**: Replace the current static explanation box with `createExpandableExplanationBox("💡 Explanation:", q.getExplanation(), 140, card)`.
  - **Short Answer Questions**:
    - Check if unsubmitted (`q.getStudentAnswer().trim().isEmpty()`).
    - If unsubmitted, show the notice: `"No response was submitted."`
    - Directly below, add `createExpandableExplanationBox("💡 Explanation:", q.getRubric(), 140, card)`.
    - If submitted with partial/zero score, show `q.getAiFeedback()` followed by `createExpandableExplanationBox("💡 Explanation:", q.getRubric(), 140, card)`.

### 2. In `GeminiApiService.java`:
- Tweak the prompt instruction slightly to ensure Gemini does not end explanations prematurely with literal `...`:
  - `"1. For MCQ: ... Provide a complete educational 'explanation' without trailing ellipsis.\n"`

---

## 5. Verification Plan

1. **Automated Unit Tests**:
   - Run `$env:JAVA_HOME = "C:\Users\User\.jdks\ms-21.0.12"; .\mvnw.cmd test` to ensure all 29 tests pass.
2. **Interactive UI Verification**:
   - **MCQ Explanation Test**:
     - Complete an MCQ question. In review mode, observe `💡 Explanation:`.
     - Verify long explanations wrap properly and display `See More ▾`.
     - Click `See More ▾`: verify full explanation expands completely without being clipped with `...`, and button flips to `See Less ▴`.
     - Click `See Less ▴`: verify it collapses cleanly.
   - **Unsubmitted Short Answer Test**:
     - Leave a short answer question blank and submit the quiz.
     - Verify the card shows `"No response was submitted."`.
     - Verify right after that line, the `💡 Explanation:` box appears with the model rubric.
     - Verify clicking `See More ▾` shows the full explanation.

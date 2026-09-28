# 🎬 Video Demonstration Speech Plan & Walkthrough Script
## Project: Revision Assistant
**Target Duration:** ~10 – 12 Minutes  
**Presenter:** Student / Developer  

---

## 📋 Checklist & Timing Breakdown

| Section | Topic | Teacher Requirement Covered | Target Time | Key Files / Screens to Show |
|:---:|:---|:---|:---:|:---|
| **0** | **Introduction** | Project identity, tech stack | 0:00 – 1:00 (1 min) | Application Welcome / Login Screen |
| **1** | **Version Control** | GitHub commits, regular usage, history | 1:00 – 2:15 (1.15 min) | GitHub Repo webpage & `git log --graph` |
| **2** | **Advanced OOP Concepts** | Interfaces, Abstract Classes, Polymorphism, Encapsulation | 2:15 – 3:45 (1.5 min) | `JobItem.java`, `AbstractNotebookJob.java`, `PageSaveJob.java`, `User.java` |
| **3** | **JavaFX UI Design** | Diverse layout panes, wide range of UI controls | 3:45 – 5:00 (1.25 min) | `login-view.fxml`, `hello-view.fxml`, Live App |
| **4** | **Layout Responsiveness** | Window width/height property bindings, constraints | 5:00 – 6:15 (1.15 min) | Live window resizing, `prefWidthProperty().bind()`, `ColumnConstraints` |
| **5** | **Concurrency & Thread Pools** | Multi-threading, Thread Pools, Producer-Consumer | 6:15 – 7:45 (1.5 min) | `NotebookJobQueue.java`, `Platform.runLater()`, Worker threads |
| **6** | **Database Integration** | SQLite schema, Foreign Keys, `ON DELETE CASCADE` | 7:45 – 9:00 (1.25 min) | `DatabaseHelper.java`, SQLite 14-table schema |
| **7** | **Data Manipulation (CRUD)** | Complete Create, Read, Update, Delete live demo | 9:00 – 10:15 (1.25 min) | Live CRUD on Tasks & Notebook Topics |
| **8** | **Networking & Data Parsing** | HTTP REST requests, JSON Jackson parsing | 10:15 – 11:30 (1.25 min) | `GeminiApiService.java`, AI Quiz live generation & parsing |
| **9** | **Conclusion** | Summary & sign-off | 11:30 – 12:00 (0.5 min) | Dashboard overview |

---

## 🛠️ Pre-Recording Setup Instructions

Before you hit "Record" on OBS / Screen Recorder:
1. **Resolution & Font**: Set display resolution to 1080p (1920×1080) and scale IDE font in IntelliJ (Settings $\rightarrow$ Appearance $\rightarrow$ Font size: 15–16 pt) for crisp readability.
2. **Browser Tab Ready**:
   - Tab 1: Your GitHub repository: `https://github.com/niimuu17/Revision-Assistant` (Commits page open).
3. **IDE Tabs Open in IntelliJ**:
   - `HelloApplication.java`
   - `JobItem.java` & `AbstractNotebookJob.java` (OOP)
   - `PageSaveJob.java` & `NotebookStatsJob.java` (Polymorphism)
   - `NotebookJobQueue.java` (Concurrency & Thread Pools)
   - `DatabaseHelper.java` (SQLite & Cascades)
   - `GeminiApiService.java` (Networking & JSON parsing)
   - `hello-view.fxml` & `login-view.fxml` (UI Panes & Controls)
4. **App Running**: Have the app launched or ready to run via `HelloApplication.java` or `mvn javafx:run`.
5. **Clean Data**: Ensure you have a test user account (e.g. `student@gmail.com` / `pass123`) with at least one sample notebook and task created so you don't start with a blank screen.

---

## 🗣️ Step-by-Step Walkthrough Script

---

### ⏱️ SECTION 0: Introduction & Project Overview (0:00 – 1:00)

#### 🖥️ What to Show on Screen:
- Open the running application at the **Login & Sign Up Screen**.
- Keep your camera/audio clear.

#### 🎙️ Speaking Script:
> *"Hello everyone and welcome to my project demonstration. Today, I am presenting **Revision Assistant** (formerly Study Buddy) — a comprehensive, high-performance desktop productivity and academic revision suite built entirely in **Java 21** and **JavaFX** with SQLite.*
>
> *Revision Assistant is designed to solve real-world student challenges: it features an intelligent weekly class timetable, interactive calendar tasks with countdown reminders, a multi-tabbed notebook workspace with screenshot paste support, and a dynamic AI-powered quiz generator that builds and grades revision questions directly from student notes.*
>
> *In this walkthrough, I will systematically cover all eight key technical criteria specified by the instructor, demonstrating our implementation both in the live running application and directly inside the source code."*

---

### ⏱️ SECTION 1: Version Control & GitHub Usage (1:00 – 2:15)

#### 🖥️ What to Show on Screen:
- Switch to your browser showing the GitHub repository:  
  **`https://github.com/niimuu17/Revision-Assistant`**
- Click on **Commits** history.
- (Optional) Open the IDE terminal and run: `git log --oneline -n 12`.

#### 🎙️ Speaking Script:
> *"Let's begin with **Requirement 1: Version Control**.*
>
> *Here is our GitHub repository, **Revision-Assistant**, located at `github.com/niimuu17/Revision-Assistant`. From the very inception of the project following the project idea submission, we have maintained a strict, disciplined Git workflow.*
>
> *As you can see in our commit log, we used feature branching and regular, descriptive semantic commits:*
> - *We began with our initial authentication and class routine infrastructure,*
> - *Followed by routine slot overlap algorithms and sidebar layouts,*
> - *Then the notebook workspace and topic attachments,*
> - *Followed by our calendar task timers and SQLite database cascades,*
> - *And recent commits implementing multi-threaded producer-consumer queues and our full rebranding to Revision Assistant.*
>
> *Every feature was developed on dedicated branches — such as `timers`, `ui_color`, and `revision_assistant` — before being merged cleanly into `main`. This disciplined version control ensures complete traceability, regression prevention, and reproducible builds."*

---

### ⏱️ SECTION 2: Advanced OOP Concepts (2:15 – 3:45)

#### 🖥️ What to Show on Screen:
- Switch to IntelliJ IDEA.
- Open and show:
  1. `JobItem.java` (Interface)
  2. `AbstractNotebookJob.java` (Abstract Class)
  3. `PageSaveJob.java` (Concrete Subclass)
  4. `User.java` (Encapsulation)

#### 🎙️ Speaking Script:
> *"Next is **Requirement 2: Advanced Object-Oriented Programming (OOP) Techniques**.*
>
> *Rather than writing monolithic code, the project is structured around clean OOP principles: Abstraction, Interfaces, Polymorphism, Inheritance, and Encapsulation.*
>
> *1. **Interface Contract**: Let's look at `JobItem.java`. This interface defines our execution contract for background asynchronous jobs. It enforces four key methods: `execute()`, `getJobName()`, `getProducerThreadName()`, and `getQueuedTimestamp()`.*
>
> *2. **Abstract Base Class**: Here in `AbstractNotebookJob.java`, we implement `JobItem`. This abstract class encapsulates common job state — such as the notebook ID, job creation timestamp, and producer thread name. It provides concrete helper methods to log queue wait latency and declares an abstract template method: `protected abstract void processJob() throws Exception`.*
>
> *3. **Polymorphism**: We implement multiple polymorphic subclasses extending `AbstractNotebookJob`:*
> - *`PageSaveJob.java`: Overrides `processJob()` to asynchronously persist notebook page blocks, sanitize text, and recalculate word count without blocking the JavaFX UI thread.*
> - *`NotebookStatsJob.java`: Overrides `processJob()` to calculate real-time analytics across all chapters, topics, and pages.*
>
> *4. **Encapsulation**: Across our model layer — including `User.java`, `Notebook.java`, `Course.java`, and `QuizQuestion.java` — all attributes are private, exposed only through strict getters, setters, and constructors, ensuring zero data corruption."*

---

### ⏱️ SECTION 3: JavaFX UI Design & Layout Panes (3:45 – 5:00)

#### 🖥️ What to Show on Screen:
- Switch to the live application.
- Show the Login screen, then log in and navigate across:
  - Homepage / Routine Dashboard
  - Calendar View
  - Notebook Workspace
  - AI Quiz Portal
- Briefly show `hello-view.fxml` and `login-view.fxml` in the IDE.

#### 🎙️ Speaking Script:
> *"Now for **Requirement 3: JavaFX UI Design and Control Diversity**.*
>
> *Our user interface was engineered to feel modern, sleek, and native using a rich variety of JavaFX layout panes and UI controls:*
>
> *1. **Layout Panes Showcase**:*
> - *`StackPane`: Used as the root container on the Login portal for centering cards, and in the main view for switching between views (Routine, Calendar, Notebook, and Quiz).*
> - *`BorderPane`: Structures our Quiz System with a dedicated header, dynamic scrollable question center, and sticky action footer.*
> - *`GridPane`: Powers our 7-day weekly class timetable and monthly Calendar grid, ensuring uniform tabular alignment.*
> - *`VBox` and `HBox`: Form our sidebars, card containers, and horizontal action toolbars with precise spacing and padding.*
> - *`FlowPane` & `ScrollPane`: Fluidly arranges notebook topic tags and renders long study notes with butter-smooth scrolling.*
>
> *2. **UI Controls Variety**:*
> *We utilize an extensive range of native JavaFX controls: `PasswordField` for secure credential input, `TextField`, `DatePicker` in our task modals, `ComboBox` with styled cell factories, `ProgressBar` and `ProgressIndicator` for loading states, `ContextMenu` and `MenuItem` for right-click contextual actions, interactive `Button` states, and custom animated status pills."*

---

### ⏱️ SECTION 4: Layout Responsiveness & Window Constraints (5:00 – 6:15)

#### 🖥️ What to Show on Screen:
- In the live application:
  - Grab the window corner and resize it freely (make it smaller, wider, taller).
  - Click the **Maximize** button and then **Restore**.
  - Show how elements reflow without overlapping or clipping.
- In IntelliJ: Open `HelloController.java` to show:
  - Lines where `prefWidthProperty().bind(...)` is implemented.
  - ColumnConstraints percentage widths (`100.0 / 7`).

#### 🎙️ Speaking Script:
> *"Moving to **Requirement 4: Layout Responsiveness**.*
>
> *A desktop application must remain usable on screens of any size, from small laptop displays to 4K monitors. We achieved full dynamic responsiveness through property bindings and flexible layout constraints:*
>
> *1. **Live Demonstration**: Watch as I resize the window horizontally and vertically. Notice that:*
> - *The weekly routine cards expand proportionally without text clipping.*
> - *The Calendar columns automatically resize so each of the 7 days receives an exact equal width.*
> - *Sidebars collapse gracefully using toggle buttons, reallocating full width to the active workspace.*
>
> *2. **Under the Hood**:*
> - *In `HelloController.java`, we utilize JavaFX dynamic property bindings — for example:  
>   `promptLabel.prefWidthProperty().bind(card.widthProperty().subtract(40));`  
>   and `fbText.prefWidthProperty().bind(card.widthProperty().subtract(60));`*
> - *In our timetable and calendar grids, we configure `ColumnConstraints` using percentage widths (`setPercentWidth(100.0 / 7.0)`).*
> - *All primary layout containers use `HBox.setHgrow(..., Priority.ALWAYS)` and `VBox.setVgrow(..., Priority.ALWAYS)`, guaranteeing seamless scaling upon maximizing and restoring the window."*

---

### ⏱️ SECTION 5: Concurrency & Thread Pools (6:15 – 7:45)

#### 🖥️ What to Show on Screen:
- In IntelliJ: Open `NotebookJobQueue.java`.
- Highlight:
  - `BlockingQueue<JobItem> jobQueue`
  - `ExecutorService threadPool` with named worker threads (`NotebookWorker-1`, `NotebookWorker-2`)
  - Consumer loop: `JobItem job = jobQueue.take();`
  - `Platform.runLater(...)`
- In `HelloApplication.java`: Show graceful shutdown on line ~50.

#### 🎙️ Speaking Script:
> *"Now let's examine **Requirement 5: Concurrency, Multi-threading, and Thread Pools**.*
>
> *In JavaFX, performing file I/O, heavy calculations, or network requests on the JavaFX Application Thread will freeze the GUI, causing noticeable lag and 'Not Responding' errors. To solve this, we implemented a robust **Producer-Consumer multi-threaded architecture** in `NotebookJobQueue.java`:*
>
> *1. **Bounded Buffer**: We use a thread-safe `LinkedBlockingQueue<JobItem>` with a fixed capacity of 100 jobs to prevent memory exhaustion.*
>
> *2. **Managed Thread Pool**: We initialize a dedicated thread pool using `Executors.newFixedThreadPool(2, new ThreadFactory() ...)` with custom named daemon threads: `NotebookWorker-1` and `NotebookWorker-2`.*
>
> *3. **Non-Blocking Producer**: When a user types a note or uploads an attachment, the UI thread simply calls `jobQueue.offer(job)`. This completes in microseconds without any frame drops.*
>
> *4. **Background Consumers**: Worker threads continuously dequeue jobs using `jobQueue.take()`, execute database updates and analytics off the main thread, and safely post results back to the GUI using `Platform.runLater()`.*
>
> *5. **Thread Safety & Lifecycle**: In `HelloApplication.java`, we register a graceful shutdown hook on the stage close event to terminate the thread pool cleanly with `awaitTermination()`. We also wrote comprehensive unit tests in `NotebookJobQueueTest.java` verifying thread safety under concurrent load."*

---

### ⏱️ SECTION 6: Database Integration & Relationships (7:45 – 9:00)

#### 🖥️ What to Show on Screen:
- In IntelliJ: Open `DatabaseHelper.java`.
- Scroll to `initDatabase()`.
- Highlight:
  - `PRAGMA foreign_keys = ON;`
  - Tables: `users`, `notebooks`, `topics`, `pages`, `calendar_tasks`, `routine_slots`, `routine_activities`.
  - Show foreign key constraints with `ON DELETE CASCADE`.
  - Show a parameterized `PreparedStatement`.

#### 🎙️ Speaking Script:
> *"Next is **Requirement 6: SQLite Database Integration and Relational Architecture**.*
>
> *Our data persistence layer is handled by SQLite via the JDBC driver in `DatabaseHelper.java`. The database file is `revision_assistant.db`.*
>
> *1. **Enforcing Referential Integrity**: SQLite by default has foreign key constraints disabled. In `getConnection()`, we explicitly execute:  
> `stmt.execute("PRAGMA foreign_keys = ON;");` on every connection to enforce relational integrity.*
>
> *2. **Relational Schema & Foreign Keys**: Our schema consists of 14 interconnected tables with clean parent-child relationships:*
> - *The `users` table is the root entity.*
> - *`notebooks` references `users(id)` with `ON DELETE CASCADE`.*
> - *`topics` references `notebooks(id)` with `ON DELETE CASCADE`.*
> - *`pages` references `topics(id)` with `ON DELETE CASCADE`.*
> - *`calendar_tasks`, `routine_slots`, and `courses` all link directly to `users(id)`.*
> - *Because of `ON DELETE CASCADE`, deleting a user or a notebook automatically cleans up all associated topics, pages, and attachments, guaranteeing zero orphan rows.*
>
> *3. **SQL Injection Security**: Every query across all 1,300 lines of `DatabaseHelper.java` strictly uses parameterized `PreparedStatement` with `?` placeholders, completely eliminating SQL injection vulnerabilities."*

---

### ⏱️ SECTION 7: Data Manipulation (Full CRUD Demonstration) (9:00 – 10:15)

#### 🖥️ What to Show on Screen:
- Switch to the live application.
- Perform a complete live CRUD workflow (e.g., using **Calendar Tasks** or **Notebook Topics**):
  1. **Create**: Click *Add Task*, type title `"Math Exam Revision"`, select date & time, click *Save*.
  2. **Read**: Show the task appearing in the Calendar date cell and in the right-hand task sidebar with its countdown timer.
  3. **Update**: Click on the task to open the *Edit Task Dialog*, change title to `"Advanced Calculus Exam Revision"`, change priority/color, click *Update*, and show the updated view.
  4. **Delete**: Right-click the task $\rightarrow$ select *Remove Task* (or click *Delete* in the dialog), confirm deletion, and show it cleanly disappearing from the UI and database.

#### 🎙️ Speaking Script:
> *"Now let's demonstrate **Requirement 7: Complete CRUD Operations (Create, Read, Update, Delete)** live in the running application.*
>
> *I will demonstrate this using our **Calendar Task Management** system:*
>
> *1. **CREATE**: I click 'Add Task'. I enter the title 'Math Exam Revision', choose the subject 'Mathematics', set tomorrow's date with a reminder time, and click 'Save'. The task is inserted into our SQLite `calendar_tasks` table.*
>
> *2. **READ**: Immediately, our dashboard reads the updated task list: it renders a task pill directly inside the calendar grid day cell, and populates our collapsible task sidebar showing the dynamic countdown timer: 'Due in 23 hours'.*
>
> *3. **UPDATE**: Let's edit this task. I click the task to open our Edit Dialog. I update the title to 'Advanced Calculus Exam Revision' and change the type to 'Exam'. I click 'Update'. The database record is modified via `UPDATE calendar_tasks SET ...`, and the UI reflects the new title instantly.*
>
> *4. **DELETE**: Finally, I right-click the task card and click 'Remove Task'. A confirmation dialog appears. Once confirmed, `DELETE FROM calendar_tasks WHERE id = ?` executes, and the card is removed smoothly from both the calendar grid and the sidebar.*
>
> *We have identical full CRUD workflows implemented across our Notebooks, Pages, and Weekly Routine Slots."*

---

### ⏱️ SECTION 8: Networking & Data Parsing (10:15 – 11:30)

#### 🖥️ What to Show on Screen:
- In IntelliJ: Briefly show `GeminiApiService.java`:
  - `java.net.http.HttpClient`
  - `HttpRequest.newBuilder().POST(...)`
  - Jackson `ObjectMapper` parsing JSON response (`rootNode.path("candidates")...`).
- In the Live Application: Switch to the **AI Quiz Section**:
  - Enter topic: `"Operating Systems"`
  - Select 3 questions (MCQ + Short Answer)
  - Click **Generate Quiz**
  - Show the progress indicator while fetching from the REST API.
  - Answer the questions live, click **Submit Quiz**, and show the parsed score pills and explanations!

#### 🎙️ Speaking Script:
> *"Finally, we arrive at **Requirement 8: Networking & JSON Data Parsing**.*
>
> *Revision Assistant integrates with Google's Gemini REST API to dynamically generate personalized quizzes and evaluate student answers.*
>
> *1. **HTTP Client & Requests**: In `GeminiApiService.java`, we utilize Java 21's native `java.net.http.HttpClient` configured with `HttpClient.Redirect.NORMAL` and a 45-second timeout. We construct asynchronous HTTPS POST requests using `HttpRequest.newBuilder()` targeting the Gemini endpoint.*
>
> *2. **JSON Serialization & Parsing**: We use **Jackson Databind** (`ObjectMapper`). We construct strict JSON request payloads containing the user's study topics or uploaded attachments. When the response arrives, we parse the raw JSON string using `objectMapper.readTree(responseBody)`.*
>
> *3. **Structured Extraction**: We extract question prompts, multiple-choice options, correct option indices, and rubric explanations directly from the JSON tree, converting them into typed `QuizQuestion` Java objects.*
>
> *4. **Live Demonstration**: Let's see this in action. I'll enter the topic 'Operating Systems', select 'Easy', and click 'Generate Quiz'.*
> - *Notice the UI remains completely responsive with an animated loading spinner because the network call executes asynchronously on a background thread.*
> - *The response JSON has been fetched, parsed, and rendered into interactive cards!*
> - *Let's answer the questions and click 'Submit Quiz'. Notice that Short Answers are sent back to the evaluation endpoint, which returns an evaluated numerical score, feedback, and expandable explanation box — all parsed cleanly from JSON."*

---

### ⏱️ SECTION 9: Conclusion & Wrap-Up (11:30 – 12:00)

#### 🖥️ What to Show on Screen:
- Return to the **Homepage Dashboard**.
- Show the clean Matcha Earthy UI theme.

#### 🎙️ Speaking Script:
> *"To conclude, **Revision Assistant** successfully implements all eight core requirements:*
> - *Disciplined Git version control from project kickoff,*
> - *Advanced OOP design with interfaces, abstract classes, and polymorphism,*
> - *A rich JavaFX UI built with diverse layout panes and controls,*
> - *Full layout responsiveness via property bindings and proportional constraints,*
> - *Safe multi-threaded concurrency using thread pools and producer-consumer queues,*
> - *A secure SQLite database with relational foreign key cascading,*
> - *Complete interactive CRUD operations,*
> - *And asynchronous HTTP networking with Jackson JSON data parsing.*
>
> *Thank you very much for your time and guidance throughout this project!"*

---

## 💡 Quick Tips for High Marks

- **Speak with confidence and pace**: Don't rush. Pause slightly between sections so the instructor can digest each requirement.
- **Always show code AND live app**: For every requirement, show where it lives in the code, and then show it running in the app.
- **Don't show console errors**: Clean your build beforehand (`mvn clean test-compile`) so your terminal is completely green.

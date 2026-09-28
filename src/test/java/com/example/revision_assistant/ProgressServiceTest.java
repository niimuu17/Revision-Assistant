package com.example.revision_assistant;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests verifying Academic Progress Tracker:
 * - Course CRUD
 * - Syllabus Chapters & Topics persistence
 * - Interactive Topic completion toggling
 * - Academic Marks CRUD & percentage calculations
 * - Term Exam date persistence
 * - File text reading
 */
public class ProgressServiceTest {

    private int testUserId;
    private int testCourseId;

    @BeforeEach
    public void setup() {
        DatabaseHelper.initDatabase();
        long ts = System.currentTimeMillis();
        String testEmail = "prog" + ts + "@gmail.com";
        String testUsername = "prog" + ts;
        DatabaseHelper.registerUser(testEmail, testUsername, "pass123");
        User user = DatabaseHelper.authenticateUser(testEmail, "pass123");
        assertNotNull(user, "User should be registered and authenticated");
        this.testUserId = user.getId();

        Course course = DatabaseHelper.createCourse(testUserId, "CSE 2100", "Object-Oriented Programming");
        assertNotNull(course, "Test course should be created");
        this.testCourseId = course.getId();
    }

    @AfterEach
    public void tearDown() {
        if (testCourseId > 0) {
            DatabaseHelper.deleteCourse(testCourseId);
        }
    }

    @Test
    public void testCourseCreationAndRetrieval() {
        List<Course> courses = DatabaseHelper.getCourses(testUserId);
        assertFalse(courses.isEmpty(), "User courses should contain test course");

        boolean found = courses.stream()
                .anyMatch(c -> "CSE 2100".equals(c.getCourseCode()) && "Object-Oriented Programming".equals(c.getCourseTitle()));
        assertTrue(found, "Created course should match code and title");
    }

    @Test
    public void testCourseUpdate() {
        boolean updated = DatabaseHelper.updateCourse(testCourseId, "CSE 2101", "Advanced Object-Oriented Programming");
        assertTrue(updated, "Course update should succeed");

        List<Course> courses = DatabaseHelper.getCourses(testUserId);
        boolean foundUpdated = courses.stream()
                .anyMatch(c -> c.getId() == testCourseId && "CSE 2101".equals(c.getCourseCode()) && "Advanced Object-Oriented Programming".equals(c.getCourseTitle()));
        assertTrue(foundUpdated, "Course should reflect updated code and title");
    }

    @Test
    public void testSyllabusChaptersAndTopicsPersistence() {
        List<SyllabusChapter> chapters = new ArrayList<>();

        SyllabusChapter ch1 = new SyllabusChapter(1, "Introduction to OOP");
        ch1.getTopics().add(new SyllabusTopic("Classes & Objects"));
        ch1.getTopics().add(new SyllabusTopic("Encapsulation"));
        chapters.add(ch1);

        SyllabusChapter ch2 = new SyllabusChapter(2, "Inheritance & Polymorphism");
        ch2.getTopics().add(new SyllabusTopic("Subclasses & Super"));
        ch2.getTopics().add(new SyllabusTopic("Interfaces"));
        ch2.getTopics().add(new SyllabusTopic("Abstract Classes"));
        chapters.add(ch2);

        DatabaseHelper.saveSyllabusChapters(testCourseId, chapters);

        List<SyllabusChapter> retrieved = DatabaseHelper.getSyllabusChapters(testCourseId);
        assertEquals(2, retrieved.size(), "Should have 2 chapters");
        assertEquals("Introduction to OOP", retrieved.get(0).getTitle());
        assertEquals(2, retrieved.get(0).getTopics().size());
        assertEquals("Classes & Objects", retrieved.get(0).getTopics().get(0).getTitle());

        assertEquals(3, retrieved.get(1).getTopics().size());
    }

    @Test
    public void testTopicCompletionToggle() {
        List<SyllabusChapter> chapters = new ArrayList<>();
        SyllabusChapter ch1 = new SyllabusChapter(1, "Basics");
        ch1.getTopics().add(new SyllabusTopic("Topic A"));
        chapters.add(ch1);

        DatabaseHelper.saveSyllabusChapters(testCourseId, chapters);

        List<SyllabusChapter> retrieved = DatabaseHelper.getSyllabusChapters(testCourseId);
        SyllabusTopic topic = retrieved.get(0).getTopics().get(0);
        assertFalse(topic.isCompleted(), "Topic should initially be uncompleted");

        // Mark completed
        DatabaseHelper.setTopicCompleted(topic.getId(), true);

        List<SyllabusChapter> updated = DatabaseHelper.getSyllabusChapters(testCourseId);
        assertTrue(updated.get(0).getTopics().get(0).isCompleted(), "Topic should now be completed");
        assertEquals(1, updated.get(0).getCompletedTopicsCount(), "Completed count should be 1");

        // Toggle back to uncompleted
        DatabaseHelper.setTopicCompleted(topic.getId(), false);
        List<SyllabusChapter> uncompleted = DatabaseHelper.getSyllabusChapters(testCourseId);
        assertFalse(uncompleted.get(0).getTopics().get(0).isCompleted(), "Topic should now be uncompleted");
    }

    @Test
    public void testAcademicMarksCRUDAndCalculations() {
        AcademicMark m1 = DatabaseHelper.addAcademicMark(testCourseId, "CT", "CT 1", 18.0, 20.0, "2026-09-20");
        assertNotNull(m1, "Mark 1 should be inserted");
        assertEquals(90.0, m1.getPercentage(), 0.001);

        AcademicMark m2 = DatabaseHelper.addAcademicMark(testCourseId, "Lab Test", "Lab Test 1", 28.5, 30.0, "2026-09-22");
        assertNotNull(m2, "Mark 2 should be inserted");
        assertEquals(95.0, m2.getPercentage(), 0.001);

        List<AcademicMark> marks = DatabaseHelper.getAcademicMarks(testCourseId);
        assertEquals(2, marks.size(), "Course should have 2 marks recorded");

        // Delete mark 1
        boolean deleted = DatabaseHelper.deleteAcademicMark(m1.getId());
        assertTrue(deleted, "Mark 1 should be deleted");

        List<AcademicMark> remaining = DatabaseHelper.getAcademicMarks(testCourseId);
        assertEquals(1, remaining.size(), "Course should now have 1 mark");
        assertEquals("Lab Test 1", remaining.get(0).getAssessmentName());
    }

    @Test
    public void testTermExamDatePersistence() {
        DatabaseHelper.setTermExamDate(testCourseId, "2026-11-15");
        String date = DatabaseHelper.getTermExamDate(testCourseId);
        assertEquals("2026-11-15", date, "Term exam date should match");

        // Update exam date
        DatabaseHelper.setTermExamDate(testCourseId, "2026-11-20");
        String updatedDate = DatabaseHelper.getTermExamDate(testCourseId);
        assertEquals("2026-11-20", updatedDate, "Updated term exam date should match");
    }

    @Test
    public void testFileReadingUtility() throws IOException {
        File tempFile = Files.createTempFile("syllabus_test", ".txt").toFile();
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Chapter 1: Principles of OOP\n- Classes\n- Objects\n- Inheritance");
        }

        String content = QuizSourceHelper.readFileContent(tempFile);
        assertNotNull(content);
        assertTrue(content.contains("Principles of OOP"));
        assertTrue(content.contains("Inheritance"));
    }

    @Test
    public void testDeleteSyllabusClearing() {
        List<SyllabusChapter> chapters = new ArrayList<>();
        SyllabusChapter ch1 = new SyllabusChapter(1, "To Be Deleted");
        ch1.getTopics().add(new SyllabusTopic("Temp Topic 1"));
        ch1.getTopics().add(new SyllabusTopic("Temp Topic 2"));
        chapters.add(ch1);

        DatabaseHelper.saveSyllabusChapters(testCourseId, chapters);
        List<SyllabusChapter> beforeDelete = DatabaseHelper.getSyllabusChapters(testCourseId);
        assertEquals(1, beforeDelete.size(), "Should have 1 chapter before deletion");
        assertEquals(2, beforeDelete.get(0).getTopics().size(), "Should have 2 topics before deletion");

        // Execute Delete Syllabus action (clear chapters)
        DatabaseHelper.saveSyllabusChapters(testCourseId, new ArrayList<>());

        List<SyllabusChapter> afterDelete = DatabaseHelper.getSyllabusChapters(testCourseId);
        assertTrue(afterDelete.isEmpty(), "Syllabus chapters should be completely cleared after deletion");
    }

    @Test
    public void testImageFileCheckAndMimeTypes() {
        File pngFile = new File("syllabus_diagram.PNG");
        File jpgFile = new File("photo.jpeg");
        File pdfFile = new File("syllabus.pdf");
        File docxFile = new File("outline.docx");

        assertTrue(QuizSourceHelper.isImageFile(pngFile), "PNG should be recognized as image");
        assertTrue(QuizSourceHelper.isImageFile(jpgFile), "JPEG should be recognized as image");
        assertFalse(QuizSourceHelper.isImageFile(pdfFile), "PDF should not be recognized as image");
        assertFalse(QuizSourceHelper.isImageFile(docxFile), "DOCX should not be recognized as image");

        assertEquals("image/png", QuizSourceHelper.getImageMimeType(pngFile));
        assertEquals("image/jpeg", QuizSourceHelper.getImageMimeType(jpgFile));
    }

    @Test
    public void testDocxZipExtraction() throws IOException {
        File tempDocx = Files.createTempFile("test_syllabus", ".docx").toFile();
        tempDocx.deleteOnExit();

        try (java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(new java.io.FileOutputStream(tempDocx))) {
            java.util.zip.ZipEntry entry = new java.util.zip.ZipEntry("word/document.xml");
            zos.putNextEntry(entry);
            String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><w:document><w:body><w:p><w:t>Chapter 1: Advanced Java</w:t></w:p></w:body></w:document>";
            zos.write(xmlContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            zos.closeEntry();
        }

        String extracted = QuizSourceHelper.readFileContent(tempDocx);
        assertNotNull(extracted);
        assertTrue(extracted.contains("Chapter 1: Advanced Java"), "Extracted DOCX text should contain Chapter 1: Advanced Java");
    }

    @Test
    public void testSyllabusLocalParser() {
        String sampleSyllabus = "Course Syllabus: CS101\n" +
                "Instructor: Dr. Smith\n" +
                "Chapter 1: Object Oriented Programming\n" +
                "- Classes and Objects\n" +
                "- Encapsulation & Data Hiding\n" +
                "- Polymorphic Behavior\n" +
                "Chapter 2: Data Structures\n" +
                "* Linked Lists\n" +
                "* Binary Search Trees\n" +
                "* Hash Tables";

        List<SyllabusChapter> chapters = SyllabusLocalParser.parseText(sampleSyllabus);
        assertNotNull(chapters);
        assertEquals(2, chapters.size(), "Should detect 2 chapters");
        assertEquals("Object Oriented Programming", chapters.get(0).getTitle());
        assertEquals(3, chapters.get(0).getTopics().size(), "Chapter 1 should have 3 topics");
        assertEquals("Classes and Objects", chapters.get(0).getTopics().get(0).getTitle());

        assertEquals("Data Structures", chapters.get(1).getTitle());
        assertEquals(3, chapters.get(1).getTopics().size(), "Chapter 2 should have 3 topics");
    }

    @Test
    public void testSyllabusLocalParserUnstructuredFallback() {
        String unstructured = "Introduction to Database Systems\n" +
                "Relational Algebra\n" +
                "SQL Queries\n" +
                "Normalization and Normal Forms\n" +
                "Indexing and B-Trees\n" +
                "Transaction Processing";

        List<SyllabusChapter> chapters = SyllabusLocalParser.parseText(unstructured);
        assertNotNull(chapters);
        assertFalse(chapters.isEmpty(), "Should partition unstructured text into chapters");
        int totalTopics = chapters.stream().mapToInt(c -> c.getTopics().size()).sum();
        assertTrue(totalTopics >= 4, "Should extract topics from unstructured lines");
    }

    @Test
    public void testGeminiResilientJsonParsing() throws IOException {
        GeminiApiService apiService = new GeminiApiService();

        // 1. Test top-level array with topic objects
        String jsonWithArrayAndObjects = "[\n" +
                "  {\n" +
                "    \"chapter_number\": 1,\n" +
                "    \"title\": \"Operating System Concepts\",\n" +
                "    \"subtopics\": [\n" +
                "      {\"title\": \"Process Scheduling\"},\n" +
                "      {\"name\": \"Virtual Memory Management\"}\n" +
                "    ]\n" +
                "  }\n" +
                "]";

        List<SyllabusChapter> chapters1 = apiService.parseSyllabusChaptersFromJson(jsonWithArrayAndObjects);
        assertNotNull(chapters1);
        assertEquals(1, chapters1.size());
        assertEquals("Operating System Concepts", chapters1.get(0).getTitle());
        assertEquals(2, chapters1.get(0).getTopics().size());
        assertEquals("Process Scheduling", chapters1.get(0).getTopics().get(0).getTitle());
        assertEquals("Virtual Memory Management", chapters1.get(0).getTopics().get(1).getTitle());

        // 2. Test alternative modules key
        String jsonModules = "{\n" +
                "  \"modules\": [\n" +
                "    {\n" +
                "      \"number\": 1,\n" +
                "      \"name\": \"Networking Fundamentals\",\n" +
                "      \"topics\": [\"OSI Model\", \"TCP/IP Protocol\"]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        List<SyllabusChapter> chapters2 = apiService.parseSyllabusChaptersFromJson(jsonModules);
        assertNotNull(chapters2);
        assertEquals(1, chapters2.size());
        assertEquals("Networking Fundamentals", chapters2.get(0).getTitle());
        assertEquals(2, chapters2.get(0).getTopics().size());
    }
}


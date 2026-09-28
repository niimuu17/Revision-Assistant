package com.example.revision_assistant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service to communicate with Google Gemini API (gemini-1.5-flash)
 * using Java 21's native HttpClient and Jackson for JSON processing.
 *
 * Implements the 2-Call Model:
 * 1. Call #1: generateQuiz - creates structured MCQ & Short Answer questions.
 * 2. Call #2: gradeShortAnswers - grades student's typed responses with feedback.
 */
public class GeminiApiService {

    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";
    private static final String[] CANDIDATE_MODELS = {
            "gemini-1.5-flash",
            "gemini-2.0-flash",
            "gemini-1.5-flash-latest",
            "gemini-1.5-pro",
            "gemini-flash-latest"
    };

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GeminiApiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Call #1: Generates an AI Quiz based on user topic, source material, question count, and difficulty.
     * Runs asynchronously and returns a CompletableFuture<QuizSession>.
     */
    public CompletableFuture<QuizSession> generateQuizAsync(
            String topicPrompt,
            String sourceText,
            int numQuestions,
            String difficulty,
            String questionTypeMode) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return generateQuiz(topicPrompt, sourceText, numQuestions, difficulty, questionTypeMode);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }

    /**
     * Synchronous implementation of Call #1 (Quiz Generation).
     */
    public QuizSession generateQuiz(
            String topicPrompt,
            String sourceText,
            int numQuestions,
            String difficulty,
            String questionTypeMode) throws IOException, InterruptedException {

        String apiKey = ApiKeyManager.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("Google Gemini API Key is missing. Please configure it in API Settings.");
        }

        // Determine breakdown of questions
        int mcqCount;
        int shortAnswerCount;
        if ("MCQ Only".equalsIgnoreCase(questionTypeMode)) {
            mcqCount = numQuestions;
            shortAnswerCount = 0;
        } else if ("Short Answer Only".equalsIgnoreCase(questionTypeMode)) {
            mcqCount = 0;
            shortAnswerCount = numQuestions;
        } else {
            // Both / Mixed mode
            shortAnswerCount = Math.max(1, numQuestions / 3);
            mcqCount = numQuestions - shortAnswerCount;
        }

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("You are an expert academic tutor. Generate a high-quality study quiz.\n");
        promptBuilder.append("Difficulty Level: ").append(difficulty).append("\n");
        promptBuilder.append("Total Questions: ").append(numQuestions).append(" (");
        promptBuilder.append(mcqCount).append(" Multiple Choice, ").append(shortAnswerCount).append(" Short Answer).\n\n");

        if (topicPrompt != null && !topicPrompt.trim().isEmpty()) {
            promptBuilder.append("Topic / Student Prompt: ").append(topicPrompt.trim()).append("\n\n");
        }

        if (sourceText != null && !sourceText.trim().isEmpty()) {
            // Limit source text length to avoid token overflow
            String trimmedSource = sourceText.length() > 15000 ? sourceText.substring(0, 15000) + "..." : sourceText;
            promptBuilder.append("Reference Source Material:\n\"\"\"\n").append(trimmedSource).append("\n\"\"\"\n\n");
        }

        promptBuilder.append("REQUIREMENTS:\n");
        promptBuilder.append("1. For MCQ: Exactly 4 distinct options. Specify 'correctIndex' as an integer 0, 1, 2, or 3. Provide a complete educational 'explanation' without trailing ellipsis.\n");
        promptBuilder.append("2. For SHORT_ANSWER: Provide a clear rubric detailing key points expected in a complete answer, and set maxScore to 5.\n");
        promptBuilder.append("3. Return strictly valid JSON with this exact structure:\n");
        promptBuilder.append("{\n");
        promptBuilder.append("  \"title\": \"Quiz Title\",\n");
        promptBuilder.append("  \"topic\": \"Summary Topic\",\n");
        promptBuilder.append("  \"difficulty\": \"").append(difficulty).append("\",\n");
        promptBuilder.append("  \"questions\": [\n");
        promptBuilder.append("    {\n");
        promptBuilder.append("      \"type\": \"MCQ\",\n");
        promptBuilder.append("      \"question\": \"Question text?\",\n");
        promptBuilder.append("      \"options\": [\"Option A\", \"Option B\", \"Option C\", \"Option D\"],\n");
        promptBuilder.append("      \"correctIndex\": 0,\n");
        promptBuilder.append("      \"explanation\": \"Why option A is correct...\"\n");
        promptBuilder.append("    },\n");
        promptBuilder.append("    {\n");
        promptBuilder.append("      \"type\": \"SHORT_ANSWER\",\n");
        promptBuilder.append("      \"question\": \"Question text?\",\n");
        promptBuilder.append("      \"rubric\": \"Key concepts and points expected in the answer...\",\n");
        promptBuilder.append("      \"maxScore\": 5\n");
        promptBuilder.append("    }\n");
        promptBuilder.append("  ]\n");
        promptBuilder.append("}\n");

        String requestJson = buildGeminiRequestBody(promptBuilder.toString());
        String responseBody = sendGeminiRequest(apiKey, requestJson);

        String jsonText = extractContentText(responseBody);
        return parseQuizSession(jsonText, topicPrompt, difficulty);
    }

    /**
     * Call #1b: Generates an AI Quiz from an image (notes, diagram, slide, or textbook page).
     */
    public CompletableFuture<QuizSession> generateQuizFromImageAsync(
            String topicPrompt,
            File imageFile,
            int numQuestions,
            String difficulty,
            String questionTypeMode) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return generateQuizFromImage(topicPrompt, imageFile, numQuestions, difficulty, questionTypeMode);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }

    public QuizSession generateQuizFromImage(
            String topicPrompt,
            File imageFile,
            int numQuestions,
            String difficulty,
            String questionTypeMode) throws IOException, InterruptedException {

        String apiKey = ApiKeyManager.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("Google Gemini API Key is missing. Please configure it in API Settings.");
        }

        int mcqCount;
        int shortAnswerCount;
        if ("MCQ Only".equalsIgnoreCase(questionTypeMode)) {
            mcqCount = numQuestions;
            shortAnswerCount = 0;
        } else if ("Short Answer Only".equalsIgnoreCase(questionTypeMode)) {
            mcqCount = 0;
            shortAnswerCount = numQuestions;
        } else {
            shortAnswerCount = Math.max(1, numQuestions / 3);
            mcqCount = numQuestions - shortAnswerCount;
        }

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("You are an expert academic tutor. Analyze the provided study image (notes, diagram, slide, or textbook page) and generate a high-quality study quiz based on its contents.\n");
        promptBuilder.append("Difficulty Level: ").append(difficulty).append("\n");
        promptBuilder.append("Total Questions: ").append(numQuestions).append(" (");
        promptBuilder.append(mcqCount).append(" Multiple Choice, ").append(shortAnswerCount).append(" Short Answer).\n\n");

        if (topicPrompt != null && !topicPrompt.trim().isEmpty()) {
            promptBuilder.append("Additional Focus / Student Prompt: ").append(topicPrompt.trim()).append("\n\n");
        }

        promptBuilder.append("REQUIREMENTS:\n");
        promptBuilder.append("1. For MCQ: Exactly 4 distinct options. Specify 'correctIndex' as an integer 0, 1, 2, or 3. Provide a complete educational 'explanation' without trailing ellipsis.\n");
        promptBuilder.append("2. For SHORT_ANSWER: Provide a clear rubric detailing key points expected in a complete answer, and set maxScore to 5.\n");
        promptBuilder.append("3. Return strictly valid JSON with this exact structure:\n");
        promptBuilder.append("{\n");
        promptBuilder.append("  \"title\": \"Quiz Title\",\n");
        promptBuilder.append("  \"topic\": \"Summary Topic\",\n");
        promptBuilder.append("  \"difficulty\": \"").append(difficulty).append("\",\n");
        promptBuilder.append("  \"questions\": [\n");
        promptBuilder.append("    {\n");
        promptBuilder.append("      \"type\": \"MCQ\",\n");
        promptBuilder.append("      \"question\": \"Question text?\",\n");
        promptBuilder.append("      \"options\": [\"Option A\", \"Option B\", \"Option C\", \"Option D\"],\n");
        promptBuilder.append("      \"correctIndex\": 0,\n");
        promptBuilder.append("      \"explanation\": \"Why option A is correct...\"\n");
        promptBuilder.append("    },\n");
        promptBuilder.append("    {\n");
        promptBuilder.append("      \"type\": \"SHORT_ANSWER\",\n");
        promptBuilder.append("      \"question\": \"Question text?\",\n");
        promptBuilder.append("      \"rubric\": \"Key concepts and points expected in the answer...\",\n");
        promptBuilder.append("      \"maxScore\": 5\n");
        promptBuilder.append("    }\n");
        promptBuilder.append("  ]\n");
        promptBuilder.append("}\n");

        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        String mimeType = QuizSourceHelper.getImageMimeType(imageFile);

        String requestJson = buildGeminiImageRequestBody(promptBuilder.toString(), imageBytes, mimeType);
        String responseBody = sendGeminiRequest(apiKey, requestJson);

        String jsonText = extractContentText(responseBody);
        return parseQuizSession(jsonText, topicPrompt != null && !topicPrompt.isEmpty() ? topicPrompt : imageFile.getName(), difficulty);
    }

    /**
     * Call #2: Grades short answer responses using Gemini AI.
     * Evaluates conceptual correctness, accuracy, and completeness.
     */
    public CompletableFuture<Void> gradeShortAnswersAsync(QuizSession session) {
        return CompletableFuture.runAsync(() -> {
            try {
                gradeShortAnswers(session);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }

    /**
     * Synchronous implementation of Call #2 (Short Answer Grading).
     */
    public void gradeShortAnswers(QuizSession session) throws IOException, InterruptedException {
        List<QuizQuestion> shortAnswerQuestions = new ArrayList<>();
        for (int i = 0; i < session.getQuestions().size(); i++) {
            QuizQuestion q = session.getQuestions().get(i);
            if (q.getType() == QuizQuestion.QuestionType.SHORT_ANSWER) {
                shortAnswerQuestions.add(q);
            }
        }

        if (shortAnswerQuestions.isEmpty()) {
            return; // No short answer questions to grade
        }

        String apiKey = ApiKeyManager.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("Google Gemini API Key is missing. Please configure it in API Settings.");
        }

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("You are an encouraging and fair academic evaluator.\n");
        promptBuilder.append("Grade each of the following student responses against the question and the expected rubric.\n\n");

        ArrayNode itemsArray = objectMapper.createArrayNode();
        for (int i = 0; i < shortAnswerQuestions.size(); i++) {
            QuizQuestion q = shortAnswerQuestions.get(i);
            ObjectNode item = objectMapper.createObjectNode();
            item.put("id", i);
            item.put("question", q.getQuestionText());
            item.put("expectedRubric", q.getRubric());
            item.put("maxScore", q.getMaxScore());
            item.put("studentAnswer", q.getStudentAnswer().trim().isEmpty() ? "(No answer submitted)" : q.getStudentAnswer().trim());
            itemsArray.add(item);
        }

        promptBuilder.append("Student Submissions JSON:\n");
        promptBuilder.append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(itemsArray)).append("\n\n");
        promptBuilder.append("EVALUATION RULES:\n");
        promptBuilder.append("1. Score each response from 0 up to maxScore.\n");
        promptBuilder.append("2. If the answer is blank or irrelevant, award 0 points.\n");
        promptBuilder.append("3. Provide concise, constructive feedback explaining why points were awarded or deducted.\n");
        promptBuilder.append("4. Return strictly valid JSON with this structure:\n");
        promptBuilder.append("{\n");
        promptBuilder.append("  \"evaluations\": [\n");
        promptBuilder.append("    {\n");
        promptBuilder.append("      \"id\": 0,\n");
        promptBuilder.append("      \"awardedScore\": 4,\n");
        promptBuilder.append("      \"feedback\": \"Great explanation of key concepts. To achieve full marks, include...\"\n");
        promptBuilder.append("    }\n");
        promptBuilder.append("  ]\n");
        promptBuilder.append("}\n");

        String requestJson = buildGeminiRequestBody(promptBuilder.toString());
        String responseBody = sendGeminiRequest(apiKey, requestJson);

        String jsonText = extractContentText(responseBody);
        applyGradingResults(jsonText, shortAnswerQuestions);
    }

    /**
     * Builds standard Gemini REST payload requesting application/json output.
     */
    private String buildGeminiRequestBody(String promptText) throws IOException {
        ObjectNode root = objectMapper.createObjectNode();

        ArrayNode contents = root.putArray("contents");
        ObjectNode content = contents.addObject();
        ArrayNode parts = content.putArray("parts");
        ObjectNode part = parts.addObject();
        part.put("text", promptText);

        ObjectNode genConfig = root.putObject("generationConfig");
        genConfig.put("responseMimeType", "application/json");
        genConfig.put("temperature", 0.3);

        return objectMapper.writeValueAsString(root);
    }

    /**
     * Executes the HTTP POST request to the Google Gemini API.
     * Iterates through candidate models in order (e.g. gemini-3.8-flash, gemini-flash-latest),
     * automatically falling back if a model version is deprecated or not found (404).
     */
    private String sendGeminiRequest(String apiKey, String jsonBody) throws IOException, InterruptedException {
        String lastErrorMsg = "";

        for (String model : CANDIDATE_MODELS) {
            String fullUrl = BASE_URL + model + ":generateContent?key=" + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(45))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            }

            lastErrorMsg = "Gemini API error (HTTP " + response.statusCode() + "): ";
            try {
                JsonNode errNode = objectMapper.readTree(response.body());
                if (errNode.has("error") && errNode.get("error").has("message")) {
                    lastErrorMsg += errNode.get("error").get("message").asText();
                } else {
                    lastErrorMsg += response.body();
                }
            } catch (Exception ignored) {
                lastErrorMsg += response.body();
            }

            // If error is a transient rate limit (429), high demand spike (503), or deprecated model (404),
            // automatically fall back to the next candidate model in the pool
            if (response.statusCode() != 404 && response.statusCode() != 429 && response.statusCode() != 503) {
                throw new IOException(lastErrorMsg);
            }
        }

        throw new IOException(lastErrorMsg);
    }

    /**
     * Extracts text from Gemini's candidate parts response, stripping any markdown wrappers.
     */
    private String extractContentText(String rawApiResponse) throws IOException {
        JsonNode root = objectMapper.readTree(rawApiResponse);
        JsonNode candidates = root.get("candidates");
        if (candidates == null || !candidates.isArray() || candidates.isEmpty()) {
            throw new IOException("Gemini API returned no candidates. Possible content filter trigger.");
        }

        JsonNode content = candidates.get(0).get("content");
        if (content == null || !content.has("parts")) {
            throw new IOException("Gemini API response missing content parts.");
        }

        JsonNode parts = content.get("parts");
        if (!parts.isArray() || parts.isEmpty()) {
            throw new IOException("Gemini API response has empty parts.");
        }

        String text = parts.get(0).path("text").asText("");
        return sanitizeJsonText(text);
    }

    /**
     * Removes ```json ... ``` code fence markers if present in the model's text.
     */
    public static String sanitizeJsonText(String text) {
        if (text == null) return "{}";
        String trimmed = text.trim();
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substring(7);
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3);
        }

        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        return trimmed.trim();
    }

    /**
     * Parses the sanitized JSON text into a QuizSession instance.
     */
    public QuizSession parseQuizSession(String jsonText, String fallbackTopic, String fallbackDifficulty) throws IOException {
        JsonNode root = objectMapper.readTree(jsonText);

        String title = root.path("title").asText("AI Study Quiz");
        String topic = root.path("topic").asText(fallbackTopic != null && !fallbackTopic.isEmpty() ? fallbackTopic : "General");
        String difficulty = root.path("difficulty").asText(fallbackDifficulty != null ? fallbackDifficulty : "Medium");

        List<QuizQuestion> questions = new ArrayList<>();
        JsonNode qArray = root.path("questions");
        if (qArray.isArray()) {
            for (JsonNode qNode : qArray) {
                String typeStr = qNode.path("type").asText("MCQ").toUpperCase();
                String questionText = qNode.path("question").asText("");

                if ("MCQ".equals(typeStr)) {
                    List<String> options = new ArrayList<>();
                    JsonNode optArray = qNode.path("options");
                    if (optArray.isArray()) {
                        for (JsonNode opt : optArray) {
                            options.add(opt.asText());
                        }
                    }
                    int correctIndex = qNode.path("correctIndex").asInt(0);
                    String explanation = qNode.path("explanation").asText("");
                    questions.add(QuizQuestion.createMcq(questionText, options, correctIndex, explanation));
                } else {
                    String rubric = qNode.path("rubric").asText("");
                    int maxScore = qNode.path("maxScore").asInt(5);
                    questions.add(QuizQuestion.createShortAnswer(questionText, rubric, maxScore));
                }
            }
        }

        return new QuizSession(title, topic, difficulty, questions);
    }

    /**
     * Applies grading evaluations back onto the short answer questions.
     */
    public void applyGradingResults(String jsonText, List<QuizQuestion> shortAnswerQuestions) throws IOException {
        JsonNode root = objectMapper.readTree(jsonText);
        JsonNode evals = root.path("evaluations");
        if (evals.isArray()) {
            for (JsonNode ev : evals) {
                int id = ev.path("id").asInt(-1);
                int score = ev.path("awardedScore").asInt(0);
                String feedback = ev.path("feedback").asText("");

                if (id >= 0 && id < shortAnswerQuestions.size()) {
                    QuizQuestion q = shortAnswerQuestions.get(id);
                    q.setAwardedScore(Math.min(score, q.getMaxScore()));
                    q.setAiFeedback(feedback);
                }
            }
        }
    }

    /**
     * Parses an uploaded syllabus (PDF text or raw outline) into structured chapters and topics.
     */
    public CompletableFuture<List<SyllabusChapter>> parseSyllabusHierarchyAsync(String syllabusText) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return parseSyllabusHierarchy(syllabusText);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }

    public List<SyllabusChapter> parseSyllabusHierarchy(String syllabusText) throws IOException, InterruptedException {
        String apiKey = ApiKeyManager.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IOException("Gemini API key is not configured. Please set your key in Settings.");
        }

        String prompt = "You are an expert academic curriculum parser. Given the following syllabus content, extract the hierarchical chapters and their corresponding study topics.\n"
                + "Return ONLY a valid JSON object matching this exact schema:\n"
                + "{\n"
                + "  \"chapters\": [\n"
                + "    {\n"
                + "      \"chapterNumber\": 1,\n"
                + "      \"title\": \"Chapter Title\",\n"
                + "      \"topics\": [\"Topic 1\", \"Topic 2\", \"Topic 3\"]\n"
                + "    }\n"
                + "  ]\n"
                + "}\n\n"
                + "Guidelines:\n"
                + "1. Number chapters sequentially starting from 1.\n"
                + "2. Keep chapter titles and topic names concise, informative, and clean.\n"
                + "3. Extract all distinct core concepts, sections, or lectures into individual topics. Every chapter MUST have multiple topics.\n\n"
                + "Syllabus Content:\n"
                + (syllabusText.length() > 25000 ? syllabusText.substring(0, 25000) : syllabusText);

        String jsonBody = buildGeminiRequestBody(prompt);
        String rawResponse = sendGeminiRequest(apiKey, jsonBody);
        String responseContent = extractContentText(rawResponse);

        return parseSyllabusChaptersFromJson(responseContent);
    }

    /**
     * Parses an uploaded syllabus image (document photo, whiteboard screenshot, or slide)
     * using Gemini's multimodal vision API to extract structured chapters and topics.
     */
    public CompletableFuture<List<SyllabusChapter>> parseSyllabusHierarchyFromImageAsync(File imageFile) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return parseSyllabusHierarchyFromImage(imageFile);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }

    public List<SyllabusChapter> parseSyllabusHierarchyFromImage(File imageFile) throws IOException, InterruptedException {
        String apiKey = ApiKeyManager.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IOException("Gemini API key is not configured. Please set your key in Settings.");
        }

        String prompt = "You are an expert academic curriculum parser. Look at this syllabus image (which may be a document photo, slide, textbook table of contents, or course outline) and extract the hierarchical chapters and their study topics.\n"
                + "Return ONLY a valid JSON object matching this exact schema:\n"
                + "{\n"
                + "  \"chapters\": [\n"
                + "    {\n"
                + "      \"chapterNumber\": 1,\n"
                + "      \"title\": \"Chapter Title\",\n"
                + "      \"topics\": [\"Topic 1\", \"Topic 2\", \"Topic 3\"]\n"
                + "    }\n"
                + "  ]\n"
                + "}\n\n"
                + "Guidelines:\n"
                + "1. Number chapters sequentially starting from 1.\n"
                + "2. Keep chapter titles and topic names concise, informative, and clean.\n"
                + "3. Extract all core concepts, sections, or topics found in the image. Every chapter MUST have at least 2 to 5 topics.";

        byte[] rawBytes = Files.readAllBytes(imageFile.toPath());
        String mimeType = QuizSourceHelper.getImageMimeType(imageFile);
        byte[] imageBytes = scaleImageBytesIfNeeded(rawBytes, mimeType);

        String jsonBody = buildGeminiImageRequestBody(prompt, imageBytes, mimeType);
        String rawResponse = sendGeminiRequest(apiKey, jsonBody);
        String responseContent = extractContentText(rawResponse);

        return parseSyllabusChaptersFromJson(responseContent);
    }

    private byte[] scaleImageBytesIfNeeded(byte[] original, String mimeType) {
        if (original.length <= 2 * 1024 * 1024) {
            return original;
        }
        try {
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(original);
            java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(bais);
            if (img == null) return original;

            int maxDim = 1600;
            int width = img.getWidth();
            int height = img.getHeight();
            if (width <= maxDim && height <= maxDim) {
                return original;
            }

            double scale = Math.min((double) maxDim / width, (double) maxDim / height);
            int newW = Math.max(1, (int) (width * scale));
            int newH = Math.max(1, (int) (height * scale));

            java.awt.image.BufferedImage resized = new java.awt.image.BufferedImage(newW, newH, java.awt.image.BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g2d = resized.createGraphics();
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(img, 0, 0, newW, newH, null);
            g2d.dispose();

            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            String fmt = mimeType.contains("png") ? "png" : "jpeg";
            javax.imageio.ImageIO.write(resized, fmt, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            return original;
        }
    }

    private String buildGeminiImageRequestBody(String promptText, byte[] imageBytes, String mimeType) throws IOException {
        ObjectNode root = objectMapper.createObjectNode();

        ArrayNode contents = root.putArray("contents");
        ObjectNode content = contents.addObject();
        ArrayNode parts = content.putArray("parts");

        ObjectNode textPart = parts.addObject();
        textPart.put("text", promptText);

        ObjectNode inlineDataPart = parts.addObject();
        ObjectNode inlineData = inlineDataPart.putObject("inlineData");
        inlineData.put("mimeType", mimeType);
        inlineData.put("data", Base64.getEncoder().encodeToString(imageBytes));

        ObjectNode genConfig = root.putObject("generationConfig");
        genConfig.put("responseMimeType", "application/json");
        genConfig.put("temperature", 0.2);

        return objectMapper.writeValueAsString(root);
    }

    public List<SyllabusChapter> parseSyllabusChaptersFromJson(String responseContent) throws IOException {
        List<SyllabusChapter> chapters = new ArrayList<>();
        if (responseContent == null || responseContent.trim().isEmpty()) {
            return chapters;
        }

        JsonNode root = objectMapper.readTree(responseContent);

        JsonNode chapArray = null;
        if (root.isArray()) {
            chapArray = root;
        } else if (root.has("chapters") && root.get("chapters").isArray()) {
            chapArray = root.get("chapters");
        } else if (root.has("modules") && root.get("modules").isArray()) {
            chapArray = root.get("modules");
        } else if (root.has("units") && root.get("units").isArray()) {
            chapArray = root.get("units");
        } else if (root.has("syllabus") && root.get("syllabus").isArray()) {
            chapArray = root.get("syllabus");
        }

        if (chapArray != null && chapArray.isArray()) {
            for (JsonNode chNode : chapArray) {
                int num = chapters.size() + 1;
                if (chNode.has("chapterNumber")) num = chNode.get("chapterNumber").asInt(num);
                else if (chNode.has("chapter_number")) num = chNode.get("chapter_number").asInt(num);
                else if (chNode.has("number")) num = chNode.get("number").asInt(num);
                else if (chNode.has("chapter")) num = chNode.get("chapter").asInt(num);

                String title = "";
                if (chNode.has("title")) title = chNode.get("title").asText("");
                else if (chNode.has("name")) title = chNode.get("name").asText("");
                else if (chNode.has("chapterTitle")) title = chNode.get("chapterTitle").asText("");
                else if (chNode.has("chapter_name")) title = chNode.get("chapter_name").asText("");

                if (title.trim().isEmpty()) {
                    title = "Chapter " + num;
                }
                SyllabusChapter chapter = new SyllabusChapter(num, title.trim());

                List<SyllabusTopic> topics = new ArrayList<>();
                JsonNode topArray = null;
                if (chNode.has("topics") && chNode.get("topics").isArray()) topArray = chNode.get("topics");
                else if (chNode.has("subtopics") && chNode.get("subtopics").isArray()) topArray = chNode.get("subtopics");
                else if (chNode.has("sections") && chNode.get("sections").isArray()) topArray = chNode.get("sections");
                else if (chNode.has("lessons") && chNode.get("lessons").isArray()) topArray = chNode.get("lessons");
                else if (chNode.has("items") && chNode.get("items").isArray()) topArray = chNode.get("items");

                if (topArray != null && topArray.isArray()) {
                    for (JsonNode tNode : topArray) {
                        String topTitle = "";
                        if (tNode.isTextual()) {
                            topTitle = tNode.asText().trim();
                        } else if (tNode.isObject()) {
                            if (tNode.has("title")) topTitle = tNode.get("title").asText("");
                            else if (tNode.has("name")) topTitle = tNode.get("name").asText("");
                            else if (tNode.has("topic")) topTitle = tNode.get("topic").asText("");
                            else if (tNode.has("text")) topTitle = tNode.get("text").asText("");
                        }
                        topTitle = topTitle.replaceAll("^[\\-*•–—+\\d.:\\s]+", "").trim();
                        if (!topTitle.isEmpty() && topTitle.length() >= 2) {
                            topics.add(new SyllabusTopic(topTitle));
                        }
                    }
                }

                if (topics.isEmpty()) {
                    topics.add(new SyllabusTopic("Overview & Core Concepts of " + chapter.getTitle()));
                }

                chapter.setTopics(topics);
                chapters.add(chapter);
            }
        }

        return chapters;
    }

    /**
     * Generates a personalized strategic forecast and study recommendation for the upcoming Term Final Exam.
     */
    public CompletableFuture<String> generateExamForecastAsync(
            String courseName,
            int totalTopics,
            int completedTopics,
            List<String> remainingTopics,
            long daysRemaining) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return generateExamForecast(courseName, totalTopics, completedTopics, remainingTopics, daysRemaining);
            } catch (Exception e) {
                return "Keep up consistent daily study to cover the remaining topics before your Term Exam.";
            }
        });
    }

    public String generateExamForecast(
            String courseName,
            int totalTopics,
            int completedTopics,
            List<String> remainingTopics,
            long daysRemaining) throws IOException, InterruptedException {

        String apiKey = ApiKeyManager.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return "Configure your Gemini API key in Settings to receive personalized AI exam forecasts.";
        }

        String remainingStr = (remainingTopics != null && !remainingTopics.isEmpty())
                ? String.join(", ", remainingTopics.subList(0, Math.min(10, remainingTopics.size())))
                : "None";

        String prompt = "You are an encouraging academic study coach. Analyze the student's progress and provide a 2-3 sentence personalized strategic plan for their upcoming Term Final Exam.\n"
                + "Course: " + courseName + "\n"
                + "Days Remaining until Exam: " + daysRemaining + " days\n"
                + "Total Topics: " + totalTopics + "\n"
                + "Completed Topics: " + completedTopics + " (" + (totalTopics > 0 ? (completedTopics * 100 / totalTopics) : 0) + "%)\n"
                + "Unstudied Remaining Topics: " + remainingStr + "\n\n"
                + "Provide realistic topic completion pace (e.g. 1 topic every X days) and recommend high-priority areas to focus on. Keep response under 50 words. Do not use markdown wrappers.";

        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode contents = root.putArray("contents");
        ObjectNode content = contents.addObject();
        ArrayNode parts = content.putArray("parts");
        ObjectNode part = parts.addObject();
        part.put("text", prompt);

        ObjectNode genConfig = root.putObject("generationConfig");
        genConfig.put("temperature", 0.4);

        String jsonBody = objectMapper.writeValueAsString(root);
        String rawResponse = sendGeminiRequest(apiKey, jsonBody);
        return extractContentText(rawResponse).trim();
    }
}

package com.example.revision_assistant;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Intelligent offline rule-based parser that extracts structured syllabus chapters
 * and topics from document text without requiring an external AI API call.
 * Acts as a rock-solid fallback when Gemini API is unavailable or offline.
 */
public class SyllabusLocalParser {

    private static final Pattern CHAPTER_PATTERN = Pattern.compile(
            "^(?:chapter|module|unit|week|part|section)\\s*(\\d+|[ivxlcdm]+)[:.\\-\\s]*(.*)$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern ROMAN_NUMERAL_PATTERN = Pattern.compile(
            "^(?:[ivx]+)[.:\\-\\s]+([A-Z].*)$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern NUMBERED_HEADING_PATTERN = Pattern.compile(
            "^(\\d+)[.:\\-\\)]\\s+([A-Z].*)$"
    );

    private static final Pattern BULLET_TOPIC_PATTERN = Pattern.compile(
            "^(?:[\\-*•–—+]|\\d+\\.\\d+|[a-zA-Z][.)])\\s*(.*)$"
    );

    /**
     * Parses raw syllabus text into structured chapters with topics.
     */
    public static List<SyllabusChapter> parseText(String rawText) {
        List<SyllabusChapter> chapters = new ArrayList<>();
        if (rawText == null || rawText.trim().isEmpty()) {
            return fallbackDefaultChapters();
        }

        String[] lines = rawText.split("\\r?\\n");
        SyllabusChapter currentChapter = null;
        List<String> rawCandidateLines = new ArrayList<>();

        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty() || isHeaderNoise(line)) {
                continue;
            }

            // Check if this line is a Chapter header
            Matcher chapMatcher = CHAPTER_PATTERN.matcher(line);
            Matcher romanMatcher = ROMAN_NUMERAL_PATTERN.matcher(line);
            Matcher numHeadingMatcher = NUMBERED_HEADING_PATTERN.matcher(line);

            boolean isChapterHeader = false;
            String chapterTitle = "";
            int chapterNum = chapters.size() + 1;

            if (chapMatcher.matches()) {
                isChapterHeader = true;
                String numStr = chapMatcher.group(1);
                String rest = chapMatcher.group(2).trim();
                try {
                    chapterNum = Integer.parseInt(numStr);
                } catch (NumberFormatException ignored) {}
                chapterTitle = rest.isEmpty() ? ("Chapter " + numStr) : cleanTitle(rest);
            } else if (romanMatcher.matches()) {
                isChapterHeader = true;
                chapterTitle = cleanTitle(romanMatcher.group(1));
            } else if (numHeadingMatcher.matches() && isSignificantHeading(line)) {
                isChapterHeader = true;
                try {
                    chapterNum = Integer.parseInt(numHeadingMatcher.group(1));
                } catch (NumberFormatException ignored) {}
                chapterTitle = cleanTitle(numHeadingMatcher.group(2));
            }

            if (isChapterHeader) {
                if (currentChapter != null && !currentChapter.getTopics().isEmpty()) {
                    chapters.add(currentChapter);
                }
                currentChapter = new SyllabusChapter(chapterNum, chapterTitle);
            } else {
                // Topic under current chapter or candidate topic
                String topicTitle = extractTopicText(line);
                if (!topicTitle.isEmpty() && topicTitle.length() >= 3 && topicTitle.length() <= 140) {
                    if (currentChapter != null) {
                        currentChapter.getTopics().add(new SyllabusTopic(topicTitle));
                    } else {
                        rawCandidateLines.add(topicTitle);
                    }
                }
            }
        }

        if (currentChapter != null && !currentChapter.getTopics().isEmpty()) {
            chapters.add(currentChapter);
        }

        // If no chapters were recognized by headings, organize candidate lines into logical chapters
        if (chapters.isEmpty()) {
            if (rawCandidateLines.isEmpty()) {
                // Extract any meaningful sentences/lines
                for (String l : lines) {
                    String clean = l.replaceAll("^[\\-*•–—+\\d.\\s]+", "").trim();
                    if (clean.length() >= 5 && clean.length() <= 120 && !isHeaderNoise(clean)) {
                        rawCandidateLines.add(clean);
                    }
                }
            }

            if (!rawCandidateLines.isEmpty()) {
                int chunkSize = Math.max(3, (int) Math.ceil((double) rawCandidateLines.size() / 4.0));
                chunkSize = Math.min(chunkSize, 6);
                int chapIndex = 1;
                SyllabusChapter ch = new SyllabusChapter(chapIndex, "Core Syllabus - Part " + chapIndex);

                for (int i = 0; i < rawCandidateLines.size(); i++) {
                    ch.getTopics().add(new SyllabusTopic(rawCandidateLines.get(i)));
                    if (ch.getTopics().size() >= chunkSize && (i + 1) < rawCandidateLines.size()) {
                        chapters.add(ch);
                        chapIndex++;
                        ch = new SyllabusChapter(chapIndex, "Core Syllabus - Part " + chapIndex);
                    }
                }
                if (!ch.getTopics().isEmpty()) {
                    chapters.add(ch);
                }
            }
        }

        // Final safety guarantee
        if (chapters.isEmpty()) {
            return fallbackDefaultChapters();
        }

        // Ensure every chapter has at least 1 topic
        for (int i = 0; i < chapters.size(); i++) {
            SyllabusChapter ch = chapters.get(i);
            if (ch.getTopics() == null || ch.getTopics().isEmpty()) {
                ch.getTopics().add(new SyllabusTopic("Overview & Fundamentals of " + ch.getTitle()));
            }
        }

        return chapters;
    }

    private static String extractTopicText(String line) {
        Matcher m = BULLET_TOPIC_PATTERN.matcher(line);
        if (m.matches()) {
            return cleanTitle(m.group(1));
        }
        return cleanTitle(line);
    }

    private static String cleanTitle(String text) {
        if (text == null) return "";
        return text.replaceAll("^[\\-*•–—+\\d.:\\s]+", "")
                   .replaceAll("[;:,]+$", "")
                   .trim();
    }

    private static boolean isSignificantHeading(String line) {
        String lower = line.toLowerCase();
        return !lower.contains("page ") && !lower.contains("grade") && !lower.contains("percent")
                && !lower.contains("attendance") && !lower.contains("office") && !lower.contains("instructor");
    }

    private static boolean isHeaderNoise(String line) {
        String lower = line.toLowerCase();
        return lower.startsWith("instructor:")
                || lower.startsWith("email:")
                || lower.startsWith("office:")
                || lower.startsWith("phone:")
                || lower.startsWith("prerequisites:")
                || lower.startsWith("grading policy")
                || lower.startsWith("grading criteria")
                || lower.startsWith("course code:")
                || lower.startsWith("credits:")
                || lower.contains("academic dishonesty")
                || lower.contains("plagiarism policy")
                || lower.startsWith("http://")
                || lower.startsWith("https://");
    }

    private static List<SyllabusChapter> fallbackDefaultChapters() {
        List<SyllabusChapter> list = new ArrayList<>();
        SyllabusChapter ch1 = new SyllabusChapter(1, "Fundamentals & Core Concepts");
        ch1.getTopics().add(new SyllabusTopic("Course Introduction & Foundations"));
        ch1.getTopics().add(new SyllabusTopic("Key Principles & Frameworks"));
        ch1.getTopics().add(new SyllabusTopic("Theoretical Overview"));
        list.add(ch1);

        SyllabusChapter ch2 = new SyllabusChapter(2, "Advanced Topics & Applications");
        ch2.getTopics().add(new SyllabusTopic("Methodologies & Implementation"));
        ch2.getTopics().add(new SyllabusTopic("Problem Solving & Case Studies"));
        ch2.getTopics().add(new SyllabusTopic("Exam Review & Practice"));
        list.add(ch2);
        return list;
    }
}

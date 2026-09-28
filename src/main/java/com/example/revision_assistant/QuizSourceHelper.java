package com.example.revision_assistant;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * Utility helper to extract text content from files or notebook pages
 * to serve as source material for AI quiz generation.
 */
public class QuizSourceHelper {

    /**
     * Reads text content from a chosen file (supports .pdf, .txt, .md, .json, etc.).
     */
    public static String readFileContent(File file) throws IOException {
        if (file == null || !file.exists()) {
            return "";
        }
        String name = file.getName().toLowerCase();
        if (name.endsWith(".pdf")) {
            try (org.apache.pdfbox.pdmodel.PDDocument document = org.apache.pdfbox.Loader.loadPDF(file)) {
                org.apache.pdfbox.text.PDFTextStripper stripper = new org.apache.pdfbox.text.PDFTextStripper();
                String text = stripper.getText(document);
                if (text != null && !text.trim().isEmpty()) {
                    return text;
                }
            } catch (Exception ignored) {}
        }
        if (name.endsWith(".docx")) {
            try {
                String docxText = extractZipXmlText(file, n -> n.equalsIgnoreCase("word/document.xml"));
                if (!docxText.trim().isEmpty()) return docxText;
            } catch (Exception ignored) {}
        }
        if (name.endsWith(".doc")) {
            try {
                String docxText = extractZipXmlText(file, n -> n.equalsIgnoreCase("word/document.xml"));
                if (!docxText.trim().isEmpty()) return docxText;
            } catch (Exception ignored) {}
            try {
                byte[] raw = Files.readAllBytes(file.toPath());
                String extracted = extractPrintableTextRuns(raw);
                if (!extracted.trim().isEmpty()) return extracted;
            } catch (Exception ignored) {}
        }
        if (name.endsWith(".pptx") || name.endsWith(".ppt")) {
            try {
                String pptxText = extractZipXmlText(file, n -> n.toLowerCase().startsWith("ppt/slides/slide") && n.toLowerCase().endsWith(".xml"));
                if (!pptxText.trim().isEmpty()) return pptxText;
            } catch (Exception ignored) {}
        }
        byte[] bytes = Files.readAllBytes(file.toPath());
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Checks if the given file is an image (.png, .jpg, .jpeg, .webp, .bmp, .gif).
     */
    public static boolean isImageFile(File file) {
        if (file == null) return false;
        String name = file.getName().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")
                || name.endsWith(".webp") || name.endsWith(".bmp") || name.endsWith(".gif");
    }

    /**
     * Determines mime type for an image file.
     */
    public static String getImageMimeType(File file) {
        if (file == null) return "image/jpeg";
        String name = file.getName().toLowerCase();
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".webp")) return "image/webp";
        if (name.endsWith(".gif")) return "image/gif";
        if (name.endsWith(".bmp")) return "image/bmp";
        return "image/jpeg";
    }

    /**
     * Renders the first page of a PDF document to a temporary PNG image file.
     * Useful for scanned PDFs where text extraction yields 0 characters.
     */
    public static File renderFirstPageOfPdfToImage(File pdfFile) {
        try (org.apache.pdfbox.pdmodel.PDDocument document = org.apache.pdfbox.Loader.loadPDF(pdfFile)) {
            if (document.getNumberOfPages() > 0) {
                org.apache.pdfbox.rendering.PDFRenderer renderer = new org.apache.pdfbox.rendering.PDFRenderer(document);
                java.awt.image.BufferedImage img = renderer.renderImageWithDPI(0, 150);
                File tempImg = File.createTempFile("pdf_page_", ".png");
                tempImg.deleteOnExit();
                javax.imageio.ImageIO.write(img, "png", tempImg);
                return tempImg;
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static String extractPrintableTextRuns(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        StringBuilder run = new StringBuilder();
        for (byte b : bytes) {
            char c = (char) (b & 0xFF);
            if ((c >= 32 && c <= 126) || c == '\n' || c == '\t' || c == '\r') {
                run.append(c);
            } else {
                if (run.length() >= 4) {
                    sb.append(run).append(" ");
                }
                run.setLength(0);
            }
        }
        if (run.length() >= 4) {
            sb.append(run);
        }
        return sb.toString().replaceAll("\\s+", " ").trim();
    }

    /**
     * Helper to extract and strip XML content from zipped OpenXML files (.docx, .pptx).
     */
    private static String extractZipXmlText(File file, java.util.function.Predicate<String> entryFilter) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(new java.io.FileInputStream(file))) {
            java.util.zip.ZipEntry entry;
            byte[] buffer = new byte[8192];
            while ((entry = zis.getNextEntry()) != null) {
                if (entryFilter.test(entry.getName())) {
                    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }
                    String xml = baos.toString(StandardCharsets.UTF_8);
                    String text = xml.replaceAll("<[^>]+>", " ")
                                     .replace("&amp;", "&")
                                     .replace("&lt;", "<")
                                     .replace("&gt;", ">")
                                     .replace("&quot;", "\"")
                                     .replace("&apos;", "'")
                                     .replaceAll("\\s+", " ")
                                     .trim();
                    if (!text.isEmpty()) {
                        sb.append(text).append("\n\n");
                    }
                }
                zis.closeEntry();
            }
        }
        return sb.toString().trim();
    }

    /**
     * Extracts readable text from a Study Buddy Page's blocks.
     */
    public static String extractPageContent(Page page) {
        if (page == null) return "";

        StringBuilder sb = new StringBuilder();
        sb.append("Page Title: ").append(page.getTitle()).append("\n\n");

        List<PageBlock> blocks = PageBlock.deserializeList(page.getContentJson());
        for (PageBlock b : blocks) {
            if (PageBlock.TYPE_TEXT.equalsIgnoreCase(b.getType())) {
                if (b.getContent() != null && !b.getContent().trim().isEmpty()) {
                    sb.append(b.getContent().trim()).append("\n\n");
                }
            } else if (PageBlock.TYPE_CODE.equalsIgnoreCase(b.getType())) {
                if (b.getContent() != null && !b.getContent().trim().isEmpty()) {
                    sb.append("Code (").append(b.getExtra()).append("):\n")
                      .append(b.getContent().trim()).append("\n\n");
                }
            }
        }

        return sb.toString().trim();
    }

    /**
     * Generates a compact summary string of source metadata.
     */
    public static String formatSourceSummary(String sourceName, String text) {
        if (text == null || text.trim().isEmpty()) {
            return sourceName + " (empty)";
        }
        int charCount = text.length();
        int wordCount = text.trim().split("\\s+").length;
        return String.format("%s (%d chars, ~%d words)", sourceName, charCount, wordCount);
    }
}
